/**
 * 密码加密工具
 * 与后端保持一致：SHA-256 + salt + Base64
 */

const SALT = 'smart-campus-2024'

/**
 * 加密密码
 * @param {string} password 原始密码
 * @returns {Promise<string>} 加密后的密码
 */
export async function encryptPassword(password) {
  try {
    const saltPassword = password + SALT
    const encoder = new TextEncoder()
    const data = encoder.encode(saltPassword)
    const hashBuffer = await crypto.subtle.digest('SHA-256', data)
    
    // 将 SHA-256 结果转为 Base64
    const hashArray = new Uint8Array(hashBuffer)
    const binaryString = String.fromCharCode(...hashArray)
    return btoa(binaryString)
  } catch (error) {
    console.error('密码加密失败:', error)
    throw error
  }
}
