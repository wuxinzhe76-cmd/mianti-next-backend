<template>
  <div class="code-editor-container">
    <div class="editor-header">
      <div class="language-selector">
        <el-select v-model="selectedLanguage" placeholder="选择编程语言" size="small" @change="onLanguageChange">
          <el-option
            v-for="lang in languages"
            :key="lang.languageCode"
            :label="`${lang.languageName} ${lang.version || ''}`"
            :value="lang.languageCode"
          />
        </el-select>
      </div>
      <div class="editor-actions">
        <el-button 
          type="primary" 
          size="small" 
          :loading="isRunning" 
          @click="runCode"
          :disabled="!canRun"
        >
          <el-icon><VideoPlay /></el-icon>
          运行代码
        </el-button>
        <el-button 
          type="success" 
          size="small" 
          :loading="isSubmitting" 
          @click="submitCode"
          :disabled="!canSubmit"
        >
          <el-icon><Download /></el-icon>
          提交答案
        </el-button>
      </div>
    </div>
    <div ref="editorContainer" class="editor-wrapper"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch, computed } from 'vue';
import * as monaco from 'monaco-editor';
import { VideoPlay, Download } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

interface ProgrammingLanguage {
  id: number;
  languageName: string;
  languageCode: string;
  version?: string;
  template?: string;
}

interface Props {
  questionId?: number;
  defaultCode?: string;
  readOnly?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  questionId: undefined,
  defaultCode: '',
  readOnly: false
});

const emit = defineEmits<{
  (e: 'run', code: string, language: string): void;
  (e: 'submit', code: string, language: string): void;
}>();

const editorContainer = ref<HTMLDivElement | null>(null);
let editor: monaco.editor.IStandaloneCodeEditor | null = null;

const selectedLanguage = ref<string>('java');
const languages = ref<ProgrammingLanguage[]>([]);
const isRunning = ref(false);
const isSubmitting = ref(false);

const canRun = computed(() => !props.readOnly && !isRunning.value);
const canSubmit = computed(() => !props.readOnly && !isSubmitting.value && !!props.questionId);

// 初始化编辑器
const initEditor = () => {
  if (!editorContainer.value) return;

  editor = monaco.editor.create(editorContainer.value, {
    value: props.defaultCode || '// 请在此处编写代码\n',
    language: 'java',
    theme: 'vs-dark',
    automaticLayout: true,
    fontSize: 14,
    minimap: {
      enabled: true
    },
    scrollBeyondLastLine: false,
    renderWhitespace: 'selection',
    suggestOnTriggerCharacters: true,
    quickSuggestions: true,
    tabSize: 4,
    insertSpaces: true,
    wordWrap: 'on',
    readOnly: props.readOnly
  });
};

// 加载支持的编程语言
const loadLanguages = async () => {
  try {
    const response = await fetch('/api/judge/languages');
    const result = await response.json();
    
    if (result.code === 0 && result.data) {
      languages.value = result.data;
      
      // 设置默认语言
      if (languages.value.length > 0) {
        selectedLanguage.value = languages.value[0].languageCode;
        
        // 如果有模板，使用模板
        const defaultLang = languages.value[0];
        if (defaultLang.template && !props.defaultCode) {
          editor?.setValue(defaultLang.template);
        }
      }
    }
  } catch (error) {
    console.error('加载编程语言失败:', error);
    // 使用默认语言列表
    languages.value = [
      { id: 1, languageName: 'Java', languageCode: 'java', version: '17' },
      { id: 2, languageName: 'Python', languageCode: 'python', version: '3.9' },
      { id: 3, languageName: 'C++', languageCode: 'cpp', version: '17' },
      { id: 4, languageName: 'JavaScript', languageCode: 'javascript', version: 'Node.js 16' }
    ];
  }
};

// 切换编程语言
const onLanguageChange = (languageCode: string) => {
  const lang = languages.value.find(l => l.languageCode === languageCode);
  if (!lang) return;

  // 更新编辑器语言
  if (editor) {
    const model = editor.getModel();
    if (model) {
      let monacoLang = languageCode;
      if (languageCode === 'python') {
        monacoLang = 'python';
      } else if (languageCode === 'cpp') {
        monacoLang = 'cpp';
      } else if (languageCode === 'javascript') {
        monacoLang = 'javascript';
      } else if (languageCode === 'java') {
        monacoLang = 'java';
      }
      
      monaco.editor.setModelLanguage(model, monacoLang);
      
      // 如果当前代码为空或者是默认提示，使用新语言的模板
      const currentValue = editor.getValue();
      if (!currentValue || currentValue.includes('// 请在此处编写代码')) {
        if (lang.template) {
          editor.setValue(lang.template);
        }
      }
    }
  }
};

// 运行代码
const runCode = async () => {
  if (!editor) return;
  
  const code = editor.getValue();
  isRunning.value = true;
  
  try {
    emit('run', code, selectedLanguage.value);
  } finally {
    isRunning.value = false;
  }
};

// 提交代码
const submitCode = async () => {
  if (!editor || !props.questionId) return;
  
  const code = editor.getValue();
  
  // 确认提交
  await new Promise<void>((resolve, reject) => {
    ElMessageBox.confirm(
      '确定要提交代码吗？提交后将无法修改。',
      '提交确认',
      {
        confirmButtonText: '确定提交',
        cancelButtonText: '再想想',
        type: 'warning'
      }
    ).then(() => resolve()).catch(() => reject());
  });
  
  isSubmitting.value = true;
  
  try {
    emit('submit', code, selectedLanguage.value);
  } finally {
    isSubmitting.value = false;
  }
};

// 监听外部代码变化
watch(() => props.defaultCode, (newCode) => {
  if (editor && newCode) {
    editor.setValue(newCode);
  }
});

onMounted(() => {
  initEditor();
  loadLanguages();
});

onBeforeUnmount(() => {
  if (editor) {
    editor.dispose();
  }
});

// 暴露方法给父组件
defineExpose({
  getCode: () => editor?.getValue() || '',
  setCode: (code: string) => editor?.setValue(code),
  getLanguage: () => selectedLanguage.value
});
</script>

<style scoped lang="scss">
.code-editor-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
}

.language-selector {
  width: 200px;
}

.editor-actions {
  display: flex;
  gap: 8px;
}

.editor-wrapper {
  flex: 1;
  min-height: 400px;
}
</style>
