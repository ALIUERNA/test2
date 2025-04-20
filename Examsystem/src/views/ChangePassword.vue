<template>
  <div class="change-password-container">
    <el-card class="change-password-card" shadow="hover">
      <h2>修改密码</h2>
      <el-form ref="passwordForm" :model="form" :rules="rules" label-position="top">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
              type="password"
              v-model="form.oldPassword"
              placeholder="请输入原密码"
              show-password
          ></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
              type="password"
              v-model="form.newPassword"
              placeholder="请输入新密码"
              show-password
          ></el-input>
          <div class="password-strength" :class="passwordStrengthClass">
            {{ passwordStrengthText }}
          </div>
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
              type="password"
              v-model="form.confirmPassword"
              placeholder="请确认新密码"
              show-password
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm" size="large" round>
            确认修改
          </el-button>
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
          { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/,
            message: '密码必须包含大小写字母、数字和特殊字符',
            trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    passwordStrength() {
      if (!this.form.newPassword) return 0;
      let strength = 0;

      // 长度评分
      if (this.form.newPassword.length >= 8) strength += 1;
      if (this.form.newPassword.length >= 12) strength += 1;

      // 复杂度评分
      if (/[A-Z]/.test(this.form.newPassword)) strength += 1;
      if (/[a-z]/.test(this.form.newPassword)) strength += 1;
      if (/\d/.test(this.form.newPassword)) strength += 1;
      if (/[^A-Za-z0-9]/.test(this.form.newPassword)) strength += 1;

      return Math.min(strength, 5);
    },
    passwordStrengthText() {
      const texts = ['非常弱', '弱', '中等', '强', '非常强', '极强'];
      return texts[this.passwordStrength] || '';
    },
    passwordStrengthClass() {
      const classes = ['strength-0', 'strength-1', 'strength-2', 'strength-3', 'strength-4', 'strength-5'];
      return classes[this.passwordStrength] || '';
    }
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
          const token = localStorage.getItem('token')?.trim();
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
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  padding: 20px;
}

.change-password-card {
  width: 450px;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.change-password-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 1.8rem;
  font-weight: 600;
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
  .change-password-card {
    width: 90%;
    padding: 20px;
  }
}
</style>