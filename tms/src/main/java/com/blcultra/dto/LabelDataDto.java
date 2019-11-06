package com.blcultra.dto;


import com.blcultra.model.AnnotationObjectInfo;
import com.blcultra.model.AnnotationObjectRelationInfo;
import com.blcultra.model.TaskReviewInfo;

import java.util.List;

/**
 * 修改过的或者删除的item对象的id
 * 单一标注对象数据的集合，如某一句话多个词的标注信息，每一个词的标注信息是一条记录即集合中的一个元素
 * Created by sgy05 on 2019/2/15.
 */
public class LabelDataDto {

    private  String savetype;

    private  String taskid;//任务id

    private String processednumber;//已处理字数

    private String costtime;//标注耗时

    private String tasktype;//任务类型：标注or审核

    private int flag;//保存标注数据时，若flag为 1 则删除列表中的id为objectdataid，若flag为 0 则走V2.0逻辑

    private List<String> deleteItemIds;//单个item的对象记录的主键id

    private List<String> deleteRelationIds;//单个关系对象的记录主键id

    private List<AnnotationObjectInfo> insertItems;//新增的item信息列表

    private List<AnnotationObjectRelationInfo> insertRelations;//新增的relation信息列表

    private List<TaskReviewInfo> taskReviewInfos;//若为审核任务，此字段存储审核员的审核评语信息

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getProcessednumber() {
        return processednumber;
    }

    public void setProcessednumber(String processednumber) {
        this.processednumber = processednumber;
    }

    public String getCosttime() {
        return costtime;
    }

    public void setCosttime(String costtime) {
        this.costtime = costtime;
    }

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public List<TaskReviewInfo> getTaskReviewInfos() {
        return taskReviewInfos;
    }

    public void setTaskReviewInfos(List<TaskReviewInfo> taskReviewInfos) {
        this.taskReviewInfos = taskReviewInfos;
    }

    public List<String> getDeleteItemIds() {
        return deleteItemIds;
    }

    public void setDeleteItemIds(List<String> deleteItemIds) {
        this.deleteItemIds = deleteItemIds;
    }

    public List<String> getDeleteRelationIds() {
        return deleteRelationIds;
    }

    public void setDeleteRelationIds(List<String> deleteRelationIds) {
        this.deleteRelationIds = deleteRelationIds;
    }

    public List<AnnotationObjectInfo> getInsertItems() {
        return insertItems;
    }

    public void setInsertItems(List<AnnotationObjectInfo> insertItems) {
        this.insertItems = insertItems;
    }

    public List<AnnotationObjectRelationInfo> getInsertRelations() {
        return insertRelations;
    }

    public void setInsertRelations(List<AnnotationObjectRelationInfo> insertRelations) {
        this.insertRelations = insertRelations;
    }

    public String getSavetype() {
        return savetype;
    }

    public void setSavetype(String savetype) {
        this.savetype = savetype;
    }

    @Override
    public String toString() {
        return "LabelDataDto{" +
                "taskid='" + taskid + '\'' +
                ", processednumber='" + processednumber + '\'' +
                ", tasktype='" + tasktype + '\'' +
                ", deleteItemIds=" + deleteItemIds +
                ", deleteRelationIds=" + deleteRelationIds +
                ", insertItems=" + insertItems +
                ", insertRelations=" + insertRelations +
                ", taskReviewInfos=" + taskReviewInfos +
                '}';
    }
}
