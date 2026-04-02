<template>
  <div class="page-container">
    <h2>预约详情</h2>
    <div v-if="loading">加载中...</div>
    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="record" class="detail-card">
      <dl>
        <dt>ID</dt>
        <dd>{{ record.id }}</dd>
        <dt>访客姓名</dt>
        <dd>{{ record.visitorName }}</dd>
        <dt>手机号</dt>
        <dd>{{ record.visitorMobile }}</dd>
        <dt>邮箱</dt>
        <dd>{{ record.visitorEmail || '-' }}</dd>
        <dt>证件</dt>
        <dd>{{ record.idType || '-' }} {{ record.idNumber || '-' }}</dd>
        <dt>受访人</dt>
        <dd>{{ record.hostName }}</dd>
        <dt>受访人手机号</dt>
        <dd>{{ record.hostMobile || '-' }}</dd>
        <dt>来访事由</dt>
        <dd>{{ record.visitReason }}</dd>
        <dt>来访地点</dt>
        <dd>{{ record.visitAddress || '-' }}</dd>
        <dt>预约时间</dt>
        <dd>{{ formatDateRange(record.appointmentStart, record.appointmentEnd) }}</dd>
        <dt>状态</dt>
        <dd>{{ statusText(record.status) }}</dd>
        <dt>审核备注</dt>
        <dd>{{ record.auditRemark || '-' }}</dd>
      </dl>
      <div class="action-row">
        <button @click="back">返回</button>
        <button @click="edit">编辑</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppointmentStore } from '../store/useAppointmentStore'

const route = useRoute()
const router = useRouter()
const appointmentStore = useAppointmentStore()

const record = computed(() => appointmentStore.current)
const loading = computed(() => appointmentStore.loading)
const error = computed(() => appointmentStore.error)

function back() {
  router.back()
}

function edit() {
  router.push({ name: 'AppointmentEdit', params: { id: route.params.id } })
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

onMounted(async () => {
  await appointmentStore.loadById(route.params.id)
})
</script>

<style scoped>
.page-container {
  max-width: 760px;
  margin: 24px auto;
  padding: 0 16px;
}
.detail-card {
  background: #ffffff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 20px;
}
.detail-card dl {
  display: grid;
  grid-template-columns: 120px 1fr;
  row-gap: 10px;
}
.detail-card dt {
  color: #909399;
}
.detail-card dd {
  margin: 0;
  color: #303133;
}
.action-row {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}
button {
  padding: 8px 18px;
  border: 1px solid #409eff;
  background: #409eff;
  color: #fff;
  cursor: pointer;
  border-radius: 4px;
}
button:hover {
  opacity: 0.92;
}
.error {
  color: #f56c6c;
}
</style>
