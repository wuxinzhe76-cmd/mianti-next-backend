package com.charles.mianti.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 面试会话
 * @TableName interview_session
 */
@TableName(value = "interview_session")
@Data
public class InterviewSession implements Serializable {
    /**
     * 主键 (雪花算法)
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 面试者 ID
     */
    private Long userId;

    /**
     * 模式：1-指定题库，2-大厂随机
     */
    private Integer mode;

    /**
     * 关联题库 ID（若是模式1则有值）
     */
    private Long bankId;

    /**
     * 状态：0-进行中，1-已结束，2-已生成报告
     */
    private Integer status;

    /**
     * 本次面试综合评分 (AI生成)
     */
    private Integer score;

    /**
     * 面试开始时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
