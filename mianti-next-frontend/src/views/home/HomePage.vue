<template>
  <MainLayout>
    <div class="home-page">
      <!-- 搜索区域 -->
      <div class="search-section">
        <h1 class="main-title">Java热门面试题 200道</h1>
        <p class="main-description">2025 最新 Java 热门面试题 200 道，涵盖 Java、MySQL、Redis、Spring、SpringBoot、SpringCloud、计算机网络、操作系统、消息队列、Netty、后端场景题、线上问题排查、后端系统设计题等</p>
        <div class="search-box">
          <el-input
            v-model="searchQuery"
            placeholder="搜索题目..."
            clearable
            size="large"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button type="primary" size="large" @click="handleSearch">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
            </template>
          </el-input>
        </div>
      </div>

      <!-- 题目列表 -->
      <div class="question-section">
        <div class="section-header">
          <h2>题目列表</h2>
          <div class="filters">
            <el-select v-model="difficultyFilter" placeholder="难度" clearable>
              <el-option label="简单" value="简单" />
              <el-option label="中等" value="中等" />
              <el-option label="困难" value="困难" />
            </el-select>
            <el-select v-model="tagFilter" placeholder="标签" clearable multiple>
              <el-option v-for="tag in allTags" :key="tag" :label="tag" :value="tag" />
            </el-select>
          </div>
        </div>
        
        <el-table :data="questions" style="width: 100%" v-loading="loading">
          <el-table-column prop="title" label="题目" min-width="400">
            <template #default="scope">
              <router-link :to="`/question/detail/${scope.row.id}`" class="question-title">
                {{ scope.row.title }}
              </router-link>
            </template>
          </el-table-column>
          <el-table-column prop="difficulty" label="难度" width="100">
            <template #default="scope">
              <el-tag :type="getDifficultyType(scope.row.difficulty)">
                {{ scope.row.difficulty }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="tagList" label="标签" min-width="200">
            <template #default="scope">
              <el-tag 
                v-for="tag in scope.row.tagList" 
                :key="tag" 
                size="small"
                class="tag-item"
              >
                {{ tag }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button 
                size="small" 
                @click="goToQuestionDetail(scope.row.id)"
              >
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>

      <!-- 题库列表 -->
      <div class="question-bank-section">
        <h2>热门面试题库</h2>
        <div class="bank-categories">
          <el-button 
            v-for="category in categories" 
            :key="category"
            :type="selectedCategory === category ? 'primary' : 'default'"
            @click="selectCategory(category)"
          >
            {{ category }}
          </el-button>
        </div>
        
        <div class="bank-grid">
          <el-card 
            v-for="bank in questionBanks" 
            :key="bank.id"
            class="bank-card"
            @click="goToBankDetail(bank.id)"
          >
            <template #header>
              <div class="bank-header">
                <h3>{{ bank.title }}</h3>
              </div>
            </template>
            <div class="bank-content">
              <div class="bank-thumbnail" v-if="bank.picture">
                <img :src="bank.picture" :alt="bank.title" />
              </div>
              <div class="bank-thumbnail bank-thumbnail-placeholder" v-else>
                <el-icon class="bank-icon"><Collection /></el-icon>
              </div>
              <p class="bank-description">{{ bank.description }}</p>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import MainLayout from '@/components/layout/MainLayout.vue';
import { useQuestionStore } from '@/stores/question';
import { useQuestionBankStore } from '@/stores/questionBank';
import type { QuestionVO, QuestionQueryRequest, QuestionBankVO, QuestionBankQueryRequest } from '@/types';

const router = useRouter();
const questionStore = useQuestionStore();
const questionBankStore = useQuestionBankStore();

// 搜索和筛选
const searchQuery = ref('');
const difficultyFilter = ref('');
const tagFilter = ref<string[]>([]);
const allTags = ref<string[]>([
  'Java', 'MySQL', 'Redis', 'Spring', 'SpringBoot', 'SpringCloud',
  '计算机网络', '操作系统', '消息队列', 'Netty', '后端场景题'
]);

// 分页
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);

// 题目列表
const questions = ref<QuestionVO[]>([]);

// 题库分类
const categories = ref([
  '热门', 'Java', '后端', '前端', 'Python', '校招原题',
  '公司题库', '真实面经', '计算机基础', 'Go', 'C++',
  '数据库', '计算机网络', '操作系统', '算法', '项目',
  '人工智能', '测试', '大数据', '移动开发'
]);
const selectedCategory = ref('热门');
const questionBanks = ref<QuestionBankVO[]>([]);

// 方法
const handleSearch = async () => {
  currentPage.value = 1;
  await loadQuestions();
};

const loadQuestions = async () => {
  loading.value = true;
  try {
    const query: QuestionQueryRequest = {
      current: currentPage.value,
      pageSize: pageSize.value
    };
    
    if (searchQuery.value) {
      query.searchText = searchQuery.value;
    }
    
    if (difficultyFilter.value) {
      // 这里需要根据后端实现调整，假设后端支持难度筛选
    }
    
    if (tagFilter.value.length > 0) {
      query.tags = tagFilter.value;
    }
    
    await questionStore.getQuestionList(query);
    questions.value = questionStore.questions;
    total.value = questionStore.total;
  } catch (error) {
    console.error('加载题目失败:', error);
  } finally {
    loading.value = false;
  }
};

const loadQuestionBanks = async () => {
  try {
    const query: QuestionBankQueryRequest = {
      current: 1,
      pageSize: 12
    };
    
    await questionBankStore.getQuestionBankList(query);
    questionBanks.value = questionBankStore.questionBanks;
  } catch (error) {
    console.error('加载题库失败:', error);
  }
};

const goToQuestionDetail = (id: number) => {
  router.push(`/question/detail/${id}`);
};

const goToBankDetail = (id: number) => {
  router.push(`/questionBank/detail/${id}?needQueryQuestionList=true`);
};

const selectCategory = (category: string) => {
  selectedCategory.value = category;
  // 这里可以根据分类筛选题库
};

const getDifficultyType = (difficulty: string) => {
  switch (difficulty) {
    case '简单':
      return 'success';
    case '中等':
      return 'warning';
    case '困难':
      return 'danger';
    default:
      return 'info';
  }
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  loadQuestions();
};

const handleCurrentChange = (current: number) => {
  currentPage.value = current;
  loadQuestions();
};

// 初始化
onMounted(async () => {
  await loadQuestions();
  await loadQuestionBanks();
});
</script>

<style scoped>
.home-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 0;
}

.search-section {
  text-align: center;
  margin-bottom: 40px;
  padding: 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  color: white;
}

.main-title {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 16px;
}

.main-description {
  font-size: 16px;
  margin-bottom: 30px;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
}

.search-box {
  max-width: 600px;
  margin: 0 auto;
}

.question-section {
  margin-bottom: 40px;
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.filters {
  display: flex;
  gap: 10px;
}

.question-title {
  color: #3498db;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.question-title:hover {
  color: #2980b9;
  text-decoration: underline;
}

.tag-item {
  margin-right: 5px;
  margin-bottom: 5px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.question-bank-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.question-bank-section h2 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.bank-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 30px;
}

.bank-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.bank-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.bank-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.bank-header h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
}

.bank-content {
  padding: 10px 0;
}

.bank-thumbnail {
  width: 100%;
  height: 120px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 10px;
}

.bank-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.bank-thumbnail-placeholder {
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bank-icon {
  font-size: 48px;
  color: #999;
}

.bank-description {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
  margin: 0;
}

@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .filters {
    width: 100%;
    flex-direction: column;
  }
  
  .bank-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }
}
</style>