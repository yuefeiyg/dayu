package com.blcultra.service.core;

import com.blcultra.cons.Common;
import com.blcultra.cons.DbConstant;
import com.dayu.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.*;

public class TaskInfoCoreLogicProcessor {

    public Map<String,String> getNewTaskInfo(String userId, String newPerformerId, String newFinishDeadline, String desc,
                                              Map<String,Object> taskInfo, String dataids, boolean directReturn){

        String taskId = String.valueOf(taskInfo.get(DbConstant.TASK_ID));
        String performerId = String.valueOf(taskInfo.get(DbConstant.PERFORMER_ID));
        String classifyCode = String.valueOf(taskInfo.get(DbConstant.CLASSIFY_CODE));
        String templateId = String.valueOf(taskInfo.get(DbConstant.TEMPLATE_ID));
        String projectId = String.valueOf(taskInfo.get(DbConstant.PROJECT_ID));
//        String taskType = String.valueOf(taskInfo.get(DbConstant.TASK_TYPE));
        String taskTitle = String.valueOf(taskInfo.get(DbConstant.TASK_TITLE));
        String callbackTimes = String.valueOf(taskInfo.get(DbConstant.CALL_BACK_TIMES));
        String auditTimes = String.valueOf(taskInfo.get(DbConstant.AUDIT_TIMES));

        Map<String,String> newTaskInfo = new HashMap<>();

        if (directReturn){//直接打回
            newTaskInfo.put(DbConstant.TASK_TYPE, Common.MODULE_TASK_TYPE_TASK);
            newTaskInfo.put(DbConstant.TASK_TITLE,taskTitle+"("+String.valueOf(Long.parseLong(callbackTimes)+1)+")");
            newTaskInfo.put(DbConstant.CALL_BACK_TIMES,String.valueOf(Long.parseLong(callbackTimes)+1));

        }else {

            if (!newPerformerId.equals(performerId)){//判断打回人和原经办人是否一致
                newTaskInfo.put(DbConstant.TASK_TYPE,Common.MODULE_TASK_TYPE_AUDIT);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = new Date();
                newTaskInfo.put(DbConstant.TASK_TITLE,"("+Common.AUDIT+" "+sdf.format(now)+")"+taskTitle+
                        "("+String.valueOf(Long.parseLong(auditTimes)+1)+")");
                newTaskInfo.put(DbConstant.AUDIT_TIMES,String.valueOf(Long.parseLong(auditTimes)+1));
            }else {
                newTaskInfo.put(DbConstant.TASK_TYPE,Common.MODULE_TASK_TYPE_TASK);
                newTaskInfo.put(DbConstant.TASK_TITLE,taskTitle+"("+String.valueOf(Long.parseLong(callbackTimes)+1)+")");
                newTaskInfo.put(DbConstant.CALL_BACK_TIMES,String.valueOf(Long.parseLong(callbackTimes)+1));
            }
        }


        newTaskInfo.put(DbConstant.CLASSIFY_CODE,classifyCode);
        newTaskInfo.put(DbConstant.TEMPLATE_ID,templateId);
        newTaskInfo.put(DbConstant.PROJECT_ID,projectId);
        if (!StringUtil.empty(desc)){
            newTaskInfo.put(DbConstant.TASK_DESC,desc);
        }

        newTaskInfo.put(DbConstant.CREATEUSER_ID,userId);
        newTaskInfo.put(DbConstant.PERFORMER_ID,newPerformerId);
        newTaskInfo.put(DbConstant.PREV_PERFORMER_ID,performerId);
        newTaskInfo.put(DbConstant.FINISH_DEADLINE,newFinishDeadline);
        if (StringUtil.empty(String.valueOf(taskInfo.get(DbConstant.PROJECT_OWNER)))){

            newTaskInfo.put(DbConstant.TASKOWNER_ID,String.valueOf(taskInfo.get(DbConstant.PROJECT_OWNER)));
        }else {
            newTaskInfo.put(DbConstant.TASKOWNER_ID,userId);
        }

//        String newDataIds = org.apache.commons.lang3.StringUtils.join(dataids, ",");

        newTaskInfo.put(DbConstant.DATA_IDS,dataids);

        return newTaskInfo;
    }

    public boolean needToUpdateNewConent(String newContent,String oldContent){
        return !oldContent.equals(newContent);
    }

    private List<String> getLineOrBarData(Map<String, Map<String, String>> data,List<String> xdatas,String key,String legend,String graphType){
        Map<String, Object> result = new HashMap<>();
//        result.put(DbConstant.NAME,legend);
//        result.put(DbConstant.TYPE,graphType);
        List<String> sdata = new ArrayList<>();
        if (xdatas == null || xdatas.size() == 0){
            for (String xdata : data.keySet()){
                Map<String, String> subdata = data.get(xdata);
                String value = subdata.get(key);
                if (StringUtil.empty(value)){
                    sdata.add(Common.ZERO);
                }else {
                    sdata.add(value);
                }
            }
        }else {

            for (String xdata : xdatas){
                Map<String, String> subdata = data.get(xdata);
                String value = subdata.get(key);
                if (StringUtil.empty(value)){
                    sdata.add(Common.ZERO);
                }else {
                    sdata.add(value);
                }
            }
        }
//        result.put(DbConstant.DATA,sdata);
        return sdata;
    }

