<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <div class="modal-header">
        <h3>回测参数配置</h3>
        <span class="close-btn" @click="handleClose">&times;</span>
      </div>
      <div class="form-container">
        <div class="form-group">
          <label>开始日期</label>
          <input 
            type="text" 
            v-model="formData.start_time"
            placeholder="格式：20240101"
            @blur="validateDate('start_time')"
          >
          <span class="error" v-if="errors.start_time">{{ errors.start_time }}</span>
        </div>
        
        <div class="form-group">
          <label>结束日期</label>
          <input 
            type="text" 
            v-model="formData.end_time"
            placeholder="格式：20241231"
            @blur="validateDate('end_time')"
          >
          <span class="error" v-if="errors.end_time">{{ errors.end_time }}</span>
        </div>

        <div class="form-group">
          <label>股票代码</label>
          <input 
            type="text" 
            v-model="formData.stock_code"
            placeholder="例如：000001.SZ"
            @blur="validateStockCode"
          >
          <span class="error" v-if="errors.stock_code">{{ errors.stock_code }}</span>
        </div>
        <div class="form-group">
          <label>初始资金</label>
          <input 
            type="number" 
            v-model.number="formData.cash"
            placeholder="请输入初始资金"
            @blur="validateCash"
          >
          <span class="error" v-if="errors.cash">{{ errors.cash }}</span>
        </div>
        <div class="form-group">
          <label>每次买入资金百分比</label>
          <input 
            type="number" 
            v-model.number="formData.sizer"
            placeholder="请输入每次买入资金百分比"
            @blur="validateSizer"
          >
          <span class="error" v-if="errors.sizer">{{ errors.sizer }}</span>
        </div>
      <div class="modal-footer">
        <button class="btn cancel" @click="handleClose">取消</button>
        <button class="btn submit" @click="handleSubmit" :disabled="hasErrors">确认</button>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type {BackTestFormData} from '@/types/api'


interface Errors {
  start_time?: string
  end_time?: string
  stock_code?: string
  cash?: string
  sizer?: string
}

const emit = defineEmits(['close', 'submit'])

const formData = ref<BackTestFormData>({
  start_time: '',
  end_time: '',
  stock_code: '',
  cash: 100000,
  sizer: 20
})

const errors = ref<Errors>({})

const validateDate = (field: 'start_time' | 'end_time') => {
  const value = formData.value[field]
  if (!value) {
    errors.value[field] = '日期不能为空'
    return false
  }
  if (!/^\d{8}$/.test(value)) {
    errors.value[field] = '日期格式错误，应为8位数字'
    return false
  }
  const year = parseInt(value.substring(0, 4))
  const month = parseInt(value.substring(4, 6))
  const day = parseInt(value.substring(6, 8))
  
  if (year < 2000 || year > 2100 || month < 1 || month > 12 || day < 1 || day > 31) {
    errors.value[field] = '日期不合法'
    return false
  }
  errors.value[field] = ''
  return true
}

const validateStockCode = () => {
  const { stock_code } = formData.value
  if (!stock_code) {
    errors.value.stock_code = '股票代码不能为空'
    return false
  }
  if (!/^\d{6}\.[A-Z]{2}$/.test(stock_code)) {
    errors.value.stock_code = '股票代码格式错误，应为000001.SZ格式'
    return false
  }
  errors.value.stock_code = ''
  return true
}
const validateSizer = () => {
  const { sizer } = formData.value
  if (sizer <= 0 || sizer > 100) {
    errors.value.sizer = '买入资金百分比应在0到100之间'
    return false
  }
  errors.value.sizer = ''
  return true
}
const validateCash = () => {
  const { cash } = formData.value
  if (cash <= 0 || cash>2147483647)  {
    errors.value.cash = '初始资金必须大于0小于21亿'
    return false
  }
  errors.value.cash = ''
  return true
}
const hasErrors = computed(() => {
  return Object.values(errors.value).some(error => error !== '')
})

const handleSubmit = () => {
  if (validateDate('start_time') && validateDate('end_time') && validateStockCode()&&validateSizer()) {
    emit('submit', formData.value)
    emit('close')
  }
}

const handleClose = () => {
  emit('close')
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  width: 400px;
  border-radius: 8px;
  padding: 20px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
}

.form-group input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.error {
  color: red;
  font-size: 12px;
  margin-top: 5px;
}

.modal-footer {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  border: none;
}

.cancel {
  background: #f5f5f5;
}

.submit {
  background: #409eff;
  color: white;
}

.submit:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}

.close-btn {
  font-size: 24px;
  cursor: pointer;
  color: #666;
}
</style>
