<template>
  <div class="question-bank-list">
    <el-card class="search-card">
      <template #header>
        <div class="card-header">
          <span>题库搜索</span>
          <el-button type="primary" @click="goToAdd">
            <el-icon><Plus /></el-icon>
            创建题库
          </el-button>
        </div>
      </template>
      <el-form :model="searchForm" class="search-form">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="题库名称">
              <el-input v-model="searchForm.name" placeholder="请输入题库名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="创建用户">
              <el-input v-model="searchForm.userId" placeholder="请输入用户ID" />
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
          <span>题库列表</span>
        </div>
      </template>
      <el-table :data="questionBanks" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="题库名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="userId" label="创建用户" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="goToDetail(scope.row.id)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button size="small" type="primary" @click="goToEdit(scope.row.id)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">
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
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useQuestionBankStore } from '@/stores/questionBank';
import type { QuestionBankVO, QuestionBankQueryRequest } from '@/types';

const router = useRouter();
const questionBankStore = useQuestionBankStore();

const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const questionBanks = ref<QuestionBankVO[]>([]);

const searchForm = reactive({
  name: '',
  userId: ''
});

const goToAdd = () => {
  router.push('/questionBank/add');
};

const goToEdit = (id: number) => {
  router.push(`/questionBank/edit/${id}`);
};

const goToDetail = (id: number) => {
  router.push(`/questionBank/detail/${id}`);
};

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个题库吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    await questionBankStore.deleteQuestionBank(id);
    ElMessage.success('删除成功');
    await loadQuestionBanks();
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败');
    }
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  loadQuestionBanks();
};

const resetSearch = () => {
  searchForm.name = '';
  searchForm.userId = '';
  currentPage.value = 1;
  loadQuestionBanks();
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  loadQuestionBanks();
};

const handleCurrentChange = (current: number) => {
  currentPage.value = current;
  loadQuestionBanks();
};

const loadQuestionBanks = async () => {
  loading.value = true;
  try {
    const query: QuestionBankQueryRequest = {
      current: currentPage.value,
      pageSize: pageSize.value
    };

    if (searchForm.name) {
      query.name = searchForm.name;
    }

    if (searchForm.userId) {
      query.userId = parseInt(searchForm.userId);
    }

    const response = await questionBankStore.getQuestionBankList(query);
    questionBanks.value = response.data.records;
    total.value = response.data.total;
  } catch (error) {
    console.error('加载题库列表失败:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadQuestionBanks();
});
</script>

<style scoped>
.question-bank-list {
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
