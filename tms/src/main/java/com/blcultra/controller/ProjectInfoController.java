package com.blcultra.controller;

import com.blcultra.dto.ProjectDto;
import com.blcultra.service.ProjectInfoService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * 项目信息相关服务
 * Created by sgy05 on 2019/3/6.
 */
@RestController
@RequestMapping("/project/")
public class ProjectInfoController {
    private static final Logger log = LoggerFactory.getLogger(ProjectInfoController.class);
    @Autowired
    private ProjectInfoService projectInfoService;
    /**
     * 新增项目
     * @return
     */
//    @PostMapping(value = "add",produces="application/json;charset=UTF-8")
//    @RequiresAuthentication
//    @RequiresRoles(value={"admin","manager"},logical= Logical.OR)
//    public String addProject(@RequestParam (value = "projectname" ,required =true) String projectname,
//                             @RequestParam(value = "projectdesc",required = false) String projectdesc,
//                             @RequestParam(value = "memebers",required = false) String memebers,
//                             @RequestParam(value = "note",required = false) String note) {
//        log.info("addProject 添加项目入参，projectname：{}  ， projectdesc：{} ， memebers：{},  note:{} "
//                                    ,projectname,projectdesc,memebers,note);
//        String res = projectInfoService.addProject(projectname,projectdesc,memebers,note);
//        return res;
//    }
    @PostMapping(value = "add",produces="application/json;charset=UTF-8")
    @RequiresAuthentication
    @RequiresRoles(value={"admin","manager"},logical= Logical.OR)
    public String addProject(@RequestBody  ProjectDto projectDto) {
        log.info("addProject 添加项目入参",projectDto.toString());
        String res = projectInfoService.addProject(projectDto);
        return res;
    }


    /**
     * 获取当前用户所属的所有项目信息列表，包括项目的操作列表
     * @param pageNow
     * @param pageSize
     * @return
     */
    @GetMapping(value = "list",produces="application/json;charset=UTF-8")
    @RequiresAuthentication
//    @RequiresRoles(value={"admin","manager"},logical= Logical.OR)
    public String projectlist(@RequestParam(value = "pageNow",required = false,defaultValue = "1") Integer pageNow,
                              @RequestParam (value = "pageSize" ,required =false,defaultValue = "5" ) Integer pageSize) {
        String res = projectInfoService.getProjectList(pageNow,pageSize);
        return res;
    }
    /**
     *获取指定项目的信息
     * @return
     */
    @GetMapping(value = "info",produces="application/json;charset=UTF-8")
    @RequiresAuthentication
//    @RequiresRoles(value={"admin","manager"},logical= Logical.OR)
    public String projectInfo(@RequestParam(value = "projectid",required = true) String projectid) {
        String res = projectInfoService.getProjectInfo(projectid);
        return res;
    }

    /**
     * 编辑指定项目信息
     * @return
     */
    @PostMapping(value = "edit",produces="application/json;charset=UTF-8")
    @RequiresAuthentication
//    @RequiresRoles(value={"admin","manager"},logical= Logical.OR)
    public String editProject(@RequestBody  ProjectDto projectDto) {

        String res = projectInfoService.editProjectInfo(projectDto);
        return res;
    }

    /**
     * 删除指定项目可批量删除（删除原则为项目下无任何任务）
     * @param projectids
     * @return
     */
    @PostMapping(value = "delete",produces="application/json;charset=UTF-8")
    @RequiresAuthentication
    @RequiresRoles(value={"admin","manager"},logical= Logical.OR)
    public String deleteProjectInfo(@RequestParam(value = "projectids",required = true) String projectids) {
        String res = projectInfoService.deleteProjectInfo(projectids);
        return res;
    }

