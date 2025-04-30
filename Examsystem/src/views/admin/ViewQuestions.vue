<template>
  <div class="container">
    <el-button type="primary" class="mb-4" @click="showAddDialog" round>
      <el-icon><Plus /></el-icon>
      新增题目
    </el-button>

    <el-table :data="tableData" border style="width: 100%" stripe>
      <el-table-column prop="content" label="题目内容" min-width="300"></el-table-column>
      <el-table-column prop="optionA" label="选项A" width="120"></el-table-column>
      <el-table-column prop="optionB" label="选项B" width="120"></el-table-column>
      <el-table-column prop="optionC" label="选项C" width="120"></el-table-column>
      <el-table-column prop="optionD" label="选项D" width="120"></el-table-column>
      <el-table-column prop="correctAnswer" label="正确答案" width="120"></el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="showEditDialog(row)" circle>
            <el-icon><Edit /></el-icon>
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)" circle>
            <el-icon><Delete /></el-icon>
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="50%">
      <el-form :model="formData" label-width="100px" label-position="top">
        <el-form-item label="题目内容" required>
          <el-input v-model="formData.content" type="textarea" :rows="3"></el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选项A" required>
              <el-input v-model="formData.optionA"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="选项B" required>
              <el-input v-model="formData.optionB"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="选项C">
              <el-input v-model="formData.optionC"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="选项D">
              <el-input v-model="formData.optionD"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="正确答案" required>
          <el-select v-model="formData.correctAnswer" placeholder="请选择正确答案" style="width: 100%">
            <el-option label="A" value="A"></el-option>
            <el-option label="B" value="B"></el-option>
            <el-option label="C" value="C"></el-option>
            <el-option label="D" value="D"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import request from '@/utils/request'
import {ElMessage} from "element-plus"
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

onMounted(() => {
  fetchData()
})

const fetchData = async () => {
  try {
    const response = await request.get('/api/question/list', {
      params: {
        page: pagination.value.current,
        pageSize: pagination.value.pageSize
      }
    })
    tableData.value = response.data.records
    pagination.value.total = response.data.total
  } catch (error) {
    ElMessage.error('获取题目列表失败')
  }
}

const tableData = ref([])
const pagination = ref({ current: 1, size: 10, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增题目')
const formData = reactive({
  id: null,
  content: '',
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  correctAnswer: ''
})

const showAddDialog = () => {
  dialogTitle.value = '新增题目'
  resetForm()
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  dialogTitle.value = '编辑题目'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    const url = formData.id ? '/api/question/update' : '/api/question/add';
    const method = formData.id ? 'put' : 'post';
    await request[method](url, formData);
    dialogVisible.value = false;
    await fetchData();
    ElMessage.success('操作成功');
  } catch (error) {
    ElMessage.error('操作失败');
  }
}

const handleDelete = async (id) => {
  try {
    await request.delete(`/api/question/delete/${id}`)
    fetchData()
    ElMessage.success('删除成功')
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    content: '',
    optionA: '',
    optionB: '',
    optionC: '',
    optionD: '',
    correctAnswer: ''
  })
}
</script>

<style scoped>
.container {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  min-height: 100vh;
}

.mb-4 {
  margin-bottom: 20px;
}

:deep(.el-table) {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

:deep(.el-table th) {
  background-color: #f8f8f9;
  color: #515a6e;
  font-weight: 600;
}

:deep(.el-dialog) {
  border-radius: 12px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

@media (max-width: 768px) {
  :deep(.el-dialog) {
    width: 90% !important;
  }

  :deep(.el-table-column) {
    width: auto !important;
  }
}
</style>