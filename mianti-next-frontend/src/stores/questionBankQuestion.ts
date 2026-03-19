import { defineStore } from 'pinia';
import { 
  getQuestionBankQuestionList, 
  addQuestionToBank, 
  removeQuestionFromBank, 
  batchAddQuestionsToBank, 
  batchRemoveQuestionsFromBank 
} from '@/services/questionBankQuestion';
import type { 
  QuestionBankQuestionVO, 
  QuestionBankQuestionQueryRequest, 
  QuestionBankQuestionAddRequest, 
  QuestionBankQuestionBatchAddRequest, 
  QuestionBankQuestionBatchRemoveRequest,
  QuestionBankQuestionRemoveRequest
} from '@/types';

export const useQuestionBankQuestionStore = defineStore('questionBankQuestion', {
  state: () => ({
    questionBankQuestions: [] as QuestionBankQuestionVO[],
    loading: false,
    total: 0,
  }),
  actions: {
    async getQuestionBankQuestionList(query: QuestionBankQuestionQueryRequest) {
      this.loading = true;
      try {
        const response = await getQuestionBankQuestionList(query);
        this.questionBankQuestions = response.data.data.records;
        this.total = response.data.data.total;
        return response;
      } finally {
        this.loading = false;
      }
    },
    async addQuestionToBank(data: QuestionBankQuestionAddRequest) {
      this.loading = true;
      try {
        const response = await addQuestionToBank(data);
        return response;
      } finally {
        this.loading = false;
      }
    },
    async removeQuestionFromBank(data: QuestionBankQuestionRemoveRequest) {
      this.loading = true;
      try {
        const response = await removeQuestionFromBank(data);
        return response;
      } finally {
        this.loading = false;
      }
    },
    async batchAddQuestionsToBank(data: QuestionBankQuestionBatchAddRequest) {
      this.loading = true;
      try {
        const response = await batchAddQuestionsToBank(data);
        return response;
      } finally {
        this.loading = false;
      }
    },
    async batchRemoveQuestionsFromBank(data: QuestionBankQuestionBatchRemoveRequest) {
      this.loading = true;
      try {
        const response = await batchRemoveQuestionsFromBank(data);
        return response;
      } finally {
        this.loading = false;
      }
    },
  },
});
