package com.yiruo.activiti.service.impl;


import static com.yiruo.common.utils.PageUtils.startPage;
import static java.util.Collections.emptyList;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.yiruo.activiti.entity.Leave;
import com.yiruo.activiti.provider.ProcessDiagramGeneratorWrapper;
import com.yiruo.activiti.service.ProcessService;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.util.IoUtil;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * ????????????????????????????????????
 *
 * @???????????? ?????????????????????????????? www.mscodecloud.com
 */
@Service
@Transactional
public class ProcessServiceImpl implements ProcessService {

    private static final String DESCRIPTION = "description";
    private static final String SUSPENDED = "suspended";
    private static final String TENANTID = "tenantId";

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private com.yiruo.activiti.service.TaskService codeTaskService;

    @Autowired
    private org.activiti.engine.TaskService taskService;

    @Autowired
    protected ProcessEngineConfiguration processEngineConfiguration;

//    @Autowired
//    private SecurityUtil securityUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private ProcessDiagramGeneratorWrapper processDiagramGeneratorWrapper;

    /**
     * ????????????????????????
     */
    @Override
    public List<LinkedHashMap<String, Object>> queryProcessDefinition(Integer currentPage, Integer pageSize, String processDefinitionKey, String processDefinitionName,
                                                      String processDefinitionCategory, Integer processDefinitionVersion, Short suspended) {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        if (StringUtils.isNotBlank(processDefinitionKey)) {
            processDefinitionQuery.processDefinitionKeyLike("%" + processDefinitionKey + "%");
        }
        if (StringUtils.isNotBlank(processDefinitionName)) {
            processDefinitionQuery.processDefinitionNameLike("%" + processDefinitionName + "%");
        }
        if (StringUtils.isNotBlank(processDefinitionCategory)) {
            processDefinitionQuery.processDefinitionCategoryLike("%" + processDefinitionCategory + "%");
        }
        if (processDefinitionVersion != null) {
            processDefinitionQuery.processDefinitionVersion(processDefinitionVersion);
        }
        if (suspended != null && suspended == 1) {
            processDefinitionQuery.suspended();
        }
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage(pageSize * (currentPage - 1), pageSize);
        List<LinkedHashMap<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < processDefinitionList.size(); i++) {
            LinkedHashMap<String, Object> processDefinitionMap = new LinkedHashMap<>();
            processDefinitionMap.put("id", processDefinitionList.get(i).getId());
            processDefinitionMap.put("category", processDefinitionList.get(i).getCategory());
            processDefinitionMap.put(DESCRIPTION, processDefinitionList.get(i).getDescription());
            processDefinitionMap.put("diagramResourceName", processDefinitionList.get(i).getDiagramResourceName());
            processDefinitionMap.put("deploymentId", processDefinitionList.get(i).getDeploymentId());
            processDefinitionMap.put("engineVersion", processDefinitionList.get(i).getEngineVersion());
            processDefinitionMap.put("key", processDefinitionList.get(i).getKey());
            processDefinitionMap.put("name", processDefinitionList.get(i).getName());
            processDefinitionMap.put("resourceName", processDefinitionList.get(i).getResourceName());
            processDefinitionMap.put(SUSPENDED, processDefinitionList.get(i).isSuspended());
            processDefinitionMap.put(TENANTID, processDefinitionList.get(i).getTenantId());
            processDefinitionMap.put("version", processDefinitionList.get(i).getVersion());
            resultList.add(processDefinitionMap);
        }
        return  resultList;
    }

    /**
     * ????????????????????????
     */
    @Override
    public List<LinkedHashMap<String, Object>> queryProcessInstance(Integer currentPage, Integer pageSize, String processDefinitionKey, String processDefinitionName,
                                                    Integer processDefinitionVersion, String processInstanceName, Short suspended, String startUserId) {
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        if (StringUtils.isNotBlank(processDefinitionKey)) {
            processInstanceQuery.processDefinitionKey(processDefinitionKey);
        }
        if (StringUtils.isNotBlank(processDefinitionName)) {
            processInstanceQuery.processDefinitionName(processDefinitionName);
        }
        if (processDefinitionVersion != null) {
            processInstanceQuery.processDefinitionVersion(processDefinitionVersion);
        }
        if (StringUtils.isNotBlank(processInstanceName)) {
            processInstanceQuery.processInstanceNameLike("%" + processInstanceName + "%");
        }
        if (suspended != null && suspended == 1) {
            processInstanceQuery.suspended();
        }
        if (StringUtils.isNotBlank(startUserId)) {
            processInstanceQuery.startedBy(startUserId);
        }
        List<ProcessInstance> processInstanceList = processInstanceQuery.includeProcessVariables().listPage(pageSize * (currentPage - 1), pageSize);
        List<LinkedHashMap<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < processInstanceList.size(); i++) {
            LinkedHashMap<String, Object> processInstanceMap = new LinkedHashMap<>();
            processInstanceMap.put("id", processInstanceList.get(i).getId());
            processInstanceMap.put("activityId", processInstanceList.get(i).getActivityId());
            processInstanceMap.put("businessKey", processInstanceList.get(i).getBusinessKey());
            processInstanceMap.put(DESCRIPTION, processInstanceList.get(i).getDescription());
            processInstanceMap.put("deploymentId", processInstanceList.get(i).getDeploymentId());
            processInstanceMap.put("ended", processInstanceList.get(i).isEnded());
            processInstanceMap.put("name", processInstanceList.get(i).getName());
            processInstanceMap.put("processInstanceId", processInstanceList.get(i).getProcessInstanceId());
            processInstanceMap.put("processDefinitionId", processInstanceList.get(i).getProcessDefinitionId());
            processInstanceMap.put("processDefinitionKey", processInstanceList.get(i).getProcessDefinitionKey());
            processInstanceMap.put("processDefinitionName", processInstanceList.get(i).getProcessDefinitionName());
            processInstanceMap.put("processDefinitionVersion", processInstanceList.get(i).getProcessDefinitionVersion());
            processInstanceMap.put("processVariables", processInstanceList.get(i).getProcessVariables());
            processInstanceMap.put("startUserId", processInstanceList.get(i).getStartUserId());
            processInstanceMap.put("startTime", processInstanceList.get(i).getStartTime());
            processInstanceMap.put(SUSPENDED, processInstanceList.get(i).isSuspended());
            processInstanceMap.put(TENANTID, processInstanceList.get(i).getTenantId());
            resultList.add(processInstanceMap);
        }

    return resultList;
    }

    /**
     * ????????????????????????????????????
     */
    @Override
    public List<LinkedHashMap<String, Object>> queryExecution(Integer currentPage, Integer pageSize, String processInstanceId) {
        ExecutionQuery executionQuery = runtimeService.createExecutionQuery();
        if (StringUtils.isNotBlank(processInstanceId)) {
            executionQuery.processInstanceId(processInstanceId);
        }
        List<Execution> executionList = executionQuery.listPage(pageSize * (currentPage - 1), pageSize);
        List<LinkedHashMap<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < executionList.size(); i++) {
            LinkedHashMap<String, Object> executionMap = new LinkedHashMap<>();
            executionMap.put("id", executionList.get(i).getId());
            executionMap.put("activityId", executionList.get(i).getActivityId());
            executionMap.put(DESCRIPTION, executionList.get(i).getDescription());
            executionMap.put("name", executionList.get(i).getName());
            executionMap.put("parentId", executionList.get(i).getParentId());
            executionMap.put("parentProcessInstanceId", executionList.get(i).getParentProcessInstanceId());
            executionMap.put("processInstanceId", executionList.get(i).getProcessInstanceId());
            executionMap.put("rootProcessInstanceId", executionList.get(i).getRootProcessInstanceId());
            executionMap.put("superExecutionId", executionList.get(i).getSuperExecutionId());
            executionMap.put(TENANTID, executionList.get(i).getTenantId());
            executionMap.put("ended", executionList.get(i).isEnded());
            executionMap.put(SUSPENDED, executionList.get(i).isSuspended());
            resultList.add(executionMap);
        }
        return  resultList;
    }

    /**
     * ??????????????????XML
     */
    @Override
    public String getProcessResource(String deploymentId) {
        ProcessDefinition processDefinition = checkProcessDefinition(deploymentId);
        String bpmnResourceName = processDefinition.getResourceName();
        final InputStream deploymentInputStream = repositoryService.getResourceAsStream(deploymentId, bpmnResourceName);
        byte[] bytes = IoUtil.readInputStream(deploymentInputStream, "input stream");
        return new String(bytes);
    }

    /**
     * ???????????????????????????
     */
    @Override
    public String getHighLightedProcessDiagram(String processInstanceId,String username) {
        UserDetails user=userDetailsService.loadUserByUsername(username);
        if(user==null){
            throw new IllegalStateException("user  not exist");
        }
        SecurityContextHolder.setContext(new SecurityContextImpl(new org.springframework.security.core.Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {

                List<GrantedAuthority> lg= AuthorityUtils.createAuthorityList(username.equals("admin")?"ROLE_ACTIVITI_ADMIN":"common","ROLE_ACTIVITI_USER");
                return lg;
            }

            @Override
            public Object getCredentials() {
                return user.getPassword();
            }

            @Override
            public Object getDetails() {
                return user;
            }

            @Override
            public Object getPrincipal() {
                return user;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated)  {
            }

            @Override
            public String getName() {
                return user.getUsername();
            }
        }));
        org.activiti.engine.impl.identity.Authentication.setAuthenticatedUserId(user.getUsername());
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if (task == null) {
            throw new ActivitiException("???????????????");
        }
        org.activiti.api.process.model.ProcessInstance processInstance = processRuntime.processInstance(processInstanceId);
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        return new String(processDiagramGeneratorWrapper.generateDiagram(bpmnModel,
                processRuntime.processInstanceMeta(processInstance.getId()).getActiveActivitiesIds(), emptyList()), StandardCharsets.UTF_8);
    }

    /**
     * ????????????????????????
     */
    @Override
    public InputStream getProcessImage(String deploymentId) {
        ProcessDefinition processDefinition = checkProcessDefinition(deploymentId);
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        InputStream inputStream = null;
        if (bpmnModel != null && bpmnModel.getLocationMap().size() > 0) {
            ProcessDiagramGenerator processDiagramGenerator = new DefaultProcessDiagramGenerator();
            inputStream = processDiagramGenerator.generateDiagram(bpmnModel, emptyList(), emptyList(), "??????", "??????", "??????", true);
        }
        return inputStream;
    }

    /**
     * ??????????????????????????????
     */
    private ProcessDefinition checkProcessDefinition(String deploymentId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
        if (processDefinition == null) {
            throw new ActivitiObjectNotFoundException("????????????????????????", ProcessDefinition.class);
        }
        return processDefinition;
    }

    /**
     * ???????????????
     */
    @Override
    public void persistProcess(String modelId, String jsonXml, String svgXml) {
        Model model = repositoryService.getModel(modelId);
        repositoryService.addModelEditorSource(model.getId(), jsonXml.getBytes(StandardCharsets.UTF_8));
        repositoryService.addModelEditorSourceExtra(model.getId(), svgXml.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * ????????????
     */
    @Override
    public void startProcessInstance(Leave leave, String starterUser) {
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(leave.getProcessDefinitionId());
        if (processDefinition.isSuspended()) {
            throw new ActivitiIllegalArgumentException("????????????????????????????????????");
        }
        Authentication.setAuthenticatedUserId(starterUser);
        Map<String, Object> startVariables = new HashMap<>();
        startVariables.put("candidate", leave.getCandidate());
        startVariables.put("startTime", leave.getStartTime());
        startVariables.put("endTime", leave.getEndTime());
        startVariables.put(DESCRIPTION, leave.getDescription());

        JSONArray customForm = leave.getCustomForm();// ???????????????
        for (int i = 0; i < customForm.size(); i++) {
            JSONObject jsonObject = customForm.getJSONObject(i);
            Iterator iterator = jsonObject.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                startVariables.put("????????????" + String.valueOf(entry.getKey()), entry.getValue());
            }
        }

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(leave.getProcessDefinitionId(), startVariables);
        runtimeService.setProcessInstanceName(processInstance.getId(), starterUser + "???????????????");
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        if (task != null) {
            if (StringUtils.isBlank(leave.getCandidate())) {
                throw new ActivitiIllegalArgumentException("????????????????????????????????????????????????");
            }
            codeTaskService.updateTask(task.getId(), leave.getCandidate());
        }
    }

    /**
     * ??????????????????
     */
    @Override
    public void suspendProcessDefinition(String processDefinitionId) {
        if (repositoryService.isProcessDefinitionSuspended(processDefinitionId)) {
            throw new ActivitiException("ID???'" + processDefinitionId + " '????????????????????????");
        }
        repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null); // true????????????????????????
    }

    /**
     * ??????????????????
     */
    @Override
    public void activateProcessDefinition(String processDefinitionId) {
        if (!repositoryService.isProcessDefinitionSuspended(processDefinitionId)) {
            throw new ActivitiException("ID???'" + processDefinitionId + " '????????????????????????");
        }
        repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);// true????????????????????????
    }

    /**
     * ??????????????????
     */
    @Override
    public void suspendProcessInstance(String processInstanceId) {
        ProcessInstance processInstance = checkProcessInstance(processInstanceId);
        if (processInstance.isSuspended()) {
            throw new ActivitiException("ID???'" + processInstance.getId() + " '????????????????????????");
        }
        runtimeService.suspendProcessInstanceById(processInstance.getId());
    }

    /**
     * ??????????????????
     */
    @Override
    public void activateProcessInstance(String processInstanceId) {
        ProcessInstance processInstance = checkProcessInstance(processInstanceId);
        if (!processInstance.isSuspended()) {
            throw new ActivitiException("ID???'" + processInstance.getId() + " '????????????????????????");
        }
        runtimeService.activateProcessInstanceById(processInstance.getId());
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void deleteProcessInstance(String[] processInstanceId) {
        for (int i = 0; i < processInstanceId.length; i++) {
            ProcessInstance processInstance = checkProcessInstance(processInstanceId[i]);
            runtimeService.deleteProcessInstance(processInstance.getId(), "??????ID???'" + processInstanceId[i] + " '???????????????");
        }
    }

    /**
     * ??????????????????????????????
     */
    private ProcessInstance checkProcessInstance(String processInstanceId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (processInstance == null) {
            throw new ActivitiObjectNotFoundException("?????????ID???'" + processInstanceId + "'???????????????");
        }
        return processInstance;
    }

}
