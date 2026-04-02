<template>
  <div class="approval-container">
    <el-card class="box-card">
      <!-- 顶部统计 -->
      <div class="header-section">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="8">
            <div class="stat-card pending">
              <div class="stat-icon">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ pendingCount }}</div>
                <div class="stat-label">待审批</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
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
          <el-col :xs="24" :sm="8">
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

      <!-- 筛选条件 -->
      <div class="filter-section">
        <el-row :gutter="15">
          <el-col :xs="24" :sm="12" :md="5">
            <el-input
              v-model="searchForm.visitorName"
              placeholder="访客姓名"
              clearable
              prefix-icon="Search"
              @keyup.enter="handleSearch"
            />
          </el-col>
          <el-col :xs="24" :sm="12" :md="5">
            <el-input
              v-model="searchForm.departmentName"
              placeholder="访问部门"
              clearable
              prefix-icon="OfficeBuilding"
              @keyup.enter="handleSearch"
            />
          </el-col>
          <el-col :xs="24" :sm="12" :md="7">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 100%"
            />
          </el-col>
          <el-col :xs="24" :sm="12" :md="7">
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
        </el-row>
      </div>

      <!-- 标签页 -->
      <el-tabs 
        v-model="activeTab" 
        class="approval-tabs"
        @tab-change="handleTabChange"
      >
        <el-tab-pane name="pending">
          <template #label>
            <el-badge :value="pendingCount" class="tab-badge">
              <el-icon><Clock /></el-icon>
              <span>待审批</span>
            </el-badge>
          </template>
        </el-tab-pane>
        <el-tab-pane name="approved">
          <template #label>
            <el-badge :value="approvedCount" class="tab-badge">
              <el-icon><SuccessFilled /></el-icon>
              <span>已批准</span>
            </el-badge>
          </template>
        </el-tab-pane>
        <el-tab-pane name="rejected">
          <template #label>
            <el-badge :value="rejectedCount" class="tab-badge">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>已拒绝</span>
            </el-badge>
          </template>
        </el-tab-pane>
      </el-tabs>

      <!-- 空状态 -->
      <el-empty 
        v-if="!loading && tableData.length === 0" 
        description="暂无数据"
        style="margin-top: 40px"
      />

      <!-- 表格 -->
      <el-table
        v-else
        :data="tableData"
        style="width: 100%; margin-top: 10px"
        :loading="loading"
        stripe
        highlight-current-row
        :default-sort="{ prop: 'appointmentId', order: 'descending' }"
        table-layout="auto"
      >
        <!-- 展开行 -->
        <el-table-column type="expand" width="50">
          <template #default="props">
            <div class="expand-content">
              <el-row :gutter="20">
                <el-col :xs="24" :md="12">
                  <div class="detail-item">
                    <h4>👤 访客信息</h4>
                    <p><span>姓名：</span>{{ props.row.visitorName }}</p>
                    <p><span>电话：</span>{{ props.row.visitorPhone }}</p>
                    <p><span>身份证：</span>{{ maskIdCard(props.row.visitorIdCard) }}</p>
                  </div>
                </el-col>
                <el-col :xs="24" :md="12">
                  <div class="detail-item">
                    <h4>📋 预约信息</h4>
                    <p><span>访问部门：</span>{{ props.row.departmentName }}</p>
                    <p><span>访问事由：</span>{{ props.row.visitReason }}</p>
                    <p><span>预约时间：</span>{{ formatTime(props.row.appointmentTime) }}</p>
                  </div>
                </el-col>
              </el-row>
            </div>
          </template>
        </el-table-column>

        <!-- 预约ID -->
        <el-table-column 
          prop="appointmentId" 
          label="预约ID" 
          width="120"
          show-overflow-tooltip
        />

        <!-- 访客信息 -->
        <el-table-column label="访客信息" min-width="160">
          <template #default="{ row }">
            <div class="visitor-info">
              <div class="name">{{ row.visitorName }}</div>
              <div class="phone">{{ row.visitorPhone }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 预约详情 -->
        <el-table-column label="预约详情" min-width="200">
          <template #default="{ row }">
            <div class="appointment-info">
              <div class="dept">📍 {{ row.departmentName }}</div>
              <div class="reason">{{ row.visitReason }}</div>
              <div class="time">⏰ {{ formatDate(row.appointmentTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 状态 -->
        <el-table-column label="状态" width="110" align="center">
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

        <!-- 审批信息 -->
        <el-table-column label="审批人" min-width="130" align="center">
          <template #default="{ row }">
            <div class="approval-info">
              <div v-if="row.approvalTime" class="approved">
                <el-icon class="check-icon"><SuccessFilled /></el-icon>
                <span>{{ row.approverName || '系统处理' }}</span>
              </div>
              <div v-else class="pending-badge">
                <el-badge is-fixed :value="'待处理'" />
              </div>
              <div class="approval-time" v-if="row.approvalTime">
                {{ formatTime(row.approvalTime) }}
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column label="操作" width="240" fixed="right" align="center">
          <template #default="{ row }">
            <el-space>
              <el-button
                v-if="row.status === '1'"
                type="success"
                text
                size="small"
                @click="handleApprove(row)"
              >
                <el-icon><SuccessFilled /></el-icon>
                批准
              </el-button>
              <el-button
                v-if="row.status === '1'"
                type="danger"
                text
                size="small"
                @click="handleReject(row)"
              >
                <el-icon><CircleCloseFilled /></el-icon>
                拒绝
              </el-button>
              <el-button
                type="primary"
                text
                size="small"
                @click="handleViewDetails(row)"
              >
                <el-icon><View /></el-icon>
                详情
              </el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-if="total > 0"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; text-align: right"
      />
    </el-card>

    <!-- 批准/拒绝对话框 -->
    <el-dialog
      v-model="actionDialogVisible"
      :title="`${actionType === 'approve' ? '批准' : '拒绝'}预约`"
      width="550px"
      @close="resetActionForm"
    >
      <el-descriptions :column="1" border style="margin-bottom: 20px">
        <el-descriptions-item label="访客名称">
          {{ selectedRow.visitorName }}
        </el-descriptions-item>
        <el-descriptions-item label="访问部门">
          {{ selectedRow.departmentName }}
        </el-descriptions-item>
        <el-descriptions-item label="访问事由">
          {{ selectedRow.visitReason }}
        </el-descriptions-item>
        <el-descriptions-item label="预约时间">
          {{ formatTime(selectedRow.appointmentTime) }}
        </el-descriptions-item>
      </el-descriptions>

      <el-form :model="actionForm" label-width="80px">
        <el-form-item
          :label="`${actionType === 'approve' ? '批准' : '拒绝'}意见`"
          prop="remark"
        >
          <el-input
            v-model="actionForm.remark"
            type="textarea"
            :rows="4"
            :placeholder="actionType === 'approve' 
              ? '请输入批准意见（可选）' 
              : '请输入拒绝原因（必填）'"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="actionDialogVisible = false">取消</el-button>
        <el-button
          :type="actionType === 'approve' ? 'success' : 'danger'"
          @click="handleConfirmAction"
          :loading="actionLoading"
        >
          {{ actionType === 'approve' ? '批准' : '拒绝' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="预约详情" 
      width="700px"
      :close-on-click-modal="false"
    >
      <div v-if="selectedRow" class="detail-dialog">
        <!-- 访客信息 -->
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
                <div class="info-label">姓名</div>
                <div class="info-value">{{ selectedRow.visitorName }}</div>
              </div>
              <div class="info-group">
                <div class="info-label">电话</div>
                <div class="info-value">{{ selectedRow.visitorPhone }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :md="12">
              <div class="info-group">
                <div class="info-label">邮箱</div>
                <div class="info-value">{{ selectedRow.visitorEmail || '未���供' }}</div>
              </div>
              <div class="info-group">
                <div class="info-label">分类</div>
                <div class="info-value">{{ selectedRow.visitorCategory || '未分类' }}</div>
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
                <div class="info-value">{{ selectedRow.departmentName }}</div>
              </div>
              <div class="info-group">
                <div class="info-label">访问事由</div>
                <div class="info-value">{{ selectedRow.visitReason }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :md="12">
              <div class="info-group">
                <div class="info-label">开始时间</div>
                <div class="info-value">{{ formatTime(selectedRow.appointmentTime) }}</div>
              </div>
              <div class="info-group">
                <div class="info-label">结束时间</div>
                <div class="info-value">{{ formatTime(selectedRow.appointmentEndTime) }}</div>
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
                <div class="info-label">审批状态</div>
                <el-tag :type="getStatusType(selectedRow.status)">
                  {{ getStatusLabel(selectedRow.status) }}
                </el-tag>
              </div>
              <div class="info-group">
                <div class="info-label">审批人</div>
                <div class="info-value">{{ selectedRow.approverName || '未审批' }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :md="12">
              <div class="info-group">
                <div class="info-label">审批时间</div>
                <div class="info-value">{{ formatTime(selectedRow.approvalTime) || '未审批' }}</div>
              </div>
              <div class="info-group">
                <div class="info-label">审批备注</div>
                <div class="info-value">{{ selectedRow.approvalRemark || '无' }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 审批历史 -->
        <el-card class="detail-card" shadow="hover" v-if="approvalHistory.length > 0">
          <template #header>
            <div class="card-header">
              <el-icon><DocumentCopy /></el-icon>
              <span>审批历史</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="history in approvalHistory"
              :key="history.id"
              :timestamp="formatTime(history.approvalTime)"
              placement="top"
            >
              <p class="history-status">
                <el-tag :type="getStatusType(history.status)">
                  {{ getStatusLabel(history.status) }}
                </el-tag>
              </p>
              <p class="history-remark">{{ history.remark || '无备注' }}</p>
            </el-timeline-item>
          </el-timeline>
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
import { ElMessage } from 'element-plus'
import {
  approveAppointment,
  rejectAppointment,
  getApprovalHistory
} from '@/api/approval'
import { listAppointments } from '@/api/appointment'

// 状态管理
const loading = ref(false)
const actionLoading = ref(false)
const tableData = ref([])
const allAppointments = ref([])
const approvalHistory = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const activeTab = ref('pending')

// 对话框状态
const actionDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const actionType = ref('approve')
const selectedRow = ref({})

// 搜索表单
const searchForm = reactive({
  visitorName: '',
  departmentName: '',
  dateRange: []
})

// 操作表单
const actionForm = reactive({
  remark: ''
})

// 统计数据（计算属性）
const pendingCount = computed(() => 
  allAppointments.value.filter(a => a.status === '1').length
)
const approvedCount = computed(() => 
  allAppointments.value.filter(a => a.status === '2').length
)
const rejectedCount = computed(() => 
  allAppointments.value.filter(a => a.status === '3').length
)

// 格式化函数
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatDate = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const dateStr = date.toLocaleDateString('zh-CN')
  const timeStr2 = date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
  return `${dateStr} ${timeStr2}`
}

const maskIdCard = (idCard) => {
  if (!idCard) return '未提供'
  return idCard.substring(0, 6) + '****' + idCard.substring(14)
}

const getStatusLabel = (status) => {
  const statusMap = {
    '1': '待审批',
    '2': '已批准',
    '3': '已拒绝',
    '4': '已使用',
    '5': '已过期',
    '6': '已取消'
  }
  return statusMap[status] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    '1': 'warning',
    '2': 'success',
    '3': 'danger',
    '4': 'info',
    '5': 'info',
    '6': 'info'
  }
  return typeMap[status] || 'info'
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

// 数据过滤和分页
const filterTableData = () => {
  let filtered = [...allAppointments.value]

  // 按标签页过滤
  switch (activeTab.value) {
    case 'pending':
      filtered = filtered.filter(a => a.status === '1')
      break
    case 'approved':
      filtered = filtered.filter(a => a.status === '2')
      break
    case 'rejected':
      filtered = filtered.filter(a => a.status === '3')
      break
  }

  // 按搜索条件过滤
  if (searchForm.visitorName.trim()) {
    filtered = filtered.filter(a =>
      a.visitorName.includes(searchForm.visitorName)
    )
  }

  if (searchForm.departmentName.trim()) {
    filtered = filtered.filter(a =>
      a.departmentName.includes(searchForm.departmentName)
    )
  }

  if (searchForm.dateRange?.length === 2) {
    const [start, end] = searchForm.dateRange
    filtered = filtered.filter(a => {
      const appointmentTime = new Date(a.appointmentTime)
      return appointmentTime >= start && appointmentTime <= end
    })
  }

  // 排序和分页
  filtered.sort((a, b) => 
    new Date(b.appointmentTime) - new Date(a.appointmentTime)
  )
  total.value = filtered.length
  tableData.value = filtered.slice(
    (currentPage.value - 1) * pageSize.value,
    currentPage.value * pageSize.value
  )
}

// 搜索和筛选
const handleSearch = () => {
  currentPage.value = 1
  filterTableData()
}

const handleReset = () => {
  searchForm.visitorName = ''
  searchForm.departmentName = ''
  searchForm.dateRange = []
  currentPage.value = 1
  filterTableData()
}

const handleTabChange = () => {
  currentPage.value = 1
  filterTableData()
}

// 批准操作
const handleApprove = (row) => {
  selectedRow.value = row
  actionType.value = 'approve'
  actionForm.remark = ''
  actionDialogVisible.value = true
}

// 拒绝操作
const handleReject = (row) => {
  selectedRow.value = row
  actionType.value = 'reject'
  actionForm.remark = ''
  actionDialogVisible.value = true
}

// 确认操作
const handleConfirmAction = async () => {
  if (actionType.value === 'reject' && !actionForm.remark.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }

  actionLoading.value = true
  try {
    if (actionType.value === 'approve') {
      await approveAppointment(selectedRow.value.id, actionForm.remark)
      ElMessage.success('预约已批准')
    } else {
      await rejectAppointment(selectedRow.value.id, actionForm.remark)
      ElMessage.success('预约已拒绝')
    }

    actionDialogVisible.value = false
    await loadAppointments()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    actionLoading.value = false
  }
}

// 查看详情
const handleViewDetails = async (row) => {
  selectedRow.value = row
  detailDialogVisible.value = true

  try {
    const res = await getApprovalHistory(row.id)
    approvalHistory.value = res || []
  } catch (error) {
    console.error('加载审批历史失败', error)
    approvalHistory.value = []
  }
}

const resetActionForm = () => {
  actionForm.remark = ''
}

onMounted(() => {
  loadAppointments()
})
</script>

<style scoped lang="scss">
.approval-container {
  padding: 20px;

  .box-card {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }
}

/* 头部统计卡片 */
.header-section {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;

  .stat-card {
    display: flex;
    align-items: center;
    padding: 16px;
    border-radius: 8px;
    transition: all 0.3s ease;
    cursor: pointer;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    }

    &.pending {
      background: linear-gradient(135deg, #fef0f0 0%, #fde2e4 100%);
      border-left: 4px solid #f56c6c;
    }

    &.approved {
      background: linear-gradient(135deg, #f0f9ff 0%, #e1f5fe 100%);
      border-left: 4px solid #67c23a;
    }

    &.rejected {
      background: linear-gradient(135deg, #faf8f3 0%, #fdf6ec 100%);
      border-left: 4px solid #e6a23c;
    }

    .stat-icon {
      font-size: 32px;
      margin-right: 12px;

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
        color: #666;
        margin-top: 4px;
      }
    }
  }
}

/* 筛选区域 */
.filter-section {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;

  .button-group {
    display: flex;
    gap: 8px;
    height: 40px;

    :deep(.el-button) {
      flex: 1;
    }
  }
}

/* 标签页 */
.approval-tabs {
  margin-top: 20px;
  margin-bottom: 10px;

  :deep(.el-tabs__header) {
    margin: 0;
  }

  .tab-badge {
    display: inline-flex;
    align-items: center;
    gap: 6px;

    :deep(.el-icon) {
      margin: 0;
    }
  }
}

/* 访客信息 */
.visitor-info {
  padding: 4px 0;

  .name {
    font-weight: 600;
    color: #333;
    font-size: 14px;
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

/* 审批信息 */
.approval-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;

  .approved {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #67c23a;
    font-weight: 500;

    .check-icon {
      font-size: 16px;
    }
  }

  .approval-time {
    font-size: 12px;
    color: #999;
  }
}

/* 展开行 */
.expand-content {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;

  .detail-item {
    h4 {
      margin: 0 0 12px 0;
      font-size: 14px;
      font-weight: 600;
      color: #333;
    }

    p {
      margin: 8px 0;
      font-size: 13px;
      color: #666;

      span {
        font-weight: 600;
        margin-right: 8px;
        min-width: 60px;
        display: inline-block;
      }
    }
  }
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

  .history-status {
    margin-bottom: 8px;
  }

  .history-remark {
    margin: 0;
    color: #666;
    font-size: 13px;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .approval-container {
    padding: 12px;
  }

  .header-section {
    flex-direction: column;
    gap: 12px;
  }

  .filter-section {
    .button-group {
      flex-direction: column;
      height: auto;

      :deep(.el-button) {
        width: 100%;
      }
    }
  }

  :deep(.el-table) {
    font-size: 12px !important;

    .el-table__cell {
      padding: 8px 4px !important;
    }
  }

  .appointment-info {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .approval-container {
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