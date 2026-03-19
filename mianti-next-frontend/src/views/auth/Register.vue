<template>
  <div class="register-form">
    <el-form
      ref="registerFormRef"
      :model="registerForm"
      :rules="registerRules"
      label-width="80px"
    >
      <el-form-item label="账号" prop="userAccount">
        <el-input
          v-model="registerForm.userAccount"
          placeholder="请输入账号"
          prefix-icon="User"
        />
      </el-form-item>
      <el-form-item label="密码" prop="userPassword">
        <el-input
          v-model="registerForm.userPassword"
          type="password"
          placeholder="请输入密码"
          prefix-icon="Lock"
          show-password
        />
      </el-form-item>
      <el-form-item label="确认密码" prop="checkPassword">
        <el-input
          v-model="registerForm.checkPassword"
          type="password"
          placeholder="请确认密码"
          prefix-icon="Check"
          show-password
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          class="register-btn"
          @click="handleRegister"
          :loading="loading"
        >
          注册
        </el-button>
        <el-button
          class="login-btn"
          @click="goToLogin"
        >
          登录
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
import type { UserRegisterRequest } from '@/types';

const router = useRouter();
const userStore = useUserStore();
const registerFormRef = ref();
const loading = ref(false);

const registerForm = reactive<UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: ''
});

const registerRules = {
  userAccount: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  userPassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  checkPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== registerForm.userPassword) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

const handleRegister = async () => {
  if (!registerFormRef.value) return;
  
  try {
    await registerFormRef.value.validate();
    loading.value = true;
    
    await userStore.register(registerForm);
    ElMessage.success('注册成功');
    
    // 注册成功后跳转到登录页面
    router.push('/auth/login');
  } catch (error: any) {
    ElMessage.error(error.message || '注册失败');
  } finally {
    loading.value = false;
  }
};

const goToLogin = () => {
  router.push('/auth/login');
};
</script>

<style scoped>
.register-form {
  width: 100%;
}

.register-btn {
  width: 100%;
  margin-bottom: 10px;
}

.login-btn {
  width: 100%;
}
</style>
