import axios from '@/utils/axios';
import type { 
  QuestionVO, 
  QuestionQueryRequest, 
  QuestionAddRequest, 
  QuestionUpdateRequest, 
  BaseResponse, 
  Page 
} from '@/types';

export async function getQuestionList(query: QuestionQueryRequest) {
  return axios.post<BaseResponse<Page<QuestionVO>>>('/question/list/page/vo', query);
}

export async function getQuestionDetail(id: number) {
  return axios.get<BaseResponse<QuestionVO>>('/question/get/vo', {
    params: { id }
  });
}

export async function createQuestion(data: QuestionAddRequest) {
  return axios.post<BaseResponse<number>>('/question/add', data);
}

export async function updateQuestion(data: QuestionUpdateRequest) {
  return axios.post<BaseResponse<boolean>>('/question/update', data);
}

export async function deleteQuestion(id: number) {
  return axios.post<BaseResponse<boolean>>('/question/delete', { id });
}

export async function getMyQuestionList(query: QuestionQueryRequest) {
  return axios.post<BaseResponse<Page<QuestionVO>>>('/question/my/list/page/vo', query);
}

export async function searchQuestion(query: QuestionQueryRequest) {
  return axios.post<BaseResponse<Page<QuestionVO>>>('/question/search/page/vo', query);
}

export async function batchDeleteQuestions(questionIdList: number[]) {
  return axios.post<BaseResponse<boolean>>('/question/delete/batch', { questionIdList });
}
