package com.yiruo.activiti.controller;


import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.yiruo.activiti.entity.Leave;
import com.yiruo.activiti.service.ProcessService;
import com.yiruo.common.core.controller.BaseController;
import com.yiruo.common.core.domain.AjaxResult;
import com.yiruo.common.core.page.TableDataInfo;
import net.sf.jsqlparser.util.validation.validator.InsertValidator;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 流程信息的控制层
 */
@RestController
@RequestMapping("/activiti/process")
public class ProcessController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProcessService processService;

    /**
     * 查询流程定义分页
     *
     * @param currentPage               当前页数
     * @param pageSize                  每页记录数
     * @param processDefinitionKey      流程定义编号
     * @param processDefinitionName     流程定义名称
     * @param processDefinitionCategory 流程定义类别
     * @return
     */
    @GetMapping(path = "/queryProcessDefinition")
    public TableDataInfo queryProcessDefinition(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                                @RequestParam(name = "processDefinitionKey", required = false) String processDefinitionKey,
                                                @RequestParam(name = "processDefinitionName", required = false) String processDefinitionName,
                                                @RequestParam(name = "processDefinitionCategory", required = false) String processDefinitionCategory,
                                                @RequestParam(name = "processDefinitionVersion", required = false) Integer processDefinitionVersion,
                                                @RequestParam(name = "suspended", required = false) Short suspended) {
        startPage();
        List<LinkedHashMap<String, Object>> list = processService.queryProcessDefinition(pageNum, pageSize, processDefinitionKey, processDefinitionName,
                processDefinitionCategory, processDefinitionVersion, suspended);
        return getDataTable(list);
    }

    /**
     * 查询流程实例分页
     *
     * @param currentPage              当前页数
     * @param pageSize                 每页记录数
     * @param processDefinitionKey     流程定义编号
     * @param processDefinitionName    流程定义名称
     * @param processDefinitionVersion 流程定义版本
     * @param processInstanceName      流程实例名称
     * @param suspended                是否挂起
     * @param startUserId              发起者
     * @return
     */
    @GetMapping(path = "/queryProcessInstance")
    public TableDataInfo queryProcessInstance(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer currentPage,
                                                   @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                                   @RequestParam(name = "processDefinitionKey", required = false) String processDefinitionKey,
                                                   @RequestParam(name = "processDefinitionName", required = false) String processDefinitionName,
                                                   @RequestParam(name = "processDefinitionVersion", required = false) Integer processDefinitionVersion,
                                                   @RequestParam(name = "processInstanceName", required = false) String processInstanceName,
                                                   @RequestParam(name = "suspended", required = false) Short suspended, @RequestParam(name = "startUserId", required = false) String startUserId) {
        List<LinkedHashMap<String, Object>> list =processService.queryProcessInstance(currentPage, pageSize, processDefinitionKey, processDefinitionName,
                processDefinitionVersion, processInstanceName, suspended, startUserId);
        return getDataTable(list);
    }

    /**
     * 查询流程实例执行路径分页
     *
     * @param currentPage       当前页数
     * @param pageSize          每页记录数
     * @param processInstanceId 流程实例ID
     * @return
     */
    @GetMapping(path = "/queryExecution")
    public TableDataInfo queryExecution(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer currentPage,
                                             @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                             @RequestParam(name = "processInstanceId", required = true) String processInstanceId) {
        List<LinkedHashMap<String, Object>> list= processService.queryExecution(currentPage, pageSize, processInstanceId);
        return getDataTable(list);
    }

    /**
     * 获取流程定义XML
     *
     * @param deploymentId 部署ID
     * @return
     */
    @PostMapping(path = "/getProcessResource")
    public AjaxResult getProcessResource(@RequestParam(name = "deploymentId", required = true) String deploymentId) {
        return AjaxResult.success(processService.getProcessResource(deploymentId));
    }

    /**
     * 获取高亮跟踪流程图
     *
     * @param processInstanceId 流程实例ID
     * @return
     */
    @PostMapping(path = "/getHighLightedProcessDiagram")
    public AjaxResult getHighLightedProcessDiagram(@RequestParam(name = "processInstanceId", required = true) String processInstanceId) {
        String username=getLoginUser().getUsername();
        return AjaxResult.success(processService.getHighLightedProcessDiagram(processInstanceId,username));
    }

    /**
     * 获取流程定义图片
     *
     * @param deploymentId 部署ID
     * @return
     */
    @PostMapping(value = "/getProcessImage")
    public ResponseEntity<byte[]> getProcessImage(@RequestParam(name = "deploymentId", required = true) String deploymentId) {
        ResponseEntity<byte[]> responseEntity = null;
        try {
            InputStream imageInputStream = processService.getProcessImage(deploymentId);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type", "text/xml");
            responseEntity = new ResponseEntity<>(IOUtils.toByteArray(imageInputStream), responseHeaders, HttpStatus.OK);
        } catch (IOException e) {
            logger.warn(e.toString());
        }
        return responseEntity;
    }

    /**
     * 持久化流程
     *
//     * @param modelId 模型ID
//     * @param jsonXml 流程基本信息
//     * @param svgXml  流程设计信息
     * @throws Exception
     */

