import request from './request'

/**
 * 添加访客
 */
export function addVisitor(data) {
  return request({
    url: '/visitor/add',
    method: 'post',
    data
  })
}

/**
 * 查询访客详情
 */
export function getVisitor(id) {
  return request({
    url: `/visitor/${id}`,
    method: 'get'
  })
}

/**
 * 根据身份证查询访客
 */
export function getVisitorByIdCard(idCard) {
  return request({
    url: `/visitor/idcard/${idCard}`,
    method: 'get'
  })
}

/**
 * 更新访客信息
 */
export function updateVisitor(data) {
  return request({
    url: '/visitor/update',
    method: 'put',
    data
  })
}

/**
 * 删除访客
 */
export function deleteVisitor(id) {
  return request({
    url: `/visitor/${id}`,
    method: 'delete'
  })
}

/**
 * 查询访客列表
 */
export function listVisitors(params) {
  return request({
    url: '/visitor/list',
    method: 'get',
    params
  })
}