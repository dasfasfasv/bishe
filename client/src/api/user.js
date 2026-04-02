import request from './request'

/**
 * 用户登录
 */
export function login(data) {
  return request({
    url: '/system/user/login',
    method: 'post',
    data: {
      username: data.username,
      password: data.password
    }
  })
}

/**
 * 查询用户列表
 */
export function listUsers(params) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params
  })
}

/**
 * 查询用户详情
 */
export function getUser(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'get'
  })
}

/**
 * 添加用户
 */
export function addUser(data) {
  return request({
    url: '/system/user/add',
    method: 'post',
    data
  })
}

/**
 * 更新用户
 */
export function updateUser(data) {
  return request({
    url: '/system/user/update',
    method: 'put',
    data
  })
}

/**
 * 删除用户
 */
export function deleteUser(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'delete'
  })
}

/**
 * 修改密码
 */
export function changePassword(data) {
  return request({
    url: '/system/user/change-password',
    method: 'post',
    data
  })
}

/**
 * 重置密码
 */
export function resetPassword(id) {
  return request({
    url: `/system/user/reset-password/${id}`,
    method: 'post'
  })
}

/**
 * 更新用户状态
 */
export function updateUserStatus(id, status) {
  return request({
    url: `/system/user/status/${id}`,
    method: 'put',
    params: { status }
  })
}