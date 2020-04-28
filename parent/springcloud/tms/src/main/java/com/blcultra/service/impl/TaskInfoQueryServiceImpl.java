package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.*;
import com.blcultra.dao.ProjectInfoMapper;
import com.blcultra.dao.ProjectUserMapper;
import com.blcultra.dao.TaskInfoMapper;
import com.blcultra.dao.UserMapper;
import com.blcultra.service.TaskInfoQueryService;
import com.blcultra.service.core.TaskAuxiliaryCalculation;
import com.blcultra.service.core.UserServiceUtil;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.JsonModel;
import com.blcultra.support.Page;
import com.dayu.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("taskInfoQueryService")
public class TaskInfoQueryServiceImpl implements TaskInfoQueryService {

    private Logger log= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Autowired
    private UserServiceUtil userServiceUtil;

    @Autowired
    private ProjectUserMapper projectUserMapper;


    @Override
    public String getPublicTasks(int pageNow, int pageSize, String keyword, Map<String, String> searchConditions) {

        JSONObject response = new JSONObject();
        String res="";

        /**
         * 添加pmanager业务角色，将manager角色提高一级。
         * 对于普通用户来说，不用改变，pmanager因为同时是项目成员，现有逻辑不用变
         * 对于manager用户来说，待领取列表包括本人创建的项目下的待领取任务和属于其他项目成员的项目下的待领取任务，
         * 因为现在manager不在项目成员表中，在项目的所有者字段中
         */
        try {
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);

            Map<String,Object> map = new HashMap<>();
            Page page = new Page();
            page.setPageNow(pageNow);
            page.setPageSize(pageSize);

            map.put("userId",userId);
            map.put("queryStart",page.getQueryStart());
            map.put("pageSize",page.getPageSize());

            if(keyword != null && ! "".equals(keyword))
                map.put("keyword",keyword);
            if (searchConditions != null)
                map.putAll(searchConditions);

            String sysrole = userServiceUtil.getUserMaxSysRoleByUserId(userId);
            map.put(Common.SYS_ROLE,sysrole);

            map.put("queryStart",page.getQueryStart());
            map.put("pageSize",page.getPageSize());

            List<Map<String,Object>> tasks=  taskInfoMapper.getPublishedTaskList(map);

            page.setResultList(getActions(tasks,userId,sysrole,Common.MODULE_TASK_TABLE_PUBLIC));

            int count = taskInfoMapper.getPublishedTaskListCounts(map);
            page.setTotal(count);
            JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(ActionEnum.QUERY.getValue(), Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,page);
            res = JSON.toJSONString(jm);

            return res;
        }catch (Exception e){
            log.error("TaskServiceImpl  getPublishedTaskList   occur exception :"+e);

            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(ActionEnum.QUERY.getValue(),
                    Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,null);
            res = JSON.toJSONString(jm);

        }
        return res;
    }

    @Override
    public String getMyTasks(int pageNow, int pageSize, String keyword, Map<String, String> searchConditions) {
        /**
         *  添加pmanager业务角色，将manager角色提高一级。
         *  user:     任务的经办人是当前用户
         *  pmanager：任务的经办人是当前用户和当前人管理的项目下的任务状态是已完成的
         *  auditor： 任务的经办人是当前用户和由当前用户审核的审核人提交的已完成的任务
         *  manager： 任务的经办人是当前用户和当前用户创建的项目下所有状态是已完成的任务
         *  admin：   任务的经办人是当前用户和所有状态是已完成的任务
         */
        String res =null;
        try{
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String performerid = JWTUtil.getUserId(token);

            Map<String,Object> map = new HashMap<>();
            Page page = new Page();
            page.setPageNow(pageNow);
            page.setPageSize(pageSize);
            map.put("queryStart",page.getQueryStart());
            map.put("pageSize",page.getPageSize());

            map.put("performerid",performerid);

            if(keyword != null && ! "".equals(keyword))
                map.put("keyword",keyword);
            if (searchConditions != null)
                map.putAll(searchConditions);

            String sysrole = userServiceUtil.getUserMaxSysRoleByUserId(performerid);
            String projectrole = userServiceUtil.getUserMaxBusinessRoleByUserId(performerid);
            map.put(Common.SYS_ROLE,sysrole);
            map.put(Common.PROJECT_ROLE,projectrole);

            List<Map<String, Object>> tasks = taskInfoMapper.getMyTaskList(map);
            page.setResultList(getActions(tasks,performerid,sysrole,Common.MODULE_TASK_TABLE_MY));
            int count = taskInfoMapper.getMyTaskListCounts(map);
            page.setTotal(count);

            JsonModel jm = new JsonModel(true,MessageInfo.actionInfo(ActionEnum.QUERY.getValue(),
                    Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,page);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e ){
            log.error("=========TaskServiceImpl  getMyTasks   occur exception :"+e);
            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(ActionEnum.QUERY.getValue(),
                    Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public String getRelatedTasks(int pageNow, int pageSize, String keyword, Map<String, String> searchConditions) {
        /**
         * 添加pmanager业务角色，将manager角色提高一级。（修改逻辑同待领取任务）
         * 对于普通用户来说，不用改变，pmanager因为同时是项目成员，现有逻辑不用变
         * 对于manager用户来说，相关任务列表包括本人创建的项目下的相关任务和属于其他项目成员的项目下的相关任务，
         * 因为现在manager不在项目成员表中，在项目的所有者字段中
         */
        String res =null;
        try{
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String performerid = JWTUtil.getUserId(token);

            Map<String,Object> map = new HashMap<>();
            Page page = new Page();
            page.setPageNow(pageNow);
            page.setPageSize(pageSize);
            map.put("queryStart",page.getQueryStart());
            map.put("pageSize",page.getPageSize());
            map.put("performerid",performerid);

            if(keyword != null && ! "".equals(keyword))
                map.put("keyword",keyword);
            if (searchConditions != null)
                map.putAll(searchConditions);

            String sysrole = userServiceUtil.getUserMaxSysRoleByUserId(performerid);
            String projectrole = userServiceUtil.getUserMaxBusinessRoleByUserId(performerid);
            map.put(Common.SYS_ROLE,sysrole);
            map.put(Common.PROJECT_ROLE,projectrole);

            if (!Common.USER_ROLE_USER.equals(sysrole) || UserRoleConstant.PROJECT_MANAGER.equals(projectrole)){

                List<Map<String, Object>> tasks = taskInfoMapper.getRelatedTaskList(map);
                page.setResultList(getActions(tasks,performerid,sysrole,Common.MODULE_TASK_TABLE_RELATED));
                int count = taskInfoMapper.getRelatedTaskListCounts(map);
                page.setTotal(count);
            }

            JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(ActionEnum.QUERY.getValue(),
                    Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,page);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e ){
            log.error("=========TaskServiceImpl  getRelatedTasks   occur exception :"+e);
            //ServiceException serviceException=(ServiceException) ExceptionUtil.handlerException4biz(e);
            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(ActionEnum.QUERY.getValue(),
                    Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public String getTaskInfo(String taskid) {
        String res =null;
        try{
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);
            Map<String,Object> taskinfo = taskInfoMapper.getAnnotationTaskInfo(taskid);
            if("002003".equals(taskinfo.get(DbConstant.TASK_STATE))){//进入页面时，状态为进行中
                long costtime = Long.parseLong(taskinfo.get(DbConstant.COST_TIME)+"");
                String pausetime = taskinfo.get(DbConstant.PAUSE_TIME)+"";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = new Date();
                Date taskpausedate = sdf.parse(pausetime);
                costtime += (now.getTime() - taskpausedate.getTime()) / 1000;
                taskinfo.put(DbConstant.COST_TIME,costtime+"");
            }
            /**
             * 判断当前任务状态以及当前用户权限
             * 1：可以标注
             * 2：只能查看
             * 3：审核功能
             */
            String powercode = "0";
            String performerId = String.valueOf(taskinfo.get(DbConstant.PERFORMER_ID));
            if(!StringUtil.empty(performerId)){

                if( TaskConstant.TASKTYPE_CHECK.equals(taskinfo.get("tasktype"))){//审核任务
                    String taskState = String.valueOf(taskinfo.get("taskstate"));
                    if(performerId.equals(userId)){//当前用户是经办人
                        if (Common.MODULE_TASK_STATE_COMPLETE.equals(taskState) || Common.MODULE_TASK_STATE_CLOSED.equals(taskState)){
                            powercode = AnnotationConstant.ANNOTATION_ACTION_VIEW;
                        }else {

                            powercode = AnnotationConstant.ANNOTATION_ACTION_CHECK;
                        }
                    }else{
                        powercode = AnnotationConstant.ANNOTATION_ACTION_VIEW;
                    }

                }else{
                    String auditor =projectUserMapper.getProjectAuditorByTaskId(taskid);
                    Map<String,Object> projectOwnerOrPamanger = projectInfoMapper.getProjectOwnerAndPmanagerByTaskId(taskid);
                    String projectOwner = String.valueOf(projectOwnerOrPamanger.get(DbConstant.PROJECT_OWNER));
                    String pmanager = String.valueOf(projectOwnerOrPamanger.get(DbConstant.P_MANAGER));
                    String taskState = String.valueOf(taskinfo.get("taskstate"));

                    if(userId.equals(performerId)){//当前用户是经办人
                        if (Common.MODULE_TASK_STATE_COMPLETE.equals(taskState)){
                            if(userId.equals(projectOwner) || userId.equals(pmanager) || userId.equals(auditor)){
                                powercode = AnnotationConstant.ANNOTATION_ACTION_CHECK;
                            }else {
                                powercode = AnnotationConstant.ANNOTATION_ACTION_VIEW;
                            }
                        }else if (Common.MODULE_TASK_STATE_CLOSED.equals(taskState)){
                            powercode = AnnotationConstant.ANNOTATION_ACTION_VIEW;
                        }else {
                            powercode = AnnotationConstant.ANNOTATION_ACTION_ANNOTE;
                        }
                    }else {
                        if(userId.equals(projectOwner) || userId.equals(pmanager) || userId.equals(auditor)){//当前用户是管理员或审核员
                            if (Common.MODULE_TASK_STATE_COMPLETE.equals(taskState)){
                                powercode = AnnotationConstant.ANNOTATION_ACTION_CHECK;
                            }else {
                                powercode = AnnotationConstant.ANNOTATION_ACTION_VIEW;
                            }
                        }else{
                            powercode = AnnotationConstant.ANNOTATION_ACTION_CHECK;
                        }

                    }
                }
            }
            taskinfo.put("powercode",powercode);

            JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(ActionEnum.QUERY.getValue(),
                    Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,taskinfo);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e ){
            log.error("=========TaskServiceImpl  getRelatedTasks   occur exception :"+e);
            //ServiceException serviceException=(ServiceException) ExceptionUtil.handlerException4biz(e);
            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(ActionEnum.QUERY.getValue(),
                    Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            e.printStackTrace();
        }
        return res;
    }

    private List<Map<String,Object>> getActions(List<Map<String,Object>> tasks,String userId,String role,String table){
        TaskAuxiliaryCalculation taskAuxiliaryCalculation = new TaskAuxiliaryCalculation();

        List<Map<String,Object>> newtasks = new ArrayList<>();
        Map<String,List<String>> projectOwners = new HashMap<>();
        for (Map<String,Object> task: tasks) {

            List<Map<String, Object>> actions = null;
            String classifyCode = String.valueOf(task.get(DbConstant.CLASSIFY_CODE));

            if(Common.USER_ROLE_ADMIN.equals(role)){
                actions = taskAuxiliaryCalculation.getActions(OperationalRole.ADMIN,table,String.valueOf(task.get(DbConstant.TASK_STATE)),
                        userId.equals(String.valueOf(task.get(DbConstant.PERFORMER_ID))),classifyCode);
            }else{
//                String projectId = String.valueOf(task.get(DbConstant.PROJECT_ID));
//                if (!projectOwners.containsKey(projectId)){
//
//                    List<Map<String,Object>> projectOwnerUsers = projectInfoMapper.getProjectOwnerOrPerformerOwnerByPorjectId(projectId);
//                    List<String> projectOnwerIds = new ArrayList<>();
//                    for (Map<String,Object> projectOwnerUser:projectOwnerUsers){
//                        projectOnwerIds.add(String.valueOf(projectOwnerUser.get(DbConstant.USER_ID)));
//                        projectOwners.put(projectId,projectOnwerIds);
//                    }
//                }
//
//                if (taskAuxiliaryCalculation.checkTaskOwnerIsMe(userId,String.valueOf(task.get(DbConstant.TASKOWNER_ID)),
//                        String.valueOf(task.get(DbConstant.PROJECT_OWNER)),projectOwners.get(projectId))){
//                    actions = taskAuxiliaryCalculation.getActions(OperationalRole.CREATOR,table,String.valueOf(task.get(DbConstant.TASK_STATE)),
//                            userId.equals(String.valueOf(task.get(DbConstant.PERFORMER_ID))),true,classifyCode);
//                }else{
//                    actions = taskAuxiliaryCalculation.getActions(OperationalRole.MEMBER,table,String.valueOf(task.get(DbConstant.TASK_STATE)),
//                            userId.equals(String.valueOf(task.get(DbConstant.PERFORMER_ID))),false,classifyCode);
//                }
                String taskId = String.valueOf(task.get(DbConstant.TASK_ID));
                Map<String,Object> pownerAndPmanager = projectInfoMapper.getProjectOwnerAndPmanagerByTaskId(taskId);
                String auditor = projectUserMapper.getProjectAuditorByTaskId(taskId);
                String projectOwner = String.valueOf(pownerAndPmanager.get(DbConstant.PROJECT_OWNER));
                String pManager = String.valueOf(pownerAndPmanager.get(DbConstant.P_MANAGER));
//                String performerId = String.valueOf(task.get(DbConstant.PERFORMER_ID));
                if (userId.equals(projectOwner)){
                    actions = taskAuxiliaryCalculation.getActions(OperationalRole.ADMIN,table,String.valueOf(task.get(DbConstant.TASK_STATE)),
                            userId.equals(String.valueOf(task.get(DbConstant.PERFORMER_ID))),classifyCode);
                }else if (userId.equals(pManager)){
                    actions = taskAuxiliaryCalculation.getActions(OperationalRole.CREATOR,table,String.valueOf(task.get(DbConstant.TASK_STATE)),
                            userId.equals(String.valueOf(task.get(DbConstant.PERFORMER_ID))),classifyCode);
                }else if (userId.equals(auditor)){
                    actions = taskAuxiliaryCalculation.getActions(OperationalRole.AUDITOR,table,String.valueOf(task.get(DbConstant.TASK_STATE)),
                            userId.equals(String.valueOf(task.get(DbConstant.PERFORMER_ID))),classifyCode);
                }else{
                    actions = taskAuxiliaryCalculation.getActions(OperationalRole.MEMBER,table,String.valueOf(task.get(DbConstant.TASK_STATE)),
                            userId.equals(String.valueOf(task.get(DbConstant.PERFORMER_ID))),classifyCode);
                }



            }
            task.put(Common.ACTIONS,actions);
            newtasks.add(task);
        }
        return newtasks;
    }

}