//    public AjaxResult persistProcess(@RequestParam(name = "modelId", required = true) String modelId,
//                                       @RequestParam(name = "jsonXml", required = true) String jsonXml, @RequestParam(name = "svgXml", required = true) String svgXml) {
    @PostMapping(path = "/persistProcess")
    public AjaxResult persistProcess(@RequestBody JSONObject data){
        String modelId=data.getString("modelId");
        String jsonXml=data.getString("jsonXml");
        String svgXml=data.getString("svgXml");
        processService.persistProcess(modelId, jsonXml, svgXml);
        return AjaxResult.success();
    }

    /**
     * 发起流程
     *
     * @param leave 请假对象
     * @return
     */
    @PostMapping(path = "/startProcessInstance")
    public AjaxResult startProcessInstance(@Validated(InsertValidator.class) @RequestBody Leave leave) {
        String username=getLoginUser().getUsername();
        processService.startProcessInstance(leave, username);
        return AjaxResult.success();
    }

    /**
     * 挂起流程定义
     *
     * @param processDefinitionId 流程定义ID
     * @return
     */
    @PostMapping(path = "/suspendProcessDefinition")
    public AjaxResult suspendProcessDefinition(@RequestParam(name = "processDefinitionId", required = true) String processDefinitionId) {
        processService.suspendProcessDefinition(processDefinitionId);
        return AjaxResult.success();
    }

    /**
     * 激活流程定义
     *
     * @param processDefinitionId 流程定义ID
     * @return
     */
    @PostMapping(path = "/activateProcessDefinition")
    public AjaxResult activateProcessDefinition(@RequestParam(name = "processDefinitionId", required = true) String processDefinitionId) {
        processService.activateProcessDefinition(processDefinitionId);
        return AjaxResult.success();
    }

    /**
     * 挂起流程实例
     *
     * @param processInstanceId 流程实例ID
     * @return
     */
    @PostMapping(path = "/suspendProcessInstance")
    public AjaxResult suspendProcessInstance(@RequestParam(name = "processInstanceId", required = true) String processInstanceId) {
        processService.suspendProcessInstance(processInstanceId);
        return AjaxResult.success();
    }

    /**
     * 激活流程实例
     *
     * @param processInstanceId 流程实例ID
     * @return
     */
    @PostMapping(path = "/activateProcessInstance")
    public AjaxResult activateProcessInstance(@RequestParam(name = "processInstanceId", required = true) String processInstanceId) {
        processService.activateProcessInstance(processInstanceId);
        return AjaxResult.success();
    }

    /**
     * 删除流程实例
     *
     * @param processInstanceId 流程实例ID
     * @return
     */
    @PostMapping(path = "/deleteProcessInstance")
    public AjaxResult deleteProcessInstance(@RequestParam(name = "processInstanceId", required = true) String[] processInstanceId) {
        processService.deleteProcessInstance(processInstanceId);
        return AjaxResult.success();
    }

}

