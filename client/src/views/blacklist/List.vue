<template>
  <div class="blacklist-container">
    <div class="search-form">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select
            v-model="searchForm.type"
            placeholder="选择类型"
            clearable
          >
            <el-option label="永久" value="1" />
            <el-option label="临时" value="2" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleSearch">
            查询
          </el-button>
          <el-button @click="handleReset">
            重置
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="success" @click="handleAdd">
            添加黑名单
          </el-button>
        </el-col>
      </el-row>
    </div>

    <div class="table-container">
      <el-table
        :data="tableData"
        style="width: 100%"
        :loading="loading"
        stripe
        highlight-current-row
      >
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="idCard" label="身份证号" width="150" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === '1' ? 'danger' : 'warning'">
              {{ row.type === '1' ? '永久' : '临时' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="原因" width="200" />
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template #default="{ row }">
            {{ row.type === '2' ? formatDate(row.endTime) : '永久' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="danger"
              size="small"
              link
              @click="handleRemove(row)"
            >
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 添加黑名单对话框 -->
    <el-dialog
      v-model="formDialogVisible"
      title="添加黑名单"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="formData.type">
            <el-radio value="1">永久</el-radio>
            <el-radio value="2">临时</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="formData.type === '2'" label="结��时间" prop="endTime">
          <el-date-picker
            v-model="formData.endTime"
            type="datetime"
            placeholder="选择结束时间"
          />
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input
            v-model="formData.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入添加原因"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">
          添加
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listBlacklist, addBlacklist, removeBlacklist } from '@/api/blacklist'

const loading = ref(false)
const tableData = ref([])
const formDialogVisible = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  type: ''
})

const formData = reactive({
  name: '',
  idCard: '',
  type: '1',
  reason: '',
  endTime: null
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入原因', trigger: 'blur' }]
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const handleSearch = async () => {
  loading.value = true
  try {
    const params = {}
    if (searchForm.type) params.type = searchForm.type

    const res = await listBlacklist(params)
    tableData.value = res || []
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.type = ''
  handleSearch()
}

const handleAdd = () => {
  formData.name = ''
  formData.idCard = ''
  formData.type = '1'
  formData.reason = ''
  formData.endTime = null
  formDialogVisible.value = true
}

const handleRemove = (row) => {
  ElMessageBox.confirm('确定要移除此黑名单吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await removeBlacklist(row.id)
      ElMessage.success('移除成功')
      handleSearch()
    } catch (error) {
      ElMessage.error('移除失败')
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    await addBlacklist(formData)
    ElMessage.success('添加成功')
    formDialogVisible.value = false
    handleSearch()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.blacklist-container {
  background: white;
  padding: 20px;
  border-radius: 4px;
}

.search-form {
  margin-bottom: 20px;
}

.table-container {
  margin-top: 20px;
}
</style>