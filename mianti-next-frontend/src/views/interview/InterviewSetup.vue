<template>
  <div class="interview-setup">
    <el-card class="setup-card">
      <template #header>
        <div class="card-header">
          <span>选择面试模式</span>
        </div>
      </template>

      <el-form :model="form" label-position="top">
        <el-form-item label="面试模式">
          <el-radio-group v-model="form.mode" class="mode-group">
            <el-radio :value="1" border class="mode-radio">
              <div class="mode-content">
                <el-icon :size="24"><FolderOpened /></el-icon>
                <div>
                  <strong>指定题库</strong>
                  <p class="mode-desc">从你选择的题库中进行面试</p>
                </div>
              </div>
            </el-radio>
            <el-radio :value="2" border class="mode-radio">
              <div class="mode-content">
                <el-icon :size="24"><Coin /></el-icon>
                <div>
                  <strong>大厂随机</strong>
                  <p class="mode-desc">AI 随机从所有题目中抽取面试</p>
                </div>
              </div>
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="form.mode === 1" label="选择题库">
          <el-select
            v-model="form.bankId"
            placeholder="请选择题库"
            filterable
            class="full-width"
          >
            <el-option
              v-for="bank in questionBanks"
              :key="bank.id"
              :label="bank.title"
              :value="bank.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            :disabled="!canStart"
            @click="handleStart"
            class="start-btn"
          >
            <el-icon><VideoPlay /></el-icon>
            开始面试
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { FolderOpened, Coin, VideoPlay } from '@element-plus/icons-vue';
import { useInterviewStore } from '@/stores/interview';
import { useQuestionBankStore } from '@/stores/questionBank';
import type { QuestionBankVO } from '@/types';

const router = useRouter();
const interviewStore = useInterviewStore();
const questionBankStore = useQuestionBankStore();

const loading = ref(false);
const questionBanks = ref<QuestionBankVO[]>([]);

const form = reactive({
  mode: 1 as number,
  bankId: null as number | null,
});

const canStart = computed(() => {
  if (form.mode === 1) {
    return form.bankId !== null;
  }
  return true;
});

const loadQuestionBanks = async () => {
  try {
    const response = await questionBankStore.getQuestionBankList({
      current: 1,
      pageSize: 100,
    });
    questionBanks.value = response.data.data.records;
  } catch (error) {
    console.error('加载题库列表失败:', error);
  }
};

const handleStart = async () => {
  loading.value = true;
  try {
    await interviewStore.startInterview({
      mode: form.mode,
      bankId: form.bankId ?? undefined,
    });

    if (interviewStore.sessionId) {
      router.push(`/interview/session/${interviewStore.sessionId}`);
    }
  } catch (error: any) {
    ElMessage.error(error.message || '开始面试失败');
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadQuestionBanks();
});
</script>

<style scoped>
.interview-setup {
  max-width: 700px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: 600;
}

.mode-group {
  display: flex;
  gap: 16px;
  width: 100%;
}

.mode-radio {
  flex: 1;
}

.mode-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.mode-desc {
  font-size: 12px;
  color: #909399;
  margin: 4px 0 0;
}

.full-width {
  width: 100%;
}

.start-btn {
  width: 100%;
}
</style>
