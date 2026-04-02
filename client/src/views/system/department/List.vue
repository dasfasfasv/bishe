<template>
  <div class="department-container">
    <el-card class="box-card">
      <!-- 搜索和操作 -->
      <div class="search-form">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="12">
            <el-input
              v-model="searchForm.deptName"
              placeholder="部门名称"
              clearable
              prefix-icon="Search"
            />
          </el-col>
          <el-col :xs="24" :sm="12" :md="12">
            <div class="button-group">
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>
                查询
              </el-button>
              <el-button @click="handleReset">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
              <el-button type="success" @click="handleAdd">
                <el-icon><Plus /></el-icon>
                新增部门
              </el-button>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 树形表格 -->
      <el-table
        :data="tableData"
        style="width: 100%"
        :loading="loading"
        row-key="id"
        default-expand-all
        highlight-current-row
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="deptName" label="部门名称" width="200" />
        <el-table-column prop="deptCode" label="部门代码" width="150" />
        <el-table-column prop="leader" label="负责人" width="120" />
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '1' ? 'success' : 'danger'">
              {{ row.status === '1' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑部门' : '新增部门'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="部门名称" prop="deptName">
          <el-input
            v-model="formData.deptName"
            placeholder="请输入部门名称"
          />
        </el-form-item>

        <el-form-item label="部门代码" prop="deptCode">
          <el-input
            v-model="formData.deptCode"
            placeholder="请输入部门代码"
          />
        </el-form-item>

        <el-form-item label="父部门" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="treeData"
            node-key="id"
            props="{ children: 'children', label: 'deptName' }"
            placeholder="选择父部门"
            clearable
            check-strictly
          />
        </el-form-item>

        <el-form-item label="负责人" prop="leader">
          <el-input
            v-model="formData.leader"
            placeholder="请输入负责人"
          />
        </el-form-item>

        <el-form-item label="电话" prop="phone">
          <el-input
            v-model="formData.phone"
            placeholder="请输入电话"
          />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="formData.email"
            type="email"
            placeholder="请输入邮箱"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const treeData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  deptName: ''
})

const formData = reactive({
  id: '',
  deptName: '',
  deptCode: '',
  parentId: null,
  leader: '',
  phone: '',
  email: '',
  status: '1'
})

const rules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  deptCode: [{ required: true, message: '请输入部门代码', trigger: 'blur' }]
}

// 模拟数据获取
const mockDepartments = [
  { id: 1, deptName: '管理部', deptCode: 'ADMIN', parentId: null, leader: '张三', phone: '010-8888001', email: 'admin@company.com', status: '1' },
  { id: 2, deptName: '人事部', deptCode: 'HR', parentId: 1, leader: '李四', phone: '010-8888002', email: 'hr@company.com', status: '1' },
  { id: 3, deptName: '市场部', deptCode: 'MARKET', parentId: 1, leader: '王五', phone: '010-8888003', email: 'market@company.com', status: '1' },
  { id: 4, deptName: '技术部', deptCode: 'TECH', parentId: 1, leader: '赵六', phone: '010-8888004', email: 'tech@company.com', status: '1' },
  { id: 6, deptName: '运维部', deptCode: 'OPS', parentId: 4, leader: '周八', phone: '010-8888006', email: 'ops@company.com', status: '1' }
]

const buildTree = (data, parentId = null) => {
  return data
    .filter(item => item.parentId === parentId)
    .map(item => ({
      ...item,
      children: buildTree(data, item.id)
    }))
}

const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    if (searchForm.deptName) {
      tableData.value = mockDepartments.filter(d => d.deptName.includes(searchForm.deptName))
    } else {
      tableData.value = buildTree(mockDepartments)
    }
    treeData.value = buildTree(mockDepartments)
    loading.value = false
  }, 500)
}

const handleReset = () => {
  searchForm.deptName = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  formData.id = ''
  formData.deptName = ''
  formData.deptCode = ''
  formData.parentId = null
  formData.leader = ''
  formData.phone = ''
  formData.email = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  formData.id = row.id
  formData.deptName = row.deptName
  formData.deptCode = row.deptCode
  formData.parentId = row.parentId
  formData.leader = row.leader
  formData.phone = row.phone
  formData.email = row.email
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    // 模拟提交
    setTimeout(() => {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      handleSearch()
      submitLoading.value = false
    }, 500)
  } catch (error) {
    console.error('验证失败', error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除部门 ${row.deptName} 吗?`,
    '删除确认',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(() => {
    ElMessage.success('删除成功')
    handleSearch()
  }).catch(() => {})
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.department-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.button-group {
  display: flex;
  gap: 10px;
  width: 100%;
}

.button-group :deep(.el-button) {
  flex: 1;
}
</style>