import { defineStore } from 'pinia'
import type { UserInfo } from '@/types/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null as UserInfo | null,
    token: '' as string
  }),

  actions: {
    setUserInfo(user: UserInfo) {
      this.userInfo = user
      // 同时存储到 localStorage
      localStorage.setItem('userInfo', JSON.stringify(user))
    },

    setToken(token: string) {
      this.token = token
      localStorage.setItem('token', token)
    },

    loadUserFromStorage() {
      const userInfo = localStorage.getItem('userInfo')
      const token = localStorage.getItem('token')
      if (userInfo) {
        this.userInfo = JSON.parse(userInfo)
      }
      if (token) {
        this.token = token
      }
    },

    logout() {
      this.userInfo = null
      this.token = ''
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
    }
  },

  getters: {
    isLoggedIn(): boolean {
      return !!this.userInfo
    },
    getUserId(): number | undefined {
      return this.userInfo?.id
    }
  }
}) 