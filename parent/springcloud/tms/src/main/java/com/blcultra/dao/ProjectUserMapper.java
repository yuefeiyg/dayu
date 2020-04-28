package com.blcultra.dao;

import com.blcultra.model.ProjectUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProjectUserMapper {
    //根据用户id查询该用户是否为pmanager及projectID信息
    List<Map<String,Object>> getPUInfoByUserId(String userid);

    //批量插入项目用户列表
    int batchInsertProjcetUser(List<ProjectUser> list) throws Exception;

    List<ProjectUser> getProjectUserByUserId(String userid);

    //根据项目的id列表删除项目--用户关联表中的关联信息
    int deleteByProjectIds(List<String> list);

    int deleteByProjectId(String projectid);

    int insertProjectUserList(List<ProjectUser> list);

    int deleteByPrimaryKey(Integer id);

    int insert(ProjectUser record);

    int insertSelective(ProjectUser record);

    ProjectUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProjectUser record);

    int updateByPrimaryKey(ProjectUser record);

    List<Map<String,Object>> getProjectUsersByProjectId(String projectid);

    int deleteByProjectid(String projectid) throws Exception;

    int checkPmanager(String userid);

    String getProjectAuditorByTaskId(String taskid);
    //根据用户id查询所在项目id列表
    List<Map<String,Object>> getProjectIdsByUserId(String member);

    List<Map<String,Object>> getUserInfosByProjectids(List<String> list);

    List<Map<String,Object>> getUserInfosByProjectid(String projectid);
}