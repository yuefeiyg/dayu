package com.blcultra.dao;

import com.blcultra.model.taskResFileInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface taskResFileInfoMapper {
    int deleteByPrimaryKey(String dataid);

    int insert(taskResFileInfo record);

    int insertSelective(taskResFileInfo record);

    taskResFileInfo selectByPrimaryKey(String dataid);

    int updateByPrimaryKeySelective(taskResFileInfo record);

    int updateByPrimaryKey(taskResFileInfo record);

    List<String> getResultDataId(String taskId);

    List<Map<String, Object>> getDatasByTaskId(String taskId);

    Map<String, Object> getDataByTaskIdAndDataId(Map<String, String> map);
    //获取通过的任务文件
    List<Map<String, Object>> getFinalDatasByTaskId(String taskid);

    int updateToDeleteByPrimaryKey(taskResFileInfo record);

    int updateToDeleteStateByTaskIds(List<String> idList) throws Exception;
}