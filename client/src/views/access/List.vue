<template>
  <div class="access-container">
    <el-card class="box-card">
      <!-- 顶部统计 -->
      <div class="header-section">
        <el-row :gutter="20">
          <el-col :xs="24" :md="8">
            <el-statistic title="有效凭证" :value="validCount">
              <template #prefix>
                <el-icon color="#67C23A"><SuccessFilled /></el-icon>
              </template>
            </el-statistic>
          </el-col>
          <el-col :xs="24" :md="8">
            <el-statistic title="已过期凭证" :value="expiredCount">
              <template #prefix>
                <el-icon color="#F56C6C"><CircleCloseFilled /></el-icon>
              </template>
            </el-statistic>
          </el-col>
          <el-col :xs="24" :md="8">
            <el-statistic title="待授权预约" :value="pendingAppointments">
              <template #prefix>
                <el-icon color="#E6A23C"><Warning /></el-icon>
              </template>
            </el-statistic>
          </el-col>
        </el-row>
      </div>

      <!-- 筛选条件 -->
      <div class="filter-section" style="margin-top: 20px">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6">
            <el-input
              v-model="searchForm.visitorName"
              placeholder="访客名称"
              clearable
              prefix-icon="Search"
            />
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-select
              v-model="searchForm.status"
              placeholder="凭证状态"
              clearable
            >
              <el-option label="有效" value="1" />
              <el-option label="已失效" value="0" />
            </el-select>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 100%"
            />
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
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
      <el-tabs v-model="activeTab" style="margin-top: 20px" @tab-change="handleTabChange">
        <el-tab-pane label="有效凭证" name="valid">
          <template #label>
            <span>
              <el-icon><SuccessFilled /></el-icon>
              有效凭证 ({{ validCount }})
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="已失效凭证" name="invalid">
          <template #label>
            <span>
              <el-icon><CircleCloseFilled /></el-icon>
              已失效凭证 ({{ expiredCount }})
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="待授权预约" name="pending">
          <template #label>
            <span>
              <el-icon><Plus /></el-icon>
              待授权预约 ({{ pendingAppointments }})
            </span>
          </template>
        </el-tab-pane>
      </el-tabs>

      <!-- 表格 -->
      <el-table
        :data="tableData"
        style="width: 100%; margin-top: 20px"
        :loading="loading"
        stripe
        highlight-current-row
        :default-sort="{ prop: 'id', order: 'descending' }"
      >
        <el-table-column type="expand">
          <template #default="props">
            <div class="expand-content">
              <el-row :gutter="20">
                <el-col :xs="24" :md="12">
                  <div class="detail-item">
                    <span class="label">门禁卡信息：</span>
                    <div style="margin-top: 10px">
                      <p><strong>卡号：</strong>{{ props.row.accessCard }}</p>
                      <p><strong>生效时间：</strong>{{ formatTime(props.row.effectiveTime) }}</p>
                      <p><strong>失效时间：</strong>{{ formatTime(props.row.expiryTime) }}</p>
                    </div>
                  </div>
                </el-col>
                <el-col :xs="24" :md="12">
                  <div class="detail-item">
                    <span class="label">允许区域：</span>
                    <div style="margin-top: 10px">
                      <el-tag
                        v-for="(area, index) in parseAreas(props.row.allowedAreas)"
                        :key="index"
                        style="margin: 4px"
                      >
                        {{ area }}
                      </el-tag>
                    </div>
                  </div>
                </el-col>
              </el-row>
              
              <!-- 二维码展示 -->
              <el-divider style="margin: 20px 0" />
              
              <div class="qrcode-section">
                <span class="label">二维码凭证：</span>
                <div style="margin-top: 15px; text-align: center">
                  <div id="qrcode" style="display: inline-block">
                    <!-- 这里可以集成 QRCode 库显示二维码 -->
                    <el-image
                      style="width: 200px; height: 200px; border: 1px solid #ddd; padding: 10px"
                      :src="`https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${props.row.qrCode}`"
                      fit="contain"
                    />
                  </div>
                  <p style="margin-top: 10px; color: #666; font-size: 12px">
                    二维码值: {{ props.row.qrCode }}
                  </p>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="id" label="授权ID" width="100" />

        <el-table-column label="访客信息" min-width="160">
          <template #default="{ row }">
            <div class="visitor-cell">
              <p class="name">{{ row.visitorName }}</p>
              <p class="id">ID: {{ row.visitorId }}</p>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="凭证信息" min-width="180">
          <template #default="{ row }">
            <div class="credential-cell">
              <p class="card-number">
                <el-icon><Key /></el-icon>
                {{ row.accessCard }}
              </p>
              <p class="areas">
                <el-icon><Location /></el-icon>
                {{ parseAreas(row.allowedAreas).length }} 个区域
              </p>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="有效期" min-width="200" align="center">
          <template #default="{ row }">
            <div class="validity-cell">
              <p class="start">⏰ {{ formatDate(row.effectiveTime) }}</p>
              <p class="end">⏳ {{ formatDate(row.expiryTime) }}</p>
              <el-progress
                :percentage="getValidityPercentage(row.effectiveTime, row.expiryTime)"
                :status="getValidityStatus(row.expiryTime)"
                style="margin-top: 8px"
              />
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '1' ? 'success' : 'danger'" effect="dark">
              {{ row.status === '1' ? '有效' : '已失效' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                type="primary"
                size="small"
                @click="handleViewQRCode(row)"
              >
                <el-icon><DocumentCopy /></el-icon>
                二维码
              </el-button>
              <el-button
                type="info"
                size="small"
                link
                @click="handleViewDetails(row)"
              >
                <el-icon><View /></el-icon>
                详情
              </el-button>
              <el-button
                v-if="row.status === '1'"
                type="danger"
                size="small"
                @click="handleRevoke(row)"
              >
                <el-icon><Delete /></el-icon>
                撤销
              </el-button>
            </el-button-group>
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

    <!-- 待授权预约表格 -->
    <el-card class="box-card" style="margin-top: 20px" v-if="activeTab === 'pending'">
      <template #header>
        <div class="card-header">
          <span>
            <el-icon><Plus /></el-icon>
            待授权预约列表
          </span>
          <el-button type="success" size="small" @click="handleRefreshPending">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table
        :data="pendingAppointmentList"
        style="width: 100%"
        :loading="pendingLoading"
        stripe
      >
        <el-table-column prop="id" label="预约ID" width="100" />
        <el-table-column label="访客信息" min-width="150">
          <template #default="{ row }">
            <div>
              <p class="name">{{ row.visitorName }}</p>
              <p class="phone">{{ row.visitorPhone }}</p>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="预约信息" min-width="180">
          <template #default="{ row }">
            <div>
              <p>{{ row.departmentName }}</p>
              <p class="reason">{{ row.visitReason }}</p>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="预约时间" min-width="160">
          <template #default="{ row }">
            <div>
              <p>{{ formatDate(row.appointmentTime) }}</p>
              <p class="time-end">至 {{ formatDate(row.appointmentEndTime) }}</p>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button
              type="success"
              size="small"
              @click="handleGenerateCredential(row)"
            >
              <el-icon><Key /></el-icon>
              生成凭证
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 二维码对话框 -->
    <el-dialog v-model="qrcodeDialogVisible" title="门禁凭证二维码" width="500px">
      <div class="qrcode-dialog">
        <div class="qrcode-container">
          <el-image
            :src="`https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=${selectedQRCode}`"
            fit="contain"
          />
        </div>
        <div class="qrcode-info">
          <p><strong>访客：</strong>{{ selectedRow.visitorName }}</p>
          <p><strong>卡号：</strong>{{ selectedRow.accessCard }}</p>
          <p><strong>二维码：</strong></p>
          <el-input
            v-model="selectedQRCode"
            type="textarea"
            :rows="3"
            readonly
          />
          <div style="margin-top: 10px">
            <el-button type="primary" @click="handleCopyQRCode">
              <el-icon><DocumentCopy /></el-icon>
              复制二维码
            </el-button>
            <el-button @click="handleDownloadQRCode">
              <el-icon><Download /></el-icon>
              下载二维码
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="授权详情" width="700px">
      <div v-if="selectedRow" class="detail-dialog">
        <!-- 基本信息 -->
        <el-card class="detail-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>🎫 授权信息</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :xs="24" :md="12">
              <p><strong>授权ID：</strong>{{ selectedRow.id }}</p>
              <p><strong>卡号：</strong>{{ selectedRow.accessCard }}</p>
              <p><strong>状态：</strong>
                <el-tag :type="selectedRow.status === '1' ? 'success' : 'danger'">
                  {{ selectedRow.status === '1' ? '有效' : '已失效' }}
                </el-tag>
              </p>
            </el-col>
            <el-col :xs="24" :md="12">
              <p><strong>生效时间：</strong>{{ formatTime(selectedRow.effectiveTime) }}</p>
              <p><strong>失效时间：</strong>{{ formatTime(selectedRow.expiryTime) }}</p>
              <p><strong>剩余时间：</strong>{{ getRemainingDays(selectedRow.expiryTime) }}</p>
            </el-col>
          </el-row>
        </el-card>

        <!-- 访客信息 -->
        <el-card class="detail-card" shadow="hover" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>👤 访客信息</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :xs="24" :md="12">
              <p><strong>访客ID：</strong>{{ selectedRow.visitorId }}</p>
              <p><strong>访客名称：</strong>{{ selectedRow.visitorName }}</p>
            </el-col>
          </el-row>
        </el-card>

        <!-- 允许区域 -->
        <el-card class="detail-card" shadow="hover" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>📍 允许进入区域</span>
            </div>
          </template>
          <div class="areas-display">
            <el-tag
              v-for="(area, index) in parseAreas(selectedRow.allowedAreas)"
              :key="index"
              style="margin: 8px"
            >
              {{ area }}
            </el-tag>
          </div>
        </el-card>

        <!-- 二维码 -->
        <el-card class="detail-card" shadow="hover" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>📱 二维码凭证</span>
            </div>
          </template>
          <div style="text-align: center">
            <el-image
              style="width: 250px; height: 250px; border: 1px solid #ddd; padding: 10px"
              :src="`https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=${selectedRow.qrCode}`"
              fit="contain"
            />
          </div>
        </el-card>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 生成凭证对话框 -->
    <el-dialog v-model="generateDialogVisible" title="生成门禁凭证" width="600px">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="访客">
          <el-input
            :value="generateForm.visitorName"
            disabled
          />
        </el-form-item>

        <el-form-item label="生效时间">
          <el-date-picker
            v-model="generateForm.effectiveTime"
            type="datetime"
            placeholder="选择生效时间"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="失效时间">
          <el-date-picker
            v-model="generateForm.expiryTime"
            type="datetime"
            placeholder="选择失效时间"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="允许区域">
          <el-select
            v-model="generateForm.allowedAreas"
            multiple
            placeholder="选择允许进入的区域"
            style="width: 100%"
          >
            <el-option label="大楼A1" value="A1" />
            <el-option label="大楼A2" value="A2" />
            <el-option label="大楼B1" value="B1" />
            <el-option label="大楼B2" value="B2" />
            <el-option label="大楼C1" value="C1" />
            <el-option label="大楼C2" value="C2" />
            <el-option label="会议室D1" value="D1" />
            <el-option label="会议室D2" value="D2" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="generateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmGenerate" :loading="generateLoading">
          生成凭证
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  generateAccessCredential,
  listValidAuthorizations,
  revokeAuthorization,
  getAuthorization
} from '@/api/access'
import { listAppointments } from '@/api/appointment'

// 状态管理
const loading = ref(false)
const pendingLoading = ref(false)
const generateLoading = ref(false)
const tableData = ref([])
const pendingAppointmentList = ref([])
const allAuthorizations = ref([])
const allAppointments = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const activeTab = ref('valid')

// 对话框状态
const qrcodeDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const generateDialogVisible = ref(false)
const selectedRow = ref({})
const selectedQRCode = ref('')

// 搜索表单
const searchForm = reactive({
  visitorName: '',
  status: '',
  dateRange: []
})

// 生成凭证表单
const generateForm = reactive({
  appointmentId: '',
  visitorId: '',
  visitorName: '',
  effectiveTime: new Date(),
  expiryTime: new Date(Date.now() + 24 * 60 * 60 * 1000), // 默认明天过期
  allowedAreas: []
})

// 统计数据
const validCount = computed(() => allAuthorizations.value.filter(a => a.status === '1').length)
const expiredCount = computed(() => allAuthorizations.value.filter(a => a.status === '0').length)
const pendingAppointments = computed(() => allAppointments.value.filter(a => a.status === '2' && !hasAuthorization(a.id)).length)

// 工具函数
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
  return date.toLocaleDateString('zh-CN')
}

