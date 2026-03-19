<template>
  <div class="main-layout">
    <Navbar />
    <el-main class="main-content">
      <slot>
        <router-view />
      </slot>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import Navbar from './Navbar.vue';
</script>

<style scoped>
.main-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: var(--bg-color);
}
</style>
