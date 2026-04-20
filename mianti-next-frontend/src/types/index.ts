// API响应类型
export interface BaseResponse<T> {
  code: number;
  message: string;
  data: T;
  [key: string]: any;
}

// 分页响应类型
export interface Page<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

// 用户相关类型
export interface UserLoginRequest {
  userAccount: string;
  userPassword: string;
}

export interface UserRegisterRequest {
  userAccount: string;
  userPassword: string;
  checkPassword: string;
}

export interface LoginUserVO {
  id: number;
  userName: string;
  userAvatar: string;
  userProfile: string;
  userRole: string;
  createTime: string;
  updateTime: string;
}

// 题库相关类型
export interface QuestionBankAddRequest {
  title?: string;
  name?: string;
  description: string;
  picture?: string;
}

export interface QuestionBankUpdateRequest {
  id: number;
  title?: string;
  name?: string;
  description: string;
  picture?: string;
}

export interface QuestionBankQueryRequest {
  id?: number;
  notId?: number;
  userId?: number;
  title?: string;
  name?: string;
  searchText?: string;
  description?: string;
  picture?: string;
  current: number;
  pageSize: number;
  needQueryQuestionList?: boolean;
}

export interface QuestionBankVO {
  id: number;
  title: string;
  name?: string;
  description: string;
  picture?: string;
  userId: number;
  createTime: string;
  updateTime: string;
  questionPage?: Page<QuestionVO>;
}

// 题目相关类型
export interface QuestionAddRequest {
  title: string;
  content: string;
  type?: string;
  difficulty?: string | number;
  questionBankId?: number;
  tags: string[];
  answer: string;
}

export interface QuestionUpdateRequest {
  id: number;
  title: string;
  content: string;
  type?: string;
  difficulty?: string | number;
  questionBankId?: number;
  tags: string[];
  answer: string;
}

export interface QuestionQueryRequest {
  id?: number;
  notId?: number;
  userId?: number;
  title?: string;
  searchText?: string;
  content?: string;
  type?: string;
  difficulty?: string | number;
  tags?: string[];
  answer?: string;
  questionBankId?: number;
  current: number;
  pageSize: number;
}

export interface QuestionVO {
  id: number;
  title: string;
  content: string;
  type?: string;
  difficulty?: string | number;
  questionBankId?: number;
  answer: string;
  tagList: string[];
  // 兼容旧字段，避免历史组件直接读取 tags 导致空白
  tags?: string[];
  userId: number;
  createTime: string;
  updateTime: string;
}

// 题库题目关联相关类型
export interface QuestionBankQuestionAddRequest {
  questionBankId: number;
  questionId: number;
  sort: number;
}

export interface QuestionBankQuestionUpdateRequest {
  id: number;
  questionBankId: number;
  questionId: number;
  sort: number;
}

export interface QuestionBankQuestionQueryRequest {
  id?: number;
  questionBankId?: number;
  questionId?: number;
  userId?: number;
  current: number;
  pageSize: number;
}

export interface QuestionBankQuestionBatchAddRequest {
  questionBankId: number;
  questionIdList: number[];
}

export interface QuestionBankQuestionBatchRemoveRequest {
  questionBankId: number;
  questionIdList: number[];
}

export interface QuestionBankQuestionRemoveRequest {
  questionBankId: number;
  questionId: number;
}

export interface QuestionBankQuestionVO {
  id: number;
  questionBankId: number;
  questionId: number;
  userId: number;
  createTime: string;
  updateTime: string;
  tagList?: string[];
}

// 通用删除请求
export interface DeleteRequest {
  id: number;
}

// AI 面试相关类型
export interface InterviewStartRequest {
  mode: number;  // 1 = 指定题库, 2 = 大厂随机
  bankId?: number;
}

export interface InterviewStartResponse {
  sessionId: number;
  openingQuestion: string;
  currentTopicMastery: number;
}

export interface InterviewAnswerRequest {
  sessionId: number;
  answer: string;
}

export type ActionDirective = 'DEEP_DIVE' | 'NEXT_QUESTION' | 'END_INTERVIEW';

export interface AiInterviewResponseDTO {
  replyToUser: string;
  actionDirective: ActionDirective;
  currentTopicMastery: number;
}

export interface ChatMessage {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
  masteryScore?: number;
  directive?: ActionDirective;
}
