<template>
  <div class="dashboard-container">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <el-statistic title="今日访客" :value="todayVisitors">
            <template #prefix>
              <el-icon color="#409EFF"><UserFilled /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <el-statistic title="待审批预约" :value="pendingAppointments">
            <template #prefix>
              <el-icon color="#E6A23C"><Notification /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <el-statistic title="园区人数" :value="currentPeople">
            <template #prefix>
              <el-icon color="#67C23A"><SuccessFilled /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <el-statistic title="黑名单数" :value="blacklistCount">
            <template #prefix>
              <el-icon color="#F56C6C"><Warning /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 访客趋势图 -->
      <el-col :xs="24" :md="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>访客趋势（最近7天）</span>
            </div>
          </template>
          <div ref="visitorTrendChart" style="height: 300px"></div>
        </el-card>
      </el-col>

      <!-- 预约状态分布 -->
      <el-col :xs="24" :md="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>预约状态分布</span>
            </div>
          </template>
          <div ref="appointmentStatusChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近活动 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近通行记录</span>
            </div>
          </template>
          <el-table
            :data="recentRecords"
            style="width: 100%"
            :loading="recordsLoading"
            max-height="400"
          >
            <el-table-column prop="visitorName" label="访客" width="120" />
            <el-table-column prop="accessType" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.accessType === 'IN' ? 'success' : 'info'">
                  {{ row.accessType === 'IN' ? '进' : '出' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="accessPoint" label="门禁点" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === '1' ? 'success' : 'danger'">
                  {{ row.status === '1' ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="accessTime" label="时间" width="200">
              <template #default="{ row }">
                {{ formatTime(row.accessTime) }}
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
import * as echarts from 'echarts'
import { listAccessRecords, getCurrentPeopleCount } from '@/api/record'
import { listAppointments } from '@/api/appointment'
import { listBlacklist } from '@/api/blacklist'
import { listVisitors } from '@/api/visitor'

const visitorTrendChart = ref(null)
const appointmentStatusChart = ref(null)
const todayVisitors = ref(0)
const pendingAppointments = ref(0)
const currentPeople = ref(0)
const blacklistCount = ref(0)
const recentRecords = ref([])
const recordsLoading = ref(false)

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN')
}

// 初始化访客趋势图
const initVisitorTrendChart = async () => {
  try {
    const records = await listAccessRecords({})
    
    // 统计最近7天的访客数
    const today = new Date()
    const data = []
    const dates = []

    for (let i = 6; i >= 0; i--) {
      const date = new Date(today)
      date.setDate(date.getDate() - i)
      dates.push(date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }))
      
      const count = records.filter(r => {
        const recordDate = new Date(r.accessTime)
        return recordDate.toDateString() === date.toDateString() && r.accessType === 'IN'
      }).length
      
      data.push(count)
    }

    const chart = echarts.init(visitorTrendChart.value)
    const option = {
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(50, 50, 50, 0.8)',
        borderColor: '#777',
        textStyle: { color: '#fff' }
      },
      grid: {
        left: '3%',
        right: '3%',
        bottom: '3%',
        top: '3%',
        containLabel: true
      },
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
          itemStyle: { color: '#409EFF' },
          areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409EFF' },
            { offset: 1, color: 'rgba(64, 158, 255, 0)' }
          ])}
        }
      ]
    }
    chart.setOption(option)
    window.addEventListener('resize', () => chart.resize())
  } catch (error) {
    console.error('初始化访客趋势图失败', error)
  }
}

// 初始化预约状态分布图
const initAppointmentStatusChart = async () => {
  try {
    const appointments = await listAppointments({})
    
    const statusMap = {
      '1': { name: '待审批', count: 0 },
      '2': { name: '已批准', count: 0 },
      '3': { name: '已拒绝', count: 0 },
      '4': { name: '已使用', count: 0 },
      '5': { name: '已过期', count: 0 },
      '6': { name: '已取消', count: 0 }
    }

    appointments.forEach(a => {
      if (statusMap[a.status]) {
        statusMap[a.status].count++
      }
    })

    const data = Object.values(statusMap).map(item => ({
      name: item.name,
      value: item.count
    })).filter(item => item.value > 0)

    const chart = echarts.init(appointmentStatusChart.value)
    const option = {
      tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(50, 50, 50, 0.8)',
        borderColor: '#777',
        textStyle: { color: '#fff' }
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '预约数',
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
    window.addEventListener('resize', () => chart.resize())
  } catch (error) {
    console.error('初始化预约状态图失败', error)
  }
}

// 加载仪表板数据
const loadDashboardData = async () => {
  try {
    // 加载访客数据
    const visitors = await listVisitors({})
    const today = new Date().toDateString()
    todayVisitors.value = visitors.filter(v => {
      const lastVisit = new Date(v.lastVisitTime).toDateString()
      return lastVisit === today
    }).length

    // 加载待审批预约
    const appointments = await listAppointments({ status: '1' })
    pendingAppointments.value = appointments.length

    // 加载园区人数
    currentPeople.value = await getCurrentPeopleCount()

    // 加载黑名单数
    const blacklist = await listBlacklist({})
    blacklistCount.value = blacklist.length

    // 加载最近通行记录
    recordsLoading.value = true
    const records = await listAccessRecords({})
    recentRecords.value = records.slice(0, 10)
    recordsLoading.value = false
  } catch (error) {
    console.error('加载仪表板数据失败', error)
  }
}

onMounted(() => {
  loadDashboardData()
  initVisitorTrendChart()
  initAppointmentStatusChart()
  
  // 每30秒刷新一次数据
  setInterval(loadDashboardData, 30000)
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  border-left: 4px solid #409EFF;
  background: linear-gradient(135deg, #ffffff 0%, #f5f7fa 100%);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.chart-card {
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #333;
}

:deep(.el-statistic__content) {
  font-size: 28px;
  font-weight: 600;
  color: #409EFF;
}

:deep(.el-statistic__title) {
  font-size: 14px;
  color: #666;
}
</style>