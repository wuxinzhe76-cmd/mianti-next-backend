package com.charles.mianti.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 判题结果详情
 * @TableName judge_result
 */
@TableName(value ="judge_result")
@Data
public class JudgeResult implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 提交 id
     */
    private Long submissionId;

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
     * 判题结果（ACCEPTED/WA/TLE/MLE/RE/CE）
     */
    private String verdict;

    /**
     * 执行时间（ms）
     */
    private Integer executionTime;

    /**
     * 执行内存（KB）
     */
    private Integer executionMemory;

    /**
     * 通过的测试用例数
     */
    private Integer passedTestCase;

    /**
     * 总测试用例数
     */
    private Integer totalTestCase;

    /**
     * 各测试用例结果（JSON 数组）
     */
    private String testCaseResults;

    /**
     * 编译输出
     */
    private String compileOutput;

    /**
     * 运行输出
     */
    private String runOutput;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 判题服务器
     */
    private String judgeServer;

    /**
     * 判题时间
     */
    private Date judgeTime;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
