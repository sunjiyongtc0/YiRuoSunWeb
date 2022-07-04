<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.processDefinitionKey" placeholder="流程定义编号" clearable class="filter-input filter-item" style="width: 200px" @keyup.enter.native="handleFilter" />
      <el-input v-model="listQuery.processDefinitionName" placeholder="流程定义名称" clearable class="filter-input filter-item" style="width: 200px" @keyup.enter.native="handleFilter" />
      <el-input-number v-model="listQuery.processDefinitionVersion" placeholder="流程定义版本" clearable class="filter-input filter-item" style="width: 200px" @keyup.enter.native="handleFilter" />
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
      </div>
      <div v-if="advancedSearch" style="float: left">
        <el-date-picker v-model="listQuery.startedAfterDate" type="datetime" placeholder="发起时间" value-format="yyyy-MM-dd HH:mm:ss" clearable class="filter-input filter-item" style="width: 200px" />
        <el-date-picker v-model="listQuery.finishedBeforeDate" type="datetime" placeholder="结束时间" value-format="yyyy-MM-dd HH:mm:ss" clearable class="filter-input filter-item" style="width: 200px" />
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
      <el-table-column label="流程状态" prop="processStatus" />
      <el-table-column label="流程发起时间" min-width="100px" prop="startTime">
        <template slot-scope="{row}">
          <span>{{ parseTime(row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="流程结束时间" min-width="100px" prop="endTime">
        <template slot-scope="{row}">
          <span>{{ parseTime(row.endTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="{row}">
          <el-button type="text" @click="handleTrack(row)">
            <svg-icon icon-class="eye-open" />跟踪
          </el-button>
          <router-link :to="{path: '/myprocessdetails', query: { processInstanceId: row.processInstanceId, processDefinitionKey: row.processDefinitionKey, processDefinitionName: row.processDefinitionName, processDefinitionVersion: row.processDefinitionVersion, processStatus: row.processStatus, startTime: row.startTime, startUserId: row.startUserId, endTime: row.endTime } }">
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
import { queryHistoricProcessInstance, getHighLightedProcessDiagram } from '@/api/activiti/activiti'

export default {
  name: 'MyProcess',
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
        status: 'personal',
        processDefinitionKey: undefined,
        processDefinitionName: undefined,
        processDefinitionVersion: undefined,
        startedAfterDate: undefined,
        finishedBeforeDate: undefined
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
      this.listQuery.startedAfterDate = undefined
      this.listQuery.finishedBeforeDate = undefined
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
