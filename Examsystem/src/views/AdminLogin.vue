<template>
  <div class="admin-login-container">
    <div class="login-card">
      <h2>管理员登录</h2>
      <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="rules"
          label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button
              type="primary"
              size="large"
              @click="handleLogin"
              style="width: 100%"
          >
            登录
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
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单引用
const loginFormRef = ref(null)

// 验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = () => {
  loginFormRef.value.validate((valid) => {
    if (valid) {
      // 发送请求到后端的管理员登录接口
      request.post('/api/user/admin/login', {
        username: loginForm.username,
        password: loginForm.password
      }).then(response => {
        // 登录成功
        ElMessage.success(response.message || '管理员登录成功！');

        // 存储 token 和用户信息
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('isAdmin', 'true');
        localStorage.setItem('phone', loginForm.username);

        // 清空表单
        loginFormRef.value.resetFields();

        // 跳转到首页
        router.push('/');
      }).catch(error => {
        // 登录失败
        ElMessage.error(error.response?.data?.message || '登录失败，请稍后再试');
      });
    } else {
      ElMessage.error('请填写正确的信息');
      return false;
    }
  })
}
</script>

<style scoped>
.admin-login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.login-card {
  background-color: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 400px;
}

.login-card h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}
</style>