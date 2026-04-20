import axios from '@/utils/axios';
import type {
  InterviewStartRequest,
  InterviewStartResponse,
  InterviewAnswerRequest,
  AiInterviewResponseDTO,
  BaseResponse,
} from '@/types';

export async function startInterview(data: InterviewStartRequest) {
  return axios.post<BaseResponse<InterviewStartResponse>>('/interview/start', data);
}

export async function submitAnswer(data: InterviewAnswerRequest) {
  return axios.post<BaseResponse<AiInterviewResponseDTO>>('/interview/answer', data);
}
