package com.blcultra.dao;

import java.util.List;
import java.util.Map;

public interface FileComparisonScriptMapper {

    List<Map<String,Object>> getScriptList();

    int deleteByPrimaryKey(String scriptid);

    int insert(Map<String, Object> record);

    int insertSelective(Map<String, Object> record);

    Map<String,Object> selectByPrimaryKey(String scriptid);

    int updateByPrimaryKeySelective(Map<String, Object> record);

    int updateByPrimaryKey(Map<String, Object> record);
}