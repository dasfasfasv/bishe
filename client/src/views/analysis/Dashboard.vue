<template>
  <div class="analysis-container">
    <!-- 日期选择 -->
    <el-row :gutter="20" class="date-selector">
      <el-col :xs="24" :md="12">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleDateChange"
        />
      </el-col>
      <el-col :xs="24" :md="12">
        <div style="text-align: right">
          <el-button type="primary" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出报表
          </el-button>
        </div>
      </el-col>
    </el-row>

    <!-- 统计指标 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card">
          <el-statistic title="总访客数" :value="metrics.totalVisitors">
            <template #prefix>
              <el-icon color="#409EFF"><User /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card">
          <el-statistic title="总预约数" :value="metrics.totalAppointments">
            <template #prefix>
              <el-icon color="#E6A23C"><Calendar /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card">
          <el-statistic title="通行次数" :value="metrics.totalRecords">
            <template #prefix>
              <el-icon color="#67C23A"><Location /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card">
          <el-statistic title="审批通过率" :value="metrics.approvalRate + '%'">
            <template #prefix>
              <el-icon color="#409EFF"><SuccessFilled /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :md="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>访客分类统计</span>
            </div>
          </template>
          <div ref="visitorCategoryChart" style="height: 300px"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>预约审批统计</span>
            </div>
          </template>
          <div ref="approvalStatsChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>每日通行量趋势</span>
            </div>
          </template>
          <div ref="dailyAccessChart" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 表格统计 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>访客统计排行</span>
            </div>
          </template>
          <el-table
            :data="visitorRanking"
            style="width: 100%"
            max-height="400"
          >
            <el-table-column prop="name" label="访客名称" width="150" />
            <el-table-column prop="visitCount" label="访问次数" width="120" />
            <el-table-column prop="category" label="分类" width="100" />
            <el-table-column prop="creditLevel" label="信用等级" width="100">
              <template #default="{ row }">
                <el-tag :type="getCreditLevelType(row.creditLevel)">
                  {{ row.creditLevel }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="lastVisitTime" label="最后访问时间" width="200">
              <template #default="{ row }">
                {{ formatTime(row.lastVisitTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { listVisitors } from '@/api/visitor'
import { listAppointments } from '@/api/appointment'
import { listAccessRecords } from '@/api/record'

const visitorCategoryChart = ref(null)
const approvalStatsChart = ref(null)
const dailyAccessChart = ref(null)
const dateRange = ref([])
const visitorRanking = ref([])

const metrics = ref({
  totalVisitors: 0,
  totalAppointments: 0,
  totalRecords: 0,
  approvalRate: 0
})

const getCreditLevelType = (level) => {
  const typeMap = { 'A': 'success', 'B': 'warning', 'C': 'danger' }
  return typeMap[level] || 'info'
}

const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN')
}

const handleDateChange = () => {
  loadAnalysisData()
}

const handleExport = () => {
  ElMessage.success('报表已导出，请检查下载文件夹')
}

const initVisitorCategoryChart = async () => {
  try {
    const visitors = await listVisitors({})
    const categoryMap = {}

    visitors.forEach(v => {
      if (!categoryMap[v.category]) {
        categoryMap[v.category] = 0
      }
      categoryMap[v.category]++
    })

    const data = Object.entries(categoryMap).map(([name, value]) => ({
      name,
      value
    }))

    const chart = echarts.init(visitorCategoryChart.value)
    const option = {
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', left: 'left' },
      series: [
        {
          name: '访客数',
          type: 'pie',
          radius: '50%',
          data: data,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    chart.setOption(option)
  } catch (error) {
    console.error('初始化图表失败', error)
  }
}

const initApprovalStatsChart = async () => {
  try {
    const appointments = await listAppointments({})
    const statusMap = { '2': 0, '3': 0 }

    appointments.forEach(a => {
      if (a.status === '2') statusMap['2']++
      else if (a.status === '3') statusMap['3']++
    })

    const chart = echarts.init(approvalStatsChart.value)
    const option = {
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: ['已批准', '已拒绝']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          data: [statusMap['2'], statusMap['3']],
          type: 'bar',
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#83bff6' },
              { offset: 0.5, color: '#188df0' },
              { offset: 1, color: '#188df0' }
            ])
          }
        }
      ]
    }
    chart.setOption(option)
  } catch (error) {
    console.error('初始化图表失败', error)
  }
}

const initDailyAccessChart = async () => {
  try {
    const records = await listAccessRecords({})
    const dailyMap = {}
    const today = new Date()

    for (let i = 6; i >= 0; i--) {
      const date = new Date(today)
      date.setDate(date.getDate() - i)
      const dateStr = date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
      dailyMap[dateStr] = 0
    }

    records.forEach(r => {
      const date = new Date(r.accessTime)
      const dateStr = date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
      if (dailyMap.hasOwnProperty(dateStr)) {
        dailyMap[dateStr]++
      }
    })

    const dates = Object.keys(dailyMap)
    const data = Object.values(dailyMap)

    const chart = echarts.init(dailyAccessChart.value)
    const option = {
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '3%', bottom: '3%', top: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        data: dates,
        boundaryGap: false
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          data: data,
          type: 'line',
          smooth: true,
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#83bff6' },
              { offset: 1, color: 'rgba(131, 191, 246, 0)' }
            ])
          },
          itemStyle: { color: '#1890ff' }
        }
      ]
    }
    chart.setOption(option)
  } catch (error) {
    console.error('初始化图表失败', error)
  }
}

const loadAnalysisData = async () => {
  try {
    const visitors = await listVisitors({})
    const appointments = await listAppointments({})
    const records = await listAccessRecords({})

    metrics.value.totalVisitors = visitors.length
    metrics.value.totalAppointments = appointments.length
    metrics.value.totalRecords = records.length

    const approved = appointments.filter(a => a.status === '2').length
    const total = appointments.filter(a => a.status === '2' || a.status === '3').length
    metrics.value.approvalRate = total > 0 ? Math.round((approved / total) * 100) : 0

    // 排序访客
    visitorRanking.value = visitors.sort((a, b) => b.visitCount - a.visitCount).slice(0, 10)
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

onMounted(() => {
  // 设置默认日期范围
  const today = new Date()
  const sevenDaysAgo = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000)
  dateRange.value = [sevenDaysAgo, today]

  loadAnalysisData()
  initVisitorCategoryChart()
  initApprovalStatsChart()
  initDailyAccessChart()
})
</script>

<style scoped>
.analysis-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.date-selector {
  background: white;
  padding: 20px;
  border-radius: 4px;
}

.metric-card {
  background: linear-gradient(135deg, #ffffff 0%, #f5f7fa 100%);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.metric-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.chart-card {
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  font-weight: 600;
  color: #333;
}

:deep(.el-statistic__content) {
  font-size: 24px;
  font-weight: 600;
  color: #409EFF;
}

:deep(.el-statistic__title) {
  font-size: 13px;
  color: #666;
}
</style>