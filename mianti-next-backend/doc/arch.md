# 技术选择

## 框架与语言
- **框架**: Vue 3
- **语言**: TypeScript
- **构建工具**: Vite
- **状态管理**: Pinia
- **路由**: Vue Router
- **UI组件库**: Element Plus
- **HTTP客户端**: Axios
- **CSS预处理器**: SCSS
- **代码规范**: ESLint + Prettier
- **单元测试**: Vitest

## 第三方依赖
- **axios**: 用于API请求
- **pinia**: 状态管理
- **vue-router**: 路由管理
- **element-plus**: UI组件库
- **@element-plus/icons-vue**: Element Plus图标库
- **scss**: CSS预处理器
- **eslint**: 代码检查
- **prettier**: 代码格式化
- **vitest**: 单元测试

# 文件结构设计

```
/src
  /assets            # 静态资源
    /styles          # 全局样式
    /images          # 图片资源
  /components        # 通用组件
    /layout          # 布局组件
    /common          # 公共组件
  /composables       # 组合式API
  /router            # 路由配置
  /stores            # Pinia状态管理
  /services          # API服务
  /types             # TypeScript类型定义
  /utils             # 工具函数
  /views             # 页面组件
    /auth            # 认证相关页面
    /dashboard       # 首页
    /questionBank    # 题库管理页面
    /question        # 题目管理页面
    /questionBankQuestion # 题库题目关联页面
  App.vue            # 根组件
  main.ts            # 入口文件
/public              # 公共资源
/index.html          # HTML模板
/package.json        # 项目配置
/tsconfig.json       # TypeScript配置
/vite.config.ts      # Vite配置
```

# 数据结构设计

## 1. 用户相关

### 用户登录请求
```typescript
interface UserLoginRequest {
  userAccount: string;
  userPassword: string;
}
```

### 用户注册请求
```typescript
interface UserRegisterRequest {
  userAccount: string;
  userPassword: string;
  checkPassword: string;
}
```

### 登录用户信息
```typescript
interface LoginUserVO {
  id: number;
  userName: string;
  userAvatar: string;
  userProfile: string;
  userRole: string;
  createTime: string;
  updateTime: string;
}
```

## 2. 题库相关

### 题库创建请求
```typescript
interface QuestionBankAddRequest {
  name: string;
  description: string;
}
```

### 题库更新请求
```typescript
interface QuestionBankUpdateRequest {
  id: number;
  name: string;
  description: string;
}
```

### 题库查询请求
```typescript
interface QuestionBankQueryRequest {
  id?: number;
  userId?: number;
  name?: string;
  current: number;
  pageSize: number;
  needQueryQuestionList?: boolean;
}
```

### 题库信息
```typescript
interface QuestionBankVO {
  id: number;
  name: string;
  description: string;
  userId: number;
  createTime: string;
  updateTime: string;
  questionPage?: Page<QuestionVO>;
}
```

## 3. 题目相关

### 题目创建请求
```typescript
interface QuestionAddRequest {
  title: string;
  content: string;
  tags: string[];
  answer: string;
  difficulty: string;
  type: string;
  questionBankId?: number;
}
```

### 题目更新请求
```typescript
interface QuestionUpdateRequest {
  id: number;
  title: string;
  content: string;
  tags: string[];
  answer: string;
  difficulty: string;
  type: string;
  questionBankId?: number;
}
```

### 题目查询请求
```typescript
interface QuestionQueryRequest {
  id?: number;
  userId?: number;
  title?: string;
  tags?: string[];
  difficulty?: string;
  type?: string;
  questionBankId?: number;
  current: number;
  pageSize: number;
}
```

### 题目信息
```typescript
interface QuestionVO {
  id: number;
  title: string;
  content: string;
  tags: string[];
  answer: string;
  difficulty: string;
  type: string;
  userId: number;
  questionBankId?: number;
  createTime: string;
  updateTime: string;
}
```

## 4. 题库题目关联相关

### 关联创建请求
```typescript
interface QuestionBankQuestionAddRequest {
  questionBankId: number;
  questionId: number;
  sort: number;
}
```

