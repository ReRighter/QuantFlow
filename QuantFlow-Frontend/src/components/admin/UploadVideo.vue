<script setup lang="ts">
import { ref } from "vue";
const emit = defineEmits(['close'])
const tag = ref('')
const selectedFiles = ref<File[]>([])

const handleFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement
  if(target.files){
    selectedFiles.value = Array.from(target.files)
  }
}

const uploadVideos = async () => {
  // 批量上传视频逻辑
}
</script>

<template>
  <div class="upload-section">
    <div class="header">
      <h3>批量上传视频</h3>
      <button class="btn-close" @click="emit('close')">关闭</button>
    </div>
    <input type="text" v-model="tag"  placeholder="请为视频指定标签" required />
    <div class="file-upload">
      <label class="file-label">
        <input type="file" multiple accept="video/*" @change="handleFileUpload" hidden />
        <span class="file-cta">
          <span class="file-icon">
            <i class="fas fa-upload"></i>
          </span>
          <span class="file-label">
            选择文件...
          </span>
        </span>
      </label>
    </div>
    <ul class="file-list" v-if="selectedFiles?.length != 0">
      <li v-for="file in selectedFiles" :key="file.name">{{ file.name }}</li>
    </ul>
    <button @click="uploadVideos">上传</button>
  </div>
</template>

<style scoped>
.upload-section {
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80%;
  max-width: 600px;
  z-index: 1000;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}
.btn-close {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.file-upload {
  margin-bottom: 1rem;
  margin-top: 1rem;
}
.file-label {
  cursor: pointer;
  background-color: #007bff;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  display: inline-block;
}
.file-icon {
  margin-right: 0.5rem;
}
.file-list {
  list-style-type: none;
  padding: 0;
  margin: 1rem 0;
}
.file-list li {
  background-color: #f8f9fa;
  padding: 0.5rem;
  border-radius: 4px;
  margin-bottom: 0.5rem;
}
</style>