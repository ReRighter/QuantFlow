<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <div class="modal-header">
        <input type="text" v-model="strategy.name" placeholder="请输入标题">
        <span class="close-btn" @click="handleClose">&times;</span>
      </div>
      <div class="code-container">
        <MonacoEditor
          :codeContent="strategy.code"
          :options="editorOptions"
          language="python"
          theme="vs-dark"
          @update:codeContent="(value)=>strategy.code = value"
        />
      </div>
      <div class="modal-footer">
        <button class="btn cancel" @click="handleClose">取消</button>
        <button class="btn save" @click="handleSaveAs">另存为</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, toRaw, toRef } from 'vue'
import MonacoEditor from '@/components/MonacoEditor.vue'
import type { Strategy } from '@/types/api'
import { inputEmits } from 'element-plus';

const props = defineProps<{
  //visible: boolean
  strategy: Strategy
}>()

// const codeContent =ref('')
// watch(()=>props.strategy, (newValue) => {
//   if(newValue.code==null){
//     codeContent.value = ''
//   }else{
//     codeContent.value = newValue.code
//   }
// })


const emit = defineEmits(['close', 'save-as'])


const handleClose = () => {
  emit('close')
}

const handleSaveAs = () => {
  emit('save-as', toRaw(props.strategy))
  emit('close')
}

const editorOptions = ref({
  fontSize: 14,
  lineNumbers: 'on',
  roundedSelection: true,
  scrollBeyondLastLine: false,
  readOnly: false,
  tabSize: 4,  // Python通常使用4空格缩进
})
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
  height: 80vh;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
}

.close-btn {
  font-size: 24px;
  cursor: pointer;
  color: #666;
}
input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.code-container {
  flex: 1;
  padding: 15px;
  background: #f5f5f5;
  overflow: auto;
}

.modal-footer {
  padding: 15px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  border-top: 1px solid #eee;
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

.save {
  background: #409eff;
  color: white;
}

.strategy-content {
  height: 100%;
  min-height: 500px;
  overflow-y: auto;
}

.code-editor {
  width: 100%;
  height: 500px;
  font-family: 'Courier New', Courier, monospace;
  font-size: 14px;
}


</style>
