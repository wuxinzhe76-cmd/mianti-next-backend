package com.charles.mianti.service;

import com.charles.mianti.model.dto.interview.AiInterviewResponseDTO;
import com.charles.mianti.model.dto.interview.InterviewStartResponse;

/**
 * 面试服务
 *
 * @author Charles
 */
public interface InterviewService {

    /**
     * 开始一场新的面试
     *
     * @param userId 用户 ID
     * @param mode   面试模式 (1: 指定题库, 2: 大厂随机)
     * @param bankId 题库 ID (仅在指定题库模式下有效)
     * @return 会话信息（含 sessionId 和开场问题）
     */
    InterviewStartResponse startInterview(Long userId, Integer mode, Long bankId);

    /**
     * 处理用户的回答
     *
     * @param sessionId 会话 ID
     * @param answer    用户的回答内容
     * @return AI 的结构化响应
     */
    AiInterviewResponseDTO answerQuestion(Long sessionId, String answer);
}
