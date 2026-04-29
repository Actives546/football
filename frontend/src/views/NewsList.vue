<template>
  <div class="news-list-container">
    <el-card class="search-card" shadow="hover">
      <el-form :model="searchForm" label-width="80px" class="search-form">
        <div class="search-row">
          <div class="search-item">
            <el-form-item label="新闻标题" class="form-item">
              <el-input
                v-model="searchForm.title"
                placeholder="请输入新闻标题"
                clearable
                @keyup.enter="handleSearch"
                class="search-input"
              />
            </el-form-item>
          </div>
          <div class="search-item">
            <el-form-item label="新闻分类" class="form-item">
              <el-select
                v-model="searchForm.category"
                placeholder="请选择新闻分类"
                clearable
                class="search-select"
                @change="handleSearch"
              >
                <el-option label="赛事新闻" value="赛事新闻" />
                <el-option label="官方通知" value="官方通知" />
                <el-option label="精彩回顾" value="精彩回顾" />
              </el-select>
            </el-form-item>
          </div>
          <div class="search-item">
            <el-form-item label="发布状态" class="form-item">
              <el-select
                v-model="searchForm.status"
                placeholder="请选择发布状态"
                clearable
                class="search-select"
                @change="handleSearch"
              >
                <el-option label="草稿" value="草稿" />
                <el-option label="已发布" value="已发布" />
                <el-option label="已下架" value="已下架" />
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
            <span class="table-title">新闻管理列表</span>
            <el-tag type="info" effect="light" class="table-count">共 {{ total }} 条记录</el-tag>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleBatchCommand" :disabled="multipleSelection.length === 0">
              <el-button type="warning" plain :disabled="multipleSelection.length === 0">
                <el-icon><Operation /></el-icon>
                批量操作
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="publish">
                    <el-icon><Upload /></el-icon>
                    批量发布
                  </el-dropdown-item>
                  <el-dropdown-item command="offline">
                    <el-icon><Download /></el-icon>
                    批量下架
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>
                    <el-icon><Delete /></el-icon>
                    批量删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增新闻
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
        <el-table-column prop="title" label="新闻标题" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="title-cell">
              <el-avatar :size="32" class="title-avatar" v-if="row.coverImage">
                <img :src="row.coverImage" />
              </el-avatar>
              <el-avatar :size="32" class="title-avatar title-avatar-default" v-else>
                <el-icon :size="16"><Document /></el-icon>
              </el-avatar>
              <span class="title-text">{{ row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="新闻分类" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" effect="light" :type="getCategoryTagType(row.category)">
              {{ row.category }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="发布状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" effect="light" :type="getStatusTagType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="80" align="center">
          <template #default="{ row }">
            <span class="view-count">{{ row.viewCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center">
          <template #default="{ row }">
            <span class="time-text">{{ formatDate(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <el-tooltip content="发布" placement="top" v-if="row.status === '草稿'">
              <el-button type="success" link size="small" @click="handlePublish(row)">
                <el-icon><Upload /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="下架" placement="top" v-if="row.status === '已发布'">
              <el-button type="warning" link size="small" @click="handleOffline(row)">
                <el-icon><Download /></el-icon>
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
      width="800px"
      :close-on-click-modal="false"
      destroy-on-close
      class="news-dialog"
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
              class="news-form"
            >
              <el-form-item label="新闻标题" prop="title" class="form-item-full">
                <el-input v-model="formData.title" placeholder="请输入新闻标题" class="form-input" />
              </el-form-item>
              
              <el-form-item label="新闻分类" prop="category">
                <el-select v-model="formData.category" placeholder="请选择新闻分类" class="form-select">
                  <el-option label="赛事新闻" value="赛事新闻" />
                  <el-option label="官方通知" value="官方通知" />
                  <el-option label="精彩回顾" value="精彩回顾" />
                </el-select>
              </el-form-item>
              
              <el-form-item label="发布状态" prop="status">
                <el-select v-model="formData.status" placeholder="请选择发布状态" class="form-select">
                  <el-option label="草稿" value="草稿" />
                  <el-option label="已发布" value="已发布" />
                  <el-option label="已下架" value="已下架" />
                </el-select>
              </el-form-item>
              
              <el-form-item label="封面图片">
                <el-input v-model="formData.coverImage" placeholder="请输入封面图片URL（可选）" class="form-input">
                  <template #append>
                    <el-button @click="previewCoverImage">预览</el-button>
                  </template>
                </el-input>
              </el-form-item>
              
              <el-form-item label="新闻内容">
                <el-input
                  v-model="formData.content"
                  type="textarea"
                  :rows="10"
                  placeholder="请输入新闻内容（支持HTML富文本）"
                  class="form-textarea"
                />
              </el-form-item>
            </el-form>
          </div>
        </div>
        
        <div class="form-section" v-if="isView && currentNews">
          <div class="section-header">
            <div class="section-icon" style="background: linear-gradient(135deg, #10b981 0%, #059669 100%);">
              <el-icon :size="18"><Clock /></el-icon>
            </div>
            <span class="section-title">系统信息</span>
          </div>
          <div class="section-body">
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">浏览量</span>
                <span class="info-value">{{ currentNews.viewCount || 0 }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">创建时间</span>
                <span class="info-value">{{ formatDate(currentNews.createTime) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">更新时间</span>
                <span class="info-value">{{ formatDate(currentNews.updateTime) }}</span>
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
      title="新闻详情"
      width="700px"
      destroy-on-close
      class="detail-dialog"
    >
      <el-descriptions :column="2" border v-if="currentNews" class="detail-descriptions">
        <el-descriptions-item label="新闻标题" :span="2">
          <span class="detail-value">{{ currentNews.title }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="新闻分类" :span="1">
          <el-tag size="small" :type="getCategoryTagType(currentNews.category)">
            {{ currentNews.category }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布状态" :span="1">
          <el-tag size="small" :type="getStatusTagType(currentNews.status)">
            {{ currentNews.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="浏览量" :span="1">
          <span class="detail-view-count">{{ currentNews.viewCount || 0 }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="1">
          <span class="detail-time">{{ formatDate(currentNews.createTime) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="封面图片" :span="2" v-if="currentNews.coverImage">
          <div class="cover-preview">
            <el-image :src="currentNews.coverImage" style="width: 100%; height: 200px;" fit="cover" />
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="新闻内容" :span="2" v-if="currentNews.content">
          <div class="detail-content" v-html="currentNews.content"></div>
        </el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">
          <span class="detail-time">{{ formatDate(currentNews.updateTime) }}</span>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getNewsPage,
  getNewsById,
  addNews,
  updateNews,
  deleteNews,
  deleteNewsBatch,
  updateNewsStatus,
  updateNewsStatusBatch
} from '@/api/news'
import {
  Search,
  Refresh,
  Plus,
  Delete,
  View,
  Edit,
  Document,
  Upload,
  Download,
  Operation,
  ArrowDown,
  InfoFilled,
  Clock
} from '@element-plus/icons-vue'

const loading = ref(false)
const formLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const previewVisible = ref(false)
const isView = ref(false)
const currentNews = ref(null)
const multipleSelection = ref([])
const tableData = ref([])
const total = ref(0)
const tableRef = ref(null)
const formRef = ref(null)

const searchForm = reactive({
  title: '',
  category: '',
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10
})

const formData = reactive({
  id: null,
  title: '',
  coverImage: '',
  category: '',
  content: '',
  status: '草稿'
})

const formRules = {
  title: [
    { required: true, message: '请输入新闻标题', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择新闻分类', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择发布状态', trigger: 'change' }
  ]
}

const dialogTitle = computed(() => {
  if (isView.value) return '查看新闻'
  return formData.id ? '编辑新闻' : '新增新闻'
})

const getCategoryTagType = (category) => {
  const map = {
    '赛事新闻': 'primary',
    '官方通知': 'success',
    '精彩回顾': 'warning'
  }
  return map[category] || 'info'
}

const getStatusTagType = (status) => {
  const map = {
    '草稿': 'info',
    '已发布': 'success',
    '已下架': 'warning'
  }
  return map[status] || 'info'
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
    const res = await getNewsPage(params)
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
  searchForm.title = ''
  searchForm.category = ''
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
  formData.title = ''
  formData.coverImage = ''
  formData.category = ''
  formData.content = ''
  formData.status = '草稿'
}

const handleAdd = () => {
  resetForm()
  isView.value = false
  currentNews.value = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  isView.value = false
  Object.assign(formData, row)
  currentNews.value = { ...row }
  dialogVisible.value = true
}

const handleView = async (row) => {
  try {
    const res = await getNewsById(row.id)
    currentNews.value = res.data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
    console.error(error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除新闻「${row.title}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteNews(row.id)
      ElMessage.success('删除成功')
      loadTableData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handlePublish = (row) => {
  ElMessageBox.confirm(`确定要发布新闻「${row.title}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateNewsStatus(row.id, '已发布')
      ElMessage.success('发布成功')
      loadTableData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleOffline = (row) => {
  ElMessageBox.confirm(`确定要下架新闻「${row.title}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateNewsStatus(row.id, '已下架')
      ElMessage.success('下架成功')
      loadTableData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleBatchCommand = (command) => {
  const ids = multipleSelection.value.map(item => item.id)
  const count = multipleSelection.value.length
  
  if (command === 'publish') {
    ElMessageBox.confirm(`确定要发布选中的 ${count} 条新闻吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await updateNewsStatusBatch(ids, '已发布')
        ElMessage.success('批量发布成功')
        multipleSelection.value = []
        loadTableData()
      } catch (error) {
        console.error(error)
      }
    }).catch(() => {})
  } else if (command === 'offline') {
    ElMessageBox.confirm(`确定要下架选中的 ${count} 条新闻吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await updateNewsStatusBatch(ids, '已下架')
        ElMessage.success('批量下架成功')
        multipleSelection.value = []
        loadTableData()
      } catch (error) {
        console.error(error)
      }
    }).catch(() => {})
  } else if (command === 'delete') {
    ElMessageBox.confirm(`确定要删除选中的 ${count} 条新闻吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await deleteNewsBatch(ids)
        ElMessage.success('批量删除成功')
        multipleSelection.value = []
        loadTableData()
      } catch (error) {
        console.error(error)
      }
    }).catch(() => {})
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    if (formData.id) {
      await updateNews(formData)
      ElMessage.success('更新成功')
    } else {
      await addNews(formData)
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
.news-list-container {
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

.title-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-avatar {
  flex-shrink: 0;
  border-radius: 8px;
}

.title-avatar-default {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.title-text {
  font-weight: 500;
  color: #1e293b;
  font-size: 14px;
}

.view-count {
  font-weight: 500;
  color: #64748b;
  font-size: 14px;
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

.news-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.news-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(to right, #f8fafc, #ffffff);
}

.news-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.news-dialog :deep(.el-dialog__body) {
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

.news-form {
  margin: 0;
}

.form-item-full {
  width: 100%;
}

.news-form :deep(.el-form-item__label) {
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
  grid-template-columns: repeat(3, 1fr);
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

.news-dialog :deep(.el-dialog__footer) {
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

.detail-view-count {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
}

.detail-time {
  font-size: 13px;
  color: #64748b;
}

.detail-content {
  font-size: 14px;
  color: #475569;
  line-height: 1.8;
  max-height: 400px;
  overflow-y: auto;
}

.cover-preview {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
</style>
