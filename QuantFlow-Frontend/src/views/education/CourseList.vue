<template>
  <div class="course-container">
    <div class="course-section">
      <h2>量化交易基础</h2>
      <div class="course-grid">
        <div class="course-card" v-for="node in basicCourses" :key="node.parent_course.id">
          <div class="course-image">
            <img :src="node.cover" :alt="node.parent_course.title">
          </div>
          <div class="course-info">
            <h3>{{ node.parent_course.title }}</h3>
            <p>{{ node.parent_course.description }}</p>
            <div class="course-meta">
              <!-- <span class="duration">{{ course.duration }}</span> -->
             
            </div>
            <button class="btn-primary" @click="startLearning(node.parent_course.id)">
                开始学习
            </button>
          </div>
        </div>
      </div>
    </div>
    <div class="course-section">
      <h2>进阶课程</h2>
      <div class="course-grid">
        <div class="course-card" v-for="node in advancedCourses" :key="node.parent_course.id">
          <div class="course-image">
            <img :src="node.cover" :alt="node.parent_course.title">
          </div>
          <div class="course-info">
            <h3>{{ node.parent_course.title }}</h3>
            <p>{{ node.parent_course.description }}</p>
            <div class="course-meta">
            
            </div>
            <button class="btn-primary" @click="startLearning(node.parent_course.id)">
              <!-- {{ course.progress ? '继续学习' : '开始学习' }} -->
                开始学习
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name = "course">
import { useRouter } from 'vue-router'
import { onMounted, ref } from 'vue'
import type { Course, CourseTree, Result } from '@/types/api';
import { apiClient } from '@/utils/apiClient';

const router = useRouter()
/*
const basicCourses = [
  {
    id: 1,
    title: '金融市场基础',
    description: '了解股票市场的基本概念和运作机制',
    cover: '/images/courses/financial-market.jpg',
    duration: '2小时',
    level: '入门'
  },
  {
    id: 2,
    title: '量化交易入门',
    description: '掌握量化交易的基本原理和策略',
    cover: '/images/courses/quant-intro.jpg',
    duration: '3小时',
    level: '入门'
  }
];

const advancedCourses = [
  {
    id: 3,
    title: '技术分析',
    description: '学习技术指标和图表分析方法',
    cover: '/images/courses/technical-analysis.jpg',
    duration: '4小时',
    level: '进阶'
  },
  {
    id: 4,
    title: '策略开发',
    description: '开发和回测量化交易策略',
    cover: '/images/courses/strategy-dev.jpg',
    duration: '5小时',
    level: '进阶'
  }
];
*/

const basicCourses = ref<CourseTree[]>([]);
const advancedCourses = ref<CourseTree[]>([]);

async function fetchCourses() {
    const response = await apiClient(`${import.meta.env.VITE_API_BASE_URL}/course/list`,{
        credentials: 'include',
        method: 'GET'
    });
    const result:Result<CourseTree[]> = await response.json();
    if(result.code === 200 && result.data){
        result.data.forEach(courseTree => {
            let level = courseTree.parent_course.description.substring(1,3);
            if(level == '入门'){
                basicCourses.value.push(courseTree);
            }else if(level == '进阶'){
                advancedCourses.value.push(courseTree);
            }
        });
    }else{
        alert("获取课程信息失败!"+result.message);
    }
}
onMounted(()=>{
    fetchCourses();
    // let testCourse:Course={
    //     id:10,
    //     title:"test",
    //     description:"test",
    //     parentId:-1,
    //     resourceId:1,
    //     orderNumber:0
    // }
    // let testCourseTree:CourseTree={
    //     parent_course:testCourse,
    //     lessons:[],
    //     cover: "http://110.41.47.134:9000/resources/image/cover/cover%E9%87%91%E8%9E%8D%E5%AD%A6%E5%9F%BA%E7%A1%80.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250323%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250323T070926Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=ada9eb0fc54dbbf2640171c1bb9e189f54bfe79e40a86d00b67201069c0fb337"
    // }

    // basicCourses.value.push(testCourseTree);
});

const startLearning = (courseId: number) => {
  router.push(`/education/courses/${courseId}`)
}
</script>

<style scoped>
.course-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.course-section {
  margin-bottom: 3rem;
}

.course-section h2 {
  margin-bottom: 1.5rem;
  color: #2c3e50;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.course-card {
  background-color: #ffffff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.course-card:hover {
  transform: translateY(-5px);
}

.course-image {
  width: 100%;
  position: relative;
  padding-top: 56.25%; /* 16:9 宽高比 */
  overflow: hidden;
}

.course-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: contain; /* 改为 contain 以保持图片比例 */
  background-color: #f5f5f5; /* 添加背景色以便于区分图片边界 */
}

.course-info {
  padding: 1.5rem;
}

.course-info h3 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.course-info p {
  color: #666;
  margin-bottom: 1rem;
  line-height: 1.5;
}

.course-meta {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
  font-size: 0.9rem;
  color: #666;
}

.btn-primary {
  width: 100%;
  background-color: #3498db;
  color: white;
  border: none;
  padding: 0.75rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-primary:hover {
  background-color: #2980b9;
}
</style>