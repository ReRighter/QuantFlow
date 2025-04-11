<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <h2>{{ isEdit ? '编辑课程' : '添加课程' }}</h2>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="title">课程名称</label>
          <input 
            type="text" 
            id="title"
            v-model="formData.title"
            required
          >
        </div>

        <div class="form-group">
          <label for="description">课程描述</label>
          <textarea 
            id="description"
            v-model="formData.description"
            required
          ></textarea>
        </div>

        <div class="form-group">
          <label for="cover">封面图片</label>
          <input 
            type="file" 
            id="cover"
            accept="image/*"
            @change="handleFileChange"
          >
          <img id="preview" :src="previewSrc" v-show="showPreview" style="width: 200px;">
        </div>

        <div class="form-group">
          <label for="level">难度级别</label>
          <select 
            id="level"
            v-model="level"
            required
          >
            <option value="入门">入门</option>
            <option value="进阶">进阶</option>
            <option value="高级">高级</option>
          </select>
        </div>
        <div class="form-group">
          <button type="button" class="btn-cancel" @click="testApi">testButton</button>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn-submit">保存</button>
          <button type="button" class="btn-cancel" @click="$emit('close')">取消</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts" name="CourseForm">
import { ref, onMounted } from 'vue'
import type { Course,Result } from '@/types/api'
import { apiClient } from '@/utils/apiClient'

const testApi = async () => {
  const response = await fetch('http://localhost:8080/admin/course/test', {
    credentials: 'include',
    method: 'POST'
  })
  console.log(response);
}

const props = defineProps<{
  course?: Course | null
  isEdit: boolean
}>()

const emit = defineEmits(['close', 'submit'])
const level = ref('入门')


const formData = ref({
  title: '',
  description: '',
  resourceId: -1,
})

onMounted(() => {
  if (props.course) {
    formData.value = { ...props.course }
    formData.value.description = formData.value.description.substring(4);
  }
})

const handleSubmit = () => {
  emit('submit', formData.value,level.value)
}

// 预览图片
let showPreview = ref(false);
let previewSrc = ref('');
const handleFileChange = async (e: Event) => {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) {
    showPreview.value = false;
    previewSrc.value = '';
    return;
  }
    try{
      let fileFormData = new FormData();
      fileFormData.append('cover', file);
      const response = await apiClient(`${import.meta.env.VITE_API_BASE_URL}/admin/course/uploadCover`, {
        method: 'POST',
        credentials: 'include',
        body: fileFormData
      })
      const result:Result<{url:string,id:number}> = await response.json();
      if (result.code === 200) {
        previewSrc.value = result.data?.url || '';
        formData.value.resourceId = result.data?.id || -1;    
        showPreview.value = true;
      }else{
        throw new Error(result.message);
      }
    } catch (error) {
      alert("上传封面失败");
      (e.target as HTMLInputElement).value = '';
      console.error('上传封面失败:', error)
    }
  
}
</script>

<style scoped>
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
  max-width: 500px;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #666;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group textarea {
  height: 100px;
  resize: vertical;
}

.form-actions {
  display: flex;
  gap: 1rem;
}

.btn-submit,
.btn-cancel {
  flex: 1;
  padding: 0.75rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.btn-submit {
  background-color: #3498db;
  color: white;
  border: none;
}

.btn-cancel {
  background-color: transparent;
  color: #666;
  border: 1px solid #ddd;
}
</style>