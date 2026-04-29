<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo-wrapper">
        <div class="logo-icon">
          <el-icon :size="isCollapse ? 24 : 32"><Football /></el-icon>
        </div>
        <transition name="fade">
          <span v-show="!isCollapse" class="logo-text">足球赛事系统</span>
        </transition>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#1d1e1f"
        text-color="#bfcbd9"
        active-text-color="#16a34a"
        router
        class="layout-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        
        <el-menu-item index="/organization/list">
          <el-icon><OfficeBuilding /></el-icon>
          <template #title>机构管理</template>
        </el-menu-item>
        
        <el-sub-menu index="match">
          <template #title>
            <el-icon><Trophy /></el-icon>
            <span>赛事管理</span>
          </template>
          <el-menu-item index="/match/list">
            <el-icon><List /></el-icon>
            <template #title>赛事信息</template>
          </el-menu-item>
          <el-menu-item index="/season/list">
            <el-icon><Calendar /></el-icon>
            <template #title>赛季管理</template>
          </el-menu-item>
          <el-menu-item index="/schedule/list">
            <el-icon><Timer /></el-icon>
            <template #title>赛程信息管理</template>
          </el-menu-item>
          <el-menu-item index="/stadium/list">
            <el-icon><Location /></el-icon>
            <template #title>场地管理</template>
          </el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="personnel">
          <template #title>
            <el-icon><User /></el-icon>
            <span>人员信息管理</span>
          </template>
          <el-menu-item index="/personnel/list">
            <el-icon><UserFilled /></el-icon>
            <template #title>人员管理</template>
          </el-menu-item>
          <el-menu-item index="/team/list">
            <el-icon><Trophy /></el-icon>
            <template #title>球队管理</template>
          </el-menu-item>
          <el-menu-item index="/player/list">
            <el-icon><User /></el-icon>
            <template #title>球员管理</template>
          </el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/news/list">
          <el-icon><Document /></el-icon>
          <template #title>新闻管理</template>
        </el-menu-item>
        
        <el-menu-item index="/talk/list">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>说说管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container class="layout-main-container">
      <el-header class="layout-header">
        <div class="header-left">
          <div class="collapse-btn" @click="toggleCollapse">
            <el-icon :size="20">
              <Fold v-if="!isCollapse" />
              <Expand v-else />
            </el-icon>
          </div>
          <el-breadcrumb separator="/" class="header-breadcrumb">
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" class="user-avatar">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <span class="user-name">{{ userInfo.nickname || userInfo.username || '用户' }}</span>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/store'
import {
  HomeFilled,
  Trophy,
  List,
  Calendar,
  Timer,
  Fold,
  Expand,
  UserFilled,
  User,
  ArrowDown,
  SwitchButton,
  Football,
  OfficeBuilding,
  Location,
  Document,
  ChatDotRound
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const userInfo = computed(() => userStore.userInfo)

const activeMenu = computed(() => route.path)

const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  return matched.map(item => ({
    path: item.path,
    title: item.meta.title
  }))
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
      ElMessage.success('已退出登录')
    }).catch(() => {})
  } else if (command === 'profile') {
    ElMessage.info('个人中心功能开发中')
  }
}

watch(() => route.path, () => {
  if (!userStore.isLoggedIn && route.path !== '/login' && route.path !== '/register') {
    router.push('/login')
  }
}, { immediate: true })
</script>

<style scoped>
.layout-container {
  width: 100%;
  height: 100%;
}

.layout-aside {
  background-color: #1d1e1f;
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
}

.logo-wrapper {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  border-bottom: 1px solid #2d2e2f;
  overflow: hidden;
}

.logo-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #16a34a 0%, #22c55e 100%);
  border-radius: 8px;
  color: #fff;
  flex-shrink: 0;
}

.logo-text {
  margin-left: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
}

.layout-menu {
  border-right: none;
  flex: 1;
}

.layout-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
}

.layout-menu :deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
}

.layout-menu :deep(.el-menu-item:hover),
.layout-menu :deep(.el-sub-menu__title:hover) {
  background-color: #2d2e2f;
}

.layout-menu :deep(.el-menu-item.is-active) {
  background-color: rgba(22, 163, 74, 0.15);
}

.layout-main-container {
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.layout-header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  height: 60px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
  color: #606266;
}

.collapse-btn:hover {
  background-color: #f5f7fa;
  color: #16a34a;
}

.header-breadcrumb {
  margin-left: 16px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 6px;
  transition: all 0.2s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.user-avatar {
  background: linear-gradient(135deg, #16a34a 0%, #22c55e 100%);
}

.user-name {
  margin: 0 8px;
  font-size: 14px;
  color: #303133;
}

.dropdown-icon {
  color: #909399;
  font-size: 12px;
}

.layout-main {
  padding: 20px;
  flex: 1;
  overflow: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
