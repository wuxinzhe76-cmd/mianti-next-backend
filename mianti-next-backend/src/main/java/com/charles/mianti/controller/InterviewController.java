package com.charles.mianti.controller;

import com.charles.mianti.common.BaseResponse;
import com.charles.mianti.common.ErrorCode;
import com.charles.mianti.common.ResultUtils;
import com.charles.mianti.exception.BusinessException;
import com.charles.mianti.model.dto.interview.AiInterviewResponseDTO;
import com.charles.mianti.model.dto.interview.InterviewAnswerRequest;
import com.charles.mianti.model.dto.interview.InterviewStartRequest;
import com.charles.mianti.model.entity.User;
import com.charles.mianti.service.InterviewService;
import com.charles.mianti.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * AI 面试接口
 */
@RestController
@RequestMapping("/interview")
public class InterviewController {

    @Resource
    private InterviewService interviewService;

    @Resource
    private UserService userService;

    /**
     * 开始面试
     */
    @PostMapping("/start")
    public BaseResponse<Long> startInterview(@RequestBody InterviewStartRequest interviewStartRequest,
                                             HttpServletRequest request) {
        if (interviewStartRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long sessionId = interviewService.startInterview(
                loginUser.getId(),
                interviewStartRequest.getMode(),
                interviewStartRequest.getBankId()
        );
        return ResultUtils.success(sessionId);
    }

    /**
     * 提交回答
     */
    @PostMapping("/answer")
    public BaseResponse<AiInterviewResponseDTO> answerQuestion(@RequestBody InterviewAnswerRequest interviewAnswerRequest,
                                                               HttpServletRequest request) {
        if (interviewAnswerRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.getLoginUser(request);
        AiInterviewResponseDTO response = interviewService.answerQuestion(
                interviewAnswerRequest.getSessionId(),
                interviewAnswerRequest.getAnswer()
        );
        return ResultUtils.success(response);
    }
}
