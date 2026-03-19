<template>
  <div class="question-bank-form">
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑题库' : '创建题库' }}</span>
        </div>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="题库名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入题库名称" />
        </el-form-item>
        <el-form-item label="题库描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入题库描述"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            {{ isEdit ? '更新' : '创建' }}
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useQuestionBankStore } from '@/stores/questionBank';
import type { QuestionBankAddRequest, QuestionBankUpdateRequest } from '@/types';

const router = useRouter();
const route = useRoute();
const questionBankStore = useQuestionBankStore();

const formRef = ref();
const loading = ref(false);
const isEdit = computed(() => !!route.params.id);

const form = reactive({
  name: '',
  description: ''
});

const rules = {
  name: [
    { required: true, message: '请输入题库名称', trigger: 'blur' },
    { min: 2, max: 50, message: '题库名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '题库描述最多 200 个字符', trigger: 'blur' }
  ]
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    loading.value = true;
    
    if (isEdit.value) {
      // 编辑模式
      const updateData: QuestionBankUpdateRequest = {
        id: parseInt(route.params.id as string),
        name: form.name,
        description: form.description
      };
      await questionBankStore.updateQuestionBank(updateData);
      ElMessage.success('更新成功');
    } else {
      // 创建模式
      const addData: QuestionBankAddRequest = {
        name: form.name,
        description: form.description
      };
      await questionBankStore.createQuestionBank(addData);
      ElMessage.success('创建成功');
    }
    
    // 跳转到题库列表
    router.push('/questionBank/list');
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败');
  } finally {
    loading.value = false;
  }
};

const handleCancel = () => {
  router.push('/questionBank/list');
};

const loadQuestionBankDetail = async () => {
  if (!isEdit.value) return;
  
  try {
    const id = parseInt(route.params.id as string);
    const response = await questionBankStore.getQuestionBankDetail(id);
    const questionBank = response.data;
    form.name = questionBank.name;
    form.description = questionBank.description;
  } catch (error) {
    console.error('加载题库详情失败:', error);
    ElMessage.error('加载题库详情失败');
  }
};

onMounted(() => {
  loadQuestionBankDetail();
});
</script>

<style scoped>
.question-bank-form {
  width: 100%;
  max-width: 800px;
}

.form-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