const parseAreas = (areasJson) => {
  try {
    if (typeof areasJson === 'string') {
      return JSON.parse(areasJson) || []
    }
    return areasJson || []
  } catch (e) {
    return []
  }
}

const getValidityPercentage = (effectiveTime, expiryTime) => {
  const now = Date.now()
  const start = new Date(effectiveTime).getTime()
  const end = new Date(expiryTime).getTime()
  const total = end - start
  const elapsed = Math.max(0, now - start)
  return Math.min(100, Math.round((elapsed / total) * 100))
}

const getValidityStatus = (expiryTime) => {
  const now = Date.now()
  const end = new Date(expiryTime).getTime()
  if (end < now) return 'exception'
  if (end - now < 3 * 24 * 60 * 60 * 1000) return 'warning'
  return 'success'
}

const getRemainingDays = (expiryTime) => {
  const now = Date.now()
  const end = new Date(expiryTime).getTime()
  const diff = end - now
  if (diff < 0) return '已过期'
  const days = Math.ceil(diff / (24 * 60 * 60 * 1000))
  return `${days} 天`
}

const hasAuthorization = (appointmentId) => {
  return allAuthorizations.value.some(a => a.appointmentId === appointmentId)
}

// 数据加载
const loadData = async () => {
  loading.value = true
  try {
    const [authorizations, appointments] = await Promise.all([
      listValidAuthorizations(0), // 获取所有授权，0 表示查询所有
      listAppointments({})
    ])

    allAuthorizations.value = authorizations || []
    allAppointments.value = appointments || []

    filterTableData()
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const filterTableData = () => {
  let filtered = [...allAuthorizations.value]

  // 按标签页过滤
  switch (activeTab.value) {
    case 'valid':
      filtered = filtered.filter(a => a.status === '1')
      break
    case 'invalid':
      filtered = filtered.filter(a => a.status === '0')
      break
    case 'pending':
      // 待授权预约单独处理
      pendingAppointmentList.value = allAppointments.value.filter(a =>
        a.status === '2' && !hasAuthorization(a.id)
      )
      return
  }

  // 按搜索条件过滤
  if (searchForm.visitorName) {
    filtered = filtered.filter(a =>
      a.visitorName?.includes(searchForm.visitorName)
    )
  }

  if (searchForm.status) {
    filtered = filtered.filter(a => a.status === searchForm.status)
  }

  if (searchForm.dateRange && searchForm.dateRange.length === 2) {
    const [start, end] = searchForm.dateRange
    filtered = filtered.filter(a => {
      const expiryTime = new Date(a.expiryTime)
      return expiryTime >= start && expiryTime <= end
    })
  }

  // 排序和分页
  filtered.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  total.value = filtered.length
  tableData.value = filtered.slice(
    (currentPage.value - 1) * pageSize.value,
    currentPage.value * pageSize.value
  )
}

// 搜索和过滤
const handleSearch = () => {
  currentPage.value = 1
  filterTableData()
}

const handleReset = () => {
  searchForm.visitorName = ''
  searchForm.status = ''
  searchForm.dateRange = []
  currentPage.value = 1
  filterTableData()
}

const handleTabChange = () => {
  currentPage.value = 1
  filterTableData()
}

const handleRefreshPending = () => {
  loadData()
}

// 二维码操作
const handleViewQRCode = (row) => {
  selectedRow.value = row
  selectedQRCode.value = row.qrCode
  qrcodeDialogVisible.value = true
}

const handleCopyQRCode = () => {
  navigator.clipboard.writeText(selectedQRCode.value)
  ElMessage.success('二维码已复制到剪贴板')
}

const handleDownloadQRCode = () => {
  const link = document.createElement('a')
  link.href = `https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=${selectedQRCode.value}`
  link.download = `qrcode-${selectedRow.value.visitorName}.png`
  link.click()
}

// 详情操作
const handleViewDetails = (row) => {
  selectedRow.value = row
  detailDialogVisible.value = true
}

// 撤销授权
const handleRevoke = (row) => {
  ElMessageBox.confirm(
    `确定要撤销对 ${row.visitorName} 的门禁授权吗?`,
    '撤销确认',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await revokeAuthorization(row.id)
      ElMessage.success('授权已撤销')
      loadData()
    } catch (error) {
      ElMessage.error('撤销失败')
    }
  }).catch(() => {})
}

