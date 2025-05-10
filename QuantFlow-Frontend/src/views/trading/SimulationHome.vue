<template>
    <div >
      <el-card>
        <template #header>
          <div class="section-header"><div class="card-header">
            <span>模拟交易列表</span>
          </div>
          <el-button type="success" @click="addSimulation()">新建模拟交易</el-button>
        </div>
        </template>
  
        <el-table :data="simulationList" border stripe style="width: 100%">
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="frequency" label="频率" >
            <template #default="scope">
                {{ scope.row.frequency =="minute"? "每分钟":"每天" }}
            </template>
          </el-table-column>
          <el-table-column prop="createAt" label="开始日期" />
          
          <el-table-column prop="status" label="状态" >
            <template #default="scope">
                {{ scope.row.status == "running" ? "运行中" : "已结束"  }}
            </template>
          </el-table-column>
          
          <el-table-column fixed="right" label="操作" width="220">
            <template #default="scope">
              <el-button type="primary" @click="viewSimulation(scope.row)">查看详情</el-button>
              <el-button :disabled="scope.row.status != `running`" type="danger" @click="stopSimulation(scope.row)">停止运行</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
    <simulation-form ref="simulationForm"  @submitSuccess="fetchSimulation"/>
      
  </template>

<script lang="ts" setup>
import { useUserStore } from '@/stores/user';
import type { Result, Simulation } from '@/types/api';
import { apiClient } from '@/utils/apiClient';
import { onMounted, ref } from 'vue';
import SimulationForm from '@/components/simulation/SimulationForm.vue';

const simulationForm = ref() //ref<InstanceType<typeof SimulationForm> | null>(null)
const simulationList = ref<Simulation[]>([])

const userStore = useUserStore();

const fetchSimulation =async ()=>{
    const userId = userStore.getUserId;
    if(!userId){
        alert("用户未登录!")
        return;
    }
    const response =await apiClient(`${import.meta.env.VITE_API_BASE_URL}/trading/get_simulations/${userId}`,{
        method: "GET",
        headers:{
            credentials: 'include',
        }
    });
    const result:Result<Simulation[]> =await response.json();
    if(result.code == 200){
        if(result.data)
        simulationList.value = result.data;
        else simulationList.value = [];
    }else{
        alert("获取用户模拟交易列表失败:"+result.message);
    }

}
onMounted(()=>{
    fetchSimulation();
})

const addSimulation = async ()=>{
    simulationForm.value.open();
}

const viewSimulation = (Simulation:Simulation)=>{

}

const stopSimulation = async (Simulation:Simulation)=>{
    const userId = userStore.getUserId;
    if(!userId){
        alert("用户未登录!")
        return;
    }
    const response =await apiClient(`${import.meta.env.VITE_API_BASE_URL}/trading/stop_simulation/${Simulation.id}`,{
        method: "POST",
        headers:{
            credentials: 'include',
        }
    });
    const result:Result<null> =await response.json();
    if(result.code == 200){
        alert("停止模拟交易成功")
        fetchSimulation();
    }else{
        alert("停止模拟交易失败:"+result.message);
    }
}

</script>
<style scoped>
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style> 
