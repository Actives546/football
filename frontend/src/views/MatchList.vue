<template>
  <div class="match-list-container">
    <el-card class="search-card" shadow="hover">
      <el-form :model="searchForm" label-width="80px" class="search-form">
        <div class="search-row">
          <div class="search-item">
            <el-form-item label="赛事名称" class="form-item">
              <el-input
                v-model="searchForm.matchName"
                placeholder="请输入赛事名称"
                clearable
                @keyup.enter="handleSearch"
                class="search-input"
              />
            </el-form-item>
          </div>
          <div class="search-item">
            <el-form-item label="赛事类型" class="form-item">
              <el-select
                v-model="searchForm.matchType"
                placeholder="请选择赛事类型"
                clearable
                class="search-select"
                @change="handleSearch"
              >
                <el-option label="足球联赛" value="足球联赛" />
                <el-option label="杯赛" value="杯赛" />
                <el-option label="友谊赛" value="友谊赛" />
                <el-option label="亚冠联赛" value="亚冠联赛" />
                <el-option label="欧冠联赛" value="欧冠联赛" />
                <el-option label="其他" value="其他" />
              </el-select>
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
            <span class="table-title">赛事信息列表</span>
            <el-tag type="info" effect="light" class="table-count">共 {{ total }} 条记录</el-tag>
          </div>
          <div class="header-right">
            <el-button type="danger" plain :disabled="multipleSelection.length === 0" @click="handleBatchDelete">
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增赛事
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
        :header-cell-style="{ backgroundColor: '#f8fafc', color: '#475569', fontWeight: 600, fontSize: '14px', padding: '12px 0' }"
        :cell-style="{ padding: '10px 0' }"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="序号" width="70" align="center">
          <template #default="{ $index }">
            <span class="index-text">{{ getIndex($index) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="matchName" label="赛事名称" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="match-name-cell">
              <el-avatar :size="32" class="match-avatar" v-if="row.coverImage">
                <img :src="row.coverImage" />
              </el-avatar>
              <el-avatar :size="32" class="match-avatar match-avatar-default" v-else>
                <el-icon :size="16"><Trophy /></el-icon>
              </el-avatar>
              <span class="match-name-text">{{ row.matchName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="matchType" label="赛事类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" effect="light" :type="getMatchTypeTagType(row.matchType)">
              {{ row.matchType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="赛事描述" min-width="120" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="desc-text">{{ row.description || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center">
          <template #default="{ row }">
            <span class="time-text">{{ formatDate(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-tooltip content="查看赛季" placement="top">
              <el-button type="success" link size="small" @click="handleViewSeasons(row)">
                <el-icon><Calendar /></el-icon>
              </el-button>
            </el-tooltip>
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
        :page-sizes="[10, 20, 50]"
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
      class="match-dialog"
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
              class="match-form"
            >
              <el-form-item label="赛事名称" prop="matchName" class="form-item-full">
                <el-input v-model="formData.matchName" placeholder="请输入赛事名称" class="form-input" />
              </el-form-item>
              
              <el-form-item label="赛事类型" prop="matchType">
                <el-select v-model="formData.matchType" placeholder="请选择赛事类型" class="form-select">
                  <el-option label="足球联赛" value="足球联赛" />
                  <el-option label="杯赛" value="杯赛" />
                  <el-option label="友谊赛" value="友谊赛" />
                  <el-option label="亚冠联赛" value="亚冠联赛" />
                  <el-option label="欧冠联赛" value="欧冠联赛" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>
              
              <el-form-item label="封面图片">
                <el-input v-model="formData.coverImage" placeholder="请输入封面图片URL（可选）" class="form-input">
                  <template #append>
                    <el-button @click="previewCoverImage">预览</el-button>
                  </template>
                </el-input>
              </el-form-item>
              
              <el-form-item label="赛事描述">
                <el-input
                  v-model="formData.description"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入赛事描述信息（可选）"
                  class="form-textarea"
                />
              </el-form-item>
            </el-form>
          </div>
        </div>
        
        <div class="form-section" v-if="isView && currentMatch">
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
                <span class="info-value">{{ formatDate(currentMatch.createTime) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">更新时间</span>
                <span class="info-value">{{ formatDate(currentMatch.updateTime) }}</span>
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
      title="赛事详情"
      width="600px"
      destroy-on-close
      class="detail-dialog"
    >
      <el-descriptions :column="2" border v-if="currentMatch" class="detail-descriptions">
        <el-descriptions-item label="赛事名称" :span="2">
          <span class="detail-value">{{ currentMatch.matchName }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="赛事类型" :span="1">
          <el-tag size="small" :type="getMatchTypeTagType(currentMatch.matchType)">
            {{ currentMatch.matchType }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="1">
          <span class="detail-time">{{ formatDate(currentMatch.createTime) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="封面图片" :span="2" v-if="currentMatch.coverImage">
          <div class="cover-preview">
            <el-image :src="currentMatch.coverImage" style="width: 100%; height: 200px;" fit="cover" />
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="赛事描述" :span="2" v-if="currentMatch.description">
          <span class="detail-desc">{{ currentMatch.description }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">
          <span class="detail-time">{{ formatDate(currentMatch.updateTime) }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
    
    <el-dialog
      v-model="previewVisible"
      title="图片预览"
      width="600px"
      destroy-on-close
    >
      <el-image
        v-if="formData.coverImage"
        :src="formData.coverImage"
        fit="contain"
        style="width: 100%; height: 400px;"
        :preview-src-list="[formData.coverImage]"
        :initial-index="0"
      />
      <el-empty v-else description="请先输入图片URL" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getMatchPage,
  getMatchById,
  addMatch,
  updateMatch,
  deleteMatch,
  deleteMatchBatch
} from '@/api/match'
import {
  Search,
  Refresh,
  Plus,
  Delete,
  View,
  Edit,
  Trophy,
  Calendar,
  InfoFilled,
  Clock
} from '@element-plus/icons-vue'

const router = useRouter()

const loading = ref(false)
const formLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const previewVisible = ref(false)
const isView = ref(false)
const currentMatch = ref(null)
const multipleSelection = ref([])
const tableData = ref([])
const total = ref(0)
const tableRef = ref(null)
const formRef = ref(null)

const searchForm = reactive({
  matchName: '',
  matchType: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10
})

const formData = reactive({
  id: null,
  matchName: '',
  matchType: '',
  description: '',
  coverImage: ''
})

const formRules = {
  matchName: [
    { required: true, message: '请输入赛事名称', trigger: 'blur' }
  ],
  matchType: [
    { required: true, message: '请选择赛事类型', trigger: 'change' }
  ]
}

const dialogTitle = computed(() => {
  if (isView.value) return '查看赛事'
  return formData.id ? '编辑赛事' : '新增赛事'
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
  const seconds = String(d.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const getIndex = (index) => {
  return (pagination.pageNum - 1) * pagination.pageSize + index + 1
}

const loadTableData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getMatchPage(params)
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
  searchForm.matchName = ''
  searchForm.matchType = ''
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
  formData.matchName = ''
  formData.matchType = ''
  formData.description = ''
  formData.coverImage = ''
}

const handleAdd = () => {
  resetForm()
  isView.value = false
  currentMatch.value = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  isView.value = false
  Object.assign(formData, row)
  currentMatch.value = { ...row }
  dialogVisible.value = true
}

const handleView = async (row) => {
  try {
    const res = await getMatchById(row.id)
    currentMatch.value = res.data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
    console.error(error)
  }
}

const handleViewSeasons = (row) => {
  router.push({
    path: '/season/list',
    query: {
      matchId: row.id,
      matchName: row.matchName
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除赛事「${row.matchName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteMatch(row.id)
      ElMessage.success('删除成功')
      loadTableData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请选择要删除的赛事')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 条赛事吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = multipleSelection.value.map(item => item.id)
      await deleteMatchBatch(ids)
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
      await updateMatch(formData)
      ElMessage.success('更新成功')
    } else {
      await addMatch(formData)
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

const previewCoverImage = () => {
  if (!formData.coverImage) {
    ElMessage.warning('请先输入图片URL')
    return
  }
  previewVisible.value = true
}

onMounted(() => {
  loadTableData()
})
</script>

<style scoped>
.match-list-container {
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
  width: 200px;
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

.match-name-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.match-avatar {
  flex-shrink: 0;
  border-radius: 8px;
}

.match-avatar-default {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.match-name-text {
  font-weight: 500;
  color: #1e293b;
  font-size: 14px;
}

.desc-text {
  color: #64748b;
  font-size: 13px;
  line-height: 1.5;
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

.match-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.match-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(to right, #f8fafc, #ffffff);
}

.match-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.match-dialog :deep(.el-dialog__body) {
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

.match-form {
  margin: 0;
}

.form-item-full {
  width: 100%;
}

.match-form :deep(.el-form-item__label) {
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

.match-dialog :deep(.el-dialog__footer) {
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

.detail-time {
  font-size: 13px;
  color: #64748b;
}

.detail-desc {
  font-size: 14px;
  color: #475569;
  line-height: 1.6;
}

.cover-preview {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
</style>