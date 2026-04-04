package com.charles.mianti.service;

import com.charles.mianti.model.dto.judge.SubmitCodeRequest;
import com.charles.mianti.model.vo.JudgeResultVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 判题服务测试
 *
 * @author Charles
 */
@SpringBootTest
public class JudgeServiceTest {

    @Resource
    private JudgeService judgeService;

    @Test
    public void testSubmitAndJudge() {
        // 准备提交请求
        SubmitCodeRequest request = new SubmitCodeRequest();
        request.setQuestionId(1L); // 假设有题目 id 为 1
        request.setLanguageCode("java");
        request.setCode("""
            import java.util.Scanner;
            
            public class Main {
                public static void main(String[] args) {
                    Scanner scanner = new Scanner(System.in);
                    int a = scanner.nextInt();
                    int b = scanner.nextInt();
                    System.out.println(a + b);
                }
            }
            """);

        // 提交代码
        Long submissionId = judgeService.submitCode(request, 1L);
        Assertions.assertNotNull(submissionId);
        System.out.println("提交成功，submissionId: " + submissionId);

        // 等待判题完成（实际应该轮询）
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 获取判题结果
        JudgeResultVO result = judgeService.getJudgeResult(submissionId);
        Assertions.assertNotNull(result);
        System.out.println("判题结果：" + result.getVerdict());
        System.out.println("执行时间：" + result.getExecutionTime() + "ms");
        System.out.println("使用内存：" + result.getExecutionMemory() + "KB");
    }

    @Test
    public void testGetJudgeResult() {
        JudgeResultVO result = judgeService.getJudgeResult(1L);
        if (result != null) {
            System.out.println("判题结果：" + result.getVerdict());
            System.out.println("题目：" + result.getQuestionTitle());
            System.out.println("语言：" + result.getLanguageName());
        }
    }
}
