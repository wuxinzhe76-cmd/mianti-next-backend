<template>
  <div class="interview-report">
    <el-card class="report-card">
      <template #header>
        <div class="report-header">
          <span>面试报告</span>
          <el-button type="primary" @click="router.push('/interview/setup')">
            <el-icon><RefreshRight /></el-icon>
            开始新面试
          </el-button>
        </div>
      </template>

      <div class="report-content">
        <el-result
          icon="success"
          title="面试已完成"
          sub-title="面试报告正在生成中，请稍后查看"
        >
          <template #extra>
            <el-button type="primary" @click="router.push('/dashboard')">
              返回首页
            </el-button>
          </template>
        </el-result>

        <el-divider />

        <div class="final-score">
          <el-statistic title="最终掌握度" :value="currentMastery" suffix="%" />
        </div>

        <el-divider />

        <h4 class="section-title">对话回顾</h4>
        <div class="conversation-review">
          <div
            v-for="msg in messages"
            :key="msg.id"
            :class="['review-item', msg.role]"
          >
            <strong>{{ msg.role === 'assistant' ? 'AI' : '我' }}：</strong>
            <span>{{ msg.content }}</span>
            <span class="review-mastery" v-if="msg.masteryScore !== undefined">
              (掌握度: {{ msg.masteryScore }}%)
            </span>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { RefreshRight } from '@element-plus/icons-vue';
import { useInterviewStore } from '@/stores/interview';

const route = useRoute();
const router = useRouter();
const interviewStore = useInterviewStore();

const messages = computed(() => interviewStore.messages);
const currentMastery = computed(() => interviewStore.currentMastery);

onMounted(() => {
  if (!interviewStore.isEnded) {
    router.push(`/interview/session/${route.params.sessionId}`);
  }
});
</script>

<style scoped>
.interview-report {
  max-width: 900px;
  margin: 0 auto;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
}

.report-content {
  padding: 20px 0;
}

.final-score {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.conversation-review {
  max-height: 400px;
  overflow-y: auto;
}

.review-item {
  padding: 10px 16px;
  margin-bottom: 8px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.6;
}

.review-item.assistant {
  background-color: #f5f7fa;
}

.review-item.user {
  background-color: #ecf5ff;
}

.review-mastery {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
}
</style>
