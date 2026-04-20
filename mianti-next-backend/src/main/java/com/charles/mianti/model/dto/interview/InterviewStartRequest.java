package com.charles.mianti.model.dto.interview;

import lombok.Data;

import java.io.Serializable;

/**
 * 开始 AI 面试请求
 */
@Data
public class InterviewStartRequest implements Serializable {

    /**
     * 面试模式：1-指定题库，2-大厂随机
     */
    private Integer mode;

    /**
     * 题库 ID（模式 1 时必填）
     */
    private Long bankId;

    private static final long serialVersionUID = 1L;
}
