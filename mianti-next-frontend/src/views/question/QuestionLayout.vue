<template>
  <MainLayout>
    <div class="question-layout">
      <el-breadcrumb :separator="'/'" class="breadcrumb">
        <el-breadcrumb-item :to="'/dashboard'">首页</el-breadcrumb-item>
        <el-breadcrumb-item>题目管理</el-breadcrumb-item>
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
  if (path.includes('/list')) return '题目列表';
  if (path.includes('/add')) return '创建题目';
  if (path.includes('/edit')) return '编辑题目';
  if (path.includes('/detail')) return '题目详情';
  return '题目管理';
});
</script>

<style scoped>
.question-layout {
  max-width: 1200px;
  margin: 0 auto;
}

.breadcrumb {
  margin-bottom: 20px;
}
</style>
