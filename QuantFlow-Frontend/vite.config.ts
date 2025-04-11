import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import monacoEditorEsmPlugin   from 'vite-plugin-monaco-editor-esm'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    monacoEditorEsmPlugin({
      languageWorkers: ['editorWorkerService'], // 按需加载 Worker 文件
      customWorkers: [
        {
          label: 'python', // 按需加载 Python 语言支持
          entry: 'monaco-editor/esm/vs/basic-languages/python/python.js'
        }
      ],
    })
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