    /**
     * 按照关键字检索符合条件的项目
     * @param keyword
     * @return
     */
    @GetMapping(value = "search",produces="application/json;charset=UTF-8")
    @RequiresAuthentication
//    @RequiresRoles(value={"admin","manager"},logical= Logical.OR)
    public String searchProject(@RequestParam(value = "keyword",required = false) String keyword,
                                @RequestParam(value = "projectname",required = false) String projectname,
                                @RequestParam(value = "note",required = false) String note,
                                @RequestParam(value = "starttime",required = false) String starttime,
                                @RequestParam(value = "endtime",required = false) String endtime,
                                @RequestParam(value = "pageNow",required = false,defaultValue = "1") Integer pageNow,
                                @RequestParam (value = "pageSize" ,required =false,defaultValue = "5" ) Integer pageSize) {
        Map<String,Object> map = new HashMap<>(8);
        map.put("keyword",keyword);
        map.put("projectname",projectname);
        map.put("note",note);
        map.put("starttime",starttime);
        map.put("endtime",endtime);
        map.put("pageNow",pageNow);
        map.put("pageSize",pageSize);
        String res = projectInfoService.searchProjectInfo(map);
        return res;
    }

    /**
     * 获取项目下拉列表
     * @param request
     * @param response
     * @return
     */
    @GetMapping(value = "/getSimpleProjectInfos",produces="application/json;charset=UTF-8")
    @ResponseBody
    @RequiresAuthentication
    public String getSimpleProjectInfos(HttpServletRequest request, HttpServletResponse response) {
        log.info("getSimpleProjectInfo ");
        String res = projectInfoService.getSimpleProjectInfo();
        return res;
    }

    /**
     * 根据项目ID查询项目组成员列表：在创建任务时，
     * 选择经办人即任务执行人时，需要有该项目组成员下拉列表
     * @param request
     * @param response
     * @param projectid
     * @return
     */
    @GetMapping(value = "/getUsersByProjectId",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String getUsersByProjectId(HttpServletRequest request, HttpServletResponse response,  String projectid) {
        log.info("☆☆☆TaskController   getUsersByProjectId 根据项目id查询经办人方法  ☆☆☆");
        String res = projectInfoService.getUsersByProjectId(projectid);
        return res;
    }

    /**
     * 按照项目分组下载数据集
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/generateDatasByProject",produces = "application/json;charset=UTF-8")
    public String generateDatasByProject(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam (value = "userid" ,required =false) String userid,
                                         @RequestParam (value = "ids" ,required =false) String ids,
                                         @RequestParam (value = "starttime" ,required =false) String starttime,
                                         @RequestParam (value = "endtime" ,required =false) String endtime
    ){
        Map<String,String> datasetmap = new HashMap<>(5);
        if(null != userid) datasetmap.put("userid",userid);
        if(null != ids) datasetmap.put("ids",ids);
        if(null != starttime) datasetmap.put("starttime",starttime);
        if(null != endtime) datasetmap.put("endtime",endtime);
        return projectInfoService.generateDatasByProject(datasetmap);
    }


    /**
     * 按照项目分组下载数据集
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/downloadDatasByProject",produces = "application/json;charset=UTF-8")
    public String downloadDatasByProject(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam (value = "filepath" ,required =false) String filepath
    ){
        Map<String,String> datasetmap = new HashMap<>(5);
        if(null != filepath) datasetmap.put("filepath",filepath);
        return projectInfoService.downloadDatasByProject(request,response,datasetmap);
    }

    /**
     * 统计项目下成员标注的字数
     * @param request
     * @param response
     * @param ids 选择的项目id
     * @param starttime 统计的开始时间
     * @param endtime 统计的结束时间
     * @return
     */
    @RequiresAuthentication
    @RequestMapping(value = "/wordStatisticOfMembers",produces = "application/json;charset=UTF-8")
    public String wordStatisticOfMembers(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam (value = "ids") String ids,
                                         @RequestParam (value = "starttime") String starttime,
                                         @RequestParam (value = "endtime" ) String endtime
    ){
        Map<String,String> datasetmap = new HashMap<>(5);
        datasetmap.put("ids",ids);
        datasetmap.put("starttime",starttime);
        datasetmap.put("endtime",endtime);
        return projectInfoService.wordStatisticOfMembers(request,response,datasetmap);
    }

}
