package com.yiruo.activiti.controller;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.yiruo.activiti.service.TaskService;
import com.yiruo.common.core.controller.BaseController;
import com.yiruo.common.core.domain.AjaxResult;
import com.yiruo.common.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 任务信息的控制层
 * 
 */
@RestController
@RequestMapping("/activiti/task")
public class TaskController extends BaseController {

	@Autowired
	private TaskService taskService;


	/**
	 * 查询任务分页
	 *
	 * @param currentPage           当前页数
	 * @param pageSize              每页记录数
	 * @param processDefinitionName 流程定义名称
	 * @param taskName              任务名称
	 * @param assignee              当前签收者
	 * @return
	 */
	@GetMapping(path = "/queryTask")
	public TableDataInfo queryTask(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer currentPage,
								   @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
								   @RequestParam(name = "processDefinitionName", required = false) String processDefinitionName,
								   @RequestParam(name = "taskName", required = false) String taskName, @RequestParam(name = "assignee", required = false) String assignee) {
		startPage();
		List<LinkedHashMap<String, Object>> list = taskService.queryTask(currentPage, pageSize, processDefinitionName, taskName, assignee);
		return getDataTable(list);
	}

	/**
	 * 签收任务
	 *
	 * @param taskId 任务ID
	 * @return
	 */
	@PostMapping(path = "/claimTask")
	public AjaxResult claimTask(@RequestParam(name = "taskId", required = true) String[] taskId) {
		String username=getLoginUser().getUsername();
		taskService.claimTask(taskId, username);
		return AjaxResult.success();
	}

	/**
	 * 完结任务，任务进入下一节点
	 *
	 * @param taskId            任务ID
	 * @param processInstanceId 流程实例ID
	 * @param candidate         下一节点候选者
	 * @param comment           批注
	 * @return
	 */
	@PostMapping(path = "/completeTask")
	public AjaxResult completeTask(@RequestParam(name = "taskId", required = true) String taskId,
			@RequestParam(name = "processInstanceId", required = true) String processInstanceId,
			@RequestParam(name = "candidate", required = false) String candidate, @RequestParam(name = "comment", required = true) String comment) {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//authentication.getName()
		String username=getLoginUser().getUsername();
		taskService.completeTask(taskId, processInstanceId, candidate, comment, username);
		return AjaxResult.success();
	}

	/**
	 * 委派任务
	 *
	 * @param taskId            任务ID
	 * @param processInstanceId 流程实例ID
	 * @param assignee          签收者
	 * @param comment           批注
	 * @return
	 */
	@PostMapping(path = "/delegateTask")
	public AjaxResult delegateTask(@RequestParam(name = "taskId", required = true) String taskId,
			@RequestParam(name = "processInstanceId", required = true) String processInstanceId,
			@RequestParam(name = "assignee", required = false) String assignee, @RequestParam(name = "comment", required = true) String comment) {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username=getLoginUser().getUsername();
		taskService.delegateTask(taskId, processInstanceId, comment, username, assignee);
		return AjaxResult.success();
	}

	/**
	 * 被委派人完结任务并返回
	 *
	 * @param taskId            任务ID
	 * @param processInstanceId 流程实例ID
	 * @param comment           批注
	 * @return
	 */
	@PostMapping(path = "/resolveTask")
	public AjaxResult resolveTask(@RequestParam(name = "taskId", required = true) String taskId,
			@RequestParam(name = "processInstanceId", required = true) String processInstanceId,
			@RequestParam(name = "comment", required = true) String comment) {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username=getLoginUser().getUsername();
		taskService.resolveTask(taskId, processInstanceId, comment, username);
		return AjaxResult.success();
	}

	/**
	 * 回退任务
	 *
	 * @param taskId            任务ID
	 * @param processInstanceId 流程实例ID
	 * @param comment           批注
	 * @return
	 */
	@PostMapping(path = "/regressTask")
	public AjaxResult regressTask(@RequestParam(name = "taskId", required = true) String taskId,
			@RequestParam(name = "processInstanceId", required = true) String processInstanceId,
			@RequestParam(name = "comment", required = true) String comment) {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username=getLoginUser().getUsername();
		taskService.regressTask(taskId, processInstanceId, comment, username);
		return AjaxResult.success();
	}

	/**
	 * 驳回任务
	 *
	 * @param taskId            任务ID
	 * @param processInstanceId 流程实例ID
	 * @param comment           批注
	 * @return
	 */
	@PostMapping(path = "/rejectTask")
	public AjaxResult rejectTask(@RequestParam(name = "taskId", required = true) String taskId,
			@RequestParam(name = "processInstanceId", required = true) String processInstanceId,
			@RequestParam(name = "comment", required = true) String comment) {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username=getLoginUser().getUsername();
		taskService.rejectTask(taskId, processInstanceId, comment, username);
		return AjaxResult.success();
	}

