<template>
  <div class="home-container">
    <el-container>
      <el-header class="header">
        <span class="title">足球赛事系统</span>
      </el-header>
      <el-main class="main">
        <div class="welcome">
          <h2>欢迎使用足球赛事系统</h2>
          <p>这是一个前后端一体化的项目骨架</p>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
</script>

<style scoped>
.home-container {
  width: 100%;
  height: 100%;
}

.header {
  background-color: #409eff;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.title {
  color: white;
  font-size: 20px;
  font-weight: bold;
}

.main {
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.welcome {
  text-align: center;
}

.welcome h2 {
  color: #303133;
  margin-bottom: 20px;
}

.welcome p {
  color: #909399;
}
</style>
