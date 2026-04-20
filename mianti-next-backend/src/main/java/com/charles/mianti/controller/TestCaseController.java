package com.charles.mianti.controller;

import com.charles.mianti.common.BaseResponse;
import com.charles.mianti.common.ResultUtils;
import com.charles.mianti.model.entity.TestCase;
import com.charles.mianti.service.TestCaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试用例接口
 *
 * @author Charles
 */
@RestController
@RequestMapping("/testcase")
@Slf4j
@Api(tags = "测试用例相关接口")
public class TestCaseController {

    @Resource
    private TestCaseService testCaseService;

    /**
     * 获取题目的示例测试用例
     *
     * @param questionId 题目 id
     * @return 示例测试用例列表
     */
    @GetMapping("/question/{questionId}/examples")
    @ApiOperation("获取题目的示例测试用例")
    public BaseResponse<List<TestCase>> getExampleTestCases(@PathVariable Long questionId) {
        List<TestCase> testCases = testCaseService.listByQuestionId(questionId);
        
        // 只返回示例测试用例（isExample=1）
        List<TestCase> exampleCases = testCases.stream()
            .filter(tc -> tc.getIsExample() != null && tc.getIsExample() == 1)
            .collect(Collectors.toList());
        
        return ResultUtils.success(exampleCases);
    }
}
