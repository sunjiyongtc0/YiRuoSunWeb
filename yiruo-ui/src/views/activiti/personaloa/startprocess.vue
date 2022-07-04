<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.processDefinitionKey" placeholder="流程定义编号" clearable class="filter-input filter-item" @keyup.enter.native="handleFilter" />
      <el-input v-model="listQuery.processDefinitionName" placeholder="流程定义名称" clearable class="filter-input filter-item" @keyup.enter.native="handleFilter" />
      <el-input-number v-model="listQuery.processDefinitionVersion" placeholder="流程定义版本" clearable class="filter-input filter-item" @keyup.enter.native="handleFilter" />
      <el-select v-model="listQuery.suspended" placeholder="是否挂起" clearable class="filter-input filter-item">
        <el-option label="是" value="1" />
        <el-option label="否" value="0" />
      </el-select>
      <div style="float: right">
        <el-button  type="primary" class="filter-item" icon="el-icon-search" @click="handleFilter">
          查询
        </el-button>
        <el-button  type="primary" class="filter-item" icon="el-icon-refresh-right" @click="handleReset">
          重置
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
      <el-table-column label="流程定义编号" prop="key" />
      <el-table-column label="流程定义名称" prop="name" />
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
          <el-button type="text" @click="handleStart(row)">
            <i class="el-icon-s-promotion" />动态发起
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize" @pagination="getList" />


    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="temp" :label-position="labelPosition" label-width="90px" style="width: 100%">
        <!--<el-form-item :label="$t('process.candidate')" prop="candidate">-->
          <!--<el-select v-model="temp.candidate" :placeholder="$t('common.choose')" clearable style="width: 100%">-->
            <!--<el-option-group v-for="group in candidateOptions" :key="group.value" :label="group.value">-->
              <!--<el-option v-for="item in group.children" :key="item.key" :label="item.value" :value="item.key" />-->
            <!--</el-option-group>-->
          <!--</el-select>-->
        <!--</el-form-item>-->
        <el-form-item label="审批人员" prop="candidate">
          <el-input v-model="temp.candidate" type="text" placeholder="请输入"  clearable />
        </el-form-item>
        <el-form-item label="起止时间" prop="rangeTime">
          <el-date-picker v-model="temp.rangeTime" type="datetimerange" value-format="timestamp" :default-time="['08:30:00', '18:00:00']" style="width: 100%" clearable />
        </el-form-item>
        <el-form-item label="备注" prop="description">
          <el-input v-model="temp.description" type="textarea" placeholder="请输入" maxlength="512" clearable />
        </el-form-item>
        <!--<parser ref="parserForm" :key="parserKey" :form-conf="formConf" />-->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="startData()">
          确定
        </el-button>
      </div>
    </el-dialog>

    <el-dialog  fullscreen :visible.sync="previewDialogVisible">
      <div v-html="svgSrc" />
    </el-dialog>


  </div>
</template>

<script>
import { queryProcessDefinition, queryUsername, getProcessResource, startProcessInstance } from '@/api/activiti/activiti'
// import Parser from '@/components/Parser'

export default {
  name: 'StartProcess',
  // components: { Pagination, 'form-generator': FormGenerator, Parser },
  data() {
    return {
      tableKey: 0,
      labelPosition: 'right',
      multipleSelection: [],
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        processDefinitionKey: undefined,
        processDefinitionName: undefined,
        processDefinitionVersion: undefined,
        suspended: undefined
      },
      candidateOptions: [],
      temp: {
        id: undefined
      },
      dialogFormVisible: false,
      previewDialogVisible: false,
      generateDialogVisible: false,
      parserKey: +new Date(),
      formConf: {},
      generator: 'generator',
      svgSrc: '',
      dialogStatus: '',
      textMap: {
        start: '动态发起流程'
      },
      rules: {
        candidate: [{ required: true, validator: this.checkCandidate, trigger: 'change' }],
        rangeTime: [{ required: true, validator: this.checkRangeTime, trigger: 'change' }],
        description: [{ required: true, validator: this.checkDescription, trigger: 'blur' }]
      },
      downloadLoading: false
    }
  },
  created() {
    if (window.innerWidth < 700) {
      this.labelPosition = 'top'
    }
    this.getList()
  },
  methods: {
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
      this.listQuery.currentPage = 1
      this.getList()
    },
    handleReset() {
      this.listQuery.processDefinitionKey = undefined
      this.listQuery.processDefinitionName = undefined
      this.listQuery.processDefinitionVersion = undefined
      this.listQuery.suspended = undefined
      this.handleFilter()
    },
    handleStart(row) {
      this.temp = Object.assign({}, row) // copy obj
      this.dialogStatus = 'start'
      this.parserKey = +new Date()
      this.formConf = {}
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
      // queryUsername().then(response => {
      //   this.candidateOptions = response.data
      // })
    },
    handlePreview(row) {
      this.previewDialogVisible = true
      getProcessResource(row.deploymentId).then(response => {
        this.svgSrc = response
      })
    },
    handleGenerate() {
      this.generateDialogVisible = true
    },
    handleResetGenerator() {
      this.parserKey = +new Date()
      this.formConf = {}
    },
    formDataMsg(e) {
      this.parserKey = +new Date()
      this.formConf = JSON.parse(e)
      this.formConf.formBtns = false
      this.generateDialogVisible = false
    },
    startData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          // const tempData = Object.assign({}, this.temp)
          const tempData = {}
          tempData.candidate = this.temp.candidate
          tempData.startTime = this.temp.rangeTime ? this.temp.rangeTime[0] : null
          tempData.endTime = this.temp.rangeTime ? this.temp.rangeTime[1] : null
          tempData.description = this.temp.description
          tempData.customForm =  []
          tempData.processDefinitionId = this.temp.id
          startProcessInstance(tempData).then(response => {
            if (response.msg === '操作成功') {
              this.dialogFormVisible = false
              this.$message({
                message: '路程发起成功',
                type: 'success',
                duration: 2000
              })
            } else {
              this.handleWarning(response)
            }
          })
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
    checkCandidate(rule, value, callback) {
      if (!value) {
        return callback(new Error('process.processCandidate-required'))
      } else {
        callback()
      }
    },
    checkRangeTime(rule, value, callback) {
      if (!value) {
        return callback(new Error('process.processRangetime-required'))
      } else {
        callback()
      }
    },
    checkDescription(rule, value, callback) {
      if (!value) {
        return callback(new Error('process.processDescription-required'))
      } else {
        callback()
      }
    }
  }
}
</script>
