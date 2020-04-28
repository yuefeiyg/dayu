package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.blcultra.cons.*;
import com.blcultra.dao.*;
import com.blcultra.exception.ExceptionUtil;
import com.blcultra.exception.ServiceException;
import com.blcultra.model.TaskInfo;
import com.blcultra.model.taskResFileInfo;
import com.blcultra.service.TaskInfoActionService;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.JsonModel;
import com.dayu.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("taskInfoActionService")
public class TaskInfoActionServiceImpl implements TaskInfoActionService {

    private Logger log= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Autowired
    private DataInfoMapper dataInfoMapper;

    @Autowired
    private AnnotationTemplateMapper annotationTemplateMapper;

    @Autowired
    private TaskAnnotationDataMapper taskAnnotationDataMapper;

    @Autowired
    private TaskAttachInfoMapper taskAttachInfoMapper;

    @Autowired
    private AnnotationObjectInfoMapper annotationObjectInfoMapper;

    @Autowired
    private AnnotationObjectRelationInfoMapper annotationObjectRelationInfoMapper;

    @Autowired
    private taskResFileInfoMapper taskResFileInfoMapper;

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Autowired
    private TaskDictMapper taskDictMapper;

    @Autowired
    private TaskReviewInfoMapper taskReviewInfoMapper;



