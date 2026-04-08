<template>
  <div class="question-practice-page">
    <div class="page-container">
      <!-- 左侧：题目描述 -->
      <div class="left-panel">
        <el-card class="question-card">
          <template #header>
            <div class="question-header">
              <div class="header-top">
                <el-button size="small" @click="router.push('/question/practice-list')">
                  <el-icon><ArrowLeft /></el-icon>
                  返回列表
                </el-button>
                <h2 class="question-title">{{ question.title }}</h2>
              </div>
              <div class="question-meta">
                <el-tag :type="getDifficultyType(question.difficulty)" size="small">
                  {{ getDifficultyText(question.difficulty) }}
                </el-tag>
                <span class="meta-item">
                  <el-icon><Clock /></el-icon>
                  {{ question.timeLimit || 1000 }} ms
                </span>
                <span class="meta-item">
                  <el-icon><Coin /></el-icon>
                  {{ question.memoryLimit || 256 }} MB
                </span>
              </div>
            </div>
          </template>

          <div class="question-content" v-html="renderedContent"></div>

          <!-- 示例测试用例 -->
          <div v-if="exampleTestCases.length > 0" class="example-cases">
            <h4 class="section-title">示例</h4>
            <div v-for="(tc, index) in exampleTestCases" :key="index" class="example-case">
              <div class="example-input">
                <strong>输入:</strong>
                <pre>{{ tc.input }}</pre>
              </div>
              <div class="example-output">
                <strong>输出:</strong>
                <pre>{{ tc.output }}</pre>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧：代码编辑器和判题结果 -->
      <div class="right-panel">
        <div class="editor-section">
          <CodeEditor
            ref="codeEditorRef"
            :question-id="questionId"
            :default-code="defaultCode"
            @run="handleRunCode"
            @submit="handleSubmitCode"
          />
        </div>

        <div class="result-section" v-if="showResult">
          <JudgeResult :result="judgeResult" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Clock, Coin, ArrowLeft } from '@element-plus/icons-vue';
import CodeEditor from '@/components/CodeEditor.vue';
import JudgeResult from '@/components/JudgeResult.vue';
import axios from '@/utils/axios';

interface Question {
  id: number;
  title: string;
  content: string;
  type?: string;
  difficulty?: string;
  timeLimit?: number;
  memoryLimit?: number;
  template?: string;
  acceptedCount?: number;
  submissionCount?: number;
  acceptanceRate?: number;
}

interface TestCase {
  id: number;
  input: string;
  output: string;
  isExample: number;
}

interface JudgeResult {
  submissionId?: number;
  verdict: string;
  verdictText?: string;
  executionTime?: number;
  executionMemory?: number;
  testCaseScore?: number;
  totalTestCase?: number;
  passedTestCase?: number;
  languageCode?: string;
  languageName?: string;
  errorMessage?: string;
  testCaseResults?: string;
  judgeTime?: string;
}

const route = useRoute();
const router = useRouter();
const questionId = computed<number>(() => Number(route.params.id) || Number(route.query.id));

const question = reactive<Question>({
  id: 0,
  title: '',
  content: '',
  difficulty: 'MEDIUM',
  timeLimit: 1000,
  memoryLimit: 256
});

const exampleTestCases = ref<TestCase[]>([]);
const defaultCode = ref<string>('');
const codeEditorRef = ref<any>(null);
const showResult = ref(false);
const judgeResult = ref<JudgeResult | null>(null);
const pollingTimer = ref<any>(null);

// 渲染题目内容（支持 Markdown）
const renderedContent = computed(() => {
  // 简单处理，实际可以使用 markdown 库
  return question.content?.replace(/\n/g, '<br/>') || '';
});

// 获取难度类型
const getDifficultyType = (difficulty?: string) => {
  const typeMap: Record<string, any> = {
    'EASY': 'success',
    'MEDIUM': 'warning',
    'HARD': 'danger'
  };
  return typeMap[difficulty || 'MEDIUM'] || 'info';
};

// 获取难度文本
const getDifficultyText = (difficulty?: string) => {
  const textMap: Record<string, string> = {
    'EASY': '简单',
    'MEDIUM': '中等',
    'HARD': '困难'
  };
  return textMap[difficulty || 'MEDIUM'] || '未知';
};

// 加载题目详情
const loadQuestion = async () => {
  try {
    const response = await axios.get('/question/get/vo', { params: { id: questionId.value } });
    if (response.data.code === 0 && response.data.data) {
      Object.assign(question, response.data.data);
      
      // 解析模板
      if (question.template) {
        try {
          const templates = JSON.parse(question.template);
          defaultCode.value = templates.java || templates.python || templates.cpp || '';
        } catch {
          defaultCode.value = question.template;
        }
      }
    }
  } catch (error) {
    console.error('加载题目失败:', error);
    ElMessage.error('加载题目失败');
  }
};

