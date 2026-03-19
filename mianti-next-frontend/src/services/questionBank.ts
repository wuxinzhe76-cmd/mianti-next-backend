import axios from '@/utils/axios';
import type { 
  QuestionBankVO, 
  QuestionBankQueryRequest, 
  QuestionBankAddRequest, 
  QuestionBankUpdateRequest, 
  BaseResponse, 
  Page
} from '@/types';

export async function getQuestionBankList(query: QuestionBankQueryRequest) {
  return axios.post<BaseResponse<Page<QuestionBankVO>>>('/questionBank/list/page/vo', query);
}

export async function getQuestionBankDetail(id: number, needQueryQuestionList?: boolean) {
  return axios.get<BaseResponse<QuestionBankVO>>('/questionBank/get/vo', {
    params: { id, needQueryQuestionList }
  });
}

export async function getQuestionBankDetailWithPage(
  id: number,
  current: number,
  pageSize: number,
  needQueryQuestionList = true,
) {
  return axios.get<BaseResponse<QuestionBankVO>>('/questionBank/get/vo', {
    params: { id, current, pageSize, needQueryQuestionList },
  });
}

export async function createQuestionBank(data: QuestionBankAddRequest) {
  return axios.post<BaseResponse<number>>('/questionBank/add', data);
}

export async function updateQuestionBank(data: QuestionBankUpdateRequest) {
  return axios.post<BaseResponse<boolean>>('/questionBank/update', data);
}

export async function deleteQuestionBank(id: number) {
  return axios.post<BaseResponse<boolean>>('/questionBank/delete', { id });
}

export async function getMyQuestionBankList(query: QuestionBankQueryRequest) {
  return axios.post<BaseResponse<Page<QuestionBankVO>>>('/questionBank/my/list/page/vo', query);
}
