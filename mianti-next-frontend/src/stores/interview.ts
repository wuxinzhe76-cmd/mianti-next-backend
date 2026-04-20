import { defineStore } from 'pinia';
import { startInterview, submitAnswer } from '@/services/interview';
import type {
  InterviewStartRequest,
  InterviewAnswerRequest,
  ChatMessage,
  AiInterviewResponseDTO,
} from '@/types';

export const useInterviewStore = defineStore('interview', {
  state: () => ({
    sessionId: null as number | null,
    messages: [] as ChatMessage[],
    currentMastery: 0,
    isEnded: false,
    currentDirective: null as string | null,
    loading: false,
  }),

  actions: {
    async startInterview(data: InterviewStartRequest) {
      this.loading = true;
      try {
        const response = await startInterview(data);
        const { sessionId, openingQuestion, currentTopicMastery } = response.data.data;

        this.sessionId = sessionId;
        this.messages = [];
        this.currentMastery = currentTopicMastery;
        this.isEnded = false;
        this.currentDirective = null;

        this.messages.push({
          id: crypto.randomUUID(),
          role: 'assistant',
          content: openingQuestion,
          timestamp: new Date(),
          masteryScore: currentTopicMastery,
        });

        return response;
      } finally {
        this.loading = false;
      }
    },

    async submitAnswer(data: InterviewAnswerRequest) {
      this.loading = true;
      try {
        const userMessage: ChatMessage = {
          id: crypto.randomUUID(),
          role: 'user',
          content: data.answer,
          timestamp: new Date(),
        };
        this.messages.push(userMessage);

        const response = await submitAnswer(data);
        const aiData: AiInterviewResponseDTO = response.data.data;

        const assistantMessage: ChatMessage = {
          id: crypto.randomUUID(),
          role: 'assistant',
          content: aiData.replyToUser,
          timestamp: new Date(),
          masteryScore: aiData.currentTopicMastery,
          directive: aiData.actionDirective,
        };
        this.messages.push(assistantMessage);

        this.currentMastery = aiData.currentTopicMastery;
        this.currentDirective = aiData.actionDirective;

        if (aiData.actionDirective === 'END_INTERVIEW') {
          this.isEnded = true;
        }

        return response;
      } finally {
        this.loading = false;
      }
    },

    resetInterview() {
      this.sessionId = null;
      this.messages = [];
      this.currentMastery = 0;
      this.isEnded = false;
      this.currentDirective = null;
      this.loading = false;
    },
  },
});
