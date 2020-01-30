package com.blcultra.service;

import java.util.Map;

public interface TaskInfoQueryService {

    String getPublicTasks(int pageNum, int pageSize, String keyword, Map<String, String> searchConditions);

    String getMyTasks(int pageNum, int pageSize, String keyword, Map<String, String> searchConditions);

    String getRelatedTasks(int pageNum, int pageSize, String keyword, Map<String, String> searchConditions);

    /**
     * 进入标注页面获取任务详情
     * @param taskid
     * @return
     */
    String getTaskInfo(String taskid);

}
