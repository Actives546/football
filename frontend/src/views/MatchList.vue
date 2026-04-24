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
        <el-form-item label="联赛">
          <el-input
            v-model="searchForm.league"
            placeholder="请输入联赛名称"
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
        <el-table-column prop="matchName" label="赛事名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="league" label="联赛" width="120" show-overflow-tooltip />
        <el-table-column label="对阵双方" min-width="200">
          <template #default="{ row }">
            <div class="match-versus">
              <span class="team home-team">{{ row.homeTeam }}</span>
              <span class="score" v-if="row.status === 'FINISHED'">
                {{ row.homeScore }} - {{ row.awayScore }}
              </span>
              <span class="vs" v-else>VS</span>
              <span class="team away-team">{{ row.awayTeam }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="matchTime" label="比赛时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.matchTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="venue" label="场地" width="120" show-overflow-tooltip />
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
      width="700px"
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
            <el-form-item label="联赛" prop="league">
              <el-input v-model="formData.league" placeholder="请输入联赛名称" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主队" prop="homeTeam">
              <el-input v-model="formData.homeTeam" placeholder="请输入主队名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客队" prop="awayTeam">
              <el-input v-model="formData.awayTeam" placeholder="请输入客队名称" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="比赛时间" prop="matchTime">
              <el-date-picker
                v-model="formData.matchTime"
                type="datetime"
                placeholder="请选择比赛时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
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
            <el-form-item label="主队得分">
              <el-input-number
                v-model="formData.homeScore"
                :min="0"
                :max="99"
                placeholder="主队得分"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客队得分">
              <el-input-number
                v-model="formData.awayScore"
                :min="0"
                :max="99"
                placeholder="客队得分"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="比赛场地">
              <el-input v-model="formData.venue" placeholder="请输入比赛场地" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主裁判">
              <el-input v-model="formData.referee" placeholder="请输入主裁判" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="观众人数">
              <el-input-number
                v-model="formData.audience"
                :min="0"
                placeholder="观众人数"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="门票价格">
              <el-input-number
                v-model="formData.ticketPrice"
                :min="0"
                :precision="2"
                placeholder="门票价格"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="赛事描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入赛事描述"
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
        <el-descriptions-item label="联赛">{{ currentMatch.league }}</el-descriptions-item>
        <el-descriptions-item label="对阵双方">
          <span style="color: #16a34a; font-weight: 600;">{{ currentMatch.homeTeam }}</span>
          <span v-if="currentMatch.status === 'FINISHED'" style="margin: 0 12px; font-weight: 600;">
            {{ currentMatch.homeScore }} - {{ currentMatch.awayScore }}
          </span>
          <span v-else style="margin: 0 12px;">VS</span>
          <span style="color: #2563eb; font-weight: 600;">{{ currentMatch.awayTeam }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="比赛时间">{{ formatDate(currentMatch.matchTime) }}</el-descriptions-item>
        <el-descriptions-item label="比赛场地">{{ currentMatch.venue || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentMatch.status)" size="small">
            {{ getStatusText(currentMatch.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="主裁判" v-if="currentMatch.referee">{{ currentMatch.referee }}</el-descriptions-item>
        <el-descriptions-item label="观众人数" v-if="currentMatch.audience">{{ currentMatch.audience }} 人</el-descriptions-item>
        <el-descriptions-item label="门票价格" v-if="currentMatch.ticketPrice">
          ¥{{ currentMatch.ticketPrice }}
        </el-descriptions-item>
        <el-descriptions-item label="赛事描述" v-if="currentMatch.description">
          {{ currentMatch.description }}
        </el-descriptions-item>
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
  league: '',
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10
})

const formData = reactive({
  id: null,
  matchName: '',
  league: '',
  homeTeam: '',
  awayTeam: '',
  matchTime: null,
  venue: '',
  status: 'SCHEDULED',
  homeScore: 0,
  awayScore: 0,
  referee: '',
  audience: null,
  ticketPrice: null,
  description: ''
})

const formRules = {
  matchName: [
    { required: true, message: '请输入赛事名称', trigger: 'blur' }
  ],
  league: [
    { required: true, message: '请输入联赛名称', trigger: 'blur' }
  ],
  homeTeam: [
    { required: true, message: '请输入主队名称', trigger: 'blur' }
  ],
  awayTeam: [
    { required: true, message: '请输入客队名称', trigger: 'blur' }
  ],
  matchTime: [
    { required: true, message: '请选择比赛时间', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
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
  searchForm.league = ''
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
  formData.league = ''
  formData.homeTeam = ''
  formData.awayTeam = ''
  formData.matchTime = null
  formData.venue = ''
  formData.status = 'SCHEDULED'
  formData.homeScore = 0
  formData.awayScore = 0
  formData.referee = ''
  formData.audience = null
  formData.ticketPrice = null
  formData.description = ''
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

.match-versus {
  display: flex;
  align-items: center;
  gap: 6px;
}

.team {
  font-weight: 500;
}

.home-team {
  color: #16a34a;
}

.away-team {
  color: #2563eb;
}

.score {
  font-size: 14px;
  font-weight: 700;
  color: #1f2937;
  padding: 2px 8px;
  background: #f3f4f6;
  border-radius: 4px;
}

.vs {
  font-size: 12px;
  color: #9ca3af;
  font-weight: 600;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  padding: 16px 20px;
  border-top: 1px solid #f3f4f6;
}
</style>
