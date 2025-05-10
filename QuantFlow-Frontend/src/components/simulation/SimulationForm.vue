<template>
  <el-dialog
    title="新建模拟交易"
    v-model="dialogVisible"
    width="700px"
    :close-on-click-modal="false"
  >
    <el-form
      :model="formData"
      :rules="rules"
      ref="formRef"
      label-width="150px"
    >
      <el-form-item label="交易名称" prop="tradeName">
        <el-input v-model="formData.tradeName" placeholder="请输入交易名称"></el-input>
      </el-form-item>
      <el-form-item label="股票代码" prop="tradeName">
        <el-input v-model="formData.stockCode" placeholder="(格式:000001.SZ,600036.SH)"></el-input>
      </el-form-item>
      <el-form-item label="选择已回测策略" prop="strategy">
        <el-select v-model="formData.strategy" placeholder="请选择策略" style="width: 100%">
          <el-option
            v-for="item in strategyOptions"
            :key="`${item.type}-${item.id}`"
            :label="`${item.type == `stored` ?`(预存策略)`:`(自定义策略)`} ${item.name} `"
            :value="`${item.type}-${item.id}`"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item label="初始资金" prop="initialCapital">
        <el-input-number
          v-model="formData.initialCapital"
          :min="1"
          :precision="2"
          style="width: 100%"
        />
      </el-form-item>
      
      <el-form-item label="运行频率" prop="frequency">
        <el-select v-model="formData.frequency" placeholder="请选择运行频率" style="width: 100%">
          <el-option label="每分钟" value="minute" />
          <el-option label="每天" value="day" />
        </el-select>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { Result, Strategy } from '@/types/api'
import { useUserStore } from '@/stores/user'
import { apiClient } from '@/utils/apiClient'

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()

interface FormData {
    stockCode: string,
    tradeName: string,
    strategy: string,
    initialCapital: number,
    frequency: 'minute' | 'day'
}
interface StrategyInfo {
    name:string,
    type:string,
    id:number
}

const formData = reactive<FormData>({
    stockCode: '',
    tradeName: '',
    strategy: '',
    initialCapital: 10000,
    frequency: 'minute'
})

// const strategyOptions = [
//   { label: '策略A', value: 'strategy_a' },
//   { label: '策略B', value: 'strategy_b' }
// ]

const strategyOptions = ref<StrategyInfo[]>([])

const fetchStrategyOptions = async ()=>{
    const userId = useUserStore().getUserId;
    if(!userId){
        alert("用户未登录!无法获取可用策略")
        return;
    }
    const response =await apiClient(`${import.meta.env.VITE_API_BASE_URL}/backtest/getreadystrategies/${userId}`,{
        method: "GET",
        headers:{
            credentials: 'include',
        }
    });
    const result:Result<StrategyInfo[]> =await response.json();
    if(result.code == 200 && result.data){
        strategyOptions.value = result.data;
    }else{
        alert("获取可用策略失败:"+result.message);
    }
}

const validateStockCode = (rule:any,value:string,callback:(err?:Error)=>void) => {

  if (!/^\d{6}\.[A-Z]{2}$/.test(value)) {
    callback(new Error('股票代码格式错误，应为000001.SZ格式'));
  }
  callback();
}

const validateCash = (rule:any,value:number,callback:(err?:Error)=>void)=>{
    if(value<=0 || value > 2147483647){
        callback(new Error("资金需为正数小于21亿"))
    }
    callback();
}

const rules: FormRules = {
    stockCode:[
        { required: true, message: '请输入股票代码', trigger: 'blur' },
        {validator:validateStockCode,message:"股票代码格式错误", trigger:'blur'}
    ],
    tradeName: [{ required: true, message: '请输入交易名称', trigger: 'blur' }],
    strategy: [{ required: true, message: '请选择策略', trigger: 'change' }],
    initialCapital: [
        { required: true, message: '请输入初始资金', trigger: 'blur' },
        { validator:validateCash,message:"初始资金范围错误",trigger:'blur'}
    ],
    frequency: [{ required: true, message: '请选择运行频率', trigger: 'change' }]
}

const emit = defineEmits(["submitSuccess"])

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
        let info:string[] = formData.strategy.split('-')
        const response =await apiClient(`${import.meta.env.VITE_API_BASE_URL}/trading/simulation`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                credentials: 'include'
            },
            body: JSON.stringify({
                userId: useUserStore().getUserId,
                stockCode: formData.stockCode,
                strategyId: info[1],
                strategyType: info[0],
                name: formData.tradeName,
                initialFunding: formData.initialCapital,
                frequency: formData.frequency,
            })
        });
        const result:Result<null> =await response.json();
        if(result.code == 200 ){
            alert("模拟交易创建成功!")
            emit("submitSuccess")
        }else{
            alert("创建模拟交易失败:"+result.message);
        }
      
      
      dialogVisible.value = false
    }
  })
}

const handleCancel = () => {
  dialogVisible.value = false
}

// 对外暴露打开弹窗的方法
defineExpose({
  open: () => {
    dialogVisible.value = true
    fetchStrategyOptions();
  }
})
</script>

<style scoped>
.el-input-number {
  width: 100%;
}
</style>
