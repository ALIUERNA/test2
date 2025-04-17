<template>
  <div class="admin-register-container">
    <div class="register-card">
      <h2>管理员注册</h2>
      <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="rules"
          label-width="100px"
      >
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
          ></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              show-password
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button
              type="primary"
              size="large"
              @click="handleRegister"
              style="width: 100%"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request' // 引入 request.js

const router = useRouter()

// 表单数据
const registerForm = reactive({
  phone: '',
  username: '',
  password: '',
  confirmPassword: ''
})

// 表单引用
const registerFormRef = ref(null)

// 验证规则
const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
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

// 处理注册
const handleRegister = () => {
  registerFormRef.value.validate((valid) => {
    if (valid) {
      // 发送请求到后端的管理员注册接口
      request.post('/api/user/admin/register', {
        phone: registerForm.phone,
        username: registerForm.username,
        password: registerForm.password
      }).then(response => {
        // 注册成功
        ElMessage.success(response.message || '管理员注册成功！');

        // 存储 token 和用户信息
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('isAdmin', 'true');
        localStorage.setItem('phone', registerForm.phone);

        // 清空表单
        registerFormRef.value.resetFields();

        // 跳转到首页
        router.push('/');
      }).catch(error => {
        // 注册失败
        ElMessage.error(error.response?.data?.message || '注册失败，请稍后再试');
      });
    } else {
      ElMessage.error('请填写正确的信息');
      return false;
    }
  })
}
</script>

<style scoped>
.admin-register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.register-card {
  background-color: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 400px;
}

.register-card h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}
</style>