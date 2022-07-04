package com.yiruo.activiti.entity;


import java.io.Serializable;
import javax.validation.constraints.NotBlank;


/**
 * 模型信息的实体类
 */
public class ModelEntity implements Serializable {

    private static final long serialVersionUID = 6103596578742231080L;
    private String modelId;// 模型ID
    private String name;// 模型名称
    private String category;// 模型类别
    private String description;// 模型描述


    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
