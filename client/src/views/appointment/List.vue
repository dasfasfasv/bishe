<template>
  <div class="appointment-container">
    <!-- 顶部统计卡片 -->
    <div class="stat-section">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="6">
          <div class="stat-card pending">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ pendingCount }}</div>
              <div class="stat-label">待审批</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="6">
          <div class="stat-card approved">
            <div class="stat-icon">
              <el-icon><SuccessFilled /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ approvedCount }}</div>
              <div class="stat-label">已批准</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="6">
          <div class="stat-card used">
            <div class="stat-icon">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ usedCount }}</div>
              <div class="stat-label">已使用</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="6">
          <div class="stat-card rejected">
            <div class="stat-icon">
              <el-icon><CircleCloseFilled /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ rejectedCount }}</div>
              <div class="stat-label">已拒绝</div>
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
            v-model="searchForm.visitorId"
            placeholder="访客ID"
            clearable
            prefix-icon="Search"
            @keyup.enter="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="6">
          <el-select
            v-model="searchForm.status"
            placeholder="选择状态"
            clearable
            @change="handleSearch"
          >
            <el-option label="待审批" value="1" />
            <el-option label="已批准" value="2" />
            <el-option label="已拒绝" value="3" />
            <el-option label="已使用" value="4" />
            <el-option label="已过期" value="5" />
            <el-option label="已取消" value="6" />
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
          <el-button type="success" @click="handleCreate" style="width: 100%">
            <el-icon><Plus /></el-icon>
            新建预约
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
        <!-- 展开行 -->
        <el-table-column type="expand" width="50">
          <template #default="props">
            <div class="expand-detail">
              <el-row :gutter="20">
                <el-col :xs="24" :md="12">
                  <div class="detail-item">
                    <span class="label">预约详情：</span>
                    <div style="margin-top: 8px">
                      <p><strong>访客ID：</strong>{{ props.row.visitorId }}</p>
                      <p><strong>访问事由：</strong>{{ props.row.visitReason }}</p>
                      <p><strong>预期结束时间：</strong>{{ formatDate(props.row.appointmentEndTime) || '待定' }}</p>
                    </div>
                  </div>
                </el-col>
                <el-col :xs="24" :md="12">
                  <div class="detail-item">
                    <span class="label">审批信息：</span>
                    <div style="margin-top: 8px">
                      <p><strong>审批人：</strong>{{ props.row.approverName || '待审批' }}</p>
                      <p><strong>审批时间：</strong>{{ formatDate(props.row.approvalTime) || '待审批' }}</p>
                      <p><strong>审批备注：</strong>{{ props.row.approvalRemark || '无' }}</p>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="id" label="预约ID" width="90" />

        <el-table-column label="访客信息" min-width="160">
          <template #default="{ row }">
            <div class="visitor-info">
              <div class="name">{{ row.visitorName }}</div>
              <div class="phone">{{ row.visitorPhone }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="预约详情" min-width="200">
          <template #default="{ row }">
            <div class="appointment-info">
              <div class="dept">📍 {{ row.departmentName }}</div>
              <div class="reason">{{ row.visitReason }}</div>
              <div class="time">⏰ {{ formatDate(row.appointmentTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag 
              :type="getStatusType(row.status)" 
              effect="dark"
              size="large"
            >
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="审批人" min-width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.approverName">{{ row.approverName }}</span>
            <el-tag v-else type="info">待处理</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-space>
              <el-button
                type="primary"
                text
                size="small"
                @click="handleView(row)"
              >
                <el-icon><View /></el-icon>
                详情
              </el-button>
              <el-button
                v-if="row.status === '1'"
                type="danger"
                text
                size="small"
                @click="handleCancel(row)"
              >
                <el-icon><Delete /></el-icon>
                取消
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

    <!-- 预约详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="预约详情"
      width="700px"
      :close-on-click-modal="false"
    >
      <div v-if="selectedAppointment" class="detail-dialog">
        <!-- 基本信息 -->
        <el-card class="detail-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><User /></el-icon>
              <span>访客信息</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :xs="24" :md="12">
              <div class="info-group">
                <div class="info-label">访客姓名</div>
                <div class="info-value">{{ selectedAppointment.visitorName }}</div>
              </div>
              <div class="info-group">
                <div class="info-label">联系电话</div>
                <div class="info-value">{{ selectedAppointment.visitorPhone }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :md="12">
              <div class="info-group">
                <div class="info-label">访客ID</div>
                <div class="info-value">{{ selectedAppointment.visitorId }}</div>
              </div>
              <div class="info-group">
                <div class="info-label">访客分类</div>
                <div class="info-value">{{ selectedAppointment.visitorCategory || '未分类' }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 预约信息 -->
        <el-card class="detail-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Calendar /></el-icon>
              <span>预约信息</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :xs="24" :md="12">
              <div class="info-group">
                <div class="info-label">访问部门</div>
                <div class="info-value">{{ selectedAppointment.departmentName }}</div>
              </div>
              <div class="info-group">
                <div class="info-label">访问事由</div>
                <div class="info-value">{{ selectedAppointment.visitReason }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :md="12">
              <div class="info-group">
                <div class="info-label">预约时间</div>
                <div class="info-value">{{ formatDate(selectedAppointment.appointmentTime) }}</div>
              </div>
              <div class="info-group">
                <div class="info-label">预期结束</div>
                <div class="info-value">{{ formatDate(selectedAppointment.appointmentEndTime) || '待定' }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 审批信息 -->
        <el-card class="detail-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><CircleCheck /></el-icon>
              <span>审批信息</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :xs="24" :md="12">
              <div class="info-group">
                <div class="info-label">预约状态</div>
                <el-tag :type="getStatusType(selectedAppointment.status)" effect="dark">
                  {{ getStatusLabel(selectedAppointment.status) }}
                </el-tag>
              </div>
              <div class="info-group">
                <div class="info-label">审批人</div>
                <div class="info-value">{{ selectedAppointment.approverName || '待审批' }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :md="12">
              <div class="info-group">
                <div class="info-label">审批时间</div>
                <div class="info-value">{{ formatDate(selectedAppointment.approvalTime) || '待审批' }}</div>
              </div>
              <div class="info-group">
                <div class="info-label">审批备注</div>
                <div class="info-value">{{ selectedAppointment.approvalRemark || '无' }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listAppointments, cancelAppointment, getAppointment } from '@/api/appointment'

// 状态管理
const loading = ref(false)
const tableData = ref([])
const allAppointments = ref([])
const detailDialogVisible = ref(false)
const selectedAppointment = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索表单
const searchForm = reactive({
  visitorId: '',
  status: ''
})

// 状态映射
const statusMap = {
  '1': '待审批',
  '2': '已批准',
  '3': '已拒绝',
  '4': '已使用',
  '5': '已过期',
  '6': '已取消'
}

const statusTypeMap = {
  '1': 'warning',
  '2': 'success',
  '3': 'danger',
  '4': 'info',
  '5': 'info',
  '6': 'info'
}

// 统计数据
const pendingCount = computed(() => 
  allAppointments.value.filter(a => a.status === '1').length
)
const approvedCount = computed(() => 
  allAppointments.value.filter(a => a.status === '2').length
)
const usedCount = computed(() => 
  allAppointments.value.filter(a => a.status === '4').length
)
const rejectedCount = computed(() => 
  allAppointments.value.filter(a => a.status === '3').length
)

// 辅助函数
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getStatusLabel = (status) => {
  return statusMap[status] || '未知'
}

const getStatusType = (status) => {
  return statusTypeMap[status] || 'info'
}

// 数据加载
const loadAppointments = async () => {
  loading.value = true
  try {
    const res = await listAppointments({})
    allAppointments.value = res || []
    filterTableData()
  } catch (error) {
    ElMessage.error('加载预约数据失败')
  } finally {
    loading.value = false
  }
}

const filterTableData = () => {
  let filtered = [...allAppointments.value]

  // 按访客ID过滤
  if (searchForm.visitorId.trim()) {
    filtered = filtered.filter(a =>
      a.visitorId.toString().includes(searchForm.visitorId)
    )
  }

  // 按状态过滤
  if (searchForm.status) {
    filtered = filtered.filter(a => a.status === searchForm.status)
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
  searchForm.visitorId = ''
  searchForm.status = ''
  currentPage.value = 1
  filterTableData()
}

// 新建预约
const handleCreate = () => {
  ElMessage.info('新建预约功能开发中...')
  // 可以改为导航到新建页面
  // router.push('/appointment/create')
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await getAppointment(row.id)
    selectedAppointment.value = res
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载详情失败')
  }
}

// 取消预约
const handleCancel = (row) => {
  ElMessageBox.confirm(
    `确定要取消预约 ${row.id} 吗？`,
    '取消预约',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      try {
        await cancelAppointment(row.id)
        ElMessage.success('预约已取消')
        await loadAppointments()
      } catch (error) {
        ElMessage.error('取消失败')
      }
    })
    .catch(() => {})
}

onMounted(() => {
  loadAppointments()
})
</script>

<style scoped lang="scss">
.appointment-container {
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

    &.pending {
      border-left: 4px solid #e6a23c;
    }

    &.approved {
      border-left: 4px solid #67c23a;
    }

    &.used {
      border-left: 4px solid #409eff;
    }

    &.rejected {
      border-left: 4px solid #f56c6c;
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
    margin-bottom: 20px;

    span {
      font-weight: 600;
      color: #333;
      display: block;
      margin-bottom: 10px;
    }

    p {
      margin: 6px 0;
      color: #666;
      font-size: 13px;

      strong {
        margin-right: 8px;
      }
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

/* 预约信息 */
.appointment-info {
  padding: 4px 0;
  font-size: 13px;

  > div {
    margin: 4px 0;
    color: #666;

    &.dept {
      color: #409eff;
      font-weight: 500;
    }

    &.reason {
      color: #333;
    }

    &.time {
      color: #999;
    }
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

/* 详情对话框 */
.detail-dialog {
  max-height: 600px;
  overflow-y: auto;

  .detail-card {
    margin-bottom: 16px;
    border-radius: 8px;

    &:last-child {
      margin-bottom: 0;
    }

    :deep(.el-card__header) {
      background-color: #f5f7fa;
      border-bottom: 1px solid #ebeef5;
      padding: 12px 16px;
    }

    .card-header {
      display: flex;
      align-items: center;
      gap: 8px;
      font-weight: 600;
      color: #333;
      font-size: 14px;

      :deep(.el-icon) {
        font-size: 16px;
      }
    }
  }

  .info-group {
    margin-bottom: 12px;

    .info-label {
      font-size: 12px;
      color: #999;
      margin-bottom: 4px;
    }

    .info-value {
      font-size: 14px;
      color: #333;
      font-weight: 500;
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .appointment-container {
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

  .appointment-info {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .appointment-container {
    padding: 8px;
  }

  :deep(.el-dialog) {
    width: 95vw !important;
  }

  .detail-dialog {
    max-height: 400px;
  }
}
</style>