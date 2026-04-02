package com.charles.mianti.judge.codesandbox.impl;

import com.charles.mianti.judge.codesandbox.CodeSandbox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

/**
 * 简单代码沙箱实现（本地执行，仅用于演示）
 * 注意：生产环境应使用 Docker 容器隔离以确保安全
 *
 * @author Charles
 */
@Slf4j
@Service
public class SimpleCodeSandbox implements CodeSandbox {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    @Override
    public ExecuteResult execute(String languageCode, String code, String input, int timeLimit, int memoryLimit) {
        log.info("执行代码 - 语言：{}, 时间限制：{}ms, 内存限制：{}MB", languageCode, timeLimit, memoryLimit);

        try {
            // 根据语言类型执行不同的逻辑
            switch (languageCode.toLowerCase()) {
                case "java":
                    return executeJava(code, input, timeLimit, memoryLimit);
                case "python":
                case "python3":
                    return executePython(code, input, timeLimit, memoryLimit);
                case "javascript":
                case "nodejs":
                    return executeJavaScript(code, input, timeLimit, memoryLimit);
                case "cpp":
                    return executeCpp(code, input, timeLimit, memoryLimit);
                default:
                    ExecuteResult result = new ExecuteResult();
                    result.setVerdict(Verdict.RUNTIME_ERROR);
                    result.setErrorMessage("不支持的编程语言：" + languageCode);
                    return result;
            }
        } catch (Exception e) {
            log.error("代码执行失败", e);
            ExecuteResult result = new ExecuteResult();
            result.setVerdict(Verdict.RUNTIME_ERROR);
            result.setErrorMessage("执行异常：" + e.getMessage());
            return result;
        }
    }

    @Override
    public CompileResult compile(String languageCode, String code) {
        try {
            switch (languageCode.toLowerCase()) {
                case "java":
                    return compileJava(code);
                case "cpp":
                    return compileCpp(code);
                default:
                    // 解释型语言不需要编译
                    CompileResult result = new CompileResult();
                    result.setSuccess(true);
                    return result;
            }
        } catch (Exception e) {
            log.error("编译失败", e);
            CompileResult result = new CompileResult();
            result.setSuccess(false);
            result.setErrorMessage("编译异常：" + e.getMessage());
            return result;
        }
    }

    /**
     * 执行 Java 代码
     */
    private ExecuteResult executeJava(String code, String input, int timeLimit, int memoryLimit) throws Exception {
        // 检查是否包含危险代码
        if (!isJavaCodeSafe(code)) {
            ExecuteResult result = new ExecuteResult();
            result.setVerdict(Verdict.RUNTIME_ERROR);
            result.setErrorMessage("代码包含不安全操作");
            return result;
        }

        Path tempDir = Files.createTempDirectory("judge_java_");
        Path mainFile = tempDir.resolve("Main.java");
        Files.write(mainFile, code.getBytes());

        // 编译
        Process compileProcess = Runtime.getRuntime().exec(
            new String[]{"javac", "-encoding", "UTF-8", "Main.java"},
            null,
            tempDir.toFile()
        );

        if (!compileProcess.waitFor(10000, java.util.concurrent.TimeUnit.MILLISECONDS)) {
            compileProcess.destroy();
            ExecuteResult result = new ExecuteResult();
            result.setVerdict(Verdict.COMPILE_ERROR);
            result.setErrorMessage("编译超时");
            return result;
        }

        int compileExitCode = compileProcess.exitValue();
        if (compileExitCode != 0) {
            String errorOutput = readStream(compileProcess.getErrorStream());
            ExecuteResult result = new ExecuteResult();
            result.setVerdict(Verdict.COMPILE_ERROR);
            result.setErrorMessage(errorOutput);
            return result;
        }

        // 执行
        return runProcess(tempDir, new String[]{"java", "-Xmx" + memoryLimit + "m", "Main"}, input, timeLimit);
    }

    /**
     * 执行 Python 代码
     */
    private ExecuteResult executePython(String code, String input, int timeLimit, int memoryLimit) throws Exception {
        Path tempDir = Files.createTempDirectory("judge_python_");
        Path mainFile = tempDir.resolve("main.py");
        Files.write(mainFile, code.getBytes());

        return runProcess(tempDir, new String[]{"python3", "-B", "main.py"}, input, timeLimit);
    }

    /**
     * 执行 JavaScript 代码
     */
    private ExecuteResult executeJavaScript(String code, String input, int timeLimit, int memoryLimit) throws Exception {
        Path tempDir = Files.createTempDirectory("judge_js_");
        Path mainFile = tempDir.resolve("main.js");
        Files.write(mainFile, code.getBytes());

        return runProcess(tempDir, new String[]{"node", "main.js"}, input, timeLimit);
    }

