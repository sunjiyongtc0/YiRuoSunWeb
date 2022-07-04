<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.processDefinitionName" placeholder="流程定义名称" clearable class="filter-input filter-item" @keyup.enter.native="handleFilter" />
      <el-input v-model="listQuery.taskName" placeholder="当前任务" clearable class="filter-input filter-item" @keyup.enter.native="handleFilter" />
      <el-input v-model="listQuery.assignee" placeholder="当前签收者" clearable class="filter-input filter-item" @keyup.enter.native="handleFilter" />
      <div style="float: right">
        <el-button  type="primary" class="filter-item" icon="el-icon-search" @click="handleFilter">
          查询
        </el-button>
        <el-button  type="primary" class="filter-item" icon="el-icon-refresh-right" @click="handleReset">
          重置
        </el-button>
        <el-button s type="primary" class="filter-item" icon="el-icon-time" @click="handleExecution">
          路径
        </el-button>
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
      <el-table-column label="当前任务" prop="name" />
      <el-table-column label="当前签收者" prop="assignee" />
      <el-table-column label="签收时间" min-width="100px" prop="claimTime">
        <template slot-scope="{row}">
          <span>{{ parseTime(row.claimTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发起者" prop="startUserId" />
      <el-table-column label="操作" align="center">
        <template slot-scope="{row}">
          <el-button type="text" @click="handleTrack(row)">
            <svg-icon icon-class="eye-open" />跟踪
          </el-button>
          <router-link :to="{path: '/taskdetails', query: { processInstanceId: row.processInstanceId, processDefinitionName: row.processDefinitionName, name: row.name, assignee: row.assignee, claimTime: row.claimTime, startUserId: row.startUserId } }">
            <el-button type="text" style="margin-left: 10px">
              <i class="el-icon-info" />详情
            </el-button>
          </router-link>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize" @pagination="getList" />


    <el-dialog  fullscreen :visible.sync="trackDialogVisible">
      <div v-html="svgSrc" />
    </el-dialog>
  </div>
</template>

<script>
import { queryTask, getHighLightedProcessDiagram } from '@/api/activiti/activiti'

export default {
  name: 'Task',
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
        processDefinitionName: undefined,
        taskName: undefined,
        assignee: undefined
      },
      temp: {
        id: undefined
      },
      trackDialogVisible: false,
      svgSrc: ''
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      queryTask(this.listQuery).then(response => {
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
      this.listQuery.processDefinitionName = undefined
      this.listQuery.taskName = undefined
      this.listQuery.assignee = undefined
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
    handleExecution() {
      if (!this.handleValidate()) {
        return
      }
      this.$router.push({ path: '/execution', query: { processInstanceId: this.multipleSelection[0].processInstanceId, flag: 'Task' }})
    },
    handleTrack(row) {
      getHighLightedProcessDiagram(row.processInstanceId).then(response => {
        if (response.code === 200) {
          this.svgSrc = response.msg
          this.trackDialogVisible = true
        } else {
          this.svgSrc = ''
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
