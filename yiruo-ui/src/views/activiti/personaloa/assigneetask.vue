<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.processDefinitionName" placeholder="流程定义名称" clearable class="filter-input filter-item" style="width: 200px" @keyup.enter.native="handleFilter" />
      <el-input v-model="listQuery.taskName" placeholder="当前任务" clearable class="filter-input filter-item" style="width: 200px" @keyup.enter.native="handleFilter" />
      <el-input v-model="listQuery.description" placeholder="备注" clearable class="filter-input filter-item" style="width: 200px" @keyup.enter.native="handleFilter" />
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
        <el-dropdown split-button type="primary" class="filter-item" style="padding-left: 10px" trigger="click" @command="handleCommand">
          <i class="el-icon-s-check" style="margin-right: 5px" /> 审批
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item icon="el-icon-circle-check" command="complete">通过</el-dropdown-item>
            <el-dropdown-item command="delegate"><svg-icon icon-class="user" /> 委派</el-dropdown-item>
            <el-dropdown-item icon="el-icon-back" command="regress">回退</el-dropdown-item>
            <el-dropdown-item icon="el-icon-remove-outline" command="reject">驳回</el-dropdown-item>
            <el-dropdown-item icon="el-icon-circle-close" command="terminate">终止</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
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
      <el-table-column label="路程实例名称" prop="processInstanceName" />
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
          <router-link :to="{path: '/mytaskdetails', query: { processInstanceId: row.processInstanceId, processDefinitionName: row.processDefinitionName, processInstanceName: row.processInstanceName, name: row.name, startTime: row.taskLocalVariables.startTime, endTime: row.taskLocalVariables.endTime, description: row.taskLocalVariables.description, dynamicProperty: row.dynamicProperty } }">
            <el-button type="text" style="margin-left: 10px">
              <i class="el-icon-info" />详情
            </el-button>
          </router-link>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize" @pagination="getList" />

    <!--<el-dialog  :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">-->
      <!--<el-form ref="dataForm" :rules="rules" :model="temp" :label-position="labelPosition" label-width="90px" style="width: 100%">-->
        <!--<el-form-item v-if="dialogStatus==='complete' || dialogStatus==='delegate'" :label="$t('process.candidate')" prop="candidate">-->
          <!--<el-select v-model="temp.candidate" :placeholder="$t('common.choose')" clearable style="width: 100%">-->
            <!--<el-option-group v-for="group in candidateOptions" :key="group.value" :label="group.value">-->
              <!--<el-option v-for="item in group.children" :key="item.key" :label="item.value" :value="item.key" />-->
            <!--</el-option-group>-->
          <!--</el-select>-->
        <!--</el-form-item>-->
        <!--<el-form-item :label="$t('process.comments')" prop="comment">-->
          <!--<el-input v-model="temp.comment" type="textarea" :placeholder="$t('common.enter')" maxlength="512" clearable />-->
        <!--</el-form-item>-->
      <!--</el-form>-->
      <!--<div slot="footer" class="dialog-footer">-->
        <!--<el-button @click="dialogFormVisible = false">-->
          <!--{{ $t('common.cancel') }}-->
        <!--</el-button>-->
        <!--<el-button type="primary" @click="startData()">-->
          <!--{{ $t('common.confirm') }}-->
        <!--</el-button>-->
      <!--</div>-->
    <!--</el-dialog>-->

    <el-dialog  fullscreen :visible.sync="trackDialogVisible">
      <div v-html="svgSrc" />
    </el-dialog>
  </div>
</template>

<script>
import { queryPersonalTask, queryUsername, getHighLightedProcessDiagram, completeTask, resolveTask, delegateTask, regressTask, rejectTask, terminateTask } from '@/api/activiti/activiti'

