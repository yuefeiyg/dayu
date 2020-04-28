package com.blcultra.dao;

import com.blcultra.model.AnnotationObjectInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnnotationObjectInfoMapper {
    //根据dataitemid查询AnnotatioinObjectInfo信息列表
    List<Map<String,Object>> getAnnotationDataInfosByDataItemIds(List<String> list);

    //根据objectdataid删除标注记录
    int batchDeleteByObjectDataId(List<String> list) throws Exception;

    int batchDeleteById(List<String> list) throws Exception;

    int batchInsertItems(List<AnnotationObjectInfo> list) throws Exception;

    int deleteByPrimaryKey(String dataitemid) throws Exception;

    int insert(AnnotationObjectInfo record) throws Exception;

    int insertSelective(AnnotationObjectInfo record) throws Exception;

    AnnotationObjectInfo selectByPrimaryKey(String dataitemid);

    int updateByPrimaryKeySelective(AnnotationObjectInfo record) throws Exception;

    int updateByPrimaryKey(AnnotationObjectInfo record) throws Exception;


    int batchInsert(List<Map<String, String>> list) throws Exception;

    /**
     * 查询标注数据
     * @param map
     * @return
     */
    List<Map<String,Object>> getLableDataItemList(Map<String, Object> map);

    /**
     * 图像的标注数据
     * @param map
     * @return
     */
    List<Map<String,Object>> getImgItemList(Map<String, Object> map);

    int deleteByTaskIdAndSourceId(Map<String, String> infos) throws Exception;


}