package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.Common;
import com.blcultra.cons.DbConstant;
import com.blcultra.cons.Messageconstant;
import com.blcultra.dao.*;
import com.blcultra.dto.LabelDataDto;
import com.blcultra.model.*;
import com.blcultra.queue.DisruptorUtil;
import com.blcultra.service.AnnotationDataSaveService;
import com.blcultra.support.JsonModel;
import com.dayu.util.DateFormatUtil;
import com.dayu.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sgy05 on 2019/3/13.
 */
@Service("annotationDataSaveService")
public class AnnotationDataSaveServiceImpl implements AnnotationDataSaveService {
    private static final Logger log = LoggerFactory.getLogger(AnnotationDataSaveServiceImpl.class);

    @Autowired
    private TaskInfoMapper taskInfoMapper;
    @Autowired
    private AnnotationObjectInfoMapper annotationObjectInfoMapper;
    @Autowired
    private AnnotationObjectRelationInfoMapper annotationObjectRelationInfoMapper;
    @Autowired
    private AnnotationOtherObjectInfoMapper annotationOtherObjectInfoMapper;
    @Autowired
    private TaskReviewInfoMapper taskReviewInfoMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String gloableSaveAnnotationData(LabelDataDto labelDataDto)  {

        try{
            //判断是否为审核任务
            if (Common.MODULE_TASK_TYPE_AUDIT.equals(labelDataDto.getTasktype())){
                //若为审核任务，那么只取参数中的mark信息进行保存评语信息
                List<TaskReviewInfo> taskReviewInfos = labelDataDto.getTaskReviewInfos();
                if (taskReviewInfos !=null || taskReviewInfos.size()>0){
                    taskReviewInfoMapper.batchInserts(taskReviewInfos);
                }
                JsonModel jm = new JsonModel(true, "保存成功", Messageconstant.REQUEST_SUCCESSED_CODE,null);
                String res = JSON.toJSONString(jm);
                return res;
            }
            DisruptorUtil.producer(labelDataDto);//日志记录
            //获取需要删除的item的主键id集合
            List<String> deleteItemIds = labelDataDto.getDeleteItemIds();
            List<String> deleteRelationIds = labelDataDto.getDeleteRelationIds();

            if (labelDataDto.getFlag() == Common.ANNOTATIONDATASAVE_V1_FLAG){
                if (deleteItemIds !=null && deleteItemIds.size()>0){
                    //首先删除此集合中包含的记录
                    annotationObjectInfoMapper.batchDeleteByObjectDataId(deleteItemIds);
                }
                //如果此集合不为空则进行删除
                if (deleteRelationIds !=null && deleteRelationIds.size()>0){
                    annotationObjectRelationInfoMapper.batchDeleteRelationsByObjectDataId(deleteRelationIds);
                }
            }else {
                if (deleteItemIds !=null && deleteItemIds.size()>0){
                    //首先删除此集合中包含的记录
                    annotationObjectInfoMapper.batchDeleteById(deleteItemIds);
                }
                //如果此集合不为空则进行删除
                if (deleteRelationIds !=null && deleteRelationIds.size()>0){
                    annotationObjectRelationInfoMapper.batchDeleteById(deleteRelationIds);
                }
            }
            Map<String,Object> resultmap = new HashMap<>();
            List<AnnotationObjectInfo> insertItems = labelDataDto.getInsertItems();
            List<AnnotationObjectRelationInfo> insertRelations = labelDataDto.getInsertRelations();
            if (insertItems != null && insertItems.size()>0){
                //批量插入
                for (AnnotationObjectInfo item :insertItems){
                    item.setDataitemid(StringUtil.getUUID());
                }
                resultmap.put("insertItems",insertItems);
                annotationObjectInfoMapper.batchInsertItems(insertItems);
            }
            if (insertRelations != null && insertRelations.size()>0){
//                for (AnnotationObjectRelationInfo relate: insertRelations){
//                    relate.setRelationdataid(StringUtil.getUUID());
//                }
                resultmap.put("insertRelations",insertRelations);
                annotationObjectRelationInfoMapper.batchInsertRelations(insertRelations);
            }

            if("saveall".equals(labelDataDto.getSavetype())){//大保存，更新耗时和暂停时间
                Map<String,Object> taskinfo = taskInfoMapper.getAnnotationTaskInfo(labelDataDto.getTaskid());
                long costtime = Long.parseLong(taskinfo.get(DbConstant.COST_TIME)+"");
                String pausetime = taskinfo.get(DbConstant.PAUSE_TIME)+"";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = new Date();
                Date taskpausedate = sdf.parse(pausetime);
                costtime += (now.getTime() - taskpausedate.getTime()) / 1000;

                Map<String,Object> map = new HashMap<>();
                map.put("costtime",costtime);
                map.put("pausetime",sdf.format(now));
                map.put("taskid",labelDataDto.getTaskid());
                int n = taskInfoMapper.updateTaskCostTimeByTaskId(map);
            }

            JsonModel jm = new JsonModel(true, "保存成功", Messageconstant.REQUEST_SUCCESSED_CODE,resultmap);
            String res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//关键
            JsonModel jm = new JsonModel(false, "保存失败", Messageconstant.REQUEST_FAILED_CODE,null);
            String res = JSON.toJSONString(jm);
            return res;
        }


    }

