package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.*;
import com.blcultra.dao.*;
import com.blcultra.dto.ProjectDto;
import com.blcultra.exception.ExceptionUtil;
import com.blcultra.exception.ServiceException;
import com.blcultra.model.*;
import com.blcultra.service.ProjectInfoService;
import com.blcultra.service.core.ProjectAuxiliaryCalculation;
import com.blcultra.service.core.ProjectDataUtil;
import com.blcultra.service.core.UserServiceUtil;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.JsonModel;
import com.blcultra.support.Page;
import com.blcultra.support.ReturnCode;
import com.dayu.util.DateFormatUtil;
import com.dayu.util.FileUtil;
import com.dayu.util.StringUtil;
import com.dayu.util.ZipFileUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;
/**
 * 项目信息服务接口
 * Created by sgy05 on 2019/3/6.
 */
@Service("projectInfoService")
@Transactional
public class ProjectInfoServiceImpl implements ProjectInfoService{
    private static final Logger log = LoggerFactory.getLogger(ProjectInfoServiceImpl.class);
    @Autowired
    private ProjectInfoMapper projectInfoMapper;
    @Autowired
    private ProjectUserMapper projectUserMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProjectHisOwnerMapper projectHisOwnerMapper;
    @Autowired
    private TaskInfoMapper taskInfoMapper;
    @Autowired
    private AuditorPerformerMapper auditorPerformerMapper;
    @Autowired
    private UserServiceUtil userServiceUtil;
    @Autowired
    private ProjectDataUtil projectDataUtil;
    /**
     * 新增项目
     * 1）创建项目时，项目成员为非必填项，也就是允许项目只有管理者一个成员。
     * @return
     */
//    @Override
//    public String addProject(String projectname, String projectdesc, String memebers, String note) {
//        String res = null;
//        try {
//            String token = SecurityUtils.getSubject().getPrincipals().toString();
//            String userid = JWTUtil.getUserId(token);
//
//            Map<String,Object> param = new HashMap<>(2);
//            param.put("projectname",projectname);
//            param.put("projectowner",userid);
//            log.info("getProjectInfoByPnameAndPowner 方法参数：",param.toString());
//            ProjectInfo projectinfo = projectInfoMapper.getProjectInfoByPnameAndPowner(param);
//            if (projectinfo != null){
//                JsonModel jm = new JsonModel(false, Messageconstant.BUSINESS_PROJECT_ALREADYEXISTS,Messageconstant.REQUEST_FAILED_CODE,null);
//                return JSON.toJSONString(jm);
//            }
//            ProjectInfo p = new ProjectInfo();
//            String projectid = StringUtil.getUUID();
//            p.setProjectid(projectid);
//            p.setProjectname(projectname);
//            p.setProjectowner(userid);
//            p.setProjectdesc(projectdesc);
//            p.setProjectstate("006001");//默认激活可用
//            p.setNote(note);
//            p.setCreatetime(DateFormatUtil.DateFormat());
//            projectInfoMapper.insertSelective(p);
//            //保存添加的项目成员
//            if (!"".equals(memebers) && null !=memebers){
//                String[] uids = memebers.split(",");
//                List<String> uidlist = Arrays.asList(uids);
//                List<ProjectUser> list = new ArrayList();
//                for (String memeber:uidlist){
//                    ProjectUser projectUser = new ProjectUser();
//                    projectUser.setProjectid(projectid);
//                    projectUser.setMember(memeber);
//                    list.add(projectUser);
//                }
//                //项目创建人应该纳入到本项目的项目组成员中2018-12-13 09:00
//                ProjectUser pu = new ProjectUser();
//                pu.setMember(userid);
//                pu.setProjectid(projectid);
//                list.add(pu);
//                projectUserMapper.insertProjectUserList(list);
//            }
//            JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(ActionEnum.ADD.getValue(),Messageconstant.REQUEST_SUCCESSED_CODE), Messageconstant.REQUEST_SUCCESSED_CODE ,null);
////            JsonModel jm = new JsonModel(true, "新增项目成功", Messageconstant.REQUEST_SUCCESSED_CODE ,null);
//            return JSON.toJSONString(jm);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(ActionEnum.ADD.getValue(),Messageconstant.REQUEST_FAILED_CODE), Messageconstant.REQUEST_FAILED_CODE ,null);
////        JsonModel jm = new JsonModel(false, "新增项目失败", Messageconstant.REQUEST_FAILED_CODE ,null);
//        return JSON.toJSONString(jm);
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addProject(ProjectDto projectDto) {
        String res = null;
        try {
            String projectname = projectDto.getProjectname();
            String projectdesc = projectDto.getProjectdesc();
            String note = projectDto.getNote();
            String pmanager = projectDto.getPmanager();
            List<Map<String, String>> auditorperformers = projectDto.getAuditorperformers();
            String projectid = StringUtil.getUUID();

            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userid = JWTUtil.getUserId(token);

            Map<String,Object> param = new HashMap<>(2);
            param.put("projectname",projectname);
            param.put("projectowner",userid);
            log.info("getProjectInfoByPnameAndPowner 方法参数：",param.toString());
            ProjectInfo projectinfo = projectInfoMapper.getProjectInfoByPnameAndPowner(param);
            if (projectinfo != null){
                JsonModel jm = new JsonModel(false, Messageconstant.BUSINESS_PROJECT_ALREADYEXISTS,Messageconstant.REQUEST_FAILED_CODE,null);
                return JSON.toJSONString(jm);
            }
            ProjectInfo p = new ProjectInfo();
            p.setProjectid(projectid);
            p.setProjectname(projectname);
            p.setProjectowner(userid);
            p.setProjectdesc(projectdesc);
            p.setProjectstate("006001");//默认激活可用
            p.setNote(note);
            p.setCreatetime(DateFormatUtil.DateFormat());
            //新增项目信息
            projectInfoMapper.insertSelective(p);

            List<ProjectUser> pulist = new ArrayList<>();
            //审核人与被审核人关系信息
            Set<String> pset = new HashSet<>();
            for (Map<String,String> ap:auditorperformers){
                String auditor = ap.get("auditor");
                String performers = ap.get("performers");

                String[] ids = performers.split(",");
                List<String> performerids = Arrays.asList(ids);
                List<AuditorPerformer> aps = new ArrayList<>();
                for (String performerid:performerids){
                    AuditorPerformer auditorPerformer = new AuditorPerformer();
                    auditorPerformer.setAuditorid(auditor);
                    auditorPerformer.setPerformerid(performerid);
                    auditorPerformer.setProjectid(projectid);
                    aps.add(auditorPerformer);//审核人_被审核人关联表信息

                    pset.add(performerid);
                }
                auditorPerformerMapper.batchInsert(aps);

                ProjectUser pu = new ProjectUser();
                pu.setMember(auditor);
                pu.setProjectid(projectid);
                pu.setProlekey(Common.BUSINESS_ROLE_AUDITOR);//业务角色为审核人
                pulist.add(pu);
            }
            //添加项目管理者
            ProjectUser pu = new ProjectUser();
            pu.setMember(pmanager);
            pu.setProjectid(projectid);
            pu.setProlekey(Common.BUSINESS_ROLE_PMANAGER);//业务角色为项目管理者
            pulist.add(pu);
            //其余项目成员
            if(null != projectDto.getMembers()){
                String[] members = projectDto.getMembers().split(",");
                for(String member : members){
                    pset.add(member);
                }
            }
//            pset.add(userid);
            //项目成员
            for (String pid:pset){
                ProjectUser pru = new ProjectUser();
                pru.setMember(pid);
                pru.setProjectid(projectid);
                pru.setProlekey(Common.BUSINESS_ROLE_MEMBER);//业务角色为普通成员
                pulist.add(pru);
            }
            //批量插入项目用户列表
            projectUserMapper.batchInsertProjcetUser(pulist);
            JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(ActionEnum.ADD.getValue(),Messageconstant.REQUEST_SUCCESSED_CODE), Messageconstant.REQUEST_SUCCESSED_CODE ,null);
            return JSON.toJSONString(jm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(ActionEnum.ADD.getValue(),Messageconstant.REQUEST_FAILED_CODE), Messageconstant.REQUEST_FAILED_CODE ,null);
        return JSON.toJSONString(jm);
    }
    /**
     * 获取项目列表信息接口
     * @param pageNow   当前页码
     * @param pageSize   每页显示条数
     * @return
     */
    @Override
    public String getProjectList(Integer pageNow, Integer pageSize) {
        String res = null;
        try {
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userid = JWTUtil.getUserId(token);

            User user = userMapper.selectUserDetailsByUserId(userid);
            Set<Role> roles = user.getRoles();
            boolean adminFlag = false;
            boolean managerFlag = false;
            for (Role role :roles){
                if(role.getRolekey().equals("admin")){
                    adminFlag = true;
                }
                if(role.getRolekey().equals("manager")){
                    managerFlag = true;
                }
            }
            List<ProjectUser> pus = projectUserMapper.getProjectUserByUserId(userid);
            List<String> ids = new ArrayList<>();
            boolean pmanagerFlag = false;
            for (ProjectUser pu:pus){
                String projectid = pu.getProjectid();
                String prolekey = pu.getProlekey();
                if (null != prolekey && prolekey.equals(Common.BUSINESS_ROLE_PMANAGER)){
                    pmanagerFlag=true;
                    ids.add(projectid);
                }
            }

            Map<String,Object> param = new HashMap<>();

            if (!adminFlag && !managerFlag && pmanagerFlag){
                param.put("role","pmanager");
//                param.put("idlist",ids);
                param.put("userid",userid);
            }
            param.put("projectowner",userid);
            if(managerFlag){
                param.put("role","manager");
            }
            Page page = new Page();
            page.setPageSize(pageSize);
            page.setPageNow(pageNow);
            param.put("queryStart",page.getQueryStart());
            param.put("pageSize",page.getPageSize());
            List<Map<String,Object>> list =  projectInfoMapper.getProjectInfoLists(param);
            //获取项目成员
            list.forEach(map -> {
                map.put("projectusers",projectUserMapper.getProjectUsersByProjectId(map.get("projectid")+""));
            });

            ProjectAuxiliaryCalculation projectAuxiliaryCalculation = new ProjectAuxiliaryCalculation();
            String role = "";
            for(Role rr : roles){
                role = rr.getRolekey();
                break;
            }
            List<Map<String,Object>> newlist = new ArrayList<>();
            for (Map<String,Object> projectinfo:list){
                List<Map<String, Object>> actions = projectAuxiliaryCalculation.getActions(role, Common.MODULE_PROJECT);
                projectinfo.put(Common.ACTIONS,actions);
                newlist.add(projectinfo);
            }

            int count = projectInfoMapper.getProjectListsCounts(param);
            page.setTotal(count);
            page.setResultList(newlist);
            JsonModel jm = new JsonModel(true, "获取项目列表信息成功", Messageconstant.REQUEST_SUCCESSED_CODE ,page);
            return  JSON.toJSONString(jm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonModel jm = new JsonModel(false, "获取项目列表信息失败", Messageconstant.REQUEST_FAILED_CODE ,null);
        return  JSON.toJSONString(jm);
    }
    /**
     * 获取指定项目的信息
     * @param projectid
     * @return
     */
    @Override
    public String getProjectInfo(String projectid) {
        ProjectInfoDto projectInfo = projectInfoMapper.getProjectInfoByProjectId(projectid);
        JSONObject jsonObject = (JSONObject)JSONObject.toJSON(projectInfo);
        List<Map<String,String>> auditorandPerformers = auditorPerformerMapper.selectAuditorandPerformerByProjectid(projectid);
        List<Map<String,Object>> aupermapList = new ArrayList<>();
        auditorandPerformers.forEach(auditorandPerformer -> {
            boolean flag = false;
            for(Map<String,Object> aupermap : aupermapList){
                if(aupermap.get("auditorid").equals(auditorandPerformer.get("auditorid"))){
                    Map<String,String> performermap = new HashMap<>(2);
                    performermap.put("performerid",auditorandPerformer.get("performerid"));
                    performermap.put("performername",auditorandPerformer.get("performername"));
                    ((List)aupermap.get("performers")).add(performermap);
                    flag = true;
                    break;
                }
            }
            if(! flag){
                Map<String,String> performermap = new HashMap<>(2);
                performermap.put("performerid",auditorandPerformer.get("performerid"));
                performermap.put("performername",auditorandPerformer.get("performername"));
                List<Map<String,String>> performerlist = new ArrayList<>();
                performerlist.add(performermap);
                Map<String,Object> auditmap = new HashMap<>();
                auditmap.put("auditorid",auditorandPerformer.get("auditorid"));
                auditmap.put("auditorname",auditorandPerformer.get("auditorname"));
                auditmap.put("performers",performerlist);
                aupermapList.add(auditmap);
            }
        });
        jsonObject.put("auditorandPerformer",aupermapList);
        JsonModel jm = new JsonModel(true, "获取项目列表信息成功", Messageconstant.REQUEST_SUCCESSED_CODE ,jsonObject);
        return  JSON.toJSONString(jm);
    }

    /**
     * 编辑指定项目信息
     * @param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String editProjectInfo(ProjectDto projectDto) {

        String res =null;
        try {
            String projectid = projectDto.getProjectid();
            String powner = projectDto.getProjectowner();
            ProjectInfo pInfo = projectInfoMapper.selectByPrimaryKey(projectid);
            String projectowner = pInfo.getProjectowner();

            ProjectInfo projectInfo = new ProjectInfo();
            projectInfo.setProjectid(projectDto.getProjectid());
            if (null!=powner && !powner.equals(projectowner)){
                //二者不同，说明编辑项目时，更换了项目管理员,即当前管理员降级为历史管理员
                //并将其置为普通的项目组员，并且新的管理员如果之前不在该项目组中时，将其加入该项目组
                ProjectHisOwner projectHisOwner = new ProjectHisOwner();
                projectHisOwner.setDeparturetime(DateFormatUtil.DateFormat());
                projectHisOwner.setFormerowner(projectowner);
                projectHisOwner.setProjectid(projectid);
                projectHisOwnerMapper.insertSelective(projectHisOwner);
                projectInfo.setProjectowner(powner);
            }

            if(null != projectDto.getNote())
                 projectInfo.setNote(projectDto.getNote());
            if(null != projectDto.getProjectdesc())
                projectInfo.setProjectdesc(projectDto.getProjectdesc());
            projectInfo.setUpdatetime(DateFormatUtil.DateFormat());
            projectInfoMapper.updateByPrimaryKeySelective(projectInfo);

            auditorPerformerMapper.deleteByProjectid(projectid);
            projectUserMapper.deleteByProjectid(projectid);
            /**
             * 插入数据
             */
            String pmanager = projectDto.getPmanager();
            List<Map<String, String>> auditorperformers = projectDto.getAuditorperformers();

            Map<String,Object> param = new HashMap<>(2);
            List<ProjectUser> pulist = new ArrayList<>();
            //审核人与被审核人关系信息
            Set<String> pset = new HashSet<>();
            for (Map<String,String> ap:auditorperformers){
                String auditor = ap.get("auditor");
                String performers = ap.get("performers");

                String[] ids = performers.split(",");
                List<String> performerids = Arrays.asList(ids);
                List<AuditorPerformer> aps = new ArrayList<>();
                for (String performerid:performerids){
                    AuditorPerformer auditorPerformer = new AuditorPerformer();
                    auditorPerformer.setAuditorid(auditor);
                    auditorPerformer.setPerformerid(performerid);
                    auditorPerformer.setProjectid(projectid);
                    aps.add(auditorPerformer);//审核人_被审核人关联表信息

                    pset.add(performerid);
                }
                auditorPerformerMapper.batchInsert(aps);

                ProjectUser pu = new ProjectUser();
                pu.setMember(auditor);
                pu.setProjectid(projectid);
                pu.setProlekey(Common.BUSINESS_ROLE_AUDITOR);//业务角色为审核人
                pulist.add(pu);
            }
            //添加项目管理者
            ProjectUser pu = new ProjectUser();
            pu.setMember(pmanager);
            pu.setProjectid(projectid);
            pu.setProlekey(Common.BUSINESS_ROLE_PMANAGER);//业务角色为项目管理者
            pulist.add(pu);
            //其余项目成员
            if(null != projectDto.getMembers()){
                String[] members = projectDto.getMembers().split(",");
                for(String member : members){
                    pset.add(member);
                }
            }
//            pset.add(userid);
            //项目成员
            for (String pid:pset){
                ProjectUser pru = new ProjectUser();
                pru.setMember(pid);
                pru.setProjectid(projectid);
                pru.setProlekey(Common.BUSINESS_ROLE_MEMBER);//业务角色为普通成员
                pulist.add(pru);
            }
            //批量插入项目用户列表
            projectUserMapper.batchInsertProjcetUser(pulist);

            JsonModel jm = new JsonModel(true, "更新项目信息成功", Messageconstant.REQUEST_SUCCESSED_CODE ,projectInfo);
            return  JSON.toJSONString(jm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonModel jm = new JsonModel(false, "更新项目信息失败", Messageconstant.REQUEST_FAILED_CODE ,null);
        return  JSON.toJSONString(jm);
    }
    /**
     * 删除指定项目可批量删除（删除原则为项目下无有效任务）
     * @param projectids
     * @return
     */
    @Override
    public String deleteProjectInfo(String projectids) {

        if (!"".equals(projectids)|| null != projectids){
            String[] pidarr = projectids.split(",");
            List<String> pids = Arrays.asList(pidarr);
            List<String> pnames = new ArrayList<>();
            for (String projectid:pids){
                Map<String, Object> task = taskInfoMapper.getTaskInfoByProjectId(projectid);
                if (task!=null){
                    String projectname = (String) task.get("projectname");
                    pnames.add(projectname);
                }
            }
            if (pnames != null && pnames.size()>0){
                JsonModel jm = new JsonModel(false, pnames.toString()+"项目存在任务，请勿删除", Messageconstant.REQUEST_FAILED_CODE ,null);
                return  JSON.toJSONString(jm);

            }
            projectInfoMapper.batchDeleteProjectsByPids(pids);//批量删除项目表中的信息
            projectUserMapper.deleteByProjectIds(pids);//根据projectid删除project_user表中的项目--用户关联信息
            JsonModel jm = new JsonModel(true, "项目删除成功", Messageconstant.REQUEST_SUCCESSED_CODE,null);
            return  JSON.toJSONString(jm);
        }
        JsonModel jm = new JsonModel(false, "请选择要删除的项目", Messageconstant.REQUEST_FAILED_CODE ,null);
        return  JSON.toJSONString(jm);
    }
    /**
     * 按照关键字检索符合条件的项目
     * @param map
     * @return
     */
    @Override
    public String searchProjectInfo(Map<String,Object> map) {

        String token = SecurityUtils.getSubject().getPrincipals().toString();
        String projectowner = JWTUtil.getUserId(token);
        User user = userMapper.selectUserDetailsByUserId(projectowner);
        Set<Role> roles = user.getRoles();

        boolean adminFlag = false;
        boolean managerFlag = false;
        for (Role role :roles){
            if(role.getRolekey().equals("admin")){
                adminFlag = true;
            }
            if(role.getRolekey().equals("manager")){
                managerFlag = true;
            }
        }


        Map<String,Object> param = new HashMap<>(4);
        if(null != map.get("keyword")){
            param.put("keyword",(map.get("keyword")+"").trim());
        }
        if(null != map.get("projectname")){
            param.put("projectname",(map.get("projectname")+"").trim());
        }
        if(null != map.get("note")){
            param.put("note",(map.get("note")+"").trim());
        }
        if(null != map.get("starttime")){
            param.put("starttime",map.get("starttime")+"");
        }
        if(null != map.get("endtime")){
            param.put("endtime",map.get("endtime")+"");
        }
        List<ProjectUser> pus = projectUserMapper.getProjectUserByUserId(projectowner);
        List<String> ids = new ArrayList<>();
        boolean pmanagerFlag = false;
        for (ProjectUser pu:pus){
            String projectid = pu.getProjectid();
            String prolekey = pu.getProlekey();
            if (prolekey.equals(Common.BUSINESS_ROLE_PMANAGER)){
                pmanagerFlag=true;
                ids.add(projectid);
            }
        }

        if (!adminFlag && !managerFlag && pmanagerFlag){
            param.put("role","pmanager");
//            param.put("idlist",ids);
            param.put("userid",projectowner);
        }
        boolean managerboo = SecurityUtils.getSubject().hasRole("manager");
        if (managerboo){
            param.put("projectowner",projectowner);
        }
        Page page = new Page();
        page.setPageSize(Integer.parseInt(map.get("pageNow")+""));
        page.setPageNow(Integer.parseInt(map.get("pageNow")+""));
        param.put("queryStart",page.getQueryStart());
        param.put("pageSize",page.getPageSize());
        List<Map<String,Object>> projects = projectInfoMapper.searchProjectInfo(param);

        String role = "";
        for(Role rr : roles){
            role = rr.getRolekey();
            break;
        }
        ProjectAuxiliaryCalculation projectAuxiliaryCalculation = new ProjectAuxiliaryCalculation();
        List<Map<String,Object>> newlist = new ArrayList<>();
        for (Map<String,Object> projectinfo:projects){
            List<Map<String, Object>> actions = projectAuxiliaryCalculation.getActions(role, Common.MODULE_PROJECT);
            projectinfo.put(Common.ACTIONS,actions);
            newlist.add(projectinfo);
        }
        int count = projectInfoMapper.searchProjectInfoCounts(param);//条数
        page.setTotal(count);
        page.setResultList(newlist);
        JsonModel jm = new JsonModel(true, "检索成功", Messageconstant.REQUEST_SUCCESSED_CODE ,page);
        return  JSON.toJSONString(jm);
    }

    /**
     * 获取项目简单信息列表：供下拉列表时使用
     * @return
     */
    @Override
    public String getSimpleProjectInfo() {
        String res = null;
        try {
            /**
             * 添加了pmanager以后，查询项目列表时，除了自己创建的项目，还有所属项目是pmanager的项目
             */
            Subject subject = SecurityUtils.getSubject();
            String token = subject.getPrincipals().toString();
            String createuserid = JWTUtil.getUserId(token);
            List<Map<String,Object>> plist = projectInfoMapper.getSimpleProjectInfo(createuserid);
            JsonModel jm = new JsonModel(true, ReturnCode.SUCESS_CODE_0000.getValue(),ReturnCode.SUCESS_CODE_0000.getKey(),plist);
            res = JSON.toJSONString(jm);
            return res;
        } catch (Exception e) {
            log.error("ProjectServiceImpl  getSimpleProjectInfo   occur exception :"+e);
            ServiceException serviceException=(ServiceException) ExceptionUtil.handlerException4biz(e);
            JsonModel jm = new JsonModel(false, serviceException.getErrorMessage(),serviceException.getErrorCode(),null);
            res = JSON.toJSONString(jm);
        }
        return res;
    }
    /**
     * 根据项目ID查询该项目下的项目成员信息
     * @param projectid
     * @return
     */
    @Override
    public String getUsersByProjectId(String projectid) {
        try {
            List<Map<String,Object>> list = projectInfoMapper.getUsersByProjectId(projectid);
            JsonModel jm = new JsonModel(true,ReturnCode.SUCESS_CODE_0000.getValue(),ReturnCode.SUCESS_CODE_0000.getKey(),list);
            return JSON.toJSONString(jm);
        } catch (Exception e) {
            log.error("TaskUserServiceImpl   getUsersByProjectId occur exception :"+e);
            ServiceException serviceException=(ServiceException) ExceptionUtil.handlerException4biz(e);
            JsonModel jm = new JsonModel(false, serviceException.getErrorMessage(),serviceException.getErrorCode(),null);
            return JSON.toJSONString(jm);
        }

    }

    @Override
    public String generateDatasByProject(Map<String, String> map) {

        /**
         * 1、判断当前用户角色：
         *      admin：所有项目下的任务生成的数据结果文件
         *      manager：本人创建的所有项目下的任务生成的数据结果文件
         *      pmanager：所属管理项目下的任务生成的数据结果文件
         *
         *      admin
         *      1、manager
         *      2、manager + member
         *      3、manager + pmanger
         *      4、user    + pmanager
         *
         * 2、判断ids是否为空，若不为空，下载指定的项目下的任务生成的数据结果文件
         */
        try {
            String userid = "";
            if(null == map.get("userid")){
                String token = SecurityUtils.getSubject().getPrincipals().toString();
                userid = JWTUtil.getUserId(token);
            }
            String sysrole = userServiceUtil.getUserMaxSysRoleByUserId(userid);
            String businessrole = userServiceUtil.getUserMaxBusinessRoleByUserId(userid);

            if(UserRoleConstant.SYS_USER.equals(sysrole) && ! UserRoleConstant.PROJECT_MANAGER.equals(businessrole)){
                return  JSON.toJSONString(new JsonModel(false,"没有下载权限",Messageconstant.REQUEST_FAILED_CODE));
            }
            if(null != map.get("ids")){
                StringBuilder stringBuilder = new StringBuilder("");
                if(map.get("ids").contains(",")){
                    String[] ids = map.get("ids").split(",");
                    for(String id : ids){
                        stringBuilder.append("'"+id +"',");
                    }
                    map.put("ids",stringBuilder.toString().substring(0,stringBuilder.toString().length() -1));
                }else{
                    map.put("ids","'"+map.get("ids") +"'");
                }
            }
            map.put("userid",userid);
            map.put("sysrole",sysrole);
            List<Map<String,Object>> contents = projectInfoMapper.getTaskResultDatasInProjectByUser(map);
            if(null != contents && contents.size() >0){
                String filepath = projectDataUtil.makeDataFilesByProject(contents);

//                String data = DateFormatUtil.DateFormat(DateFormatUtil.date_pattern_2);
//                String zipfilepath = filepath+"/"+data+".zip";

                String filename = filepath.substring(filepath.lastIndexOf("/") +1);
                String zipfilepath = filepath+"/"+filename+".zip";

                OutputStream fos2 = new FileOutputStream(new File(zipfilepath));
                ZipFileUtil.toZip(filepath,fos2,true);

                return  JSON.toJSONString(new JsonModel(true,"生成数据成功",Messageconstant.REQUEST_SUCCESSED_CODE,zipfilepath));

            }else{
                return JSON.toJSONString(new JsonModel(false,"没有数据可下载",Messageconstant.REQUEST_FAILED_CODE));
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return  JSON.toJSONString(new JsonModel(false,"生成数据失败",Messageconstant.REQUEST_FAILED_CODE));
        }
    }

    @Override
    public String downloadDatasByProject(HttpServletRequest request, HttpServletResponse response, Map<String, String> map) {

        String filepath = map.get("filepath");
        if(null == filepath){
            return JSON.toJSONString(new JsonModel(false,"下载路径为空",Messageconstant.REQUEST_FAILED_CODE));
        }
        String filename = filepath.substring(filepath.lastIndexOf("/") +1);
        String filedir = filepath.substring(0,filepath.lastIndexOf("/"));
        try {
            ZipFileUtil.downloadzipfile(filedir,filename, response);

            FileUtil.deleteFile(new File(filedir));
        }catch (Exception e){
            log.error(e.getMessage());
            return  JSON.toJSONString(new JsonModel(false,"下载结果数据失败",Messageconstant.REQUEST_FAILED_CODE));
        }
        return  JSON.toJSONString(new JsonModel(true,"下载结果数据成功",Messageconstant.REQUEST_SUCCESSED_CODE));
    }

    @Override
    public String wordStatisticOfMembers(HttpServletRequest request, HttpServletResponse response, Map<String, String> map) {
        try {
            String uuid = StringUtil.getUUID();
            StringBuilder csv = new StringBuilder("username,countwords,abandonwords\n");
            StringBuilder taskcsv = new StringBuilder("username,tasktitle,tasktype,wordsize,finishtime,finishdeadline\n");
            String projectname = "";
            String absoluteFilePath = "";
            String taskAbsoluteFilePath = "";
            String generalfilepath = projectDataUtil.getGeneralFilePath() + uuid + "/";
            FileUtil.checkFileOrDirExist(generalfilepath,"dir");
            String csvdir = generalfilepath + "csv/";
            FileUtil.checkFileOrDirExist(csvdir,"dir");
            if(map.get("ids").contains(",")){
                String[] ids = map.get("ids").split(",");
                for(String id : ids){
                    map.put("projectid",id);
                    /**
                     * 查询该项目的项目成员标注字数，并生成文件
                     */
                    List<Map<String,Object>> statistic = projectInfoMapper.wordStatisticOfMembers(map);
                    projectname = statistic.get(0).get("projectname")+"";
                    for(Map<String,Object> member : statistic){
                        String countwords = member.get("countwords")+"";
                        String abandonwords = member.get("abandonwords")+"";
                        csv.append(member.get("username")+","+(countwords).substring(0,countwords.indexOf("."))+","+
                                abandonwords.substring(0,abandonwords.indexOf("."))+"\n");
                    }
                    absoluteFilePath = csvdir + projectname +".csv";
                    projectDataUtil.makeWordStatisticOfMembersFile(absoluteFilePath,csv.toString());
                    /**
                     * 查询该项目成员的详细任务信息，并生成文件
                     */
                    List<Map<String,Object>> statistictasks = projectInfoMapper.taskStatisticOfMembers(map);
                    for(Map<String,Object> task : statistictasks){
                        taskcsv.append(task.get("username")+","+task.get("tasktitle")+","+task.get("tasktype")+","+
                                task.get("wordsize")+","+task.get("finishtime")+","+task.get("finishdeadline")+"\n");
                    }
                    taskAbsoluteFilePath = csvdir + projectname +"_taskinfo.csv";
                    projectDataUtil.makeWordStatisticOfMembersFile(taskAbsoluteFilePath,taskcsv.toString());
                    csv = new StringBuilder("username,countwords,abandonwords\n");
                    taskcsv = new StringBuilder("username,tasktitle,tasktype,wordsize,finishtime,finishdeadline\n");
                }
                csv = null;
                //生成压缩文件，下载
                String zipfilepath = generalfilepath +"wordcount.zip";
                OutputStream fos2 = new FileOutputStream(new File(zipfilepath));
                ZipFileUtil.toZip(csvdir,fos2,true);
                return  JSON.toJSONString(new JsonModel(true,"生成数据成功",Messageconstant.REQUEST_SUCCESSED_CODE,zipfilepath));
            }else{
                map.put("projectid",map.get("ids"));
                List<Map<String,Object>> statistic = projectInfoMapper.wordStatisticOfMembers(map);
                projectname = statistic.get(0).get("projectname")+"";
                for(Map<String,Object> member : statistic){
                    String countwords = member.get("countwords")+"";
                    String abandonwords = member.get("abandonwords")+"";
                    csv.append(member.get("username")+","+(countwords).substring(0,countwords.indexOf("."))+","+
                            abandonwords.substring(0,abandonwords.indexOf("."))+"\n");
                }
                absoluteFilePath = csvdir + projectname +".csv";
                projectDataUtil.makeWordStatisticOfMembersFile(absoluteFilePath,csv.toString());
                /**
                 * 查询该项目成员的详细任务信息，并生成文件
                 */
                List<Map<String,Object>> statistictasks = projectInfoMapper.taskStatisticOfMembers(map);
                for(Map<String,Object> task : statistictasks){
                    taskcsv.append(task.get("username")+","+task.get("tasktitle")+","+task.get("tasktype")+","+
                            task.get("wordsize")+","+task.get("finishtime")+","+task.get("finishdeadline")+"\n");
                }
                taskAbsoluteFilePath = csvdir + projectname +"_taskinfo.csv";
                projectDataUtil.makeWordStatisticOfMembersFile(taskAbsoluteFilePath,taskcsv.toString());
                String zipfilepath = generalfilepath +"wordcount.zip";
                OutputStream fos2 = new FileOutputStream(new File(zipfilepath));
                ZipFileUtil.toZip(csvdir,fos2,true);
                return  JSON.toJSONString(new JsonModel(true,"生成数据成功",Messageconstant.REQUEST_SUCCESSED_CODE,zipfilepath));
            }

        }catch (Exception e){
            log.error(e.getMessage(),e);
            return  JSON.toJSONString(new JsonModel(false,"生成数据失败",Messageconstant.REQUEST_FAILED_CODE));
        }
    }


}
