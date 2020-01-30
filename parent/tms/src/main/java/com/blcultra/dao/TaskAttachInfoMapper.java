package com.blcultra.dao;

import com.blcultra.model.TaskAttachInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TaskAttachInfoMapper {
    int deleteByPrimaryKey(String attachmentid) throws Exception;

    int insert(TaskAttachInfo record) throws Exception;

    int insertSelective(TaskAttachInfo record) throws Exception;

    TaskAttachInfo selectByPrimaryKey(String attachmentid);

    int updateByPrimaryKeySelective(TaskAttachInfo record) throws Exception;

    int updateByPrimaryKey(TaskAttachInfo record) throws Exception;

    int batchInsert(List<Map<String, String>> taskAttachInfos) throws Exception;

    List<Map<String, Object>> getDatasByTaskId(String taskId);

    List<String> getDataIdsByTaskId(String taskId);

    List<String> getTypeDataByTaskId(String taskId);

    int deleteByTaskIdAndAttachmentid(Map<String, String> infos) throws Exception;

    Map<String, Object> getDataByTaskIdAndDataId(Map<String, String> map);
}