    @Override
    public String saveAnnotationData(Map data) {
        String res;
        try {
            String dataitemid = data.get("dataitemid")+"";
            if(StringUtil.empty(dataitemid)){//初次保存
                dataitemid = StringUtil.getUUID();
                AnnotationOtherObjectInfo annotationOtherObjectInfo = new AnnotationOtherObjectInfo();
                String dataid = StringUtil.getUUID();
                annotationOtherObjectInfo.setDataitemid(dataid);
                annotationOtherObjectInfo.setItemid(dataid);
                annotationOtherObjectInfo.setObjectdataid(data.get("objectdataid")+"");
                annotationOtherObjectInfo.setDatainfo(data.get("item"));
                annotationOtherObjectInfo.setCreatetime(DateFormatUtil.DateFormat());
                annotationOtherObjectInfo.setAnnotationtype(data.get("annotationtype")+"");
                annotationOtherObjectInfoMapper.updateByPrimaryKeySelective(annotationOtherObjectInfo);
            }else {//信息修改
                AnnotationOtherObjectInfo  annotationObjectInfo = new AnnotationOtherObjectInfo();
                annotationObjectInfo.setDataitemid(dataitemid);
                annotationObjectInfo.setDatainfo(data.get("item"));
                annotationOtherObjectInfoMapper.updateByPrimaryKeySelective(annotationObjectInfo);
            }
            JsonModel jm = new JsonModel(true, "保存成功", Messageconstant.REQUEST_SUCCESSED_CODE,dataitemid);
            res = JSON.toJSONString(jm);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            JsonModel jm = new JsonModel(false, "保存失败", Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
        }
        return res;
    }

    @Override
    public String deleteAnnotationData(String dataitemid) {
        String res;
        try {
            annotationOtherObjectInfoMapper.deleteByPrimaryKey(dataitemid);
            JsonModel jm = new JsonModel(true, "删除成功", Messageconstant.REQUEST_SUCCESSED_CODE,null);
            res = JSON.toJSONString(jm);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            JsonModel jm = new JsonModel(false, "删除失败", Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
        }
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveAnnotationAllData(Map data) {
        String res;
        try {
            List<AnnotationOtherObjectInfo> insertlist = new ArrayList<>();
            List items = (List) data.get("items");
            if(null != items && items.size() > 0){
                for(Object item : items){
                    JSONObject datainfo = JSONObject.parseObject(JSONObject.toJSONString(item));
                    String dataitemid = datainfo.get("dataitemid")+"";
                    if(! StringUtil.empty(dataitemid)){
                        AnnotationOtherObjectInfo  annotationObjectInfo = new AnnotationOtherObjectInfo();
                        annotationObjectInfo.setDataitemid(dataitemid);
                        annotationObjectInfo.setDatainfo(datainfo.toJSONString());
                        annotationOtherObjectInfoMapper.updateByPrimaryKeySelective(annotationObjectInfo);
                    }else{
                        AnnotationOtherObjectInfo annotationOtherObjectInfo = new AnnotationOtherObjectInfo();
                        String dataid = StringUtil.getUUID();
                        annotationOtherObjectInfo.setDataitemid(dataid);
                        annotationOtherObjectInfo.setItemid(dataid);
                        annotationOtherObjectInfo.setObjectdataid(data.get("objectdataid")+"");
                        annotationOtherObjectInfo.setDatainfo(datainfo.toJSONString());
                        annotationOtherObjectInfo.setCreatetime(DateFormatUtil.DateFormat());
                        annotationOtherObjectInfo.setAnnotationtype(datainfo.get("type")+"");
                        insertlist.add(annotationOtherObjectInfo);
                    }
                }
            }
            if(insertlist.size() > 0){
                annotationOtherObjectInfoMapper.batchInsertItems(insertlist);
            }
            JsonModel jm = new JsonModel(true, "保存成功", Messageconstant.REQUEST_SUCCESSED_CODE,null);
            res = JSON.toJSONString(jm);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            JsonModel jm = new JsonModel(false, "保存失败", Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
        }
        return res;
    }

    /**
     * 计算耗时
     * @param taskpausetime
     * @param costtime
     * @return
     */
    private String calculateTime(String taskpausetime,String costtime){

        try {
            String now = DateFormatUtil.DateFormat();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date nowdate = sdf.parse(now);
            long time = nowdate.getTime();
            Date pause = sdf.parse(taskpausetime);
            long pauseseconds = pause.getTime();
            long ct = Long.parseLong(costtime == null ? "0" : costtime);
            long seconds = (time - pauseseconds)/1000;
            long cost = ct+seconds;
            log.info("Calculate 耗时"+cost);
            String costt = String.valueOf(cost);
            return costt;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        log.error("计算耗时异常。。");
        return null;
    }

}
