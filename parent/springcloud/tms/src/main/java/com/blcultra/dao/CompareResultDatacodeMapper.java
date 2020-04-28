package com.blcultra.dao;

import com.blcultra.model.CompareResultDatacode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CompareResultDatacodeMapper {


    Map<String,Object> getDatacodeFilePath(Map<String, Object> param);

    int insertDatacodes(Map<String, Object> map);

    int batchInsert(List<Map<String, Object>> list);

    int deleteByPrimaryKey(String resultid);

    int insert(CompareResultDatacode record);

    int insertSelective(CompareResultDatacode record);

    CompareResultDatacode selectByPrimaryKey(String resultid);

    int updateByPrimaryKeySelective(CompareResultDatacode record);

    int updateByPrimaryKey(CompareResultDatacode record);
}