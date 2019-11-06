package com.blcultra.dao;

import com.blcultra.model.Action;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ActionMapper {

    List<String> getActionMoudles();
    List<Map<String,Object>> getActionsByMoudle(String moudle);

    int deleteByPrimaryKey(String actionid);

    int insert(Action record);

    int insertSelective(Action record);

    Action selectByPrimaryKey(String actionid);

    int updateByPrimaryKeySelective(Action record);

    int updateByPrimaryKey(Action record);
}