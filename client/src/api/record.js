import request from './request'

/**
 * 记录通行
 */
export function recordAccess(data, status, failureReason) {
  return request({
    url: '/record/record',
    method: 'post',
    data,
    params: { status, failureReason }
  })
}

/**
 * 查询通行记录
 */
export function listAccessRecords(params) {
  return request({
    url: '/record/list',
    method: 'get',
    params
  })
}

/**
 * 查询异常通行记录
 */
export function listAbnormalRecords() {
  return request({
    url: '/record/abnormal',
    method: 'get'
  })
}

/**
 * 查询当前园区人数
 */
export function getCurrentPeopleCount() {
  return request({
    url: '/record/current-count',
    method: 'get'
  })
}