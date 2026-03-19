import axios from '@/utils/axios';
import type { UserLoginRequest, UserRegisterRequest, LoginUserVO, BaseResponse } from '@/types';

export async function login(data: UserLoginRequest) {
  return axios.post<BaseResponse<LoginUserVO>>('/user/login', data);
}

export async function register(data: UserRegisterRequest) {
  return axios.post<BaseResponse<number>>('/user/register', data);
}

export async function logout() {
  return axios.post<BaseResponse<boolean>>('/user/logout');
}

export async function getCurrentUser() {
  return axios.get<BaseResponse<LoginUserVO>>('/user/current');
}

export async function getWxOpenAppId() {
  return axios.get<BaseResponse<string>>('/user/login/wx_open/app_id');
}

export async function loginByWxOpen(code: string) {
  return axios.get<BaseResponse<LoginUserVO>>(`/user/login/wx_open?code=${code}`);
}
