<template>
  <div class="pagebj" ref="page">
    <div class="container clearfix">
    <!--底部图表-->
    <div class="charts">
      <div class="alarm">
        <!--告警信息-->
        <div class="letf_bom">
          <!--<div class="left_title">告警信息</div>-->
          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="alarm_table">
            <thead>
            <tr >
              <th width="8%">&nbsp;</th>
              <th width="18%">告警时间</th>
              <th width="22%">告警类型</th>
              <th>告警描述</th>
              <th width="17%">告警程度</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for=" (item,i) in alertTable">
              <td align="center">{{i+1}}</td>
              <td>{{item.time}}</td>
              <td>{{item.type}}</td>
              <td>{{item.desc}}</td>
              <td><img src='./images/star.png' v-for="i in item.level"></td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div class="column" ref="column">
        <!--<img src="./images/item/column.png">-->
      </div>
    </div>
      <div class="charts2">
        <div class="diagram" ref="diagram">
          <!--<img src="./images/item/diagram.png" width="100%">-->
        </div>
      </div>
  </div>
  </div>
</template>

<script>
  import echarts from 'echarts'

  export default {
    name: 'Data',
    data() {
      return {
        alertTable:[{time:'15:55:05',type:'链路告警',desc:'北京-山东-时延200ms',level:3},{time:'15:55:05',type:'DNS告警',desc:'北京-山东-时延200ms',level:3},
          {time:'15:55:05',type:'重点网站告警',desc:'北京-山东-时延200ms',level:2},{time:'15:55:05',type:'链路告警',desc:'北京-山东-时延200ms',level:3},
          {time:'15:55:05',type:'链路告警',desc:'北京-山东-时延200ms',level:3},{time:'15:55:05',type:'DNS告警',desc:'北京-山东-时延200ms',level:3},
          {time:'15:55:05',type:'重点网站告警',desc:'北京-山东-时延200ms',level:2},{time:'15:55:05',type:'链路告警',desc:'北京-山东-时延200ms',level:3},
          {time:'15:55:05',type:'链路告警',desc:'北京-山东-时延200ms',level:3},{time:'15:55:05',type:'重点网站告警',desc:'北京-山东-时延200ms',level:2},{time:'15:55:05',type:'链路告警',desc:'北京-山东-时延200ms',level:3},
          {time:'15:55:05',type:'链路告警',desc:'北京-山东-时延200ms',level:3}],
        xdata:['百度','淘宝','爱奇艺','京东','支付宝','抖音','央视网'],
        avgspdata:[58,84,68,79,85,58,33],
        rttdata:  [24,25,36,12,31,53,49],
        xData:['01:00','02:00','03:00','04:00','05:00','06:00','07:00','08:00','09:00','10:00','11:00','12:00','13:00','14:00','15:00','16:00','17:00','18:00'],
        ydata:[[1.3,2.7,3.1,2.4,3.5,3.1,2.2,1.3,0.4,4.5,0.4,4.5,1.3,2.7,3.1,2.4,3.5,3.1],[3.1,2.2,1.3,0.4,4.5,1.3,2.7,3.1,2.4,3.5,3.1,2.2,3.1,2.2,1.3,0.4,4.5,0.4]],


    }
    },
    mounted(){
      this.init();
    },
    methods: {
      init() {
        this.columnInit();
        this.diagramInit();
      },
      diagramInit(){
        let rttData=this.ydata[0];
        let rateData=this.ydata[1];
        let lineChart = echarts.init(this.$refs.diagram);
        let option = {
          title: {
            text: '时延丢包变化趋势',//主标题文本，'\n'指定换行
            textStyle:{
              fontSize:12,
              fontFamily:'微软雅黑',
              color: '#90B4E4'
            },
            subtextStyle: {
              fontSize: 0.1,
            }
          },
          tooltip : {
            trigger: 'axis',//鼠标提示格式对比型
          },
          legend: {
            x:'right',
            textStyle: {
              color: '#ccc'
            },
            data:['时延','丢包']
          },
          xAxis :{
            name: '小时',
            type : 'category',
            splitLine: {
              show: false,//影藏横向网格线
            },
            axisLine:{
              lineStyle:{
                color:'#ccc'
              }
            },
            axisLabel:{
              textStyle:{
                color:'#ccc'
              }
            },
            data : this.xData
          },
          yAxis :[{
            type : 'value',
            axisLabel:{
              textStyle:{
                color:'#ccc'
              }
            },
            axisLine:{
              lineStyle:{
                color:'#ccc'
              }
            },
            splitLine: {
              show: true,//影藏横向网格线
              lineStyle:{
                type:'dashed',
                color:'rgb(128, 255, 165,0.2)'
              }
            }
          },{
            type : 'value',
            axisLabel:{
              textStyle:{
                color:'#ccc'
              }
            },
            axisLine:{
              lineStyle:{
                color:'#ccc'
              }
            },
            splitLine: {
              show: true,//影藏横向网格线
              lineStyle:{
                type:'dashed',
                color:'rgb(128, 255, 165,0.2)'
              }
            }
          }
          ],
          grid: {x:50,y:45, y2:18, x2:'9%',
            show: true,
            borderWidth:0,
            backgroundColor:'rgba(1,13,60,0.4)',
            containLabel: true
          },//曲线轴现在上下间隔宽度
          series : [
            {
              name:'时延',
              type:'line',
              smooth: true,
              lineStyle: {
                width: 2,
                color:'#5adc0b'
              },
              showSymbol: false,
              areaStyle: {
                opacity: 0.8,
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {
                    offset: 0,
                    color: 'rgb(128, 255, 165,0.4)'
                  },
                  {
                    offset: 1,
                    color: 'rgb(1, 191, 236,0)'
                  }
                ])
              },
              emphasis: {
                focus: 'series'
              },
              data:rttData
            },
            {
              name:'丢包',
              type:'line',
              stack: 'Total',
              smooth: true,
              lineStyle: {
                width: 2,
                color:'#90B4E4'
              },
              showSymbol: false,
              yAxisIndex: 1,
              areaStyle: {
                opacity: 0.8,
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {
                    offset: 0,
                    color: 'rgb(55, 162, 255,0.4)'
                  },
                  {
                    offset: 1,
                    color: 'rgb(116, 21, 219,0)'
                  }
                ])
              },
              emphasis: {
                focus: 'series'
              },
              data:rateData
            }
          ]
        };
        //配置图表实例任何可配置选项
        lineChart.setOption(option);
      },
      columnInit(){
        let columnChart = echarts.init(this.$refs.column);
        let option={
          title:{
            show:true,
            text:'重点网站',
            textStyle:{
              fontSize:12,
              fontFamily:'微软雅黑',
              color: '#90B4E4'
            }
          },
          color:['#7fd0ff'],
          grid:{
            x:'5%',y:'15%',x2:'8%',y2:'10%',
            show: true,
            borderWidth:0,
            backgroundColor:'rgba(1,13,60,0.4)',
          },
          legend: {
            x:'right',
            textStyle: {
              color: '#ccc'
            },
            data:['下载速度','响应时延']
          },
          xAxis : [ {
            name:'网站',
            type : 'category',
            axisLine:{
              lineStyle:{
                color:'#ccc'
              }
            },
            axisLabel:{
              textStyle:{
                color:'#ccc'
              }
            },
            data : this.xdata
          } ],
          yAxis :[{
            type : 'value',
            axisLabel:{
              textStyle:{
                color:'#ccc'
              }
            },
            axisLine:{
              lineStyle:{
                color:'#ccc'
              }
            },
            splitLine: {
              show: true,//影藏横向网格线
              lineStyle:{
                type:'dashed',
                color:'rgb(128, 255, 165,0.2)'
              }
            }
          },{
            type : 'value',
            axisLabel:{
              textStyle:{
                color:'#ccc'
              }
            },
            axisLine:{
              lineStyle:{
                color:'#ccc'
              }
            },
            splitLine: {
              show: false,//影藏横向网格线
              lineStyle:{
                type:'dashed',
                color:'rgb(128, 255, 165,0.2)'
              }
            }
          }
          ],
          series:[{
            name : '下载速度',
            type: 'bar',
            barWidth:10,
            itemStyle:{
              normal: {
                borderWidth:1,
                borderColor:'#18CEE2',
                barBorderRadius: 28,
                color: new echarts.graphic.LinearGradient(
                  0, 0, 1, 0,
                  [
                    {offset: 0, color: '#2dc3db'},
                    {offset: 1, color: '#0f88c0'}
                  ]
                )
              },
              emphasis: {
                barBorderRadius: 13,
                shadowBlur: 18,
                shadowColor: 'rgba(218,170, 58, 0.7)'
              }
            },
            data:this.avgspdata

          },
            {
              name : '响应时延',
              type: 'bar',
              yAxisIndex: 1,
              barWidth:10,
              itemStyle:{
                normal: {
                  borderWidth:1,
                  borderColor:'#18CEE2',
                  barBorderRadius: 28,
                  color: new echarts.graphic.LinearGradient(
                    0, 0, 1, 0,
                    [
                      {offset: 0, color: '#2dc3db'},
                      {offset: 1, color: '#0f88c0'}
                    ]
                  )
                },
                emphasis: {
                  barBorderRadius: 13,
                  shadowBlur: 18,
                  shadowColor: 'rgba(218,170, 58, 0.7)'
                }
              },
              data: this.rttdata

            }]

        };
        columnChart.setOption(option);// 为echarts对象加载数据
      }
    }
  }

