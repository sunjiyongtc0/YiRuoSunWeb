package com.yiruo.activiti.service;


import org.activiti.engine.repository.Model;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.stream.XMLStreamException;

/**
 * 模型信息的业务逻辑接口层
 */
public interface ModelService {

    /**
     * 查询模型分页
     *
     * @param currentPage 当前页数
     * @param pageSize    每页记录数
     * @param name        模型名称
     * @param category    模型类别
     * @param orgId       机构ID
     * @param version     模型版本
     * @return
     */
    List<Model> queryModel(Integer currentPage, Integer pageSize, String name, String category, Integer version);

    /**
     * 获取模型XML
     *
     * @param modelId 模型ID
     * @return
     */
    String getModelResource(String modelId);

    /**
     * 新增模型
     *
     * @param name         模型名称
     * @param category     模型类别
     * @param description  模型描述
     */
    void addModel(String name, String category, String description);

    /**
     * 编辑模型
     *
     * @param modelId      模型ID
     * @param name         模型名称
     * @param category     模型类别
     * @param description  模型描述

     * @throws IOException
     */
    void updateModel(String modelId, String name, String category,  String description) throws IOException;

    /**
     * 复制模型
     *
     * @param modelId 模型ID
     * @throws IOException
     * @throws XMLStreamException
     */
    void copyModel(String modelId) throws IOException, XMLStreamException;

    /**
     * 部署模型
     *
     * @param modelId 模型ID
     * @throws IOException
     * @throws XMLStreamException
     */
    void deployModel(String modelId) throws IOException, XMLStreamException;

    /**
     * 删除模型
     *
     * @param modelId 模型ID
     */
    void deleteModel(String[] modelId);

}
