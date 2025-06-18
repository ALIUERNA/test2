<template>
  <div class="exam-container">
    <el-card class="exam-card" shadow="hover">
      <div class="exam-header">
        <h2>在线考试</h2>
        <div class="timer" v-if="!submitted">
          <el-icon><Clock /></el-icon>
          <span>剩余时间: {{ formattedTime }}</span>
        </div>
      </div>

      <div v-if="loading" class="loading">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <p>正在加载题目，请稍候...</p>
      </div>

      <div v-else-if="!submitted && questions.length > 0" class="questions-container">
        <div
            v-for="(question, index) in questions"
            :key="question.id"
            class="question-item"
            :class="{ 'current-question': currentQuestion === index }"
        >
          <div class="question-title">
            <span class="question-number">{{ index + 1 }}.</span>
            {{ question.content }}
          </div>
          <el-radio-group v-model="answers[index]" class="options">
            <el-radio label="A" class="option">{{ question.optionA }}</el-radio>
            <el-radio label="B" class="option">{{ question.optionB }}</el-radio>
            <el-radio label="C" class="option">{{ question.optionC }}</el-radio>
            <el-radio label="D" class="option">{{ question.optionD }}</el-radio>
          </el-radio-group>
        </div>

        <div class="navigation">
          <el-button
              v-for="(_, index) in questions"
              :key="index"
              :type="answers[index] ? 'primary' : ''"
              @click="currentQuestion = index"
              circle
          >
            {{ index + 1 }}
          </el-button>
        </div>

        <div class="submit-area">
          <el-button
              type="primary"
              size="large"
              @click="showConfirmDialog"
              :disabled="answers.filter(a => a).length < questions.length"
              round
          >
            提交试卷
          </el-button>
          <span class="answered-count">
            已答 {{ answers.filter(a => a).length }}/{{ questions.length }} 题
          </span>
        </div>
      </div>

      <div v-else-if="!submitted && questions.length === 0" class="no-questions">
        <el-empty description="未获取到有效题目">
          <el-button type="primary" @click="fetchQuestions">重新加载</el-button>
        </el-empty>
      </div>

      <div v-else class="result-container">
        <h3 class="result-title">考试结果</h3>
        <div class="result-stats">
          <div class="stat-item correct">
            <div class="stat-value">{{ result.correctCount }}</div>
            <div class="stat-label">答对</div>
          </div>
          <div class="stat-item wrong">
            <div class="stat-value">{{ result.wrongCount }}</div>
            <div class="stat-label">答错</div>
          </div>
          <div class="stat-item score">
            <div class="stat-value">{{ result.score }}</div>
            <div class="stat-label">得分</div>
          </div>
        </div>

        <div class="action-buttons">
          <el-button type="primary" @click="fetchQuestions" round>再考一次</el-button>
          <el-button @click="$router.push('/test-records')" round>查看记录</el-button>
        </div>
      </div>
    </el-card>

    <el-dialog
        v-model="confirmDialogVisible"
        title="确认提交"
        width="30%"
        align-center
    >
      <p>您已完成 {{ answers.filter(a => a).length }}/{{ questions.length }} 题</p>
      <p>确定要提交考试吗？提交后将无法修改答案。</p>
      <template #footer>
        <el-button @click="confirmDialogVisible = false" round>取消</el-button>
        <el-button type="primary" @click="submitTest" round>确认提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted, onBeforeUnmount, computed} from 'vue'
import request from '@/utils/request';
import { Clock, Loading } from '@element-plus/icons-vue';
import {ElMessage} from "element-plus";

const questions = ref([])
const answers = ref([])
const submitted = ref(false)
const result = ref({
  correctCount: 0,
  wrongCount: 0,
  score: 0
})
const confirmDialogVisible = ref(false)
const loading = ref(true)
const currentQuestion = ref(0)
const timeLeft = ref(1800) // 30分钟
let timer = null

onMounted(() => {
  fetchQuestions();
  startTimer();
})

onBeforeUnmount(() => {
  clearInterval(timer);
})

const formattedTime = computed(() => {
  const minutes = Math.floor(timeLeft.value / 60);
  const seconds = timeLeft.value % 60;
  return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
})

