import request from './request'

/**
 * 创建预约
 */
export function createAppointment(data) {
  return request({
    url: '/appointment/create',
    method: 'post',
    data
  })
}

/**
 * 查询预约详情
 */
export function getAppointment(id) {
  return request({
    url: `/appointment/${id}`,
    method: 'get'
  })
}

/**
 * 查询预约列表
 */
export function listAppointments(params) {
  return request({
    url: '/appointment/list',
    method: 'get',
    params
  })
}

/**
 * 取消预约
 */
export function cancelAppointment(id) {
  return request({
    url: `/appointment/cancel/${id}`,
    method: 'put'
  })
}