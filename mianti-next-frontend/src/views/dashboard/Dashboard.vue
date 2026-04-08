<template>
  <MainLayout>
    <div class="dashboard">
      <h1>欢迎使用题库管理系统</h1>
      <div class="dashboard-cards">
        <el-card class="dashboard-card">
          <template #header>
            <div class="card-header">
              <span>题库统计</span>
            </div>
          </template>
          <div class="card-content">
            <el-statistic :value="questionBankCount" title="题库数量" />
          </div>
        </el-card>
        <el-card class="dashboard-card">
          <template #header>
            <div class="card-header">
              <span>题目统计</span>
            </div>
          </template>
          <div class="card-content">
            <el-statistic :value="questionCount" title="题目数量" />
          </div>
        </el-card>
        <el-card class="dashboard-card">
          <template #header>
            <div class="card-header">
              <span>用户信息</span>
            </div>
          </template>
          <div class="card-content">
            <div class="user-info">
              <el-avatar :size="48" :src="user?.userAvatar || ''">
                {{ user?.userName?.charAt(0) || 'U' }}
              </el-avatar>
              <div class="user-details">
                <p class="user-name">{{ userStore.displayLabel }}</p>
                <p class="user-role">{{ user?.userRole === 'admin' ? '管理员' : '普通用户' }}</p>
              </div>
            </div>
          </div>
        </el-card>
      </div>
      <div class="dashboard-lists">
        <el-card class="dashboard-list">
          <template #header>
            <div class="card-header">
              <span>最近创建的题库</span>
              <el-button 
                type="primary" 
                size="small" 
                @click="goToQuestionBankList"
                v-if="isAdmin"
              >
                查看全部
              </el-button>
            </div>
          </template>
          <el-table :data="recentQuestionBanks" style="width: 100%">
            <el-table-column prop="name" label="题库名称" />
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button 
                  size="small" 
                  @click="goToQuestionBankDetail(scope.row.id)"
                >
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
        <el-card class="dashboard-list">
          <template #header>
            <div class="card-header">
              <span>最近创建的题目</span>
              <el-button 
                type="primary" 
                size="small" 
                @click="goToQuestionList"
                v-if="isAdmin"
              >
                查看全部
              </el-button>
            </div>
          </template>
          <el-table :data="recentQuestions" style="width: 100%">
            <el-table-column prop="title" label="题目标题" />
            <el-table-column prop="type" label="类型" />
            <el-table-column prop="difficulty" label="难度" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button 
                  size="small" 
                  @click="goToQuestionDetail(scope.row.id)"
                >
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import MainLayout from '@/components/layout/MainLayout.vue';
import { useUserStore } from '@/stores/user';
import { useQuestionBankStore } from '@/stores/questionBank';
import { useQuestionStore } from '@/stores/question';
import type { QuestionBankVO, QuestionVO } from '@/types';

const router = useRouter();
const userStore = useUserStore();
const questionBankStore = useQuestionBankStore();
const questionStore = useQuestionStore();

const user = computed(() => userStore.user);
const isAdmin = computed(() => userStore.isAdmin);

const questionBankCount = ref(0);
const questionCount = ref(0);
const recentQuestionBanks = ref<QuestionBankVO[]>([]);
const recentQuestions = ref<QuestionVO[]>([]);

const goToQuestionBankList = () => {
  router.push('/questionBank/list');
};

const goToQuestionBankDetail = (id: number) => {
  router.push(`/questionBank/detail/${id}`);
};

const goToQuestionList = () => {
  router.push('/question/list');
};

const goToQuestionDetail = (id: number) => {
  router.push(`/question/detail/${id}`);
};

const loadData = async () => {
  try {
    // 加载题库列表
    await questionBankStore.getQuestionBankList({
      current: 1,
      pageSize: 5
    });
    questionBankCount.value = questionBankStore.total;
    recentQuestionBanks.value = questionBankStore.questionBanks;
    
    // 加载题目列表
    await questionStore.getQuestionList({
      current: 1,
      pageSize: 5
    });
    questionCount.value = questionStore.total;
    recentQuestions.value = questionStore.questions;
  } catch (error) {
    console.error('加载数据失败:', error);
  }
};

onMounted(async () => {
  // 确保用户信息已加载
  if (!userStore.user) {
    try {
      await userStore.getCurrentUser();
    } catch (error) {
      console.error('获取用户信息失败:', error);
    }
  }
  
  // 加载数据
  await loadData();
});
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard h1 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 20px;
  color: var(--text-color);
}

.dashboard-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.dashboard-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  padding: 20px 0;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-details {
  margin-left: 16px;
}

.user-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 4px;
}

.user-role {
  font-size: 14px;
  color: var(--info-color);
}

.dashboard-lists {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 20px;
}

.dashboard-list {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .dashboard-lists {
    grid-template-columns: 1fr;
  }
}
</style>
