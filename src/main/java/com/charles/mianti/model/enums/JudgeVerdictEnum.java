package com.charles.mianti.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 判题结果枚举
 *
 * @author Charles
 */
public enum JudgeVerdictEnum {

    PENDING("待判题", "PENDING"),
    JUDGING("判题中", "JUDGING"),
    ACCEPTED("答案正确", "ACCEPTED"),
    WRONG_ANSWER("答案错误", "WA"),
    TIME_LIMIT_EXCEEDED("超时", "TLE"),
    MEMORY_LIMIT_EXCEEDED("内存超限", "MLE"),
    RUNTIME_ERROR("运行错误", "RE"),
    COMPILE_ERROR("编译错误", "CE");

    private final String text;
    private final String value;

    JudgeVerdictEnum(String text, String value) {
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
    public static JudgeVerdictEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JudgeVerdictEnum anEnum : JudgeVerdictEnum.values()) {
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
