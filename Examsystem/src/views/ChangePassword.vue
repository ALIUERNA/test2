<template>
  <div class="change-password-container">
    <el-card class="change-password-card">
      <h2>修改密码</h2>
      <el-form ref="passwordForm" :model="form" :rules="rules">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input type="password" v-model="form.oldPassword" placeholder="请输入原密码"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="form.newPassword" placeholder="请输入新密码"></el-input>
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input type="password" v-model="form.confirmPassword" placeholder="请确认新密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request';

export default {
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.form.newPassword) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };

    return {
      form: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      rules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/, message: '密码必须包含大写字母、小写字母、数字和特殊字符，且长度不少于8位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    async submitForm() {
      try {
        const isValid = await new Promise((resolve) => {
          this.$refs.passwordForm.validate((valid) => {
            resolve(valid);
          });
        });

        if (isValid) {
          const { oldPassword, newPassword } = this.form;
          const token = localStorage.getItem('token')?.trim(); // 对获取的token去除前后空格
          const response = await request.post('/api/user/change-password', null, {
            headers: {
              Authorization: `Bearer ${token}`
            },
            params: {
              oldPassword,
              newPassword
            }
          });

          this.$message.success('密码修改成功');
          this.$router.push('/login');
        }
      } catch (error) {
        console.error('Change password error:', error);
        this.$message.error('密码修改失败，请检查输入或稍后重试');
      }
    }
  }
};
</script>

<style scoped>
.change-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.change-password-card {
  width: 400px;
  padding: 20px;
}
</style>