export default {
  name: 'AssigneeTask',
  data() {
    return {
      tableKey: 0,
      labelPosition: 'right',
      list: [],
      multipleSelection: [],
      total: 0,
      listLoading: true,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        taskStatus: 'assignee',
        processDefinitionName: undefined,
        taskName: undefined,
        startTime: undefined,
        endTime: undefined,
        description: undefined
      },
      candidateOptions: [],
      temp: {
        id: undefined
      },
      trackDialogVisible: false,
      advancedSearch: false,
      svgSrc: '',
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        complete: '通过',
        delegate: '委派',
        regress: '回退',
        reject: '驳回',
        terminate: '终止'
      },
      rules: {
        comment: [{ required: true, validator: this.checkComment, trigger: 'blur' }]
      }
    }
  },
  created() {
    if (window.innerWidth < 700) {
      this.labelPosition = 'top'
    }
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
      queryUsername().then(response => {
        this.candidateOptions = response.data
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
      if (command === 'complete') {
        this.handleComplete(this.multipleSelection[0])
      } else if (command === 'delegate') {
        this.handleDelegate(this.multipleSelection[0])
      } else if (command === 'regress') {
        this.handleRegress(this.multipleSelection[0])
      } else if (command === 'reject') {
        this.handleReject(this.multipleSelection[0])
      } else if (command === 'terminate') {
        this.handleTerminate(this.multipleSelection[0])
      }
    },
    handleComplete(row) {
      this.handleClearValidateDataForm()
      this.temp = Object.assign({}, row) // copy obj
      this.dialogStatus = 'complete'
      this.dialogFormVisible = true
    },
    handleDelegate(row) {
      this.handleClearValidateDataForm()
      this.temp = Object.assign({}, row) // copy obj
      this.dialogStatus = 'delegate'
      this.dialogFormVisible = true
    },
    handleRegress(row) {
      this.handleClearValidateDataForm()
      this.temp = Object.assign({}, row) // copy obj
      this.dialogStatus = 'regress'
      this.dialogFormVisible = true
    },
    handleReject(row) {
      this.handleClearValidateDataForm()
      this.temp = Object.assign({}, row) // copy obj
      this.dialogStatus = 'reject'
      this.dialogFormVisible = true
    },
    handleTerminate(row) {
      this.handleClearValidateDataForm()
      this.temp = Object.assign({}, row) // copy obj
      this.dialogStatus = 'terminate'
      this.dialogFormVisible = true
    },
    handleClearValidateDataForm() {
      this.$nextTick(() => {
        if (this.$refs['dataForm']) {
          this.$refs['dataForm'].clearValidate()
        }
      })
    },
    startData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          if (this.dialogStatus === 'complete' && (!this.temp.delegationState || this.temp.delegationState === 'RESOLVED')) {
            const tempData = {}
            tempData.taskId = this.temp.id
            tempData.processInstanceId = this.temp.processInstanceId
            tempData.candidate = this.temp.candidate
            tempData.comment = this.temp.comment
            completeTask(tempData).then(response => {
              if (response.msg === '操作成功') {
                this.list.unshift(this.temp)
                this.dialogFormVisible = false
                this.$message({
                  message: 'task.complete-success',
                  type: 'success',
                  duration: 2000
                })
                this.handleReset()
              } else {
                this.handleWarning(response)
              }
            })
          } else if (this.dialogStatus === 'complete' && this.temp.delegationState === 'PENDING') {
            const tempData = {}
            tempData.taskId = this.temp.id
            tempData.processInstanceId = this.temp.processInstanceId
            tempData.comment = this.temp.comment
            resolveTask(tempData).then(response => {
              if (response.msg === '操作成功') {
                this.list.unshift(this.temp)
                this.dialogFormVisible = false
                this.$message({
                  message: 'task.handle-delegate-success',
                  type: 'success',
                  duration: 2000
                })
                this.handleReset()
              } else {
                this.handleWarning(response)
              }
            })
          } else if (this.dialogStatus === 'delegate') {
            const tempData = {}
            tempData.taskId = this.temp.id
            tempData.processInstanceId = this.temp.processInstanceId
            tempData.assignee = this.temp.candidate
            tempData.comment = this.temp.comment
            delegateTask(tempData).then(response => {
              if (response.msg === '操作成功') {
                this.list.unshift(this.temp)
                this.dialogFormVisible = false
                this.$message({
                  message: 'task.delegate-success',
                  type: 'success',
                  duration: 2000
                })
                this.handleReset()
              } else {
                this.handleWarning(response)
              }
            })
          } else if (this.dialogStatus === 'regress') {
            const tempData = {}
            tempData.taskId = this.temp.id
            tempData.processInstanceId = this.temp.processInstanceId
            tempData.comment = this.temp.comment
            regressTask(tempData).then(response => {
              if (response.msg === '操作成功') {
                this.list.unshift(this.temp)
                this.dialogFormVisible = false
                this.$message({
                  message: 'task.regress-success',
                  type: 'success',
                  duration: 2000
                })
                this.handleReset()
              } else {
                this.handleWarning(response)
              }
            })
          } else if (this.dialogStatus === 'reject') {
            const tempData = {}
            tempData.taskId = this.temp.id
            tempData.processInstanceId = this.temp.processInstanceId
            tempData.comment = this.temp.comment
            rejectTask(tempData).then(response => {
              if (response.msg === '操作成功') {
                this.list.unshift(this.temp)
                this.dialogFormVisible = false
                this.$message({
                  message: 'task.reject-success',
                  type: 'success',
                  duration: 2000
                })
                this.handleReset()
              } else {
                this.handleWarning(response)
              }
            })
          } else if (this.dialogStatus === 'terminate') {
            const tempData = {}
            tempData.taskId = this.temp.id
            tempData.processInstanceId = this.temp.processInstanceId
            tempData.comment = this.temp.comment
            terminateTask(tempData).then(response => {
              if (response.msg === '操作成功') {
                this.list.unshift(this.temp)
                this.dialogFormVisible = false
                this.$message({
                  message: 'task.terminate-success',
                  type: 'success',
                  duration: 2000
                })
                this.handleReset()
              } else {
                this.handleWarning(response)
              }
            })
          }
        }
      })
    },
    handleWarning(response) {
      this.$message({
        message: response.message || response,
        type: 'warning',
        duration: 2000
      })
    },
    checkComment(rule, value, callback) {
      if (!value) {
        return callback(new Error('task.taskComment-required'))
      } else {
        callback()
      }
    }
  }
}
</script>
