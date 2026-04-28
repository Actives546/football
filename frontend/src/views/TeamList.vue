<template>
  <div class="team-list-container">
    <el-card class="search-card" shadow="hover">
      <el-form :model="searchForm" label-width="80px" class="search-form">
        <div class="search-row">
          <div class="search-item">
            <el-form-item label="球队名称" class="form-item">
              <el-input
                v-model="searchForm.teamName"
                placeholder="请输入球队名称"
                clearable
                class="search-input"
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </div>
          <div class="search-item">
            <el-form-item label="地区" class="form-item">
              <el-input
                v-model="searchForm.region"
                placeholder="请输入地区"
                clearable
                class="search-input"
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </div>
          <div class="search-item">
            <el-form-item label="状态" class="form-item">
              <el-select
                v-model="searchForm.status"
                placeholder="请选择状态"
                clearable
                class="search-select"
                @change="handleSearch"
              >
                <el-option
                  v-for="item in statusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
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
            <span class="table-title">球队信息列表</span>
            <el-tag type="info" effect="light" class="table-count">共 {{ total }} 条记录</el-tag>
          </div>
          <div class="header-right">
            <el-button type="warning" plain :disabled="multipleSelection.length === 0" @click="handleBatchEnable">
              <el-icon><CircleCheck /></el-icon>
              批量启用
            </el-button>
            <el-button type="info" plain :disabled="multipleSelection.length === 0" @click="handleBatchDisable">
              <el-icon><CircleClose /></el-icon>
              批量禁用
            </el-button>
            <el-button type="danger" plain :disabled="multipleSelection.length === 0" @click="handleBatchDelete">
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
            <el-button type="success" @click="handleExport">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增球队
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
        row-key="id"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="序号" width="70" align="center">
          <template #default="{ $index }">
            <span class="index-text">{{ getIndex($index) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="teamName" label="球队名称" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="team-name-cell">
              <el-avatar :size="36" class="team-avatar" :src="row.logoUrl">
                <el-icon :size="18"><Trophy /></el-icon>
              </el-avatar>
              <div class="team-info">
                <span class="team-name">{{ row.teamName }}</span>
                <span class="team-region" v-if="row.region">{{ row.region }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="region" label="地区" width="120" align="center" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="region-text">{{ row.region || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="foundingDate" label="成立时间" width="120" align="center">
          <template #default="{ row }">
            <span class="date-text">{{ formatDate(row.foundingDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="homeStadium" label="主场" min-width="140" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="stadium-text">{{ row.homeStadium || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="headCoach" label="主教练" width="100" align="center" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="coach-text">{{ row.headCoach || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="contactPerson" label="联系人" width="100" align="center" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="contact-text">{{ row.contactPerson || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small" effect="dark">
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
      width="720px"
      :close-on-click-modal="false"
      destroy-on-close
      class="team-dialog"
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
              class="team-form"
            >
              <el-row :gutter="24">
                <el-col :span="12">
                  <el-form-item label="球队名称" prop="teamName">
                    <el-input v-model="formData.teamName" placeholder="请输入球队名称" class="form-input" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="地区">
                    <el-input v-model="formData.region" placeholder="请输入地区（可选）" class="form-input" />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="24">
                <el-col :span="12">
                  <el-form-item label="成立时间">
                    <el-date-picker
                      v-model="formData.foundingDate"
                      type="date"
                      placeholder="请选择成立时间（可选）"
                      value-format="YYYY-MM-DD"
                      class="form-date"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="主场">
                    <el-input v-model="formData.homeStadium" placeholder="请输入主场（可选）" class="form-input" />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="24">
                <el-col :span="12">
                  <el-form-item label="主教练">
                    <el-input v-model="formData.headCoach" placeholder="请输入主教练（可选）" class="form-input" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="联系人">
                    <el-input v-model="formData.contactPerson" placeholder="请输入联系人（可选）" class="form-input" />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="24">
                <el-col :span="12">
                  <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="formData.status" class="status-radio">
                      <el-radio :value="1">启用</el-radio>
                      <el-radio :value="0">禁用</el-radio>
                    </el-radio-group>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="球队Logo">
                    <el-input v-model="formData.logoUrl" placeholder="请输入Logo URL（可选）" class="form-input" />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-form-item label="球队简介">
                <el-input
                  v-model="formData.description"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入球队简介信息（可选）"
                  class="form-textarea"
                />
              </el-form-item>
            </el-form>
          </div>
        </div>
        
        <div class="form-section" v-if="isView && currentTeam">
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
                <span class="info-value">{{ formatDateTime(currentTeam.createTime) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">更新时间</span>
                <span class="info-value">{{ formatDateTime(currentTeam.updateTime) }}</span>
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getTeamPage,
  getTeamById,
  addTeam,
  updateTeam,
  deleteTeam,
  deleteTeamBatch,
  exportTeam,
  enableTeamBatch,
  disableTeamBatch
} from '@/api/team'
import {
  Search,
  Refresh,
  Plus,
  Delete,
  View,
  Edit,
  Download,
  Trophy,
  InfoFilled,
  Clock,
  CircleCheck,
  CircleClose
} from '@element-plus/icons-vue'

const loading = ref(false)
const formLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isView = ref(false)
const currentTeam = ref(null)
const multipleSelection = ref([])
const tableData = ref([])
const total = ref(0)
const tableRef = ref(null)
const formRef = ref(null)

const statusOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 }
]

const searchForm = reactive({
  teamName: '',
  region: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10
})

const formData = reactive({
  id: null,
  teamName: '',
  logoUrl: '',
  region: '',
  foundingDate: null,
  homeStadium: '',
  headCoach: '',
  contactPerson: '',
  status: 1,
  description: ''
})

const formRules = {
  teamName: [
    { required: true, message: '请输入球队名称', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const dialogTitle = computed(() => {
  if (isView.value) return '查看球队'
  return formData.id ? '编辑球队' : '新增球队'
})

const getStatusText = (status) => {
  return status === 1 ? '启用' : status === 0 ? '禁用' : '未知'
}

const getStatusTagType = (status) => {
  return status === 1 ? 'success' : 'danger'
}

const formatDate = (date) => {
  if (!date) return '-'
  return date
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

const loadTableData = async () => {
  loading.value = true
  try {
    const params = {
      teamName: searchForm.teamName,
      region: searchForm.region,
      status: searchForm.status,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getTeamPage(params)
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
  searchForm.teamName = ''
  searchForm.region = ''
  searchForm.status = null
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
  formData.teamName = ''
  formData.logoUrl = ''
  formData.region = ''
  formData.foundingDate = null
  formData.homeStadium = ''
  formData.headCoach = ''
  formData.contactPerson = ''
  formData.status = 1
  formData.description = ''
}

const handleAdd = () => {
  resetForm()
  isView.value = false
  currentTeam.value = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  isView.value = false
  Object.assign(formData, row)
  if (row.foundingDate) {
    formData.foundingDate = row.foundingDate
  }
  currentTeam.value = { ...row }
  dialogVisible.value = true
}

const handleView = async (row) => {
  try {
    const res = await getTeamById(row.id)
    currentTeam.value = res.data
    isView.value = true
    Object.assign(formData, res.data)
    if (res.data.foundingDate) {
      formData.foundingDate = res.data.foundingDate
    }
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
    console.error(error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除球队「${row.teamName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteTeam(row.id)
      ElMessage.success('删除成功')
      loadTableData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请选择要删除的球队')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 条球队吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = multipleSelection.value.map(item => item.id)
      await deleteTeamBatch(ids)
      ElMessage.success('批量删除成功')
      multipleSelection.value = []
      loadTableData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleBatchEnable = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请选择要启用的球队')
    return
  }
  ElMessageBox.confirm(`确定要启用选中的 ${multipleSelection.value.length} 条球队吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = multipleSelection.value.map(item => item.id)
      await enableTeamBatch(ids)
      ElMessage.success('批量启用成功')
      multipleSelection.value = []
      loadTableData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleBatchDisable = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请选择要禁用的球队')
    return
  }
  ElMessageBox.confirm(`确定要禁用选中的 ${multipleSelection.value.length} 条球队吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = multipleSelection.value.map(item => item.id)
      await disableTeamBatch(ids)
      ElMessage.success('批量禁用成功')
      multipleSelection.value = []
      loadTableData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleExport = async () => {
  try {
    const params = {
      teamName: searchForm.teamName,
      region: searchForm.region,
      status: searchForm.status
    }
    const res = await exportTeam(params)
    const data = res.data || []
    
    if (data.length === 0) {
      ElMessage.warning('没有可导出的数据')
      return
    }
    
    const headers = ['球队名称', '地区', '成立时间', '主场', '主教练', '联系人', '状态', '球队简介']
    const csvContent = [
      headers.join(','),
      ...data.map(row => [
        `"${row.teamName || ''}"`,
        `"${row.region || ''}"`,
        `"${row.foundingDate || ''}"`,
        `"${row.homeStadium || ''}"`,
        `"${row.headCoach || ''}"`,
        `"${row.contactPerson || ''}"`,
        `"${getStatusText(row.status)}"`,
        `"${(row.description || '').replace(/"/g, '""')}"`
      ].join(','))
    ].join('\n')
    
    const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = `球队数据_${new Date().toISOString().slice(0, 10)}.csv`
    link.click()
    URL.revokeObjectURL(link.href)
    
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
    console.error(error)
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    if (formData.id) {
      await updateTeam(formData)
      ElMessage.success('更新成功')
    } else {
      await addTeam(formData)
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

onMounted(() => {
  loadTableData()
})
</script>

<style scoped>
.team-list-container {
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

.team-name-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.team-avatar {
  flex-shrink: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: #fff;
}

.team-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.team-name {
  font-weight: 500;
  color: #1e293b;
  font-size: 14px;
}

.team-region {
  font-size: 12px;
  color: #909399;
}

.region-text,
.stadium-text,
.coach-text,
.contact-text,
.date-text {
  color: #475569;
  font-size: 13px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  padding: 20px 24px;
  border-top: 1px solid #f1f5f9;
  background: #fafafa;
}

.team-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.team-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(to right, #f8fafc, #ffffff);
}

.team-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.team-dialog :deep(.el-dialog__body) {
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

.team-form {
  margin: 0;
}

.team-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #475569;
}

.form-input,
.form-date,
.form-textarea {
  width: 100%;
}

.form-textarea :deep(.el-textarea__inner) {
  resize: none;
}

.status-radio {
  display: flex;
  gap: 20px;
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

.team-dialog :deep(.el-dialog__footer) {
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
</style>
