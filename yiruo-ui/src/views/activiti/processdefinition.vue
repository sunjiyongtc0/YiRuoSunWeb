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
        <el-button  type="primary" class="filter-item" icon="el-icon-search" @click="clickSwitch">
          高级查询
        </el-button>
        <el-dropdown split-button type="primary" class="filter-item" style="padding-left: 10px" trigger="click" @command="handleCommand">
          <i class="el-icon-s-operation" style="margin-right: 5px" /> 操作
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item icon="el-icon-video-pause" command="suspend">挂起</el-dropdown-item>
            <el-dropdown-item icon="el-icon-video-play" command="activate">激活</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <router-link :to="{path: '/model', query: { } }" style="padding-left: 10px">
          <el-button type="primary" class="filter-item">
            <svg-icon icon-class="drag" /> 流程设计
          </el-button>
        </router-link>
      </div>
      <div v-if="advancedSearch" style="float: left">
        <el-input v-model="listQuery.processDefinitionCategory" placeholder="流程定义类别" clearable class="filter-input filter-item" @keyup.enter.native="handleFilter" />
        <el-input-number v-model="listQuery.processDefinitionVersion" placeholder="流程定义版本" clearable class="filter-input filter-item" @keyup.enter.native="handleFilter" />
        <el-select v-model="listQuery.suspended" placeholder="是否挂起" clearable class="filter-input filter-item">
          <el-option label="是" value="1" />
          <el-option label="否" value="0" />
        </el-select>
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
      <el-table-column label="流程定义编号" prop="key" />
      <el-table-column label="流程定义名称" prop="name" />
      <el-table-column label="流程定义类别" prop="category" />
      <el-table-column label="流程定义版本">
        <template slot-scope="{row}">
          <el-tag v-if="row.version" type="success">
            <span>{{ 'v' + row.version }}</span>
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="是否挂起">
        <template slot-scope="{row}">
          <span v-if="row.suspended === false">否</span>
          <span v-else>是</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="{row}">
          <el-button type="text" @click="handlePreview(row)">
            <i class="el-icon-view" />预览
          </el-button>
          <el-button type="text" @click="handleDownload(row)">
            <i class="el-icon-download" />下载
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--<pagination v-show="total>0" :total="total" :page.sync="listQuery.currentPage" :limit.sync="listQuery.pageSize" @pagination="getList" />-->

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.pageNum"
      :limit.sync="listQuery.pageSize"
      @pagination="getList"
    />

    <el-dialog  fullscreen :visible.sync="previewDialogVisible">
      <div v-html="svgSrc" />
    </el-dialog>
  </div>
</template>

<script>
import { queryProcessDefinition, getProcessResource, getProcessImage, suspendProcessDefinition, activateProcessDefinition } from '@/api/activiti/activiti'
// import waves from '@/directive/waves' // waves directive
// import elDragDialog from '@/directive/el-drag-dialog' // base on element-ui
// import Pagination from '@/components/Pagination' // secondary package based on el-pagination
// import { saveAs } from 'file-saver'

export default {
  name: 'ProcessDefinition',
  // components: { Pagination },
  // directives: { waves, elDragDialog },
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
        processDefinitionCategory: undefined,
        processDefinitionVersion: undefined,
        suspended: undefined
      },
      temp: {
        id: undefined
      },
      previewDialogVisible: false,
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
      queryProcessDefinition(this.listQuery).then(response => {
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
      this.listQuery.processDefinitionKey = undefined
      this.listQuery.processDefinitionName = undefined
      this.listQuery.processDefinitionCategory = undefined
      this.listQuery.processDefinitionVersion = undefined
      this.listQuery.suspended = undefined
      this.handleFilter()
    },
    handlePreview(row) {
      this.previewDialogVisible = true
      getProcessResource(row.deploymentId).then(response => {
        this.svgSrc = response
      })
    },
    handleDownload(row) {
      getProcessImage(row.deploymentId).then(response => {
        if (response.size === 0) {
          this.$message({
            message: 'file.cannotfind',
            type: 'warning',
            duration: 2000
          })
        } else {
          saveAs(response, row.name + '.svg')
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
      if (command === 'suspend') {
        this.handleSuspend(this.multipleSelection[0].id)
      } else if (command === 'activate') {
        this.handleActivate(this.multipleSelection[0].id)
      }
    },
    handleSuspend(id) {
      suspendProcessDefinition(id).then(response => {
        if (response.code === 200) {
          this.$message({
            message: '流程挂起成功',
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
      activateProcessDefinition(id).then(response => {
        if (response.code === 200) {
          this.$message({
            message: '流程激活成功',
            type: 'success',
            duration: 2000
          })
          this.handleFilter()
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