### 批量添加请求
```typescript
interface QuestionBankQuestionBatchAddRequest {
  questionBankId: number;
  questionIdList: number[];
}
```

### 批量移除请求
```typescript
interface QuestionBankQuestionBatchRemoveRequest {
  questionBankId: number;
  questionIdList: number[];
}
```

### 关联信息
```typescript
interface QuestionBankQuestionVO {
  id: number;
  questionBankId: number;
  questionId: number;
  sort: number;
  userId: number;
  createTime: string;
  updateTime: string;
  questionBank?: QuestionBankVO;
  question?: QuestionVO;
}
```

## 5. 通用数据结构

### 分页响应
```typescript
interface Page<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}
```

### API响应
```typescript
interface BaseResponse<T> {
  code: number;
  message: string;
  data: T;
}
```

# 功能模块分解与技术实现

## 1. 认证模块

### 登录页面
- **组件**: `src/views/auth/Login.vue`
- **功能**: 
  - 用户名密码登录
  - 微信登录
  - 跳转到注册页面
- **技术实现**:
  - 使用 Element Plus 表单组件
  - 表单验证
  - 调用登录API
  - 登录成功后存储token到localStorage
  - 跳转到首页

### 注册页面
- **组件**: `src/views/auth/Register.vue`
- **功能**:
  - 用户注册
  - 跳转到登录页面
- **技术实现**:
  - 使用 Element Plus 表单组件
  - 表单验证
  - 调用注册API
  - 注册成功后跳转到登录页面

## 2. 首页模块

### 导航栏
- **组件**: `src/components/layout/Navbar.vue`
- **功能**:
  - 系统名称
  - 导航菜单
  - 用户信息
  - 退出登录
- **技术实现**:
  - 使用 Element Plus 导航组件
  - 根据用户角色显示不同的导航菜单
  - 退出登录时清除localStorage中的token

### 侧边栏
- **组件**: `src/components/layout/Sidebar.vue`
- **功能**:
  - 导航菜单
  - 折叠/展开功能
- **技术实现**:
  - 使用 Element Plus 菜单组件
  - 响应式设计，在移动端折叠为汉堡菜单

### 题库列表
- **组件**: `src/views/dashboard/QuestionBankList.vue`
- **功能**:
  - 展示题库列表
  - 分页
  - 搜索
- **技术实现**:
  - 使用 Element Plus 表格组件
  - 调用题库列表API
  - 分页处理
  - 搜索功能

### 题目列表
- **组件**: `src/views/dashboard/QuestionList.vue`
- **功能**:
  - 展示题目列表
  - 分页
  - 搜索
- **技术实现**:
  - 使用 Element Plus 表格组件
  - 调用题目列表API
  - 分页处理
  - 搜索功能

## 3. 题库管理模块

### 题库创建
- **组件**: `src/views/questionBank/QuestionBankForm.vue`
- **功能**:
  - 创建新题库
- **技术实现**:
  - 使用 Element Plus 表单组件
  - 表单验证
  - 调用创建题库API

### 题库编辑
- **组件**: `src/views/questionBank/QuestionBankForm.vue`
- **功能**:
  - 编辑现有题库
- **技术实现**:
  - 使用 Element Plus 表单组件
  - 表单验证
  - 调用更新题库API

### 题库删除
- **功能**:
  - 删除指定题库
- **技术实现**:
  - 使用 Element Plus 确认对话框
  - 调用删除题库API

### 题库详情
- **组件**: `src/views/questionBank/QuestionBankDetail.vue`
- **功能**:
  - 查看题库详细信息
  - 查看关联的题目列表
- **技术实现**:
  - 调用题库详情API
  - 展示题目列表

## 4. 题目管理模块

### 题目创建
- **组件**: `src/views/question/QuestionForm.vue`
- **功能**:
  - 创建新题目
- **技术实现**:
  - 使用 Element Plus 表单组件
  - 表单验证
  - 调用创建题目API

### 题目编辑
- **组件**: `src/views/question/QuestionForm.vue`
- **功能**:
  - 编辑现有题目
