<template>
  <MainLayout>
    <div class="interview-layout">
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="'/dashboard'">首页</el-breadcrumb-item>
        <el-breadcrumb-item>AI 面试</el-breadcrumb-item>
        <el-breadcrumb-item>{{ currentPage }}</el-breadcrumb-item>
      </el-breadcrumb>
      <router-view />
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import MainLayout from '@/components/layout/MainLayout.vue';

const route = useRoute();

const currentPage = computed(() => {
  const path = route.path;
  if (path.includes('/setup')) return '面试设置';
  if (path.includes('/session')) return '面试进行中';
  if (path.includes('/report')) return '面试报告';
  return 'AI 面试';
});
</script>

<style scoped>
.interview-layout {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.breadcrumb {
  margin-bottom: 20px;
}
</style>
