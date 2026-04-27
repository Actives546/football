<template>
  <div class="dashboard-container">
    <div class="welcome-card">
      <div class="welcome-content">
        <h1 class="welcome-title">欢迎使用足球赛事管理系统</h1>
        <p class="welcome-subtitle">专业的足球赛事管理平台，助力您高效管理各项赛事</p>
      </div>
      <div class="welcome-illustration">
        <el-icon :size="80" color="#16a34a"><Football /></el-icon>
      </div>
    </div>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <div class="stat-card stat-green">
          <div class="stat-icon">
            <el-icon :size="32"><Trophy /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalMatches }}</div>
            <div class="stat-label">赛事总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-blue">
          <div class="stat-icon">
            <el-icon :size="32"><List /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalSeasons }}</div>
            <div class="stat-label">赛季总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-orange">
          <div class="stat-icon">
            <el-icon :size="32"><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.recentCreated }}</div>
            <div class="stat-label">本周新增</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-purple">
          <div class="stat-icon">
            <el-icon :size="32"><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">98%</div>
            <div class="stat-label">系统可用率</div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="recent-matches-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">最近赛事</span>
              <el-button type="primary" text @click="goToMatchList">
                查看全部 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          <el-table :data="recentMatches" style="width: 100%" v-loading="loading">
            <el-table-column prop="matchName" label="赛事名称" min-width="180" show-overflow-tooltip />
            <el-table-column prop="matchType" label="赛事类型" width="100">
              <template #default="{ row }">
                <el-tag size="small" effect="light" :type="getMatchTypeTagType(row.matchType)">
                  {{ row.matchType }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loading && recentMatches.length === 0" description="暂无赛事数据" />
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="quick-actions-card" shadow="never">
          <template #header>
            <span class="card-title">快捷操作</span>
          </template>
          <el-row :gutter="16">
            <el-col :span="12">
              <div class="action-card" @click="goToAddMatch">
                <div class="action-icon action-green">
                  <el-icon :size="28"><Plus /></el-icon>
                </div>
                <div class="action-text">
                  <div class="action-title">新增赛事</div>
                  <div class="action-desc">创建新的足球赛事</div>
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="action-card" @click="goToMatchList">
                <div class="action-icon action-blue">
                  <el-icon :size="28"><List /></el-icon>
                </div>
                <div class="action-text">
                  <div class="action-title">赛事管理</div>
                  <div class="action-desc">管理所有赛事信息</div>
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="action-card">
                <div class="action-icon action-orange">
                  <el-icon :size="28"><TrendCharts /></el-icon>
                </div>
                <div class="action-text">
                  <div class="action-title">数据统计</div>
                  <div class="action-desc">查看赛事统计数据</div>
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="action-card">
                <div class="action-icon action-purple">
                  <el-icon :size="28"><Setting /></el-icon>
                </div>
                <div class="action-text">
                  <div class="action-title">系统设置</div>
                  <div class="action-desc">配置系统参数</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMatchPage } from '@/api/match'
import {
  Football,
  Trophy,
  Clock,
  CircleCheck,
  ArrowRight,
  Plus,
  List,
  TrendCharts,
  Setting
} from '@element-plus/icons-vue'

const router = useRouter()

const loading = ref(false)
const recentMatches = ref([])
const stats = ref({
  totalMatches: 0,
  totalSeasons: 0,
  recentCreated: 0
})

const getMatchTypeTagType = (type) => {
  const map = {
    '足球联赛': 'primary',
    '杯赛': 'success',
    '友谊赛': 'info',
    '亚冠联赛': 'warning',
    '欧冠联赛': 'danger'
  }
  return map[type] || 'info'
}

const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

const loadStats = async () => {
  try {
    const totalRes = await getMatchPage({ pageNum: 1, pageSize: 1 })
    stats.value.totalMatches = totalRes.data.total
    stats.value.recentCreated = Math.min(totalRes.data.total, 3)
    stats.value.totalSeasons = 0
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadRecentMatches = async () => {
  loading.value = true
  try {
    const res = await getMatchPage({ pageNum: 1, pageSize: 5 })
    recentMatches.value = res.data.list || []
  } catch (error) {
    ElMessage.error('加载最近赛事失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const goToMatchList = () => {
  router.push('/match/list')
}

const goToAddMatch = () => {
  router.push('/match/list')
}

onMounted(() => {
  loadStats()
  loadRecentMatches()
})
</script>

<style scoped>
.dashboard-container {
  width: 100%;
}

.welcome-card {
  background: linear-gradient(135deg, #16a34a 0%, #22c55e 100%);
  border-radius: 16px;
  padding: 32px 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.welcome-content {
  color: #fff;
}

.welcome-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.welcome-subtitle {
  font-size: 14px;
  margin: 0;
  opacity: 0.9;
}

.welcome-illustration {
  opacity: 0.2;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-green .stat-icon {
  background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
  color: #16a34a;
}

.stat-blue .stat-icon {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #2563eb;
}

.stat-orange .stat-icon {
  background: linear-gradient(135deg, #ffedd5 0%, #fed7aa 100%);
  color: #ea580c;
}

.stat-purple .stat-icon {
  background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
  color: #9333ea;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
  margin-top: 4px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.recent-matches-card,
.quick-actions-card {
  border-radius: 12px;
}

.recent-matches-card :deep(.el-card__header),
.quick-actions-card :deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #f3f4f6;
}

.recent-matches-card :deep(.el-card__body),
.quick-actions-card :deep(.el-card__body) {
  padding: 20px;
}

.action-card {
  display: flex;
  align-items: center;
  padding: 16px;
  border-radius: 12px;
  background: #fafafa;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 16px;
}

.action-card:hover {
  background: #f3f4f6;
  transform: translateY(-2px);
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.action-green {
  background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
  color: #16a34a;
}

.action-blue {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #2563eb;
}

.action-orange {
  background: linear-gradient(135deg, #ffedd5 0%, #fed7aa 100%);
  color: #ea580c;
}

.action-purple {
  background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
  color: #9333ea;
}

.action-text {
  flex: 1;
}

.action-title {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 2px;
}

.action-desc {
  font-size: 12px;
  color: #6b7280;
}
</style>