</script>



<style lang="scss" scoped>
  *{ margin:0px; padding:0px; list-style:none;}
  div,table,tr,td,p,span,a,ul,li,dl,dd,dt,ol,h1,h2,h3,h4,h5,h6,input{ margin:0px; padding:0px;}
  html{ font-size:20px;}
  @media screen and (max-width:4000px){html{font-size:45px;}}
  @media screen and (max-width:3000px){html{font-size:30px;}}
  @media screen and (max-width:2500px){html{font-size:24px;}}
  @media screen and (max-width:1600px){html{font-size:20px;}}
  body{ margin:0px;
    padding:0px;
    list-style:none;
    font:normal 0.6rem/1rem 'Microsoft Yahei',arial, Helvetica, sans-serif,sans-serif;
    color:#fff;
  }/*-- Bug--*/
  .clearfix:after {content:""; display:block; height:0px; clear:both; overflow:hidden;}
  .clearfix {display:inline-block;}/*ie7*/
  .clearfix {display:block;}/*ie5,ie6*/
  .clearfix {zoom:1;}
  a:link {}
  a:visited {}
  a:hover {}
  a:active {}
  .bornone{ border:none;}
  /*--边距--*/
  .h5{ width:100%;
    height:0.25rem;
    overflow:hidden;
  }
  .h10{ width:100%;
    height:0.5rem;
    overflow:hidden;
  }
  .h30{ width:100%;
    height:1.5rem;
    overflow:hidden;
    clear:both;
  }
  .w100{ width:100%; }
  .w93{ width:93.6%; float: left; margin-left: 0.6%;}
  /*浮动*/
  .floleft{float:left;}
  .floright{float:right;}
  /*page*/
  .pagebj{background-image:url(./images/bj.png);
    background-repeat: no-repeat;
    background-size: 100% 100%;
    width:100%;
    min-width:648px;
    min-height:660px;
    position:relative; left:0; top:0; overflow: hidden;
    padding: 0;
    margin: 0;
  }

  .container,.charts, .charts2{ width: 100%;  position:absolute; left: 1%;}
  .container{  top: 2.5rem;}
  .charts{ height: 300px;top:10px }
  .charts2{ height: 300px;top:310px }
  .alarm,.column,.diagram{ float: left; }
  .alarm,.column{ margin-right: 1%;}
  .alarm{width: 49%;}
  .column{ width: 49%;height: 300px;}
  .diagram{ width: 99%;height: 300px;}
  /*告警信息*/
  .left_title{width:100%; color: #a7cdff; line-height: 0.8rem;}
  .alarm_table{ color:#a7cdff; width:100%;}
  .alarm_table thead{ background:url(./images/sub_titl.png) no-repeat bottom; background-size:100% 100%;
    height:1.45rem; line-height:1.35rem; text-align:left; font-weight:normal;
  }
  .alarm_table tr td{ height:1.2rem; line-height:1.2rem; font-size:0.6rem; background-color: rgba(0,0,0,0.4);}
  .average{  width:56%;  position: absolute; left: 0; line-height: 1rem;}
  .cor_green{ color: #68f085;}
  .cor_org{ color: #ffd189;}
  .cor_blue{ color: #48c1ff;}
  .cor_normal{color:#a7cdff; font-size:0.6rem;  padding-left: 4px;}


</style>
