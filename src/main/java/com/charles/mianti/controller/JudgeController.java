package com.charles.mianti.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charles.mianti.common.BaseResponse;
import com.charles.mianti.common.ErrorCode;
import com.charles.mianti.common.ResultUtils;
import com.charles.mianti.exception.BusinessException;
import com.charles.mianti.exception.ThrowUtils;
import com.charles.mianti.model.dto.judge.SubmitCodeRequest;
import com.charles.mianti.model.entity.ProgrammingLanguage;
import com.charles.mianti.model.entity.Submission;
import com.charles.mianti.model.entity.User;
import com.charles.mianti.model.vo.JudgeResultVO;
import com.charles.mianti.model.vo.ProgrammingLanguageVO;
import com.charles.mianti.model.vo.SubmissionVO;
import com.charles.mianti.service.JudgeService;
import com.charles.mianti.service.ProgrammingLanguageService;
import com.charles.mianti.service.SubmissionService;
import com.charles.mianti.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 判题接口
 *
 * @author Charles
 */
@RestController
@RequestMapping("/judge")
@Slf4j
@Api(tags = "判题相关接口")
public class JudgeController {

    @Resource
    private JudgeService judgeService;

    @Resource
    private ProgrammingLanguageService programmingLanguageService;

    @Resource
    private SubmissionService submissionService;

    @Resource
    private UserService userService;

    /**
     * 提交代码
     *
     * @param submitCodeRequest 提交请求
     * @param request 请求
     * @return 提交记录 id
     */
    @PostMapping("/submit")
    @ApiOperation("提交代码")
    public BaseResponse<Long> submitCode(@RequestBody SubmitCodeRequest submitCodeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(submitCodeRequest == null, ErrorCode.PARAMS_ERROR);
        
        User loginUser = userService.getLoginUser(request);
        Long submissionId = judgeService.submitCode(submitCodeRequest, loginUser.getId());
        
        return ResultUtils.success(submissionId);
    }

    /**
     * 获取判题结果
     *
     * @param submissionId 提交 id
     * @return 判题结果
     */
    @GetMapping("/result/{submissionId}")
    @ApiOperation("获取判题结果")
    public BaseResponse<JudgeResultVO> getJudgeResult(@PathVariable Long submissionId) {
        ThrowUtils.throwIf(submissionId == null || submissionId <= 0, ErrorCode.PARAMS_ERROR);
        
        JudgeResultVO resultVO = judgeService.getJudgeResult(submissionId);
        return ResultUtils.success(resultVO);
    }

    /**
     * 获取所有支持的编程语言
     *
     * @return 编程语言列表
     */
    @GetMapping("/languages")
    @ApiOperation("获取编程语言列表")
    public BaseResponse<List<ProgrammingLanguageVO>> getProgrammingLanguages() {
        List<ProgrammingLanguage> languages = programmingLanguageService.listActiveLanguages();
        
        List<ProgrammingLanguageVO> voList = languages.stream()
            .map(language -> {
                ProgrammingLanguageVO vo = new ProgrammingLanguageVO();
                BeanUtils.copyProperties(language, vo);
                return vo;
            })
            .collect(Collectors.toList());
        
        return ResultUtils.success(voList);
    }

    /**
     * 获取我的提交记录
     *
     * @param questionId 题目 id（可选）
     * @param current 当前页码
     * @param size 页面大小
     * @param request 请求
     * @return 提交记录分页
     */
    @GetMapping("/my-submissions")
    @ApiOperation("获取我的提交记录")
    public BaseResponse<Page<SubmissionVO>> getMySubmissions(
        @RequestParam(required = false) Long questionId,
        @RequestParam(defaultValue = "1") long current,
        @RequestParam(defaultValue = "10") long size,
        HttpServletRequest request) {
        
        User loginUser = userService.getLoginUser(request);
        Page<SubmissionVO> submissionVOPage = submissionService.getMySubmissions(
            questionId, loginUser.getId(), current, size);
        
        return ResultUtils.success(submissionVOPage);
    }

    /**
     * 获取题目提交记录（管理员可见）
     *
     * @param questionId 题目 id
     * @param current 当前页码
     * @param size 页面大小
     * @param request 请求
     * @return 提交记录分页
     */
    @GetMapping("/submissions")
    @ApiOperation("获取题目提交记录")
    public BaseResponse<Page<SubmissionVO>> getSubmissions(
        @RequestParam Long questionId,
        @RequestParam(defaultValue = "1") long current,
        @RequestParam(defaultValue = "10") long size,
        HttpServletRequest request) {
        
        ThrowUtils.throwIf(questionId == null || questionId <= 0, ErrorCode.PARAMS_ERROR);
        
        User loginUser = userService.getLoginUser(request);
        // 仅管理员可以查看所有提交记录
        if (!StpUtil.hasRole("admin")) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        Page<SubmissionVO> submissionVOPage = submissionService.getSubmissionsByQuestion(
            questionId, current, size);
        
        return ResultUtils.success(submissionVOPage);
    }
}
