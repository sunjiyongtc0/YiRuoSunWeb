<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.processDefinitionName" placeholder="流程定义名称" clearable class="filter-input filter-item" style="width: 200px" @keyup.enter.native="handleFilter" />
      <el-input v-model="listQuery.taskName" placeholder="当前任务" clearable class="filter-input filter-item" style="width: 200px" @keyup.enter.native="handleFilter" />
      <el-input v-model="listQuery.description" placeholder="任务备注" clearable class="filter-input filter-item" style="width: 200px" @keyup.enter.native="handleFilter" />
      <div style="float: right">
        <el-button  type="primary" class="filter-item" icon="el-icon-search" @click="handleFilter">
          查询
        </el-button>
        <el-button  type="primary" class="filter-item" icon="el-icon-refresh-right" @click="handleReset">
          重置
        </el-button>
        <el-button  type="primary" class="filter-item" icon="el-icon-search" @click="clickSwitch">
          高级查询
        </el-button>
        <el-button  type="primary" class="filter-item" icon="el-icon-s-claim" @click="handleAssign">
          签收
        </el-button>
      </div>
      <div v-if="advancedSearch" style="float: left">
        <el-date-picker v-model="listQuery.startTime" type="datetime" placeholder="任务开始时间" value-format="yyyy-MM-dd HH:mm:ss" clearable class="filter-input filter-item" style="width: 200px" />
        <el-date-picker v-model="listQuery.endTime" type="datetime" placeholder="任务结束时间" value-format="yyyy-MM-dd HH:mm:ss" clearable class="filter-input filter-item" style="width: 200px" />
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
      <el-table-column label="流程定义名称" prop="processDefinitionName" />
      <el-table-column label="流程实例名称" prop="processInstanceName" />
      <el-table-column label="当前任务" prop="name" />
      <el-table-column label="任务开始时间" min-width="100px" prop="startTime">
        <template slot-scope="{row}">
          <span>{{ parseTime(row.taskLocalVariables.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="任务结束时间" min-width="100px" prop="endTime">
        <template slot-scope="{row}">
          <span>{{ parseTime(row.taskLocalVariables.endTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="任务备注" prop="taskLocalVariables.description" />
      <el-table-column label="动态属性" prop="dynamicProperty">
        <template slot-scope="{row}">
          <span>{{ row.dynamicProperty }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="{row}">
          <el-button type="text" @click="handleTrack(row)">
            <svg-icon icon-class="eye-open" />跟踪
          </el-button>
          <router-link :to="{path: '/personaloa/mytaskdetails', query: { processInstanceId: row.processInstanceId, processDefinitionName: row.processDefinitionName, processInstanceName: row.processInstanceName, name: row.name, startTime: row.taskLocalVariables.startTime, endTime: row.taskLocalVariables.endTime, description: row.taskLocalVariables.description, dynamicProperty: row.dynamicProperty } }">
            <el-button type="text" style="margin-left: 10px">
              <i class="el-icon-info" />详情
            </el-button>
          </router-link>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize" @pagination="getList" />

    <el-dialog v-el-drag-dialog fullscreen :visible.sync="trackDialogVisible">
      <div v-html="svgSrc" />
    </el-dialog>
  </div>
</template>

<script>
import { queryPersonalTask, getHighLightedProcessDiagram, claimTask } from '@/api/activiti/activiti'

export default {
  name: 'CandidateTask',
  data() {
    return {
      tableKey: 0,
      list: [],
      multipleSelection: [],
      total: 0,
      listLoading: true,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        taskStatus: 'candidate',
        processDefinitionName: undefined,
        taskName: undefined,
        startTime: undefined,
        endTime: undefined,
        description: undefined
      },
      temp: {
        id: undefined
      },
      trackDialogVisible: false,
      advancedSearch: false,
      svgSrc: ''
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
      queryPersonalTask(this.listQuery).then(response => {
        this.list = response.rows
        this.total = response.total
        this.listLoading = false
      })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    handleFilter() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleReset() {
      this.listQuery.processDefinitionName = undefined
      this.listQuery.taskName = undefined
      this.listQuery.startTime = undefined
      this.listQuery.endTime = undefined
      this.listQuery.description = undefined
      this.handleFilter()
    },
    handleTrack(row) {
      getHighLightedProcessDiagram(row.processInstanceId).then(response => {
        if (response.msg === '操作成功') {
          this.svgSrc = response.msg
          this.trackDialogVisible = true
        } else {
          this.svgSrc = ''
          this.handleWarning(response)
        }
      })
    },
    handleAssign() {
      if (this.multipleSelection.length === 0) {
        this.handleWarning('common.check-required')
        return
      }
      claimTask(this.multipleSelection.map(item => { return item.id }).join(',')).then(response => {
        if (response.msg === '操作成功') {
          this.$message({
            message: 'task.assign-success',
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
