package com.yiruo.activiti.controller;

import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import com.yiruo.activiti.entity.ModelEntity;
import com.yiruo.activiti.service.ModelService;
import com.yiruo.common.core.controller.BaseController;
import com.yiruo.common.core.domain.AjaxResult;
import com.yiruo.common.core.page.TableDataInfo;
import net.sf.jsqlparser.util.validation.validator.InsertValidator;
import net.sf.jsqlparser.util.validation.validator.UpdateValidator;
import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 模型信息的控制层
 */
@RestController
@RequestMapping("/activiti/model")
public class ModelController extends BaseController {

    @Autowired
    private ModelService modelService;

    /**
     * 查询模型分页
     *
     * @param currentPage 当前页数
     * @param pageSize    每页记录数
     * @param name        模型名称
     * @param category    模型类别
     * @param version     模型版本
     * @return
     */
    @GetMapping(path = "/queryModel")
    public TableDataInfo queryModel(@RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer currentPage,
                                    @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                    @RequestParam(name = "name", required = false) String name, @RequestParam(name = "category", required = false) String category,
                                    @RequestParam(name = "version", required = false) Integer version) {
        startPage();
        List<Model> list = modelService.queryModel(currentPage, pageSize, name, category,  version);
        return getDataTable(list);
    }

    /**
     * 获取模型XML
     *
     * @param modelId 模型ID
     * @return
     */
    @PostMapping(path = "/getModelResource")
    public AjaxResult getModelResource(@RequestParam(name = "modelId", required = true) String modelId) {
        String msg=modelService.getModelResource(modelId);
        if(msg==null){
            return AjaxResult.success();
        }else{
            return AjaxResult.success(msg);
        }

    }

    /**
     * 新增模型
     *
     * @param modelEntity 模型对象
     * @return
     */
    @PostMapping(path = "/addModel")
    public AjaxResult addModel(@Validated(InsertValidator.class) @RequestBody ModelEntity modelEntity) {
        modelService.addModel(modelEntity.getName(), modelEntity.getCategory(), modelEntity.getDescription());
        return AjaxResult.success();
    }

    /**
     * 编辑模型
     *
     * @param modelEntity 模型对象
     * @return
     * @throws IOException
     */
    @PutMapping(path = "/updateModel")
    public AjaxResult updateModel(@Validated(UpdateValidator.class) @RequestBody ModelEntity modelEntity) throws IOException {
        modelService.updateModel(modelEntity.getModelId(), modelEntity.getName(), modelEntity.getCategory(), modelEntity.getDescription());
        return AjaxResult.success();
    }

    /**
     * 复制模型
     *
     * @param modelId 模型ID
     * @return
     * @throws XMLStreamException
     * @throws IOException
     */
    @PostMapping(path = "/copyModel")
    public AjaxResult copyModel(@RequestParam(name = "modelId", required = true) String modelId) throws IOException, XMLStreamException {
        modelService.copyModel(modelId);
        return AjaxResult.success();
    }

    /**
     * 部署模型
     *
     * @param modelId 模型ID
     * @return
     * @throws XMLStreamException
     * @throws IOException
     */
    @PostMapping(path = "/deployModel")
    public AjaxResult deployModel(@RequestParam(name = "modelId", required = true) String modelId) throws IOException, XMLStreamException {
        modelService.deployModel(modelId);
        return AjaxResult.success();
    }

    /**
     * 删除模型
     *
     * @param modelId 模型ID
     * @return
     */
    @PostMapping(path = "/deleteModel")
    public AjaxResult deleteModel(@RequestParam(name = "modelId", required = true) String[] modelId) {
        modelService.deleteModel(modelId);
        return AjaxResult.success();
    }
}
