<template>
  <div class="login-form">
    <el-form
      ref="loginFormRef"
      :model="loginForm"
      :rules="loginRules"
      label-width="80px"
    >
      <el-form-item label="账号" prop="userAccount">
        <el-input
          v-model="loginForm.userAccount"
          placeholder="请输入账号"
          prefix-icon="User"
        />
      </el-form-item>
      <el-form-item label="密码" prop="userPassword">
        <el-input
          v-model="loginForm.userPassword"
          type="password"
          placeholder="请输入密码"
          prefix-icon="Lock"
          show-password
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          class="login-btn"
          @click="handleLogin"
          :loading="loading"
        >
          登录
        </el-button>
        <el-button
          class="register-btn"
          @click="goToRegister"
        >
          注册
        </el-button>
      </el-form-item>
      <el-divider>其他登录方式</el-divider>
      <el-form-item>
        <el-button
          type="primary"
          class="wx-login-btn"
          @click="handleWxLogin"
        >
          <el-icon><ChatDotRound /></el-icon>
          微信登录
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/user';
import type { UserLoginRequest } from '@/types';

const router = useRouter();
const userStore = useUserStore();
const loginFormRef = ref();
const loading = ref(false);

const loginForm = reactive<UserLoginRequest>({
  userAccount: '',
  userPassword: ''
});

const loginRules = {
  userAccount: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  userPassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
};

const handleLogin = async () => {
  if (!loginFormRef.value) return;
  
  try {
    await loginFormRef.value.validate();
    loading.value = true;
    
    const response = await userStore.login(loginForm);
    ElMessage.success('登录成功');
    
    // 跳转到首页或重定向页面
    const redirect = router.currentRoute.value.query.redirect as string;
    if (redirect) {
      router.push(redirect);
    } else {
      router.push('/dashboard');
    }
  } catch (error: any) {
    ElMessage.error(error.message || '登录失败');
  } finally {
    loading.value = false;
  }
};

const goToRegister = () => {
  router.push('/auth/register');
};

const handleWxLogin = () => {
  // 微信登录逻辑
  ElMessage.info('微信登录功能开发中');
};
</script>

<style scoped>
.login-form {
  width: 100%;
}

.login-btn {
  width: 100%;
  margin-bottom: 10px;
}

.register-btn {
  width: 100%;
}

.wx-login-btn {
  width: 100%;
}
</style>
