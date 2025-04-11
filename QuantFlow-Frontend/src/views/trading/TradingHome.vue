<template>
  <div class="trading-home">
    <h1>模拟交易</h1>
    <div class="trading-container">
      <div class="market-overview section">
        <h2>市场行情</h2>
        <div class="market-grid">
          <div class="market-card">
            <h3>上证指数</h3>
            <div class="price">3,203.91</div>
            <div class="change up">+21.32 (+0.67%)</div>
          </div>
          <div class="market-card">
            <h3>深证成指</h3>
            <div class="price">10,238.52</div>
            <div class="change down">-52.68 (-0.51%)</div>
          </div>
        </div>
      </div>

      <div class="portfolio section">
        <h2>持仓信息</h2>
        <div class="portfolio-summary">
          <div class="summary-item">
            <span>总资产</span>
            <strong>¥100,000.00</strong>
          </div>
          <div class="summary-item">
            <span>可用资金</span>
            <strong>¥50,000.00</strong>
          </div>
          <div class="summary-item">
            <span>持仓市值</span>
            <strong>¥50,000.00</strong>
          </div>
        </div>
      </div>

      <div class="trading-panel section">
        <h2>交易操作</h2>
        <div class="trading-form">
          <div class="form-group">
            <label>股票代码</label>
            <input type="text" placeholder="请输入股票代码" class="form-input" />
          </div>
          <div class="form-group">
            <label>交易价格</label>
            <input type="number" placeholder="请输入价格" class="form-input" />
          </div>
          <div class="form-group">
            <label>交易数量</label>
            <input type="number" placeholder="请输入数量" class="form-input" />
          </div>
          <div class="button-group">
            <button class="btn-buy">买入</button>
            <button class="btn-sell">卖出</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 交易模块逻辑
</script>

<style scoped>
.trading-home {
  padding: 2rem;
}

.trading-container {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.section {
  background-color: #ffffff;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.market-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.market-card {
  padding: 1rem;
  border: 1px solid #eee;
  border-radius: 4px;
}

.price {
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0.5rem 0;
}

.change {
  font-size: 0.9rem;
}

.change.up {
  color: #e74c3c;
}

.change.down {
  color: #2ecc71;
}

.portfolio-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.summary-item {
  text-align: center;
}

.summary-item span {
  display: block;
  color: #666;
  margin-bottom: 0.5rem;
}

.summary-item strong {
  font-size: 1.2rem;
  color: #2c3e50;
}

.trading-form {
  margin-top: 1rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #666;
}

.form-input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.button-group {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-top: 1.5rem;
}

.btn-buy,
.btn-sell {
  padding: 0.75rem;
  border: none;
  border-radius: 4px;
  color: white;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-buy {
  background-color: #e74c3c;
}

.btn-buy:hover {
  background-color: #c0392b;
}

.btn-sell {
  background-color: #2ecc71;
}

.btn-sell:hover {
  background-color: #27ae60;
}
</style>