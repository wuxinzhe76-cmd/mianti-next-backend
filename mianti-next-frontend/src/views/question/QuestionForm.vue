<template>
  <div class="question-form">
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑题目' : '创建题目' }}</span>
        </div>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="题目标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入题目标题" />
        </el-form-item>
        <el-form-item label="题目内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="请输入题目内容"
          />
        </el-form-item>
        <el-form-item label="题目类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择题目类型">
            <el-option label="单选题" value="single" />
            <el-option label="多选题" value="multiple" />
            <el-option label="判断题" value="judgment" />
            <el-option label="简答题" value="essay" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度" prop="difficulty">
          <el-select v-model="form.difficulty" placeholder="请选择难度">
            <el-option label="简单" value="easy" />
            <el-option label="中等" value="medium" />
            <el-option label="困难" value="hard" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-tag
            v-for="tag in form.tags"
            :key="tag"
            closable
            @close="removeTag(tag)"
          >
            {{ tag }}
          </el-tag>
          <el-input
            v-model="newTag"
            class="tag-input"
            placeholder="输入标签后按回车添加"
            @keyup.enter="addTag"
          />
        </el-form-item>
        <el-form-item label="答案" prop="answer">
          <el-input
            v-model="form.answer"
            type="textarea"
            :rows="4"
            placeholder="请输入答案"
          />
        </el-form-item>
        <el-form-item label="所属题库" prop="questionBankId">
          <el-select v-model="form.questionBankId" placeholder="请选择所属题库">
            <el-option
              v-for="bank in questionBanks"
              :key="bank.id"
              :label="bank.name"
              :value="bank.id"
            />
          </el-select>
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
import { useQuestionStore } from '@/stores/question';
import { useQuestionBankStore } from '@/stores/questionBank';
import type { QuestionAddRequest, QuestionUpdateRequest, QuestionBankVO } from '@/types';

const router = useRouter();
const route = useRoute();
const questionStore = useQuestionStore();
const questionBankStore = useQuestionBankStore();

const formRef = ref();
const loading = ref(false);
const isEdit = computed(() => !!route.params.id);
const newTag = ref('');

const form = reactive({
  title: '',
  content: '',
  type: '',
  difficulty: '',
  tags: [] as string[],
  answer: '',
  questionBankId: undefined as number | undefined
});

const questionBanks = ref<QuestionBankVO[]>([]);

const rules = {
  title: [
    { required: true, message: '请输入题目标题', trigger: 'blur' },
    { min: 2, max: 100, message: '题目标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入题目内容', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择题目类型', trigger: 'change' }
  ],
  difficulty: [
    { required: true, message: '请选择难度', trigger: 'change' }
  ],
  answer: [
    { required: true, message: '请输入答案', trigger: 'blur' }
  ]
};

const addTag = () => {
  if (newTag.value && !form.tags.includes(newTag.value)) {
    form.tags.push(newTag.value);
    newTag.value = '';
  }
};

const removeTag = (tag: string) => {
  const index = form.tags.indexOf(tag);
  if (index > -1) {
    form.tags.splice(index, 1);
  }
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    loading.value = true;
    
    if (isEdit.value) {
      // 编辑模式
      const updateData: QuestionUpdateRequest = {
        id: parseInt(route.params.id as string),
        title: form.title,
        content: form.content,
        type: form.type,
        difficulty: form.difficulty,
        tags: form.tags,
        answer: form.answer,
        questionBankId: form.questionBankId
      };
      await questionStore.updateQuestion(updateData);
      ElMessage.success('更新成功');
    } else {
      // 创建模式
      const addData: QuestionAddRequest = {
        title: form.title,
        content: form.content,
        type: form.type,
        difficulty: form.difficulty,
        tags: form.tags,
        answer: form.answer,
        questionBankId: form.questionBankId
      };
      await questionStore.createQuestion(addData);
      ElMessage.success('创建成功');
    }
    
    // 跳转到题目列表
    router.push('/question/list');
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败');
  } finally {
    loading.value = false;
  }
};

const handleCancel = () => {
  router.push('/question/list');
};

const loadQuestionBanks = async () => {
  try {
    const response = await questionBankStore.getQuestionBankList({
      current: 1,
      pageSize: 100
    });
    questionBanks.value = response.data.records;
  } catch (error) {
    console.error('加载题库列表失败:', error);
  }
};

const loadQuestionDetail = async () => {
  if (!isEdit.value) return;
  
  try {
    const id = parseInt(route.params.id as string);
    const response = await questionStore.getQuestionDetail(id);
    const question = response.data;
    form.title = question.title;
    form.content = question.content;
    form.type = question.type;
    form.difficulty = question.difficulty;
    form.tags = question.tags;
    form.answer = question.answer;
    form.questionBankId = question.questionBankId;
  } catch (error) {
    console.error('加载题目详情失败:', error);
    ElMessage.error('加载题目详情失败');
  }
};

onMounted(async () => {
  // 加载题库列表
  await loadQuestionBanks();
  // 加载题目详情（编辑模式）
  await loadQuestionDetail();
});
</script>

<style scoped>
.question-form {
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

.tag-input {
  width: 200px;
  margin-top: 10px;
}

.el-tag {
  margin-right: 10px;
  margin-bottom: 10px;
}
</style>
