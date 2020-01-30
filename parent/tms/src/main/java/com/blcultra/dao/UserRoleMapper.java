package com.blcultra.dao;

import com.blcultra.model.Role;
import com.blcultra.model.UserRoleKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserRoleMapper {

    Map<String,Object> getUserRoleDetailsByUserid(String userid);

    int deleteRoleByUserid(String userid);

    //根据用户id获取用户角色信息
    UserRoleKey getUserRoleInfoByUserid(String userid);
    //批量新增用户--角色关联记录
    int batchInsert(List<UserRoleKey> list);

    int deleteByPrimaryKey(UserRoleKey key);

    int insert(UserRoleKey record);

    int insertSelective(UserRoleKey record);
}