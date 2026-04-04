package com.charles.mianti.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 测试用例
 * @TableName test_case
 */
@TableName(value ="test_case")
@Data
public class TestCase implements Serializable {
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
     * 输入样例
     */
    private String input;

    /**
     * 输出样例
     */
    private String output;

    /**
     * 是否为示例测试用例（0-隐藏，1-示例）
     */
    private Integer isExample;

    /**
     * 分值
     */
    private Integer score;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
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
