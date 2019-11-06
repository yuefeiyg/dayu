package com.blcultra.dao;

import com.blcultra.model.UserBehaviorLog;
import com.blcultra.model.UserBehaviorLogWithBLOBs;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserBehaviorLogMapper {
    int deleteByPrimaryKey(String logid);

    int insert(UserBehaviorLogWithBLOBs record);

    int insertSelective(UserBehaviorLogWithBLOBs record);

    UserBehaviorLogWithBLOBs selectByPrimaryKey(String logid);

    int updateByPrimaryKeySelective(UserBehaviorLogWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(UserBehaviorLogWithBLOBs record);

    int updateByPrimaryKey(UserBehaviorLog record);
}