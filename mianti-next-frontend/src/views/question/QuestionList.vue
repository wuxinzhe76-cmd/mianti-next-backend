<template>
  <div class="question-list">
    <el-card class="search-card">
      <template #header>
        <div class="card-header">
          <span>题目搜索</span>
          <el-button type="primary" @click="goToAdd">
            <el-icon><Plus /></el-icon>
            创建题目
          </el-button>
        </div>
      </template>
      <el-form :model="searchForm" class="search-form">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="题目标题">
              <el-input v-model="searchForm.title" placeholder="请输入题目标题" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="题目类型">
              <el-select v-model="searchForm.type" placeholder="请选择题目类型" clearable>
                <el-option label="单选题" value="single" />
                <el-option label="多选题" value="multiple" />
                <el-option label="判断题" value="judgment" />
                <el-option label="简答题" value="essay" />
                <el-option label="编程题" value="PROGRAMMING" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="难度">
              <el-select v-model="searchForm.difficulty" placeholder="请选择难度">
                <el-option label="简单" value="easy" />
                <el-option label="中等" value="medium" />
                <el-option label="困难" value="hard" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item>
              <el-button type="primary" @click="handleSearch" style="margin-right: 10px">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
              <el-button @click="resetSearch">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>题目列表</span>
          <el-button 
            type="danger" 
            @click="handleBatchDelete"
            :disabled="selectedQuestionIds.length === 0"
          >
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
        </div>
      </template>
      <el-table 
        :data="questions" 
        style="width: 100%" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="题目标题" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            {{ getTypeLabel(scope.row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="100">
          <template #default="scope">
            {{ getDifficultyLabel(scope.row.difficulty) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="240">
          <template #default="scope">
            <el-button size="small" @click="goToDetail(scope.row.id)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button
              v-if="scope.row.type === 'PROGRAMMING'"
              size="small"
              type="success"
              @click="goToPractice(scope.row.id)"
            >
              <el-icon><VideoPlay /></el-icon>
              练习
            </el-button>
            <el-button size="small" type="primary" @click="goToEdit(scope.row.id)" v-if="isAdmin">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)" v-if="isAdmin">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useQuestionStore } from '@/stores/question';
import { useUserStore } from '@/stores/user';
import type { QuestionVO, QuestionQueryRequest } from '@/types';

const router = useRouter();
const questionStore = useQuestionStore();
const userStore = useUserStore();
const isAdmin = computed(() => userStore.isAdmin);

const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const questions = ref<QuestionVO[]>([]);
const selectedQuestionIds = ref<number[]>([]);

const searchForm = reactive({
  title: '',
  type: '',
  difficulty: ''
});

const getTypeLabel = (type: string) => {
  const typeMap: Record<string, string> = {
    'single': '单选题',
    'multiple': '多选题',
    'judgment': '判断题',
    'essay': '简答题',
    'PROGRAMMING': '编程题'
  };
  return typeMap[type] || type;
};

const getDifficultyLabel = (difficulty: string) => {
  const difficultyMap: Record<string, string> = {
    'easy': '简单',
    'medium': '中等',
    'hard': '困难'
  };
  return difficultyMap[difficulty] || difficulty;
};

const goToAdd = () => {
  router.push('/question/add');
};

const goToEdit = (id: number) => {
  router.push(`/question/edit/${id}`);
};

const goToDetail = (id: number) => {
  router.push(`/question/detail/${id}`);
};

const goToPractice = (id: number) => {
  router.push(`/practice/${id}`);
};

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个题目吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    await questionStore.deleteQuestion(id);
    ElMessage.success('删除成功');
    await loadQuestions();
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败');
    }
  }
};

const handleBatchDelete = async () => {
  if (selectedQuestionIds.value.length === 0) return;

  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedQuestionIds.value.length} 个题目吗？`, '批量删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    await questionStore.batchDeleteQuestions(selectedQuestionIds.value);
    ElMessage.success('批量删除成功');
    selectedQuestionIds.value = [];
    await loadQuestions();
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量删除失败');
    }
  }
};

const handleSelectionChange = (selection: QuestionVO[]) => {
  selectedQuestionIds.value = selection.map(item => item.id);
};

const handleSearch = () => {
  currentPage.value = 1;
  loadQuestions();
};

const resetSearch = () => {
  searchForm.title = '';
  searchForm.type = '';
  searchForm.difficulty = '';
  currentPage.value = 1;
  loadQuestions();
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  loadQuestions();
};

const handleCurrentChange = (current: number) => {
  currentPage.value = current;
  loadQuestions();
};

const loadQuestions = async () => {
  loading.value = true;
  try {
    const query: QuestionQueryRequest = {
      current: currentPage.value,
      pageSize: pageSize.value
    };

    if (searchForm.title) {
      query.title = searchForm.title;
    }

    if (searchForm.type) {
      query.type = searchForm.type;
    }

    if (searchForm.difficulty) {
      query.difficulty = searchForm.difficulty;
    }

    const response = await questionStore.getQuestionList(query);
    questions.value = response.data.records;
    total.value = response.data.total;
  } catch (error) {
    console.error('加载题目列表失败:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadQuestions();
});
</script>

<style scoped>
.question-list {
  width: 100%;
}

.search-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-top: 10px;
}

.list-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
