<template>
  <div class="import-questions-container">
    <div class="import-card">
      <h2>试卷导入</h2>
      <el-upload
          class="upload-demo"
          drag
          :action="uploadUrl"
          :headers="headers"
          :on-success="handleSuccess"
          :on-error="handleError"
          :before-upload="beforeUpload"
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到此处或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 Excel 文件（.xlsx 格式）
          </div>
        </template>
      </el-upload>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'

// 获取 token
const token = localStorage.getItem('token')
const headers = { Authorization: `Bearer ${token}` }

// 上传 URL
const uploadUrl = 'http://localhost:8080/api/question/import'

// 处理上传成功
const handleSuccess = (response) => {
  if (response.code === '200') {
    ElMessage.success('试卷导入成功！')
    // 跳转到其他页面或刷新
  } else {
    ElMessage.error(response.msg || '导入失败')
  }
}

// 处理上传失败
const handleError = () => {
  ElMessage.error('导入失败，请稍后再试')
}

// 文件上传前的验证
const beforeUpload = (file) => {
  const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isExcel) {
    ElMessage.error('只能上传 Excel 文件（.xlsx 格式）')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('文件大小不能超过 5MB')
    return false
  }
  return true
}
</script>

<style scoped>
.import-questions-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.import-card {
  background-color: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 400px;
}

.import-card h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}
</style>