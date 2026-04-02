import request from './request'

/**
 * 生成门禁凭证
 */
export function generateAccessCredential(data) {
  return request({
    url: '/access/generate',
    method: 'post',
    data
  })
}

/**
 * 查询授权信息
 */
export function getAuthorization(id) {
  return request({
    url: `/access/${id}`,
    method: 'get'
  })
}

/**
 * 根据二维码查询
 */
export function getAuthorizationByQRCode(qrCode) {
  return request({
    url: `/access/qrcode/${qrCode}`,
    method: 'get'
  })
}

/**
 * 撤销授权
 */
export function revokeAuthorization(id) {
  return request({
    url: `/access/revoke/${id}`,
    method: 'put'
  })
}

/**
 * 查询有效授权
 */
export function listValidAuthorizations(visitorId) {
  return request({
    url: `/access/valid/${visitorId}`,
    method: 'get'
  })
}

/**
 * 验证访问权限
 */
export function validateAccess(qrCode, areaCode) {
  return request({
    url: '/access/validate',
    method: 'post',
    params: { qrCode, areaCode }
  })
}

/**
 * 查询所有授权（支持分页和过��）
 */
export function listAuthorizations(params) {
  return request({
    url: '/access/list',
    method: 'get',
    params
  })
}