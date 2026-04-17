package com.charles.mianti.service.impl;

import com.charles.mianti.config.InterviewPromptConstants;
import com.charles.mianti.model.dto.interview.AiInterviewResponseDTO;
import com.charles.mianti.model.entity.Question;
import com.charles.mianti.service.AiInterviewStrategyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI 面试策略服务实现
 */
@Slf4j
@Service
public class AiInterviewStrategyServiceImpl implements AiInterviewStrategyService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiInterviewStrategyServiceImpl(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    @Override
    public AiInterviewResponseDTO evaluateAnswer(Question question, List<String> conversationHistory, String userAnswer) {
        String systemPrompt = buildSystemPrompt(question);
        String userPrompt = buildUserPrompt(question, conversationHistory, userAnswer);

        log.info("Calling Spring AI for interview evaluation, questionId: {}", question.getId());

        try {
            AiInterviewResponseDTO response = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .entity(AiInterviewResponseDTO.class);

            log.info("AI response: action={}, mastery={}", response.getActionDirective(), response.getCurrentTopicMastery());
            return response;
        } catch (Exception e) {
            log.error("Failed to call Spring AI for interview evaluation", e);
            throw new RuntimeException("AI 评估失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateOpeningQuestion(Question question) {
        String systemPrompt = """
                你是一个资深的 Java 架构师面试官。
                请根据下面的题目信息，用自然语言向候选人提出问题，引导他开始回答。
                你的输出只有一段简短的提问，不要包含任何 JSON 或其他格式。
                """;

        String userPrompt = """
                题目: %s
                描述: %s

                请向候选人提出这个问题。
                """.formatted(question.getTitle(), question.getContent());

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
    }

    /**
     * 构造系统 Prompt（包含 JSON 格式约束）
     */
    private String buildSystemPrompt(Question question) {
        return InterviewPromptConstants.SYSTEM_PROMPT
                .replace("{{questionTitle}}", question.getTitle())
                .replace("{{questionContent}}", question.getContent())
                .replace("{{questionAnswer}}", question.getAnswer() != null ? question.getAnswer() : "无");
    }

    /**
     * 构造用户 Prompt（包含对话历史和当前回答）
     */
    private String buildUserPrompt(Question question, List<String> conversationHistory, String userAnswer) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("【当前用户回答】\n").append(userAnswer).append("\n\n");

        if (!conversationHistory.isEmpty()) {
            promptBuilder.append("【之前的对话历史】\n");
            for (int i = 0; i < conversationHistory.size(); i++) {
                promptBuilder.append(conversationHistory.get(i)).append("\n");
            }
            promptBuilder.append("\n");
        }

        promptBuilder.append("请根据以上回答和历史对话，评估用户的表现，并返回严格的 JSON 格式结果。");
        return promptBuilder.toString();
    }
}
