package com.charles.mianti.model.enums;

import lombok.Getter;

/**
 * 面试模式枚举
 */
@Getter
public enum InterviewModeEnum {

    SPECIFIED_BANK("指定题库模式", 1),
    RANDOM_BIG_TECH("大厂随机模式", 2);

    private final String text;
    private final int value;

    InterviewModeEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     */
    public static InterviewModeEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (InterviewModeEnum e : values()) {
            if (e.getValue() == value) {
                return e;
            }
        }
        return null;
    }
}