    /**
     * 执行 C++ 代码
     */
    private ExecuteResult executeCpp(String code, String input, int timeLimit, int memoryLimit) throws Exception {
        Path tempDir = Files.createTempDirectory("judge_cpp_");
        Path mainFile = tempDir.resolve("main.cpp");
        Files.write(mainFile, code.getBytes());

        // 编译
        Process compileProcess = Runtime.getRuntime().exec(
            new String[]{"g++", "-std=c++17", "-O2", "-o", "main", "main.cpp"},
            null,
            tempDir.toFile()
        );

        if (!compileProcess.waitFor(10000, java.util.concurrent.TimeUnit.MILLISECONDS)) {
            compileProcess.destroy();
            ExecuteResult result = new ExecuteResult();
            result.setVerdict(Verdict.COMPILE_ERROR);
            result.setErrorMessage("编译超时");
            return result;
        }

        int compileExitCode = compileProcess.exitValue();
        if (compileExitCode != 0) {
            String errorOutput = readStream(compileProcess.getErrorStream());
            ExecuteResult result = new ExecuteResult();
            result.setVerdict(Verdict.COMPILE_ERROR);
            result.setErrorMessage(errorOutput);
            return result;
        }

        // 执行
        return runProcess(tempDir, new String[]{"./main"}, input, timeLimit);
    }

    /**
     * 运行进程
     */
    private ExecuteResult runProcess(Path workDir, String[] command, String input, int timeLimit) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workDir.toFile());
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // 写入输入
        if (input != null && !input.isEmpty()) {
            try (OutputStream os = process.getOutputStream()) {
                os.write(input.getBytes());
                os.flush();
            }
        }

        // 异步读取输出
        Future<String> outputFuture = EXECUTOR.submit(() -> readStream(process.getInputStream()));

        // 等待执行完成
        long startTime = System.currentTimeMillis();
        boolean finished = process.waitFor(timeLimit, java.util.concurrent.TimeUnit.MILLISECONDS);
        long endTime = System.currentTimeMillis();

        ExecuteResult result = new ExecuteResult();
        result.setExecutionTime((int) (endTime - startTime));
        result.setExecutionMemory(1024); // 简化处理，实际需要更精确的内存测量

        if (!finished) {
            process.destroyForcibly();
            result.setVerdict(Verdict.TIME_LIMIT_EXCEEDED);
            result.setErrorMessage("执行超时，限制时间：" + timeLimit + "ms");
        } else {
            String output = outputFuture.get();
            result.setOutput(output);
            result.setExitCode(process.exitValue());

            if (process.exitValue() != 0) {
                result.setVerdict(Verdict.RUNTIME_ERROR);
                result.setErrorMessage("运行时错误，退出码：" + process.exitValue());
            } else {
                result.setVerdict(Verdict.ACCEPTED);
            }
        }

        // 清理临时文件
        cleanupDirectory(workDir);

        return result;
    }

    /**
     * 编译 Java
     */
    private CompileResult compileJava(String code) throws Exception {
        Path tempDir = Files.createTempDirectory("compile_java_");
        Path mainFile = tempDir.resolve("Main.java");
        Files.write(mainFile, code.getBytes());

        Process compileProcess = Runtime.getRuntime().exec(
            new String[]{"javac", "-encoding", "UTF-8", "Main.java"},
            null,
            tempDir.toFile()
        );

        CompileResult result = new CompileResult();

        if (!compileProcess.waitFor(10000, java.util.concurrent.TimeUnit.MILLISECONDS)) {
            compileProcess.destroy();
            result.setSuccess(false);
            result.setErrorMessage("编译超时");
            return result;
        }

        if (compileProcess.exitValue() == 0) {
            result.setSuccess(true);
        } else {
            result.setSuccess(false);
            result.setErrorMessage(readStream(compileProcess.getErrorStream()));
        }

        cleanupDirectory(tempDir);
        return result;
    }

    /**
     * 编译 C++
     */
    private CompileResult compileCpp(String code) throws Exception {
        Path tempDir = Files.createTempDirectory("compile_cpp_");
        Path mainFile = tempDir.resolve("main.cpp");
        Files.write(mainFile, code.getBytes());

        Process compileProcess = Runtime.getRuntime().exec(
            new String[]{"g++", "-std=c++17", "-O2", "-o", "main", "main.cpp"},
            null,
            tempDir.toFile()
        );

        CompileResult result = new CompileResult();

        if (!compileProcess.waitFor(10000, java.util.concurrent.TimeUnit.MILLISECONDS)) {
            compileProcess.destroy();
            result.setSuccess(false);
            result.setErrorMessage("编译超时");
            return result;
        }

        if (compileProcess.exitValue() == 0) {
            result.setSuccess(true);
        } else {
            result.setSuccess(false);
            result.setErrorMessage(readStream(compileProcess.getErrorStream()));
        }

        cleanupDirectory(tempDir);
        return result;
    }

    /**
     * 检查 Java 代码安全性
     */
    private boolean isJavaCodeSafe(String code) {
        // 简单的安全检查，防止危险操作
        String[] dangerousPatterns = {
            "System\\.exit",
            "Runtime\\.getRuntime",
            "ProcessBuilder",
            "\\bexec\\b",
            "FileWriter.*(/etc/|/root/|C:/)"
        };

        for (String pattern : dangerousPatterns) {
            if (Pattern.compile(pattern).matcher(code).find()) {
                log.warn("检测到危险代码模式：{}", pattern);
                return false;
            }
        }

        return true;
    }

    /**
     * 读取流内容
     */
    private String readStream(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * 清理临时目录
     */
    private void cleanupDirectory(Path directory) {
        try {
            Files.walk(directory)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        log.error("删除文件失败：{}", path, e);
                    }
                });
        } catch (IOException e) {
            log.error("清理目录失败", e);
        }
    }
}
