package com.charles.mianti.service;

import com.charles.mianti.model.dto.interview.AiInterviewResponseDTO;
import com.charles.mianti.model.enums.ActionDirectiveEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * InterviewService 路由分发逻辑单元测试
 */
class InterviewServiceImplTest {

    @Test
    void testAnswerQuestion_DeepDive_Response() {
        AiInterviewResponseDTO response = new AiInterviewResponseDTO();
        response.setReplyToUser("回答不错，利用了 HashMap 的 O(1) 特性");
        response.setActionDirective(ActionDirectiveEnum.DEEP_DIVE);
        response.setCurrentTopicMastery(75);

        assertEquals(ActionDirectiveEnum.DEEP_DIVE, response.getActionDirective());
        assertEquals("回答不错，利用了 HashMap 的 O(1) 特性", response.getReplyToUser());
        assertEquals(75, response.getCurrentTopicMastery());
    }

    @Test
    void testAnswerQuestion_NextQuestion_Response() {
        AiInterviewResponseDTO response = new AiInterviewResponseDTO();
        response.setReplyToUser("很好，我们来看下一道关于并发编程的题目");
        response.setActionDirective(ActionDirectiveEnum.NEXT_QUESTION);
        response.setCurrentTopicMastery(90);

        assertEquals(ActionDirectiveEnum.NEXT_QUESTION, response.getActionDirective());
        assertNotNull(response.getReplyToUser());
    }

    @Test
    void testAnswerQuestion_EndInterview_Response() {
        AiInterviewResponseDTO response = new AiInterviewResponseDTO();
        response.setReplyToUser("面试结束，感谢你的参与，综合评分 85");
        response.setActionDirective(ActionDirectiveEnum.END_INTERVIEW);
        response.setCurrentTopicMastery(85);

        assertEquals(ActionDirectiveEnum.END_INTERVIEW, response.getActionDirective());
        assertNotNull(response.getReplyToUser());
    }

    @Test
    void testActionDirectiveEnum_Conversion() {
        ActionDirectiveEnum deepDive = ActionDirectiveEnum.getEnumByValue("DEEP_DIVE");
        assertNotNull(deepDive);
        assertEquals("DEEP_DIVE", deepDive.getValue());
        assertEquals("继续追问", deepDive.getText());

        ActionDirectiveEnum nextQuestion = ActionDirectiveEnum.getEnumByValue("NEXT_QUESTION");
        assertNotNull(nextQuestion);
        assertEquals("NEXT_QUESTION", nextQuestion.getValue());

        ActionDirectiveEnum endInterview = ActionDirectiveEnum.getEnumByValue("END_INTERVIEW");
        assertNotNull(endInterview);
        assertEquals("END_INTERVIEW", endInterview.getValue());

        assertNull(ActionDirectiveEnum.getEnumByValue("INVALID"));
        assertNull(ActionDirectiveEnum.getEnumByValue(null));
    }

    @Test
    void testAiInterviewResponseDTO_Fields() {
        AiInterviewResponseDTO dto = new AiInterviewResponseDTO();
        dto.setReplyToUser("test reply");
        dto.setActionDirective(ActionDirectiveEnum.DEEP_DIVE);
        dto.setCurrentTopicMastery(80);

        assertEquals("test reply", dto.getReplyToUser());
        assertEquals(ActionDirectiveEnum.DEEP_DIVE, dto.getActionDirective());
        assertEquals(80, dto.getCurrentTopicMastery());
    }
}
