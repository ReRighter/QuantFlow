<template>
  <div class="home">
    

    <h1>量学通 QuantFlow</h1>
    <div class="module-container">
      <router-link to="/education" class="module-card">
        <h2>在线教育</h2>
        <p>学习量化交易知识，掌握金融市场技能</p>
      </router-link>
      <router-link to="/strategy" class="module-card">
        <h2>量化策略</h2>
        <p>策略开发以及回测</p>
      </router-link>
      <router-link to="/trading" class="module-card">
        <h2>模拟交易</h2>
        <p>体验真实的股票交易模拟环境</p>
      </router-link>
    </div>

  </div>
</template>

<script setup lang="ts">

</script>

<style scoped>
.home {
  padding: 2rem;
  text-align: center;
}

.header {
  position: absolute;
  top: 1rem;
  right: 2rem;
  display: flex;
  gap: 1rem;
}




.module-container {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-top: 3rem;
}

.module-card {
  width: 300px;
  padding: 2rem;
  border-radius: 8px;
  background-color: #f5f5f5;
  text-decoration: none;
  color: inherit;
  transition: transform 0.3s ease;
}

.module-card:hover {
  transform: translateY(-5px);
}

.module-card h2 {
  margin-bottom: 1rem;
  color: #2c3e50;
}

.module-card p {
  color: #666;
}
</style>