// 加载示例测试用例
const loadExampleTestCases = async () => {
  try {
    const response = await axios.get(`/testcase/question/${questionId.value}/examples`);
    if (response.data.code === 0 && response.data.data) {
      exampleTestCases.value = response.data.data;
    }
  } catch (error) {
    console.log('加载示例测试用例失败（可选功能）');
  }
};

// 运行代码
const handleRunCode = async (code: string, language: string) => {
  ElMessage.info('运行代码中...（此功能需要后端支持）');
  // TODO: 实现运行代码接口
};

// 提交代码
const handleSubmitCode = async (code: string, language: string) => {
  try {
    const response = await axios.post('/judge/submit', {
      questionId: questionId.value,
      languageCode: language,
      code: code
    });

    if (response.data.code === 0 && response.data.data) {
      const submissionId = response.data.data;
      ElMessage.success('提交成功，判题中...');
      
      // 开始轮询判题结果
      startPollingResult(submissionId);
    } else {
      ElMessage.error(response.data.message || '提交失败');
    }
  } catch (error: any) {
    console.error('提交失败:', error);
    ElMessage.error(error.response?.data?.message || '提交失败');
  }
};

// 轮询判题结果
const startPollingResult = (submissionId: number) => {
  showResult.value = true;
  
  const poll = async () => {
    try {
      const response = await axios.get(`/judge/result/${submissionId}`);
      
      if (response.data.code === 0 && response.data.data) {
        judgeResult.value = response.data.data;
        
        // 如果判题完成，停止轮询
        const status = judgeResult.value.verdict;
        if (['ACCEPTED', 'WA', 'TLE', 'MLE', 'RE', 'CE'].includes(status)) {
          stopPolling();
          
          if (status === 'ACCEPTED') {
            ElMessage.success('答案正确！🎉');
          } else {
            ElMessage.error(`判题结果：${judgeResult.value.verdictText || status}`);
          }
        }
      }
    } catch (error) {
      console.error('获取判题结果失败:', error);
    }
  };

  // 立即执行一次
  poll();
  
  // 每 2 秒轮询一次
  pollingTimer.value = setInterval(poll, 2000);
  
  // 最多轮询 60 秒
  setTimeout(() => {
    stopPolling();
    if (judgeResult.value?.verdict === 'PENDING' || judgeResult.value?.verdict === 'JUDGING') {
      ElMessage.warning('判题超时，请稍后查看提交记录');
    }
  }, 60000);
};

// 停止轮询
const stopPolling = () => {
  if (pollingTimer.value) {
    clearInterval(pollingTimer.value);
    pollingTimer.value = null;
  }
};

onMounted(() => {
  if (questionId.value) {
    loadQuestion();
    loadExampleTestCases();
  }
});

onBeforeUnmount(() => {
  stopPolling();
});
</script>

<style scoped lang="scss">
.question-practice-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.page-container {
  display: flex;
  gap: 16px;
  padding: 20px;
  max-width: 1600px;
  margin: 0 auto;
}

.left-panel {
  flex: 1;
  min-width: 500px;
  
  .question-card {
    :deep(.el-card__header) {
      padding: 16px 20px;
    }
    
    :deep(.el-card__body) {
      padding: 20px;
    }
  }
  
  .question-header {
    .header-top {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 12px;
    }

    .question-title {
      font-size: 20px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }
    
    .question-meta {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .meta-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 13px;
        color: #909399;
      }
    }
  }
  
  .question-content {
    font-size: 14px;
    line-height: 1.8;
    color: #606266;
    margin-bottom: 24px;
  }
  
  .example-cases {
    .section-title {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 16px;
      color: #303133;
    }
    
    .example-case {
      margin-bottom: 16px;
      padding: 16px;
      background-color: #fafafa;
      border-radius: 8px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .example-input,
      .example-output {
        margin-bottom: 12px;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        strong {
          display: block;
          margin-bottom: 8px;
          color: #606266;
        }
        
        pre {
          background-color: #fff;
          padding: 12px;
          border-radius: 4px;
          font-family: 'Courier New', monospace;
          font-size: 13px;
          white-space: pre-wrap;
          word-break: break-all;
          margin: 0;
          border: 1px solid #e4e7ed;
        }
      }
    }
  }
}

.right-panel {
  width: 600px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
  
  .editor-section {
    height: 500px;
  }
  
  .result-section {
    min-height: 200px;
  }
}

@media (max-width: 1200px) {
  .page-container {
    flex-direction: column;
  }
  
  .left-panel {
    min-width: auto;
  }
  
  .right-panel {
    width: 100%;
  }
}
</style>
