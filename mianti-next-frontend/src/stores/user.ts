import { defineStore } from 'pinia';
import { login, register, logout, getCurrentUser } from '@/services/auth';
import type { LoginUserVO, UserLoginRequest, UserRegisterRequest } from '@/types';

export const useUserStore = defineStore('user', {
  state: () => {
    const savedUser = localStorage.getItem('user');
    return {
      user: savedUser ? JSON.parse(savedUser) : null as LoginUserVO | null,
      token: localStorage.getItem('token') || '',
      loading: false,
    };
  },
  getters: {
    isLoggedIn: (state) => !!state.user,
    isAdmin: (state) => state.user?.userRole === 'admin',
    /** 展示用昵称：已登录但昵称为空时仍显示账号或「用户」，避免与「未登录」混淆 */
    displayLabel: (state) => {
      if (!state.user) return '未登录';
      const name = state.user.userName?.trim();
      return name || '用户';
    },
  },
  actions: {
    async login(loginData: UserLoginRequest) {
      this.loading = true;
      try {
        const response = await login(loginData);
        this.user = response.data.data;
        localStorage.setItem('user', JSON.stringify(this.user));
        return response;
      } finally {
        this.loading = false;
      }
    },
    async register(registerData: UserRegisterRequest) {
      this.loading = true;
      try {
        const response = await register(registerData);
        return response;
      } finally {
        this.loading = false;
      }
    },
    async logout() {
      try {
        await logout();
      } finally {
        this.token = '';
        this.user = null;
        localStorage.removeItem('user');
        localStorage.removeItem('token');
      }
    },
    async getCurrentUser() {
      const response = await getCurrentUser();
      this.user = response.data.data;
      localStorage.setItem('user', JSON.stringify(this.user));
      return response;
    },
    initializeFromStorage() {
      const savedUser = localStorage.getItem('user');
      if (savedUser) {
        try {
          this.user = JSON.parse(savedUser);
        } catch {
          localStorage.removeItem('user');
        }
      }
    },
  },
});
