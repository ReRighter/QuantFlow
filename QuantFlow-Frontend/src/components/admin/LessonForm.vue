<script setup lang="ts">

import type {Course, Result} from "@/types/api.ts";
import {onMounted, ref} from "vue";
import {apiClient} from "@/utils/apiClient.ts";
import {getHashHex} from "@/utils/hashUtil.ts";

const {currentLesson,isEdit,parent_id,order_number} = defineProps<{
  currentLesson: Course |null,
  isEdit: boolean,
  parent_id:number | null,
  order_number :number
}>()
const emit =  defineEmits(['close'])
onMounted(()=>{

})
const title = ref("");
const description =ref("");


async function  handleSubmit(){
  const fileInput  = document.getElementById("video") as HTMLInputElement;
  const file = fileInput.files?.item(0);
  const button = document.querySelector("button[type='submit']") as HTMLButtonElement;
  const loading = document.getElementById("loading") as HTMLDivElement;
  button.style.display = "none";
  loading.style.display = "block";

  let resource_id: number | null = null;
  if(file){
    let formData = new FormData();
    const hashString = (await getHashHex(file)).substring(0,12);
    formData.set("video",file)
    formData.set("hash",hashString)
    const res = await apiClient(`${import.meta.env.VITE_API_BASE_URL}/admin/course/uploadVideo`,{
      credentials:"include",
      method: "POST",
      body: formData
    })
    let result:Result<number> = await res.json(); // 上传成功后返回id
    if(result.code === 200 && result.data){
      resource_id = result.data 
    }else{
      alert("视频上传失败!" + result.message);
      return;
    }
  }

  if(!isEdit){
    const response = await apiClient(`${import.meta.env.VITE_API_BASE_URL}/admin/course/addLesson`,{
      credentials:"include",
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        title:title.value,
        parentId:parent_id,
        description:description.value,
        resourceId:resource_id,
        orderNumber:order_number
      })
    })
    const result:Result<string> = await response.json()
    if(result.code==200 && result.data  ){
      alert(result.data);
      button.style.display = "block";
      loading.style.display = "none";
      emit("close");

    }else{
      alert("课时添加失败!" + result.message);
    }
  }
}

</script>

<template>
  <div class="lesson-form">
    <div class="header">
    <h3>{{ isEdit ? '编辑课时' : '新增课时' }}</h3>
      <button @click="$emit('close')">关闭</button></div>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="title">标题</label>
        <input type="text" id="title" name="title" v-model="title" required />
      </div>
      <div class="form-group">
        <label for="description">描述</label>
        <textarea id="description" name="description" v-model="description" required></textarea>
      </div>
      <div class="form-group">
        <label for="video">视频资源</label>
        <input  type="file" accept="video/*" id="video">

<!--        <select id="resource" v-model="currentLesson?.resource_id" required>
          <option value="">请选择视频资源</option>
          <option v-for="resource in resources" :key="resource.id" :value="resource.id">
            {{ resource.name }}
          </option>
        </select>-->
      </div>
      <button type="submit">{{ isEdit ? '保存' : '新增' }}</button>
      <div id="loading" class="loading-spinner">  
        <div class="spinner"></div>
      </div>
    </form>
  </div>
</template>

<style scoped>
/* 隐藏加载动画的默认状态 */
#loading {
  display: none;
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1001;
}

/* 环形旋转样式 */
.spinner {
  border: 4px solid #f3f3f3; /* 外边框颜色 */
  border-top: 4px solid #3498db; /* 顶部边框颜色（形成旋转效果） */
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite; /* 动画效果 */
}

/* 旋转动画 */
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
.lesson-form {
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 90%;
  max-width: 600px;
  z-index: 1000;
  box-sizing: border-box; /* 确保内边距和边框包含在宽度内 */
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.form-group {
  margin-bottom: 1.5rem; /* 增加表单项之间的间距 */
}

label {
  display: block;
  margin-bottom: 0.5rem; /* 增加标签和输入框之间的间距 */
}

input[type="text"],
textarea,
input[type="file"] {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box; /* 确保内边距和边框包含在宽度内 */
}

button {
  padding: 0.75rem 1.5rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #0056b3;
}

</style>