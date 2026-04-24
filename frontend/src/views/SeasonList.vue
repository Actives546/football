<template>
  <div class="season-list-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" label-width="100px">
        <el-row :gutter="24">
          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="所属赛事">
              <el-select
                v-model="searchForm.matchId"
                placeholder="请选择赛事"
                clearable
                filterable
                style="width: 100%"
                @change="handleSearch"
              >
                <el-option
                  v-for="item in matchOptions"
                  :key="item.id"
                  :label="item.matchName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="赛季名称">
              <el-input
                v-model="searchForm.seasonName"
                placeholder="请输入赛季名称"
                clearable
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="赛季状态">
              <el-select
                v-model="searchForm.status"
                placeholder="请选择状态"
                clearable
                style="width: 100%"
                @change="handleSearch"
              >
                <el-option label="未开始" value="SCHEDULED" />
                <el-option label="进行中" value="LIVE" />
                <el-option label="已结束" value="FINISHED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" class="search-btn-group">
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
    
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="table-header">
          <div class="header-left">
            <span class="table-title">赛季信息列表</span>
            <el-tag type="info" effect="light" class="table-count" v-if="currentMatchName">
              当前赛事：{{ currentMatchName }}
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
              新增赛季
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
        :header-cell-style="{ backgroundColor: '#fafafa', color: '#606266', fontWeight: 600 }"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="matchName" label="所属赛事" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="match-name-cell">
              <el-tag size="small" type="primary" effect="light">
                {{ row.matchName }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="seasonName" label="赛季名称" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="season-name-cell">
              <el-avatar :size="32" class="season-avatar">
                <el-icon :size="16"><Calendar /></el-icon>
              </el-avatar>
              <div class="season-info">
                <span class="season-name">{{ row.seasonName }}</span>
                <span class="season-year" v-if="row.seasonYear">{{ row.seasonYear }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" align="center">
          <template #default="{ row }">
            {{ formatDate(row.startDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="endDate" label="结束日期" width="120" align="center">
          <template #default="{ row }">
            {{ formatDate(row.endDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small" effect="dark">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentRound" label="轮次" width="120" align="center">
          <template #default="{ row }">
            <div class="round-info">
              <span class="current-round">{{ row.currentRound || 0 }}</span>
              <span class="round-divider">/</span>
              <span class="total-rounds">{{ row.totalRounds || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-tooltip content="查看" placement="top">
              <el-button type="primary" link @click="handleView(row)">
                <el-icon><View /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="编辑" placement="top">
              <el-button type="primary" link @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button type="danger" link @click="handleDelete(row)">
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
      width="650px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        v-loading="formLoading"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="所属赛事" prop="matchId">
              <el-select
                v-model="formData.matchId"
                placeholder="请选择所属赛事"
                filterable
                style="width: 100%"
              >
                <el-option
                  v-for="item in matchOptions"
                  :key="item.id"
                  :label="item.matchName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="赛季名称" prop="seasonName">
              <el-input v-model="formData.seasonName" placeholder="请输入赛季名称" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="赛季年份">
              <el-input v-model="formData.seasonYear" placeholder="请输入赛季年份（如：2024）" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="赛季状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择赛季状态" style="width: 100%">
                <el-option label="未开始" value="SCHEDULED" />
                <el-option label="进行中" value="LIVE" />
                <el-option label="已结束" value="FINISHED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="formData.startDate"
                type="date"
                placeholder="请选择开始日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期">
              <el-date-picker
                v-model="formData.endDate"
                type="date"
                placeholder="请选择结束日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="总轮次">
              <el-input-number
                v-model="formData.totalRounds"
                :min="0"
                :max="999"
                placeholder="请输入总轮次"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前轮次">
              <el-input-number
                v-model="formData.currentRound"
                :min="0"
                :max="999"
                placeholder="请输入当前轮次"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="赛季描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入赛季描述信息（可选）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit" v-if="!isView">
          确定
        </el-button>
      </template>
    </el-dialog>
    
    <el-dialog
      v-model="detailVisible"
      title="赛季详情"
      width="600px"
      destroy-on-close
    >
      <el-descriptions :column="2" border v-if="currentSeason">
        <el-descriptions-item label="赛季ID" :span="1">{{ currentSeason.id }}</el-descriptions-item>
        <el-descriptions-item label="所属赛事" :span="1">
          <el-tag size="small" type="primary">
            {{ currentSeason.matchName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="赛季名称" :span="1">{{ currentSeason.seasonName }}</el-descriptions-item>
        <el-descriptions-item label="赛季年份" :span="1">{{ currentSeason.seasonYear || '-' }}</el-descriptions-item>
        <el-descriptions-item label="赛季状态" :span="1">
          <el-tag :type="getStatusType(currentSeason.status)" size="small">
            {{ getStatusText(currentSeason.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="轮次进度" :span="1">
          <span class="detail-round">{{ currentSeason.currentRound || 0 }} / {{ currentSeason.totalRounds || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="开始日期" :span="1">{{ formatDate(currentSeason.startDate) }}</el-descriptions-item>
        <el-descriptions-item label="结束日期" :span="1">{{ formatDate(currentSeason.endDate) }}</el-descriptions-item>
        <el-descriptions-item label="赛季描述" :span="2" v-if="currentSeason.description">
          {{ currentSeason.description }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="1">{{ formatDateTime(currentSeason.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="1">{{ formatDateTime(currentSeason.updateTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getSeasonPage,
  getSeasonById,
  addSeason,
  updateSeason,
  deleteSeason,
  deleteSeasonBatch
} from '@/api/season'
import { getMatchPage } from '@/api/match'
import {
  Search,
  Refresh,
  Plus,
  Delete,
  View,
  Edit,
  Calendar
} from '@element-plus/icons-vue'

const route = useRoute()

const loading = ref(false)
const formLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const isView = ref(false)
const currentSeason = ref(null)
const multipleSelection = ref([])
const tableData = ref([])
const total = ref(0)
const matchOptions = ref([])
const tableRef = ref(null)
const formRef = ref(null)

const currentMatchName = computed(() => {
  if (searchForm.matchId) {
    const match = matchOptions.value.find(m => m.id === searchForm.matchId)
    return match ? match.matchName : ''
  }
  return route.query.matchName || ''
})

const searchForm = reactive({
  matchId: null,
  seasonName: '',
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10
})

const formData = reactive({
  id: null,
  matchId: null,
  seasonName: '',
  seasonYear: '',
  status: 'SCHEDULED',
  startDate: null,
  endDate: null,
  description: '',
  totalRounds: null,
  currentRound: 0
})

const formRules = {
  matchId: [
    { required: true, message: '请选择所属赛事', trigger: 'change' }
  ],
  seasonName: [
    { required: true, message: '请输入赛季名称', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择赛季状态', trigger: 'change' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ]
}

const dialogTitle = computed(() => {
  if (isView.value) return '查看赛季'
  return formData.id ? '编辑赛季' : '新增赛季'
})

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

const loadMatchOptions = async () => {
  try {
    const res = await getMatchPage({ pageNum: 1, pageSize: 1000 })
    matchOptions.value = res.data.list || []
  } catch (error) {
    console.error('加载赛事列表失败:', error)
  }
}

const loadTableData = async () => {
  loading.value = true
  try {
    const params = {
      matchId: searchForm.matchId,
      seasonName: searchForm.seasonName,
      status: searchForm.status,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getSeasonPage(params)
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
  searchForm.matchId = null
  searchForm.seasonName = ''
  searchForm.status = ''
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
  formData.matchId = searchForm.matchId || null
  formData.seasonName = ''
  formData.seasonYear = ''
  formData.status = 'SCHEDULED'
  formData.startDate = null
  formData.endDate = null
  formData.description = ''
  formData.totalRounds = null
  formData.currentRound = 0
}

const handleAdd = () => {
  resetForm()
  isView.value = false
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  isView.value = false
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleView = async (row) => {
  try {
    const res = await getSeasonById(row.id)
    currentSeason.value = res.data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
    console.error(error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除赛季「${row.seasonName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteSeason(row.id)
      ElMessage.success('删除成功')
      loadTableData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请选择要删除的赛季')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 条赛季吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = multipleSelection.value.map(item => item.id)
      await deleteSeasonBatch(ids)
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
      await updateSeason(formData)
      ElMessage.success('更新成功')
    } else {
      await addSeason(formData)
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
  if (query.matchId) {
    searchForm.matchId = Number(query.matchId)
    loadTableData()
  }
}, { immediate: true })

onMounted(() => {
  loadMatchOptions()
  loadTableData()
})
</script>

<style scoped>
.season-list-container {
  width: 100%;
}

.search-card {
  border-radius: 12px;
  margin-bottom: 20px;
}

.search-card :deep(.el-card__body) {
  padding: 20px 24px;
}

.search-btn-group {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.search-btn-group .el-button {
  margin-left: 12px;
}

.table-card {
  border-radius: 12px;
}

.table-card :deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #f3f4f6;
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
  color: #1f2937;
}

.table-count {
  margin-left: 8px;
}

.header-right {
  display: flex;
  gap: 12px;
}

.season-name-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.season-avatar {
  flex-shrink: 0;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.season-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.season-name {
  font-weight: 500;
  color: #303133;
}

.season-year {
  font-size: 12px;
  color: #909399;
}

.round-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.current-round {
  font-size: 14px;
  font-weight: 600;
  color: #409eff;
}

.round-divider {
  color: #909399;
}

.total-rounds {
  font-size: 14px;
  color: #606266;
}

.detail-round {
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  padding: 20px;
  border-top: 1px solid #f3f4f6;
}
</style>
