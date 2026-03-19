<template>
  <div class="question-bank-question-list">
    <el-card class="search-card">
      <template #header>
        <div class="card-header">
          <span>题库题目关联搜索</span>
        </div>
      </template>
      <el-form :model="searchForm" class="search-form">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="题库ID">
              <el-input v-model="searchForm.questionBankId" placeholder="请输入题库ID" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="题目ID">
              <el-input v-model="searchForm.questionId" placeholder="请输入题目ID" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
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
          <span>题库题目关联列表</span>
          <el-button type="primary" @click="goToAdd">
            <el-icon><Plus /></el-icon>
            添加关联
          </el-button>
        </div>
      </template>
      <el-table 
        :data="questionBankQuestions" 
        style="width: 100%" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="questionBankId" label="题库ID" width="100" />
        <el-table-column prop="questionId" label="题目ID" width="100" />
        <el-table-column prop="userId" label="创建用户ID" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" type="danger" @click="handleRemove(scope.row)">
              <el-icon><Delete /></el-icon>
              移除
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
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useQuestionBankQuestionStore } from '@/stores/questionBankQuestion';
import type { QuestionBankQuestionVO, QuestionBankQuestionQueryRequest, QuestionBankQuestionRemoveRequest } from '@/types';

const router = useRouter();
const questionBankQuestionStore = useQuestionBankQuestionStore();

const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const questionBankQuestions = ref<QuestionBankQuestionVO[]>([]);
const selectedItems = ref<QuestionBankQuestionVO[]>([]);

const searchForm = reactive({
  questionBankId: '',
  questionId: ''
});

const goToAdd = () => {
  router.push('/questionBankQuestion/add');
};

const handleRemove = async (item: QuestionBankQuestionVO) => {
  try {
    await ElMessageBox.confirm('确定要移除这个关联吗？', '移除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    const data: QuestionBankQuestionRemoveRequest = {
      questionBankId: item.questionBankId,
      questionId: item.questionId
    };
    await questionBankQuestionStore.removeQuestionFromBank(data);
    ElMessage.success('移除成功');
    await loadQuestionBankQuestions();
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '移除失败');
    }
  }
};

const handleSelectionChange = (selection: QuestionBankQuestionVO[]) => {
  selectedItems.value = selection;
};

const handleSearch = () => {
  currentPage.value = 1;
  loadQuestionBankQuestions();
};

const resetSearch = () => {
  searchForm.questionBankId = '';
  searchForm.questionId = '';
  currentPage.value = 1;
  loadQuestionBankQuestions();
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  loadQuestionBankQuestions();
};

const handleCurrentChange = (current: number) => {
  currentPage.value = current;
  loadQuestionBankQuestions();
};

const loadQuestionBankQuestions = async () => {
  loading.value = true;
  try {
    const query: QuestionBankQuestionQueryRequest = {
      current: currentPage.value,
      pageSize: pageSize.value
    };

    if (searchForm.questionBankId) {
      query.questionBankId = parseInt(searchForm.questionBankId);
    }

    if (searchForm.questionId) {
      query.questionId = parseInt(searchForm.questionId);
    }

    const response = await questionBankQuestionStore.getQuestionBankQuestionList(query);
    questionBankQuestions.value = response.data.records;
    total.value = response.data.total;
  } catch (error) {
    console.error('加载题库题目关联列表失败:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadQuestionBankQuestions();
});
</script>

<style scoped>
.question-bank-question-list {
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
