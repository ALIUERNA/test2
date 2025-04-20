<template>
  <div class="admin-register-container">
    <div class="register-card">
      <h2>管理员注册</h2>
      <div class="logo">
        <el-icon><UserFilled /></el-icon>
      </div>
      <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="rules"
          label-position="top"
      >
        <el-form-item label="手机号" prop="phone">
          <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号"
              size="large"
          >
            <template #prefix>
              <el-icon><Iphone /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名"
              size="large"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
              size="large"
          >
            <template #prefix>
              <el-icon><Key /></el-icon>
            </template>
          </el-input>
          <div class="password-strength" :class="passwordStrengthClass">
            {{ passwordStrengthText }}
          </div>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
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
              @click="handleRegister"
              style="width: 100%"
              round
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="footer-links">
        <el-link type="info" @click="$router.push('/admin/login')">已有账号？立即登录</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Iphone, User, Key, UserFilled } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()

const registerForm = reactive({
  phone: '',
  username: '',
  password: '',
  confirmPassword: ''
})

const registerFormRef = ref(null)

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 16, message: '长度在3到16个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 16, message: '长度在8到16个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/,
      message: '必须包含大小写字母、数字和特殊字符',
      trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const passwordStrength = computed(() => {
  if (!registerForm.password) return 0;
  let strength = 0;

  if (registerForm.password.length >= 8) strength += 1;
  if (registerForm.password.length >= 12) strength += 1;

  if (/[A-Z]/.test(registerForm.password)) strength += 1;
  if (/[a-z]/.test(registerForm.password)) strength += 1;
  if (/\d/.test(registerForm.password)) strength += 1;
  if (/[^A-Za-z0-9]/.test(registerForm.password)) strength += 1;

  return Math.min(strength, 5);
})

const passwordStrengthText = computed(() => {
  const texts = ['非常弱', '弱', '中等', '强', '非常强', '极强'];
  return texts[passwordStrength.value] || '';
})

const passwordStrengthClass = computed(() => {
  const classes = ['strength-0', 'strength-1', 'strength-2', 'strength-3', 'strength-4', 'strength-5'];
  return classes[passwordStrength.value] || '';
})

const handleRegister = () => {
  registerFormRef.value.validate((valid) => {
    if (valid) {
      request.post('/api/user/admin/register', {
        phone: registerForm.phone,
        username: registerForm.username,
        password: registerForm.password
      }).then(response => {
        ElMessage.success(response.message || '管理员注册成功！')
        router.push('/admin/login')
      }).catch(error => {
        ElMessage.error(error.message || '注册失败，请稍后再试')
      })
    }
  })
}
</script>

<style scoped>
.admin-register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  padding: 20px;
}

.register-card {
  background-color: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  width: 450px;
  text-align: center;
}

.register-card h2 {
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
  text-align: center;
}

.password-strength {
  margin-top: 8px;
  height: 6px;
  border-radius: 3px;
  transition: all 0.3s;
}

.strength-0 {
  width: 20%;
  background-color: #ff4d4f;
}

.strength-1 {
  width: 30%;
  background-color: #ff7d45;
}

.strength-2 {
  width: 50%;
  background-color: #ffa940;
}

.strength-3 {
  width: 70%;
  background-color: #ffc53d;
}

.strength-4 {
  width: 85%;
  background-color: #73d13d;
}

.strength-5 {
  width: 100%;
  background-color: #52c41a;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  padding-bottom: 0;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

@media (max-width: 500px) {
  .register-card {
    width: 90%;
    padding: 30px 20px;
  }
}
</style>