- **技术实现**:
  - 使用 Element Plus 表单组件
  - 表单验证
  - 调用更新题目API

### 题目删除
- **功能**:
  - 删除指定题目
- **技术实现**:
  - 使用 Element Plus 确认对话框
  - 调用删除题目API

### 题目详情
- **组件**: `src/views/question/QuestionDetail.vue`
- **功能**:
  - 查看题目详细信息
- **技术实现**:
  - 调用题目详情API

## 5. 题库题目关联模块

### 添加题目到题库
- **组件**: `src/views/questionBankQuestion/QuestionBankQuestionForm.vue`
- **功能**:
  - 将题目添加到题库
- **技术实现**:
  - 使用 Element Plus 表单组件
  - 表单验证
  - 调用添加关联API

### 从题库移除题目
- **功能**:
  - 将题目从题库中移除
- **技术实现**:
  - 使用 Element Plus 确认对话框
  - 调用移除关联API

### 批量操作
- **功能**:
  - 批量添加或移除题目
- **技术实现**:
  - 使用 Element Plus 多选框
  - 调用批量操作API

# 状态管理设计

## 用户状态
```typescript
// src/stores/user.ts
import { defineStore } from 'pinia';
import { login, register, logout, getCurrentUser } from '@/services/auth';
import type { LoginUserVO, UserLoginRequest, UserRegisterRequest } from '@/types';

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null as LoginUserVO | null,
    token: localStorage.getItem('token') || '',
    loading: false,
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.user?.userRole === 'admin',
  },
  actions: {
    async login(loginData: UserLoginRequest) {
      this.loading = true;
      try {
        const response = await login(loginData);
        this.token = response.data.token;
        this.user = response.data.user;
        localStorage.setItem('token', this.token);
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
      await logout();
      this.token = '';
      this.user = null;
      localStorage.removeItem('token');
    },
    async getCurrentUser() {
      try {
        const response = await getCurrentUser();
        this.user = response.data;
        return response;
      } catch (error) {
        this.token = '';
        this.user = null;
        localStorage.removeItem('token');
        throw error;
      }
    },
  },
});
```

## 题库状态
```typescript
// src/stores/questionBank.ts
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
        this.questionBanks = response.data.records;
        this.total = response.data.total;
        return response;
      } finally {
        this.loading = false;
      }
    },
    async getQuestionBankDetail(id: number) {
      this.loading = true;
      try {
        const response = await getQuestionBankDetail(id);
        this.currentQuestionBank = response.data;
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
```

## 题目状态
```typescript
// src/stores/question.ts
import { defineStore } from 'pinia';
import { 
  getQuestionList, 
  getQuestionDetail, 
  createQuestion, 
  updateQuestion, 
  deleteQuestion 
} from '@/services/question';
import type { 
  QuestionVO, 
  QuestionQueryRequest, 
  QuestionAddRequest, 
  QuestionUpdateRequest 
} from '@/types';

export const useQuestionStore = defineStore('question', {
  state: () => ({
    questions: [] as QuestionVO[],
    currentQuestion: null as QuestionVO | null,
    loading: false,
    total: 0,
  }),
  actions: {
    async getQuestionList(query: QuestionQueryRequest) {
      this.loading = true;
      try {
        const response = await getQuestionList(query);
        this.questions = response.data.records;
        this.total = response.data.total;
        return response;
      } finally {
        this.loading = false;
      }
    },
    async getQuestionDetail(id: number) {
      this.loading = true;
      try {
        const response = await getQuestionDetail(id);
        this.currentQuestion = response.data;
        return response;
      } finally {
        this.loading = false;
      }
    },
    async createQuestion(data: QuestionAddRequest) {
      this.loading = true;
      try {
        const response = await createQuestion(data);
        return response;
      } finally {
        this.loading = false;
      }
    },
    async updateQuestion(data: QuestionUpdateRequest) {
      this.loading = true;
      try {
        const response = await updateQuestion(data);
        return response;
      } finally {
        this.loading = false;
      }
    },
    async deleteQuestion(id: number) {
      this.loading = true;
      try {
        const response = await deleteQuestion(id);
        return response;
      } finally {
        this.loading = false;
      }
    },
  },
});
```

