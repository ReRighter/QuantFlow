<template>
  <div class="course-management">
    <div class="header">
      <h2>课程管理</h2><div>
      <button class="btn-add" @click="showAddModal = true">
        <i class="fas fa-plus"> 添加课程</i>
      </button>
      <!-- <button class="btn-upload" @click="showUploadVideo = true"> 批量上传视频</button> --></div>
    </div>

    <div class="course-list">
      <table>
        <thead>
          <tr>
            <th>序号</th>
            <th>封面</th>
            <th>课程名称</th>
            <th>难度</th>
            <th>课时数</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="node in courses" :key="node.parent_course.id">
            <td>{{ ++cnt }}</td>
            <td>
              <img :src="node.cover" :alt="node.parent_course.title" class="course-cover" style="width: 200px">
            </td>
            <td>{{ node.parent_course.title }}</td>
            <td>{{ node.parent_course.description.substring(1,3) }}</td>
            <td>{{ node.lessons?.length || 0 }}</td>
            <td>
              <button class="btn-edit" @click="editCourse(node.parent_course)">编辑</button>
              <button class="btn-delete" @click="confirmDelete(node.parent_course)">删除</button>
              <button class="btn-lessons" @click="manageLessons(node)">课时管理</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <CourseForm
      v-if="showAddModal || showEditModal"
      :course="currentCourse"
      :isEdit="showEditModal"
      @close="closeModal"
      @submit="handleSubmit"
    />
    <!--添加/编辑课程弹窗 -->
    <!-- 
    

    
    <ConfirmModal
      v-if="showDeleteModal"
      :title="'确认删除'"
      :message="`确定要删除课程"${currentCourse?.title}"吗？`"
      @confirm="deleteCourse"
      @cancel="showDeleteModal = false"
    /> -->
    <LessonManagement
      v-if="showLessonManagementModal"
      :course="currentCourse"
      @close="showLessonManagementModal = false"
    />
    <upload-video
      v-if="showUploadVideo"
      @close="showUploadVideo = false"
    />



  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import type { Course } from '@/types/api'
import CourseForm from '@/components/admin/CourseForm.vue'
//import ConfirmModal from '@/components/common/ConfirmModal.vue'
import { useUserStore } from '@/stores/user'
import { apiClient } from '@/utils/apiClient'
import type { Result,CourseTree } from '@/types/api'
import LessonManagement from '@/components/admin/LessonManagement.vue'
import UploadVideo from "@/components/admin/UploadVideo.vue";

const router = useRouter()
const userStore = useUserStore()

// 检查权限
onMounted(() => {
  if (!userStore.userInfo?.role || userStore.userInfo.role !== 'admin') {
    router.push('/')
  }
})

const courses = ref<CourseTree[]>([])
const currentCourse = ref<Course | null>(null)
const showAddModal = ref(false)
const showEditModal = ref(false)
const showDeleteModal = ref(false)
const showLessonManagementModal = ref(false)
const showUploadVideo = ref(false);
let  cnt =0;
// 获取课程列表
const fetchCourses = async () => {
  try {
    const response = await apiClient(`${import.meta.env.VITE_API_BASE_URL}/admin/course/list`, {
      credentials: 'include',
      method: 'GET'
    })
    const result:Result<CourseTree[]> = await response.json()
    if (result.code === 200) {
      courses.value = result.data || []

    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
  }
}

// 编辑课程
const editCourse = (course: Course) => {
  currentCourse.value = { ...course }
  showAddModal.value = false;
  showEditModal.value = true;
}

// 确认删除
const confirmDelete = (course: Course) => {
  currentCourse.value = course
  showDeleteModal.value = true
}

// 删除课程
const deleteCourse = async () => {
  if (!currentCourse.value) return
  
  try {
    const response = await fetch(
      `${import.meta.env.VITE_API_BASE_URL}/admin/course/${currentCourse.value.id}`,
      {
        method: 'DELETE'
      }
    )
    const result = await response.json()
    if (result.code === 200) {
      await fetchCourses()
    }
  } catch (error) {
    console.error('删除课程失败:', error)
  }
  showDeleteModal.value = false
}

// 管理课时
const manageLessons = (node: CourseTree) => {
  currentCourse.value = node.parent_course
  showLessonManagementModal.value = true
}
// 处理表单提交
const handleSubmit = async (formData: Partial<Course> ,level:string) => {
  const isEdit = !!formData.id
  const url = `${import.meta.env.VITE_API_BASE_URL}/admin/course${isEdit ? `/edit/${formData.id}` : '/add'}`
  const method = isEdit ? 'PUT' : 'POST'

  try {
    formData.description = `(${level})${formData.description}`
    const response = await apiClient(url, {
      method,
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(formData)
    })
    const result = await response.json()
    if (result.code === 200) {
      await fetchCourses()
      closeModal()
    }
  } catch (error) {
    console.error('保存课程失败:', error)
  }
}

const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
  currentCourse.value = null
}

onMounted(() => {
  fetchCourses();
  cnt=0;
})
</script>

<style scoped>
.course-management {
  padding: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.btn-add,.btn-upload {
  background-color: #27ae60;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 0 0 10px ;
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

.course-cover {
  width: 80px;
  height: 45px;
  object-fit: cover;
  border-radius: 4px;
}

.btn-edit,
.btn-delete,
.btn-lessons {
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

.btn-lessons {
  background-color: #f39c12;
  color: white;
  border: none;
}
</style> 