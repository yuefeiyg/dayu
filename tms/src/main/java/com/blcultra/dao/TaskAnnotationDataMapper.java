package com.blcultra.dao;

import com.blcultra.model.AnnotationObjectInfo;
import com.blcultra.model.TaskAnnotationData;
import javafx.beans.binding.ObjectBinding;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TaskAnnotationDataMapper {

    //根据任务id查询QA标注数据
    List<Map<String,Object>> getAnnotationDataByTaskId(String taskid);

    Map<String,Object> getSourceidAndDataIdByObjectdataid(String objectdataid);

    List<AnnotationObjectInfo> getPicAnnotationDataByTaskIdSourceId(Map<String, Object> param);

    int deleteByPrimaryKey(String objectdataid) throws Exception;

    int insert(TaskAnnotationData record) throws Exception;

    int insertSelective(TaskAnnotationData record) throws Exception;

    TaskAnnotationData selectByPrimaryKey(String objectdataid);

    int updateByPrimaryKeySelective(TaskAnnotationData record) throws Exception;

    int updateByPrimaryKey(TaskAnnotationData record) throws Exception;

    int batchInsert(List<Map<String, String>> taskAnnotationDatas) throws Exception;

    int deleteByTaskIdAndSourceId(Map<String, String> infos) throws Exception;

    List<Map<String,Object>> getSourceIdAndSourceNameByTaskId(String taskid);

    List<AnnotationObjectInfo> getAnnotationObjectInfos(Map<String, Object> param);
    //查询任务下的文件id
    List<String> getSourceIdsByTaskId(String taskid);

    List<Map<String,Object>> getSourceinfoByTaskId(String taskid);

    List<Map<String,Object>> selectAnnotationDataByTaskid(Map<String, Object> param);

}