import { defineStore } from 'pinia'
import { login } from '@/api/user'
import { setToken, removeToken, setUserInfo, removeUserInfo } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userInfo: {}
  }),

  getters: {
    isLoggedIn: (state) => !!state.token
  },

  actions: {
    // 登录
    async login(credentials) {
      try {
        const response = await login(credentials)
        const { token, userId, username, realName } = response

        this.token = token
        this.userInfo = {
          userId,
          username,
          realName
        }

        setToken(token)
        setUserInfo(this.userInfo)

        return response
      } catch (error) {
        throw error
      }
    },

    // 登出
    logout() {
      this.token = ''
      this.userInfo = {}
      removeToken()
      removeUserInfo()
    },

    // 设置用户信息
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      setUserInfo(userInfo)
    }
  }
})