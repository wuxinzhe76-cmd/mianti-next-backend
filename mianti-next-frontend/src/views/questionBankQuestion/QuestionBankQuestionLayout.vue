<template>
  <MainLayout>
    <div class="question-bank-question-layout">
      <router-view />
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import MainLayout from '@/components/layout/MainLayout.vue';
</script>

<style scoped>
.question-bank-question-layout {
  width: 100%;
}
</style>