## 题库题目关联状态
```typescript
// src/stores/questionBankQuestion.ts
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
  QuestionBankQuestionBatchRemoveRequest 
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
        this.questionBankQuestions = response.data.records;
        this.total = response.data.total;
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
    async removeQuestionFromBank(questionBankId: number, questionId: number) {
      this.loading = true;
      try {
        const response = await removeQuestionFromBank(questionBankId, questionId);
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
```

# API服务设计

## 认证服务
```typescript
// src/services/auth.ts
import axios from '@/utils/axios';
import type { UserLoginRequest, UserRegisterRequest, LoginUserVO, BaseResponse } from '@/types';

export async function login(data: UserLoginRequest) {
  return axios.post<BaseResponse<{ user: LoginUserVO; token: string }>>('/user/login', data);
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
```

## 题库服务
```typescript
// src/services/questionBank.ts
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
```

## 题目服务
```typescript
// src/services/question.ts
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
```

## 题库题目关联服务
```typescript
// src/services/questionBankQuestion.ts
import axios from '@/utils/axios';
import type { 
  QuestionBankQuestionVO, 
  QuestionBankQuestionQueryRequest, 
  QuestionBankQuestionAddRequest, 
  QuestionBankQuestionBatchAddRequest, 
  QuestionBankQuestionBatchRemoveRequest, 
  BaseResponse, 
  Page 
} from '@/types';

export async function getQuestionBankQuestionList(query: QuestionBankQuestionQueryRequest) {
  return axios.post<BaseResponse<Page<QuestionBankQuestionVO>>>('/questionBankQuestion/list/page/vo', query);
}

export async function addQuestionToBank(data: QuestionBankQuestionAddRequest) {
  return axios.post<BaseResponse<number>>('/questionBankQuestion/add', data);
}

export async function removeQuestionFromBank(questionBankId: number, questionId: number) {
  return axios.post<BaseResponse<boolean>>('/questionBankQuestion/remove', {
    questionBankId,
    questionId
  });
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
```

# 路由设计

```typescript
// src/router/index.ts
import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import { useUserStore } from '@/stores/user';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/dashboard',
  },
  {
    path: '/auth',
    component: () => import('@/views/auth/AuthLayout.vue'),
    children: [
      {
        path: 'login',
        name: 'Login',
        component: () => import('@/views/auth/Login.vue'),
        meta: { title: '登录' },
      },
      {
        path: 'register',
        name: 'Register',
        component: () => import('@/views/auth/Register.vue'),
        meta: { title: '注册' },
      },
    ],
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/dashboard/Dashboard.vue'),
    meta: { title: '首页', requiresAuth: true },
  },
  {
    path: '/questionBank',
    name: 'QuestionBank',
    component: () => import('@/views/questionBank/QuestionBankLayout.vue'),
    meta: { title: '题库管理', requiresAuth: true },
    children: [
      {
        path: 'list',
        name: 'QuestionBankList',
        component: () => import('@/views/questionBank/QuestionBankList.vue'),
        meta: { title: '题库列表' },
      },
      {
        path: 'add',
        name: 'QuestionBankAdd',
        component: () => import('@/views/questionBank/QuestionBankForm.vue'),
        meta: { title: '创建题库', requiresAdmin: true },
      },
      {
        path: 'edit/:id',
        name: 'QuestionBankEdit',
        component: () => import('@/views/questionBank/QuestionBankForm.vue'),
        meta: { title: '编辑题库', requiresAdmin: true },
      },
      {
        path: 'detail/:id',
        name: 'QuestionBankDetail',
        component: () => import('@/views/questionBank/QuestionBankDetail.vue'),
        meta: { title: '题库详情' },
      },
    ],
  },
  {
    path: '/question',
    name: 'Question',
    component: () => import('@/views/question/QuestionLayout.vue'),
    meta: { title: '题目管理', requiresAuth: true },
    children: [
      {
        path: 'list',
        name: 'QuestionList',
        component: () => import('@/views/question/QuestionList.vue'),
        meta: { title: '题目列表' },
      },
      {
        path: 'add',
        name: 'QuestionAdd',
        component: () => import('@/views/question/QuestionForm.vue'),
        meta: { title: '创建题目', requiresAdmin: true },
      },
      {
        path: 'edit/:id',
        name: 'QuestionEdit',
        component: () => import('@/views/question/QuestionForm.vue'),
        meta: { title: '编辑题目', requiresAdmin: true },
      },
      {
        path: 'detail/:id',
        name: 'QuestionDetail',
        component: () => import('@/views/question/QuestionDetail.vue'),
        meta: { title: '题目详情' },
      },
    ],
  },
  {
    path: '/questionBankQuestion',
    name: 'QuestionBankQuestion',
    component: () => import('@/views/questionBankQuestion/QuestionBankQuestionLayout.vue'),
    meta: { title: '题库题目关联', requiresAuth: true },
    children: [
      {
        path: 'list',
        name: 'QuestionBankQuestionList',
        component: () => import('@/views/questionBankQuestion/QuestionBankQuestionList.vue'),
        meta: { title: '关联列表' },
      },
      {
        path: 'add',
        name: 'QuestionBankQuestionAdd',
        component: () => import('@/views/questionBankQuestion/QuestionBankQuestionForm.vue'),
        meta: { title: '添加关联', requiresAdmin: true },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '404' },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();
  
  // 设置页面标题
  document.title = `${to.meta.title || '题库管理系统'} - 管理平台`;
  
  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    if (!userStore.isLoggedIn) {
      // 未登录，跳转到登录页
      next({ name: 'Login', query: { redirect: to.fullPath } });
    } else {
      // 已登录，检查是否是管理员
      if (to.meta.requiresAdmin && !userStore.isAdmin) {
        // 不是管理员，跳转到首页
        next({ name: 'Dashboard' });
      } else {
        // 是管理员，继续访问
        next();
      }
    }
  } else {
    // 不需要认证，直接访问
    next();
  }
});

export default router;
```

