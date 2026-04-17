package com.charles.mianti.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 面试报告消息生产者（RabbitMQ 未配置前的占位实现）
 * <p>
 * 当配置 RabbitMQ 后，只需替换本方法中的 log 为 rabbitTemplate.convertAndSend 即可。
 * 例如：rabbitTemplate.convertAndSend("interview.exchange", "interview.report", sessionId);
 * </p>
 */
@Slf4j
@Component
public class InterviewReportProducer {

    // TODO: 引入 RabbitTemplate，替换下方逻辑为实际消息发送
    // private final RabbitTemplate rabbitTemplate;
    // public InterviewReportProducer(RabbitTemplate rabbitTemplate) {
    //     this.rabbitTemplate = rabbitTemplate;
    // }

    /**
     * 发送异步面试报告生成消息
     *
     * @param sessionId 面试会话 ID
     */
    public void sendReportMessage(Long sessionId) {
        log.info("[MOCK] Interview report message sent for sessionId={}", sessionId);
        // 实际实现示例：
        // rabbitTemplate.convertAndSend("interview.exchange", "interview.report", sessionId);
    }
}
