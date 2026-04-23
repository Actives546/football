<template>
  <div class="header-component">
    <div class="logo">{{ title }}</div>
    <div class="nav">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: '足球赛事系统'
  }
})
</script>

<style scoped>
.header-component {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  background-color: #409eff;
  color: white;
}

.logo {
  font-size: 20px;
  font-weight: bold;
}

.nav {
  display: flex;
  align-items: center;
  gap: 20px;
}
</style>
