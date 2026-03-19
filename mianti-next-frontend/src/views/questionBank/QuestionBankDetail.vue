<template>
  <div class="question-bank-detail">
    <div class="detail-container" v-loading="loading">
      <div class="bank-header">
        <div class="bank-info">
          <div class="bank-thumbnail" v-if="questionBank?.picture">
            <img :src="questionBank.picture" :alt="questionBank.title" />
          </div>
          <div class="bank-thumbnail bank-thumbnail-placeholder" v-else>
            <el-icon class="bank-icon"><Collection /></el-icon>
          </div>
          <div class="bank-meta">
            <h1 class="bank-title">{{ questionBank?.title }}</h1>
            <p class="bank-description">{{ questionBank?.description }}</p>
            <div class="bank-stats">
              <span class="stat-item">
                <el-icon><Document /></el-icon>
                {{ questionBank?.questionPage?.total || 0 }} 道题目
              </span>
              <span class="stat-item">
                <el-icon><Timer /></el-icon>
                创建于 {{ questionBank?.createTime }}
              </span>
            </div>
          </div>
        </div>
        <div class="bank-actions">
          <el-button type="primary" @click="goToEdit" v-if="isAdmin">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>
      </div>

      <div class="questions-section" v-if="questionBank?.questionPage">
        <div class="section-header">
          <h2>题目列表</h2>
          <div class="question-count">
            共 {{ questionBank.questionPage.total }} 道题目
          </div>
        </div>
        
        <el-table :data="questionBank.questionPage.records" style="width: 100%" class="questions-table">
          <el-table-column prop="title" label="题目" min-width="400">
            <template #default="scope">
              <router-link :to="`/question/detail/${scope.row.id}`" class="question-title">
                {{ scope.row.title }}
              </router-link>
            </template>
          </el-table-column>
          <el-table-column prop="difficulty" label="难度" width="100">
            <template #default="scope">
              <el-tag :type="getDifficultyType(scope.row.difficulty)">
                {{ getDifficultyLabel(scope.row.difficulty) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="tagList" label="标签" min-width="200">
            <template #default="scope">
              <el-tag 
                v-for="tag in scope.row.tagList" 
                :key="tag" 
                size="small"
                class="tag-item"
              >
                {{ tag }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
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
        
        <div class="pagination" v-if="questionBank.questionPage.total > 0">
          <el-pagination
            v-model:current-page="questionPage.current"
            v-model:page-size="questionPage.pageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="questionBank.questionPage.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useQuestionBankStore } from '@/stores/questionBank';
import { useUserStore } from '@/stores/user';
import type { QuestionBankVO } from '@/types';

const router = useRouter();
const route = useRoute();
const questionBankStore = useQuestionBankStore();
const userStore = useUserStore();

const loading = ref(false);
const questionBank = ref<QuestionBankVO | null>(null);
const questionPage = ref({
  current: 1,
  pageSize: 10
});

const isAdmin = computed(() => {
  return userStore.isAdmin;
});

const getDifficultyLabel = (difficulty: string | undefined) => {
  if (!difficulty) return '';
  const difficultyMap: Record<string, string> = {
    'easy': '简单',
    'medium': '中等',
    'hard': '困难'
  };
  return difficultyMap[difficulty] || difficulty;
};

const getDifficultyType = (difficulty: string | undefined) => {
  if (!difficulty) return 'info';
  const typeMap: Record<string, string> = {
    'easy': 'success',
    'medium': 'warning',
    'hard': 'danger'
  };
  return typeMap[difficulty] || 'info';
};

const goToEdit = () => {
  if (questionBank.value) {
    router.push(`/questionBank/edit/${questionBank.value.id}`);
  }
};

const goToQuestionDetail = (id: number) => {
  router.push(`/question/detail/${id}`);
};

const goBack = () => {
  router.back();
};

const handleSizeChange = (size: number) => {
  questionPage.value.pageSize = size;
  loadQuestionBankDetail();
};

const handleCurrentChange = (current: number) => {
  questionPage.value.current = current;
  loadQuestionBankDetail();
};

const loadQuestionBankDetail = async () => {
  loading.value = true;
  try {
    const id = parseInt(route.params.id as string);
    const response = await questionBankStore.getQuestionBankDetail(id, true);
    questionBank.value = response.data.data;
  } catch (error) {
    console.error('加载题库详情失败:', error);
    ElMessage.error('加载题库详情失败');
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadQuestionBankDetail();
});
</script>

<style scoped>
.question-bank-detail {
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
}

.detail-container {
  background: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.bank-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.bank-info {
  display: flex;
  gap: 20px;
  flex: 1;
}

.bank-thumbnail {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.bank-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.bank-thumbnail-placeholder {
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bank-icon {
  font-size: 48px;
  color: #999;
}

.bank-meta {
  flex: 1;
}

.bank-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.bank-description {
  font-size: 16px;
  line-height: 1.5;
  color: #666;
  margin-bottom: 15px;
  max-width: 600px;
}

.bank-stats {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #999;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.bank-actions {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

.questions-section {
  margin-top: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.question-count {
  font-size: 14px;
  color: #999;
}

.questions-table {
  margin-bottom: 20px;
}

.question-title {
  color: #3498db;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.question-title:hover {
  color: #2980b9;
  text-decoration: underline;
}

.tag-item {
  margin-right: 5px;
  margin-bottom: 5px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .detail-container {
    padding: 20px;
  }
  
  .bank-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .bank-info {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .bank-thumbnail {
    width: 100px;
    height: 100px;
  }
  
  .bank-actions {
    width: 100%;
    justify-content: center;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
