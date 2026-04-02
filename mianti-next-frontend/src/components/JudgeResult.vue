<template>
  <div class="judge-result-container">
    <el-card v-if="result" class="result-card">
      <template #header>
        <div class="result-header">
          <span class="title">判题结果</span>
          <el-tag 
            :type="getResultType(result.verdict)" 
            size="large"
            class="verdict-tag"
          >
            {{ result.verdictText || result.verdict }}
          </el-tag>
        </div>
      </template>

      <div class="result-content">
        <!-- 基本信息 -->
        <div class="info-grid">
          <div class="info-item">
            <span class="label">执行时间:</span>
            <span class="value">{{ result.executionTime ?? '-' }} ms</span>
          </div>
          <div class="info-item">
            <span class="label">使用内存:</span>
            <span class="value">{{ result.executionMemory ?? '-' }} KB</span>
          </div>
          <div class="info-item">
            <span class="label">编程语言:</span>
            <span class="value">{{ result.languageName ?? result.languageCode }}</span>
          </div>
          <div class="info-item">
            <span class="label">通过率:</span>
            <span class="value">{{ result.testCaseScore ?? 0 }}%</span>
          </div>
        </div>

        <!-- 测试用例详情 -->
        <div v-if="testCaseResults && testCaseResults.length > 0" class="test-cases">
          <h4 class="section-title">测试用例详情</h4>
          <div class="test-case-list">
            <div 
              v-for="(tc, index) in testCaseResults" 
              :key="index"
              class="test-case-item"
              :class="'status-' + tc.verdict.toLowerCase()"
            >
              <div class="test-case-header">
                <span class="case-number">用例 #{{ index + 1 }}</span>
                <el-tag :type="getVerdictType(tc.verdict)" size="small">
                  {{ getVerdictText(tc.verdict) }}
                </el-tag>
              </div>
              <div class="test-case-detail" v-if="expandedCase === index">
                <div class="detail-row">
                  <span class="detail-label">输入:</span>
                  <pre class="detail-content">{{ tc.input || 'N/A' }}</pre>
                </div>
                <div class="detail-row">
                  <span class="detail-label">期望输出:</span>
                  <pre class="detail-content">{{ tc.expectedOutput || 'N/A' }}</pre>
                </div>
                <div class="detail-row" v-if="tc.output">
                  <span class="detail-label">实际输出:</span>
                  <pre class="detail-content">{{ tc.output }}</pre>
                </div>
                <div class="detail-row" v-if="tc.executionTime !== undefined">
                  <span class="detail-label">执行时间:</span>
                  <span class="detail-value">{{ tc.executionTime }} ms</span>
                </div>
              </div>
              <div class="expand-btn" @click="toggleExpand(index)">
                {{ expandedCase === index ? '收起' : '展开详情' }}
              </div>
            </div>
          </div>
        </div>

        <!-- 错误信息 -->
        <div v-if="result.errorMessage" class="error-message">
          <h4 class="section-title error">错误信息</h4>
          <pre class="error-content">{{ result.errorMessage }}</pre>
        </div>
      </div>
    </el-card>

    <el-empty v-else description="暂无判题结果" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

interface TestCaseResult {
  testCaseId?: number;
  verdict: string;
  executionTime?: number;
  executionMemory?: number;
  output?: string;
  expectedOutput?: string;
  input?: string;
}

interface JudgeResult {
  submissionId?: number;
  questionId?: number;
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
}

interface Props {
  result: JudgeResult | null;
}

const props = defineProps<Props>();

const expandedCase = ref<number | null>(null);

// 解析测试用例结果
const testCaseResults = computed<TestCaseResult[]>(() => {
  if (!props.result?.testCaseResults) return [];
  
  try {
    const parsed = JSON.parse(props.result.testCaseResults);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
});

// 获取结果标签类型
const getResultType = (verdict: string) => {
  const typeMap: Record<string, any> = {
    'ACCEPTED': 'success',
    'WA': 'danger',
    'TLE': 'warning',
    'MLE': 'warning',
    'RE': 'danger',
    'CE': 'danger'
  };
  return typeMap[verdict] || 'info';
};

// 获取判题结果类型
const getVerdictType = (verdict: string) => {
  return getResultType(verdict);
};

// 获取判题结果文本
const getVerdictText = (verdict: string) => {
  const textMap: Record<string, string> = {
    'ACCEPTED': '通过',
    'WA': '答案错误',
    'TLE': '超时',
    'MLE': '内存超限',
    'RE': '运行错误',
    'CE': '编译错误'
  };
  return textMap[verdict] || verdict;
};

// 切换展开状态
const toggleExpand = (index: number) => {
  expandedCase.value = expandedCase.value === index ? null : index;
};
</script>

<style scoped lang="scss">
.judge-result-container {
  padding: 16px;
}

.result-card {
  :deep(.el-card__header) {
    padding: 16px;
  }
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .title {
    font-size: 16px;
    font-weight: bold;
  }
  
  .verdict-tag {
    font-size: 14px;
    padding: 6px 16px;
  }
}

.result-content {
  .info-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
    margin-bottom: 24px;
    
    .info-item {
      display: flex;
      flex-direction: column;
      
      .label {
        font-size: 13px;
        color: #909399;
        margin-bottom: 4px;
      }
      
      .value {
        font-size: 16px;
        font-weight: 500;
        color: #303133;
      }
    }
  }
  
  .section-title {
    font-size: 14px;
    font-weight: 600;
    margin: 20px 0 12px;
    color: #303133;
    
    &.error {
      color: #f56c6c;
    }
  }
  
  .test-cases {
    .test-case-list {
      .test-case-item {
        border: 1px solid #e4e7ed;
        border-radius: 4px;
        margin-bottom: 8px;
        overflow: hidden;
        
        &.status-accepted {
          border-left: 4px solid #67c23a;
        }
        
        &.status-wa,
        &.status-ce,
        &.status-re {
          border-left: 4px solid #f56c6c;
        }
        
        &.status-tle,
        &.status-mle {
          border-left: 4px solid #e6a23c;
        }
        
        .test-case-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 12px 16px;
          background-color: #fafafa;
          
          .case-number {
            font-weight: 500;
            color: #606266;
          }
        }
        
        .test-case-detail {
          padding: 12px 16px;
          background-color: #fff;
          
          .detail-row {
            margin-bottom: 12px;
            
            &:last-child {
              margin-bottom: 0;
            }
            
            .detail-label {
              display: block;
              font-size: 13px;
              color: #909399;
              margin-bottom: 4px;
            }
            
            .detail-content {
              background-color: #f5f7fa;
              padding: 8px 12px;
              border-radius: 4px;
              font-family: 'Courier New', monospace;
              font-size: 13px;
              white-space: pre-wrap;
              word-break: break-all;
              margin: 0;
            }
            
            .detail-value {
              font-size: 14px;
              color: #303133;
            }
          }
        }
        
        .expand-btn {
          padding: 8px 16px;
          text-align: center;
          cursor: pointer;
          font-size: 13px;
          color: #409eff;
          background-color: #fafafa;
          transition: background-color 0.2s;
          
          &:hover {
            background-color: #ecf5ff;
          }
        }
      }
    }
  }
  
  .error-message {
    .error-content {
      background-color: #fef0f0;
      border: 1px solid #fde2e2;
      padding: 16px;
      border-radius: 4px;
      font-family: 'Courier New', monospace;
      font-size: 13px;
      color: #f56c6c;
      white-space: pre-wrap;
      word-break: break-all;
      margin: 0;
    }
  }
}
</style>
