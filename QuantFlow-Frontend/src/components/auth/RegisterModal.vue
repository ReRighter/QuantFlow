<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <h2>用户注册</h2>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="username">用户名</label>
          <input 
            type="text" 
            id="username" 
            v-model="formData.username"
            required
          >
        </div>
        <div class="form-group">
          <label for="email">邮箱</label>
          <input 
            type="email" 
            id="email" 
            v-model="formData.email"
            required
          >
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input 
            type="password" 
            id="password" 
            v-model="formData.password"
            required
          >
        </div>
        <div class="form-group">
          <label for="confirmPassword">确认密码</label>
          <input 
            type="password" 
            id="confirmPassword" 
            v-model="formData.confirmPassword"
            required
          >
        </div>
        <div class="error-message" v-if="error">{{ error }}</div>
        <div class="form-actions">
          <button type="submit" class="btn-submit" >注册</button>
          <button type="button" class="btn-cancel" @click="$emit('close')">取消</button>
        </div>
        <div class="form-footer">
          <span>已有账号？</span>
          <button 
            type="button" 
            class="btn-text" 
            @click="switchToLogin"
          >立即登录</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { Result } from '@/types/api'

const emit = defineEmits(['close', 'success'])

const formData = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const error = ref('')

const handleSubmit = async () => {
  error.value = '';
  if (formData.value.password !== formData.value.confirmPassword) {
    error.value = '两次输入的密码不一致'
    return
  }

  try {
    const baseUrl = import.meta.env.VITE_API_BASE_URL
    const response = await fetch(`${baseUrl}/users/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: formData.value.username,
        email: formData.value.email,
        password: formData.value.password
      })
    })

    const result: Result = await response.json()
    
    if (result.code === 200) {
      emit('success')
    } else {
      throw new Error(result.message)
    }
  } catch (err: any) {
    error.value = err.message || '注册失败，请重试'
  }
}

const switchToLogin = () => {
  emit('close')
  // TODO: 打开登录弹窗
}
</script>

<style scoped>
/* 使用与LoginModal相同的样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  width: 100%;
  max-width: 400px;
}

.modal-content h2 {
  margin-bottom: 1.5rem;
  color: #2c3e50;
  text-align: center;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #666;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group input:focus {
  outline: none;
  border-color: #3498db;
}

.error-message {
  color: #e74c3c;
  margin-bottom: 1rem;
  font-size: 0.9rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.btn-submit,
.btn-cancel {
  flex: 1;
  padding: 0.75rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.btn-submit {
  background-color: #3498db;
  color: white;
  border: none;
}

.btn-submit:hover {
  background-color: #2980b9;
}

.btn-cancel {
  background-color: transparent;
  color: #666;
  border: 1px solid #ddd;
}

.btn-cancel:hover {
  background-color: #f5f5f5;
}

.form-footer {
  text-align: center;
  color: #666;
}

.btn-text {
  background: none;
  border: none;
  color: #3498db;
  cursor: pointer;
  padding: 0;
  font-size: inherit;
}

.btn-text:hover {
  text-decoration: underline;
}
</style> 