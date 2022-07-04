package com.yiruo.activiti.entity;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;

/**
 * 请假信息的实体类
 */
public class Leave implements Serializable {

    private static final long serialVersionUID = 7706521973024688277L;
    private String processDefinitionId;// 流程定义ID
    private String candidate;// 审批人员
//    private Date startTime;// 开始时间
//    private Date endTime;// 结束时间
    private Long startTime;// 开始时间
    private Long endTime;// 结束时间
    private String description;// 备注
    private JSONArray customForm;// 自定义字段

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

//    public Date getStartTime() {
//        return startTime;
//    }
//
//    public void setStartTime(Date startTime) {
//        this.startTime = startTime;
//    }
//
//    public Date getEndTime() {
//        return endTime;
//    }
//
//    public void setEndTime(Date endTime) {
//        this.endTime = endTime;
//    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JSONArray getCustomForm() {
        return customForm;
    }

    public void setCustomForm(JSONArray customForm) {
        this.customForm = customForm;
    }

}
