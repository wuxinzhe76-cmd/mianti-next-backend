package com.charles.mianti.model.dto.interview;

import com.charles.mianti.model.enums.ActionDirectiveEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * AI 面试结构化响应 DTO
 */
@Data
public class AiInterviewResponseDTO implements Serializable {

    /**
     * AI 回复给用户的内容
     */
    private String replyToUser;

    /**
     * 行为指令
     */
    private ActionDirectiveEnum actionDirective;

    /**
     * 当前知识点掌握度 (0-100)
     */
    private Integer currentTopicMastery;

    private static final long serialVersionUID = 1L;
}
