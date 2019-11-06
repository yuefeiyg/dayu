package com.blcultra.dao;

import com.blcultra.model.TaskInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TaskInfoMapper {
    int deleteByPrimaryKey(String taskid) throws Exception;

    int insert(TaskInfo record) throws Exception;

    int insertSelective(TaskInfo record) throws Exception;

    TaskInfo selectByPrimaryKey(String taskid) throws Exception;

    int updateByPrimaryKeySelective(TaskInfo record) throws Exception;

    int updateByPrimaryKey(TaskInfo record) throws Exception;

    //根据项目id获取任务列表信息
    Map<String,Object> getTaskInfoByProjectId(String projectid);

    //获取已发布任务列表
    List<Map<String,Object>> getPublishedTaskList(Map<String, Object> map);

    //获取待领取任务条数
    int  getPublishedTaskListCounts(Map<String, Object> map);

    //获取个人任务列表
    List<Map<String,Object>> getMyTaskList(Map<String, Object> map);
    //获取个人任务条数
    int getMyTaskListCounts(Map<String, Object> map);

    //获取相关任务任务列表
    List<Map<String,Object>> getRelatedTaskList(Map<String, Object> map);
    //获取相关任务条数
    int  getRelatedTaskListCounts(Map<String, Object> map);

    TaskInfo getTaskByProjectName(@Param(value = "taskname") String taskname, @Param(value = "projcetid") String projcetid, @Param(value = "taskowner") String taskowner);

    Map<String,Object> getTaskInfo(String taskId);

    //批量更新任务状态
    int updateTaskToDeleteStateByIds(List<String> idList) throws Exception;

    //批量更新任务状态
    int updateTaskToInvalidStateByIds(List<String> idList) throws Exception;

    int updateTaskState(Map<String, String> map) throws Exception;

    int updateTaskToReceiveStateByIds(List<String> idList) throws Exception;

    int updateTaskToCloseStateByIds(List<String> idList) throws Exception;

    int updateTaskInfoState(Map<String, Object> map) throws Exception;

    int  updateTaskCostTimeByTaskId(Map<String, Object> map) throws Exception;

    //按照项目维度统计
    List<Map<String,String>> statisticsDimensionProject(Map<String, Object> map);

    //按照任务维度统计
    List<Map<String,String>> statisticsDimensionTask(Map<String, Object> map);

    //按照任务状态维度统计
    List<Map<String,String>> statisticsDimensionTaskState(Map<String, Object> map);

    //按照用户维度统计
    List<Map<String,String>> statisticsDimensionUser(Map<String, Object> map);

    //检索任务列表
    List<Map<String,Object>> searchTask(Map<String, Object> map);

    Map<String,Object> getAnnotationTaskInfo(String taskid);

    //统计时使用
    List<Map<String,Object>> statisticSearchTask(Map<String, Object> map);

    List<Map<String,Object>> pManagerStatisticTaskSearch(Map<String, Object> map);

    List<Map<String,Object>> adminStatisticTaskSearch(Map<String, Object> map);
}