	/**
	 * 终止任务
	 *
	 * @param taskId            任务ID
	 * @param processInstanceId 流程实例ID
	 * @param comment           批注
	 * @return
	 */
	@PostMapping(path = "/terminateTask")
	public AjaxResult terminateTask(@RequestParam(name = "taskId", required = true) String taskId,
			@RequestParam(name = "processInstanceId", required = true) String processInstanceId,
			@RequestParam(name = "comment", required = true) String comment) {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username=getLoginUser().getUsername();
		taskService.terminateTask(taskId, processInstanceId, comment, username);
		return AjaxResult.success();
	}

	/**
	 * 查询个人任务分页
	 *
	 * @param currentPage           当前页数
	 * @param pageSize              每页记录数
	 * @param processDefinitionName 流程定义名称
	 * @param taskName              任务名称
	 * @param startTime             请假开始时间
	 * @param endTime               请假结束时间
	 * @param description           请假备注
	 * @param taskStatus            任务状态
	 * @return
	 */
	@GetMapping(path = "/queryPersonalTask")
	public TableDataInfo queryPersonalTask(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer currentPage,
			@RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
			@RequestParam(name = "processDefinitionName", required = false) String processDefinitionName,
			@RequestParam(name = "taskName", required = false) String taskName, @RequestParam(name = "startTime", required = false) String startTime,
			@RequestParam(name = "endTime", required = false) String endTime, @RequestParam(name = "description", required = false) String description,
			@RequestParam(name = "taskStatus", required = true) String taskStatus) {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username=getLoginUser().getUsername();
		startPage();
		List<LinkedHashMap<String, Object>> list = taskService.queryPersonalTask(currentPage, pageSize, username, processDefinitionName, taskName, startTime,
				endTime, description, taskStatus);
		return getDataTable(list);
	}



	/**
	 * 查询任务批注
	 *
	 * @param processInstanceId 流程实例ID
	 * @return
	 */
	@GetMapping(path = "/queryTaskComment")
	public AjaxResult queryTaskComment(@RequestParam(name = "processInstanceId", required = true) String processInstanceId) {
		Map<String, Object> data = taskService.queryTaskComment(processInstanceId);
		return AjaxResult.success(data);
	}


	/**
	 * 查询代理人的下拉框数据
	 *
	 * @return
	 */
	@GetMapping(path = "/queryUsername")
	public AjaxResult queryUsername() {
		List<IdentityHashMap<String, Object>> data = new ArrayList<IdentityHashMap<String, Object>>();

//		List<LinkedHashMap<String, Object>> sysOrgList = sysOrgServiceClient.querySysOrgList("null");
//		for (int i = 0; i < sysOrgList.size(); i++) {
//			IdentityHashMap<String, Object> optGroupMap = new IdentityHashMap<>();
//			for (Entry<String, Object> sysOrgEntry : sysOrgList.get(i).entrySet()) {
//				String sysOrgKey = sysOrgEntry.getKey();
//				Object sysOrgValue = sysOrgEntry.getValue();
//				if (sysOrgKey.equals("orgName")) {
//					optGroupMap.put("value", sysOrgValue);
//				}
//				if (sysOrgKey.equals("id")) {
//					List<LinkedHashMap<String, Object>> sysUserList = sysUserServiceClient.querySysUserList(Long.valueOf(sysOrgValue.toString()));
//					List<IdentityHashMap<String, Object>> childrenList = new ArrayList<IdentityHashMap<String, Object>>();
//					for (int j = 0; j < sysUserList.size(); j++) {
//						IdentityHashMap<String, Object> optionMap = new IdentityHashMap<>();
//						StringBuilder stringBuilder = new StringBuilder();
//						for (Entry<String, Object> sysUserEntry : sysUserList.get(j).entrySet()) {
//							String sysUserKey = sysUserEntry.getKey();
//							Object sysUserValue = sysUserEntry.getValue();
//							if (sysUserKey.equals("username")) {
//								stringBuilder.append(sysUserValue);
//								optionMap.put("key", sysUserValue);
//							}
//							if (sysUserKey.equals("nickname")) {
//								stringBuilder.append("(" + sysUserValue + ")");
//							}
//						}
//						optionMap.put("value", stringBuilder.toString());
//						childrenList.add(optionMap);
//					}
//					optGroupMap.put("children", childrenList);
//				}
//			}
//			if (!optGroupMap.get("children").toString().equals("[]")) {
//				data.add(optGroupMap);
//			}
//		}

		return AjaxResult.success(data);
	}
}