# 工具函数设计

## Axios 配置
```typescript
// src/utils/axios.ts
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/user';

const axiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
axiosInstance.interceptors.request.use(
  (config) => {
    const userStore = useUserStore();
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`;
    }
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
```

## 本地存储工具
```typescript
// src/utils/storage.ts
export const storage = {
  set(key: string, value: any) {
    localStorage.setItem(key, JSON.stringify(value));
  },
  get(key: string) {
    const value = localStorage.getItem(key);
    return value ? JSON.parse(value) : null;
  },
  remove(key: string) {
    localStorage.removeItem(key);
  },
  clear() {
    localStorage.clear();
  },
};
```

## 时间格式化工具
```typescript
// src/utils/date.ts
export function formatDate(date: Date | string, format: string = 'YYYY-MM-DD HH:mm:ss') {
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  const seconds = String(d.getSeconds()).padStart(2, '0');
  
  return format
    .replace('YYYY', String(year))
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds);
}
```

# 样式设计

## 全局样式
```scss
// src/assets/styles/main.scss
:root {
  --primary-color: #3498db;
  --secondary-color: #2ecc71;
  --text-color: #333;
  --bg-color: #f5f5f5;
  --border-color: #e0e0e0;
  --success-color: #67c23a;
  --warning-color: #e6a23c;
  --danger-color: #f56c6c;
  --info-color: #909399;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 14px;
  line-height: 1.5;
  color: var(--text-color);
  background-color: var(--bg-color);
}

.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.btn {
  display: inline-block;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
  outline: none;
  
  &:hover {
    opacity: 0.8;
  }
  
  &.btn-primary {
    background-color: var(--primary-color);
    color: #fff;
  }
  
  &.btn-secondary {
    background-color: var(--secondary-color);
    color: #fff;
  }
  
  &.btn-danger {
    background-color: var(--danger-color);
    color: #fff;
  }
  
  &.btn-outline {
    background-color: transparent;
    border: 1px solid var(--border-color);
    color: var(--text-color);
    
    &:hover {
      background-color: var(--bg-color);
    }
  }
}

