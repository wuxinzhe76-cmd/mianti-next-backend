package com.charles.mianti.judge.codesandbox;

import java.util.List;

/**
 * 代码沙箱接口
 *
 * @author Charles
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param languageCode 编程语言代码
     * @param code 源代码
     * @param input 输入数据
     * @param timeLimit 时间限制（ms）
     * @param memoryLimit 内存限制（MB）
     * @return 执行结果
     */
    ExecuteResult execute(String languageCode, String code, String input, int timeLimit, int memoryLimit);

    /**
     * 编译代码（仅编译型语言需要）
     *
     * @param languageCode 编程语言代码
     * @param code 源代码
     * @return 编译结果
     */
    CompileResult compile(String languageCode, String code);

    /**
     * 判题结果枚举
     */
    enum Verdict {
        ACCEPTED("ACCEPTED", "答案正确"),
        WRONG_ANSWER("WA", "答案错误"),
        TIME_LIMIT_EXCEEDED("TLE", "超时"),
        MEMORY_LIMIT_EXCEEDED("MLE", "内存超限"),
        RUNTIME_ERROR("RE", "运行错误"),
        COMPILE_ERROR("CE", "编译错误");

        private final String value;
        private final String text;

        Verdict(String value, String text) {
            this.value = value;
            this.text = text;
        }

        public String getValue() {
            return value;
        }

        public String getText() {
            return text;
        }
    }

    /**
     * 执行结果
     */
    class ExecuteResult {
        private Verdict verdict;
        private String output;
        private String errorMessage;
        private Integer executionTime;
        private Integer executionMemory;
        private Integer exitCode;

        public Verdict getVerdict() {
            return verdict;
        }

        public void setVerdict(Verdict verdict) {
            this.verdict = verdict;
        }

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Integer getExecutionTime() {
            return executionTime;
        }

        public void setExecutionTime(Integer executionTime) {
            this.executionTime = executionTime;
        }

        public Integer getExecutionMemory() {
            return executionMemory;
        }

        public void setExecutionMemory(Integer executionMemory) {
            this.executionMemory = executionMemory;
        }

        public Integer getExitCode() {
            return exitCode;
        }

        public void setExitCode(Integer exitCode) {
            this.exitCode = exitCode;
        }
    }

    /**
     * 编译结果
     */
    class CompileResult {
        private boolean success;
        private String errorMessage;
        private String compileOutput;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getCompileOutput() {
            return compileOutput;
        }

        public void setCompileOutput(String compileOutput) {
            this.compileOutput = compileOutput;
        }
    }
}
