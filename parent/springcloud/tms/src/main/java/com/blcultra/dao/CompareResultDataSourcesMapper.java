package com.blcultra.dao;

import com.blcultra.model.CompareResultDataSources;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CompareResultDataSourcesMapper {

    int insertCompareResultDataSoucrce(Map<String, Object> map);

    int batchInsert(List<Map<String, Object>> list);

    int deleteByPrimaryKey(String resultid);

    int insert(CompareResultDataSources record);

    int insertSelective(CompareResultDataSources record);

    CompareResultDataSources selectByPrimaryKey(String resultid);

    int updateByPrimaryKeySelective(CompareResultDataSources record);

    int updateByPrimaryKey(CompareResultDataSources record);
}