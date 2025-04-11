<template>
  <div class="book-container">
    <div class="book-section">
      <h2>量化基础理论</h2>
      <div class="book-grid">
        <div class="book-card" v-for="book in basicBooks" :key="book.id">
          <div class="book-cover">
            <img :src="book.cover" :alt="book.title">
            <div class="reading-progress" v-if="book.progress">
              <div class="progress-bar" :style="{ width: book.progress + '%' }"></div>
            </div>
          </div>
          <div class="book-info">
            <h3>{{ book.title }}</h3>
            <p class="author">{{ book.author }}</p>
            <p class="description">{{ book.description }}</p>
            <div class="book-meta">
              <span class="pages">{{ book.pages }}页</span>
              <span class="level">{{ book.level }}</span>
            </div>
            <button class="btn-primary">
              {{ book.progress ? '继续阅读' : '开始阅读' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="book-section">
      <h2>策略与实践</h2>
      <div class="book-grid">
        <div class="book-card" v-for="book in advancedBooks" :key="book.id">
          <div class="book-cover">
            <img :src="book.cover" :alt="book.title">
            <div class="reading-progress" v-if="book.progress">
              <div class="progress-bar" :style="{ width: book.progress + '%' }"></div>
            </div>
          </div>
          <div class="book-info">
            <h3>{{ book.title }}</h3>
            <p class="author">{{ book.author }}</p>
            <p class="description">{{ book.description }}</p>
            <div class="book-meta">
              <span class="pages">{{ book.pages }}页</span>
              <span class="level">{{ book.level }}</span>
            </div>
            <button class="btn-primary">
              {{ book.progress ? '继续阅读' : '开始阅读' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="books">
const basicBooks = [
  {
    id: 1,
    title: '量化交易基础理论',
    author: '张三',
    description: '系统介绍量化交易的基本概念和理论框架',
    cover: '/images/books/quant-basic.jpg',
    pages: 280,
    level: '入门',
    progress: 30
  },
  {
    id: 2,
    title: '金融数学基础',
    author: '李四',
    description: '量化交易中必需的数学知识和模型',
    cover: '/images/books/financial-math.jpg',
    pages: 320,
    level: '入门',
    progress: null
  }
];

const advancedBooks = [
  {
    id: 3,
    title: '量化策略设计与实现',
    author: '王五',
    description: '详解量化交易策略的设计方法和实践技巧',
    cover: '/images/books/strategy-design.jpg',
    pages: 400,
    level: '进阶',
    progress: 60
  },
  {
    id: 4,
    title: '机器学习在量化交易中的应用',
    author: '赵六',
    description: '机器学习算法在量化交易中的实战应用',
    cover: '/images/books/ml-trading.jpg',
    pages: 350,
    level: '高级',
    progress: null
  }
];
</script>

<style scoped>
.book-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.book-section {
  margin-bottom: 3rem;
}

.book-section h2 {
  margin-bottom: 1.5rem;
  color: #2c3e50;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
}

.book-card {
  background-color: #ffffff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.book-card:hover {
  transform: translateY(-5px);
}

.book-cover {
  position: relative;
  width: 100%;
  height: 220px;
  overflow: hidden;
}

.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.reading-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background-color: rgba(0, 0, 0, 0.1);
}

.progress-bar {
  height: 100%;
  background-color: #3498db;
}

.book-info {
  padding: 1.5rem;
}

.book-info h3 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.author {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.description {
  color: #666;
  margin-bottom: 1rem;
  line-height: 1.5;
  font-size: 0.9rem;
}

.book-meta {
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