<template>
  <div class="welcome-container">
    <div class="header">
      <h1>欢迎使用在线考试系统</h1>
      <p>一个高效、便捷的在线学习和考试平台</p>
      <div v-if="isLoggedIn" class="user-info">
        <el-icon><User /></el-icon>
        <span>欢迎您，{{ userPhone }}</span>
      </div>
    </div>

    <div class="main-content">
      <el-row :gutter="20">
        <el-col :span="8" v-for="(feature, index) in features" :key="index">
          <el-card class="feature-card" shadow="hover">
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
              round
          >
            立即登录
          </el-button>
          <el-button
              v-if="!isLoggedIn"
              size="large"
              @click="$router.push('/register')"
              round
          >
            注册账号
          </el-button>
          <el-button
              v-if="!isLoggedIn"
              size="large"
              @click="$router.push('/admin/register')"
              round
          >
            管理员注册
          </el-button>
          <el-button
              v-if="!isLoggedIn"
              size="large"
              @click="$router.push('/admin/login')"
              round
          >
            管理员登录
          </el-button>
          <el-button
              v-if="isLoggedIn && !isAdmin"
              type="success"
              size="large"
              @click="$router.push('/change-password')"
              round
          >
            修改密码
          </el-button>
          <el-button
              v-if="isLoggedIn && !isAdmin"
              type="success"
              size="large"
              @click="$router.push('/exam')"
              round
          >
            开始考试
          </el-button>
          <el-button
              v-if="isLoggedIn && !isAdmin"
              type="success"
              size="large"
              @click="$router.push('/test-records')"
              round
          >
            测试记录
          </el-button>
          <el-button
              v-if="isLoggedIn && !isAdmin"
              type="info"
              size="large"
              @click="handleLogout"
              round
          >
            退出登录
          </el-button>
          <el-button
              v-if="isLoggedIn && isAdmin"
              type="warning"
              size="large"
              @click="$router.push('/admin/import-questions')"
              round
          >
            试卷导入
          </el-button>
          <el-button
              v-if="isLoggedIn && isAdmin"
              type="warning"
              size="large"
              @click="$router.push('/admin/view-questions')"
              round
          >
            查看试题
          </el-button>
          <el-button
              v-if="isLoggedIn && isAdmin"
              type="info"
              size="large"
              @click="handleLogout"
              round
          >
            退出登录
          </el-button>
          <el-button
              v-if="isLoggedIn && isAdmin"
              type="primary"
              size="large"
              @click="$router.push('/test-records')"
              round
          >
            查看成绩记录
          </el-button>
          <el-button
              v-if="isLoggedIn && isAdmin"
              type="success"
              size="large"
              @click="$router.push('/change-password')"
              round
          >
            修改密码
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
const userPhone = ref(localStorage.getItem('phone') || '')

const isLoggedIn = computed(() => {
  const token = localStorage.getItem('token')
  return !!token
})

const isAdmin = computed(() => localStorage.getItem('isAdmin') === 'true')

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

const navigateToLogin = () => {
  try {
    router.push('/login')
  } catch (error) {
    console.error('路由跳转失败:', error)
  }
}

const handleLogout = () => {
  localStorage.clear()
  window.location.reload()
}
</script>

<style scoped>
.welcome-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
}

.header {
  text-align: center;
  padding: 60px 0;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  color: white;
  position: relative;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.header h1 {
  font-size: 2.8rem;
  margin-bottom: 15px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header p {
  font-size: 1.3rem;
  opacity: 0.9;
  margin-bottom: 0;
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
  padding: 8px 18px;
  border-radius: 30px;
  backdrop-filter: blur(5px);
}

.main-content {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  width: 100%;
}

.feature-card {
  height: 100%;
  text-align: center;
  margin-bottom: 20px;
  transition: all 0.3s ease;
  border: none;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.9);
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

.icon-wrapper {
  margin: 25px 0;
}

.feature-card h3 {
  margin: 15px 0;
  font-size: 1.4rem;
  color: #333;
}

.feature-card p {
  color: #666;
  line-height: 1.7;
  padding: 0 15px;
  font-size: 0.95rem;
}

.action-area {
  text-align: center;
  margin: 60px 0 40px;
  padding: 30px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

.action-area h2 {
  font-size: 2rem;
  margin-bottom: 30px;
  color: #333;
  font-weight: 600;
}

.buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

.footer {
  text-align: center;
  padding: 25px;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.95rem;
}

@media (max-width: 768px) {
  .el-col {
    width: 100%;
    margin-bottom: 20px;
  }

  .header h1 {
    font-size: 2rem;
  }

  .header p {
    font-size: 1.1rem;
  }

  .user-info {
    position: static;
    justify-content: center;
    margin: 20px auto 0;
    width: fit-content;
  }

  .buttons {
    flex-direction: column;
    align-items: center;
  }

  .action-area {
    margin: 40px 0;
    padding: 20px;
  }
}
</style>