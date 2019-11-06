package com.blcultra.service;

import java.util.Map;

public interface TaskInfoActionService {

    //查询任务详情
    String getTaskInfo(String taskId);

    //批量删除任务
    String deleteTask(String ids);

    //领取任务
    String receiveTask(String taskId);

    //开始任务
    String startTask(String taskId);

    //暂停任务
    String pauseTask(String taskId);

    //批量召回任务
    String recallTask(String ids);

    //批量召回任务
    String invalidateTask(String ids);

    //进入任务
    String enterTask(String taskId);

    //提交任务（非标注任务）
    String commitTask(Map<String, String> task);



}
