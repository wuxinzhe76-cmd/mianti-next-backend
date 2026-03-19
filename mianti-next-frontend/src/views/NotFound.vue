<template>
  <div class="not-found">
    <div class="not-found-content">
      <h1>404</h1>
      <h2>页面不存在</h2>
      <p>抱歉，您访问的页面不存在或已被删除</p>
      <el-button type="primary" @click="goToHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';

const router = useRouter();

const goToHome = () => {
  router.push('/dashboard');
};
</script>

<style scoped>
.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-color);
}

.not-found-content {
  text-align: center;
  padding: 40px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.not-found-content h1 {
  font-size: 72px;
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: 20px;
}

.not-found-content h2 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 16px;
}

.not-found-content p {
  font-size: 16px;
  color: var(--info-color);
  margin-bottom: 32px;
}
</style>
