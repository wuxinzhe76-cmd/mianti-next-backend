package com.charles.mianti.model.dto.judge;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 代码提交请求
 *
 * @author Charles
 */
@Data
public class SubmitCodeRequest implements Serializable {

    /**
     * 题目 id
     */
    @NotNull(message = "题目 id 不能为空")
    private Long questionId;

    /**
     * 编程语言代码
     */
    @NotBlank(message = "编程语言不能为空")
    private String languageCode;

    /**
     * 提交的代码
     */
    @NotBlank(message = "提交的代码不能为空")
    private String code;

    private static final long serialVersionUID = 1L;
}
