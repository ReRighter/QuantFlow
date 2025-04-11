<template>
  <div class="education-home">
    <h1>在线教育</h1>
    <nav class="resource-nav">
      <router-link 
        to="/education/courses" 
        class="nav-item" 
        active-class="active"
        exact
      >在线课程</router-link>
      <!-- <router-link 
        to="/education/ebooks" 
        class="nav-item" 
        active-class="active"
        exact
      >电子书籍</router-link>
      <router-link 
        to="/education/cases" 
        class="nav-item" 
        active-class="active"
        exact
      >案例分析</router-link> -->
    </nav>
    <router-view></router-view>
  </div>
</template>

<script setup lang="ts">
// 教育模块逻辑
</script>

<style scoped>
.education-home {
  padding: 2rem;
}

.resource-nav {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-bottom: 2rem;
  border-bottom: 1px solid #eee;
  padding-bottom: 1rem;
}

.nav-item {
  color: #666;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.nav-item:hover,
.nav-item.active {
  color: #3498db;
  background-color: rgba(52, 152, 219, 0.1);
}

.course-container {
  max-width: 1200px;
  margin: 0 auto;
}

.course-section {
  margin-bottom: 3rem;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  margin-top: 1.5rem;
}

.course-card {
  background-color: #ffffff;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.course-card h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.course-card p {
  color: #666;
  margin-bottom: 1.5rem;
}

.btn-primary {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-primary:hover {
  background-color: #2980b9;
}
</style>