<template>
  <div class="page-container">
    <h2>{{ isEdit ? '编辑预约' : '新增预约' }}</h2>

    <form @submit.prevent="save">
      <div class="form-row">
        <label>访客姓名</label>
        <input v-model="form.visitorName" required />
      </div>
      <div class="form-row">
        <label>访客手机号</label>
        <input v-model="form.visitorMobile" required />
      </div>
      <div class="form-row">
        <label>证件类型</label>
        <input v-model="form.idType" placeholder="身份证/护照" />
      </div>
      <div class="form-row">
        <label>证件号码</label>
        <input v-model="form.idNumber" />
      </div>
      <div class="form-row">
        <label>受访人</label>
        <input v-model="form.hostName" required />
      </div>
      <div class="form-row">
        <label>受访人手机号</label>
        <input v-model="form.hostMobile" />
      </div>
      <div class="form-row">
        <label>来访事由</label>
        <input v-model="form.visitReason" required />
      </div>
      <div class="form-row">
        <label>来访地点</label>
        <input v-model="form.visitAddress" />
      </div>
      <div class="form-row">
        <label>预约开始</label>
        <input type="datetime-local" v-model="form.appointmentStart" required />
      </div>
      <div class="form-row">
        <label>预约结束</label>
        <input type="datetime-local" v-model="form.appointmentEnd" />
      </div>
      <div class="form-row">
        <label>车牌号</label>
        <input v-model="form.vehiclePlate" />
      </div>
      <div class="form-actions">
        <button type="button" @click="cancel">返回</button>
        <button type="submit">保存</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppointmentStore } from '../store/useAppointmentStore'

const route = useRoute()
const router = useRouter()
const appointmentStore = useAppointmentStore()

const isEdit = computed(() => Boolean(route.params.id))

const form = reactive({
  visitorName: '',
  visitorMobile: '',
  visitorEmail: '',
  idType: '',
  idNumber: '',
  hostId: null,
  hostName: '',
  hostMobile: '',
  visitReason: '',
  visitAddress: '',
  appointmentStart: '',
  appointmentEnd: '',
  vehiclePlate: ''
})

async function loadCurrent() {
  if (!isEdit.value) return
  await appointmentStore.loadById(route.params.id)
  const record = appointmentStore.current
  if (!record) {
    router.replace({ name: 'AppointmentList' })
    return
  }
  Object.assign(form, {
    visitorName: record.visitorName,
    visitorMobile: record.visitorMobile,
    visitorEmail: record.visitorEmail,
    idType: record.idType || '',
    idNumber: record.idNumber || '',
    hostId: record.hostId || null,
    hostName: record.hostName || '',
    hostMobile: record.hostMobile || '',
    visitReason: record.visitReason || '',
    visitAddress: record.visitAddress || '',
    appointmentStart: record.appointmentStart || '',
    appointmentEnd: record.appointmentEnd || '',
    vehiclePlate: record.vehiclePlate || ''
  })
}

async function save() {
  const payload = {
    visitorName: form.visitorName,
    visitorMobile: form.visitorMobile,
    visitorEmail: form.visitorEmail,
    idType: form.idType,
    idNumber: form.idNumber,
    hostId: form.hostId,
    hostName: form.hostName,
    hostMobile: form.hostMobile,
    visitReason: form.visitReason,
    visitAddress: form.visitAddress,
    appointmentStart: form.appointmentStart,
    appointmentEnd: form.appointmentEnd,
    vehiclePlate: form.vehiclePlate
  }
  try {
    if (isEdit.value) {
      await appointmentStore.update(route.params.id, payload)
    } else {
      await appointmentStore.create(payload)
    }
    router.push({ name: 'AppointmentList' })
  } catch (err) {
    alert(err.message)
  }
}

function cancel() {
  router.back()
}

onMounted(loadCurrent)
</script>

<style scoped>
.page-container {
  max-width: 700px;
  margin: 24px auto;
  padding: 0 16px;
}
.form-row {
  display: flex;
  align-items: center;
  margin-bottom: 14px;
}
.form-row label {
  width: 120px;
  color: #606266;
}
.form-row input {
  flex: 1;
  padding: 8px 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}
button {
  padding: 8px 18px;
  border: 1px solid #409eff;
  background: #409eff;
  color: #fff;
  cursor: pointer;
  border-radius: 4px;
}
button[type='button'] {
  background: #fff;
  color: #606266;
}
button:hover {
  opacity: 0.92;
}
</style>
