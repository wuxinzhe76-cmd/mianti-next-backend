package com.charles.mianti.model.dto.interview;

import lombok.Data;

import java.io.Serializable;

/**
 * 回答 AI 面试问题请求
 */
@Data
public class InterviewAnswerRequest implements Serializable {

    /**
     * 面试会话 ID
     */
    private Long sessionId;

    /**
     * 用户回答内容
     */
    private String answer;

    private static final long serialVersionUID = 1L;
}
