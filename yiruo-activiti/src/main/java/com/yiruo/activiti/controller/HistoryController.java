package com.yiruo.activiti.controller;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.yiruo.activiti.service.HistoryService;
import com.yiruo.common.core.controller.BaseController;
import com.yiruo.common.core.domain.AjaxResult;
import com.yiruo.common.core.page.TableDataInfo;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricDetail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 流程任务历史信息的控制层
 * 
 */
@RestController
@RequestMapping("/activiti/history")
public class HistoryController extends BaseController {

	@Autowired
	private HistoryService historyService;

	/**
	 * 查询流程实例历史数据分页
	 * 
	 * @param currentPage              当前页数
	 * @param pageSize                 每页记录数
	 * @param processInstanceName      流程实例名称
	 * @param processDefinitionKey     流程定义编号
	 * @param processDefinitionName    流程定义名称
	 * @param processDefinitionVersion 流程定义版本
	 * @param startedAfterDate         发起时间
	 * @param finishedBeforeDate       结束时间
	 * @param status                   值为personal指我的发起
	 * @return
	 */
	@GetMapping(path = "/queryHistoricProcessInstance")
	public TableDataInfo queryHistoricProcessInstance(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer currentPage,
													  @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
													  @RequestParam(name = "processInstanceName", required = false) String processInstanceName,
													  @RequestParam(name = "processDefinitionKey", required = false) String processDefinitionKey,
													  @RequestParam(name = "processDefinitionName", required = false) String processDefinitionName,
													  @RequestParam(name = "processDefinitionVersion", required = false) Integer processDefinitionVersion,
													  @RequestParam(name = "startedAfterDate", required = false) String startedAfterDate,
													  @RequestParam(name = "finishedBeforeDate", required = false) String finishedBeforeDate,
													  @RequestParam(name = "startUserId", required = false) String startUserId, @RequestParam(name = "status", required = false) String status) {
		String username = null;
		if ("personal".equals(status) && StringUtils.isBlank(startUserId)) {
//			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//			username = authentication.getName();
		} else {
			username = startUserId;
		}
		startPage();
		List<LinkedHashMap<String, Object>> list = historyService.queryHistoricProcessInstance(currentPage, pageSize, processInstanceName, processDefinitionKey,
				processDefinitionName, processDefinitionVersion, startedAfterDate, finishedBeforeDate, username);
		return getDataTable(list);
	}

	/**
	 * 查询节点执行历史数据分页
	 *
	 * @param currentPage       当前页数
	 * @param pageSize          每页记录数
	 * @param processInstanceId 流程实例ID
	 * @return
	 */
	@GetMapping(path = "/queryHistoricActivityInstance")
	public TableDataInfo queryHistoricActivityInstance(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer currentPage,
			@RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
			@RequestParam(name = "processInstanceId", required = true) String processInstanceId) {
		startPage();
		List<HistoricActivityInstance> list = historyService.queryHistoricActivityInstance(currentPage, pageSize, processInstanceId);
		return getDataTable(list);
	}

	/**
	 * 查询流程实例和节点执行的详细信息历史数据分页
	 *
	 * @param currentPage       当前页数
	 * @param pageSize          每页记录数
	 * @param processInstanceId 流程实例ID
	 * @return
	 */
	@GetMapping(path = "/queryHistoricDetail")
	public TableDataInfo queryHistoricDetail(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer currentPage,
			@RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
			@RequestParam(name = "processInstanceId", required = true) String processInstanceId) {

		startPage();
		List<HistoricDetail> list = historyService.queryHistoricDetail(currentPage, pageSize, processInstanceId);
		return getDataTable(list);
	}

	/**
	 * 查询流程实例已结束的变量历史数据分页
	 *
	 * @param currentPage       当前页数
	 * @param pageSize          每页记录数
	 * @param processInstanceId 流程实例ID
	 * @return
	 * @throws ParseException
	 */
	@GetMapping(path = "/queryHistoricVariableInstance")
	public TableDataInfo queryHistoricVariableInstance(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer currentPage,
			@RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
			@RequestParam(name = "processInstanceId", required = true) String processInstanceId) throws ParseException {
		startPage();
		List<LinkedHashMap<String, Object>> list = historyService.queryHistoricVariableInstance(currentPage, pageSize, processInstanceId);
		return getDataTable(list);
	}

	/**
	 * 查询流程任务历史数据分页
	 *
	 * @param currentPage           当前页数
	 * @param pageSize              每页记录数
	 * @param processDefinitionName 流程定义名称
	 * @param taskName              任务名称
	 * @param startTime             开始时间
	 * @param endTime               结束时间
	 * @return
	 */
	@GetMapping(path = "/queryHistoricTaskInstance")
	public TableDataInfo queryHistoricTaskInstance(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer currentPage,
			@RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
			@RequestParam(name = "processDefinitionName", required = false) String processDefinitionName,
			@RequestParam(name = "taskName", required = false) String taskName, @RequestParam(name = "startTime", required = false) String startTime,
			@RequestParam(name = "endTime", required = false) String endTime) {
		startPage();
		List<LinkedHashMap<String, Object>> list =historyService.queryHistoricTaskInstance(currentPage, pageSize, processDefinitionName, taskName, startTime, endTime);
		return getDataTable(list);
	}

	/**
	 * 删除流程实例历史数据
	 *
	 * @param processInstanceId 流程实例ID
	 * @return
	 */
	@PostMapping(path = "/deleteHistoricProcessInstance")
	public AjaxResult deleteHistoricProcessInstance(@RequestParam(name = "processInstanceId", required = true) String processInstanceId) {
		historyService.deleteHistoricProcessInstance(processInstanceId);
		return AjaxResult.success();
	}

	/**
	 * 删除流程任务历史数据
	 *
	 * @param id 流程任务历史ID
	 * @return
	 */
	@PostMapping(path = "/deleteHistoricTaskInstance")
	public AjaxResult deleteHistoricTaskInstance(@RequestParam(name = "id", required = true) String[] id) {
		historyService.deleteHistoricTaskInstance(id);
		return AjaxResult.success();
	}

}
