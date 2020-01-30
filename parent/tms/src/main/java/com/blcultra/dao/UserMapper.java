package com.blcultra.dao;

import com.blcultra.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    List<Map<String,Object>> adminGetUsers();

    List<Map<String,Object>> getSearchUserList(Map<String, Object> map);
    int getSearchUserListCounts(Map<String, Object> map);

    List<Map<String,Object>> getUserList(Map<String, Object> map);
    int getUserListCounts(Map<String, Object> map);

    //批量删除用户即批量更新用户状态为不可用，逻辑删除
    int updateUserByIds(List<String> list);

    User selectByLoginName(String username);

    //根据用户id获取用户详细信息包括用户信息和用户角色信息以及所拥有的菜单信息
    User selectUserDetailsByUserId(String userid);

    int deleteByPrimaryKey(String userid);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String userid);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //根据用户名模糊查询用户信息
    List<Map<String,Object>> getLikeUserByUserName(Map<String, Object> map);

    List<Map<String,Object>> getManagers(Map<String, String> map);

    String checkAndgetUserIdByUserNameAndProject(Map<String, String> map);

    List<Map<String,Object>> getAuditorsByTaskId(Map<String, String> taskid);
}