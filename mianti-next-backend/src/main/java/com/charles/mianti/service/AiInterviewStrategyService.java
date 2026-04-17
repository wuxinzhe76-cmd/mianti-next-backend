package com.charles.mianti.service;

import com.charles.mianti.model.dto.interview.AiInterviewResponseDTO;
import com.charles.mianti.model.entity.Question;

import java.util.List;

/**
 * AI 面试策略服务 - 封装 Spring AI 调用与结构化输出逻辑
 */
public interface AiInterviewStrategyService {

    /**
     * 调用 AI 评估用户的回答
     *
     * @param question       当前题目信息
     * @param conversationHistory 历史对话（交替的 user/assistant 消息）
     * @param userAnswer     用户当前回答
     * @return AI 的结构化响应
     */
    AiInterviewResponseDTO evaluateAnswer(Question question, List<String> conversationHistory, String userAnswer);

    /**
     * 生成面试开场白/首道题目
     *
     * @param question 当前题目信息
     * @return AI 的开场提问
     */
    String generateOpeningQuestion(Question question);
}
