<template>
  <div class="import-questions-container">
    <el-card class="import-card" shadow="hover">
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
            只能上传 Excel 文件（.xlsx 格式），且不超过5MB
          </div>
        </template>
      </el-upload>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'

const token = localStorage.getItem('token')
const headers = { Authorization: `Bearer ${token}` }
const uploadUrl = 'http://localhost:8080/api/question/import'

const handleSuccess = (response) => {
  if (response.code === '200') {
    ElMessage.success('试卷导入成功！')
  } else {
    ElMessage.error(response.msg || '导入失败')
  }
}

const handleError = () => {
  ElMessage.error('导入失败，请稍后再试')
}

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
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  padding: 20px;
}

.import-card {
  background-color: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  width: 500px;
  text-align: center;
}

.import-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 1.8rem;
  font-weight: 600;
}

:deep(.upload-demo) {
  width: 100%;
}

:deep(.el-upload) {
  width: 100%;
}

:deep(.el-upload-dragger) {
  width: 100%;
  padding: 40px;
  border-radius: 8px;
  background-color: rgba(245, 247, 250, 0.8);
  border: 2px dashed #dcdfe6;
  transition: all 0.3s;
}

:deep(.el-upload-dragger:hover) {
  border-color: #409EFF;
  background-color: rgba(236, 245, 255, 0.6);
}

.el-icon--upload {
  font-size: 60px;
  color: #409EFF;
  margin-bottom: 20px;
}

.el-upload__text {
  font-size: 1.1rem;
  color: #606266;
  margin-bottom: 10px;
}

.el-upload__tip {
  margin-top: 15px;
  color: #909399;
  font-size: 0.9rem;
}

@media (max-width: 600px) {
  .import-card {
    width: 90%;
    padding: 30px 20px;
  }
}
</style>