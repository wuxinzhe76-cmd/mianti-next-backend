package com.charles.mianti.model.enums;

import lombok.Getter;

/**
 * AI 行为指令枚举
 */
@Getter
public enum ActionDirectiveEnum {

    DEEP_DIVE("继续追问", "DEEP_DIVE"),
    NEXT_QUESTION("切换下一题", "NEXT_QUESTION"),
    END_INTERVIEW("结束面试", "END_INTERVIEW");

    private final String text;
    private final String value;

    ActionDirectiveEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     */
    public static ActionDirectiveEnum getEnumByValue(String value) {
        if (value == null) {
            return null;
        }
        for (ActionDirectiveEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
