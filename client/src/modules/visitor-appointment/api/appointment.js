const STORAGE_KEY = 'visitor_appointment_data'

const defaultData = [
  {
    id: 1,
    visitorName: '张三',
    visitorMobile: '13800000000',
    visitorEmail: 'zhangsan@example.com',
    idType: '身份证',
    idNumber: '110101199001010001',
    hostId: 101,
    hostName: '李经理',
    hostMobile: '13900000001',
    visitReason: '商务洽谈',
    visitAddress: 'A座大会议室',
    appointmentStart: '2026-04-10T10:00',
    appointmentEnd: '2026-04-10T11:00',
    vehiclePlate: '京A12345',
    status: 0,
    auditUserId: null,
    auditTime: null,
    auditRemark: null,
    createTime: '2026-03-31T09:00:00',
    updateTime: '2026-03-31T09:00:00'
  }
]

function loadStorage() {
  const raw = window.localStorage.getItem(STORAGE_KEY)
  if (!raw) {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(defaultData))
    return [...defaultData]
  }
  try {
    return JSON.parse(raw)
  } catch (e) {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(defaultData))
    return [...defaultData]
  }
}

function saveStorage(data) {
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(data))
}

function nextId(data) {
  return data.length ? Math.max(...data.map(item => item.id)) + 1 : 1
}

function clone(data) {
  return JSON.parse(JSON.stringify(data))
}

export async function fetchAppointments() {
  const list = loadStorage()
  return clone(list)
}

export async function fetchAppointmentById(id) {
  const list = loadStorage()
  const item = list.find(record => record.id === Number(id))
  return item ? clone(item) : null
}

export async function createAppointment(payload) {
  const list = loadStorage()
  const now = new Date().toISOString()
  const record = {
    ...payload,
    id: nextId(list),
    status: 0,
    auditUserId: null,
    auditTime: null,
    auditRemark: null,
    createTime: now,
    updateTime: now
  }
  list.unshift(record)
  saveStorage(list)
  return clone(record)
}

export async function updateAppointment(id, payload) {
  const list = loadStorage()
  const idx = list.findIndex(record => record.id === Number(id))
  if (idx === -1) {
    throw new Error('预约不存在')
  }
  list[idx] = {
    ...list[idx],
    ...payload,
    updateTime: new Date().toISOString()
  }
  saveStorage(list)
  return clone(list[idx])
}

export async function patchAppointmentStatus(id, status, remark = '') {
  const list = loadStorage()
  const idx = list.findIndex(record => record.id === Number(id))
  if (idx === -1) {
    throw new Error('预约不存在')
  }
  list[idx] = {
    ...list[idx],
    status,
    auditRemark: remark,
    auditTime: new Date().toISOString(),
    updateTime: new Date().toISOString()
  }
  saveStorage(list)
  return clone(list[idx])
}

export async function deleteAppointment(id) {
  const list = loadStorage()
  const next = list.filter(record => record.id !== Number(id))
  saveStorage(next)
  return true
}
