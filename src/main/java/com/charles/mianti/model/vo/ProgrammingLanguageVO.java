package com.charles.mianti.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 编程语言视图对象
 *
 * @author Charles
 */
@Data
public class ProgrammingLanguageVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 语言名称
     */
    private String languageName;

    /**
     * 语言代码
     */
    private String languageCode;

    /**
     * 版本信息
     */
    private String version;

    /**
     * 语言图标
     */
    private String icon;

    /**
     * 代码模板
     */
    private String template;

    private static final long serialVersionUID = 1L;
}
