<template>
  <div class="backtestResult-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>回测报告列表</span>
        </div>
      </template>

      <el-table :data="resultList" stripe style="width: 100%">
        <el-table-column prop="strategy_name" label="策略名称" />
        <el-table-column prop="stock_code" label="股票代码" />
        <el-table-column prop="start_date" label="开始日期" />
        <el-table-column prop="end_date" label="结束日期" />
        <el-table-column prop="earnings_rate" label="收益率">
          <template #default="scope">
            {{ (scope.row.earnings_rate * 100).toFixed(2) }}%
          </template>
        </el-table-column>
        <el-table-column prop="annual_returns" label="年化收益率">
          <template #default="scope">
            {{ (scope.row.annual_returns * 100).toFixed(2) }}%
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="120">
          <template #default="scope">
            <el-button link @click="viewReport(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <BackTestReport
      v-if="showReport"
      :result="currentReport"
      @close="showReport = false"
      @save="saveReport"
      :saveable="false"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import BackTestReport from '@/components/strategy/BackTestReport.vue'
import type { BackTestResultDetails, BackTestResult, Result } from '@/types/api'
import { apiClient } from '@/utils/apiClient'
import { useUserStore } from '@/stores/user'

const resultList = ref<BackTestResultDetails[]>([])
const showReport = ref(false)
const currentReport = ref<BackTestResult>({} as BackTestResult )

const viewReport = (row: BackTestResultDetails) => {
    let logs:string[] = JSON.parse(row.trading_log);
  currentReport.value = {
    start_date: row.start_date,
    end_date: row.end_date,
    stock_code: row.stock_code,
    initial_funding: row.initial_funding,
    end_funding: row.end_funding,
    trading_size: row.trading_size,
    trading_log: logs,
    earnings: row.earnings,
    earnings_rate: row.earnings_rate,
    annual_returns: row.annual_returns,
    sharpe_ratio: row.sharpe_ratio,
    max_drawdown: row.max_drawdown
  }
  showReport.value = true
}

const saveReport = () => {
  // 处理保存报告的逻辑
  showReport.value = false
}
const getBacktestResults = async () => {
  try {
    const userStore = useUserStore()
    const userId = userStore.userInfo?.id;
    if(!userId) {
      alert("请先登录")
      return []
    }
    const response = await apiClient(`${import.meta.env.VITE_API_BASE_URL}/backtest/listreport/${userId}`, {
      method: 'GET',
      headers: {
        credentials: 'include'
      }
    })
    const result:Result<BackTestResultDetails[]> = await response.json()
    if (result.code == 200 && result.data) {
      return result.data;
    } else {
      alert('获取回测结果失败:' + result.message)
      return []
    }
  } catch (error) {
    console.error('获取回测结果失败:', error)
    return []
  }
}

onMounted(async () => {
  try {
    const data = await getBacktestResults()
    resultList.value = data
  } catch (error) {
    console.error('获取回测结果列表失败:', error)
  }
})
</script>

<style scoped>
.backtestResult-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>