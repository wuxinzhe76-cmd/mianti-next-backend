package com.charles.mianti.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 编程语言
 * @TableName programming_language
 */
@TableName(value ="programming_language")
@Data
public class ProgrammingLanguage implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 语言名称
     */
    private String languageName;

    /**
     * 语言代码（java/python/cpp）
     */
    private String languageCode;

    /**
     * 版本信息
     */
    private String version;

    /**
     * 编译命令
     */
    private String compileCommand;

    /**
     * 运行命令
     */
    private String runCommand;

    /**
     * 语言图标
     */
    private String icon;

    /**
     * 是否启用
     */
    private Integer isActive;

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