const fetchQuestions = async () => {
  loading.value = true;
  submitted.value = false;
  try {
    const response = await request.get('/api/question/random', {
      params: { count: 10 }
    })

    if (response && response.code === '200' && Array.isArray(response.data)) {
      questions.value = response.data;
      answers.value = new Array(questions.value.length).fill('');
      currentQuestion.value = 0;
    } else {
      ElMessage.error('获取题目失败');
    }
  } catch (error) {
    ElMessage.error('获取题目失败');
  } finally {
    loading.value = false;
  }
}

const startTimer = () => {
  timer = setInterval(() => {
    if (timeLeft.value > 0 && !submitted.value) {
      timeLeft.value--;
    } else if (!submitted.value) {
      submitTest();
      clearInterval(timer);
    }
  }, 1000);
}

const showConfirmDialog = () => {
  if (answers.value.some(answer => !answer)) {
    ElMessage.warning('您还有未完成的题目，确定要提交吗？');
  }
  confirmDialogVisible.value = true;
}

const submitTest = async () => {
  confirmDialogVisible.value = false;
  try {
    const response = await request.post('/api/test/submit', answers.value)
    if (response && response.code === '200' && response.data) {
      submitted.value = true;
      result.value = {
        correctCount: response.data.correctCount,
        wrongCount: response.data.wrongCount,
        score: response.data.score
      };
      clearInterval(timer);
    } else {
      ElMessage.error('提交考试失败');
    }
  } catch (error) {
    ElMessage.error('提交失败');
  }
}
</script>

<style scoped>
.exam-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  padding: 20px;
}

.exam-card {
  width: 800px;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.exam-header h2 {
  margin: 0;
  font-size: 1.8rem;
  color: #333;
}

.timer {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.1rem;
  color: #f56c6c;
  font-weight: 500;
}

.loading {
  text-align: center;
  padding: 50px 0;
}

.loading-icon {
  font-size: 50px;
  color: #409EFF;
  margin-bottom: 20px;
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.questions-container {
  margin-top: 20px;
}

.question-item {
  margin-bottom: 30px;
  padding: 20px;
  border-radius: 8px;
  background-color: #fafafa;
  transition: all 0.3s;
}

.current-question {
  background-color: #f0f7ff;
  box-shadow: 0 0 0 2px #409EFF;
}

.question-title {
  font-size: 1.1rem;
  font-weight: 500;
  margin-bottom: 15px;
  line-height: 1.6;
}

.question-number {
  color: #409EFF;
  font-weight: bold;
  margin-right: 8px;
}

.options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option {
  display: block;
  padding: 10px 15px;
  border-radius: 6px;
  background-color: white;
  border: 1px solid #eee;
  transition: all 0.2s;
}

.option:hover {
  background-color: #f5f7fa;
}

:deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #409EFF;
}

.navigation {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
  margin: 30px 0;
}

.submit-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 40px;
}

.answered-count {
  margin-top: 15px;
  color: #666;
  font-size: 0.9rem;
}

.no-questions {
  padding: 40px 0;
}

.result-container {
  text-align: center;
  padding: 20px;
}

.result-title {
  font-size: 1.5rem;
  color: #333;
  margin-bottom: 30px;
}

.result-stats {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-bottom: 40px;
}

.stat-item {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  font-weight: bold;
}

.stat-item.correct {
  background: linear-gradient(135deg, #67C23A, #85CE61);
}

.stat-item.wrong {
  background: linear-gradient(135deg, #F56C6C, #F78989);
}

.stat-item.score {
  background: linear-gradient(135deg, #409EFF, #79BBFF);
}

.stat-value {
  font-size: 2rem;
  line-height: 1;
}

.stat-label {
  font-size: 1rem;
  margin-top: 5px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

@media (max-width: 850px) {
  .exam-card {
    width: 95%;
    padding: 20px;
  }

  .question-item {
    padding: 15px;
  }

  .result-stats {
    gap: 15px;
  }

  .stat-item {
    width: 80px;
    height: 80px;
  }

  .stat-value {
    font-size: 1.5rem;
  }
}
</style>