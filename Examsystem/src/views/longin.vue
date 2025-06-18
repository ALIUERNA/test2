<template>
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <h2>用户登录</h2>
      <div class="logo">
        <el-icon><UserFilled /></el-icon>
      </div>
      <el-form ref="loginForm" :model="form" :rules="rules" label-position="top">
        <el-form-item label="手机号" prop="phone">
          <el-input
              v-model="form.phone"
              placeholder="请输入手机号"
              size="large"
          >
            <template #prefix>
              <el-icon><Iphone /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
              type="password"
              v-model="form.password"
              placeholder="请输入密码"
              size="large"
              show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button
              type="primary"
              @click="submitForm"
              size="large"
              style="width: 100%"
              round
          >
            登录
          </el-button>
        </el-form-item>
        <div class="footer-links">
          <el-link type="info" @click="$router.push('/register')">注册账号</el-link>
          <el-link type="primary" @click="$router.push('/change-password')">忘记密码？</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Iphone, Lock, UserFilled } from '@element-plus/icons-vue'
import request from '@/utils/request';

const router = useRouter()
const loginForm = ref(null)
const form = reactive({
  phone: '',
  password: ''
})

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const submitForm = () => {
  loginForm.value.validate((valid) => {
    if (valid) {
      request.post('/api/user/login', form)
          .then(response => {
            if (response && response.data && response.data.token) {
              localStorage.clear();
              const token = response.data.token.trim();
              localStorage.setItem('token', token);
              localStorage.setItem('userId', response.data.userId);
              localStorage.setItem('isAdmin', response.data.isAdmin || false);
              localStorage.setItem('phone', form.phone);

              router.push('/').then(() => {
                window.location.reload();
              });
            } else {
              ElMessage.error('登录失败：未获取到有效的 token');
              localStorage.clear();
            }
          })
          .catch(error => {
            ElMessage.error('登录失败: ' + (error.message || '请检查用户名和密码'));
            localStorage.clear();
          });
    }
  });
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  padding: 20px;
}

.login-card {
  background-color: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  width: 450px;
  text-align: center;
}

.login-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 1.8rem;
  font-weight: 600;
}

.logo {
  width: 80px;
  height: 80px;
  margin: 0 auto 30px;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 40px;
}

.footer-links {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  padding-bottom: 0;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

@media (max-width: 500px) {
  .login-card {
    width: 90%;
    padding: 30px 20px;
  }
}
</style>