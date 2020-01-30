package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.blcultra.cons.*;
import com.blcultra.dao.TaskInfoMapper;
import com.blcultra.service.TaskInfoStatisticService;
import com.blcultra.service.core.TaskInfoCoreLogicProcessor;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.JsonModel;
import com.dayu.util.DateFormatUtil;
import com.dayu.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("taskInfoStatisticService")
public class TaskInfoStatisticServiceImpl implements TaskInfoStatisticService{

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Override
    public String statistic(Map<String, Object> statisticConditions) {
        String res = null;
        try {
            Subject subject = SecurityUtils.getSubject();
            String token = subject.getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);
            boolean ub = subject.hasRole(Common.USER_ROLE_USER);
            boolean mb = subject.hasRole(Common.USER_ROLE_MANAGER);
            boolean ab = subject.hasRole(Common.USER_ROLE_ADMIN);

//            String userId = statisticConditions.get(DbConstant.USER_ID);
            String graphType = String.valueOf(statisticConditions.get(DbConstant.TYPE));
//            boolean ub = false;
//            boolean mb = true;
//            boolean ab = false;

            System.out.println("是否admin" + ab);
            Map<String, Object> map = new HashMap<>();

//            if (ub && !mb){//是普通用户但不是项目管理员
//                map.put(DbConstant.PERFORMER_ID,userId);//当前用户id，普通用户只能查询自己的任务量情况
//            }else if (mb){//如果当前用户是manager，那么需要获得其创建的项目，根据这些项目去查询属于这些项目的任务
//                List<String> pidlist = projectMapper.getProjectListByCreateUserId(userId);
//                map.put("list",pidlist);
//            }
            if (ub) {
                map.put(Common.ROLE, Common.USER_ROLE_USER);

            } else if (mb) {
                map.put(Common.ROLE, Common.USER_ROLE_MANAGER);
            } else if (ab) {
                map.put(Common.ROLE, Common.USER_ROLE_ADMIN);
            }

            map.put(DbConstant.PERFORMER_ID, userId);

            map.put(DbConstant.TASK_TITLE, String.valueOf(statisticConditions.get(DbConstant.TASK_TITLE)));//任务名称
            map.put(DbConstant.PROJECT_NAME, String.valueOf(statisticConditions.get(DbConstant.PROJECT_NAME)));//项目名称
            map.put(DbConstant.CREATEUSER_NAME, String.valueOf(statisticConditions.get(DbConstant.CREATEUSER_NAME)));//项目发布人姓名
            map.put(DbConstant.CLASSIFY_CODE, String.valueOf(statisticConditions.get(DbConstant.CLASSIFY_CODE)));//任务分类id
            map.put(DbConstant.PERFORMER, String.valueOf(statisticConditions.get(DbConstant.PERFORMER)));//经办人名称
            map.put(DbConstant.TASKOWNER, String.valueOf(statisticConditions.get(DbConstant.TASKOWNER)));//所有者名称
            map.put(DbConstant.TASK_STATE, String.valueOf(statisticConditions.get(DbConstant.TASK_STATE)));//任务状态

            String pstarttime = String.valueOf(statisticConditions.get(DbConstant.P_START_TIME));
            String pendtime = String.valueOf(statisticConditions.get(DbConstant.P_END_TIME));

            String fstarttime = String.valueOf(statisticConditions.get(DbConstant.F_START_TIME));
            String fendtime = String.valueOf(statisticConditions.get(DbConstant.F_END_TIME));

//            String statisticDimension = statisticConditions.get(Common.STATISITIC_DIMENSION);

            List<String> statisticDimensions = (List<String>)statisticConditions.get(Common.CHOOSED_SEARCH_OPTIONS);
            String statisticDimension = statisticDimensions.get(0);

            List<String> days = new ArrayList<>();

            if (Common.STATISTIC_DIMENSION_TASK.equals(statisticDimension)) {
                if (StringUtil.empty(pstarttime) && StringUtil.empty(pendtime)) {
                    pstarttime = DateFormatUtil.getPastDate(7);
                    pendtime = DateFormatUtil.DateFormat();
                } else if (!StringUtil.empty(pstarttime) && StringUtil.empty(pendtime)) {
                    pendtime = DateFormatUtil.getBasePastDate(pstarttime, 7);
                } else if (StringUtil.empty(pstarttime) && !StringUtil.empty(pendtime)) {
                    pstarttime = DateFormatUtil.getBasePastDate(pendtime, -7);
                } else if (StringUtil.empty(fstarttime) && StringUtil.empty(fendtime)) {
                    fstarttime = DateFormatUtil.getPastDate(7);
                    fendtime = DateFormatUtil.DateFormat();
                } else if (!StringUtil.empty(fstarttime) && StringUtil.empty(fendtime)) {
                    fendtime = DateFormatUtil.getBasePastDate(pstarttime, 7);
                } else if (!StringUtil.empty(fstarttime) && StringUtil.empty(fendtime)) {
                    fstarttime = DateFormatUtil.getBasePastDate(pendtime, -7);
                }
                String start = pstarttime;
                String end = pendtime;
                try {
                    start = DateFormatUtil.DataCompareReturnEarly(pstarttime, fstarttime);
                    end = DateFormatUtil.DataCompareReturnEarly(pendtime, fendtime);
                } catch (Exception e) {

                }

                days = DateFormatUtil.getDays(start, end);
            }

            map.put(DbConstant.P_START_TIME, pstarttime);
            map.put(DbConstant.P_END_TIME, pendtime);
            map.put(DbConstant.F_START_TIME, fstarttime);//查询任务完成时间段的开始时间
            map.put(DbConstant.F_END_TIME, fendtime);//查询任务完成时间段的结束时间



            List<Map<String, Object>> taskInfos = taskInfoMapper.searchTask(map);

            Map<String, Map<String, String>> taskGroupByProject = new HashMap<>();
            Map<String, Map<String, String>> taskGroupByTaskStateName = new HashMap<>();
            Map<String, Map<String, String>> taskGroupByClassifyName = new HashMap<>();

//            Map<String, List<Map<String, String>>> taskGroupByUser = new HashMap<>();
            Map<String, Map<String, String>> taskGroupByUser = new HashMap<>();
//            Map<String, List<Map<String, String>>> taskGroupByTask = new HashMap<>();
            Map<String, Map<String, String>> taskGroupByTask = new HashMap<>();
            if (Common.STATISTIC_DIMENSION_TASK.equals(statisticDimension)){

                for (String day:days) {

                    if (!taskGroupByTask.containsKey(day)){
//                        String[] taskStateName = {DbConstant.STATISTIC_CREATE_TASK,DbConstant.STATISTIC_RECEIVE_TASK,
//                                DbConstant.STATISTIC_PROCESSING_TASK,DbConstant.STATISTIC_FINISH_TASK};
                        String[] taskNumberName = {DbConstant.UN_RECEIVE_TASK_NUMBER,DbConstant.RECEIVE_TASK_NUMBER,
                                DbConstant.PROCESSING_TASK_NUMBER,DbConstant.FINISH_TASK_NUMBER};
//                        List<Map<String, String>> tasks = new ArrayList<>(numebr);
                        Map<String, String> taskNumber = new HashMap<>();
                        taskNumber.put(DbConstant.DAY,day);
                        for(int i = 0;i<taskNumberName.length;i++){

                            taskNumber.put(taskNumberName[i],Common.ZERO);

                        }
                        taskGroupByTask.put(day,taskNumber);
                    }
                }
            }

            for (Map<String, Object> taskInfo : taskInfos) {

                if (Common.STATISTIC_DIMENSION_PROJECT.equals(statisticDimension)) {

//                    String projectName = String.valueOf(taskInfo.get(DbConstant.PROJECT_NAME));
//                    if (taskGroupByProject.containsKey(projectName)) {
//                        Map<String, String> taskNumber = taskGroupByProject.get(projectName);
//                        taskNumber.put(projectName, String.valueOf(Integer.parseInt(taskNumber.get(projectName)) + 1));
//
//                    } else {
//                        Map<String, String> taskNumber = new HashMap<>();
//                        taskNumber.put(projectName, Common.ONE);
//                        taskGroupByProject.put(projectName, taskNumber);
//                    }

                    String projectName = String.valueOf(taskInfo.get(DbConstant.PROJECT_NAME));
                    if (taskGroupByProject.containsKey(projectName)) {
                        Map<String, String> taskNumber = taskGroupByProject.get(projectName);
                        taskNumber.put(DbConstant.TASK_NUMBER, String.valueOf(Integer.parseInt(taskNumber.get(DbConstant.TASK_NUMBER)) + 1));

                    } else {
                        Map<String, String> taskNumber = new HashMap<>();
                        taskNumber.put(DbConstant.PROJECT_NAME,projectName);
                        taskNumber.put(DbConstant.PROJECT_ID,String.valueOf(taskInfo.get(DbConstant.PROJECT_ID)));
                        taskNumber.put(DbConstant.TASK_NUMBER, Common.ONE);
                        taskGroupByProject.put(projectName, taskNumber);
                    }

                } else if (Common.STATISTIC_DIMENSION_TASK.equals(statisticDimension)) {

                    String createTime = DateFormatUtil.removeTimeFromstandardDate(String.valueOf(taskInfo.get(DbConstant.CREATE_TIME)));
                    String receiveTime = DateFormatUtil.removeTimeFromstandardDate(String.valueOf(taskInfo.get(DbConstant.RECEIVE_TIME)));
                    String startTime = DateFormatUtil.removeTimeFromstandardDate(String.valueOf(taskInfo.get(DbConstant.BEGIN_TIME)));
                    String finishTime = DateFormatUtil.removeTimeFromstandardDate(String.valueOf(taskInfo.get(DbConstant.FINISH_TIME)));

                    String taskState = String.valueOf(taskInfo.get(DbConstant.TASK_STATE));
                    String taskStateName = String.valueOf(taskInfo.get(DbConstant.TASK_STATE_NAME));

//                    if (taskGroupByTask.containsKey(createTime)){
//                        if (Common.MODULE_TASK_STATE_CREATE.equals(taskState)){
//                            List<Map<String, String>> taskNumber = taskGroupByTask.get(createTime);
//                            Map<String, String> unReceiveTaskNumber = taskNumber.get(0);
//                            unReceiveTaskNumber.put(createTime, String.valueOf(Integer.parseInt
//                                    (unReceiveTaskNumber.get(createTime)) + 1));
//                        }
//
//                    }else if (taskGroupByTask.containsKey(receiveTime)){
//                        if (Common.MODULE_TASK_STATE_RECEIVE.equals(taskState)){
//                            List<Map<String, String>> taskNumber = taskGroupByTask.get(receiveTime);
//                            Map<String, String> receiveTaskNumber = taskNumber.get(1);
//                            receiveTaskNumber.put(receiveTime, String.valueOf(Integer.parseInt
//                                    (receiveTaskNumber.get(receiveTime)) + 1));
//                        }
//
//                    }else if (taskGroupByTask.containsKey(startTime)){
//                        if (Common.MODULE_TASK_STATE_START.equals(taskState) ||
//                                Common.MODULE_TASK_STATE_PAUSE.equals(taskState)){
//                            List<Map<String, String>> taskNumber = taskGroupByTask.get(startTime);
//                            Map<String, String> processsingTaskNumber = taskNumber.get(2);
//                            processsingTaskNumber.put(startTime, String.valueOf(Integer.parseInt
//                                    (processsingTaskNumber.get(startTime)) + 1));
//                        }
//
//                    }else if (taskGroupByTask.containsKey(finishTime)){
//                        if (Common.MODULE_TASK_STATE_COMPLETE.equals(taskState)
//                                || Common.MODULE_TASK_STATE_CLOSED.equals(taskState)){
//                            List<Map<String, String>> taskNumber = taskGroupByTask.get(finishTime);
//                            Map<String, String> finsihTaskNumber = taskNumber.get(3);
//                            finsihTaskNumber.put(finishTime, String.valueOf(Integer.parseInt
//                                    (finsihTaskNumber.get(finishTime)) + 1));
//                        }
//
//                    }
                    if (taskGroupByTask.containsKey(createTime)){
                        Map<String, String> taskNumber = taskGroupByTask.get(createTime);

                        if (Common.MODULE_TASK_STATE_CREATE.equals(taskState)){

                            taskNumber.put(DbConstant.UN_RECEIVE_TASK_NUMBER, String.valueOf(Integer.parseInt
                                    (taskNumber.get(DbConstant.UN_RECEIVE_TASK_NUMBER)) + 1));
                        }

                    }
                    if (taskGroupByTask.containsKey(receiveTime)){
                        Map<String, String> taskNumber = taskGroupByTask.get(receiveTime);
                        if (Common.MODULE_TASK_STATE_RECEIVE.equals(taskState)){
                            taskNumber.put(DbConstant.RECEIVE_TASK_NUMBER, String.valueOf(Integer.parseInt
                                    (taskNumber.get(DbConstant.RECEIVE_TASK_NUMBER)) + 1));
                        }

                    }
                     if (taskGroupByTask.containsKey(startTime)){
                        Map<String, String> taskNumber = taskGroupByTask.get(startTime);
                        if (Common.MODULE_TASK_STATE_START.equals(taskState) ||
                                Common.MODULE_TASK_STATE_PAUSE.equals(taskState)){
                            taskNumber.put(DbConstant.PROCESSING_TASK_NUMBER, String.valueOf(Integer.parseInt
                                    (taskNumber.get(DbConstant.PROCESSING_TASK_NUMBER)) + 1));
                        }

                    }
                    if (taskGroupByTask.containsKey(finishTime)){
                        Map<String, String> taskNumber = taskGroupByTask.get(finishTime);
                        if (Common.MODULE_TASK_STATE_COMPLETE.equals(taskState)
                                || Common.MODULE_TASK_STATE_CLOSED.equals(taskState)){
                            taskNumber.put(DbConstant.FINISH_TASK_NUMBER, String.valueOf(Integer.parseInt
                                    (taskNumber.get(DbConstant.FINISH_TASK_NUMBER)) + 1));
                        }

                    }
                }  else if (Common.STATISTIC_DIMENSION_TASK_STATE.equals(statisticDimension)) {
//                    String taskStateName = String.valueOf(taskInfo.get(DbConstant.TASK_STATE_NAME));
//                    if (taskGroupByTaskStateName.containsKey(taskStateName)) {
//                        Map<String, String> taskNumber = taskGroupByTaskStateName.get(taskStateName);
//                        taskNumber.put(taskStateName, String.valueOf(Integer.parseInt(taskNumber.get(taskStateName)) + 1));
//                    } else {
//                        Map<String, String> taskNumber = new HashMap<>();
//                        taskNumber.put(taskStateName, Common.ONE);
//                        taskGroupByTaskStateName.put(taskStateName, taskNumber);
//                    }
                    String taskStateName = String.valueOf(taskInfo.get(DbConstant.TASK_STATE_NAME));
                    String taskState = String.valueOf(taskInfo.get(DbConstant.TASK_STATE));
                    if (Common.MODULE_TASK_STATE_START.equals(taskState) ||
                            Common.MODULE_TASK_STATE_PAUSE.equals(taskState)){

                        taskStateName = DbConstant.STATISTIC_PROCESSING_TASK;
                    }else  if (Common.MODULE_TASK_STATE_COMPLETE.equals(taskState)
                            || Common.MODULE_TASK_STATE_CLOSED.equals(taskState)){
                        taskStateName= DbConstant.STATISTIC_FINISH_TASK;
                    }

                    if (taskGroupByTaskStateName.containsKey(taskStateName)) {
                        Map<String, String> taskNumber = taskGroupByTaskStateName.get(taskStateName);
                        taskNumber.put(DbConstant.TASK_NUMBER, String.valueOf(Integer.parseInt(taskNumber.get(DbConstant.TASK_NUMBER)) + 1));
                    } else {
                        Map<String, String> taskNumber = new HashMap<>();
                        taskNumber.put(DbConstant.TASK_STATE_NAME,taskStateName);
                        taskNumber.put(DbConstant.TASK_STATE,taskState);
                        taskNumber.put(DbConstant.TASK_NUMBER, Common.ONE);
                        taskGroupByTaskStateName.put(taskStateName, taskNumber);
                    }

                } else if (Common.STATISTIC_DIMENSION_TASK_CLASSIFY.equals(statisticDimension)) {
                    String classifyName = String.valueOf(taskInfo.get(DbConstant.CLASSIFY_NAME));
                    String classifyCode = String.valueOf(taskInfo.get(DbConstant.CLASSIFY_CODE));
                    if (taskGroupByClassifyName.containsKey(classifyName)) {
                        Map<String, String> taskNumber = taskGroupByClassifyName.get(classifyName);
                        taskNumber.put(DbConstant.TASK_NUMBER, String.valueOf(Integer.parseInt(taskNumber.get(DbConstant.TASK_NUMBER)) + 1));
                    } else {
                        Map<String, String> taskNumber = new HashMap<>();
                        taskNumber.put(DbConstant.CLASSIFY_NAME,classifyName);
                        taskNumber.put(DbConstant.CLASSIFY_CODE,classifyCode);
                        taskNumber.put(DbConstant.TASK_NUMBER, Common.ONE);
                        taskGroupByClassifyName.put(classifyName, taskNumber);
                    }

                } else if (Common.STATISTIC_DIMENSION_USER.equals(statisticDimension)) {

                    String performername = String.valueOf(taskInfo.get(DbConstant.PERFORMER_NAME));
                    String performerId = String.valueOf(taskInfo.get(DbConstant.USER_ID));

                    if (StringUtil.empty(performername)){
                        performername = Common.UN_ACCALIMED;
                    }
                    String taskstate = String.valueOf(taskInfo.get(DbConstant.TASK_STATE));
                    if (taskGroupByUser.containsKey(performername)) {

                        Map<String, String> taskNumber = taskGroupByUser.get(performername);
                        if (Common.MODULE_TASK_STATE_COMPLETE.equals(taskstate) ||
                                Common.MODULE_TASK_STATE_CLOSED.equals(taskstate)) {

                            taskNumber.put(DbConstant.FINISH_TASK_NUMBER, String.valueOf(Integer.parseInt
                                    (taskNumber.get(DbConstant.FINISH_TASK_NUMBER)) + 1));

                        } else {
                            taskNumber.put(DbConstant.PROCESSING_TASK_NUMBER, String.valueOf(Integer.parseInt
                                    (taskNumber.get(DbConstant.PROCESSING_TASK_NUMBER)) + 1));
                        }

                    } else {
                        Map<String, String> taskNumber = new HashMap<>();
                        taskNumber.put(DbConstant.PERFORMER_NAME,performername);
                        taskNumber.put(DbConstant.PERFORMER_ID,performerId);
                        if (Common.MODULE_TASK_STATE_COMPLETE.equals(taskstate) ||
                                Common.MODULE_TASK_STATE_CLOSED.equals(taskstate)) {
                            taskNumber.put(DbConstant.FINISH_TASK_NUMBER, Common.ONE);
                            taskNumber.put(DbConstant.PROCESSING_TASK_NUMBER, Common.ZERO);

                        } else {
                            taskNumber.put(DbConstant.PROCESSING_TASK_NUMBER, Common.ONE);
                            taskNumber.put(DbConstant.FINISH_TASK_NUMBER, Common.ZERO);
                        }

                        taskGroupByUser.put(performername, taskNumber);
                    }
                }

            }

            Map<String,Object> resmap = null;
            TaskInfoCoreLogicProcessor taskInfoCoreLogicProcessor = new TaskInfoCoreLogicProcessor();
            if (Common.STATISTIC_DIMENSION_PROJECT.equals(statisticDimension)){
                graphType = DbConstant.STATISTIC_GRAPH_TYPE_BAR;
                resmap = taskInfoCoreLogicProcessor.getStatisticsGrahData(statisticDimension,graphType,taskGroupByProject,null);
            }else if(Common.STATISTIC_DIMENSION_TASK.equals(statisticDimension)){
                graphType = DbConstant.STATISTIC_GRAPH_TYPE_LINE;
                resmap = taskInfoCoreLogicProcessor.getStatisticsGrahData(statisticDimension,graphType,taskGroupByTask,days);
            }else if(Common.STATISTIC_DIMENSION_TASK_STATE.equals(statisticDimension)){
                graphType = DbConstant.STATISTIC_GRAPH_TYPE_PIE;
                resmap = taskInfoCoreLogicProcessor.getStatisticsGrahData(statisticDimension,graphType,taskGroupByTaskStateName,null);
            }else if(Common.STATISTIC_DIMENSION_USER.equals(statisticDimension)){
//                List<Map<String, String>> userstatistics = new ArrayList<>();//用户统计，Map中放用户id，用户名称，进行中任务量，已完成任务量
//                userstatistics.addAll(taskGroupByUser.values());
//                resmap.put(DbConstant.SERIES_DATA,userstatistics);
//                resmap.put(DbConstant.TYPE,DbConstant.STATISTIC_GRAPH_TYPE_BAR);
//                String[] legend = {Common.STATISTIC_LEGEND_PROCESSING_TASK_NUMBER,Common.STATISTIC_LEGEND_COMPLETE_TASK_NUMBER};
//                resmap.put(DbConstant.LEGEND,legend);
//                resmap.put(DbConstant.X_DATA,taskGroupByUser.keySet());
                graphType = DbConstant.STATISTIC_GRAPH_TYPE_BAR;
                resmap = taskInfoCoreLogicProcessor.getStatisticsGrahData(statisticDimension,graphType,taskGroupByUser,null);
            }

            JsonModel jm = new JsonModel(true,MessageInfo.actionInfo(ActionEnum.STATISTICS.getValue()
                    ,Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,resmap);
            res = JSON.toJSONString(jm);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

}
