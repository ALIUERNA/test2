<template>
  <div class="test-records-container">
    <h2 class="title">
      {{ isAdmin ? "所有用户考试成绩记录" : "我的考试成绩记录" }}
    </h2>

    <div class="filter-container">
      <el-input
          v-model="searchText"
          placeholder="搜索考试记录"
          class="search-input"
          clearable
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-select
          v-model="sortField"
          placeholder="排序方式"
          class="sort-select"
      >
        <el-option label="按时间排序" value="time" />
        <el-option label="按成绩排序" value="score" />
      </el-select>
    </div>

    <el-table
        :data="filteredRecords"
        stripe
        style="width: 100%"
        v-loading="loading"
        element-loading-text="加载中..."
        class="record-table"
    >
      <!-- 管理员视图额外字段 -->
      <el-table-column
          v-if="isAdmin"
          prop="userName"
          label="用户名"
      />
      <el-table-column
          v-if="isAdmin"
          prop="userPhone"
          label="手机号"
      />

      <el-table-column prop="testTime" label="考试时间" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.testTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="correctCount" label="答对题数" />
      <el-table-column prop="wrongCount" label="答错题数" />
      <el-table-column prop="score" label="得分">
        <template #default="scope">
          <el-tag :type="getScoreTagType(scope.row.score)">
            {{ scope.row.score.toFixed(2) }} 分
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button
              type="primary"
              size="small"
              @click="viewDetails(scope.row)"
          >
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
        v-model="detailDialogVisible"
        title="考试详情"
        width="50%"
    >
      <div v-if="selectedRecord">
        <h3>考试时间: {{ formatDateTime(selectedRecord.testTime) }}</h3>

        <!-- 管理员视图显示用户信息 -->
        <div v-if="isAdmin" class="user-info">
          <p><strong>用户名:</strong> {{ selectedRecord.userName }}</p>
          <p><strong>手机号:</strong> {{ selectedRecord.userPhone }}</p>
        </div>

        <div class="score-summary">
          <el-statistic title="总分" :value="selectedRecord.score" precision="2">
            <template #suffix>分</template>
          </el-statistic>
          <el-statistic title="答对题数" :value="selectedRecord.correctCount" />
          <el-statistic title="答错题数" :value="selectedRecord.wrongCount" />
        </div>

        <div class="recommendation">
          <h4>学习建议:</h4>
          <p v-if="selectedRecord.score >= 90">恭喜你取得了优异的成绩！继续保持！</p>
          <p v-else-if="selectedRecord.score >= 70">
            表现良好，建议复习错题部分，提升准确率。
          </p>
          <p v-else>
            需要加强学习，建议重新学习相关知识点并多做练习。
          </p>
        </div>
      </div>
    </el-dialog>

    <div class="no-data" v-if="!loading && filteredRecords.length === 0">
      <el-empty :description="isAdmin ? '暂无考试记录' : '您暂无考试记录'" />
      <el-button
          v-if="!isAdmin"
          type="primary"
          @click="goToExam"
      >
        去考试
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

const router = useRouter()
const testRecords = ref([])
const loading = ref(true)
const searchText = ref('')
const sortField = ref('time')
const detailDialogVisible = ref(false)
const selectedRecord = ref(null)

// 判断是否是管理员
const isAdmin = computed(() => localStorage.getItem('isAdmin') === 'true')

onMounted(() => {
  loadTestRecords()
})

const loadTestRecords = async () => {
  try {
    loading.value = true
    // 根据用户角色选择不同接口
    const endpoint = isAdmin.value
        ? '/api/test/admin/all-records'
        : '/api/test/records'

    const response = await request.get(endpoint)

    // 处理不同数据结构
    if (isAdmin.value) {
      // 管理员接口返回的是分组数据，需要展平
      testRecords.value = response.data.flatMap(user =>
          user.records.map(record => ({
            ...record,
            userId: user.userId,
            userName: user.userName,
            userPhone: user.userPhone
          }))
      )
    } else {
      // 普通用户接口
      testRecords.value = response.data.records || []
    }
  } catch (error) {
    console.error('获取考试记录失败:', error)
  } finally {
    loading.value = false
  }
}

const filteredRecords = computed(() => {
  let records = [...testRecords.value]

  // 搜索过滤
  if (searchText.value) {
    const search = searchText.value.toLowerCase()
    records = records.filter(record =>
        (record.score.toString().includes(search)) ||
        (record.testTime && record.testTime.toLowerCase().includes(search)) ||
        (isAdmin.value && record.userName && record.userName.toLowerCase().includes(search)) ||
        (isAdmin.value && record.userPhone && record.userPhone.includes(search))
    )
  }

  // 排序
  if (sortField.value === 'score') {
    return records.sort((a, b) => b.score - a.score)
  } else {
    return records.sort((a, b) =>
        new Date(b.testTime) - new Date(a.testTime)
    )
  }
})

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  return date.toLocaleString()
}

const getScoreTagType = (score) => {
  if (score >= 90) return 'success'
  if (score >= 70) return 'warning'
  return 'danger'
}

const viewDetails = (record) => {
  selectedRecord.value = record
  detailDialogVisible.value = true
}

const goToExam = () => {
  router.push('/exam')
}
</script>

<style scoped>
.test-records-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.filter-container {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
}

.sort-select {
  width: 150px;
}

.record-table {
  margin-top: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.score-summary {
  display: flex;
  justify-content: space-around;
  margin: 20px 0;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.user-info {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #ff0000;
}
</style>
