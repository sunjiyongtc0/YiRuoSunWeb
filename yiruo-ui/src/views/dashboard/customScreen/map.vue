<template>
  <div class="pagebj" ref="page">
    <div class="container clearfix">
      <!--左侧中国地图-->
      <div class="map_con">
        <div class="average">
          <div class="h10"></div>
          <table idth="100%" border="0" cellspacing="0" cellpadding="0" class="menu_val" >
            <tbody>
            <tr>
              <td width="23%">联合全国平均值</td>
              <td width="22%">实时数据总量</td>
              <td colspan="3">工信部平均值</td>
            </tr>
            <tr>
              <td class="cor_green">{{menuData.avg}}</td>
              <td class="cor_org">{{menuData.count}}</td>
              <td class="cor_blue">{{menuData.lt}}%<small class="cor_normal">联通</small></td>
              <td class="cor_blue">{{menuData.yd}}%<small class="cor_normal">移动</small></td>
              <td class="cor_blue">{{menuData.dx}}%<small class="cor_normal">电信</small></td>
            </tr>
            </tbody>
          </table>
        </div>
        <div class="china_map" ref="mapResize">
          <div :class="className" ref="chinaMap"  :style="{height:height,width:width}"  />
        </div>
        <div class="legend">
          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="legend_con">
            <tbody>
            <tr>
              <td width="10%">&nbsp;</td>
              <td>图例</td>
              <td>时延(ms)</td>
            </tr>
            <tr>
              <td>&nbsp;</td>
              <td><font color="#89e58d">&#9679;</font>&nbsp;优</td>
              <td>&gt;600</td>
            </tr>
            <tr>
              <td>&nbsp;</td>
              <td><font color="#fdb072">&#9679;</font>&nbsp;中</td>
              <td>300-600</td>
            </tr>
            <tr>
              <td>&nbsp;</td>
              <td><font color="#ff5337">&#9679;</font>&nbsp;一般</td>
              <td>&lt;300</td>
            </tr>
            </tbody>
          </table>
        </div>
        <div class="date">
          <!--时间-->
          <div class="date_new2">
            <div class="more_open2 clearfix" ref="divTimeAxis" id ="divTimeAxis" :style="len>0?'':'display:none'">
              <ul>
                <li v-for="item in signList" :class="item.class" @click="timeClickEvent(item.sign,item.val)" :data-time="item.dataTime">
                  {{item.val}}
                </li>
              </ul>
            </div>
            <div id="date_timing" class="date_timing2">
              <span @click="initTimeAxis('hours')" ref="hoursDiv" class="hours2">{{hoursDiv}}</span>
              <span @click="initTimeAxis('hours')" class="hours_more2"></span>
              <span @click="initTimeAxis('minutes')" ref="minutesDiv" class="hours2">{{minutesDiv}}</span>
              <span @click="initTimeAxis('minutes')" class="hours_more3"></span>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
  import echarts from 'echarts'
  require('echarts/map/js/china') // echarts theme
  export default {
    name: 'Map',
    props: {
      className: {
        type: String,
        default: 'chart'
      },
    },
    data() {
      return {
        mapInitData:{"doing":"地图数据","state":1,"message":"操作成功","object":{"data":[{"rtt":"98.56","destCode":"450000","srcCode":"110000","lost_rate":"4.95"},{"rtt":"19.28","destCode":"460000","srcCode":"110000","lost_rate":"2.79"},{"rtt":"30.20","destCode":"370000","srcCode":"110000","lost_rate":"3.04"},{"rtt":"23.67","destCode":"530000","srcCode":"110000","lost_rate":"2.43"},{"rtt":"74.31","destCode":"500000","srcCode":"110000","lost_rate":"7.48"},{"rtt":"54.23","destCode":"130000","srcCode":"110000","lost_rate":"2.57"},{"rtt":"92.20","destCode":"520000","srcCode":"110000","lost_rate":"5.53"},{"rtt":"71.00","destCode":"220000","srcCode":"110000","lost_rate":"7.82"},{"rtt":"89.24","destCode":"230000","srcCode":"110000","lost_rate":"3.21"},{"rtt":"28.39","destCode":"120000","srcCode":"110000","lost_rate":"7.34"},{"rtt":"71.75","destCode":"340000","srcCode":"110000","lost_rate":"7.75"},{"rtt":"42.06","destCode":"330000","srcCode":"110000","lost_rate":"3.44"},{"rtt":"20.44","destCode":"360000","srcCode":"110000","lost_rate":"8.43"},{"rtt":"7.21","destCode":"350000","srcCode":"110000","lost_rate":"2.99"},{"rtt":"99.56","destCode":"510000","srcCode":"110000","lost_rate":"9.88"},{"rtt":"21.33","destCode":"310000","srcCode":"110000","lost_rate":"1.13"},{"rtt":"14.46","destCode":"620000","srcCode":"110000","lost_rate":"3.78"},{"rtt":"21.49","destCode":"210000","srcCode":"110000","lost_rate":"1.80"},{"rtt":"75.90","destCode":"430000","srcCode":"110000","lost_rate":"4.90"},{"rtt":"6.58","destCode":"650000","srcCode":"110000","lost_rate":"7.15"},{"rtt":"29.13","destCode":"420000","srcCode":"110000","lost_rate":"5.61"},{"rtt":"78.09","destCode":"150000","srcCode":"110000","lost_rate":"8.50"},{"rtt":"58.08","destCode":"540000","srcCode":"110000","lost_rate":"8.07"},{"rtt":"99.97","destCode":"630000","srcCode":"110000","lost_rate":"4.55"},{"rtt":"80.66","destCode":"440000","srcCode":"110000","lost_rate":"7.30"},{"rtt":"47.08","destCode":"640000","srcCode":"110000","lost_rate":"9.03"},{"rtt":"9.55","destCode":"610000","srcCode":"110000","lost_rate":"9.16"},{"rtt":"85.21","destCode":"410000","srcCode":"110000","lost_rate":"8.53"},{"rtt":"65.27","destCode":"140000","srcCode":"110000","lost_rate":"6.62"},{"rtt":"0.90","destCode":"110000","srcCode":"110000","lost_rate":"3.25"},{"rtt":"92.49","destCode":"320000","srcCode":"110000","lost_rate":"4.49"}],"provinceJson":{"370000":"济南","620000":"兰州","320000":"南京","110000":"北京","530000":"昆明","460000":"海口","330000":"杭州","310000":"上海","120000":"天津","610000":"西安","650000":"乌鲁木齐","520000":"贵阳","340000":"合肥","430000":"长沙","130000":"石家庄","210000":"沈阳","510000":"成都","640000":"银川","220000":"长春","350000":"福州","420000":"武汉","440000":"广州","500000":"重庆","140000":"太原","360000":"南昌","230000":"哈尔滨","630000":"西宁","410000":"郑州","150000":"呼和浩特","540000":"拉萨","450000":"南宁"},"sysTime":1656480739727}},
        menuData:{},
        chart: null,
        geoCoord : {
          '北京': [116.4551,40.2539],//北京
          '上海': [121.4648,31.2891],//上海
          '天津': [117.4219,39.4189],//天津
          '重庆': [107.7539,30.1904],//重庆
          '哈尔滨': [127.9688,47.368],  //哈尔滨
          '长春': [126.8154,43.2584],//长春
          '沈阳': [123.1238,41.1216],//沈阳
          '呼和浩特': [111.4124,41.4901],//呼和浩特
          '石家庄': [115.4995,38.1006],//石家庄
          '乌鲁木齐': [86.9236,40.5883],//乌鲁木齐
          '兰州': [103.5901,36.3043],//兰州
          '西宁': [97.4038,35.8207],//西宁
          '西安': [109.1162,34.2004],//西安
          '银川': [106.3586,37.5775],//银川
          '郑州': [113.4668,33.6234],//郑州
          '济南': [118.1582,36.3701],//济南
          '太原': [112.3352,37.9413],//太原
          '合肥': [117.29,32.0581],//合肥
          '武汉': [112.3896,31.3628],//武汉
          '长沙': [111.5823,27.5568],//长沙
          '南京': [119.8062,32.9208],//南京
          '成都': [103.9526,30.7617],//成都
          '贵阳': [106.6992,26.7682],//贵阳
          '昆明': [101.6199,23.9663],//昆明
          '南宁': [108.479,23.6152],//南宁
          '拉萨': [89.1865,30.9465],//拉萨
          '杭州': [120.5313,29.3773],//杭州
          '南昌': [116.0046,28.3633],//南昌
          '广州': [113.5107,23.5196],//广州
          '福州': [118.3543,25.9222],//福州
          '台北':[ 121.1173,23.9638],//台湾省 台 台北
          '海口': [110.0893,19.3516],//海口
          '香港': [115.1373,21.3338],//香港
          '澳门': [115.5873,22.1538]//澳门
        },
        height:'100%',
        width:'100%',
        hoursDiv:undefined,
        minutesDiv:undefined,
        bs_map_sysDate:new Date(),
        bs_map_minutes:undefined,
        bs_map_hours:undefined,
        len:0,
        signList:[],
        isYesterday:false,
      }
    },
    mounted(){
      this.init();
      this.$nextTick(() => {
        this.initChart()
      })
    },
    methods: {
      init() {
        this.width=this.$refs.mapResize.offsetWidth+'px';
        this.height=this.$refs.page.offsetHeight+'px';
      this.mapInit();
      this.menuInit();
      },
      mapInit(){
        this.initTime(this.mapInitData.object.sysTime);
      },
      menuInit(){
        this.menuData={avg:7889,count:100158,lt:38,yd:32,dx:30};
      },
      initChart() {

        // this.chart = echarts.init(this.$el, 'china')
        this.chart = echarts.init(this.$refs.chinaMap, 'china')

        //设置范围
        let splitList = new Array();
        splitList.push({
          "end" : 70
        });
        splitList.push({
          "start" : 70,
          "end" : 90
        });
        splitList.push({
          "start" : 90
        });


        let lineData = new Array();// [{coord:[2.2,1.1],value:10}, {coord:[2.2,1.1,10]}]
        let pointData = new Array();// {name:'常州',value:[2.2,1.1,10]}


        let data=this.mapInitData.object.data;
        let provinceJson=this.mapInitData.object.provinceJson;

        for (let i = 0; i < data.length; i++) {
          let d = data[i];
          let fromCoord = this.geoCoord[provinceJson[d["destCode"]]]
          let toCoord   = this.geoCoord[provinceJson[d["srcCode"]]]
          if(fromCoord && toCoord){
            let point = {
              "name" : provinceJson[d["destCode"]],
              "value" : this.geoCoord[provinceJson[d["destCode"]]].concat(d["rtt"])
            };
            pointData.push(point);

            lineData.push([{
              coord: fromCoord,
              value: d["rtt"]
            }, {
              coord: toCoord,
            }]);
          }

        }




        //设置结果
        let series = [{
          type: 'lines',
          effect: {
            show: true,
            period: 2, //箭头指向速度，值越小速度越快
            trailLength: 0.02, //特效尾迹长度[0,1]值越大，尾迹越长重
            symbol: 'arrow', //箭头图标
            symbolSize: 5, //图标大小
          },
          lineStyle: {
            normal: {
              color : '#2cbe55',
              width: 1, //尾迹线条宽度
              opacity: 1, //尾迹线条透明度
              curveness: .3 //尾迹线条曲直度
            }
          },
          data: lineData
        }, {
          type: 'effectScatter',
          coordinateSystem: 'geo',
          rippleEffect: { //涟漪特效
            period: 4, //动画时间，值越小速度越快
            brushType: 'stroke', //波纹绘制方式 stroke, fill
            scale: 3 //波纹圆环最大限制，值越大波纹越大
          },
          label: {
            normal: {
              show: true,
              position: 'right', //显示位置
              offset: [5, 0], //偏移设置
              formatter: function(params){//圆环显示文字
                return params.data.name;
              },
              fontSize: 13
            },
            emphasis: {
              show: true
            }
          },
          symbol: 'circle',
          symbolSize: 12,
          itemStyle: {
            normal: {
              show: false,
              color: '#2cbe55'
            }
          },
          data: pointData
        }
        ];


        this.chart.setOption({
          geo: {
            map: 'china',
            zoom: 1.2,
            label: {
              emphasis: {
                show: false
              }
            },
            itemStyle: {
              normal: {
                borderWidth:1,//外边框宽度
                borderColor: '#648dff', //外边框颜色
                color: 'rgba(31,29,178,1)',//'#1f1db2', //地图背景色
                opacity :0.8,
                shadowOffsetX:15,
                shadowOffsetY:15,
                shadowColor:'rgba(1,14,88,0.6)',//阴影颜色
              }
            },
          },
          dataRange : {
            show : false,
            splitNumber : 3,
            splitList : splitList,
            color :['#ff2121','#ffa200','#5adc0b'],
          },
          series:series
        })

      },
      initTime(sysTime) {
        let currentMinutes = this.bs_map_sysDate.getMinutes();
        let currentHours = this.bs_map_sysDate.getHours();
        currentHours = (currentHours < 10 ? '0' + currentHours : currentHours);
        this.getStrategyMinute(currentHours, currentMinutes);
        this.getBefore24Hours(currentHours);
        this.minutesDiv=this.bs_map_minutes.split(",")[this.bs_map_minutes.split(",").length - 1];
        this.hoursDiv=currentHours;

      },
      getStrategyMinute(currentHours, currentMinutes) {
        let strategyValue = 5;// 默认策略值
        let defaultMinutes = 60;
        let minutesStr = "00,";
        let minutes = 0;
        let index = parseInt(defaultMinutes / strategyValue);
        for (let i = 0; i < (index - 1); i++) {
          minutes = parseInt(minutes) + parseInt(strategyValue);
          if (!this.hoursDiv|| currentHours == this.hoursDiv) {
            // 如果是当时小时，则要显示的分钟数不能大于当时分钟数
            if (minutes > parseInt(currentMinutes)) {
              break;
            }
          }
          minutesStr += ((minutes < 10 ? '0' + minutes : minutes) + ",");
        }
        this.bs_map_minutes= minutesStr.substring(0, minutesStr.length - 1);
      },
      getBefore24Hours(currentHours) {
        let hoursStr = "";
        let dayName = "(昨)";
        for (let i = 0; i < 24; i++) {
          if (currentHours == 23) {
            currentHours = -1;
          }
          if (currentHours == -1) {
            dayName = "";
          }
          currentHours = parseInt(currentHours) + 1;
          hoursStr += ((currentHours < 10 ? '0' + currentHours : currentHours)+ dayName + ",");
        }
        this.bs_map_hours =  hoursStr.substring(0, hoursStr.length - 1);
      },
      initTimeAxis(sign){
        let hours = this.bs_map_hours;
        let minutes = this.bs_map_minutes;
        let str = "";

        let axis;
        if (sign == "hours") {
          this.len = hours.split(",").length % 4 > 0 ? hours.split(",").length / 4 + 1 : hours.split(",").length / 4;
          axis = hours;
        } else if (sign == "minutes") {
          this.len = minutes.split(",").length % 4 > 0 ? minutes.split(",").length / 4 + 1 : minutes.split(",").length / 4;
          axis = minutes;
        } else {
          return;
        }
        this.signList=[];
        for (let i = 0; i < this.len; i++) {
          for (let j = 0; j < 4; j++) {
            let index = i * 4 + j;
            if (index >= axis.split(",").length) {
              break;
            }
            let value = axis.split(",")[index];
            let liClass = "";
            if ((sign == "hours" && this.hoursDiv == (value.substring(0, 2)))
              || (sign == "minutes" && this.minutesDiv == (value.substring(0, 2)))) {
              liClass = " 'hours_click' ";
            }
            let signJson={};
            signJson.sign=sign;
            signJson.dataTime=value;
            signJson.val=value;
            signJson.class=liClass;
            this.signList.push(signJson);
          }
        }
      },
      timeClickEvent(sign, time) {
        let nowDate = this.getNowFormatDate(0);
        let timesheet = "";
        if (sign == "hours") {
          if (time.length > 2) {
            nowDate = this.getNowFormatDate(-1);
            this.isYesterday = true;
          } else {
            this.isYesterday = false;
          }
          time = time.substring(0, 2);// 只取前两个数字，去掉汉字
          this.hoursDiv=time;

          // 判断所选择是否为当前小时，如果是当前小时，则分钟数的显示就不能大于当前的分钟
          let currentHours = this.bs_map_sysDate.getHours();
          currentHours < 10 ? '0' + currentHours : currentHours;
          if (time == currentHours) {
            let currentMinutes = this.bs_map_sysDate.getMinutes();
            this.bs_map_minutes = this.getStrategyMinute(currentHours, currentMinutes);
            this.minutesDiv=this.bs_map_minutes.split(",")[this.bs_map_minutes.split(",").length - 1];
          }
        } else if (sign == "minutes") {
          if (this.isYesterday) {
            nowDate = getNowFormatDate(-1);
          }
          time = time.substring(0, 2);// 只取前两个数字，去掉汉字
          this.minutesDiv=time;
        }
        timesheet = nowDate + this.hoursDiv + this.minutesDiv + "00";
        this.currTime = timesheet;//设置时间节点
      },
      getNowFormatDate(AddDayCount) {
        let day = this.bs_map_sysDate;
        day.setDate(day.getDate() + AddDayCount);// 获取AddDayCount天后的日期
        let Year = 0;
        let Month = 0;
        let Day = 0;
        let CurrentDate = "";
        Year = day.getFullYear();// 支持IE和火狐浏览器.
        Month = day.getMonth() + 1;
        Day = day.getDate();
        CurrentDate += Year;
        if (Month >= 10) {
          CurrentDate += Month;
        } else {
          CurrentDate += "0" + Month;
        }
        if (Day >= 10) {
          CurrentDate += Day;
        } else {
          CurrentDate += "0" + Day;
        }
        return CurrentDate;
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
  .pagebj{background-image:url(./images/bj.png);
    background-repeat: no-repeat;
    background-size: 100% 100%;
    width:100%;
    min-width:648px;
    min-height:1040px;
    position:relative; left:0; top:0; overflow: hidden;
    padding: 0;
    margin: 0;
  }

  .kong{ width:7rem; float:left;height:1.9rem; background:url(./images/unicom_logo.png) no-repeat bottom left; background-size:auto 95%;}
  .title{
    width: 100%;
    height: 2rem;
    line-height: 2rem;
    font-size: 1rem;
    color: #e7e7ff;
    text-align: center;
    position: absolute;
    top: 1px;
    z-index: 2;
  }
  .container,.charts{ width: 98%;  position:absolute; left: 1%;}
  .container{  top: 2.5rem;}
  .charts{ height: 10rem; bottom: 0;}
  /*时间选择*/
  .date{ width:13rem; position:absolute; top:800px; left:0.9rem; z-index:3;}
  .map_con,.matrix_con{  position: absolute;  height: 100%;}
  .map_con{left: 0;width: 100%;}

  .alarm,.column,.diagram{ float: left; height: 10rem;}
  .alarm,.column{ margin-right: 1%;}
  .alarm{width: 21%;}
  .column{ width: 27%;}
  .diagram{ width: 50%;}
  .china_map{ width: 96%; position:absolute; left: 2%; height: 90%}


  .average{  width:56%;  position: absolute; left: 0; line-height: 1rem;}
  .menu_val{ color:#a7cdff; font-size:0.7rem; text-align: center;  line-height: 1.2rem; background-image:url("./images/average_bj.png"); background-repeat: no-repeat; background-size: 100% 100%; width: 100%;}
  .menu_val tr td.cor_green,.menu_val tr td.cor_org,.menu_val tr td.cor_blue{ font-size:1.1rem; line-height: 2rem;}
  .menu_val tr td.cor_green,.menu_val tr td.cor_org{ border-right: 1px solid #0e227d;}




  /*图例*/
  .legend{background-color: rgba(0,0,0,0.5); border-radius:0.3rem; position:absolute; right:1rem; top:600px;color:#a7cdff; font-size:12px;
    line-height:1.2rem;
    width: 6.2rem;
    padding: 0.4rem 0;
  }

  /*时间控件*/
  .date_timing2{ background:url(./images/tate2.png) no-repeat left; height:75px; font-size:42px; line-height:75px; font-weight:bold;}
  .date_timing2 span{ float:left;}
  .date_timing2 .hours2{background: -webkit-gradient(linear, 0 0, 0 bottom,from(#fff),to(#b5bbc2)); color:#fff;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
  .hours2{ padding-left:16px;}
  .hours_more2,.hours_more3{ background:url(./images/more.png) no-repeat center; height:75px; cursor:pointer; width:17px;}
  .hours_more2{ margin:0px 19px 0px 12px;}
  .hours_more3{ margin:0px 0px 0px 9px;}

  .more_open2{ width:216px; background:url(./images/hours_open2.png) no-repeat right center; position:relative; top:2px; left:0; z-index:4;}
  .more_open2 ul{ background:url(./images/line3.png) no-repeat bottom; padding:3px 2px 2px; height:20px;}
  .more_open2 ul li{ width:53px; float:left; height:16px; background:url(./images/line2.png) no-repeat left center;
    font-size:12px; color:#a7cdff; text-align:center;
    line-height:15px; cursor:pointer;}
  .more_open2 ul li:first-child{ background-image:none;}
  .more_open2 ul li.hours_click,.more_open2 ul li:hover.hours_click{ background:url(./images/hours_click.png) no-repeat center; color:#fff;}
  .more_open2 ul li:hover{ background:url(./images/hours_hover.png) no-repeat center; color:#fff;}
</style>
