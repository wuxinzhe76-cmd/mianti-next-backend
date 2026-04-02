package com.charles.mianti.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 提交记录视图对象
 *
 * @author Charles
 */
@Data
public class SubmissionVO implements Serializable {

    /**
     * 提交 id
     */
    private Long id;

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
     * 用户昵称
     */
    private String userName;

    /**
     * 编程语言代码
     */
    private String languageCode;

    /**
     * 编程语言名称
     */
    private String languageName;

    /**
     * 代码长度
     */
    private Integer codeLength;

    /**
     * 提交状态
     */
    private String status;

    /**
     * 状态文本描述
     */
    private String statusText;

    /**
     * 执行时间（ms）
     */
    private Integer executionTime;

    /**
     * 执行内存（KB）
     */
    private Integer executionMemory;

    /**
     * 通过率
     */
    private String passRate;

    /**
     * 提交时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
