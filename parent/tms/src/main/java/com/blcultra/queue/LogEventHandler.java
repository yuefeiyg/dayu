package com.blcultra.queue;

import com.blcultra.dao.*;
import com.blcultra.dto.LabelDataDto;
import com.blcultra.model.*;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.SpringBeanFactory;
import com.dayu.util.DateFormatUtil;
import com.dayu.util.StringUtil;
import com.lmax.disruptor.WorkHandler;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 日志时间处理器,消费
 * Created by sgy05 on 2019/1/9.
 */
public class LogEventHandler implements WorkHandler<LabelDataDto> {
    Logger log= LoggerFactory.getLogger(this.getClass());

    private AnnotationObjectInfoMapper annotationObjectInfoMapper = SpringBeanFactory.getBean(AnnotationObjectInfoMapper.class);
    private AnnotationObjectRelationInfoMapper annotationObjectRelationInfoMapper = SpringBeanFactory.getBean(AnnotationObjectRelationInfoMapper.class);
    private LogAnnotationObjectInfoMapper logAnnotationObjectInfoMapper = SpringBeanFactory.getBean(LogAnnotationObjectInfoMapper.class);
    private LogAnnotationRelationsMapper logAnnotationRelationsMapper = SpringBeanFactory.getBean(LogAnnotationRelationsMapper.class);


