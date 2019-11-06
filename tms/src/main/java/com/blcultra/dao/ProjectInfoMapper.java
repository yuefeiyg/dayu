package com.blcultra.dao;

import com.blcultra.model.ProjectInfo;
import com.blcultra.model.ProjectInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProjectInfoMapper {

    List<Map<String,Object>>  getProjects();

    //根据项目所有者projectowner和身为pmanager的管理的projectid列表查询对应项目列表信息
    List<Map<String,Object>> getProjectInfosByProjectOwner(Map<String, Object> params);

    ProjectInfo getProjectInfoByProjectName(String projectname);

    //根据关键词模糊查询项目列表
    List<Map<String,Object>> searchProjectInfo(Map<String, Object> param);
    //条数
    int searchProjectInfoCounts(Map<String, Object> param);
    //获取项目详情
    ProjectInfoDto getProjectInfoByProjectId(@Param(value = "projectid") String projectid);

    //根据项目id列表，批量删除项目信息表中的项目信息
    int batchDeleteProjectsByPids(List<String> list);

    //根据项目id获取项目的当前所有者和历史所有者（projectowner、performerowner）
    List<Map<String,Object>> getProjectOwnerOrPerformerOwnerByPorjectId(String projectid);

    //根据项目名和项目创建人查询项目信息
    ProjectInfo getProjectInfoByPnameAndPowner(Map<String, Object> param);

    //根据当前用户角色和当前用户id获取项目列表信息
    List<Map<String,Object>> getProjectInfoLists(Map<String, Object> param);

    //条数
    int getProjectListsCounts(Map<String, Object> param);

    int deleteByPrimaryKey(String projectid);

    int insert(ProjectInfo record);

    int insertSelective(ProjectInfo record);

    ProjectInfo selectByPrimaryKey(String projectid);

    int updateByPrimaryKeySelective(ProjectInfo record) throws Exception;

    int updateByPrimaryKey(ProjectInfo record);

    //获取简单项目信息
    List<Map<String,Object>> getSimpleProjectInfo(@Param(value = "createuserid") String createuserid);
    //根据项目ID查询属于该项目下的用户列表信息
    List<Map<String,Object>> getUsersByProjectId(String projectid);

    Map<String,Object> getProjectOwnerAndPmanagerByTaskId(String taskid);

    List<String> getProjectidsByTaskids(Map<String, String> param);

    List<Map<String,Object>> getTaskResultDatasInProjectByUser(Map<String, String> param);

    List<Map<String,Object>> wordStatisticOfMembers(Map<String, String> param) throws Exception;

    List<Map<String,Object>> taskStatisticOfMembers(Map<String, String> param) throws Exception;

}