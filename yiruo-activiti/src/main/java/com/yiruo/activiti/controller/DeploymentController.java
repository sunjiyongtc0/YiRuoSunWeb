package com.yiruo.activiti.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.yiruo.activiti.service.DeploymentService;
import com.yiruo.common.core.controller.BaseController;
import com.yiruo.common.core.domain.AjaxResult;
import com.yiruo.common.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 部署信息的控制层
 * 
 */
@RestController
@RequestMapping("/activiti/deployment")
public class DeploymentController extends BaseController {

	@Autowired
	private DeploymentService deploymentService;

	/**
	 * 查询已部署的模型分页
	 * 
	 * @param currentPage    当前页数
	 * @param pageSize       每页记录数
	 * @param deploymentName 已部署的模型名称
	 * @return
	 */
	@GetMapping(path = "/queryDeployment")
	public TableDataInfo queryDeployment(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer currentPage,
										 @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
										 @RequestParam(name = "deploymentName", required = false) String deploymentName,
										 @RequestParam(name = "category", required = false) String category) {
		startPage();
		List<LinkedHashMap<String, Object>> list = deploymentService.queryDeployment(currentPage, pageSize, deploymentName, category);
		return getDataTable(list);
	}

	/**
	 * 删除已部署的模型
	 * 
	 * @param deploymentId 部署ID
	 * @return
	 */
	@PostMapping(path = "/deleteDeployment")
	public AjaxResult deleteDeployment(@RequestParam(name = "deploymentId", required = true) String[] deploymentId) {
		deploymentService.deleteDeployment(deploymentId);
		return AjaxResult.success();
	}

}
