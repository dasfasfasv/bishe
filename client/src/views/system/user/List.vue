<template>
  <div class="user-container">
    <el-card class="box-card">
      <!-- 搜索栏 -->
      <div class="search-form">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6">
            <el-input
              v-model="searchForm.username"
              placeholder="用户名"
              clearable
              prefix-icon="Search"
            />
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-select
              v-model="searchForm.departmentId"
              placeholder="选择部门"
              clearable
            >
              <el-option
                v-for="dept in departments"
                :key="dept.id"
                :label="dept.deptName"
                :value="dept.id"
              />
            </el-select>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-select
              v-model="searchForm.status"
              placeholder="选择状态"
              clearable
            >
              <el-option label="启用" value="1" />
              <el-option label="禁用" value="0" />
            </el-select>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
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
                新增
              </el-button>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 表格 -->
      <el-table
        :data="tableData"
        style="width: 100%"
        :loading="loading"
        stripe
        highlight-current-row
        class="user-table"
      >
        <el-table-column prop="id" label="用户ID" width="100" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="email" label="邮箱" width="150" />
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="departmentName" label="部门" width="120" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag type="info">{{ row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              active-value="1"
              inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="180">
          <template #default="{ row }">
            {{ formatTime(row.lastLoginTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="warning" size="small" link @click="handleResetPassword(row)">
              重置密码
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; text-align: right"
      />
    </el-card>

    <!-- 用户编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="formData.username"
            :disabled="isEdit"
            placeholder="请输入用户名"
          />
        </el-form-item>

        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="真实姓名" prop="realName">
          <el-input
            v-model="formData.realName"
            placeholder="请输入真实姓名"
          />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="formData.email"
            type="email"
            placeholder="请输入邮箱"
          />
        </el-form-item>

        <el-form-item label="电话" prop="phone">
          <el-input
            v-model="formData.phone"
            placeholder="请输入电话"
          />
        </el-form-item>

        <el-form-item label="部门" prop="departmentId">
          <el-select
            v-model="formData.departmentId"
            placeholder="选择部门"
          >
            <el-option
              v-for="dept in departments"
              :key="dept.id"
              :label="dept.deptName"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-select
            v-model="formData.role"
            placeholder="选择角色"
            multiple
          >
            <el-option label="系统管理员" value="ADMIN" />
            <el-option label="部门审批人" value="APPROVER" />
            <el-option label="保安" value="SECURITY" />
            <el-option label="运维人员" value="OPERATOR" />
          </el-select>
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
import { listUsers, addUser, updateUser, deleteUser, resetPassword, updateUserStatus } from '@/api/user'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const departments = ref([])

const searchForm = reactive({
  username: '',
  departmentId: '',
  status: ''
})

const formData = reactive({
  id: '',
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  departmentId: '',
  role: []
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请选择部门', trigger: 'change' }]
}

const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN')
}

const handleSearch = async () => {
  loading.value = true
  try {
    const params = {
      username: searchForm.username,
      departmentId: searchForm.departmentId
    }
    const res = await listUsers(params)
    tableData.value = res || []
    total.value = res.length
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.departmentId = ''
  searchForm.status = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  formData.id = row.id
  formData.username = row.username
  formData.realName = row.realName
  formData.email = row.email
  formData.phone = row.phone
  formData.departmentId = row.departmentId
  formData.role = row.role.split(',')
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true

    if (isEdit.value) {
      await updateUser({
        ...formData,
        role: formData.role.join(',')
      })
      ElMessage.success('更新成功')
    } else {
      await addUser({
        ...formData,
        role: formData.role.join(',')
      })
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    handleSearch()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleResetPassword = (row) => {
  ElMessageBox.confirm(
    `确定要重置用户 ${row.username} 的密码为 123456 吗?`,
    '重置密码',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await resetPassword(row.id)
      ElMessage.success('密码已重置为 123456')
    } catch (error) {
      ElMessage.error('重置失败')
    }
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除用户 ${row.username} 吗?`,
    '删除确认',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      handleSearch()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleStatusChange = async (row) => {
  try {
    await updateUserStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error('状态更新失败')
    row.status = row.status === '1' ? '0' : '1'
  }
}

const resetForm = () => {
  formData.id = ''
  formData.username = ''
  formData.password = ''
  formData.realName = ''
  formData.email = ''
  formData.phone = ''
  formData.departmentId = ''
  formData.role = []
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.user-container {
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

.user-table {
  font-size: 13px;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px 4px 0 0;
}

:deep(.el-dialog__title) {
  color: white;
}
</style>