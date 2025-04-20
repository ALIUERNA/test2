<template>
  <div class="admin-login-container">
    <div class="login-card" shadow="hover">
      <h2>管理员登录</h2>
      <div class="logo">
        <el-icon><Lock /></el-icon>
      </div>
      <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="rules"
          label-position="top"
      >
        <el-form-item label="手机号" prop="phone">
          <el-input
              v-model="loginForm.phone"
              placeholder="请输入注册手机号"
              size="large"
          >
            <template #prefix>
              <el-icon><Iphone /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
              size="large"
          >
            <template #prefix>
              <el-icon><Key /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              size="large"
              @click="handleLogin"
              style="width: 100%"
              round
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="footer-links">
        <el-link type="info" @click="$router.push('/login')">普通用户登录</el-link>
        <el-link type="primary" @click="$router.push('/admin/register')">注册管理员账号</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Iphone, Key, Lock } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()

const loginForm = reactive({
  phone: '',
  password: ''
})

const loginFormRef = ref(null)

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入有效的手机号码',
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '长度在6到16个字符', trigger: 'blur' }
  ]
}

const handleLogin = () => {
  loginFormRef.value.validate((valid) => {
    if (valid) {
      request.post('/api/user/admin/login', {
        phone: loginForm.phone,
        password: loginForm.password
      }).then(response => {
        ElMessage.success(response.message || '管理员登录成功！')

        localStorage.setItem('token', response.data.token)
        localStorage.setItem('isAdmin', 'true')
        localStorage.setItem('phone', loginForm.phone)

        loginFormRef.value.resetFields()
        router.push('/')
      }).catch(error => {
        ElMessage.error(error.message || '登录失败，请稍后再试')
      })
    } else {
      ElMessage.error('请填写正确的信息')
    }
  })
}
</script>

<style scoped>
.admin-login-container {
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
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
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