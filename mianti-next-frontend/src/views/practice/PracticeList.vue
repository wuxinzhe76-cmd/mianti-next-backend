<template>
  <div class="practice-list-page">
    <div class="page-header">
      <h1 class="page-title">在线练习</h1>
      <p class="page-subtitle">选择编程题目，在线编写并提交代码</p>
    </div>

    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="题目标题">
          <el-input
            v-model="searchForm.title"
            placeholder="搜索题目"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="searchForm.difficulty" placeholder="全部难度" clearable style="width: 120px">
            <el-option label="简单" value="EASY" />
            <el-option label="中等" value="MEDIUM" />
            <el-option label="困难" value="HARD" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="list-card" v-loading="loading">
      <el-table :data="questions" style="width: 100%">
        <el-table-column prop="id" label="#" width="80" />
        <el-table-column prop="title" label="题目" min-width="200">
          <template #default="scope">
            <el-link type="primary" @click="goToPractice(scope.row.id)">
              {{ scope.row.title }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="难度" width="100">
          <template #default="scope">
            <el-tag :type="getDifficultyType(scope.row.difficulty)" size="small">
              {{ getDifficultyText(scope.row.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时间限制" width="120">
          <template #default="scope">
            {{ scope.row.timeLimit || 1000 }} ms
          </template>
        </el-table-column>
        <el-table-column label="内存限制" width="120">
          <template #default="scope">
            {{ scope.row.memoryLimit || 256 }} MB
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="goToPractice(scope.row.id)">
              <el-icon><VideoPlay /></el-icon>
              去练习
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && questions.length === 0" description="暂无编程题目" />

      <div class="pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axiosInstance from '@/utils/axios';

interface PracticeQuestion {
  id: number;
  title: string;
  difficulty?: string;
  timeLimit?: number;
  memoryLimit?: number;
}

const router = useRouter();
const loading = ref(false);
const questions = ref<PracticeQuestion[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

const searchForm = reactive({
  title: '',
  difficulty: ''
});

const getDifficultyType = (difficulty?: string) => {
  const map: Record<string, string> = { EASY: 'success', MEDIUM: 'warning', HARD: 'danger' };
  return map[difficulty || ''] || 'info';
};

const getDifficultyText = (difficulty?: string) => {
  const map: Record<string, string> = { EASY: '简单', MEDIUM: '中等', HARD: '困难' };
  return map[difficulty || ''] || '未知';
};

const goToPractice = (id: number) => {
  router.push(`/practice/${id}`);
};

const loadQuestions = async () => {
  loading.value = true;
  try {
    const params: Record<string, any> = {
      current: currentPage.value,
      pageSize: pageSize.value,
      type: 'PROGRAMMING'
    };
    if (searchForm.title) params.title = searchForm.title;
    if (searchForm.difficulty) params.difficulty = searchForm.difficulty;

    const response = await axiosInstance.post('/question/list/page/vo', params);
    if (response.data.code === 0 && response.data.data) {
      questions.value = response.data.data.records || [];
      total.value = response.data.data.total || 0;
    }
  } catch (error) {
    console.error('加载题目失败:', error);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  loadQuestions();
};

const resetSearch = () => {
  searchForm.title = '';
  searchForm.difficulty = '';
  currentPage.value = 1;
  loadQuestions();
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  loadQuestions();
};

const handleCurrentChange = (page: number) => {
  currentPage.value = page;
  loadQuestions();
};

onMounted(() => {
  loadQuestions();
});
</script>

<style scoped lang="scss">
.practice-list-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;

  .page-title {
    font-size: 28px;
    font-weight: 700;
    color: #303133;
    margin: 0 0 8px;
  }

  .page-subtitle {
    font-size: 14px;
    color: #909399;
    margin: 0;
  }
}

.search-card {
  margin-bottom: 16px;
}

.list-card {
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
