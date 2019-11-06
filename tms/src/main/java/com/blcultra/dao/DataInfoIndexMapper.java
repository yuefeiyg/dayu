package com.blcultra.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataInfoIndexMapper {

    /**
     * 批量新增数据文本入库
     * @param datainfos
     * @return
     */
    int addDataInfoIndexBatch(List<Map<String, Object>> datainfos) throws Exception;

    /**
     * 根据任务id或文本id查询索引
     * @param map
     * @return
     */
    List<Map<String, Object>> getDataIndex(Map<String, Object> map) throws Exception;

}