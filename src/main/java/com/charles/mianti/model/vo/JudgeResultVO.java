package com.charles.mianti.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 判题结果响应
 *
 * @author Charles
 */
@Data
public class JudgeResultVO implements Serializable {

    /**
     * 提交 id
     */
    private Long submissionId;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 题目名称
     */
    private String questionTitle;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 编程语言代码
     */
    private String languageCode;

    /**
     * 编程语言名称
     */
    private String languageName;

    /**
     * 判题结果（ACCEPTED/WA/TLE/MLE/RE/CE）
     */
    private String verdict;

    /**
     * 判题结果文本描述
     */
    private String verdictText;

    /**
     * 执行时间（ms）
     */
    private Integer executionTime;

    /**
     * 执行内存（KB）
     */
    private Integer executionMemory;

    /**
     * 测试用例得分
     */
    private Integer testCaseScore;

    /**
     * 总测试用例数
     */
    private Integer totalTestCase;

    /**
     * 通过的测试用例数
     */
    private Integer passedTestCase;

    /**
     * 各测试用例结果（JSON 数组）
     */
    private String testCaseResults;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 判题时间
     */
    private String judgeTime;

    private static final long serialVersionUID = 1L;
}
