<template>
  <el-header class="navbar">
    <div class="navbar-left">
      <el-menu
          :default-active="activeIndex"
          class="el-menu-demo"
          mode="horizontal"
          background-color="#3498db"
          text-color="#fff"
          active-text-color="#fff"
          @select="handleSelect"
        >
          <el-menu-item index="/home">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/dashboard">
            <el-icon><DataPanel /></el-icon>
            <span>管理首页</span>
          </el-menu-item>
        <el-menu-item index="/questionBank/list">
          <el-icon><Collection /></el-icon>
          <span>题库</span>
        </el-menu-item>
        <el-menu-item index="/question/list">
          <el-icon><Document /></el-icon>
          <span>题目</span>
        </el-menu-item>
        <el-menu-item index="/question/practice-list">
          <el-icon><Monitor /></el-icon>
          <span>在线练习</span>
        </el-menu-item>
        <el-menu-item index="/questionBankQuestion/list" v-if="isAdmin">
          <el-icon><Link /></el-icon>
          <span>题库题目关联</span>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="navbar-right">
      <el-dropdown>
        <span class="user-info">
          <el-avatar :size="32" :src="user?.userAvatar || ''">
            {{ user?.userName?.charAt(0) || 'U' }}
          </el-avatar>
          <span class="user-name">{{ userStore.displayLabel }}</span>
          <el-icon class="el-icon--right"><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const user = computed(() => userStore.user);
const isAdmin = computed(() => userStore.isAdmin);

const activeIndex = computed(() => {
  return route.path;
});

const handleSelect = (key: string) => {
  router.push(key);
};

const handleLogout = async () => {
  try {
    await userStore.logout();
    ElMessage.success('退出登录成功');
    router.push('/auth/login');
  } catch (error: any) {
    ElMessage.error(error.message || '退出登录失败');
  }
};
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background-color: #3498db;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.navbar-left {
  flex: 1;
}

.navbar-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #fff;
  font-size: 14px;
}

.user-name {
  margin: 0 10px;
}

.el-menu-demo {
  border-bottom: none;
}

.el-menu-item {
  height: 60px;
  line-height: 60px;
  font-size: 14px;
}
</style>
