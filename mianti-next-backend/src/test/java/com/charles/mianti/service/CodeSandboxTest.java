package com.charles.mianti.service;

import com.charles.mianti.judge.codesandbox.CodeSandbox;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 代码沙箱测试
 *
 * @author Charles
 */
@SpringBootTest
public class CodeSandboxTest {

    @Resource
    private CodeSandbox codeSandbox;

    @Test
    public void testExecuteJava() {
        String code = """
            import java.util.Scanner;
            
            public class Main {
                public static void main(String[] args) {
                    Scanner scanner = new Scanner(System.in);
                    int a = scanner.nextInt();
                    int b = scanner.nextInt();
                    System.out.println(a + b);
                }
            }
            """;
        
        String input = "1 2";
        
        CodeSandbox.ExecuteResult result = codeSandbox.execute("java", code, input, 1000, 256);
        
        Assertions.assertNotNull(result);
        System.out.println("Verdict: " + result.getVerdict());
        System.out.println("Output: " + result.getOutput());
        System.out.println("Execution Time: " + result.getExecutionTime() + "ms");
    }

    @Test
    public void testExecutePython() {
        String code = """
            a, b = map(int, input().split())
            print(a + b)
            """;
        
        String input = "3 4";
        
        CodeSandbox.ExecuteResult result = codeSandbox.execute("python", code, input, 1000, 256);
        
        Assertions.assertNotNull(result);
        System.out.println("Verdict: " + result.getVerdict());
        System.out.println("Output: " + result.getOutput());
    }

    @Test
    public void testExecuteJavaScript() {
        String code = """
            const readline = require('readline');
            const rl = readline.createInterface({
                input: process.stdin,
                output: process.stdout
            });
            
            rl.on('line', (line) => {
                const [a, b] = line.split(' ').map(Number);
                console.log(a + b);
                rl.close();
            });
            """;
        
        String input = "5 6";
        
        CodeSandbox.ExecuteResult result = codeSandbox.execute("javascript", code, input, 1000, 256);
        
        Assertions.assertNotNull(result);
        System.out.println("Verdict: " + result.getVerdict());
        System.out.println("Output: " + result.getOutput());
    }

    @Test
    public void testTLE() {
        String code = """
            public class Main {
                public static void main(String[] args) {
                    while (true) {
                        // 无限循环
                    }
                }
            }
            """;
        
        CodeSandbox.ExecuteResult result = codeSandbox.execute("java", code, "", 1000, 256);
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(CodeSandbox.Verdict.TIME_LIMIT_EXCEEDED, result.getVerdict());
        System.out.println("Verdict: " + result.getVerdict());
    }

    @Test
    public void testCompileError() {
        String code = """
            public class Main {
                public static void main(String[] args) {
                    int x = ; // 语法错误
                }
            }
            """;
        
        CodeSandbox.ExecuteResult result = codeSandbox.execute("java", code, "", 1000, 256);
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(CodeSandbox.Verdict.COMPILE_ERROR, result.getVerdict());
        System.out.println("Verdict: " + result.getVerdict());
        System.out.println("Error: " + result.getErrorMessage());
    }
}
