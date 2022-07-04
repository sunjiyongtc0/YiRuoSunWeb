<template>
  <div class="flow-container">
    <div ref="content" class="containers">
      <div ref="canvas" class="canvas" />
      <div id="js-properties-panel" class="panel" />
      <ul class="buttons">
        <li>
          <input ref="file" style="display: none" type="file" @change="handleOpenFile">
          <el-button type="primary" icon="el-icon-upload" size="mini" @click="handleOpen">
            打开BPMN文件
          </el-button>
        </li>
        <li>
          <a ref="saveDiagram" href="javascript:" style="padding: 5px"><i class="el-icon-download" />下载BPMN文件</a>
        </li>
        <li>
          <a ref="saveSvg" href="javascript:" style="padding: 5px"><i class="el-icon-download" />下载SVG图片</a>
        </li>
        <li>
          <el-button type="primary" icon="el-icon-plus" size="mini" @click="persistProcess">
            保存
          </el-button>
        </li>
        <li>
          <el-button icon="el-icon-arrow-left" class="pan-back-btn" size="mini" @click="back">
            返回
          </el-button>
        </li>
      </ul>
      <!--<el-dialog  :title="'sysuser.view-user'" :visible.sync="viewUserDialogFormVisible" width="68%">-->
        <!--<sysuser-component :msg="singleMulti" @hideSysUserComponent="hideSysUserComponent" @sysUserComponentMsg="sysUserComponentMsg" />-->
      <!--</el-dialog>-->
      <!--<el-dialog  :title="'sysorg.view-org'" :visible.sync="viewOrgDialogFormVisible" width="68%">-->
        <!--<sysorg-component :msg="singleMulti" @hideSysOrgComponent="hideSysOrgComponent" @sysOrgComponentMsg="sysOrgComponentMsg" />-->
      <!--</el-dialog>-->
      <!--<el-dialog  :title="'sysrole.view-role'" :visible.sync="viewRoleDialogFormVisible" width="68%">-->
        <!--<sysrole-component :msg="singleMulti" @hideSysRoleComponent="hideSysRoleComponent" @sysRoleComponentMsg="sysRoleComponentMsg" />-->
      <!--</el-dialog>-->
      <!--<el-dialog  :title="'syspost.view-post'" :visible.sync="viewPostDialogFormVisible" width="68%">-->
        <!--<syspost-component :msg="singleMulti" @hideSysPostComponent="hideSysPostComponent" @sysPostComponentMsg="sysPostComponentMsg" />-->
      <!--</el-dialog>-->
    </div>
  </div>
</template>

<script>
// 引入相关的依赖
import BpmnModeler from 'bpmn-js/lib/Modeler'
import propertiesPanelModule from './bpmn-js-properties-panel'
import propertiesProviderModule from './bpmn-js-properties-panel/lib/provider/camunda'
import camundaModdleDescriptor from 'camunda-bpmn-moddle/resources/camunda'
import translationCn from './customTranslate/customTranslate' // 汉化
import { getModelResource, persistProcess } from '@/api/activiti/activiti'
import qs from 'qs'
// import elDragDialog from '@/directive/el-drag-dialog' // base on element-ui
// import SysUserComponent from '@/components/SysUser'
// import SysOrgComponent from '@/components/SysOrg'
// import SysRoleComponent from '@/components/SysRole'
// import SysPostComponent from '@/components/SysPost'

