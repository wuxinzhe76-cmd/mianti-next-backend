package com.charles.mianti.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 题目难度枚举
 *
 * @author Charles
 */
public enum QuestionDifficultyEnum {

    EASY("简单", "EASY"),
    MEDIUM("中等", "MEDIUM"),
    HARD("困难", "HARD");

    private final String text;
    private final String value;

    QuestionDifficultyEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     */
    public static QuestionDifficultyEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (QuestionDifficultyEnum anEnum : QuestionDifficultyEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
