package com.blcultra.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface LogUserBehaviorMapper {

    int batchInsertLogUserBehavior(List<Map<String, Object>> loginfos);

    int insertLogUseBehaviorSelective(Map<String, Object> loginfo);

}