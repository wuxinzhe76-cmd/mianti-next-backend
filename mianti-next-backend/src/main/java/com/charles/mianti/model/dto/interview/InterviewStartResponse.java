package com.charles.mianti.model.dto.interview;

import lombok.Data;

import java.io.Serializable;

/**
 * 面试开始响应
 */
@Data
public class InterviewStartResponse implements Serializable {
    private Long sessionId;
    private String openingQuestion;
    private Integer currentTopicMastery;
    private static final long serialVersionUID = 1L;
}
