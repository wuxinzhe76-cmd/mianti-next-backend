<template>
  <div class="auth-layout">
    <div class="auth-container">
      <div class="auth-header">
        <h1>题库管理系统</h1>
        <p>欢迎登录/注册</p>
      </div>
      <div class="auth-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 认证布局组件
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-color);
}

.auth-container {
  width: 100%;
  max-width: 400px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.auth-header {
  text-align: center;
  margin-bottom: 30px;
  
  h1 {
    font-size: 24px;
    font-weight: 600;
    color: var(--primary-color);
    margin-bottom: 10px;
  }
  
  p {
    font-size: 14px;
    color: var(--text-color);
  }
}

.auth-content {
  /* 子组件内容 */
}
</style>