.form-item {
  margin-bottom: 16px;
  
  label {
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
  }
  
  .el-input,
  .el-select,
  .el-textarea {
    width: 100%;
  }
}

.table {
  width: 100%;
  border-collapse: collapse;
  
  th,
  td {
    padding: 12px;
    text-align: left;
    border-bottom: 1px solid var(--border-color);
  }
  
  th {
    background-color: #f9f9f9;
    font-weight: 600;
  }
  
  tr:hover {
    background-color: #f5f7fa;
  }
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.breadcrumb {
  margin-bottom: 20px;
}

// 响应式设计
@media (max-width: 768px) {
  .container {
    padding: 0 10px;
  }
  
  .card {
    padding: 15px;
  }
  
  .table {
    th,
    td {
      padding: 8px;
      font-size: 12px;
    }
  }
}
```

# 测试策略

## 单元测试
- **测试框架**: Vitest
- **测试文件位置**: `src/__tests__`
- **测试覆盖率**: 目标 80% 以上

## 测试用例

### 认证模块
- 登录功能测试
- 注册功能测试
- 退出登录功能测试
- 获取当前用户信息测试

### 题库模块
- 题库列表获取测试
- 题库详情获取测试
- 题库创建测试
- 题库更新测试
- 题库删除测试

### 题目模块
- 题目列表获取测试
- 题目详情获取测试
- 题目创建测试
- 题目更新测试
- 题目删除测试

### 题库题目关联模块
- 关联列表获取测试
- 添加题目到题库测试
- 从题库移除题目测试
- 批量添加题目测试
- 批量移除题目测试

# 部署策略

## 构建
- 使用 Vite 构建生产版本
- 构建命令: `npm run build`
- 构建产物: `dist` 目录

## 部署
- 可以部署到任何静态文件服务器
- 推荐部署到 Nginx、Apache 等服务器
- 也可以部署到云服务提供商的静态网站托管服务

## 环境变量
- 开发环境: `.env.development`
- 生产环境: `.env.production`

## CI/CD
- 推荐使用 GitHub Actions 或 GitLab CI 进行持续集成和部署
- 每次代码提交时自动运行测试
- 合并到 master 分支时自动构建和部署

# 代码规范

## ESLint 配置
```javascript
// .eslintrc.js
module.exports = {
  root: true,
  env: {
    node: true,
  },
  extends: [
    'plugin:vue/vue3-recommended',
    '@vue/eslint-config-typescript',
    '@vue/eslint-config-prettier',
  ],
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
  },
};
```

## Prettier 配置
```javascript
// .prettierrc.js
module.exports = {
  semi: true,
  trailingComma: 'es5',
  singleQuote: true,
  printWidth: 80,
  tabWidth: 2,
};
```

# 性能优化

## 代码分割
- 使用 Vue Router 的动态导入功能
- 对大型第三方库进行按需导入

## 缓存策略
- 利用浏览器缓存
- 对静态资源使用哈希命名

## 懒加载
- 图片懒加载
- 组件懒加载

## 其他优化
- 使用 CDN 加速静态资源
- 优化 API 请求，减少不必要的请求
- 使用防抖和节流优化用户输入
- 优化渲染性能，减少不必要的重渲染

# 安全性

## 前端安全
- 防止 XSS 攻击
- 防止 CSRF 攻击
- 安全存储用户 token
- 输入验证

## 后端安全
- API 接口认证
- 权限控制
- 数据验证
- 防止 SQL 注入

# 监控与日志

## 前端监控
- 错误监控
- 性能监控
- 用户行为分析

## 日志
- 前端错误日志
- API 请求日志
- 用户操作日志

# 总结

本技术架构文档基于 Vue 3 + TypeScript + Element Plus 技术栈，设计了一个完整的题库管理系统前端方案。该方案包括用户认证、题库管理、题目管理和题库题目关联管理等核心功能，采用了现代化的前端开发实践，如组件化、状态管理、路由管理等。

通过本方案的实施，可以构建一个功能完整、性能优异、用户体验良好的题库管理系统前端应用，满足用户对题库和题目进行高效管理的需求。