package com.blcultra.service;

import com.blcultra.dto.ProjectDto;
import com.blcultra.model.ProjectInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 项目信息服务接口
 * Created by sgy05 on 2019/3/6.
 */
public interface ProjectInfoService {

    //新增项目
//    String addProject(String projectname,String projectdesc,String memebers,String note);
    String addProject(ProjectDto projectDto);
    //获取项目列表信息
    String getProjectList(Integer pageNow, Integer pageSize);
    //获取指定项目的信息
    String getProjectInfo(String projectid);
    //编辑指定项目信息
//    String editProjectInfo(ProjectInfo projectInfo, String members);
    String editProjectInfo(ProjectDto projectDto);
    //删除指定项目可批量删除（删除原则为项目下无有效任务）
    String deleteProjectInfo(String projectids);
    //按照关键字检索符合条件的项目
    String searchProjectInfo(Map<String, Object> map);

     // 获取项目下拉列表简单信息接口
    String getSimpleProjectInfo();

    //根据项目id查询用户信息
    String getUsersByProjectId(String projectid);

    /**
     * 按照项目分组下载数据集
     * @param map
     * @return
     */
    String generateDatasByProject(Map<String, String> map);

    String downloadDatasByProject(HttpServletRequest request, HttpServletResponse response, Map<String, String> map);

    /**
     * 统计项目下成员标注的字数
     * @param request
     * @param response
     * @param map
     * @return
     */
    String wordStatisticOfMembers(HttpServletRequest request, HttpServletResponse response, Map<String, String> map);

}
