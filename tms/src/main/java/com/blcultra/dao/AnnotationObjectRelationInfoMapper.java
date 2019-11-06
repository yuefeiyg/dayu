package com.blcultra.dao;

import com.blcultra.model.AnnotationObjectRelationInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnnotationObjectRelationInfoMapper {

    List<Map<String,Object>> getRelateLableDatasByRelationIds(List<String> list);

    int batchDeleteRelationsByObjectDataId(List<String> list) throws Exception;

    //批量删除关联关系信息
    int batchDeleteById(List<String> list) throws Exception;
    //批量插入
    int batchInsertRelations(List<AnnotationObjectRelationInfo> list) throws Exception;

    int deleteByPrimaryKey(String relationdataid) throws Exception;

    int insert(AnnotationObjectRelationInfo record) throws Exception;

    int insertSelective(AnnotationObjectRelationInfo record) throws Exception;

    AnnotationObjectRelationInfo selectByPrimaryKey(String relationdataid);

    int updateByPrimaryKeySelective(AnnotationObjectRelationInfo record) throws Exception;

    int updateByPrimaryKey(AnnotationObjectRelationInfo record) throws Exception;

    int batchInsert(List<Map<String, String>> list) throws Exception;
    /**
     * 查询标注数据
     * @param map
     * @return
     */
    List<Map<String,Object>> getRelateLableDataItemList(Map<String, Object> map);

    int deleteByTaskIdAndSourceId(Map<String, String> infos) throws Exception;
    //根据任务id、sourceid、dataid（即contentid）关联查询关系标注数据记录
    List<Map<String,Object>> getRelateLableDataItems(Map<String, Object> map);
    //根据任务id和sourceid查询图片标注的关联关系记录列表
    List<Map<String,Object>>  getPicRelateLableDataItems(Map<String, Object> querymap);

}