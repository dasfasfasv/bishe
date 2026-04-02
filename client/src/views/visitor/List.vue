<template>
  <div class="visitor-container">
    <!-- 顶部统计卡片 -->
    <div class="stat-section">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="8">
          <div class="stat-card business">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ totalCount }}</div>
              <div class="stat-label">总访客数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="stat-card logistics">
            <div class="stat-icon">
              <el-icon><ShoppingBag /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ businessCount }}</div>
              <div class="stat-label">商务访客</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="stat-card family">
            <div class="stat-icon">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ familyCount }}</div>
              <div class="stat-label">其他访客</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索和操作区域 -->
    <div class="search-section">
      <el-row :gutter="15" align="middle">
        <el-col :xs="24" :sm="6">
          <el-input
            v-model="searchForm.name"
            placeholder="访客姓名"
            clearable
            prefix-icon="Search"
            @keyup.enter="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="6">
          <el-select
            v-model="searchForm.category"
            placeholder="选择分类"
            clearable
            @change="handleSearch"
          >
            <el-option label="商务" value="商务" />
            <el-option label="物流" value="物流" />
            <el-option label="家属" value="家属" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="6">
          <div class="button-group">
            <el-button type="primary" @click="handleSearch" :loading="loading">
              <el-icon><Search /></el-icon>
              查询
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
        </el-col>
        <el-col :xs="24" :sm="6">
          <el-button type="success" @click="handleAdd" style="width: 100%">
            <el-icon><Plus /></el-icon>
            新增访客
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 表格区域 -->
    <div class="table-section">
      <el-table
        :data="tableData"
        style="width: 100%"
        :loading="loading"
        stripe
        highlight-current-row
        :default-sort="{ prop: 'id', order: 'descending' }"
        table-layout="auto"
      >
        <el-table-column type="expand">
          <template #default="props">
            <div class="expand-detail">
              <el-row :gutter="20">
                <el-col :xs="24" :md="12">
                  <div class="detail-item">
                    <span class="label">身份证号：</span>
                    <span class="value">{{ props.row.idCard }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="label">邮箱：</span>
                    <span class="value">{{ props.row.email || '未提供' }}</span>
                  </div>
                </el-col>
                <el-col :xs="24" :md="12">
                  <div class="detail-item">
                    <span class="label">信用等级：</span>
                    <el-tag :type="getCreditLevelType(props.row.creditLevel)">
                      {{ props.row.creditLevel }}
                    </el-tag>
                  </div>
                  <div class="detail-item">
                    <span class="label">最后访问：</span>
                    <span class="value">{{ formatDate(props.row.lastVisitTime) || '从未访问' }}</span>
                  </div>
                </el-col>
              </el-row>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="id" label="访客ID" width="80" />

        <el-table-column label="访客信息" min-width="150">
          <template #default="{ row }">
            <div class="visitor-info">
              <div class="name">{{ row.name }}</div>
              <div class="phone">{{ row.phone }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="身份证号" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            <span>{{ maskIdCard(row.idCard) }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip />

        <el-table-column prop="category" label="分类" width="90" align="center">
          <template #default="{ row }">
            <el-tag 
              :type="getCategoryType(row.category)" 
              size="small"
            >
              {{ row.category }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="信用等级" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getCreditLevelType(row.creditLevel)" effect="dark">
              {{ row.creditLevel }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="visitCount" label="访问次数" width="90" align="center" />

        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-space>
              <el-button
                type="primary"
                text
                size="small"
                @click="handleEdit(row)"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button
                type="danger"
                text
                size="small"
                @click="handleDelete(row)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-section" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="formDialogVisible"
      :title="isEdit ? '编辑访客' : '新增访客'"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input 
            v-model="formData.name" 
            placeholder="请输入访客姓名"
            maxlength="50"
          />
        </el-form-item>

        <el-form-item label="身份证号" prop="idCard">
          <el-input 
            v-model="formData.idCard" 
            placeholder="请输入身份证号"
            maxlength="18"
          />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input 
            v-model="formData.phone" 
            placeholder="请输入联系电话"
            maxlength="11"
          />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input 
            v-model="formData.email" 
            type="email" 
            placeholder="请输入邮箱"
            maxlength="100"
          />
        </el-form-item>

        <el-form-item label="分类" prop="category">
          <el-select v-model="formData.category" placeholder="选择分类">
            <el-option label="商务" value="商务" />
            <el-option label="物流" value="物流" />
            <el-option label="家属" value="家属" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ isEdit ? '更新' : '新增' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listVisitors, addVisitor, updateVisitor, deleteVisitor } from '@/api/visitor'

// 状态管理
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const allVisitors = ref([])
const formDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索表单
const searchForm = reactive({
  name: '',
  category: ''
})

// 表单数据
const formData = reactive({
  id: '',
  name: '',
  idCard: '',
  phone: '',
  email: '',
  category: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入访客姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度在2-50之间', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { len: 18, message: '身份证号长度必须为18位', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '电话号码格式不正确', trigger: 'blur' }
  ],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

// 统计数据
const totalCount = computed(() => allVisitors.value.length)
const businessCount = computed(() => 
  allVisitors.value.filter(v => v.category === '商务').length
)
const familyCount = computed(() => 
  allVisitors.value.filter(v => v.category === '家属').length
)

// 辅助函数
const getCreditLevelType = (level) => {
  const typeMap = {
    'A': 'success',
    'B': 'warning',
    'C': 'danger'
  }
  return typeMap[level] || 'info'
}

const getCategoryType = (category) => {
  const typeMap = {
    '商务': 'primary',
    '物流': 'success',
    '家属': 'info'
  }
  return typeMap[category] || 'info'
}

const maskIdCard = (idCard) => {
  if (!idCard) return '****'
  return idCard.substring(0, 6) + '****' + idCard.substring(14)
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

// 数据加载和过滤
const loadVisitors = async () => {
  loading.value = true
  try {
    const res = await listVisitors({})
    allVisitors.value = res || []
    filterTableData()
  } catch (error) {
    ElMessage.error('加载访客数据失败')
  } finally {
    loading.value = false
  }
}

const filterTableData = () => {
  let filtered = [...allVisitors.value]

  // 按名字过滤
  if (searchForm.name.trim()) {
    filtered = filtered.filter(v =>
      v.name.includes(searchForm.name)
    )
  }

  // 按分类过滤
  if (searchForm.category) {
    filtered = filtered.filter(v => v.category === searchForm.category)
  }

  // 排序
  filtered.sort((a, b) => b.id - a.id)

  // 分页
  total.value = filtered.length
  tableData.value = filtered.slice(
    (currentPage.value - 1) * pageSize.value,
    currentPage.value * pageSize.value
  )
}

// 搜索操作
const handleSearch = () => {
  currentPage.value = 1
  filterTableData()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.category = ''
  currentPage.value = 1
  filterTableData()
}

// 新增操作
const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    id: '',
    name: '',
    idCard: '',
    phone: '',
    email: '',
    category: ''
  })
  formRef.value?.clearValidate()
  formDialogVisible.value = true
}

// 编辑操作
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    idCard: row.idCard,
    phone: row.phone,
    email: row.email,
    category: row.category
  })
  formRef.value?.clearValidate()
  formDialogVisible.value = true
}

