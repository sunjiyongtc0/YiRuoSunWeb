<template>
  <div class="app-container">
    <el-button icon="el-icon-arrow-left" class="pan-back-btn" @click="back">
      返回
    </el-button>
    <el-divider />
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
      <el-table-column label="流程实例ID" prop="processInstanceId" min-width="120px" />
      <el-table-column label="节点编号" prop="activityId" />
      <el-table-column label="执行名称" prop="name" />
      <el-table-column label="是否挂起">
        <template slot-scope="{row}">
          <span v-if="row.suspended === false">否</span>
          <span v-else>是</span>
        </template>
      </el-table-column>
      <el-table-column label="是否结束">
        <template slot-scope="{row}">
          <span v-if="row.ended === false">否</span>
          <span v-else>是</span>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { queryExecution } from '@/api/activiti/activiti'

export default {
  name: 'Execution',
  data() {
    return {
      tableKey: 0,
      list: [],
      multipleSelection: [],
      total: 0,
      listLoading: true,
      listQuery: {
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  // activated() {
  mounted(){
    const processInstanceId = this.$route.query && this.$route.query.processInstanceId
    this.listQuery.processInstanceId = processInstanceId
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      queryExecution(this.listQuery).then(response => {
        this.list = response.rows
        this.total = response.total
        this.listLoading = false
      })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    back() {
      if (this.$route.query.noGoBack) {
        if (this.$route.query.flag === 'ProcessInstance') {
          this.$router.push({ path: '/activiti/processinstance' })
        } else if (this.$route.query.flag === 'Task') {
          this.$router.push({ path: '/activiti/task' })
        }
      } else {
        this.$router.go(-1)
      }
    }
  }
}
</script>
<style lang="scss" scoped>
  .pan-back-btn {
    background: #008489;
    color: #fff;
    border: none!important;
  }
</style>
