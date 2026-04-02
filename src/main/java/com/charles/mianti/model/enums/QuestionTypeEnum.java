package com.charles.mianti.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 题目类型枚举
 *
 * @author Charles
 */
public enum QuestionTypeEnum {

    PROGRAMMING("编程题", "PROGRAMMING"),
    CHOICE("选择题", "CHOICE"),
    FILL_IN("填空题", "FILL_IN");

    private final String text;
    private final String value;

    QuestionTypeEnum(String text, String value) {
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
    public static QuestionTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (QuestionTypeEnum anEnum : QuestionTypeEnum.values()) {
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
