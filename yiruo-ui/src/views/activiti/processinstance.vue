<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.processDefinitionKey" placeholder="流程定义编号" clearable class="filter-input filter-item" @keyup.enter.native="handleFilter" />
      <el-input v-model="listQuery.processDefinitionName" placeholder="流程定义名称" clearable class="filter-input filter-item" @keyup.enter.native="handleFilter" />
      <div style="float: right">
        <el-button  type="primary" class="filter-item" icon="el-icon-search" @click="handleFilter">
          查询
        </el-button>
        <el-button  type="primary" class="filter-item" icon="el-icon-refresh-right" @click="handleReset">
          重置
        </el-button>
        <el-button type="primary" class="filter-item" icon="el-icon-search" @click="clickSwitch">
          高级查询
        </el-button>
        <el-dropdown split-button type="primary" class="filter-item" style="padding-left: 10px; padding-right: 10px" trigger="click" @command="handleCommand">
          <i class="el-icon-s-operation" style="margin-right: 5px" /> 操作
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item icon="el-icon-time" command="execution">路径</el-dropdown-item>
            <el-dropdown-item icon="el-icon-video-pause" command="suspend">挂起</el-dropdown-item>
            <el-dropdown-item icon="el-icon-video-play" command="activate">激活</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-button type="danger" plain class="filter-item" icon="el-icon-delete" @click="handleDelete">
          删除
        </el-button>
      </div>
      <div v-if="advancedSearch" style="float: left">
        <el-input-number v-model="listQuery.processDefinitionVersion" placeholder="流程版本" clearable class="filter-input filter-item" @keyup.enter.native="handleFilter" />
        <el-input v-model="listQuery.processInstanceName" placeholder="流程实例名称" clearable class="filter-input filter-item" @keyup.enter.native="handleFilter" />
        <el-select v-model="listQuery.suspended" placeholder="是否挂起" clearable class="filter-input filter-item">
          <el-option label="是" value="1" />
          <el-option label="否" value="0" />
        </el-select>
        <el-input v-model="listQuery.startUserId" placeholder="发起人" clearable class="filter-input filter-item" @keyup.enter.native="handleFilter" />
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
      <el-table-column label="是否挂起">
        <template slot-scope="{row}">
          <span v-if="row.suspended === false">否</span>
          <span v-else>是</span>
        </template>
      </el-table-column>
      <el-table-column label="发起人" prop="startUserId" />
      <el-table-column label="任务开始时间" min-width="100px" prop="startTime">
        <template slot-scope="{row}">
          <span>{{ parseTime(row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="{row,$index}">
          <el-button type="text" @click="handleTrack(row)">
            <svg-icon icon-class="eye-open" />跟踪
          </el-button>
          <el-popover :ref="'popover-' + row.id" placement="top" width="160" title="'common.confirm-delete'" trigger="click">
            <div>
              <el-button size="mini" @click="$refs[`popover-` + row.id].doClose()">取消</el-button>
              <el-button type="primary" size="mini" @click="$refs[`popover-` + row.id].doClose();handleDelete(row,$index)">确定</el-button>
            </div>
            <el-button slot="reference" type="text" style="margin-left: 10px">
              <i class="el-icon-delete" />删除
            </el-button>
          </el-popover>
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
import { queryProcessInstance, getHighLightedProcessDiagram, suspendProcessInstance, activateProcessInstance, deleteProcessInstance } from '@/api/activiti/activiti'

export default {
  name: 'ProcessInstance',
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
        processDefinitionKey: undefined,
        processDefinitionName: undefined,
        processDefinitionVersion: undefined,
        processInstanceName: undefined,
        suspended: undefined,
        startUserId: undefined
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
      queryProcessInstance(this.listQuery).then(response => {
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
      this.listQuery.suspended = undefined
      this.listQuery.startUserId = undefined
      this.handleFilter()
    },
    handleTrack(row) {
      getHighLightedProcessDiagram(row.id).then(response => {
        if (response.code === 200) {
          this.svgSrc = response.msg
          this.trackDialogVisible = true
        } else {
          this.svgSrc = ''
          this.handleWarning(response)
        }
      })
    },
    handleCommand(command) {
      if (this.multipleSelection.length === 0) {
        this.handleWarning('common.check-required')
        return
      }
      if (this.multipleSelection.length > 1) {
        this.handleWarning('common.check-one')
        return
      }
      if (command === 'execution') {
        this.$router.push({ path: '/execution', query: { processInstanceId: this.multipleSelection[0].id, flag: 'ProcessInstance' }})
      } else if (command === 'suspend') {
        this.handleSuspend(this.multipleSelection[0].id)
      } else if (command === 'activate') {
        this.handleActivate(this.multipleSelection[0].id)
      }
    },
    handleSuspend(id) {
      suspendProcessInstance(id).then(response => {
        if (response.message === 'success') {
          this.$message({
            message: 'process.processInstance-suspendSuccess',
            type: 'success',
            duration: 2000
          })
          this.handleFilter()
        } else {
          this.handleWarning(response)
        }
      })
    },
    handleActivate(id) {
      activateProcessInstance(id).then(response => {
        if (response.message === 'success') {
          this.$message({
            message: 'process.processInstance-activateSuccess',
            type: 'success',
            duration: 2000
          })
          this.handleFilter()
        } else {
          this.handleWarning(response)
        }
      })
    },
    handleDelete(row, index) {
      if (row.id) {
        this.delete(row.id)
      } else {
        if (this.multipleSelection.length === 0) {
          this.handleWarning('common.choose-delete')
          return
        }
        this.$confirm('common.confirm-batch-delete', 'common.batch-delete', {
          confirmButtonText: 'common.confirm',
          cancelButtonText: 'common.cancel',
          type: 'warning'
        }).then(() => {
          this.delete(this.multipleSelection.map(item => { return item.id }).join(','))
        }).catch(() => {})
      }
    },
    delete(deleteArray) {
      deleteProcessInstance(deleteArray).then(response => {
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
