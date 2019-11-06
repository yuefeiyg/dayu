package com.blcultra.dao;

import com.blcultra.model.DataContent;
import com.blcultra.model.DataContentWithBLOBs;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataContentMapper {
    int deleteByPrimaryKey(String contentid) throws Exception;

    int insertSelective(Map<String, Object> record) throws Exception;

    /**
     * 批量新增数据文本入库
     * @param datainfos
     * @return
     */
    int addDataContentBatch(List<Map<String, Object>> datainfos) throws Exception;

    DataContentWithBLOBs selectByPrimaryKey(String contentid);

    int updateByPrimaryKeySelective(DataContentWithBLOBs record) throws Exception;

    int updateByPrimaryKey(DataContent record) throws Exception;

    /**
     * 查询获取每一段的对应的句子集合
     * @param requestmap
     * @return
     */
    List<Map<String,Object>> getContents(Map<String, Object> requestmap);

    List<DataContentWithBLOBs> getContentDataByDataId(String dataid);

    int batchInsertDataContent(List<DataContentWithBLOBs> list) throws Exception;


    Map<String,Object> getDataByIndexId(Map<String, Object> map);

}