// 删除操作
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除访客"${row.name}"吗？`,
    '删除提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      try {
        await deleteVisitor(row.id)
        ElMessage.success('删除成功')
        await loadVisitors()
      } catch (error) {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

// 表单提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true

    if (isEdit.value) {
      await updateVisitor(formData)
      ElMessage.success('更新成功')
    } else {
      await addVisitor(formData)
      ElMessage.success('新增成功')
    }

    formDialogVisible.value = false
    await loadVisitors()
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    submitLoading.value = false
  }
}

const resetForm = () => {
  formRef.value?.clearValidate()
}

onMounted(() => {
  loadVisitors()
})
</script>

<style scoped lang="scss">
.visitor-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 统计卡片区域 */
.stat-section {
  margin-bottom: 20px;

  .stat-card {
    display: flex;
    align-items: center;
    padding: 16px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    }

    &.business {
      border-left: 4px solid #409eff;
    }

    &.logistics {
      border-left: 4px solid #67c23a;
    }

    &.family {
      border-left: 4px solid #e6a23c;
    }

    .stat-icon {
      font-size: 32px;
      margin-right: 16px;

      :deep(.el-icon) {
        vertical-align: middle;
      }
    }

    .stat-content {
      flex: 1;

      .stat-value {
        font-size: 24px;
        font-weight: bold;
        color: #333;
      }

      .stat-label {
        font-size: 13px;
        color: #999;
        margin-top: 4px;
      }
    }
  }
}

/* 搜索区域 */
.search-section {
  background: white;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  .button-group {
    display: flex;
    gap: 8px;
    height: 40px;

    :deep(.el-button) {
      flex: 1;
    }
  }
}

/* 表格区域 */
.table-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;

  :deep(.el-table) {
    border-radius: 8px;

    .el-table__header {
      background-color: #f5f7fa;
    }

    .el-table__row {
      &:hover {
        background-color: #f5f7fa !important;
      }
    }
  }
}

/* 展开行 */
.expand-detail {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;

  .detail-item {
    margin-bottom: 12px;

    .label {
      font-weight: 600;
      color: #333;
      margin-right: 8px;
      min-width: 80px;
      display: inline-block;
    }

    .value {
      color: #666;
      font-size: 13px;
    }
  }
}

/* 访客信息 */
.visitor-info {
  .name {
    font-weight: 600;
    color: #333;
    margin-bottom: 4px;
  }

  .phone {
    color: #999;
    font-size: 12px;
  }
}

/* 分页区域 */
.pagination-section {
  background: white;
  padding: 15px;
  border-radius: 8px;
  text-align: right;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 表单对话框 */
:deep(.el-dialog) {
  .el-form {
    padding: 20px;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .visitor-container {
    padding: 12px;
  }

  .search-section {
    padding: 12px;

    .button-group {
      margin-top: 8px;
      flex-direction: column;
      height: auto;

      :deep(.el-button) {
        width: 100%;
      }
    }
  }

  :deep(.el-table) {
    font-size: 12px;

    .el-table__cell {
      padding: 8px 4px !important;
    }
  }
}

@media (max-width: 480px) {
  .visitor-container {
    padding: 8px;
  }

  :deep(.el-dialog) {
    width: 95vw !important;
  }

  .table-section {
    overflow-x: auto;
  }
}
</style>