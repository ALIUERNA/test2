<template>
  <div class="register-container">
    <el-card class="register-card" shadow="hover">
      <h2>用户注册</h2>
      <el-form ref="registerForm" :model="form" :rules="rules" label-position="top">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号">
            <template #prefix>
              <el-icon><Iphone /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名">
            <template #prefix>
              <el-icon><Avatar /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm" size="large" round>
            立即注册
          </el-button>
        </el-form-item>
        <div class="login-link">
          已有账号？<el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request.js';
import { Iphone, User, Avatar, Message } from '@element-plus/icons-vue';

export default {
  components: { Iphone, User, Avatar, Message },
  data() {
    return {
      form: {
        phone: '',
        idCard: '',
        name: '',
        email: '',
        password: ''
      },
      rules: {
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        idCard: [
          { required: true, message: '请输入身份证号', trigger: 'blur' },
          { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])\d{3}[0-9Xx]$/,
            message: '身份证号格式不正确',
            trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    submitForm() {
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          this.form.password = this.form.idCard.slice(-6);

          request.post('/api/user/register', this.form)
              .then(response => {
                if (response.code === "200") {
                  this.$message.success('注册成功，请登录');
                  this.$router.push('/login');
                } else {
                  this.$message.error(response.message || '注册失败');
                }
              })
              .catch(error => {
                this.$message.error(error.message || '注册失败');
              });
        }
      });
    }
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  padding: 20px;
}

.register-card {
  width: 500px;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.register-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 1.8rem;
  font-weight: 600;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #606266;
  font-size: 0.95rem;
}

:deep(.el-input__prefix) {
  display: flex;
  align-items: center;
  padding: 0 10px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  padding-bottom: 0;
}

@media (max-width: 600px) {
  .register-card {
    width: 90%;
    padding: 30px 20px;
  }
}
</style>