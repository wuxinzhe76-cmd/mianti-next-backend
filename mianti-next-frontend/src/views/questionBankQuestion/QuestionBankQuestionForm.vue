<template>
  <div class="question-bank-question-form">
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <span>添加题库题目关联</span>
        </div>
      </template>
      <el-form 
        ref="formRef"
        :model="form" 
        :rules="rules"
        label-width="120px"
        class="form-content"
      >
        <el-form-item label="题库" prop="questionBankId">
          <el-select 
            v-model="form.questionBankId" 
            placeholder="请选择题库"
            style="width: 100%"
            @change="loadQuestions"
          >
            <el-option
              v-for="item in questionBanks"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="题目" prop="questionIdList">
          <el-select 
            v-model="form.questionIdList" 
            multiple
            placeholder="请选择题目（可多选）"
            style="width: 100%"
            v-loading="questionLoading"
          >
            <el-option
              v-for="item in questions"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            <el-icon><Check /></el-icon>
            提交
          </el-button>
          <el-button @click="goBack">
            <el-icon><Close /></el-icon>
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import { useQuestionBankStore } from '@/stores/questionBank';
import { useQuestionStore } from '@/stores/question';
import { useQuestionBankQuestionStore } from '@/stores/questionBankQuestion';
import type { QuestionBankVO, QuestionVO, QuestionBankQuestionBatchAddRequest } from '@/types';

const router = useRouter();
const questionBankStore = useQuestionBankStore();
const questionStore = useQuestionStore();
const questionBankQuestionStore = useQuestionBankQuestionStore();

const formRef = ref<FormInstance>();
const loading = ref(false);
const questionLoading = ref(false);
const questionBanks = ref<QuestionBankVO[]>([]);
const questions = ref<QuestionVO[]>([]);

const form = reactive({
  questionBankId: null as number | null,
  questionIdList: [] as number[]
});

const rules: FormRules = {
  questionBankId: [
    { required: true, message: '请选择题库', trigger: 'change' }
  ],
  questionIdList: [
    { required: true, message: '请选择题目', trigger: 'change' },
    { type: 'array', min: 1, message: '至少选择一个题目', trigger: 'change' }
  ]
};

const loadQuestionBanks = async () => {
  try {
    const response = await questionBankStore.getQuestionBankList({
      current: 1,
      pageSize: 1000
    });
    questionBanks.value = response.data.records;
  } catch (error) {
    console.error('加载题库列表失败:', error);
    ElMessage.error('加载题库列表失败');
  }
};

const loadQuestions = async () => {
  if (!form.questionBankId) {
    questions.value = [];
    return;
  }
  
  questionLoading.value = true;
  try {
    const response = await questionStore.getQuestionList({
      current: 1,
      pageSize: 1000
    });
    questions.value = response.data.records;
  } catch (error) {
    console.error('加载题目列表失败:', error);
    ElMessage.error('加载题目列表失败');
  } finally {
    questionLoading.value = false;
  }
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    loading.value = true;
    
    const data: QuestionBankQuestionBatchAddRequest = {
      questionBankId: form.questionBankId!,
      questionIdList: form.questionIdList
    };
    
    await questionBankQuestionStore.batchAddQuestionsToBank(data);
    ElMessage.success('添加成功');
    goBack();
  } catch (error: any) {
    if (error !== false) {
      ElMessage.error(error.message || '添加失败');
    }
  } finally {
    loading.value = false;
  }
};

const goBack = () => {
  router.push('/questionBankQuestion/list');
};

onMounted(() => {
  loadQuestionBanks();
});
</script>

<style scoped>
.question-bank-question-form {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.form-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-content {
  margin-top: 20px;
}
</style>
