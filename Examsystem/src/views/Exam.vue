<template>
  <div class="test-container">
    <el-card class="test-card">
      <h2>在线考试</h2>
      <div v-if="!submitted">
        <div v-for="(question, index) in questions" :key="index" class="question-item">
          <div class="question-title">{{ index + 1 }}. {{ question.content }}</div>
          <el-radio-group v-model="answers[index]">
            <el-radio label="A">{{ question.optionA }}</el-radio>
            <el-radio label="B">{{ question.optionB }}</el-radio>
            <el-radio label="C">{{ question.optionC }}</el-radio>
            <el-radio label="D">{{ question.optionD }}</el-radio>
          </el-radio-group>
        </div>
        <el-button type="primary" @click="submitTest">提交</el-button>
      </div>
      <div v-else class="result">
        <h3>考试结果</h3>
        <p>答对题目数量: {{ result.correctCount }}</p>
        <p>答错题目数量: {{ result.wrongCount }}</p>
        <p>得分: {{ result.score }}</p>
      </div>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request';

export default {
  data() {
    return {
      questions: [],
      answers: [],
      submitted: false,
      result: {
        correctCount: 0,
        wrongCount: 0,
        score: 0
      }
    };
  },
  created() {
    this.fetchQuestions();
  },
  methods: {
    fetchQuestions() {
      request.get('/api/question/random', {
        params: {
          count: 10
        }
      })
          .then(response => {
            this.questions = response.data;
          })
          .catch(error => {
            console.error('Fetch questions error:', error);
          });
    },
    submitTest() {
      const testRecord = {
        userId: localStorage.getItem('userId'),
        answers: this.answers
      };

      request.post('/api/test/submit', testRecord)
          .then(response => {
            this.submitted = true;
            this.result = response.data;
          })
          .catch(error => {
            console.error('Submit test error:', error);
          });
    }
  }
};
</script>

<style scoped>
.test-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.test-card {
  width: 800px;
  padding: 20px;
}

.question-item {
  margin-bottom: 20px;
}

.question-title {
  font-weight: bold;
  margin-bottom: 10px;
}

.result {
  text-align: center;
}
</style>