export default {
  name: 'ProcessDesign',
  // components: { 'sysuser-component': SysUserComponent, 'sysorg-component': SysOrgComponent, 'sysrole-component': SysRoleComponent, 'syspost-component': SysPostComponent },
  // directives: { elDragDialog },
  data() {
    return {
      // bpmn建模器
      bpmnModeler: null,
      bpmnModelerEvent: null,
      container: null,
      canvas: null,
      xmlStr: null,
      flag: null,
      singleMulti: 'multi',
      viewUserDialogFormVisible: false,
      viewOrgDialogFormVisible: false,
      viewRoleDialogFormVisible: false,
      viewPostDialogFormVisible: false,
      processName: '',
      temp: {}
    }
  },
  activated() {
    this.createNewDiagram()
  },
  mounted() {

    // 获取到属性ref为“content”的dom节点
    this.container = this.$refs.content
    // 获取到属性ref为“canvas”的dom节点
    const canvas = this.$refs.canvas


    var translationCnModule = {
      translate: ['value', translationCn]
    }

    // 建模，官方文档这里讲的很详细
    this.bpmnModeler = new BpmnModeler({
      container: canvas,
      // 添加控制板
      propertiesPanel: {
        parent: '#js-properties-panel'
      },
      additionalModules: [
        translationCnModule,
        // 左边工具栏以及节点
        propertiesProviderModule,
        // 右边的工具栏
        propertiesPanelModule
      ],
      moddleExtensions: {
        camunda: camundaModdleDescriptor
      }
    })


    // 下载画图
    const _this = this
    // 获取a标签dom节点
    const downloadLink = this.$refs.saveDiagram
    const downloadSvgLink = this.$refs.saveSvg
    // 给图绑定事件，当图有发生改变就会触发这个事件
    this.bpmnModeler.on('commandStack.changed', function() {
      _this.saveSVG(function(err, svg) {
        console.log("saveSVG")
        _this.setEncoded(downloadSvgLink, 'diagram.svg', err ? null : svg)
      })
      _this.saveDiagram(function(err, xml) {
        console.log("saveDiagram")
        _this.setEncoded(downloadLink, 'diagram.bpmn', err ? null : xml)
      })
    })

     //监听节点属性变化
    this.bpmnModeler.on('element.click', (e) => {
      if (document.querySelector('#camunda-assignee')) {
        document.querySelector('#camunda-assignee').onclick = function() {
          console.log('assignee')
          _this.openUserDialog(_this, e, 'assignee')
        }
      }
      if (document.querySelector('#camunda-candidateUsers')) {
        document.querySelector('#camunda-candidateUsers').onclick = function() {
          _this.openUserDialog(_this, e, 'candidateUsers')
        }
      }
      if (document.querySelector('#camunda-roleCode')) {
        document.querySelector('#camunda-roleCode').onclick = function() {
          _this.openRoleDialog(_this, e)
        }
      }
      if (document.querySelector('#camunda-postCode')) {
        document.querySelector('#camunda-postCode').onclick = function() {
          _this.openPostDialog(_this, e)
        }
      }
      if (document.querySelector('#camunda-candidateGroups')) {
        document.querySelector('#camunda-candidateGroups').onclick = function() {
          _this.openOrgDialog(_this, e)
        }
      }
    })
    this.createNewDiagram();
  },
  methods: {
    createNewDiagram() {
      // 删除 bpmn logo
      const bpmnjsLogo = document.querySelector('.bjs-powered-by')
      while (bpmnjsLogo.firstChild) {
        bpmnjsLogo.removeChild(bpmnjsLogo.firstChild)
      }
      if (this.$route.query.modelId) {
        getModelResource(this.$route.query.modelId).then(response => {
          if (response.code === 200&&response.msg!='操作成功') {
            this.bpmnModeler.importXML(response.msg, function(err) {
              if (err) {
                console.error(err)
              }
            })
          } else {
            const bpmnXmlStr = `<?xml version="1.0" encoding="UTF-8"?>
            <bpmn2:definitions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd" id="sample-diagram" targetNamespace="http://bpmn.io/schema/bpmn">
              <bpmn2:process id="Process_1" isExecutable="true">
                <bpmn2:startEvent id="StartEvent_1"/>
              </bpmn2:process>
              <bpmndi:BPMNDiagram id="BPMNDiagram_1">
                <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Process_1">
                  <bpmndi:BPMNShape id="_BPMNShape_StartEvent_2" bpmnElement="StartEvent_1">
                    <dc:Bounds height="36.0" width="36.0" x="412.0" y="240.0"/>
                  </bpmndi:BPMNShape>
                </bpmndi:BPMNPlane>
              </bpmndi:BPMNDiagram>
            </bpmn2:definitions>`
            // 将字符串转换成图显示出来
            this.bpmnModeler.importXML(bpmnXmlStr, function(err) {
              if (err) {
                console.error(err)
              }
            })
          }
        })
      }
    },
    // 下载为SVG格式,done是个函数，调用的时候传入的
    saveSVG(done) {
      // 把传入的done再传给bpmn原型的saveSVG函数调用
      this.bpmnModeler.saveSVG(done)
    },
    // 下载为XML格式,done是个函数，调用的时候传入的
    saveDiagram(done) {
      // 把传入的done再传给bpmn原型的saveXML函数调用
      this.bpmnModeler.saveXML({ format: true }, function(err, xml) {
        done(err, xml)
      })
    },
    // 当图发生改变的时候会调用这个函数，这个data就是图的xml
    setEncoded(link, name, data) {
      // 把xml转换为URI，下载要用到的
      const encodedData = encodeURIComponent(data)
      // 获取到图的xml，保存就是把这个xml提交给后台
      this.xmlStr = data
      // 下载图的具体操作
      if (data) {
        link.className = 'active'
        link.href = 'data:application/bpmn20-xml;charset=UTF-8,' + encodedData
        link.download = name
      }
    },
    persistProcess() {
      let json_xml = ''
      let svg_xml = ''
      this.bpmnModeler.saveXML({ format: true }, (err, xml) => {
        if (err) {
          console.error(err)
        }
        json_xml = xml
      })
      this.bpmnModeler.saveSVG({ format: true }, (err, data) => {
        if (err) {
          console.error(err)
        }
        svg_xml = data
      })
      this.temp.modelId = this.$route.query.modelId
      this.temp.jsonXml = json_xml
      this.temp.svgXml = svg_xml
      //qs.stringify(this.temp)
      persistProcess(this.temp).then(response => {
        if (response.msg === '操作成功') {
          this.$message({
            message: '保存设计成功',
            type: 'success',
            duration: 2000
          })
        } else {
          this.handleWarning(response)
        }
      })
    },
    handleOpen() {
      this.$refs.file.click()
    },
    handleOpenFile(e) {
      const that = this
      const file = e.target.files[0]
      const reader = new FileReader()
      let data = ''
      reader.readAsText(file)
      reader.onload = function(event) {
        data = event.target.result
        that.renderDiagram(data, 'open')
      }
    },
    renderDiagram(xml) { // 渲染 XML 格式
      this.bpmnModeler.importXML(xml, err => {
        if (err) {
          this.handleWarning(err)
        }
      })
    },
    hideSysUserComponent(e) {
      if (e === 1) {
        this.viewUserDialogFormVisible = false
      }
    },
    sysUserComponentMsg(e) {
      const usernameArray = []
      for (const data of e) {
        usernameArray.push(data.username)
      }

      const modeling = this.bpmnModeler.get('modeling')
      const elementRegistry = this.bpmnModeler.get('elementRegistry')
      var shape = this.bpmnModelerEvent.element ? elementRegistry.get(this.bpmnModelerEvent.element.id) : this.bpmnModelerEvent.shape
      if (this.flag === 'assignee') {
        modeling.updateProperties(shape, {
          assignee: usernameArray.toString()
        })
      } else if (this.flag === 'candidateUsers') {
        modeling.updateProperties(shape, {
          candidateUsers: usernameArray.toString()
        })
      }
    },
    openUserDialog(_this, e, flag) {
      this.singleMulti = 'single'
      this.viewUserDialogFormVisible = true
      this.bpmnModelerEvent = e
      this.flag = flag
    },
    openRoleDialog(_this, e) {
      this.singleMulti = 'single'
      this.viewRoleDialogFormVisible = true
      this.bpmnModelerEvent = e
    },
    sysRoleComponentMsg(e) {
      const roleCode = []
      for (const data of e) {
        roleCode.push(data.roleCode)
      }
      const modeling = this.bpmnModeler.get('modeling')
      const elementRegistry = this.bpmnModeler.get('elementRegistry')
      var shape = this.bpmnModelerEvent.element ? elementRegistry.get(this.bpmnModelerEvent.element.id) : this.bpmnModelerEvent.shape
      modeling.updateProperties(shape, {
        roleCode: roleCode.toString()
      })
    },
    hideSysRoleComponent(e) {
      if (e === 1) {
        this.viewRoleDialogFormVisible = false
      }
    },
    openPostDialog(_this, e) {
      this.singleMulti = 'single'
      this.viewPostDialogFormVisible = true
      this.bpmnModelerEvent = e
    },
    sysPostComponentMsg(e) {
      const postCode = []
      for (const data of e) {
        postCode.push(data.postCode)
      }
      const modeling = this.bpmnModeler.get('modeling')
      const elementRegistry = this.bpmnModeler.get('elementRegistry')
      var shape = this.bpmnModelerEvent.element ? elementRegistry.get(this.bpmnModelerEvent.element.id) : this.bpmnModelerEvent.shape
      modeling.updateProperties(shape, {
        postCode: postCode.toString()
      })
    },
    hideSysPostComponent(e) {
      if (e === 1) {
        this.viewPostDialogFormVisible = false
      }
    },
    openOrgDialog(_this, e) {
      this.singleMulti = 'single'
      this.viewOrgDialogFormVisible = true
      this.bpmnModelerEvent = e
    },
    sysOrgComponentMsg(e) {
      const orgName = []
      const orgId = []
      for (const data of e) {
        orgName.push(data.orgName)
        orgId.push(data.id)
      }
      const modeling = this.bpmnModeler.get('modeling')
      const elementRegistry = this.bpmnModeler.get('elementRegistry')
      var shape = this.bpmnModelerEvent.element ? elementRegistry.get(this.bpmnModelerEvent.element.id) : this.bpmnModelerEvent.shape
      modeling.updateProperties(shape, {
        candidateGroups: orgName.toString()
      })
    },
    hideSysOrgComponent(e) {
      if (e === 1) {
        this.viewOrgDialogFormVisible = false
      }
    },
    handleWarning(response) {
      this.$message({
        message: response.message || response,
        type: 'warning',
        duration: 2000
      })
    },
    back() {
      if (this.$route.query.noGoBack) {
        this.$router.push({ path: '/activiti/model' })
      } else {
        this.$router.go(-1)
      }
    }
  }
}
</script>
<style lang="scss" scoped>
  /*左边工具栏以及编辑节点的样式*/
  @import '~bpmn-js/dist/assets/diagram-js.css';
  @import '~bpmn-js/dist/assets/bpmn-font/css/bpmn.css';
  @import '~bpmn-js/dist/assets/bpmn-font/css/bpmn-codes.css';
  @import '~bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css';
  .flow-container {
    display: flex;
  }
  /*右边工具栏样式*/
  @import '~bpmn-js-properties-panel/dist/assets/bpmn-js-properties-panel.css';
  .containers {
    position: absolute;
    background-color: #ffffff;
    width: 100%;
    height: 100%;
    font-size: 14px;
  }
  .canvas {
    width: 100%;
    height: 100%;
  }
  .panel {
    position: fixed;
    right: 0;
    top: 83px;
    padding-bottom: 100px;
    width: 300px;
    height: 100%;
    overflow-y: scroll;
  }
  .buttons {
    position: fixed;
    left: 250px;
    bottom: 10px;
    &>li {
      display: inline-block;
      margin: 5px;
      &>a {
        color: #999;
        border-radius: 4px;
        background: #eee;
        cursor: not-allowed;
        padding: 10px;
        border: 1px solid #ccc;
        &.active {
          color: #333;
          background: #fff;
          cursor: pointer;
        }
      }
    }
  }
  .pan-back-btn {
    background: #008489;
    color: #fff;
  }
</style>
<style>
  .bpp-properties-group .group-header .group-label {
    font-size: 14px;
  }
</style>
