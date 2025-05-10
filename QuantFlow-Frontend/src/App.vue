<template>
  <div class="app">
    <nav class="navbar">
      <div class="nav-left">
        <router-link to="/" class="nav-brand">量学通QuantFlow</router-link>
        <div class="nav-links">
          <router-link to="/education">在线教育</router-link>
          <router-link to="/strategy">量化策略</router-link>
          <router-link to="/trading">模拟交易</router-link>
          <router-link to="/resultlist">回测报告</router-link>
        </div>
      </div>

      <div class="nav-right">
        <div class="auth-buttons" v-if="!userStore.isLoggedIn">
          <button class="btn-login" @click="showLoginModal = true">登录</button>
          <button class="btn-register" @click="showRegisterModal = true">注册</button>
        </div>
        <div class="user-info" v-else>
          <span class="welcome">欢迎，{{ userStore.userInfo?.username }}</span>
          <router-link 
            v-if="userStore.userInfo?.role === 'admin'" 
            to="/admin/courses" 
            class="btn-admin"
          >后台管理</router-link>
          <button class="btn-logout" @click="handleLogout">退出</button>
        </div>
      </div>
    </nav>

    <router-view></router-view>

    <!-- 登录弹窗 -->
    <LoginModal 
      v-if="showLoginModal" 
      @close="showLoginModal = false"
      @success="handleLoginSuccess"
    />

    <!-- 注册弹窗 -->
    <RegisterModal 
      v-if="showRegisterModal" 
      @close="showRegisterModal = false"
      @success="handleRegisterSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import LoginModal from '@/components/auth/LoginModal.vue'
import RegisterModal from '@/components/auth/RegisterModal.vue'

const showLoginModal = ref(false)
const showRegisterModal = ref(false)
const userStore = useUserStore()

const handleLoginSuccess = (user: any) => {
  showLoginModal.value = false
}

const handleRegisterSuccess = () => {
  alert('注册成功')
  showRegisterModal.value = false
  showLoginModal.value = true
}

const handleLogout = () => {
  userStore.logout()
}

onMounted(() => {
  userStore.loadUserFromStorage()
})
</script>

<style>
.app {
  font-family: Arial, sans-serif;
}

.navbar {
  padding: 1rem 2rem;
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 3rem;
}

.nav-brand {
  font-size: 1.5rem;
  font-weight: bold;
  color: #2c3e50;
  text-decoration: none;
}

.nav-links {
  display: flex;
  gap: 2rem;
}

.nav-links a {
  color: #2c3e50;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
  padding: 0.5rem 1rem;
  border-radius: 4px;
}

.nav-links a:hover,
.nav-links a.router-link-active {
  color: #3498db;
  background-color: rgba(52, 152, 219, 0.1);
}

.nav-right {
  display: flex;
  align-items: center;
}

.auth-buttons {
  display: flex;
  gap: 1rem;
}

.btn-login,
.btn-register,
.btn-logout {
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.btn-login {
  background-color: #3498db;
  color: white;
  border: none;
}

.btn-login:hover {
  background-color: #2980b9;
}

.btn-register {
  background-color: transparent;
  color: #3498db;
  border: 1px solid #3498db;
}

.btn-register:hover {
  background-color: rgba(52, 152, 219, 0.1);
}

.btn-logout {
  background-color: transparent;
  color: #666;
  border: 1px solid #ddd;
}

.btn-logout:hover {
  background-color: #f5f5f5;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.welcome {
  color: #666;
}

.btn-admin {
  background-color: #2c3e50;
  color: white;
  padding: 6px 12px;
  border-radius: 4px;
  text-decoration: none;
}
</style>