    @Override
    public void onEvent(LabelDataDto labelDataDto) {

        try{
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userid = JWTUtil.getUserId(token);
            String taskid = labelDataDto.getTaskid();

            //获取需要删除的item的主键id集合
            List<String> deleteItemIds = labelDataDto.getDeleteItemIds();
            List<String> deleteRelationIds = labelDataDto.getDeleteRelationIds();
            //处理业务逻辑
            if (deleteItemIds !=null && deleteItemIds.size()>0){
                //首先插入删除操作记录
                List<Map<String, Object>> adinfos = annotationObjectInfoMapper.getAnnotationDataInfosByDataItemIds(deleteItemIds);
                List<LogAnnotationObjectInfo> logaoinfos = new ArrayList<>();
                for (Map<String, Object> adinfo : adinfos) {
                    LogAnnotationObjectInfo logAnnotationObjectInfo = new LogAnnotationObjectInfo();
                    logAnnotationObjectInfo.setLogid(StringUtil.getUUID());
                    logAnnotationObjectInfo.setTaskid(taskid);
                    logAnnotationObjectInfo.setUserid(userid);
                    logAnnotationObjectInfo.setCreateordellable("delete");
                    logAnnotationObjectInfo.setStartoffset(String.valueOf(adinfo.get("startoffset")));
                    logAnnotationObjectInfo.setEndoffset(String.valueOf(adinfo.get("endoffset")));
                    logAnnotationObjectInfo.setItemid(String.valueOf(adinfo.get("itemid")));
                    logAnnotationObjectInfo.setItem(String.valueOf(adinfo.get("item")));
                    logAnnotationObjectInfo.setLabelid(String.valueOf(adinfo.get("labelid")));
                    logAnnotationObjectInfo.setObjectdataid(String.valueOf(adinfo.get("objectdataid")));
                    logAnnotationObjectInfo.setBndbox(String.valueOf(adinfo.get("bndbox")));
                    logAnnotationObjectInfo.setOperatetime(DateFormatUtil.DateFormat());
                    logaoinfos.add(logAnnotationObjectInfo);
                }
                if (logaoinfos != null && logaoinfos.size()>0){
                    logAnnotationObjectInfoMapper.batchInsertLogAnnotationObjectInfo(logaoinfos);
                }
            }

            if (deleteRelationIds !=null && deleteRelationIds.size()>0){
                List<Map<String, Object>> relationdatas =
                        annotationObjectRelationInfoMapper.getRelateLableDatasByRelationIds(deleteItemIds);
                List<LogAnnotationRelations> logannotationRelationinfos = new ArrayList<>();
                for (Map<String, Object> relationdata : relationdatas) {
                    LogAnnotationRelations logAnnotationRelations = new LogAnnotationRelations();
                    logAnnotationRelations.setLogid(StringUtil.getUUID());
                    logAnnotationRelations.setTaskid(taskid);
                    logAnnotationRelations.setUserid(userid);
                    logAnnotationRelations.setOperatetime(DateFormatUtil.DateFormat());
                    logAnnotationRelations.setRelationdataid(String.valueOf(relationdata.get("relationdataid")));
                    logAnnotationRelations.setObjectdataid(String.valueOf(relationdata.get("objectdataid")));
                    logAnnotationRelations.setLabelid(String.valueOf(relationdata.get("labelid")));
                    logAnnotationRelations.setFromitemid(String.valueOf(relationdata.get("fromitemid")));
                    logAnnotationRelations.setToitemid(String.valueOf(relationdata.get("toitemid")));
                    logAnnotationRelations.setCreateordellable("delete");
                    logannotationRelationinfos.add(logAnnotationRelations);
                }
                if (logannotationRelationinfos != null && logannotationRelationinfos.size()>0){
                    logAnnotationRelationsMapper.batchInsertLogRelations(logannotationRelationinfos);
                }

            }
            List<AnnotationObjectInfo> insertItems = labelDataDto.getInsertItems();
            List<AnnotationObjectRelationInfo> insertRelations = labelDataDto.getInsertRelations();

            if (insertItems != null && insertItems.size()>0){
                List<LogAnnotationObjectInfo> logAnnotationsInsert = new ArrayList<>();
                for (AnnotationObjectInfo insertItem : insertItems) {

                    LogAnnotationObjectInfo logAnnotationObjectInfo = new LogAnnotationObjectInfo();
                    logAnnotationObjectInfo.setLogid(StringUtil.getUUID());
                    logAnnotationObjectInfo.setTaskid(taskid);
                    logAnnotationObjectInfo.setUserid(userid);
                    logAnnotationObjectInfo.setCreateordellable("insert");
                    logAnnotationObjectInfo.setStartoffset(insertItem.getStartoffset());
                    logAnnotationObjectInfo.setEndoffset(insertItem.getEndoffset());
                    logAnnotationObjectInfo.setItemid(insertItem.getItemid());
                    logAnnotationObjectInfo.setItem(insertItem.getItem());
                    logAnnotationObjectInfo.setLabelid(insertItem.getLabelid());
                    logAnnotationObjectInfo.setObjectdataid(insertItem.getObjectdataid());
                    logAnnotationObjectInfo.setBndbox(insertItem.getBndbox());
                    logAnnotationObjectInfo.setOperatetime(DateFormatUtil.DateFormat());
                    logAnnotationsInsert.add(logAnnotationObjectInfo);
                }
                if (logAnnotationsInsert != null && logAnnotationsInsert.size()>0){
                    logAnnotationObjectInfoMapper.batchInsertLogAnnotationObjectInfo(logAnnotationsInsert);
                }
            }
            if (insertRelations != null && insertRelations.size()>0){

                List<LogAnnotationRelations> logRelationsInsert = new ArrayList<>();
                for (AnnotationObjectRelationInfo insertRelation : insertRelations) {
                    LogAnnotationRelations logAnnotationRelations = new LogAnnotationRelations();
                    logAnnotationRelations.setLogid(StringUtil.getUUID());
                    logAnnotationRelations.setTaskid(taskid);
                    logAnnotationRelations.setUserid(userid);
                    logAnnotationRelations.setOperatetime(DateFormatUtil.DateFormat());
                    logAnnotationRelations.setRelationdataid(insertRelation.getRelationdataid());
                    logAnnotationRelations.setObjectdataid(insertRelation.getObjectdataid());
                    logAnnotationRelations.setLabelid(insertRelation.getLabelid());
                    logAnnotationRelations.setFromitemid(insertRelation.getFromitemid());
                    logAnnotationRelations.setToitemid(insertRelation.getToitemid());
                    logAnnotationRelations.setCreateordellable("insert");
                    logRelationsInsert.add(logAnnotationRelations);
                }
                if (logRelationsInsert != null && logRelationsInsert.size()>0){
                    logAnnotationRelationsMapper.batchInsertLogRelations(logRelationsInsert);
                }
            }
        }catch (Exception e){
            log.error(">>>>>>>>>>>>>>>>>>>>>>>>>>> 插入日志表异常 <<<<<<<<<<<<<<<<<<<<<<<<<");
            log.error(e.getMessage(),e);
        }

    }
}
