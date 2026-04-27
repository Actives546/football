<template>
  <div class="organization-list-container">
    <el-card class="search-card" shadow="hover">
      <el-form :model="searchForm" label-width="80px" class="search-form">
        <div class="search-row">
          <div class="search-item">
            <el-form-item label="机构名称" class="form-item">
              <el-input
                v-model="searchForm.orgName"
                placeholder="请输入机构名称"
                clearable
                class="search-input"
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </div>
          <div class="search-item">
            <el-form-item label="机构类型" class="form-item">
              <el-select
                v-model="searchForm.orgType"
                placeholder="请选择机构类型"
                clearable
                class="search-select"
                @change="handleSearch"
              >
                <el-option
                  v-for="item in orgTypeOptions"
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
    
    <el-row :gutter="20" class="content-row">
      <el-col :span="6">
        <el-card class="tree-card" shadow="hover">
          <template #header>
            <div class="tree-header">
              <span class="tree-title">机构树</span>
              <el-tooltip content="刷新" placement="top">
                <el-button type="primary" link size="small" @click="loadTreeData">
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </el-tooltip>
            </div>
          </template>
          <div class="tree-wrapper">
            <el-tree
              ref="treeRef"
              :data="treeData"
              :props="treeProps"
              node-key="id"
              highlight-current
              default-expand-all
              @node-click="handleNodeClick"
              class="organization-tree"
            >
              <template #default="{ node, data }">
                <div class="tree-node">
                  <span class="tree-node-icon">
                    <el-icon><Folder /></el-icon>
                  </span>
                  <span class="tree-node-label">{{ node.label }}</span>
                  <span class="tree-node-badge" v-if="data.orgType">
                    <el-tag :type="getOrgTypeTagType(data.orgType)" size="small" effect="light">
                      {{ getOrgTypeText(data.orgType) }}
                    </el-tag>
                  </span>
                </div>
              </template>
            </el-tree>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="18">
        <el-card class="table-card" shadow="hover">
          <template #header>
            <div class="table-header">
              <div class="header-left">
                <span class="table-title">机构信息列表</span>
                <el-tag type="info" effect="light" class="table-count">共 {{ total }} 条记录</el-tag>
              </div>
              <div class="header-right">
                <el-button type="danger" plain :disabled="multipleSelection.length === 0" @click="handleBatchDelete">
                  <el-icon><Delete /></el-icon>
                  批量删除
                </el-button>
                <el-button type="primary" @click="handleAdd">
                  <el-icon><Plus /></el-icon>
                  新增机构
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
            <el-table-column prop="orgName" label="机构名称" min-width="180" show-overflow-tooltip>
              <template #default="{ row }">
                <div class="org-name-cell">
                  <el-avatar :size="32" class="org-avatar">
                    <el-icon :size="16"><OfficeBuilding /></el-icon>
                  </el-avatar>
                  <div class="org-info">
                    <span class="org-name">{{ row.orgName }}</span>
                    <span class="org-short-name" v-if="row.orgShortName">{{ row.orgShortName }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="orgType" label="机构类型" width="140" align="center">
              <template #default="{ row }">
                <el-tag :type="getOrgTypeTagType(row.orgType)" size="small" effect="dark">
                  {{ getOrgTypeText(row.orgType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="parentName" label="上级机构" min-width="150" show-overflow-tooltip>
              <template #default="{ row }">
                <span class="parent-text">{{ row.parentName || '无' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="orgShortName" label="机构简写" width="120" align="center" show-overflow-tooltip>
              <template #default="{ row }">
                <span class="short-name-text">{{ row.orgShortName || '-' }}</span>
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
      </el-col>
    </el-row>
    
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="680px"
      :close-on-click-modal="false"
      destroy-on-close
      class="organization-dialog"
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
              class="organization-form"
            >
              <el-row :gutter="24">
                <el-col :span="12">
                  <el-form-item label="机构名称" prop="orgName">
                    <el-input v-model="formData.orgName" placeholder="请输入机构名称" class="form-input" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="机构简写">
                    <el-input v-model="formData.orgShortName" placeholder="请输入机构简写（可选）" class="form-input" />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="24">
                <el-col :span="12">
                  <el-form-item label="机构类型">
                    <el-select v-model="formData.orgType" placeholder="请选择机构类型（可选）" clearable class="form-select">
                      <el-option
                        v-for="item in orgTypeOptions"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="上级机构">
                    <el-tree-select
                      v-model="formData.parentId"
                      :data="parentOrgOptions"
                      :props="treeProps"
                      placeholder="请选择上级机构（可选）"
                      clearable
                      check-strictly
                      filterable
                      class="form-select"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-form-item label="机构介绍">
                <el-input
                  v-model="formData.description"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入机构介绍信息（可选）"
                  class="form-textarea"
                />
              </el-form-item>
            </el-form>
          </div>
        </div>
        
        <div class="form-section" v-if="isView && currentOrg">
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
                <span class="info-value">{{ formatDateTime(currentOrg.createTime) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">更新时间</span>
                <span class="info-value">{{ formatDateTime(currentOrg.updateTime) }}</span>
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
      title="机构详情"
      width="600px"
      destroy-on-close
      class="detail-dialog"
    >
      <el-descriptions :column="2" border v-if="currentOrg" class="detail-descriptions">
        <el-descriptions-item label="机构ID" :span="1">
          <span class="detail-value">{{ currentOrg.id }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="机构类型" :span="1">
          <el-tag :type="getOrgTypeTagType(currentOrg.orgType)" size="small">
            {{ getOrgTypeText(currentOrg.orgType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="机构名称" :span="2">
          <span class="detail-value">{{ currentOrg.orgName }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="机构简写" :span="1">
          <span class="detail-text">{{ currentOrg.orgShortName || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="上级机构" :span="1">
          <span class="detail-text">{{ currentOrg.parentName || '无' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="1">
          <span class="detail-time">{{ formatDateTime(currentOrg.createTime) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="1">
          <span class="detail-time">{{ formatDateTime(currentOrg.updateTime) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="机构介绍" :span="2" v-if="currentOrg.description">
          <span class="detail-desc">{{ currentOrg.description }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getOrganizationPage,
  getOrganizationById,
  addOrganization,
  updateOrganization,
  deleteOrganization,
  deleteOrganizationBatch,
  getOrganizationTree
} from '@/api/organization'
import {
  Search,
  Refresh,
  Plus,
  Delete,
  View,
  Edit,
  Folder,
  OfficeBuilding,
  InfoFilled,
  Clock
} from '@element-plus/icons-vue'

const loading = ref(false)
const formLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const isView = ref(false)
const currentOrg = ref(null)
const multipleSelection = ref([])
const tableData = ref([])
const treeData = ref([])
const total = ref(0)
const tableRef = ref(null)
const formRef = ref(null)
const treeRef = ref(null)

const orgTypeOptions = [
  { label: '公司', value: 'COMPANY' },
  { label: '部门', value: 'DEPARTMENT' },
  { label: '小组', value: 'TEAM' },
  { label: '学校', value: 'SCHOOL' },
  { label: '俱乐部', value: 'CLUB' },
  { label: '其他', value: 'OTHER' }
]

const treeProps = {
  label: 'label',
  children: 'children'
}

const searchForm = reactive({
  orgName: '',
  orgType: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10
})

const formData = reactive({
  id: null,
  orgName: '',
  orgType: '',
  orgShortName: '',
  description: '',
  parentId: null
})

const formRules = {
  orgName: [
    { required: true, message: '请输入机构名称', trigger: 'blur' }
  ]
}

const parentOrgOptions = computed(() => {
  return treeData.value
})

const dialogTitle = computed(() => {
  if (isView.value) return '查看机构'
  return formData.id ? '编辑机构' : '新增机构'
})

const getOrgTypeText = (type) => {
  const map = {
    'COMPANY': '公司',
    'DEPARTMENT': '部门',
    'TEAM': '小组',
    'SCHOOL': '学校',
    'CLUB': '俱乐部',
    'OTHER': '其他'
  }
  return map[type] || type || '-'
}

const getOrgTypeTagType = (type) => {
  const map = {
    'COMPANY': 'primary',
    'DEPARTMENT': 'success',
    'TEAM': 'warning',
    'SCHOOL': 'info',
    'CLUB': 'danger',
    'OTHER': ''
  }
  return map[type] || 'info'
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

const loadTreeData = async () => {
  try {
    const res = await getOrganizationTree()
    treeData.value = res.data.tree || []
  } catch (error) {
    console.error('加载机构树失败:', error)
  }
}

const loadTableData = async () => {
  loading.value = true
  try {
    const params = {
      orgName: searchForm.orgName,
      orgType: searchForm.orgType,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getOrganizationPage(params)
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
  searchForm.orgName = ''
  searchForm.orgType = ''
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

const handleNodeClick = (data) => {
  searchForm.orgName = ''
  searchForm.orgType = ''
  pagination.pageNum = 1
  loadTableData()
}

const resetForm = () => {
  formData.id = null
  formData.orgName = ''
  formData.orgType = ''
  formData.orgShortName = ''
  formData.description = ''
  formData.parentId = null
}

const handleAdd = () => {
  resetForm()
  isView.value = false
  currentOrg.value = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  isView.value = false
  Object.assign(formData, row)
  currentOrg.value = { ...row }
  dialogVisible.value = true
}

const handleView = async (row) => {
  try {
    const res = await getOrganizationById(row.id)
    currentOrg.value = res.data
    isView.value = true
    Object.assign(formData, res.data)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
    console.error(error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除机构「${row.orgName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteOrganization(row.id)
      ElMessage.success('删除成功')
      loadTableData()
      loadTreeData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请选择要删除的机构')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 条机构吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = multipleSelection.value.map(item => item.id)
      await deleteOrganizationBatch(ids)
      ElMessage.success('批量删除成功')
      multipleSelection.value = []
      loadTableData()
      loadTreeData()
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
      await updateOrganization(formData)
      ElMessage.success('更新成功')
    } else {
      await addOrganization(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadTableData()
    loadTreeData()
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadTreeData()
  loadTableData()
})
</script>

<style scoped>
.organization-list-container {
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

.content-row {
  margin-bottom: 0;
}

.tree-card {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  height: 100%;
}

.tree-card :deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(to right, #f8fafc, #ffffff);
}

.tree-card :deep(.el-card__body) {
  padding: 0;
}

.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tree-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  position: relative;
}

.tree-title::before {
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

.tree-wrapper {
  padding: 16px;
  max-height: 580px;
  overflow-y: auto;
}

.organization-tree {
  width: 100%;
}

.organization-tree :deep(.el-tree-node__content) {
  height: 40px;
  border-radius: 8px;
  margin-bottom: 4px;
  transition: all 0.2s ease;
}

.organization-tree :deep(.el-tree-node__content:hover) {
  background-color: #f0f7ff;
}

.organization-tree :deep(.el-tree-node__content.is-current) {
  background-color: #dbeafe;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.tree-node-icon {
  color: #3b82f6;
}

.tree-node-label {
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
}

.tree-node-badge {
  margin-left: auto;
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

.org-name-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.org-avatar {
  flex-shrink: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
}

.org-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.org-name {
  font-weight: 500;
  color: #1e293b;
  font-size: 14px;
}

.org-short-name {
  font-size: 12px;
  color: #909399;
}

.parent-text {
  color: #475569;
  font-size: 13px;
}

.short-name-text {
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

.organization-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.organization-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(to right, #f8fafc, #ffffff);
}

.organization-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.organization-dialog :deep(.el-dialog__body) {
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

.organization-form {
  margin: 0;
}

.organization-form :deep(.el-form-item__label) {
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

.organization-dialog :deep(.el-dialog__footer) {
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
