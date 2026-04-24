<template>
  <div class="match-list-container">
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="赛事名称">
          <el-input
            v-model="searchForm.matchName"
            placeholder="请输入赛事名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="赛事类型">
          <el-input
            v-model="searchForm.matchType"
            placeholder="请输入赛事类型"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="未开始" value="SCHEDULED" />
            <el-option label="进行中" value="LIVE" />
            <el-option label="已结束" value="FINISHED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="table-header">
          <span class="table-title">赛事信息列表</span>
          <div class="table-actions">
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
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="matchName" label="赛事名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="matchType" label="赛事类型" width="120" show-overflow-tooltip />
        <el-table-column prop="startTime" label="开始时间" width="170">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="170">
          <template #default="{ row }">
            {{ formatDate(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small" effect="light">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && tableData.length === 0" description="暂无数据" style="margin: 40px 0;" />
      
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
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="赛事名称" prop="matchName">
              <el-input v-model="formData.matchName" placeholder="请输入赛事名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="赛事类型" prop="matchType">
              <el-select v-model="formData.matchType" placeholder="请选择赛事类型" style="width: 100%">
                <el-option label="足球联赛" value="足球联赛" />
                <el-option label="杯赛" value="杯赛" />
                <el-option label="友谊赛" value="友谊赛" />
                <el-option label="亚冠联赛" value="亚冠联赛" />
                <el-option label="欧冠联赛" value="欧冠联赛" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="赛事状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择赛事状态" style="width: 100%">
                <el-option label="未开始" value="SCHEDULED" />
                <el-option label="进行中" value="LIVE" />
                <el-option label="已结束" value="FINISHED" />
                <el-option label="已取消" value="CANCELLED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="formData.startTime"
                type="datetime"
                placeholder="请选择开始时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-date-picker
                v-model="formData.endTime"
                type="datetime"
                placeholder="请选择结束时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="封面图片">
          <el-input v-model="formData.coverImage" placeholder="请输入封面图片URL（可选）" />
        </el-form-item>
        
        <el-form-item label="赛事描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入赛事描述信息（可选）"
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
      title="赛事详情"
      width="600px"
      destroy-on-close
    >
      <el-descriptions :column="1" border v-if="currentMatch">
        <el-descriptions-item label="赛事名称">{{ currentMatch.matchName }}</el-descriptions-item>
        <el-descriptions-item label="赛事类型">{{ currentMatch.matchType }}</el-descriptions-item>
        <el-descriptions-item label="赛事状态">
          <el-tag :type="getStatusType(currentMatch.status)" size="small">
            {{ getStatusText(currentMatch.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ formatDate(currentMatch.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间" v-if="currentMatch.endTime">{{ formatDate(currentMatch.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="封面图片" v-if="currentMatch.coverImage">
          <el-image :src="currentMatch.coverImage" style="width: 200px; height: 120px;" fit="cover" />
        </el-descriptions-item>
        <el-descriptions-item label="赛事描述" v-if="currentMatch.description">
          {{ currentMatch.description }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(currentMatch.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDate(currentMatch.updateTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
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
  Edit
} from '@element-plus/icons-vue'

const loading = ref(false)
const formLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const isView = ref(false)
const currentMatch = ref(null)
const multipleSelection = ref([])
const tableData = ref([])
const total = ref(0)
const tableRef = ref(null)
const formRef = ref(null)

const searchForm = reactive({
  matchName: '',
  matchType: '',
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10
})

const formData = reactive({
  id: null,
  matchName: '',
  matchType: '',
  status: 'SCHEDULED',
  startTime: null,
  endTime: null,
  description: '',
  coverImage: ''
})

const formRules = {
  matchName: [
    { required: true, message: '请输入赛事名称', trigger: 'blur' }
  ],
  matchType: [
    { required: true, message: '请选择赛事类型', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择赛事状态', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ]
}

const dialogTitle = computed(() => {
  if (isView.value) return '查看赛事'
  return formData.id ? '编辑赛事' : '新增赛事'
})

const getStatusType = (status) => {
  const map = {
    'SCHEDULED': 'info',
    'LIVE': 'warning',
    'FINISHED': 'success',
    'CANCELLED': 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'SCHEDULED': '未开始',
    'LIVE': '进行中',
    'FINISHED': '已结束',
    'CANCELLED': '已取消'
  }
  return map[status] || status
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
  formData.matchName = ''
  formData.matchType = ''
  formData.status = 'SCHEDULED'
  formData.startTime = null
  formData.endTime = null
  formData.description = ''
  formData.coverImage = ''
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
    const res = await getMatchById(row.id)
    currentMatch.value = res.data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
    console.error(error)
  }
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

onMounted(() => {
  loadTableData()
})
</script>

<style scoped>
.match-list-container {
  width: 100%;
}

.search-card {
  border-radius: 12px;
  margin-bottom: 20px;
}

.search-card :deep(.el-card__body) {
  padding: 16px 20px;
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

.table-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  padding: 16px 20px;
  border-top: 1px solid #f3f4f6;
}
</style>
