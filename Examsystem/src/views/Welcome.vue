<template>
  <div class="welcome-container">
    <div class="header">
      <h1>欢迎使用在线考试系统</h1>
      <p>一个高效、便捷的在线学习和考试平台</p>
      <!-- 登录后显示用户名 -->
      <div v-if="isLoggedIn" class="user-info">
        <el-icon><User /></el-icon>
        <span>欢迎您，{{ userPhone }}</span>
      </div>
    </div>

    <div class="main-content">
      <el-row :gutter="20">
        <el-col :span="8" v-for="(feature, index) in features" :key="index">
          <el-card class="feature-card">
            <div class="icon-wrapper">
              <el-icon :size="40" :color="feature.color">
                <component :is="feature.icon" />
              </el-icon>
            </div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.description }}</p>
          </el-card>
        </el-col>
      </el-row>

      <div class="action-area">
        <h2>开始使用</h2>
        <div class="buttons">
          <el-button
              v-if="!isLoggedIn"
              type="primary"
              size="large"
              @click="navigateToLogin"
          >
            立即登录
          </el-button>
          <el-button
              v-if="!isLoggedIn"
              size="large"
              @click="$router.push('/register')"
          >
            注册账号
          </el-button>
          <el-button
              v-if="isLoggedIn && !isAdmin"
              type="success"
              size="large"
              @click="$router.push('/exam')"
          >
            开始考试
          </el-button>
          <el-button
              v-if="isLoggedIn && isAdmin"
              type="warning"
              size="large"
              @click="$router.push('/admin/import-questions')"
          >
            管理题目
          </el-button>
          <!-- 添加注销按钮 -->
          <el-button
              v-if="isLoggedIn"
              type="info"
              size="large"
              @click="handleLogout"
          >
            退出登录
          </el-button>
        </div>
      </div>
    </div>

    <div class="footer">
      <p>© 2023 在线考试系统 | 技术支持: 您的团队</p>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  User,
  Notebook,
  Document,
  Setting,
  EditPen,
  CollectionTag
} from '@element-plus/icons-vue'

const router = useRouter()

// 用户手机号（从localStorage获取）
const userPhone = ref(localStorage.getItem('phone') || '')

// 检查登录状态（严格模式）
const isLoggedIn = computed(() => {
  const token = localStorage.getItem('token')
  return !!token // 严格布尔转换
})

const isAdmin = computed(() => localStorage.getItem('isAdmin') === 'true')

// 系统特性展示
const features = [
  {
    icon: User,
    title: '用户友好',
    description: '简洁直观的界面设计，操作简单易上手',
    color: '#409EFF'
  },
  {
    icon: Notebook,
    title: '多样化题目',
    description: '支持多种题型，丰富的题库资源',
    color: '#67C23A'
  },
  {
    icon: Document,
    title: '实时反馈',
    description: '考试结束后立即显示成绩和答案解析',
    color: '#E6A23C'
  },
  {
    icon: Setting,
    title: '智能管理',
    description: '管理员可轻松导入和管理考试题目',
    color: '#F56C6C'
  },
  {
    icon: EditPen,
    title: '安全可靠',
    description: '严格的权限控制和数据加密保护',
    color: '#909399'
  },
  {
    icon: CollectionTag,
    title: '学习记录',
    description: '完整保存您的考试历史和进步轨迹',
    color: '#B37FEB'
  }
]

// 导航到登录页（带错误处理）
const navigateToLogin = () => {
  try {
    router.push('/login')
  } catch (error) {
    console.error('路由跳转失败:', error)
  }
}

// 注销登录
const handleLogout = () => {
  localStorage.clear()
  window.location.reload() // 强制刷新页面以清除所有状态
}
</script>

<style scoped>
.welcome-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.header {
  text-align: center;
  padding: 40px 0;
  background: linear-gradient(135deg, #409EFF, #79BBFF);
  color: white;
  position: relative;
}

.header h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
}

.header p {
  font-size: 1.2rem;
  opacity: 0.9;
}

.user-info {
  position: absolute;
  top: 20px;
  right: 30px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1rem;
  background: rgba(255, 255, 255, 0.2);
  padding: 5px 15px;
  border-radius: 20px;
}

.main-content {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.feature-card {
  height: 100%;
  text-align: center;
  margin-bottom: 20px;
  transition: transform 0.3s;
  cursor: default;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.icon-wrapper {
  margin: 20px 0;
}

.feature-card h3 {
  margin: 15px 0;
  font-size: 1.3rem;
}

.feature-card p {
  color: #666;
  line-height: 1.6;
  padding: 0 10px;
}

.action-area {
  text-align: center;
  margin: 50px 0;
}

.action-area h2 {
  font-size: 1.8rem;
  margin-bottom: 30px;
  color: #333;
}

.buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

.footer {
  text-align: center;
  padding: 20px;
  background-color: #e4e7ed;
  color: #666;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .el-col {
    width: 100%;
    margin-bottom: 20px;
  }

  .buttons {
    flex-direction: column;
    align-items: center;
  }

  .user-info {
    position: static;
    justify-content: center;
    margin-top: 15px;
  }
}
</style>