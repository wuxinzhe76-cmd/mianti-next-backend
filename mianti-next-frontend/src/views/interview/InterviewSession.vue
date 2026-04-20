<template>
  <div class="interview-session">
    <el-card class="session-card">
      <template #header>
        <div class="session-header">
          <div class="header-left">
            <el-button size="small" @click="handleBack">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
            <span class="session-title">AI 面试进行中</span>
          </div>
          <div class="header-right">
            <div class="mastery-badge">
              <span class="mastery-label">掌握度</span>
              <el-progress
                :percentage="currentMastery"
                :stroke-width="8"
                :show-text="false"
                :color="getMasteryColor"
                style="width: 120px"
              />
              <span class="mastery-value">{{ currentMastery }}%</span>
            </div>
          </div>
        </div>
      </template>

      <div class="chat-container" ref="chatContainerRef">
        <div
          v-for="msg in messages"
          :key="msg.id"
          :class="['message', msg.role]"
        >
          <div class="message-avatar">
            <el-avatar :size="36" v-if="msg.role === 'assistant'">
              <el-icon><ChatDotRound /></el-icon>
            </el-avatar>
            <el-avatar :size="36" v-else>
              {{ user?.userName?.charAt(0) || 'U' }}
            </el-avatar>
          </div>
          <div class="message-bubble">
            <div class="message-content">{{ msg.content }}</div>
            <div class="message-meta" v-if="msg.role === 'assistant'">
              <span v-if="msg.directive" :class="['directive-tag', getDirectiveClass(msg.directive)]">
                {{ getDirectiveLabel(msg.directive) }}
              </span>
              <span class="mastery-score" v-if="msg.masteryScore !== undefined">
                掌握度: {{ msg.masteryScore }}%
              </span>
            </div>
          </div>
        </div>

        <div v-if="loading" class="message assistant">
          <div class="message-avatar">
            <el-avatar :size="36">
              <el-icon><ChatDotRound /></el-icon>
            </el-avatar>
          </div>
          <div class="message-bubble">
            <div class="message-content typing-indicator">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>

      <div class="input-area" :class="{ 'input-disabled': isEnded }">
        <el-alert
          v-if="isEnded"
          title="面试已结束"
          type="info"
          :closable="false"
          show-icon
          class="ended-alert"
        />
        <div class="input-row">
          <el-input
            v-model="userAnswer"
            type="textarea"
            :rows="3"
            placeholder="请输入你的回答..."
            :disabled="loading || isEnded"
            @keydown.ctrl.enter="handleSubmit"
          />
          <el-button
            type="primary"
            :loading="loading"
            :disabled="!userAnswer.trim() || loading || isEnded"
            @click="handleSubmit"
            class="submit-btn"
          >
            <el-icon><Promotion /></el-icon>
            提交回答
          </el-button>
        </div>
        <p class="input-hint">Ctrl + Enter 快捷提交</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ArrowLeft, ChatDotRound, Promotion } from '@element-plus/icons-vue';
import { useInterviewStore } from '@/stores/interview';
import { useUserStore } from '@/stores/user';

const route = useRoute();
const router = useRouter();
const interviewStore = useInterviewStore();
const userStore = useUserStore();

const userAnswer = ref('');
const chatContainerRef = ref<HTMLElement | null>(null);

const sessionId = computed<number>(() => Number(route.params.sessionId));
const messages = computed(() => interviewStore.messages);
const loading = computed(() => interviewStore.loading);
const currentMastery = computed(() => interviewStore.currentMastery);
const isEnded = computed(() => interviewStore.isEnded);
const user = computed(() => userStore.user);

const getMasteryColor = computed(() => {
  if (currentMastery.value >= 80) return '#67C23A';
  if (currentMastery.value >= 60) return '#E6A23C';
  return '#F56C6C';
});

const getDirectiveLabel = (directive: string) => {
  const map: Record<string, string> = {
    DEEP_DIVE: '继续追问',
    NEXT_QUESTION: '切换题目',
    END_INTERVIEW: '结束面试',
  };
  return map[directive] || directive;
};

const getDirectiveClass = (directive: string) => {
  const map: Record<string, string> = {
    DEEP_DIVE: 'directive-deep-dive',
    NEXT_QUESTION: 'directive-next',
    END_INTERVIEW: 'directive-end',
  };
  return map[directive] || '';
};

const scrollToBottom = async () => {
  await nextTick();
  if (chatContainerRef.value) {
    chatContainerRef.value.scrollTop = chatContainerRef.value.scrollHeight;
  }
};

watch(messages, scrollToBottom, { deep: true });
watch(loading, (val) => {
  if (val) scrollToBottom();
});

const handleSubmit = async () => {
  const answer = userAnswer.value.trim();
  if (!answer || loading.value || isEnded.value) return;

  userAnswer.value = '';

  try {
    await interviewStore.submitAnswer({
      sessionId: sessionId.value,
      answer,
    });

    if (interviewStore.isEnded) {
      ElMessage.info('面试已结束，正在跳转至报告页...');
      setTimeout(() => {
        router.push(`/interview/report/${sessionId.value}`);
      }, 3000);
    }
  } catch (error: any) {
    ElMessage.error(error.message || '提交回答失败');
    userAnswer.value = answer;
  }
};

const handleBack = () => {
  router.push('/interview/setup');
};

onMounted(() => {
  if (interviewStore.sessionId !== sessionId.value) {
    ElMessage.warning('未找到当前面试会话，请重新开始');
    router.push('/interview/setup');
  }
});
</script>

<style scoped>
.interview-session {
  max-width: 900px;
  margin: 0 auto;
}

.session-card {
  min-height: 70vh;
  display: flex;
  flex-direction: column;
}

.session-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.session-title {
  font-size: 16px;
  font-weight: 600;
}

.mastery-badge {
  display: flex;
  align-items: center;
  gap: 8px;
}

.mastery-label {
  font-size: 13px;
  color: #909399;
}

.mastery-value {
  font-size: 14px;
  font-weight: 600;
  min-width: 40px;
  text-align: right;
}

.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px 0;
  min-height: 400px;
  max-height: 60vh;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message.user {
  flex-direction: row-reverse;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.message.assistant .message-bubble {
  align-items: flex-start;
}

.message.assistant .message-content {
  background-color: #f5f7fa;
  color: #303133;
  border-bottom-left-radius: 4px;
}

.message.user .message-bubble {
  align-items: flex-end;
}

.message.user .message-content {
  background-color: #ecf5ff;
  color: #303133;
  border-bottom-right-radius: 4px;
}

.message-meta {
  display: flex;
  gap: 12px;
  margin-top: 6px;
  font-size: 12px;
}

.directive-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
}

.directive-deep-dive {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.directive-next {
  background-color: #ecf5ff;
  color: #409eff;
}

.directive-end {
  background-color: #fef0f0;
  color: #f56c6c;
}

.mastery-score {
  color: #909399;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background-color: #909399;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { opacity: 0.3; transform: translateY(0); }
  30% { opacity: 1; transform: translateY(-4px); }
}

.input-area {
  border-top: 1px solid #e4e7ed;
  padding-top: 16px;
  margin-top: 16px;
}

.input-row {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.submit-btn {
  flex-shrink: 0;
  height: 80px;
}

.input-hint {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 8px;
  text-align: right;
}

.ended-alert {
  margin-bottom: 12px;
}

.input-disabled .el-textarea__inner {
  background-color: #f5f7fa;
}
</style>
