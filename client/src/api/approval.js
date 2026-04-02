import request from './request'

/**
 * 查询待审批列表（当前用户）
 */
export function listPendingApprovals() {
  return request({
    url: '/approval/pending',
    method: 'get'
  })
}

/**
 * 分页查询审批列表
 */
export function listApprovals(params) {
  return request({
    url: '/approval/list',
    method: 'get',
    params
  })
}

/**
 * 查询审批详情
 */
export function getApprovalDetail(id) {
  return request({
    url: `/approval/${id}`,
    method: 'get'
  })
}

/**
 * 查询审批历史
 */
export function getApprovalHistory(appointmentId) {
  return request({
    url: `/approval/history/${appointmentId}`,
    method: 'get'
  })
}

/**
 * 批准预约
 */
export function approveAppointment(appointmentId, remark) {
  return request({
    url: `/approval/approve/${appointmentId}`,
    method: 'post',
    params: { remark }
  })
}

/**
 * 拒绝预约
 */
export function rejectAppointment(appointmentId, remark) {
  return request({
    url: `/approval/reject/${appointmentId}`,
    method: 'post',
    params: { remark }
  })
}

/**
 * 退回预约
 */
export function returnAppointment(appointmentId, remark) {
  return request({
    url: `/approval/return/${appointmentId}`,
    method: 'post',
    params: { remark }
  })
}

/**
 * 获取审批统计
 */
export function getApprovalStatistics() {
  return request({
    url: '/approval/statistics',
    method: 'get'
  })
}

/**
 * 批量审批
 */
export function batchApprove(appointmentIds, status, remark) {
  return request({
    url: '/approval/batch',
    method: 'post',
    params: { appointmentIds, status, remark }
  })
}