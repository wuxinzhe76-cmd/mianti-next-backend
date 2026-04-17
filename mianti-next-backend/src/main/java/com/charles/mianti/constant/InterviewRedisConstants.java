package com.charles.mianti.constant;

/**
 * AI 面试 Redis Key 常量
 */
public class InterviewRedisConstants {

    /**
     * 会话对话历史前缀: interview:history:{sessionId}
     * 存储格式：RList<String>，每条为 "role: content"
     */
    public static final String HISTORY_KEY_PREFIX = "interview:history:";

    /**
     * 当前题目 ID: interview:question:{sessionId}
     */
    public static final String QUESTION_KEY_PREFIX = "interview:question:";

    /**
     * 当前轮次: interview:round:{sessionId}
     */
    public static final String ROUND_KEY_PREFIX = "interview:round:";

    /**
     * 会话已抽题列表: interview:used:{sessionId}
     * 用于 NEXT_QUESTION 模式下避免重复抽题
     */
    public static final String USED_QUESTIONS_KEY_PREFIX = "interview:used:";

    /**
     * 面试热数据过期时间（秒），默认 2 小时
     */
    public static final long TTL_SECONDS = 7200;
}
