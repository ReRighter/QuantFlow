<template>
  <div class="strategy-home">
    <h1>量化交易策略管理</h1>
    
    <!-- 预设策略部分 -->
    <div class="strategy-section">
      <h2>预设策略</h2>
      <el-table :data="storedStrategies" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="策略名称" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleView(scope.row)">查看/保存</el-button>
            <el-button size="small" type="primary" @click="handleRun(scope.row)">运行</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 自定义策略部分 -->
    <div class="strategy-section">
      <div class="section-header">
        <h2>自定义策略</h2>
        <el-button type="success" @click="handleCreate">新建策略</el-button>
      </div>
      <el-table :data="customStrategies" border style="width: 100%">
        <!-- <el-table-column prop="id" label="ID" width="80" /> -->
        <el-table-column prop="name" label="策略名称" />
        <el-table-column label="操作" width="320">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">查看/修改</el-button>
            <el-button size="small" type="primary" @click="handleRun(scope.row)">运行</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
  <StrategyDetails
  v-show="showDetails"
  :strategy="currentStrategy"
  @save-as="handleSaveAs"
  @close="showDetails = false"
  />
  <BackTestDetails 
  v-show="showBackTest"
  @close="showBackTest = false"
  @submit="handleBackTestSubmit"
  />
  <BackTestReport
  v-if="showReport"
  :result="currentReport"
  @close="showReport = false"
  @save="()=>{}"
  />

</template>

<script setup lang="ts" name="StrategyHome">
import { onMounted, ref } from 'vue'
import {apiClient}  from '@/utils/apiClient'
import type { Result, Strategy ,BackTestFormData,BackTestResult} from '@/types/api';
import { useUserStore } from '@/stores/user';
import StrategyDetails from '@/components/strategy/StrategyDetails.vue';
import BackTestDetails from '@/components/strategy/BackTestDetails.vue';
import BackTestReport from '@/components/strategy/BackTestReport.vue';
const showDetails = ref(false);
const showBackTest = ref(false);
const currentStrategy = ref<Strategy>({}as Strategy);

// 预设策略数据
const storedStrategies = ref<Strategy[]>([])
const baseUrl:string = import.meta.env.VITE_API_BASE_URL;

const fetchStoredStrategies =async ()=>{
   const response=await apiClient(baseUrl+'/strategy/getStoredStrategies',{
    method: 'GET',
    headers: {
      credentials: 'include',
    }
  });
  const result:Result<Strategy[]>=await response.json();
  if (result.code == 200 && result.data) {
    result.data.sort((a, b) => a.id - b.id); // 按照ID升序排序
    storedStrategies.value = result.data;
  } else {
    alert('获取预设策略失败:'+ result.message);
  }

}
// 自定义策略数据
const customStrategies = ref<Strategy[]>([]);
const fetchCustomStrategies =async ()=>{
  const userId =useUserStore().userInfo?.id;
  if (!userId) {
    alert('用户未登录');
    return;
  }
  const response=await apiClient(baseUrl+`/strategy/getCustomStrategies/${userId}`,{
   method: 'GET',
   headers: {
     credentials: 'include',
   }
 });
 const result:Result<Strategy[]>=await response.json();
 if (result.code == 200 && result.data) {
   customStrategies.value = result.data;
 } else {
   alert('获取自定义策略失败:'+ result.message);
 }
}

onMounted(() => {
  fetchStoredStrategies();
  fetchCustomStrategies();
})





// 操作处理函数
const handleView = (row: Strategy) => {
  currentStrategy.value = row;
  showDetails.value = true;

}

const handleRun = (row: Strategy) => {
  currentStrategy.value= row;
  showBackTest.value=true;  
}
const currentReport = ref<BackTestResult>({} as BackTestResult);
const showReport = ref(false);

const handleBackTestSubmit=async (formData:BackTestFormData)=>{
  const response =await apiClient(`${import.meta.env.VITE_API_TRADING_URL}/strategy/backtest`, {
    method: 'POST',
    headers: {
      credentials: 'include',
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(formData),
  });
  const result:Result<BackTestResult>=await response.json();
  if (result.code == 200 && result.data) {
    currentReport.value = result.data;
    showReport.value= true;
  } else {
    alert('回测失败:'+ result.message);
  }
}

const handleCreate = () => {
  currentStrategy.value = {} as Strategy;
  showDetails.value = true; 
}

const handleEdit = (row: any) => {
  console.log('编辑策略:', row)
}



const handleDelete = async (row: Strategy) => {
  const response=await apiClient(baseUrl+`/strategy/delete/${row.id}`,{
      method: 'DELETE',
      headers: {
        credentials: 'include',
        'Content-Type': 'application/json',
      }
    });
  const result:Result<string>=await response.json();
  if (result.code == 200) {
    alert('删除成功');
    fetchCustomStrategies();
  } else {
    alert('删除失败:'+ result.message);
  }
}

const handleSaveAs=async (row :Strategy)=>{
  const userId = useUserStore().userInfo?.id;
    if (!userId) {
      alert('用户未登录');
      return;
    }
    const response=await apiClient(baseUrl+`/strategy/saveAs/${userId}`,{
      method: 'POST',
      headers: {
        credentials: 'include',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        name: row.name,
        code: row.code,
      }),
    });
    const result:Result<string>=await response.json();
    if (result.code == 200) {
      alert('保存成功');
      fetchCustomStrategies();
    } else {
      alert('保存失败:'+ result.message);
    }

}
</script>

<style scoped>
.strategy-home {
  padding: 20px;
}

.strategy-section {
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

h1 {
  font-size: 24px;
  margin-bottom: 30px;
}

h2 {
  font-size: 20px;
  margin-bottom: 20px;
}
</style>