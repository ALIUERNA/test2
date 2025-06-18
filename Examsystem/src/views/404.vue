<template>
  <div class="not-found-container">
    <div class="content">
      <h1>404</h1>
      <p class="title">页面不存在</p>
      <p class="description">您访问的页面不存在或已被移除</p>
      <el-button
          type="primary"
          size="large"
          @click="goHome"
          round
      >
        <el-icon><HomeFilled /></el-icon>
        返回首页
      </el-button>
    </div>
    <div class="illustration">
      <el-icon><Picture /></el-icon>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { HomeFilled, Picture } from '@element-plus/icons-vue';

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: space-around;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  padding: 20px;
}

.content {
  text-align: center;
  max-width: 500px;
}

.content h1 {
  font-size: 8rem;
  margin: 0;
  color: #409EFF;
  line-height: 1;
  font-weight: 700;
}

.title {
  font-size: 2rem;
  margin: 20px 0 10px;
  color: #333;
  font-weight: 500;
}

.description {
  font-size: 1.1rem;
  color: #666;
  margin-bottom: 30px;
}

.illustration {
  font-size: 300px;
  color: rgba(64, 158, 255, 0.2);
}

@media (max-width: 768px) {
  .not-found-container {
    flex-direction: column;
    justify-content: center;
    text-align: center;
  }

  .content {
    order: 2;
  }

  .illustration {
    order: 1;
    font-size: 200px;
    margin-bottom: 30px;
  }

  .content h1 {
    font-size: 5rem;
  }

  .title {
    font-size: 1.5rem;
  }
}
</style>