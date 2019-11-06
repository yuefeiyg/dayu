package com.blcultra.dao;

import com.blcultra.model.TaskReviewInfo;

import java.util.List;
import java.util.Map;

public interface TaskReviewInfoMapper {
    //批量插入评语信息
    int batchInserts(List<TaskReviewInfo> taskReviewInfos) throws Exception;

    int insert(TaskReviewInfo record) throws Exception;

    int insertSelective(TaskReviewInfo record) throws Exception;

    TaskReviewInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TaskReviewInfo record) throws Exception;

    int updateByPrimaryKey(TaskReviewInfo record) throws Exception;

    List<Map<String, String>> getDatasByTaskId(String taskId);

    int batchInsert(List<Map<String, String>> taskReviewInfos) throws Exception;
}