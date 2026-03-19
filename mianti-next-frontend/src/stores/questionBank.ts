import { defineStore } from 'pinia';
import { 
  getQuestionBankList, 
  getQuestionBankDetail, 
  createQuestionBank, 
  updateQuestionBank, 
  deleteQuestionBank 
} from '@/services/questionBank';
import type { 
  QuestionBankVO, 
  QuestionBankQueryRequest, 
  QuestionBankAddRequest, 
  QuestionBankUpdateRequest 
} from '@/types';

export const useQuestionBankStore = defineStore('questionBank', {
  state: () => ({
    questionBanks: [] as QuestionBankVO[],
    currentQuestionBank: null as QuestionBankVO | null,
    loading: false,
    total: 0,
  }),
  actions: {
    async getQuestionBankList(query: QuestionBankQueryRequest) {
      this.loading = true;
      try {
        const response = await getQuestionBankList(query);
        this.questionBanks = response.data.data.records;
        this.total = response.data.data.total;
        return response;
      } finally {
        this.loading = false;
      }
    },
    async getQuestionBankDetail(id: number, needQueryQuestionList?: boolean) {
      this.loading = true;
      try {
        const response = await getQuestionBankDetail(id, needQueryQuestionList);
        this.currentQuestionBank = response.data.data;
        return response;
      } finally {
        this.loading = false;
      }
    },
    async createQuestionBank(data: QuestionBankAddRequest) {
      this.loading = true;
      try {
        const response = await createQuestionBank(data);
        return response;
      } finally {
        this.loading = false;
      }
    },
    async updateQuestionBank(data: QuestionBankUpdateRequest) {
      this.loading = true;
      try {
        const response = await updateQuestionBank(data);
        return response;
      } finally {
        this.loading = false;
      }
    },
    async deleteQuestionBank(id: number) {
      this.loading = true;
      try {
        const response = await deleteQuestionBank(id);
        return response;
      } finally {
        this.loading = false;
      }
    },
  },
});
