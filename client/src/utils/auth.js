/**
 * 认证工具类
 */

// 获取 Token
export function getToken() {
  return localStorage.getItem('token')
}

// 设置 Token
export function setToken(token) {
  localStorage.setItem('token', token)
}

// 删除 Token
export function removeToken() {
  localStorage.removeItem('token')
}

// 获取用户信息
export function getUserInfo() {
  const userInfo = localStorage.getItem('userInfo')
  return userInfo ? JSON.parse(userInfo) : null
}

// 设置用户信息
export function setUserInfo(userInfo) {
  localStorage.setItem('userInfo', JSON.stringify(userInfo))
}

// 删除用户信息
export function removeUserInfo() {
  localStorage.removeItem('userInfo')
}

// 清空所有认证信息
export function clearAuth() {
  removeToken()
  removeUserInfo()
}