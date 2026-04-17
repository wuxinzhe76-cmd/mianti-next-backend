package com.charles.mianti.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 面试问答明细
 * @TableName interview_record
 */
@TableName(value = "interview_record")
@Data
public class InterviewRecord implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的面试会话 ID
     */
    private Long sessionId;

    /**
     * 当前讨论的具体题目 ID
     */
    private Long questionId;

    /**
     * 发送方角色：user 或 assistant
     */
    private String role;

    /**
     * 具体的回答或提问内容
     */
    private String content;

    /**
     * 当前对话属于第几轮
     */
    private Integer roundNum;

    /**
     * 记录时间
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
