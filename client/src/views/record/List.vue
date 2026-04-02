<template>
  <div class="record-container">
    <div class="stats">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-statistic title="当前园区人数" :value="currentCount">
            <template #prefix>
              <el-icon color="#409eff"><Users /></el-icon>
            </template>
          </el-statistic>
        </el-col>
      </el-row>
    </div>

    <div class="search-form">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select
            v-model="searchForm.accessType"
            placeholder="选择通行类型"
            clearable
          >
            <el-option label="进" value="IN" />
            <el-option label="出" value="OUT" />
          </el-select>
        </el-col>
        <el-col :span="12">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleSearch">
            查询
          </el-button>
          <el-button @click="handleReset">
            重置
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
        <el-table-column prop="id" label="记录ID" width="100" />
        <el-table-column prop="visitorName" label="访客姓名" width="120" />
        <el-table-column prop="accessType" label="通行类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.accessType === 'IN' ? 'success' : 'info'">
              {{ row.accessType === 'IN' ? '进' : '出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="accessPoint" label="门禁点" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '1' ? 'success' : 'danger'">
              {{ row.status === '1' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="accessTime" label="通行时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.accessTime) }}
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listAccessRecords, getCurrentPeopleCount } from '@/api/record'

const loading = ref(false)
const tableData = ref([])
const currentCount = ref(0)
const dateRange = ref([])

const searchForm = reactive({
  accessType: ''
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const handleSearch = async () => {
  loading.value = true
  try {
    const params = {}
    if (searchForm.accessType) params.accessType = searchForm.accessType
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0].toISOString()
      params.endTime = dateRange.value[1].toISOString()
    }

    const res = await listAccessRecords(params)
    tableData.value = res || []
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.accessType = ''
  dateRange.value = []
  handleSearch()
}

const loadCurrentCount = async () => {
  try {
    const count = await getCurrentPeopleCount()
    currentCount.value = count
  } catch (error) {
    console.error('加载人数失败', error)
  }
}

onMounted(() => {
  handleSearch()
  loadCurrentCount()
  // 每30秒刷新一次人数统计
  setInterval(loadCurrentCount, 30000)
})
</script>

<style scoped>
.record-container {
  background: white;
  padding: 20px;
  border-radius: 4px;
}

.stats {
  margin-bottom: 30px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.search-form {
  margin-bottom: 20px;
}

.table-container {
  margin-top: 20px;
}
</style>