    private List<Map<String,String>> getPieData(Map<String, Map<String, String>> data,String[] legends,String graphType,String name){
//        Map<String, Object> result = new HashMap<>();
//        result.put(DbConstant.NAME,name);
//        result.put(DbConstant.TYPE,graphType);
        List<Map<String,String>> sdata = new ArrayList<>();
        for (int i = 0;i<legends.length;i++){
            Map<String,String> value = new HashMap<>();
            value.put(DbConstant.NAME,legends[i]);
            Map<String, String> temp = data.get(legends[i]);
            value.put(DbConstant.VALUE,temp == null?"0":temp.get(DbConstant.TASK_NUMBER));
            sdata.add(value);
        }
//        result.put(DbConstant.DATA,sdata);
        return sdata;
    }

    public Map<String,Object> getStatisticsGrahData(String statisticDimension,String graphType,Map<String, Map<String, String>> data,List<String> xdatas){
        Map<String,Object> resmap = new HashMap<>();

        if (Common.STATISTIC_DIMENSION_PROJECT.equals(statisticDimension)){
            List<List<String>> series = new ArrayList<>();
            String[] legends = {Common.STATISTIC_LEGEND_TASK_NUMBER};
            String[] keys = {DbConstant.TASK_NUMBER};

            if (DbConstant.STATISTIC_GRAPH_TYPE_BAR.equals(graphType)){
                for (int i= 0;i<legends.length;i++) {
                    List<String> value = getLineOrBarData(data,xdatas,keys[i],legends[i],graphType);
                    series.add(value);
                }
                resmap.put(DbConstant.X_DATA,data.keySet());
            }
            resmap.put(DbConstant.SERIES_DATA,series);
            resmap.put(DbConstant.TYPE,graphType);
            resmap.put(DbConstant.LEGEND,legends);

        }else if(Common.STATISTIC_DIMENSION_TASK.equals(statisticDimension)){
            List<List<String>> series = new ArrayList<>();
            String[] legends = {DbConstant.STATISTIC_CREATE_TASK,DbConstant.STATISTIC_RECEIVE_TASK,
                    DbConstant.STATISTIC_PROCESSING_TASK,DbConstant.STATISTIC_FINISH_TASK};
            String[] keys = {DbConstant.UN_RECEIVE_TASK_NUMBER,DbConstant.RECEIVE_TASK_NUMBER,
                    DbConstant.PROCESSING_TASK_NUMBER,DbConstant.FINISH_TASK_NUMBER};

            if (DbConstant.STATISTIC_GRAPH_TYPE_LINE.equals(graphType)){
                for (int i= 0;i<legends.length;i++) {
                    List<String> value = getLineOrBarData(data,xdatas,keys[i],legends[i],graphType);
                    series.add(value);
                }
                resmap.put(DbConstant.X_DATA,xdatas);
            }
            resmap.put(DbConstant.SERIES_DATA,series);
            resmap.put(DbConstant.TYPE,graphType);
            resmap.put(DbConstant.LEGEND,legends);
        }else if(Common.STATISTIC_DIMENSION_TASK_STATE.equals(statisticDimension)){

            String[] legends = {DbConstant.STATISTIC_CREATE_TASK,DbConstant.STATISTIC_RECEIVE_TASK,
                    DbConstant.STATISTIC_PROCESSING_TASK,DbConstant.STATISTIC_FINISH_TASK};
            List<Map<String, String>> series = null;
            if (DbConstant.STATISTIC_GRAPH_TYPE_PIE.equals(graphType)){

                series = getPieData(data,legends,graphType,Common.TASK_STATE_STATISTIC_NAME);
            }
            resmap.put(DbConstant.SERIES_DATA,series);
            resmap.put(DbConstant.TYPE,graphType);
            resmap.put(DbConstant.LEGEND,legends);

        }else if(Common.STATISTIC_DIMENSION_USER.equals(statisticDimension)){
            String[] legends = {Common.STATISTIC_LEGEND_PROCESSING_TASK_NUMBER,Common.STATISTIC_LEGEND_COMPLETE_TASK_NUMBER};
            String[] keys = {DbConstant.PROCESSING_TASK_NUMBER,DbConstant.FINISH_TASK_NUMBER};

            List<List<String>> series = new ArrayList<>();
            if (DbConstant.STATISTIC_GRAPH_TYPE_BAR.equals(graphType)){
                for (int i= 0;i<legends.length;i++) {
                    List<String> value = getLineOrBarData(data,xdatas,keys[i],legends[i],graphType);
                    series.add(value);
                }
                resmap.put(DbConstant.X_DATA,data.keySet());
            }
            resmap.put(DbConstant.SERIES_DATA,series);
            resmap.put(DbConstant.TYPE,DbConstant.STATISTIC_GRAPH_TYPE_BAR);
            resmap.put(DbConstant.LEGEND,legends);
        }
        resmap.put(Common.STATISITIC_DIMENSION,statisticDimension);

        return resmap;

    }


}
