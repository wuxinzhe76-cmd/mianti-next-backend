package com.charles.mianti.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 代码提交
 * @TableName submission
 */
@TableName(value ="submission")
@Data
public class Submission implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 编程语言代码
     */
    private String languageCode;

    /**
     * 提交的代码
     */
    private String code;

    /**
     * 提交状态（PENDING/JUDGING/ACCEPTED/WA/TLE/MLE/RE/CE）
     */
    private String status;

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
     * 错误信息
     */
    private String errorMessage;

    /**
     * 提交 IP
     */
    private String ip;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
