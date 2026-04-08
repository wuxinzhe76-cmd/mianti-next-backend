import { createRouter, createWebHistory, type RouteRecord } from 'vue-router';
import { useUserStore } from '@/stores/user';

const routes: RouteRecord[] = [
  {
    path: '/',
    redirect: '/home',
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/home/HomePage.vue'),
    meta: { title: '首页' },
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
    path: '/question/practice-list',
    name: 'PracticeList',
    component: () => import('@/views/practice/PracticeList.vue'),
    meta: { title: '在线练习', requiresAuth: true },
  },
  {
    path: '/practice/:id',
    name: 'QuestionPractice',
    component: () => import('@/views/practice/QuestionPractice.vue'),
    meta: { title: '在线练习', requiresAuth: true },
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

router.beforeEach(async (to) => {
  const userStore = useUserStore();
  
  // 设置页面标题
  document.title = `${to.meta.title || '题库管理系统'} - 管理平台`;
  
  if ((to.name === 'Login' || to.name === 'Register') && userStore.isLoggedIn) {
    return { name: 'Dashboard' };
  }

  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    if (!userStore.isLoggedIn) {
      // 未登录，跳转到登录页
      return { name: 'Login', query: { redirect: to.fullPath } };
    } else {
      // 已登录，检查是否是管理员
      if (to.meta.requiresAdmin && !userStore.isAdmin) {
        // 不是管理员，跳转到首页
        return { name: 'Dashboard' };
      }
    }
  }
  // 允许访问
  return true;
});

export default router;
