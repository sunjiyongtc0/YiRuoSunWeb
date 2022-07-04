<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.deploymentName" placeholder="模型名称" clearable class="filter-input filter-item" @keyup.enter.native="handleFilter" />
      <el-input v-model="listQuery.category" placeholder="模型类别" clearable class="filter-input filter-item" @keyup.enter.native="handleFilter" />
      <div style="float: right">
        <el-button  type="primary" class="filter-item" icon="el-icon-search" @click="handleFilter">
          查询
        </el-button>
        <el-button  type="primary" class="filter-item" icon="el-icon-refresh-right" @click="handleReset">
          重置
        </el-button>
        <el-button type="danger" plain class="filter-item" icon="el-icon-delete" @click="handleDelete">
          删除
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
      <el-table-column label="模型名称" prop="name" />
      <el-table-column label="模型类别" prop="category" />
      <el-table-column label="模型版本">
        <template slot-scope="{row}">
          <el-tag v-if="row.modelVersion" type="success">
            {{ 'v' + row.modelVersion }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="部署时间" min-width="100px" prop="deploymentTime">
        <template slot-scope="{row}">
          <span>{{ parseTime(row.deploymentTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="{row}">
          <el-button type="text" @click="handlePreview(row)">
            <i class="el-icon-view" />预览
          </el-button>
          <el-button  type="text" @click="handleDownload(row)">
            <i class="el-icon-download" />下载
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize" @pagination="getList" />

    <el-dialog  fullscreen :visible.sync="previewDialogVisible">
      <div v-html="svgSrc" />
    </el-dialog>
  </div>
</template>

<script>
import { queryDeployment, getProcessResource, getProcessImage, deleteDeployment } from '@/api/activiti/activiti'
// import { saveAs } from 'file-saver'

export default {
  name: 'Deployment',
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
        deploymentName: undefined,
        category: undefined
      },
      temp: {
        id: undefined
      },
      previewDialogVisible: false,
      svgSrc: ''
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      queryDeployment(this.listQuery).then(response => {
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
      this.listQuery.deploymentName = undefined
      this.listQuery.category = undefined
      this.handleFilter()
    },
    handlePreview(row) {
      this.previewDialogVisible = true
      getProcessResource(row.id).then(response => {
        this.svgSrc = response
      })
    },
    handleDownload(row) {
      getProcessImage(row.id).then(response => {
        if (response.size === 0) {
          this.$message({
            message: '没有可下载的文件',
            type: 'warning',
            duration: 2000
          })
        } else {
          // saveAs(response, row.name + '.svg')
        }
      })
    },
    handleDelete(row, index) {
      if (row.id) {
        this.delete(row.id)
      } else {
        if (this.multipleSelection.length === 0) {
          this.handleWarning('没有所选数据')
          return
        }
        this.$confirm('删除所选数据', '数据删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.delete(this.multipleSelection.map(item => { return item.id }).join(','))
        }).catch(() => {})
      }
    },
    delete(deleteArray) {
      deleteDeployment(deleteArray).then(response => {
        if (response.msg === '操作成功') {
          this.$message({
            message: '删除成功',
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
