<template>
  <div class="modal-overlay" >
    <div class="modal-content">
      <div class="modal-header">
        <h3>回测报告</h3>
        <span class="close-btn" @click="handleClose">&times;</span>
      </div>
      
      <div class="report-content">
        <!-- 基础信息 -->
        <div class="info-section">
          <div class="info-row">
            <div class="info-item">
              <label>回测区间：</label>
              <span>{{ result.start_date }} 至 {{ result.end_date }}</span>
            </div>
            <div class="info-item">
              <label>股票代码：</label>
              <span>{{ result.stock_code }}</span>
            </div>
          </div>
          
          <div class="metrics-grid">
            <div class="metric-item">
              <div class="metric-value">{{ formatMoney(result.initial_funding) }}</div>
              <div class="metric-label">初始资金</div>
            </div>
            <div class="metric-item">
              <div class="metric-value">{{ formatMoney(result.end_funding) }}</div>
              <div class="metric-label">最终资金</div>
            </div>
            <div class="metric-item">
              <div class="metric-value" :class="{ 'positive': result.earnings > 0, 'negative': result.earnings < 0 }">
                {{ formatMoney(result.earnings) }}
              </div>
              <div class="metric-label">收益金额</div>
            </div>
            <div class="metric-item">
              <div class="metric-value" :class="{ 'positive': result.earnings_rate > 0, 'negative': result.earnings_rate < 0 }">
                {{ formatPercent(result.earnings_rate) }}
              </div>
              <div class="metric-label">收益率</div>
            </div>
            <div class="metric-item">
              <div class="metric-value">{{ formatPercent(result.annual_returns) }}</div>
              <div class="metric-label">年化收益</div>
            </div>
            <div class="metric-item">
              <div class="metric-value">{{ formateSharpe(result.sharpe_ratio)}}</div>
              <div class="metric-label">夏普比率</div>
            </div>
            <div class="metric-item">
              <div class="metric-value">{{ result.max_drawdown.toFixed(2) }}%</div>
              <div class="metric-label">最大回撤</div>
            </div>
            <div class="metric-item">
              <div class="metric-value">{{ result.trading_size }}</div>
              <div class="metric-label">每次交易资金百分比</div>
            </div>
          </div>
        </div>

        <!-- 交易记录 -->
        <div class="trading-log">
          <div class="log-header" @click="toggleLog">
            <h4>交易记录</h4>
            <span class="toggle-icon">{{ showLog ? '▼' : '▶' }}</span>
          </div>
          <div v-show="showLog" class="log-content">
            <div v-for="(log, index) in result.trading_log" :key="index" class="log-item">
              {{ log }}
            </div>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn cancel" @click="handleClose">取消</button>
        <button v-if="saveable" class="btn save" @click="handleSave">保存记录</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { BackTestResult, BackTestResultDetails, Result } from '@/types/api'
import  {apiClient}  from '@/utils/apiClient';

const props = defineProps<{
  result: BackTestResult,
  saveable: boolean
}>()

const emit = defineEmits(['close', 'save'])
const showLog = ref(false)

const fetchLog = async(id:number)=> {
  try{
    const response = await apiClient(`${import.meta.env.VITE_API_BASE_URL}/backtest/getlog/${id}`,{
      method: 'GET',
      headers: {
        credentials: 'include',
      }
    })
    const result:Result<BackTestResultDetails[]> = await response.json()
    if (response.status == 200 && result.data) {
        return result.data
    } else {
      throw new Error('网络错误,获取日志失败')
    }
  }catch (error) {
    console.error('Error fetching log:', error)
    return null
  }
}
  

const toggleLog = () => {
  showLog.value = !showLog.value
  // if(showLog.value === true && !props.result.trading_log ) {
  //     props.result.trading_log = await fetchLog(props.result.id)
  // }
}

const formateSharpe = (value: number|undefined) => {
  if (value === undefined) {
    return 'N/A'
  }
  return value.toFixed(2)
}

const formatMoney = (value: number) => {
  return new Intl.NumberFormat('zh-CN', {
    style: 'currency',
    currency: 'CNY'
  }).format(value)
}

const formatPercent = (value: number) => {
  return (value * 100).toFixed(2) + '%'
}

const handleClose = () => {
  emit('close')
}

const handleSave = () => {
  emit('save', props.result)
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
  width: 80%;
  max-width: 1000px;
  border-radius: 8px;
  padding: 20px;
}

.report-content {
  max-height: 70vh;
  overflow-y: auto;
}

.info-section {
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.metric-item {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  text-align: center;
}

.metric-value {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 5px;
}

.metric-label {
  color: #666;
  font-size: 14px;
}

.positive { color: #67c23a; }
.negative { color: #f56c6c; }

.trading-log {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.log-header {
  padding: 10px 15px;
  background: #f5f7fa;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.log-content {
  padding: 15px;
  max-height: 300px;
  overflow-y: auto;
}

.log-item {
  padding: 8px;
  border-bottom: 1px solid #eee;
  font-family: monospace;
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

.cancel { background: #f5f5f5; }
.save { 
  background: #409eff;
  color: white;
}
</style>
