import axios from '@/utils/axios';
import type { 
  QuestionBankQuestionVO, 
  QuestionBankQuestionQueryRequest, 
  QuestionBankQuestionAddRequest, 
  QuestionBankQuestionBatchAddRequest, 
  QuestionBankQuestionBatchRemoveRequest, 
  QuestionBankQuestionRemoveRequest,
  BaseResponse, 
  Page 
} from '@/types';

export async function getQuestionBankQuestionList(query: QuestionBankQuestionQueryRequest) {
  return axios.post<BaseResponse<Page<QuestionBankQuestionVO>>>('/questionBankQuestion/list/page/vo', query);
}

export async function addQuestionToBank(data: QuestionBankQuestionAddRequest) {
  return axios.post<BaseResponse<number>>('/questionBankQuestion/add', data);
}

export async function removeQuestionFromBank(data: QuestionBankQuestionRemoveRequest) {
  return axios.post<BaseResponse<boolean>>('/questionBankQuestion/remove', data);
}

export async function batchAddQuestionsToBank(data: QuestionBankQuestionBatchAddRequest) {
  return axios.post<BaseResponse<boolean>>('/questionBankQuestion/add/batch', data);
}

export async function batchRemoveQuestionsFromBank(data: QuestionBankQuestionBatchRemoveRequest) {
  return axios.post<BaseResponse<boolean>>('/questionBankQuestion/remove/batch', data);
}

export async function getMyQuestionBankQuestionList(query: QuestionBankQuestionQueryRequest) {
  return axios.post<BaseResponse<Page<QuestionBankQuestionVO>>>('/questionBankQuestion/my/list/page/vo', query);
}
