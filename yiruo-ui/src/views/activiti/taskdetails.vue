<template>
  <div class="app-container">
    <el-button icon="el-icon-arrow-left" class="pan-back-btn" @click="back">
      返回
    </el-button>
    <el-divider />
    <div style="margin:10px 0 5px 0px">
      任务详情
    </div>
    <el-row class="el-row-item">
      <el-col :span="12">流程实例ID：{{ this.$route.query && this.$route.query.processInstanceId }}</el-col>
      <el-col :span="12">流程定义名称：{{ this.$route.query && this.$route.query.processDefinitionName }}</el-col>
    </el-row>
    <el-row class="el-row-item">
      <el-col :span="12">当前任务：{{ this.$route.query && this.$route.query.name }}</el-col>
      <el-col :span="12">当前签收者：{{ this.$route.query && this.$route.query.assignee }}</el-col>
    </el-row>
    <el-row class="el-row-item">
      <el-col :span="12">当前签收时间：{{ this.$route.query && (parseTime(new RegExp(/^[0-9]*$/).test(this.$route.query.claimTime) ? parseInt(this.$route.query.claimTime) : this.$route.query.claimTime) ) }}</el-col>
      <el-col :span="12">发起者：{{ this.$route.query && this.$route.query.startUserId }}</el-col>
    </el-row>
    <el-divider />
    <div style="margin:10px 0 5px 0px">
      审批信息
    </div>
    <el-table
      :key="tableKey"
      v-loading="listLoading"
      :data="list"
      border
      fit
      highlight-current-row
      style="width: 100%"
    >
      <el-table-column label="审批人员">
        <template slot-scope="{row}">
          <span>{{ row.userId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审批时间">
        <template slot-scope="{row}">
          <span>{{ parseTime(row.time)}}</span>
        </template>
      </el-table-column>
      <el-table-column label="批注">
        <template slot-scope="{row}">
          <span>{{ row.fullMessage }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { queryTaskComment } from '@/api/activiti/activiti'

export default {
  name: 'TaskDetails',
  data() {
    return {
      tableKey: 0,
      list: [],
      listLoading: true,
      listQuery: {}
    }
  },
  // activated() {
  mounted(){
    const processInstanceId = this.$route.query && this.$route.query.processInstanceId
    this.getList(processInstanceId)
  },
  methods: {
    getList(processInstanceId) {
      this.listLoading = true
      this.listQuery.processInstanceId = processInstanceId
      queryTaskComment(this.listQuery).then(response => {
        this.list = response.rows
        this.listLoading = false
      })
    },
    back() {
      if (this.$route.query.noGoBack) {
        this.$router.push({ path: '/home' })
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
  .el-row-item {
    color: #606266;
    margin: 10px 10px 10px 0px;
    font-size: 14px;
  }
</style>
