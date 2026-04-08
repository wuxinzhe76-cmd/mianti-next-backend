import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/user';

// 开发环境使用相对路径 /api，由 Vite 代理到后端，保证与页面同源，Sa-Token Cookie 才能随请求携带。
// 生产环境可通过 VITE_API_BASE_URL 指定（例如与站点同源的 /api）；未配置时默认直连本地后端便于联调。
const baseURL =
  import.meta.env.VITE_API_BASE_URL ??
  (import.meta.env.DEV ? '/api' : 'http://localhost:8101/api');

const axiosInstance = axios.create({
  baseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

// 请求拦截器
axiosInstance.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
axiosInstance.interceptors.response.use(
  (response) => {
    const { code, message } = response.data;
    if (code !== 0) {
      ElMessage.error(message || '请求失败');
      return Promise.reject(new Error(message || '请求失败'));
    }
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      const userStore = useUserStore();
      userStore.logout();
      ElMessage.error('登录过期，请重新登录');
    } else {
      ElMessage.error(error.message || '网络错误');
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
