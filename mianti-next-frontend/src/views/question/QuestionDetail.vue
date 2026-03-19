<template>
  <div class="question-detail">
    <div class="detail-container" v-loading="loading">
      <div class="detail-header">
        <h1 class="question-title">{{ question?.title }}</h1>
        <div class="question-meta">
          <el-tag :type="getDifficultyType(question?.difficulty)">
            {{ getDifficultyLabel(question?.difficulty) }}
          </el-tag>
          <div class="tags">
            <el-tag 
              v-for="tag in question?.tagList" 
              :key="tag" 
              size="small"
              class="tag-item"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </div>

      <div class="question-content">
        <h2>题目内容</h2>
        <div class="content-body">{{ question?.content }}</div>
      </div>

      <div class="answer-section">
        <div class="answer-header" @click="toggleAnswer">
          <h2>回答重点</h2>
          <el-button type="primary" size="small">
            {{ showAnswer ? '隐藏答案' : '查看答案' }}
          </el-button>
        </div>
        <div class="answer-content" v-if="showAnswer">
          <div v-html="formattedAnswer"></div>
        </div>
      </div>

      <div class="question-info">
        <div class="info-item">
          <span class="info-label">所属题库：</span>
          <router-link :to="`/questionBank/detail/${question?.questionBankId}`" v-if="question?.questionBankId && questionBankName">
            {{ questionBankName }}
          </router-link>
          <span v-else>未分配题库</span>
        </div>
        <div class="info-item">
          <span class="info-label">创建时间：</span>
          <span>{{ question?.createTime }}</span>
        </div>
      </div>

      <div class="action-buttons">
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useQuestionStore } from '@/stores/question';
import { useQuestionBankStore } from '@/stores/questionBank';
import { useUserStore } from '@/stores/user';
import type { QuestionVO, QuestionBankVO } from '@/types';

const router = useRouter();
const route = useRoute();
const questionStore = useQuestionStore();
const questionBankStore = useQuestionBankStore();
const userStore = useUserStore();

const loading = ref(false);
const question = ref<QuestionVO | null>(null);
const questionBank = ref<QuestionBankVO | null>(null);
const showAnswer = ref(false);

const questionBankName = computed(() => {
  return questionBank.value?.title || '未分配题库';
});

const isAdmin = computed(() => {
  return userStore.isAdmin;
});

const formattedAnswer = computed(() => {
  if (!question.value?.answer) return '';
  // 简单的答案格式化，将换行符转换为 <br>
  return question.value.answer.replace(/\n/g, '<br>');
});

const getTypeLabel = (type: string | undefined) => {
  if (!type) return '';
  const typeMap: Record<string, string> = {
    'single': '单选题',
    'multiple': '多选题',
    'judgment': '判断题',
    'essay': '简答题'
  };
  return typeMap[type] || type;
};

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
  if (question.value) {
    router.push(`/question/edit/${question.value.id}`);
  }
};

const goBack = () => {
  router.back();
};

const toggleAnswer = () => {
  showAnswer.value = !showAnswer.value;
};

const loadQuestionDetail = async () => {
  loading.value = true;
  try {
    const id = parseInt(route.params.id as string);
    const response = await questionStore.getQuestionDetail(id);
    question.value = response.data.data;
    
    // 加载所属题库信息
    if (question.value.questionBankId) {
      const bankResponse = await questionBankStore.getQuestionBankDetail(question.value.questionBankId);
      questionBank.value = bankResponse.data.data;
    }
  } catch (error) {
    console.error('加载题目详情失败:', error);
    ElMessage.error('加载题目详情失败');
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadQuestionDetail();
});
</script>

<style scoped>
.question-detail {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
}

.detail-container {
  background: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.detail-header {
  margin-bottom: 30px;
}

.question-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
  line-height: 1.4;
}

.question-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  margin-bottom: 5px;
}

.question-content {
  margin-bottom: 30px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
}

.question-content h2 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
}

.content-body {
  font-size: 16px;
  line-height: 1.6;
  color: #555;
}

.answer-section {
  margin-bottom: 30px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

.answer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f5f7fa;
  cursor: pointer;
  transition: background-color 0.3s;
}

.answer-header:hover {
  background: #edf1f7;
}

.answer-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.answer-content {
  padding: 20px;
  background: white;
  border-top: 1px solid #e4e7ed;
}

.answer-content div {
  font-size: 16px;
  line-height: 1.6;
  color: #333;
  white-space: pre-wrap;
}

.question-info {
  margin-bottom: 30px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
}

.info-item {
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
}

.info-label {
  font-weight: 500;
  margin-right: 10px;
}

.action-buttons {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .detail-container {
    padding: 20px;
  }
  
  .question-title {
    font-size: 20px;
  }
  
  .question-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>
