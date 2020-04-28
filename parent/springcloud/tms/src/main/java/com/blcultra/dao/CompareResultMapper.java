package com.blcultra.dao;

import com.blcultra.model.CompareResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CompareResultMapper {

    List<Map<String,Object>> getResultInfosByExcutor(Map<String, Object> map);

    int countResults(Map<String, Object> map);

    List<Map<String,Object>> batchSelectCompareResultsByResultids(List<String> list);

    int deleteByPrimaryKey(String resultid);

    int insert(CompareResult record);

    int insertSelective(CompareResult record);

    CompareResult selectByPrimaryKey(String resultid);

    int updateByPrimaryKeySelective(CompareResult record);

    int updateByPrimaryKey(CompareResult record);
}