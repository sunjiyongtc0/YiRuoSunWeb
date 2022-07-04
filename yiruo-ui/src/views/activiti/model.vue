<template>
  <div class="app-container">
    <div class="filter-container">
          <el-input v-model="listQuery.name" placeholder="模型名称" clearable class="filter-input filter-item allQueryClass" style="width: 150px" @keyup.enter.native="handleFilter" />
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
            <el-button type="primary" class="filter-item" icon="el-icon-plus" @click="handleCreate">
              新增
            </el-button>
            <el-button type="primary" class="filter-item" icon="el-icon-copy-document" @click="handleCopy">
              复制
            </el-button>
            <el-button  type="primary" class="filter-item" icon="el-icon-share" @click="handleDeploy">
              部署
            </el-button>
            <el-button type="primary" class="filter-item" @click="handleProcessdesign">
              <svg-icon icon-class="drag" /> 流程设计
            </el-button>
            <el-button type="danger" plain class="filter-item" icon="el-icon-delete" @click="handleDelete">
              删除
            </el-button>
          </div>
          <div v-if="advancedSearch" style="float: left">
            <el-input v-model="listQuery.category" placeholder="流程类别" clearable class="filter-input filter-item" style="width: 150px" @keyup.enter.native="handleFilter" />
            <el-input-number v-model="listQuery.version" placeholder="流程版本" clearable class="filter-input filter-item" style="width: 150px" @keyup.enter.native="handleFilter" />
          </div>
    </div>

        <el-table
          :key="tableKey"
          v-loading="listLoading"
          :data="list"
          border
          fit
          highlight-current-row
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="40" />
          <el-table-column label="流程名称" prop="name" />
          <el-table-column label="流程类别" prop="category" />
          <el-table-column label="模型版本">
            <template slot-scope="{row}">
              <el-tag type="success">
                <span>{{ 'v' + row.version }}</span>
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="模型修订">
            <template slot-scope="{row}">
              <el-tag type="success">
                <span>{{ 'v' + row.revision }}</span>
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" min-width="120px">
            <template slot-scope="{row}">
              <span>{{ parseTime(row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template slot-scope="{row,$index}">
              <el-button type="text" @click="handleUpdate(row)">
                <i class="el-icon-edit-outline" />修改
              </el-button>
              <el-popover :ref="'popover-' + row.id" placement="top" width="160" title="是否要删除此行" trigger="click">
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

  <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="600px" append-to-body>
      <el-form ref="dataForm" :rules="rules" :model="temp" :label-position="labelPosition" label-width="110px" style="width: 100%">
        <el-form-item label="模型名称" prop="name">
          <el-input v-model="temp.name" placeholder="请输入" maxlength="256" clearable />
        </el-form-item>
        <el-form-item label="模型类别" prop="category">
          <el-input v-model="temp.category" placeholder="请输入" maxlength="256" clearable />
        </el-form-item>

        <el-form-item label="模型描述" prop="description">
          <el-input v-model="temp.description" type="textarea" placeholder="请输入" maxlength="512" clearable />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { queryModel, addModel, updateModel, copyModel, deployModel, deleteModel } from '@/api/activiti/activiti'
// import waves from '@/directive/waves' // waves directive
// import elDragDialog from '@/directive/el-drag-dialog' // base on element-ui
// import Pagination from '@/components/Pagination' // secondary package based on el-pagination
// import SysOrgComponent from '@/components/SysOrg'
// import treeTransfer from 'el-tree-transfer'
import Treeselect from "@riophae/vue-treeselect";

export default {
  name: 'Model',
  // components: { Pagination, treeTransfer, 'sysorg-component': SysOrgComponent },
  // directives: { waves, elDragDialog },
  data() {
    return {
      leftIcon: true,
      rightIcon: false,
      tableKey: 0,
      labelPosition: 'right',
      list: [],
      allOrgList: this.$store.getters.orgs,
      total: 0,
      listLoading: true,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        category: undefined,
        version: undefined
      },
      temp: {
        id: undefined,
        referGroupId: undefined
      },
      multipleSelection: [],
      sysOrgOptions: [],
      userId: [],
      usernames: [],
      orgUserTree: [],
      assignOrgUserTree: [],
      toData: [],
      toUserData: [],
      parentName: undefined,
      referGroupNames: undefined,
      viewOrgDialogFormVisible: false,
      publishObjectDialogVisible: false,
      advancedSearch: false,
      singleMulti: 'multi',
      relateOrgId: undefined,
      relateOrgName: undefined,
      dialogFormVisible: false,
      dialogStatus: undefined,
      textMap: {
        update: '修改',
        create: '新增'
      },
      rules: {
        name: [{ required: true, validator: this.checkName, trigger: 'blur' }],
        category: [{ required: true, validator: this.checkCategory, trigger: 'blur' }]
      }
    }
  },
  watch: {

  },
  created() {
    if (window.innerWidth < 700) {
      this.labelPosition = 'top'
    }
    this.getInitList()
  },
  methods: {
    clickSwitch() {
      this.advancedSearch = !this.advancedSearch
    },
    getInitList() {
      this.listLoading = true
      queryModel(this.listQuery).then(response => {
        this.list = response.rows
        this.total = response.total
        this.listLoading = false
      })
    },
    getList() {
      this.listLoading = true
      queryModel(this.listQuery).then(response => {
        this.list = response.rows
        this.total = response.total
        this.listLoading = false
      })
    },
    clickAsideData(row, event, column) {
      this.relateOrgId = row.id
      this.relateOrgName = row.orgName

      this.listQuery.orgId = row.id
      this.listLoading = true
      queryModel(this.listQuery).then(response => {
        this.list = response.rows
        this.total = response.total
        this.listLoading = false
      })
    },
    handleFilter() {
      this.listQuery.pageNum = 1
      this.listLoading = true
      queryModel(this.listQuery).then(response => {
        this.list = response.rows
        this.total = response.total
        this.listLoading = false
      })
    },
    handleReset() {
      this.listQuery.name = undefined
      this.listQuery.category = undefined
      this.listQuery.version = undefined
      this.handleFilter()
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    resetTemp() {
      this.temp = {
        id: undefined
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.parentName = ''
      this.temp.referGroupId = ''
      this.referGroupNames = ''
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
        this.userId = []
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          addModel(this.temp).then(response => {
            if (response.msg === '操作成功') {
              this.list.unshift(this.temp)
              this.dialogFormVisible = false
              this.$message({
                message: '新增成功',
                type: 'success',
                duration: 2000
              })
              this.handleReset()
            } else {
              this.handleWarning(response)
            }
          })
        }
      })
    },
    handleValidate() {
      if (this.multipleSelection.length === 0) {
        this.handleWarning('请选择数据')
        return false
      }
      if (this.multipleSelection.length > 1) {
        this.handleWarning('所选数据不能超过一条')
        return false
      }
      return true
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row) // copy obj
      this.$set(this.temp, 'description', (JSON.parse(row.metaInfo))['description'])
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = {}
          tempData.modelId = this.temp.id
          tempData.name = this.temp.name
          tempData.description = this.temp.description
          tempData.category = this.temp.category
          updateModel(tempData).then(response => {
            if (response.msg === '操作成功') {
              this.dialogFormVisible = false
              this.$message({
                message: '修改成功',
                type: 'success',
                duration: 2000
              })
              this.handleReset()
            } else {
              this.handleWarning(response)
            }
          })
        }
      })
    },
    handleCopy() {
      if (!this.handleValidate()) {
        return
      }
      copyModel(this.multipleSelection[0].id).then(response => {
        if (response.msg === '操作成功') {
          this.$message({
            message: '复制成功',
            type: 'success',
            duration: 2000
          })
          this.handleReset()
        } else {
          this.handleWarning(response)
        }
      })
    },
    handleDeploy() {
      if (!this.handleValidate()) {
        return
      }
      deployModel(this.multipleSelection[0].id).then(response => {
        if (response.msg === '操作成功') {
          this.$message({
            message: '模型部署成功',
            type: 'success',
            duration: 2000
          })
          this.handleReset()
        } else {
          this.handleWarning(response)
        }
      })
    },
    handleProcessdesign() {
      if (!this.handleValidate()) {
        return
      }
      this.$router.push({ path: '/processdesign', query: { modelId: this.multipleSelection[0].id, deploymentId: this.multipleSelection[0].deploymentId }})
    },
    handleDelete(row, index) {
      if (row.id) {
        this.delete(row.id)
      } else {
        if (this.multipleSelection.length === 0) {
          this.handleWarning('至少选中一条数据')
          return
        }
        this.$confirm('确定删除数据', '删除数据', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.delete(this.multipleSelection.map(item => { return item.id }).join(','))
        }).catch(() => {})
      }
    },
    delete(deleteArray) {
      deleteModel(deleteArray).then(response => {
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
    createUser(toData) {
      const res = []
      const username = []
      toData.forEach(data => {
        const tmp = { ...data }
        if (tmp.children) {
          tmp.children = this.createUser(tmp.children)
        }
        if (tmp.label.split('-').length > 1) {
          res.push(tmp.id)
          username.push(tmp.label)
        }
      })
      if (res.length > 0) {
        this.toUserData.push(res)
        this.usernames.push(username)
      }
    },
    handleWarning(response) {
      this.$message({
        message: response.message || response,
        type: 'warning',
        duration: 2000
      })
    },
    checkName(rule, value, callback) {
      if (!value) {
        return callback(new Error('model.modelName-required'))
      } else {
        callback()
      }
    },
    checkCategory(rule, value, callback) {
      if (!value) {
        return callback(new Error('model.modelCategory-required'))
      } else {
        callback()
      }
    }
  }
}
</script>
<style>
  .aside-table td,.aside-table th.is-leaf {
    border: none;
  }
  .aside-table::before {
    width: 0;
  }
  .allQueryClass input.el-input__inner {
    padding: 10px;
  }
</style>
<style scoped>
  .el-table .cell {
    white-space: nowrap;
    text-overflow: unset;
    margin-right: 10px;
  }
  ::-webkit-scrollbar {
    width: 8px;
    height: 8px;
  }
  ::-webkit-scrollbar-thumb {
    background-color: #C0D4F0;
    border-radius: 3px;
  }
</style>
