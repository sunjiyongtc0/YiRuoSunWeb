<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.processDefinitionKey" placeholder="流程定义编号" clearable class="filter-input filter-item" style="width: 200px" @keyup.enter.native="handleFilter" />
      <el-input v-model="listQuery.processDefinitionName" placeholder="流程定义名称" clearable class="filter-input filter-item" style="width: 200px" @keyup.enter.native="handleFilter" />
      <div style="float: right">
        <el-button type="primary" class="filter-item" icon="el-icon-search" @click="handleFilter">
          查询
        </el-button>
        <el-button  type="primary" class="filter-item" icon="el-icon-refresh-right" @click="handleReset">
          重置
        </el-button>
        <el-button  type="primary" class="filter-item" icon="el-icon-search" @click="clickSwitch">
          高级查询
        </el-button>
        <el-dropdown split-button type="primary" class="filter-item" style="padding-left: 10px; padding-right: 10px" trigger="click" @command="handleCommand">
          <i class="el-icon-s-operation" style="margin-right: 5px" /> 操作
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item icon="el-icon-tickets" command="activity">节点执行历史</el-dropdown-item>
            <el-dropdown-item icon="el-icon-info" command="detail">流程节点详细历史</el-dropdown-item>
            <el-dropdown-item icon="el-icon-c-scale-to-original" command="variable">流程实例变量历史</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-button type="danger" plain class="filter-item" icon="el-icon-delete" @click="handleDelete">
          删除
        </el-button>
      </div>
      <div v-if="advancedSearch" style="float: left">
        <el-input-number v-model="listQuery.processDefinitionVersion" placeholder="流程定义版本" clearable class="filter-input filter-item" style="width: 200px" @keyup.enter.native="handleFilter" />
        <el-input v-model="listQuery.processInstanceName" placeholder="流程实例名称" clearable class="filter-input filter-item" style="width: 200px" @keyup.enter.native="handleFilter" />
        <el-input v-model="listQuery.startUserId" placeholder="发起人" clearable class="filter-input filter-item" style="width: 200px" @keyup.enter.native="handleFilter" />
        <el-date-picker v-model="listQuery.startedAfterDate" type="datetime" placeholder="发起开始时间" value-format="yyyy-MM-dd HH:mm:ss" clearable class="filter-input filter-item" style="width: 200px" />
        <el-date-picker v-model="listQuery.finishedBeforeDate" type="datetime" placeholder="发起结束时间" value-format="yyyy-MM-dd HH:mm:ss" clearable class="filter-input filter-item" style="width: 200px" />
      </div>
    </div>

    <el-table
      :key="tableKey"
      ref="multipleTable"
      v-loading="listLoading"
      :data="list"
      border
      fit
      highlight-current-row
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="40" />
      <el-table-column label="流程定义编号" prop="processDefinitionKey" />
      <el-table-column label="流程定义名称" prop="processDefinitionName" />
      <el-table-column label="流程定义版本">
        <template slot-scope="{row}">
          <el-tag v-if="row.processDefinitionVersion" type="success">
            <span>{{ 'v' + row.processDefinitionVersion }}</span>
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="流程实例名称" prop="name" />
      <el-table-column label="流程状态">
        <template slot-scope="{row}">
          <span v-if="row.endTime === null">运行中</span>
          <span v-else>已结束</span>
        </template>
      </el-table-column>
      <el-table-column label="发起人" prop="startUserId" />
      <el-table-column label="任务开始时间" min-width="70px" prop="startTime">
        <template slot-scope="{row}">
          <span>{{ parseTime(row.startTime ) }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="任务结束时间" min-width="70px" prop="endTime">
        <template slot-scope="{row}">
          <span>{{ parseTime(row.endTime) }}</span>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize" @pagination="getList" />

  </div>
</template>

<script>
import { queryHistoricProcessInstance, deleteHistoricProcessInstance } from '@/api/activiti/activiti'

export default {
  name: 'HistoricProcessInstance',
  data() {
    return {
      tableKey: 0,
      list: [],
      multipleSelection: [],
      total: 0,
      listLoading: true,
      advancedSearch: false,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        processDefinitionKey: undefined,
        processDefinitionName: undefined,
        processDefinitionVersion: undefined,
        processInstanceName: undefined,
        startUserId: undefined,
        startedAfterDate: undefined,
        finishedBeforeDate: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    clickSwitch() {
      this.advancedSearch = !this.advancedSearch
    },
    getList() {
      this.listLoading = true
      queryHistoricProcessInstance(this.listQuery).then(response => {
        this.list = response.rows
        this.total = response.total
        this.listLoading = false
      })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    handleFilter() {
      this.listQuery.currentPage = 1
      this.getList()
    },
    handleReset() {
      this.listQuery.processDefinitionKey = undefined
      this.listQuery.processDefinitionName = undefined
      this.listQuery.processDefinitionVersion = undefined
      this.listQuery.processInstanceName = undefined
      this.listQuery.startUserId = undefined
      this.listQuery.startedAfterDate = undefined
      this.listQuery.finishedBeforeDate = undefined
      this.handleFilter()
    },
    handleValidate() {
      if (this.multipleSelection.length === 0) {
        this.handleWarning('common.check-required')
        return false
      }
      if (this.multipleSelection.length > 1) {
        this.handleWarning('common.check-one')
        return false
      }
      return true
    },
    handleCommand(command) {
      if (!this.handleValidate()) {
        return
      }
      if (command === 'activity') {
        this.$router.push({ path: '/historicactivityinstance', query: { processInstanceId: this.multipleSelection[0].processInstanceId }})
      } else if (command === 'detail') {
        this.$router.push({ path: '/historicdetail', query: { processInstanceId: this.multipleSelection[0].processInstanceId }})
      } else if (command === 'variable') {
        this.$router.push({ path: '/historicvariableinstance', query: { processInstanceId: this.multipleSelection[0].processInstanceId }})
      }
    },
    handleDelete(row, index) {
      if (row.id) {
        this.delete(row.id)
      } else {
        if (!this.handleValidate()) {
          return
        }
        this.$confirm('common.confirm-batch-delete', 'common.batch-delete', {
          confirmButtonText: 'common.confirm',
          cancelButtonText: 'common.cancel',
          type: 'warning'
        }).then(() => {
          this.delete(this.multipleSelection.map(item => { return item.processInstanceId }).join(','))
        }).catch(() => {})
      }
    },
    delete(deleteArray) {
      deleteHistoricProcessInstance(deleteArray).then(response => {
        if (response.message === 'success') {
          this.$message({
            message: 'common.delete-success',
            type: 'success',
            duration: 2000
          })
          this.handleReset()
        } else {
          this.handleWarning(response)
        }
      })
    },
    handleWarning(response) {
      this.$message({
        message: response.message || response,
        type: 'warning',
        duration: 2000
      })
    }
  }
}
</script>
