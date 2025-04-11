<template>
  <div class="course-detail">
    <div class="main-content">
      <div class="video-container">
        <video 
          ref="videoPlayer"
          class="video-player"
          :src="currentLesson.videoUrl"
          controls
        ></video>
        <div class="video-info">
          <h2>{{ currentLesson.lesson.title || "空"}}</h2>
          <p class="lesson-description">{{ currentLesson.lesson.description }}</p>
        </div>
      </div>
    </div>
    
    <div class="lesson-list">
      <h3>课程目录</h3>
      <div class="lesson-items">
        <div 
          v-for="lesson in lessons" 
          :key="lesson.id"
          class="lesson-item"
          :class="{ active: currentLesson.lesson.id === lesson.id }"
          @click="switchLesson(lesson)"
        >
          <div class="lesson-info">
            <!-- <span class="lesson-index">{{ lesson.index }}</span> -->
            <div class="lesson-detail">
              <h4>{{ lesson.title }}</h4>
              <!-- <span class="duration">{{ lesson.duration }}</span> -->
            </div>
          </div>
          <div class="lesson-status">
            <!-- <span v-if="lesson.completed" class="completed">已完成</span> -->
            <span v-if="currentLesson.lesson.id === lesson.id" class="learning">学习中</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="CourseDetail" >
import type { Course, LessonDetails, Result } from '@/types/api'
import { apiClient } from '@/utils/apiClient'
import { isArrayLiteralExpression } from 'typescript'
import { ref, onMounted } from 'vue'
import { useRouter,useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute();
const videoPlayer = ref<HTMLVideoElement | null>(null)

// 模拟课程数据
// const courseLessons = ref([
//   {
//     id: 1,
//     index: '第1课',
//     title: '金融市场概述',
//     description: '本课程介绍金融市场的基本概念和主要参与者',
    
//     videoUrl: 'https://example.com/video1.mp4',
//     completed: true
//   },
//   {
//     id: 2,
//     index: '第2课',
//     title: '技术分析基础',
//     description: '学习基本的技术分析方法和图表解读',
    
//     videoUrl: 'https://example.com/video2.mp4',
//     completed: false
//   },
//   {
//     id: 3,
//     index: '第3课',
//     title: '量化交易策略',
//     description: '探讨常见的量化交易策略和实现方法',
//     videoUrl: 'https://example.com/video3.mp4',
//     completed: false
//   }
// ])
const lessons = ref<Course[]>([]);
const defaultLesson = {
  title:"loading",
  description:"loading",
  id:0,
} as Course;
const currentLesson = ref<LessonDetails>({ lesson:defaultLesson,videoUrl:"" } as LessonDetails);

const switchLesson = (lesson: Course) => {
  currentLesson.value.lesson = lesson
  fetchVideoUrl(lesson).then((url)=>{
    currentLesson.value.videoUrl = url;
  })

  if (videoPlayer.value) {
    videoPlayer.value.currentTime = 0
    //videoPlayer.value.play()
  }
}

const fetchVideoUrl = async (lesson: Course) => {
    const response = await apiClient(`${import.meta.env.VITE_API_BASE_URL}/course/video/${lesson.id}`,{
      method:'GET',
      credentials: 'include'
    });
    const result: Result<string> = await response.json();
    if(result.code === 200 && result.data){
      return result.data;
    }else{
      alert("获取视频资源失败!")
      return "";
    }
}

onMounted(async () => {
  // 这里可以根据路由参数加载具体课程数据
  const courseId = route.params.id
  // 根据courseId获取课程详情
  const response = await apiClient(`${import.meta.env.VITE_API_BASE_URL}/course/${courseId}/lessons`,{
    credentials: 'include',
    method: 'GET'
  });
  const result: Result<Course[]> = await response.json();
  if(result.code==200 && result.data){
    result.data.sort((a,b)=>a.orderNumber-b.orderNumber);
    lessons.value = result.data;
    currentLesson.value.lesson = result.data[0];
    currentLesson.value.videoUrl = await fetchVideoUrl(result.data[0]);
  }else{
    alert("获取视频资源失败!")
    router.push('education/courses');
  }

})
</script>

<style scoped>
.course-detail {
  display: flex;
  gap: 2rem;
  padding: 2rem;
  height: calc(100vh - 200px);
}

.main-content {
  flex:1;
  min-width: 0;
}

.video-container {
  float: center;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  max-width: 90%;
}

.video-player {
  width: 100%;
  aspect-ratio: 16/9;
  background: #000;
}

.video-info {
  padding: 1.5rem;
}

.video-info h2 {
  margin-bottom: 1rem;
  color: #2c3e50;
}

.lesson-description {
  color: #666;
  line-height: 1.6;
}

.lesson-list {
  width: 300px;
  background: #fff;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  height: calc(100vh - 4rem); /* 设置固定高度，减去页面padding */
  display: flex;
  flex-direction: column;
  max-height: 70%;
}

.lesson-items {
  flex: 1;
  overflow-y: auto;
  padding-right: 8px; /* 为滚动条预留空间 */
}

/* 美化滚动条 */
.lesson-items::-webkit-scrollbar {
  width: 6px;
}

.lesson-items::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.lesson-items::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.lesson-items::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.lesson-list h3 {
  margin-bottom: 1.5rem;
  color: #2c3e50;
}

.lesson-items {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.lesson-item {
  padding: 1rem;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #eee;
}

.lesson-item:hover {
  background-color: rgba(52, 152, 219, 0.1);
}

.lesson-item.active {
  background-color: rgba(52, 152, 219, 0.1);
  border-color: #3498db;
}

.lesson-info {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.lesson-index {
  background: #3498db;
  color: white;
  padding: 0.5rem;
  border-radius: 4px;
  font-size: 0.9rem;
}

.lesson-detail {
  flex: 1;
}

.lesson-detail h4 {
  margin-bottom: 0.25rem;
  color: #2c3e50;
}

.duration {
  font-size: 0.9rem;
  color: #666;
}

.lesson-status {
  margin-top: 0.5rem;
  font-size: 0.9rem;
}

.completed {
  color: #27ae60;
}

.learning {
  color: #3498db;
}
</style>