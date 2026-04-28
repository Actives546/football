<template>
  <div class="schedule-list-container">
    <el-card class="search-card" shadow="hover">
      <el-form :model="searchForm" label-width="80px" class="search-form">
        <div class="search-row">
          <div class="search-item">
            <el-form-item label="所属赛季" class="form-item">
              <el-select
                v-model="searchForm.seasonId"
                placeholder="请选择赛季"
                clearable
                filterable
                class="search-select"
                @change="handleSearch"
              >
                <el-option
                  v-for="item in seasonOptions"
                  :key="item.id"
                  :label="item.seasonName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </div>
          <div class="search-item">
            <el-form-item label="赛程名称" class="form-item">
              <el-input
                v-model="searchForm.scheduleName"
                placeholder="请输入赛程名称"
                clearable
                class="search-input"
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </div>
          <div class="search-item">
            <el-form-item label="赛程状态" class="form-item">
              <el-select
                v-model="searchForm.status"
                placeholder="请选择状态"
                clearable
                class="search-select"
                @change="handleSearch"
              >
                <el-option label="未开始" value="SCHEDULED" />
                <el-option label="进行中" value="LIVE" />
                <el-option label="已结束" value="FINISHED" />
              </el-select>
            </el-form-item>
          </div>
          <div class="search-item">
            <el-form-item label="比赛地点" class="form-item">
              <el-input
                v-model="searchForm.location"
                placeholder="请输入比赛地点"
                clearable
                class="search-input"
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </div>
          <div class="search-item">
            <el-button type="primary" @click="handleSearch" class="search-btn">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
          <div class="search-item">
            <el-button @click="handleReset" class="search-btn">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
        </div>
      </el-form>
    </el-card>
    
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="table-header">
          <div class="header-left">
            <span class="table-title">赛程信息列表</span>
            <el-tag type="info" effect="light" class="table-count" v-if="currentSeasonName">
              当前赛季：{{ currentSeasonName }}
            </el-tag>
            <el-tag type="info" effect="light" class="table-count">共 {{ total }} 条记录</el-tag>
          </div>
          <div class="header-right">
            <el-button type="danger" plain :disabled="multipleSelection.length === 0" @click="handleBatchDelete">
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增赛程
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        stripe
        :header-cell-style="{ backgroundColor: '#f8fafc', color: '#475569', fontWeight: 600, fontSize: '14px', padding: '8px 0', height: '40px' }"
        :cell-style="{ padding: '6px 0', height: '36px' }"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="序号" width="70" align="center">
          <template #default="{ $index }">
            <span class="index-text">{{ getIndex($index) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="matchName" label="所属赛事" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="match-name-cell">
              <el-tag v-if="row.matchName" size="small" type="primary">
                {{ row.matchName }}
              </el-tag>
              <span v-else class="empty-text">-</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="seasonName" label="所属赛季" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="season-name-cell">
              <el-tag v-if="row.seasonName" size="small" type="success">
                {{ row.seasonName }}
              </el-tag>
              <span v-else class="empty-text">-</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="scheduleName" label="赛程名称" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="schedule-name-cell">
              <el-avatar :size="32" class="schedule-avatar">
                <el-icon :size="16"><Timer /></el-icon>
              </el-avatar>
              <div class="schedule-info">
                <span class="schedule-name">{{ row.scheduleName }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="比赛地点" min-width="120" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="location-cell">
              <el-icon class="location-icon"><Location /></el-icon>
              <span class="location-text">{{ row.location || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="170" align="center">
          <template #default="{ row }">
            <span class="time-text">{{ formatDateTime(row.startTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small" effect="dark">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-tooltip content="查看" placement="top">
              <el-button type="primary" link size="small" @click="handleView(row)">
                <el-icon><View /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="编辑" placement="top">
              <el-button type="primary" link size="small" @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button type="danger" link size="small" @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && tableData.length === 0" description="暂无数据" style="margin: 60px 0;" />
      
      <el-pagination
        v-if="total > 0"
        class="pagination"
        :current-page="pagination.pageNum"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
    
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="680px"
      :close-on-click-modal="false"
      destroy-on-close
      class="schedule-dialog"
    >
      <div class="dialog-content" v-loading="formLoading">
        <div class="form-section">
          <div class="section-header">
            <div class="section-icon">
              <el-icon :size="18"><InfoFilled /></el-icon>
            </div>
            <span class="section-title">基本信息</span>
          </div>
          <div class="section-body">
            <el-form
              ref="formRef"
              :model="formData"
              :rules="formRules"
              label-width="100px"
              class="schedule-form"
            >
              <el-row :gutter="24">
                <el-col :span="12">
                  <el-form-item label="所属赛季" prop="seasonId">
                    <el-select
                      v-model="formData.seasonId"
                      placeholder="请选择所属赛季"
                      filterable
                      class="form-select"
                    >
                      <el-option
                        v-for="item in seasonOptions"
                        :key="item.id"
                        :label="item.seasonName"
                        :value="item.id"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="赛程名称" prop="scheduleName">
                    <el-input v-model="formData.scheduleName" placeholder="请输入赛程名称" class="form-input" />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="24">
                <el-col :span="12">
                  <el-form-item label="赛程状态" prop="status">
                    <el-select v-model="formData.status" placeholder="请选择赛程状态" class="form-select">
                      <el-option label="未开始" value="SCHEDULED" />
                      <el-option label="进行中" value="LIVE" />
                      <el-option label="已结束" value="FINISHED" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="比赛地点">
                    <el-input v-model="formData.location" placeholder="请输入比赛地点（可选）" class="form-input" />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-form-item label="开始时间" prop="startTime">
                <el-date-picker
                  v-model="formData.startTime"
                  type="datetime"
                  placeholder="请选择开始时间"
                  class="form-select"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  :disabled-date="disabledDate"
                />
              </el-form-item>
            </el-form>
          </div>
        </div>
        
        <div class="form-section" v-if="isView && currentSchedule">
          <div class="section-header">
            <div class="section-icon" style="background: linear-gradient(135deg, #10b981 0%, #059669 100%);">
              <el-icon :size="18"><Clock /></el-icon>
            </div>
            <span class="section-title">系统信息</span>
          </div>
          <div class="section-body">
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">创建时间</span>
                <span class="info-value">{{ formatDateTime(currentSchedule.createTime) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">更新时间</span>
                <span class="info-value">{{ formatDateTime(currentSchedule.updateTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="footer-btn">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit" v-if="!isView" class="footer-btn primary-btn">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
    
    <el-dialog
      v-model="detailVisible"
      title="赛程详情"
      width="600px"
      destroy-on-close
      class="detail-dialog"
    >
      <el-descriptions :column="2" border v-if="currentSchedule" class="detail-descriptions">
        <el-descriptions-item label="赛程ID" :span="1">
          <span class="detail-value">{{ currentSchedule.id }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="所属赛事" :span="1">
          <el-tag size="small" type="primary">
            {{ currentSchedule.matchName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="所属赛季" :span="1">
          <el-tag size="small" type="success">
            {{ currentSchedule.seasonName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="赛程状态" :span="1">
          <el-tag :type="getStatusType(currentSchedule.status)" size="small">
            {{ getStatusText(currentSchedule.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="赛程名称" :span="2">
          <span class="detail-value">{{ currentSchedule.scheduleName }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="比赛地点" :span="1">
          <span class="detail-text">{{ currentSchedule.location || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间" :span="1">
          <span class="detail-time">{{ formatDateTime(currentSchedule.startTime) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="1">
          <span class="detail-time">{{ formatDateTime(currentSchedule.createTime) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="1">
          <span class="detail-time">{{ formatDateTime(currentSchedule.updateTime) }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getSchedulePage,
  getScheduleById,
  addSchedule,
  updateSchedule,
  deleteSchedule,
  deleteScheduleBatch
} from '@/api/schedule'
import { getSeasonPage } from '@/api/season'
import {
  Search,
  Refresh,
  Plus,
  Delete,
  View,
  Edit,
  Timer,
  Location,
  InfoFilled,
  Clock
} from '@element-plus/icons-vue'

const route = useRoute()

const loading = ref(false)
const formLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const isView = ref(false)
const currentSchedule = ref(null)
const multipleSelection = ref([])
const tableData = ref([])
const total = ref(0)
const seasonOptions = ref([])
const tableRef = ref(null)
const formRef = ref(null)

const currentSeasonName = computed(() => {
  if (searchForm.seasonId) {
    const season = seasonOptions.value.find(s => s.id === searchForm.seasonId)
    return season ? season.seasonName : ''
  }
  return route.query.seasonName || ''
})

const searchForm = reactive({
  seasonId: null,
  scheduleName: '',
  status: '',
  location: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10
})

const formData = reactive({
  id: null,
  seasonId: null,
  scheduleName: '',
  status: 'SCHEDULED',
  location: '',
  startTime: null
})

const formRules = {
  seasonId: [
    { required: true, message: '请选择所属赛季', trigger: 'change' }
  ],
  scheduleName: [
    { required: true, message: '请输入赛程名称', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择赛程状态', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ]
}

const dialogTitle = computed(() => {
  if (isView.value) return '查看赛程'
  return formData.id ? '编辑赛程' : '新增赛程'
})

const disabledDate = (time) => {
  return false
}

const getStatusType = (status) => {
  const map = {
    'SCHEDULED': 'info',
    'LIVE': 'warning',
    'FINISHED': 'success'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'SCHEDULED': '未开始',
    'LIVE': '进行中',
    'FINISHED': '已结束'
  }
  return map[status] || status
}

const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const formatDateTime = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const getIndex = (index) => {
  return (pagination.pageNum - 1) * pagination.pageSize + index + 1
}

const loadSeasonOptions = async () => {
  try {
    const res = await getSeasonPage({ pageNum: 1, pageSize: 1000 })
    seasonOptions.value = res.data.list || []
  } catch (error) {
    console.error('加载赛季列表失败:', error)
  }
}

const loadTableData = async () => {
  loading.value = true
  try {
    const params = {
      seasonId: searchForm.seasonId,
      scheduleName: searchForm.scheduleName,
      status: searchForm.status,
      location: searchForm.location,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getSchedulePage(params)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载数据失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadTableData()
}

const handleReset = () => {
  searchForm.seasonId = null
  searchForm.scheduleName = ''
  searchForm.status = ''
  searchForm.location = ''
  handleSearch()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadTableData()
}

const handleCurrentChange = (page) => {
  pagination.pageNum = page
  loadTableData()
}

const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

const resetForm = () => {
  formData.id = null
  formData.seasonId = searchForm.seasonId || null
  formData.scheduleName = ''
  formData.status = 'SCHEDULED'
  formData.location = ''
  formData.startTime = null
}

const handleAdd = () => {
  resetForm()
  isView.value = false
  currentSchedule.value = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  isView.value = false
  Object.assign(formData, row)
  if (row.startTime) {
    formData.startTime = formatDateTime(row.startTime)
  }
  currentSchedule.value = { ...row }
  dialogVisible.value = true
}

const handleView = async (row) => {
  try {
    const res = await getScheduleById(row.id)
    currentSchedule.value = res.data
    isView.value = true
    Object.assign(formData, res.data)
    if (res.data.startTime) {
      formData.startTime = formatDateTime(res.data.startTime)
    }
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
    console.error(error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除赛程「${row.scheduleName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteSchedule(row.id)
      ElMessage.success('删除成功')
      loadTableData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请选择要删除的赛程')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 条赛程吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = multipleSelection.value.map(item => item.id)
      await deleteScheduleBatch(ids)
      ElMessage.success('批量删除成功')
      multipleSelection.value = []
      loadTableData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    if (formData.id) {
      await updateSchedule(formData)
      ElMessage.success('更新成功')
    } else {
      await addSchedule(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadTableData()
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

watch(() => route.query, (query) => {
  if (query.seasonId) {
    searchForm.seasonId = Number(query.seasonId)
    loadTableData()
  }
}, { immediate: true })

onMounted(() => {
  loadSeasonOptions()
  loadTableData()
})
</script>

<style scoped>
.schedule-list-container {
  width: 100%;
  padding: 0;
}

.search-card {
  border-radius: 12px;
  margin-bottom: 20px;
  border: 1px solid #e2e8f0;
}

.search-card :deep(.el-card__body) {
  padding: 20px 24px;
}

.search-form {
  margin: 0;
}

.search-row {
  display: flex;
  align-items: flex-end;
  gap: 24px;
  flex-wrap: wrap;
}

.search-item {
  display: flex;
  align-items: flex-end;
  margin-bottom: 0;
  flex: 0 0 auto;
}

.form-item {
  margin-bottom: 0;
}

.form-item :deep(.el-form-item__label) {
  font-weight: 500;
  color: #475569;
}

.search-input,
.search-select {
  width: 180px;
}

.search-btn {
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
  margin-bottom: 2px;
}

.search-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.table-card {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.table-card :deep(.el-card__header) {
  padding: 16px 24px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(to right, #f8fafc, #ffffff);
}

.table-card :deep(.el-card__body) {
  padding: 0;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.table-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  position: relative;
}

.table-title::before {
  content: '';
  position: absolute;
  left: -12px;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 18px;
  background: linear-gradient(180deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 2px;
}

.table-count {
  margin-left: 8px;
  border-color: #e2e8f0;
  color: #64748b;
}

.header-right {
  display: flex;
  gap: 12px;
}

.index-text {
  font-weight: 500;
  color: #64748b;
}

.empty-text {
  color: #909399;
  font-size: 14px;
}

.match-name-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.season-name-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.schedule-name-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.schedule-avatar {
  flex-shrink: 0;
  background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
  border-radius: 8px;
}

.schedule-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  overflow: hidden;
  min-width: 0;
}

.schedule-name {
  font-weight: 500;
  color: #1e293b;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.location-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}

.location-icon {
  color: #f97316;
  font-size: 14px;
}

.location-text {
  color: #64748b;
  font-size: 13px;
}

.time-text {
  color: #64748b;
  font-size: 13px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  padding: 20px 24px;
  border-top: 1px solid #f1f5f9;
  background: #fafafa;
}

.schedule-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.schedule-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(to right, #f8fafc, #ffffff);
}

.schedule-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.schedule-dialog :deep(.el-dialog__body) {
  padding: 24px;
  background: #fafafa;
}

.dialog-content {
  min-height: 200px;
}

.form-section {
  background: #ffffff;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.form-section:last-child {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: linear-gradient(to right, #f8fafc, #ffffff);
  border-bottom: 1px solid #f1f5f9;
}

.section-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.section-body {
  padding: 20px;
}

.schedule-form {
  margin: 0;
}

.schedule-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #475569;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
}

.form-textarea :deep(.el-textarea__inner) {
  resize: none;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.info-label {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.info-value {
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
}

.schedule-dialog :deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #f1f5f9;
  background: #ffffff;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.footer-btn {
  padding: 10px 24px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.primary-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.detail-dialog :deep(.el-dialog) {
  border-radius: 16px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.detail-descriptions :deep(.el-descriptions__label) {
  font-weight: 500;
  color: #64748b;
  background: #f8fafc;
}

.detail-descriptions :deep(.el-descriptions__content) {
  color: #1e293b;
}

.detail-value {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.detail-text {
  font-size: 14px;
  color: #475569;
}

.detail-time {
  font-size: 13px;
  color: #64748b;
}

.detail-desc {
  font-size: 14px;
  color: #475569;
  line-height: 1.6;
}
</style>
