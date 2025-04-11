<template>
  <div class="lesson-management">
    <div class="header">
      <h2>课时管理</h2>
      <button class="btn-close" @click="closeModal">
        <i class="fas fa-times">关闭</i>
      </button>
      <button @click="addNewLesson">新增课程</button>
    </div>
    <div class="lesson-list" v-if="lessons.length != 0">
      <table>
        <thead>
          <tr>
            <th>序号</th>
            <th>标题</th>
            <th>描述</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="lesson in lessons" :key="lesson.id">
            <td>{{ lesson.orderNumber }}</td>
            <td>{{ lesson.title }}</td>
            <td>{{ lesson.description }}</td>
            <td>
              <button class="btn-edit" @click="editLesson(lesson)">编辑</button>
              <button class="btn-delete" @click="confirmDeleteLesson(lesson)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <LessonForm
      v-if="showLessonForm"
      :currentLesson="currentLesson"
      @close="closeLessonForm"
      :isEdit="isEdit"
      :parent_id="props.course?.id || null"
      :order_number="lessons.length+1"
    />

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Course, resource,CourseTree } from '@/types/api'
import { apiClient } from '@/utils/apiClient'
import LessonForm from "@/components/admin/LessonForm.vue";

const props = defineProps<{
  course : Course | null
}>()

const emit = defineEmits(['close'])

const lessons = ref<Course[]>([])
const currentLesson =  ref<Course | null>(null)
const isEdit = ref(false)
const resources = ref<resource[]>([])
const showLessonForm = ref(false)

const addNewLesson = () => {
  isEdit.value = false
  showLessonForm.value = true
}

const closeLessonForm = () => {
  showLessonForm.value = false
  fetchLessons();
}

const fetchLessons = async () => {
  try {
    if(props.course === null) return;
    const response = await apiClient(`${import.meta.env.VITE_API_BASE_URL}/admin/course/${props.course.id}/lessons`, {
      credentials: 'include',
      method: 'GET'
    })
    const result = await response.json()
    if (result.code === 200) {
      lessons.value = result.data || []
      lessons.value.sort((a,b)=>a.orderNumber - b.orderNumber)
    }
  } catch (error) {
    console.error('获取课时列表失败:', error)
  }
}

const fetchResources = async (tag: string) => {
  try {
    const response = await apiClient(`${import.meta.env.VITE_API_BASE_URL}/resources?tag=${tag}`, {
      credentials: 'include',
      method: 'GET'
    })
    const result = await response.json()
    if (result.code === 200) {
      resources.value = result.data || []
    }
  } catch (error) {
    console.error('获取视频资源失败:', error)
  }
}

const editLesson = (lesson: Course) => {
  currentLesson.value = { ...lesson }
  isEdit.value = true
}

const confirmDeleteLesson = (lesson: Course) => {
  // 删除逻辑
}

// const handleSubmit = async () => {
//   if (!currentLesson.value) return

//   const isEdit = !!currentLesson.value.id
//   const url = `${import.meta.env.VITE_API_BASE_URL}/admin/course/${props.course.id}/lessons${isEdit ? `/edit/${currentLesson.value.id}` : '/add'}`
//   const method = isEdit ? 'PUT' : 'POST'

//   try {
//     const response = await apiClient(url, {
//       method,
//       credentials: 'include',
//       headers: {
//         'Content-Type': 'application/json'
//       },
//       body: JSON.stringify(currentLesson.value)
//     })
//     const result = await response.json()
//     if (result.code === 200) {
//       await fetchLessons()
//       closeModal()
//     }
//   } catch (error) {
//     console.error('保存课时失败:', error)
//   }
// }





const closeModal = () => {
  emit('close')
  currentLesson.value = null
  isEdit.value = false
}

onMounted(() => {
  fetchLessons()
})
</script>

<style scoped>
.lesson-management {
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80%;
  max-width: 75%;
  z-index: 999;
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

table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

th, td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background-color: #f8f9fa;
  font-weight: 500;
}

.btn-edit,
.btn-delete {
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 0.5rem;
}

.btn-edit {
  background-color: #3498db;
  color: white;
  border: none;
}

.btn-delete {
  background-color: #e74c3c;
  color: white;
  border: none;
}



label {
  display: block;
  margin-bottom: 0.5rem;
}

input, textarea, select {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  background-color: #27ae60;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}


</style>