package com.blcultra.dao;


import com.blcultra.model.AnnotationOtherObjectInfo;

import java.util.List;
import java.util.Map;

public interface AnnotationOtherObjectInfoMapper {

    int batchDeleteByObjectDataId(List<String> list) throws Exception;

    int batchDeleteById(List<String> list) throws Exception;

    int batchDeleteByItemId(List<String> list) throws Exception;

    int deleteByTaskIdAndSourceId(Map<String, String> infos) throws Exception;

    int batchInsertItems(List<AnnotationOtherObjectInfo> list) throws Exception;

    int batchUpdateItmes(List<AnnotationOtherObjectInfo> list) throws Exception;

    int batchInsert(List<Map<String, String>> list) throws Exception;

    int deleteByPrimaryKey(String dataitemid) throws Exception;

    int insert(AnnotationOtherObjectInfo record);

    int insertSelective(AnnotationOtherObjectInfo record);

    int updateByPrimaryKeySelective(AnnotationOtherObjectInfo record);

    int updateByPrimaryKey(AnnotationOtherObjectInfo record);

    /**
     * 查询标注数据
     * @param map
     * @return
     */
    List<Map<String,Object>> getLableDataItemList(Map<String, Object> map);

    List<Map<String,Object>> getItemsByLabel(Map<String, Object> map);

    List<Map<String,Object>> getObjectItemListByTaskid(Map<String, Object> map);

    Map<String,Object> getObjectItemByIndexid(Map<String, Object> map);


}