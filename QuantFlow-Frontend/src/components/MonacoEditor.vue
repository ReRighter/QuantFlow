<script setup lang="ts" name = "MonacoEditor">
import { ref, onMounted, watch, onBeforeUnmount, toRaw } from 'vue'
import * as monaco from 'monaco-editor'
const props = defineProps({
  codeContent: {
    type: String,
    default: '',
  },
  language: {
    type: String,
    default: 'python',
  },
  theme: {
    type: String,
    default: 'vs',
  },
  options: {
    type: Object,
    default: () => ({}),
  },
})

const emit = defineEmits(['update:codeContent'])
const editorRef = ref<HTMLElement | null>(null)
let editor :monaco.editor.IStandaloneCodeEditor|null = null;

// 初始化编辑器
onMounted(() => {
  if (!editorRef.value) return

  // 设置Python语言配置
  monaco.languages.register({ id: 'python' })
  
  editor = monaco.editor.create(editorRef.value, {
    value: props.codeContent,
    language: props.language,
    theme: props.theme,
    automaticLayout: true,
    minimap: { enabled: false },
    ...props.options,
  })

  //失去焦点时将代码内容发送到父组件. 点击父组件的saveas按钮时会先触发"失去焦点",再执行"saveas"方法
  editor.onDidBlurEditorWidget(()=>{
    const value = editor?.getValue() || ''
    emit('update:codeContent', value)
  })

  //emit('editorMounted', editor.value)
})


//父组件中的codeContent发生变化时,更新编辑器的值
watch(() => props.codeContent, (newValue:string) => {
  if (editor && newValue !== editor.getValue()) {
    if(newValue == null){
      newValue = ''
    }
    editor.setValue(newValue)
  }
})

// 清理资源
onBeforeUnmount(() => {
  editor?.dispose()
})

</script>

<template>
  <div ref="editorRef" class="monaco-editor"></div>
</template>

<style scoped>
.monaco-editor {
  width: 100%;
  height: 100%;
  min-height: 300px;
}
</style>