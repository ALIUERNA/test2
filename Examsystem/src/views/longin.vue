<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>在线考试系统登录</h2>
      <el-form ref="loginForm" :model="form" :rules="rules">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="form.password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">登录</el-button>
          <el-button type="text" @click="$router.push('/change-password')">忘记密码？</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request';

export default {
  data() {
    return {
      form: {
        phone: '',
        password: ''
      },
      rules: {
        phone: [
          {required: true, message: '请输入手机号', trigger: 'blur'},
          {pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'}
        ]
      }
    };
  },
  methods: {
    submitForm() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          request.post('/api/user/login', this.form)
              .then(response => {
                // 清除旧缓存
                localStorage.clear();

                // 存储新登录状态
                localStorage.setItem('token', response.data.token);
                localStorage.setItem('userId', response.data.userId);
                localStorage.setItem('isAdmin', response.data.isAdmin || false);

                // 明确跳转到主页
                this.$router.push('/').then(() => {
                  window.location.reload(); // 强制刷新清除Vue状态缓存
                });
              })
              .catch(error => {
                this.$message.error('登录失败: ' + error.message);
                localStorage.clear(); // 失败时清除状态
              });
        }
      });
    }
  }
}

</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-card {
  width: 400px;
  padding: 20px;
}
</style>