import request from './request'

/**
 * 添加黑名单
 */
export function addBlacklist(data) {
  return request({
    url: '/blacklist/add',
    method: 'post',
    data
  })
}

/**
 * 移除黑名单
 */
export function removeBlacklist(id) {
  return request({
    url: `/blacklist/${id}`,
    method: 'delete'
  })
}

/**
 * 查询黑名单列表
 */
export function listBlacklist(params) {
  return request({
    url: '/blacklist/list',
    method: 'get',
    params
  })
}

/**
 * 检查访客是否在黑名单中
 */
export function checkBlacklist(visitorId) {
  return request({
    url: `/blacklist/check/${visitorId}`,
    method: 'get'
  })
}

/**
 * 根据身份证��查黑名单
 */
export function checkBlacklistByIdCard(idCard) {
  return request({
    url: `/blacklist/check-idcard/${idCard}`,
    method: 'get'
  })
}

/**
 * 更新黑名单
 */
export function updateBlacklist(data) {
  return request({
    url: '/blacklist/update',
    method: 'put',
    data
  })
}