// 生成凭证
const handleGenerateCredential = (row) => {
  generateForm.appointmentId = row.id
  generateForm.visitorId = row.visitorId
  generateForm.visitorName = row.visitorName
  generateForm.effectiveTime = new Date(row.appointmentTime)
  generateForm.expiryTime = new Date(row.appointmentEndTime)
  generateForm.allowedAreas = ['A1', 'B1']
  generateDialogVisible.value = true
}

const handleConfirmGenerate = async () => {
  if (!generateForm.allowedAreas || generateForm.allowedAreas.length === 0) {
    ElMessage.warning('请选择允许进入的区域')
    return
  }

  generateLoading.value = true
  try {
    await generateAccessCredential({
      appointmentId: generateForm.appointmentId,
      visitorId: generateForm.visitorId,
      effectiveTime: generateForm.effectiveTime,
      expiryTime: generateForm.expiryTime,
      allowedAreas: JSON.stringify(generateForm.allowedAreas)
    })

    ElMessage.success('门禁凭证已生成')
    generateDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '生成失败')
  } finally {
    generateLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.access-container {
  padding: 20px;
}

.box-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.header-section {
  padding: 20px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: 4px;
  margin-bottom: 20px;
}

.filter-section {
  padding: 0;
}

.button-group {
  display: flex;
  gap: 10px;
  width: 100%;
}

.button-group :deep(.el-button) {
  flex: 1;
}

.expand-content {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.detail-item {
  margin-bottom: 20px;
}

.detail-item .label {
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 10px;
}

.detail-item p {
  margin: 8px 0;
  color: #666;
  font-size: 13px;
}

.qrcode-section {
  margin-top: 15px;
}

.qrcode-section .label {
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 10px;
}

.visitor-cell {
  padding: 8px 0;
}

.visitor-cell .name {
  font-weight: 600;
  color: #333;
  margin: 5px 0;
}

.visitor-cell .id {
  color: #999;
  font-size: 12px;
  margin: 5px 0;
}

.credential-cell {
  padding: 8px 0;
}

.credential-cell p {
  margin: 6px 0;
  font-size: 13px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 6px;
}

.validity-cell {
  padding: 8px 0;
}

.validity-cell .start,
.validity-cell .end {
  font-size: 12px;
  color: #666;
  margin: 4px 0;
}

.qrcode-dialog {
  padding: 20px;
  text-align: center;
}

.qrcode-container {
  margin-bottom: 20px;
}

.qrcode-info {
  text-align: left;
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.qrcode-info p {
  margin: 10px 0;
  font-size: 13px;
  color: #666;
}

.detail-dialog {
  max-height: 600px;
  overflow-y: auto;
}

.detail-card {
  border-radius: 4px;
}

.detail-card :deep(.el-card__header) {
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}

.card-header {
  font-weight: 600;
  color: #333;
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-card p {
  margin: 10px 0;
  font-size: 13px;
  color: #666;
}

.detail-card strong {
  color: #333;
  min-width: 100px;
  display: inline-block;
}

.areas-display {
  padding: 10px 0;
}

.time-end {
  color: #999;
  font-size: 12px;
  margin-top: 4px;
}

.reason {
  color: #999 !important;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .button-group {
    flex-direction: column;
  }

  :deep(.el-table__cell) {
    padding: 10px 8px !important;
    font-size: 12px !important;
  }
}
</style>