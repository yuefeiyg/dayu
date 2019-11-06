package com.blcultra.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TaskDictMapper {


//    获取任务字典表数据
    List<Map<String, Object>> getTaskDictData(@Param(value = "parentcode") String parentcode);

    Map<String, Object> getTaskDictCodebyName(String dictdesc);
}