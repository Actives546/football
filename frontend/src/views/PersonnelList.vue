<template>
  <div class="personnel-list-container">
    <el-card class="search-card" shadow="hover">
      <el-form :model="searchForm" label-width="80px" class="search-form">
        <div class="search-row">
          <div class="search-item">
            <el-form-item label="人员类型" class="form-item">
              <el-select
                v-model="searchForm.personType"
                placeholder="请选择人员类型"
                clearable
                class="search-select"
                @change="handleSearch"
              >
                <el-option
                  v-for="item in personTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </div>
          <div class="search-item">
            <el-form-item label="人员姓名" class="form-item">
              <el-input
                v-model="searchForm.personName"
                placeholder="请输入人员姓名"
                clearable
                class="search-input"
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </div>
          <div class="search-item">
            <el-form-item label="手机号" class="form-item">
              <el-input
                v-model="searchForm.phone"
                placeholder="请输入手机号"
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
                <el-option label="正常" :value="1" />
                <el-option label="禁用" :value="0" />
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
              <span class="tree-title">部门树</span>
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
                <span class="table-title">人员信息列表</span>
                <el-tag type="info" effect="light" class="table-count">共 {{ total }} 条记录</el-tag>
              </div>
              <div class="header-right">
                <el-button type="danger" plain :disabled="multipleSelection.length === 0" @click="handleBatchDelete">
                  <el-icon><Delete /></el-icon>
                  批量删除
                </el-button>
                <el-button type="primary" @click="handleAdd">
                  <el-icon><Plus /></el-icon>
                  新增人员
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
            <el-table-column prop="personName" label="姓名" min-width="120" show-overflow-tooltip>
              <template #default="{ row }">
                <div class="person-name-cell">
                  <el-avatar :size="32" class="person-avatar">
                    <el-icon :size="16"><UserFilled /></el-icon>
                  </el-avatar>
                  <div class="person-info">
                    <span class="person-name">{{ row.personName }}</span>
                    <span class="person-phone" v-if="row.phone">{{ row.phone }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="personType" label="类型" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getPersonTypeTagType(row.personType)" size="small" effect="dark">
                  {{ getPersonTypeText(row.personType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="orgName" label="所属部门" min-width="150" show-overflow-tooltip>
              <template #default="{ row }">
                <span class="org-text">{{ row.orgName || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="phone" label="手机号" width="140" align="center" show-overflow-tooltip>
              <template #default="{ row }">
                <span class="phone-text">{{ row.phone || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="gender" label="性别" width="80" align="center">
              <template #default="{ row }">
                <span class="gender-text">{{ getGenderText(row.gender) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small" effect="light">
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
      </el-col>
    </el-row>
    
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      :width="isView ? '800px' : '680px'"
      :close-on-click-modal="false"
      destroy-on-close
      class="personnel-dialog"
    >
      <div class="dialog-content" v-loading="formLoading">
        <div v-if="isView && currentPerson" class="detail-view-container">
          <div class="detail-header-card">
            <div class="detail-avatar-section">
              <el-avatar :size="80" class="detail-avatar">
                <el-icon :size="40"><UserFilled /></el-icon>
              </el-avatar>
              <div class="detail-basic-info">
                <div class="detail-name-row">
                  <span class="detail-name">{{ currentPerson.personName }}</span>
                  <el-tag :type="getPersonTypeTagType(currentPerson.personType)" size="large" effect="dark">
                    {{ getPersonTypeText(currentPerson.personType) }}
                  </el-tag>
                </div>
                <div class="detail-sub-info">
                  <span class="detail-org">
                    <el-icon><Folder /></el-icon>
                    {{ currentPerson.orgName || '-' }}
                  </span>
                  <span class="detail-status">
                    <el-tag :type="currentPerson.status === 1 ? 'success' : 'danger'" size="small" effect="light">
                      {{ getStatusText(currentPerson.status) }}
                    </el-tag>
                  </span>
                </div>
              </div>
            </div>
          </div>

          <el-tabs v-model="activeTab" class="detail-tabs">
            <el-tab-pane label="基本信息" name="basic">
              <div class="detail-section">
                <div class="detail-section-header">
                  <div class="detail-section-icon" style="background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);">
                    <el-icon :size="16"><InfoFilled /></el-icon>
                  </div>
                  <span class="detail-section-title">联系方式</span>
                </div>
                <div class="detail-info-grid">
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #3b82f6;">
                      <el-icon><Phone /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">手机号</span>
                      <span class="detail-info-value">{{ formatValue(currentPerson.phone) }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #10b981;">
                      <el-icon><Message /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">邮箱</span>
                      <span class="detail-info-value">{{ formatValue(currentPerson.email) }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #f59e0b;">
                      <el-icon><Location /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">地址</span>
                      <span class="detail-info-value">{{ formatValue(currentPerson.address) }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #ef4444;">
                      <el-icon><Document /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">身份证号</span>
                      <span class="detail-info-value">{{ formatValue(currentPerson.idCard) }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="detail-section">
                <div class="detail-section-header">
                  <div class="detail-section-icon" style="background: linear-gradient(135deg, #10b981 0%, #059669 100%);">
                    <el-icon :size="16"><UserFilled /></el-icon>
                  </div>
                  <span class="detail-section-title">个人信息</span>
                </div>
                <div class="detail-info-grid">
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #8b5cf6;">
                      <el-icon><UserFilled /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">性别</span>
                      <span class="detail-info-value">{{ getGenderText(currentPerson.gender) }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #ec4899;">
                      <el-icon><Calendar /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">出生日期</span>
                      <span class="detail-info-value">{{ formatDate(currentPerson.birthday) }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="详细信息" name="detail">
              <div class="detail-section">
                <div class="detail-section-header">
                  <div class="detail-section-icon" style="background: linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%);">
                    <el-icon :size="16"><Briefcase /></el-icon>
                  </div>
                  <span class="detail-section-title">工作信息</span>
                </div>
                <div class="detail-info-grid">
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #8b5cf6;">
                      <el-icon><Briefcase /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">职位</span>
                      <span class="detail-info-value">{{ formatValue(currentPerson.position) }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #06b6d4;">
                      <el-icon><Calendar /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">入职日期</span>
                      <span class="detail-info-value">{{ formatDate(currentPerson.joinDate) }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #14b8a6;">
                      <el-icon><School /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">学历</span>
                      <span class="detail-info-value">{{ formatValue(currentPerson.education) }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #f97316;">
                      <el-icon><Document /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">专业</span>
                      <span class="detail-info-value">{{ formatValue(currentPerson.major) }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #64748b;">
                      <el-icon><School /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">毕业院校</span>
                      <span class="detail-info-value">{{ formatValue(currentPerson.school) }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="detail-section">
                <div class="detail-section-header">
                  <div class="detail-section-icon" style="background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);">
                    <el-icon :size="16"><Warning /></el-icon>
                  </div>
                  <span class="detail-section-title">紧急联系人</span>
                </div>
                <div class="detail-info-grid">
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #ef4444;">
                      <el-icon><UserFilled /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">联系人</span>
                      <span class="detail-info-value">{{ formatValue(currentPerson.emergencyContact) }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #f97316;">
                      <el-icon><Phone /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">联系电话</span>
                      <span class="detail-info-value">{{ formatValue(currentPerson.emergencyPhone) }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="detail-section" v-if="currentPerson.workExperience || currentPerson.detailRemark">
                <div class="detail-section-header">
                  <div class="detail-section-icon" style="background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);">
                    <el-icon :size="16"><Document /></el-icon>
                  </div>
                  <span class="detail-section-title">其他信息</span>
                </div>
                <div class="detail-text-section" v-if="currentPerson.workExperience">
                  <span class="detail-text-label">工作经验</span>
                  <p class="detail-text-content">{{ currentPerson.workExperience }}</p>
                </div>
                <div class="detail-text-section" v-if="currentPerson.detailRemark">
                  <span class="detail-text-label">备注</span>
                  <p class="detail-text-content">{{ currentPerson.detailRemark }}</p>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="球员信息" name="player" v-if="currentPerson.personType === 'PLAYER'">
              <div class="detail-section">
                <div class="detail-section-header">
                  <div class="detail-section-icon" style="background: linear-gradient(135deg, #10b981 0%, #059669 100%);">
                    <el-icon :size="16"><Medal /></el-icon>
                  </div>
                  <span class="detail-section-title">球员基本信息</span>
                </div>
                <div class="detail-info-grid">
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #10b981;">
                      <el-icon><Medal /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">球衣号码</span>
                      <span class="detail-info-value">{{ formatValue(currentPerson.jerseyNumber) }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #3b82f6;">
                      <el-icon><Location /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">场上位置</span>
                      <span class="detail-info-value">{{ formatValue(currentPerson.fieldPosition) }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #8b5cf6;">
                      <el-icon><DataLine /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">身高</span>
                      <span class="detail-info-value">{{ currentPerson.height ? currentPerson.height + ' cm' : '-' }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #ec4899;">
                      <el-icon><DataLine /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">体重</span>
                      <span class="detail-info-value">{{ currentPerson.weight ? currentPerson.weight + ' kg' : '-' }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #f59e0b;">
                      <el-icon><Trophy /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">惯用脚</span>
                      <span class="detail-info-value">{{ getPreferredFootText(currentPerson.preferredFoot) }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #ef4444;">
                      <el-icon><Flag /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">国籍</span>
                      <span class="detail-info-value">{{ formatValue(currentPerson.nationality) }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="detail-section">
                <div class="detail-section-header">
                  <div class="detail-section-icon" style="background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);">
                    <el-icon :size="16"><Timer /></el-icon>
                  </div>
                  <span class="detail-section-title">合同信息</span>
                </div>
                <div class="detail-info-grid">
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #10b981;">
                      <el-icon><Calendar /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">入队日期</span>
                      <span class="detail-info-value">{{ formatDate(currentPerson.teamJoinDate) }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #ef4444;">
                      <el-icon><Calendar /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">合同到期</span>
                      <span class="detail-info-value">{{ formatDate(currentPerson.contractEndDate) }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #8b5cf6;">
                      <el-icon><Money /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">身价</span>
                      <span class="detail-info-value">{{ currentPerson.marketValue ? currentPerson.marketValue + ' 万' : '-' }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="detail-section" v-if="currentPerson.technicalFeatures || currentPerson.pastExperience">
                <div class="detail-section-header">
                  <div class="detail-section-icon" style="background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);">
                    <el-icon :size="16"><Document /></el-icon>
                  </div>
                  <span class="detail-section-title">球员描述</span>
                </div>
                <div class="detail-text-section" v-if="currentPerson.technicalFeatures">
                  <span class="detail-text-label">技术特点</span>
                  <p class="detail-text-content">{{ currentPerson.technicalFeatures }}</p>
                </div>
                <div class="detail-text-section" v-if="currentPerson.pastExperience">
                  <span class="detail-text-label">过往经历</span>
                  <p class="detail-text-content">{{ currentPerson.pastExperience }}</p>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="系统信息" name="system">
              <div class="detail-section">
                <div class="detail-section-header">
                  <div class="detail-section-icon" style="background: linear-gradient(135deg, #64748b 0%, #475569 100%);">
                    <el-icon :size="16"><Clock /></el-icon>
                  </div>
                  <span class="detail-section-title">系统记录</span>
                </div>
                <div class="detail-info-grid">
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #10b981;">
                      <el-icon><Clock /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">创建时间</span>
                      <span class="detail-info-value">{{ formatDateTime(currentPerson.createTime) }}</span>
                    </div>
                  </div>
                  <div class="detail-info-item">
                    <div class="detail-info-icon" style="color: #3b82f6;">
                      <el-icon><Clock /></el-icon>
                    </div>
                    <div class="detail-info-content">
                      <span class="detail-info-label">更新时间</span>
                      <span class="detail-info-value">{{ formatDateTime(currentPerson.updateTime) }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>

        <div v-else>
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
                class="personnel-form"
              >
                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="人员姓名" prop="personName">
                      <el-input v-model="formData.personName" placeholder="请输入人员姓名" class="form-input" :disabled="isView" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="人员类型" prop="personType">
                      <el-select v-model="formData.personType" placeholder="请选择人员类型" class="form-select" :disabled="isView">
                        <el-option
                          v-for="item in personTypeOptions"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="所属部门" prop="orgId">
                      <el-tree-select
                        v-model="formData.orgId"
                        :data="orgOptions"
                        :props="treeProps"
                        placeholder="请选择所属部门"
                        check-strictly
                        filterable
                        class="form-select"
                        :disabled="isView"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="手机号">
                      <el-input v-model="formData.phone" placeholder="请输入手机号" class="form-input" :disabled="isView" />
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="邮箱">
                      <el-input v-model="formData.email" placeholder="请输入邮箱" class="form-input" :disabled="isView" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="身份证号">
                      <el-input v-model="formData.idCard" placeholder="请输入身份证号" class="form-input" maxlength="18" :disabled="isView" />
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="性别">
                      <el-radio-group v-model="formData.gender" :disabled="isView">
                        <el-radio :value="1">男</el-radio>
                        <el-radio :value="2">女</el-radio>
                        <el-radio :value="0">未知</el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="出生日期">
                      <el-date-picker
                        v-model="formData.birthday"
                        type="date"
                        placeholder="请选择出生日期"
                        value-format="YYYY-MM-DD"
                        class="form-select"
                        :disabled="isView"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="状态">
                      <el-radio-group v-model="formData.status" :disabled="isView">
                        <el-radio :value="1">正常</el-radio>
                        <el-radio :value="0">禁用</el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="头像">
                      <el-input v-model="formData.avatar" placeholder="请输入头像URL（可选）" class="form-input" :disabled="isView" />
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-form-item label="地址">
                  <el-input v-model="formData.address" type="textarea" :rows="2" placeholder="请输入地址（可选）" class="form-textarea" :disabled="isView" />
                </el-form-item>
                
                <el-form-item label="备注">
                  <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注（可选）" class="form-textarea" :disabled="isView" />
                </el-form-item>
              </el-form>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="footer-btn">关闭</el-button>
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
  getPersonById,
  getPersonPage,
  addPerson,
  updatePerson,
  deletePerson,
  deletePersonBatch,
  getPersonDetailById,
  savePersonDetail
} from '@/api/personnel'
import {
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
  UserFilled,
  InfoFilled,
  Clock,
  Phone,
  Message,
  Location,
  Calendar,
  Document,
  Briefcase,
  Medal,
  Money,
  Timer,
  Flag,
  Trophy,
  DataLine,
  School,
  Warning
} from '@element-plus/icons-vue'

const loading = ref(false)
const formLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isView = ref(false)
const currentPerson = ref(null)
const multipleSelection = ref([])
const tableData = ref([])
const treeData = ref([])
const total = ref(0)
const tableRef = ref(null)
const formRef = ref(null)
const treeRef = ref(null)
const activeTab = ref('basic')

const personTypeOptions = [
  { label: '人员', value: 'PERSON' },
  { label: '球员', value: 'PLAYER' }
]

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
  orgId: null,
  personType: '',
  personName: '',
  phone: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10
})

const formData = reactive({
  id: null,
  orgId: null,
  personType: '',
  personName: '',
  phone: '',
  email: '',
  gender: 0,
  birthday: '',
  idCard: '',
  address: '',
  avatar: '',
  remark: '',
  status: 1
})

const formRules = {
  personName: [
    { required: true, message: '请输入人员姓名', trigger: 'blur' }
  ],
  personType: [
    { required: true, message: '请选择人员类型', trigger: 'change' }
  ],
  orgId: [
    { required: true, message: '请选择所属部门', trigger: 'change' }
  ]
}

const orgOptions = computed(() => {
  return treeData.value
})

const dialogTitle = computed(() => {
  if (isView.value) return '查看人员'
  return formData.id ? '编辑人员' : '新增人员'
})

const getPersonTypeText = (type) => {
  const map = {
    'PERSON': '人员',
    'PLAYER': '球员'
  }
  return map[type] || type || '-'
}

const getPersonTypeTagType = (type) => {
  const map = {
    'PERSON': 'primary',
    'PLAYER': 'success'
  }
  return map[type] || 'info'
}

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

const getGenderText = (gender) => {
  const map = {
    0: '未知',
    1: '男',
    2: '女'
  }
  return map[gender] || '未知'
}

const getStatusText = (status) => {
  return status === 1 ? '正常' : '禁用'
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

const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const formatValue = (value, defaultValue = '-') => {
  if (value === null || value === undefined || value === '') {
    return defaultValue
  }
  return value
}

const getPreferredFootText = (foot) => {
  const map = {
    'LEFT': '左脚',
    'RIGHT': '右脚',
    'BOTH': '双脚'
  }
  return map[foot] || '-'
}

const getIndex = (index) => {
  return (pagination.pageNum - 1) * pagination.pageSize + index + 1
}

const loadTreeData = async () => {
  try {
    const res = await getOrganizationTree()
    treeData.value = res.data.tree || []
  } catch (error) {
    console.error('加载部门树失败:', error)
  }
}

const loadTableData = async () => {
  loading.value = true
  try {
    const params = {
      orgId: searchForm.orgId,
      personType: searchForm.personType,
      personName: searchForm.personName,
      phone: searchForm.phone,
      status: searchForm.status,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getPersonPage(params)
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
  searchForm.orgId = null
  searchForm.personType = ''
  searchForm.personName = ''
  searchForm.phone = ''
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

const handleNodeClick = (data) => {
  searchForm.orgId = data.id
  pagination.pageNum = 1
  loadTableData()
}

const resetForm = () => {
  formData.id = null
  formData.orgId = null
  formData.personType = ''
  formData.personName = ''
  formData.phone = ''
  formData.email = ''
  formData.gender = 0
  formData.birthday = ''
  formData.idCard = ''
  formData.address = ''
  formData.avatar = ''
  formData.remark = ''
  formData.status = 1
}

const handleAdd = () => {
  resetForm()
  isView.value = false
  currentPerson.value = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  isView.value = false
  Object.assign(formData, row)
  if (row.birthday) {
    formData.birthday = row.birthday
  }
  currentPerson.value = { ...row }
  dialogVisible.value = true
}

const handleView = async (row) => {
  try {
    const res = await getPersonDetailById(row.id)
    currentPerson.value = res.data
    isView.value = true
    activeTab.value = 'basic'
    Object.assign(formData, res.data)
    if (res.data.birthday) {
      formData.birthday = res.data.birthday
    }
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
    console.error(error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除人员「${row.personName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deletePerson(row.id)
      ElMessage.success('删除成功')
      loadTableData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请选择要删除的人员')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 条人员吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = multipleSelection.value.map(item => item.id)
      await deletePersonBatch(ids)
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
    const submitData = { ...formData }
    if (!submitData.birthday) {
      delete submitData.birthday
    }
    
    if (formData.id) {
      await updatePerson(submitData)
      ElMessage.success('更新成功')
    } else {
      await addPerson(submitData)
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
  loadTreeData()
  loadTableData()
})
</script>

<style scoped>
.personnel-list-container {
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

.person-name-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.person-avatar {
  flex-shrink: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
}

.person-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.person-name {
  font-weight: 500;
  color: #1e293b;
  font-size: 14px;
}

.person-phone {
  font-size: 12px;
  color: #909399;
}

.org-text,
.phone-text,
.gender-text {
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

.personnel-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.personnel-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(to right, #f8fafc, #ffffff);
}

.personnel-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.personnel-dialog :deep(.el-dialog__body) {
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

.personnel-form {
  margin: 0;
}

.personnel-form :deep(.el-form-item__label) {
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

.personnel-dialog :deep(.el-dialog__footer) {
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

.detail-view-container {
  width: 100%;
}

.detail-header-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
}

.detail-avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.detail-avatar {
  background: rgba(255, 255, 255, 0.2);
  border: 3px solid rgba(255, 255, 255, 0.5);
}

.detail-basic-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-name-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-name {
  font-size: 24px;
  font-weight: 700;
  color: #ffffff;
}

.detail-sub-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.detail-org {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
}

.detail-tabs {
  background: #ffffff;
  border-radius: 12px;
  padding: 0;
}

.detail-tabs :deep(.el-tabs__header) {
  margin: 0;
  padding: 0 20px;
  border-bottom: 1px solid #f1f5f9;
}

.detail-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.detail-tabs :deep(.el-tabs__item) {
  height: 50px;
  line-height: 50px;
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
}

.detail-tabs :deep(.el-tabs__item.is-active) {
  color: #3b82f6;
}

.detail-tabs :deep(.el-tabs__active-bar) {
  background: linear-gradient(90deg, #3b82f6 0%, #1d4ed8 100%);
  height: 3px;
  border-radius: 2px;
}

.detail-tabs :deep(.el-tabs__content) {
  padding: 20px;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.detail-section-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
}

.detail-section-title {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.detail-info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

@media (max-width: 600px) {
  .detail-info-grid {
    grid-template-columns: 1fr;
  }
}

.detail-info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  transition: all 0.2s ease;
}

.detail-info-item:hover {
  background: #ffffff;
  border-color: #cbd5e1;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.detail-info-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: rgba(59, 130, 246, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.detail-info-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
  overflow: hidden;
}

.detail-info-label {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.detail-info-value {
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-text-section {
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  margin-bottom: 12px;
}

.detail-text-section:last-child {
  margin-bottom: 0;
}

.detail-text-label {
  display: block;
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
  margin-bottom: 8px;
}

.detail-text-content {
  font-size: 14px;
  color: #1e293b;
  line-height: 1.6;
  margin: 0;
  white-space: pre-wrap;
}
</style>