    @Override
    public String getTaskInfo(String taskId) {
        String res = null;
        if(taskId == null || taskId.equals("")){
            JsonModel jm = new JsonModel(false,MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.VIEW.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            return res;
        }

        try {
            Map<String, Object> taskInfo = taskInfoMapper.getTaskInfo(taskId);

            List<Map<String, Object>> attachData = taskAttachInfoMapper.getDatasByTaskId(taskId);

            List<Map<String, Object>> resData = taskResFileInfoMapper.getDatasByTaskId(taskId);

            taskInfo.put(Common.DATA,attachData);

            taskInfo.put(Common.RESULT_DATA,resData);

            JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.VIEW.getValue(), Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,taskInfo);
            res = JSON.toJSONString(jm);

        }catch (Exception e){
            log.error("=========TaskServiceImpl  addTask   occur exception :"+e);
            JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.VIEW.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);

        }
        return res;
    }

    @Override
    public String deleteTask(String ids) {
        String res = null;
        try{
            if (StringUtil.empty(ids)){
                JsonModel jm = new JsonModel(true, BusinessEnum.TASK_DELETE_DATA_ID_EMPTY.getValue(),
                        Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }
            String[] str = ids.split(",");
            List<String> idList = Arrays.asList(str);
            int m = taskInfoMapper.updateTaskToDeleteStateByIds(idList);
            if (m>0){
                JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                        ActionEnum.DELETE.getValue(), Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }
            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.DELETE.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);

        }catch (Exception e ){
            log.error("=========TaskServiceImpl  deleteTask   occur exception :"+e);
            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.DELETE.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public synchronized String receiveTask(String taskId) {
        String res = null;
        try {
//            beforeValidateParam(taskid,taskstate);
            Subject subject = SecurityUtils.getSubject();
            String token = subject.getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);


            //检测任务是否被人领取
            Map<String,Object> taskInfo = taskInfoMapper.getTaskInfo(taskId);
            String performerId = String.valueOf(taskInfo.get(DbConstant.PERFORMER_ID));

            if (!userId.equals(performerId)&& !StringUtil.empty(performerId)) {
                JsonModel jm = new JsonModel(true, BusinessEnum.TASK_ALREADY_RECEIVED.getValue(),Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }

            String taskVersion = String.valueOf(taskInfo.get(DbConstant.TASK_VERSION));

            Map<String,String> map = new HashMap<>();
            map.put(DbConstant.TASK_ID,taskId);
            map.put(DbConstant.TASK_STATE,Common.MODULE_TASK_STATE_RECEIVE);
            map.put(DbConstant.PERFORMER_ID,userId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            map.put(DbConstant.RECEIVE_TIME,sdf.format(now));
            map.put(DbConstant.TASK_VERSION,taskVersion);

            int m = taskInfoMapper.updateTaskState(map);
            if (m>0){
                JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                        ActionEnum.RECEIVE.getValue(), Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }
            JsonModel jm = new JsonModel(true,  BusinessEnum.TASK_ALREADY_CHANGE_CAUSE_RECEIVED_FAILED.getValue(),Messageconstant.REQUEST_SUCCESSED_CODE,null);
            res = JSON.toJSONString(jm);
            return res;
        } catch (Exception e) {
            log.error("=========TaskServiceImpl  doBeginOrPauseTask   occur exception :"+e);
            JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.RECEIVE.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public synchronized String startTask(String taskId) {
        String res = null;
        try {
//            beforeValidateParam(taskid,taskstate);
            Subject subject = SecurityUtils.getSubject();
            String token = subject.getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);


            //检测任务是否被人领取
            Map<String,Object> taskInfo = taskInfoMapper.getTaskInfo(taskId);
            String performerId = String.valueOf(taskInfo.get(DbConstant.PERFORMER_ID));

            if (!userId.equals(performerId)&& !StringUtil.empty(performerId)) {
                JsonModel jm = new JsonModel(true, BusinessEnum.TASK_ALREADY_RECEIVED.getValue(),Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }

            String taskVersion = String.valueOf(taskInfo.get(DbConstant.TASK_VERSION));
            String beginTime = String.valueOf(taskInfo.get(DbConstant.BEGIN_TIME));

            Map<String,String> map = new HashMap<>();
            map.put(DbConstant.TASK_ID,taskId);
            map.put(DbConstant.TASK_STATE,Common.MODULE_TASK_STATE_START);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            if (StringUtil.empty(performerId)){
                map.put(DbConstant.PERFORMER_ID,userId);
                map.put(DbConstant.RECEIVE_TIME,sdf.format(now));
            }
            if (StringUtil.empty(beginTime)){
                map.put(DbConstant.BEGIN_TIME,sdf.format(now));
            }
            map.put(DbConstant.PAUSE_TIME,sdf.format(now));
            map.put(DbConstant.TASK_VERSION,taskVersion);

            int m = taskInfoMapper.updateTaskState(map);
            if (m>0){
                JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                        ActionEnum.START.getValue(), Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }
            JsonModel jm = new JsonModel(true,  BusinessEnum.TASK_ALREADY_CHANGE_CAUSE_RECEIVED_FAILED.getValue(),Messageconstant.REQUEST_SUCCESSED_CODE,null);
            res = JSON.toJSONString(jm);
            return res;
        } catch (Exception e) {
            log.error("=========TaskServiceImpl  doBeginOrPauseTask   occur exception :"+e);
            JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.START.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public String pauseTask(String taskId) {
        String res = null;
        try {
//            beforeValidateParam(taskid,taskstate);
            Subject subject = SecurityUtils.getSubject();
            String token = subject.getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);


            Map<String,Object> taskInfo = taskInfoMapper.getTaskInfo(taskId);
            long costTime = Long.parseLong(taskInfo.get(DbConstant.COST_TIME) == null ? "0" : String.valueOf(taskInfo.get(DbConstant.COST_TIME)));
            String pauseTime = String.valueOf(taskInfo.get(DbConstant.PAUSE_TIME));
            String taskVersion = String.valueOf(taskInfo.get(DbConstant.TASK_VERSION));

            Map<String,String> map = new HashMap<>();
            map.put(DbConstant.TASK_ID,taskId);
            map.put(DbConstant.TASK_STATE,Common.MODULE_TASK_STATE_PAUSE);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();

            Date taskpausedate = sdf.parse(pauseTime);
            long rate = (now.getTime()-taskpausedate.getTime()) / 1000;

            map.put(DbConstant.COST_TIME,String.valueOf(costTime+rate));
            map.put(DbConstant.PAUSE_TIME,sdf.format(now));
            map.put(DbConstant.TASK_VERSION,taskVersion);

            int m = taskInfoMapper.updateTaskState(map);
            if (m>0){
                JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                        ActionEnum.PAUSE.getValue(), Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }
            JsonModel jm = new JsonModel(true,  MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.PAUSE.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            return res;
        } catch (Exception e) {
            log.error("=========TaskServiceImpl  doBeginOrPauseTask   occur exception :"+e);
            JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.START.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            e.printStackTrace();
        }
        return res;
    }



    @Override
    public String recallTask(String ids) {
        String res = null;
        try{
             if (StringUtil.empty(ids)){
                JsonModel jm = new JsonModel(true, BusinessEnum.TASK_RECALL_DATA_ID_EMPTY.getValue(),
                        Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }
            String[] str = ids.split(",");
            List<String> idList = Arrays.asList(str);
            int m = taskInfoMapper.updateTaskToReceiveStateByIds(idList);
            int n = taskResFileInfoMapper.updateToDeleteStateByTaskIds(idList);
            if (m>0 && n > 0){
                JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                        ActionEnum.RECALL.getValue(), Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }
            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.RECALL.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);

        }catch (Exception e ){
            log.error("=========TaskServiceImpl  deleteTask   occur exception :"+e);
            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.RECALL.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public String invalidateTask(String ids) {
        String res = null;
        try{
            if (StringUtil.empty(ids)){
                JsonModel jm = new JsonModel(true, BusinessEnum.TASK_INVALIDATE_DATA_ID_EMPTY.getValue(),
                        Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }
            String[] str = ids.split(",");
            List<String> idList = Arrays.asList(str);
            int m = taskInfoMapper.updateTaskToInvalidStateByIds(idList);
            taskResFileInfoMapper.updateToDeleteStateByTaskIds(idList);
            if (m>0){
                JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                        ActionEnum.INVALIDATE.getValue(), Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }
            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.INVALIDATE.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);

        }catch (Exception e ){
            log.error("=========TaskServiceImpl  deleteTask   occur exception :"+e);
            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.INVALIDATE.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public String enterTask(String taskId) {

        return null;
    }

    @Override
    //task中的字段 taskid 提交类型committype，结果文件名称，文件路径，提交备注；评分，评价
    public String commitTask(Map<String, String> task) {
        String res = null;

        String token = SecurityUtils.getSubject().getPrincipals().toString();
        String userid = JWTUtil.getUserId(token);
        String commitType = task.get(DbConstant.COMMIT_TYPE);
        String taskId = task.get(DbConstant.TASK_ID);

        TaskInfo taskInfo = new TaskInfo();
        taskResFileInfo taskResFileInfo = new taskResFileInfo();
        taskInfo.setTaskid(taskId);
        boolean needToInsertRes = false;


        if(Common.COMMIT_TYPE_RESULT.equals(commitType)){//提交
            if (!StringUtil.empty(task.get(DbConstant.TASK_RES_FILE_DATA_NAME))){
                needToInsertRes = true;
                String dataId = StringUtil.getUUID();
                taskResFileInfo.setDataid(dataId);
                taskResFileInfo.setDataname(task.get(DbConstant.TASK_RES_FILE_DATA_NAME));
                taskResFileInfo.setPath(task.get(DbConstant.TASK_RES_FILE_PATH));
                taskResFileInfo.setTaskid(taskId);
            }

            taskInfo.setResultdesc(task.get(DbConstant.RESULT_DESC));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            taskInfo.setFinishtime(sdf.format(now));
            taskInfo.setTaskstate(Common.MODULE_TASK_STATE_COMPLETE);
        }
        else if(Common.COMMIT_TYPE_EVALUATION.equals(commitType)){//评价
            taskInfo.setScore(task.get(DbConstant.SCORE));
            taskInfo.setComments(task.get(DbConstant.COMMENTS));
            taskInfo.setTaskstate(Common.MODULE_TASK_STATE_CLOSED);//将完成评价的任务置为已关闭任务
        }
        try{
            int m = -1;
            m = taskInfoMapper.updateByPrimaryKeySelective(taskInfo);
            if (needToInsertRes){
                m = taskResFileInfoMapper.insert(taskResFileInfo);
            }
            if (m>0){
                JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                        ActionEnum.COMMIT.getValue(), Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }
            JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.COMMIT.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            log.error("=========TaskServiceImpl  updateTaskState   occur exception :"+e);
            ServiceException serviceException=(ServiceException) ExceptionUtil.handlerException4biz(e);
            JsonModel jm = new JsonModel(false, serviceException.getErrorMessage(),serviceException.getErrorCode(),null);
            res = JSON.toJSONString(jm);
            return res;
        }
    }


}
