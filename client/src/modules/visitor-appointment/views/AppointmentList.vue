<template>
  <div class="page-container">
    <h2>访客预约列表</h2>
    <div class="toolbar">
      <button @click="createNew">新增预约</button>
      <button @click="loadData">刷新</button>
    </div>

    <div v-if="loading">数据加载中...</div>
    <div v-if="error" class="error">{{ error }}</div>

    <table v-if="!loading" class="grid-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>访客</th>
          <th>手机号</th>
          <th>来访事由</th>
          <th>预约时间</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in appointments" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.visitorName }}</td>
          <td>{{ item.visitorMobile }}</td>
          <td>{{ item.visitReason }}</td>
          <td>{{ formatDateRange(item.appointmentStart, item.appointmentEnd) }}</td>
          <td>{{ statusText(item.status) }}</td>
          <td>
            <button @click="viewDetail(item.id)">查看</button>
            <button @click="editItem(item.id)">编辑</button>
            <button v-if="item.status === 0" @click="approve(item.id)">通过</button>
            <button v-if="item.status === 0" @click="reject(item.id)">拒绝</button>
            <button v-if="item.status === 0" @click="cancel(item.id)">取消</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAppointmentStore } from '../store/useAppointmentStore'

const router = useRouter()
const appointmentStore = useAppointmentStore()
const appointments = computed(() => appointmentStore.list)
const loading = computed(() => appointmentStore.loading)
const error = computed(() => appointmentStore.error)

function loadData() {
  appointmentStore.loadList()
}

function createNew() {
  router.push({ name: 'AppointmentCreate' })
}

function viewDetail(id) {
  router.push({ name: 'AppointmentDetail', params: { id } })
}

function editItem(id) {
  router.push({ name: 'AppointmentEdit', params: { id } })
}

async function approve(id) {
  await appointmentStore.changeStatus(id, 1)
}

async function reject(id) {
  await appointmentStore.changeStatus(id, 2, '审核不通过')
}

async function cancel(id) {
  await appointmentStore.changeStatus(id, 3, '预约取消')
}

function formatDateRange(start, end) {
  if (!start) return '-'
  return `${start.replace('T', ' ')} ${end ? '→ ' + end.replace('T', ' ') : ''}`
}

function statusText(value) {
  switch (value) {
    case 0:
      return '待审核'
    case 1:
      return '已通过'
    case 2:
      return '已拒绝'
    case 3:
      return '已取消'
    case 4:
      return '已完成'
    default:
      return '未知'
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-container {
  max-width: 960px;
  margin: 24px auto;
  padding: 0 16px;
}
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}
button {
  padding: 6px 14px;
  border: 1px solid #409eff;
  background: #409eff;
  color: #fff;
  cursor: pointer;
  border-radius: 4px;
}
button:hover {
  opacity: 0.92;
}
.grid-table {
  width: 100%;
  border-collapse: collapse;
}
.grid-table th,
.grid-table td {
  border: 1px solid #e8e8e8;
  padding: 10px;
  text-align: left;
}
.error {
  color: #f56c6c;
  margin-bottom: 12px;
}
</style>
