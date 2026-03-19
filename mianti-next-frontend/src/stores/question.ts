import { defineStore } from 'pinia';
import { 
  getQuestionList, 
  getQuestionDetail, 
  createQuestion, 
  updateQuestion, 
  deleteQuestion,
  batchDeleteQuestions
} from '@/services/question';
import type { 
  QuestionVO, 
  QuestionQueryRequest, 
  QuestionAddRequest, 
  QuestionUpdateRequest 
} from '@/types';

export const useQuestionStore = defineStore('question', {
  state: () => ({
    questions: [] as QuestionVO[],
    currentQuestion: null as QuestionVO | null,
    loading: false,
    total: 0,
  }),
  actions: {
    async getQuestionList(query: QuestionQueryRequest) {
      this.loading = true;
      try {
        const response = await getQuestionList(query);
        this.questions = response.data.data.records;
        this.total = response.data.data.total;
        return response;
      } finally {
        this.loading = false;
      }
    },
    async getQuestionDetail(id: number) {
      this.loading = true;
      try {
        const response = await getQuestionDetail(id);
        this.currentQuestion = response.data.data;
        return response;
      } finally {
        this.loading = false;
      }
    },
    async createQuestion(data: QuestionAddRequest) {
      this.loading = true;
      try {
        const response = await createQuestion(data);
        return response;
      } finally {
        this.loading = false;
      }
    },
    async updateQuestion(data: QuestionUpdateRequest) {
      this.loading = true;
      try {
        const response = await updateQuestion(data);
        return response;
      } finally {
        this.loading = false;
      }
    },
    async deleteQuestion(id: number) {
      this.loading = true;
      try {
        const response = await deleteQuestion(id);
        return response;
      } finally {
        this.loading = false;
      }
    },
    async batchDeleteQuestions(questionIdList: number[]) {
      this.loading = true;
      try {
        const response = await batchDeleteQuestions(questionIdList);
        return response;
      } finally {
        this.loading = false;
      }
    },
  },
});
