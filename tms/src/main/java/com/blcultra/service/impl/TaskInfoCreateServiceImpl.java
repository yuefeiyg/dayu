package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.*;
import com.blcultra.dao.*;
import com.blcultra.exception.ExceptionUtil;
import com.blcultra.exception.ServiceException;
import com.blcultra.model.ProjectInfo;
import com.blcultra.model.TaskInfo;
import com.blcultra.service.TaskInfoCreateService;
import com.blcultra.service.core.TaskInfoCoreLogicProcessor;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.JsonModel;
import com.blcultra.support.ReturnCode;
import com.dayu.util.DateFormatUtil;
import com.dayu.util.JsonFileUtil;
import com.dayu.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.*;
@Service("taskInfoCreateService")
public class TaskInfoCreateServiceImpl implements TaskInfoCreateService{

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

    @Autowired
    private UserMapper userMapper;

    @Override
    public String addTask(Map<String, String> task) {

        Subject subject = SecurityUtils.getSubject();
        String userId = JWTUtil.getUserId(subject.getPrincipals().toString());
//        String userId = task.get("userid");

        return createTask(task,userId,null);
    }

    //oldTaskId审核不通过打回时需要词参数
    @Transactional(rollbackFor = Exception.class)
    public String createTask(Map<String, String> task,String userId,String oldTaskId){

        String res = null;
        try{

            //beforeValidate(task);

            TaskInfo existedTask = taskInfoMapper.getTaskByProjectName(task.get(DbConstant.TASK_TITLE),task.get(DbConstant.PROJECT_ID),userId);
            if (existedTask != null){
                JsonModel jm = new JsonModel(false, BusinessEnum.TASK_EXISTED.getValue(), Messageconstant.REQUEST_FAILED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }
            TaskInfo taskInfo = new TaskInfo();
            //todo map set value to taskInfo
            taskInfo.setTasktitle(task.get(DbConstant.TASK_TITLE));
            taskInfo.setProjectid(task.get(DbConstant.PROJECT_ID));
            taskInfo.setClassifycode(task.get(DbConstant.CLASSIFY_CODE));

            String prevPerformerId = task.get(DbConstant.PREV_PERFORMER_ID);
            if (!StringUtils.isEmpty(prevPerformerId)){
                taskInfo.setPrevperformerid(prevPerformerId);
            }

            String callBackTimes = task.get(DbConstant.CALL_BACK_TIMES);
            if (!StringUtils.isEmpty(callBackTimes)){
                taskInfo.setCallbacktimes(callBackTimes);
            }else {
                taskInfo.setCallbacktimes(Common.ZERO);
            }

            String auditTimes = task.get(DbConstant.AUDIT_TIMES);
            if (!StringUtils.isEmpty(auditTimes)){
                taskInfo.setAudittimes(auditTimes);
            }else {
                taskInfo.setAudittimes(Common.ZERO);
            }

            String version = task.get(DbConstant.TASK_VERSION);
            if (!StringUtils.isEmpty(version)){
                taskInfo.setVersion(version);
            }else {
                taskInfo.setVersion(Common.ZERO);
            }

            if (Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(task.get(DbConstant.CLASSIFY_CODE))){
                taskInfo.setTemplateid(task.get(DbConstant.TEMPLATE_ID));
            }
            String taskType = task.get(DbConstant.TASK_TYPE);
            if (!StringUtils.isEmpty(taskType)){
                taskInfo.setTasktype(taskType);
            }else {
                taskInfo.setTasktype(Common.MODULE_TASK_TYPE_TASK);
            }

            String taskDesc = task.get(DbConstant.TASK_DESC);
            if (!StringUtils.isEmpty(taskDesc)){
                taskInfo.setTaskdesc(taskDesc);
            }

            String performerid = task.get(DbConstant.PERFORMER_ID);
            if (StringUtils.isEmpty(performerid)){//执行人ID为空，说明未指定执行人，故为只是已发布状态
                taskInfo.setTaskstate(Common.MODULE_TASK_STATE_CREATE);//dictCode: 002001 任务创建并发布：任务待领取
            }else{//反之，指定了执行人，那么该任务的状态为已认领
                taskInfo.setTaskstate(Common.MODULE_TASK_STATE_RECEIVE);//dictCode: 002002 任务已领取
                taskInfo.setReceivetime(DateFormatUtil.DateFormat());
                taskInfo.setPerformerid(performerid);
            }

            String deadlineTime = task.get(DbConstant.FINISH_DEADLINE);
            if (!StringUtils.isEmpty(deadlineTime)){
                taskInfo.setFinishdeadline(deadlineTime);
            }

            String note = task.get(DbConstant.NOTE);
            if (!StringUtils.isEmpty(note)){
                taskInfo.setNote(note);
            }

            String taskid = StringUtil.getUUID();

            taskInfo.setCreatetime(DateFormatUtil.DateFormat());//创建时间
            taskInfo.setUpdatetime(DateFormatUtil.DateFormat());//更新时间初始化
            taskInfo.setTaskid(taskid);
            taskInfo.setCreateuserid(userId);
            taskInfo.setCosttime(Common.ZERO);
            taskInfo.setResultdataset(task.get(DbConstant.RESULT_DATASET));
            String taskownerId = task.get(DbConstant.TASKOWNER_ID);
            if (!StringUtils.isEmpty(taskownerId)){
                taskInfo.setTaskownerid(taskownerId);
            }else {
                taskInfo.setTaskownerid(userId);
            }

            //插入任务的数据至相关表中
            String dataids = task.get(DbConstant.DATA_IDS);
            String attFiles = task.get(DbConstant.ATT_FILES);


            if ("001004".equals(taskType) && StringUtil.empty(dataids) && StringUtil.empty(attFiles)){
                JsonModel jm = new JsonModel(false, BusinessEnum.TASK_DATA_NOT_EXISTED.getValue(),
                        Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }

            int totalWords = 0;
            int taskDataNumber = 0;

            List<Map<String,String>> taskAttachInfos = new ArrayList<>();
            List<Map<String,String>> taskAnnotationDatas = new ArrayList<>();
            List<Map<String,String>> annotationObjectInfos = new ArrayList<>();
            List<Map<String,String>> annotationObjectRelationInfos = new ArrayList<>();
            List<Map<String,String>> taskReviewInfos = new ArrayList<>();

            if ("001004".equals(taskType) && !StringUtil.empty(dataids)){//标注任务才会选择数据集
                //如果选择是整个数据集，此时dataids是数据集id
                boolean selectWholeDataset = false;
                String whole = task.get(DbConstant.SELECT_WHOLE_DATASET);
                if (!StringUtil.empty(whole)){
                    selectWholeDataset = Boolean.parseBoolean(task.get(DbConstant.SELECT_WHOLE_DATASET));
                }
                List<String> dataItemIds = new ArrayList<>();
                if (selectWholeDataset){
                    List<String> dataIdsInDataset = dataInfoMapper.getDataIds(dataids);
                    dataItemIds.addAll(dataIdsInDataset);
                }else {
                    dataItemIds.addAll(new ArrayList<>(Arrays.asList(dataids.split(","))));
                }
                List<Map<String,Object>> dataItmes = dataInfoMapper.getDataInfoByDataIds(dataItemIds);
                if (Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(task.get(DbConstant.CLASSIFY_CODE))) {

                    Map<String,Object> template = annotationTemplateMapper.getTemplateInfoById(task.get(DbConstant.TEMPLATE_ID));

                    String labelType = String.valueOf(template.get(DbConstant.ANNOTATION_TEMPLATE_ANNOTATION_TYPE));
                    String templateType = String.valueOf(template.get(DbConstant.ANNOTATION_TEMPLATE_TYPE));
                    String annotationDataType = String.valueOf(template.get(DbConstant.ANNOTATION_TEMPLATE_ANNOTATION_OBJECT));
                    String annotationType = String.valueOf(template.get(DbConstant.ANNOTATION_TEMPLATE_ANNOTATION_TYPE));

                    // String hasRelationType = "通过模板ID获取,是双对象时，为true，其他时为false";
                    String hasRelationType = Common.TASK_ANNO_DATA_RELATION_NO;
                    if (Common.DOUBLE_OBJECT_ATTRIBUTE_LABEL.equals(templateType)){
                        hasRelationType = Common.TASK_ANNO_DATA_RELATION_YES;
                    }
                    //TODO insert into biaozhu table

                    List<String> dataSourceItems = new ArrayList<>();

                    for (Map<String,Object> dataItme: dataItmes) {
                        String dataType = String.valueOf(dataItme.get(DbConstant.DATA_OBJECT_TYPE));
                        if (!annotationDataType.equals(dataType)){
                            continue;
                        }
                        String dataId = String.valueOf(dataItme.get(DbConstant.DATA_ID));
                        if (!dataSourceItems.contains(dataId)){
                            taskDataNumber += 1;
                            dataSourceItems.add(dataId);
                            if(Common.DATA_TYPE_TEXT.equals(dataType)){
                                int wordCount = Integer.parseInt(String.valueOf(dataItme.get(DbConstant.WORD_COUNT)));
                                totalWords += wordCount;
                            }
                        }
                        String dataName = String.valueOf(dataItme.get(DbConstant.DATA_NAME));
                        String dataPath = String.valueOf(dataItme.get(DbConstant.DATA_PATH));
                        String contentId = String.valueOf(dataItme.get(DbConstant.CONTENT_ID));

                        Map<String,String> taskAnnotationData = new HashMap<>();

                        String objectDataId = StringUtil.getUUID();
                        taskAnnotationData.put(DbConstant.TASK_ANNO_OBJECT_DATA_ID,objectDataId);
                        taskAnnotationData.put(DbConstant.TASK_ANNO_DATA_TYPE,dataType);
                        taskAnnotationData.put(DbConstant.TASK_ANNO_TASK_ID,taskid);
                        taskAnnotationData.put(DbConstant.TASK_ANNO_SOURCE_ID,dataId);

                        taskAnnotationData.put(DbConstant.TASK_ANNO_LABEL_TYPE,labelType);
                        taskAnnotationData.put(DbConstant.TASK_ANNO_DATA_ID,contentId);

                        taskAnnotationData.put(DbConstant.TASK_ANNO_RELATION,hasRelationType);
                        taskAnnotationData.put(DbConstant.TASK_ANNO_EXECUTE_STATE,Common.DATA_EXCUTE_STATE_NO);

                        taskAnnotationDatas.add(taskAnnotationData);

//                        Map<String,String> taskAttachInfo = new HashMap<>();
////                    String attachmentId = StringUtil.getUUID();
//                        taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_ATTACHMENT_ID,dataId);
//                        taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_ATTACHMENT_NAME,dataName);
//                        taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_ATTACHMENT_TYPE,Common.ATTACHMENT_TYPE_DATA);
//                        taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_PATH,dataPath);
//                        taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_TASK_ID,taskid);
//
//                        taskAttachInfos.add(taskAttachInfo);

                        String anninfos = String.valueOf(dataItme.get(DbConstant.ANNINFOS));
                        //TODO anninfos 解析至标注结果页
                        if (!StringUtil.empty(anninfos)){
                            JSONObject anninfosObject = JSON.parseObject(anninfos);

                            String templateId =anninfosObject.getString(DbConstant.TEMPLATE_ID);
//                            if (templateId.equals(task.get(DbConstant.TEMPLATE_ID))){

                                JSONArray items = anninfosObject.getJSONArray(Common.ITEMS);

                                JSONArray relations = anninfosObject.getJSONArray(Common.RELATIONS);

                                if (items != null){

                                    for (Object item: items) {

                                        JSONObject annItem = (JSONObject)item;

                                        String dataItemId = StringUtil.getUUID();
                                        String itemId = String.valueOf(annItem.get(DbConstant.ANNOTATION_OBJECT_ITEM_ID));
                                        String labelId = String.valueOf(annItem.get(DbConstant.ANNOTATION_OBJECT_LABEL_ID));
                                        String createtime = String.valueOf(annItem.get(DbConstant.ANNOTATION_OBJECT_CREATETIME));

                                        Map<String,String> annotationObjectInfo = new HashMap<>();
                                        annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_DATA_ITEM_ID,dataItemId);
                                        annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_ITEM_ID,itemId);
                                        annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_OBJECT_DATA_ID,objectDataId);
                                        annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_LABEL_ID,labelId);
                                        annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_CREATETIME,createtime);

                                        if(Common.DATA_TYPE_TEXT.equals(dataType)){
                                            String itemContent = String.valueOf(annItem.get(DbConstant.ANNOTATION_OBJECT_OBJECT_ITEM));
                                            String startOffset = String.valueOf(annItem.get(DbConstant.ANNOTATION_OBJECT_START_OFFSET));
                                            String endOffset = String.valueOf(annItem.get(DbConstant.ANNOTATION_OBJECT_END_OFFSET));
                                            annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_OBJECT_ITEM,itemContent);
                                            annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_START_OFFSET,startOffset);
                                            annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_END_OFFSET,endOffset);
                                        }else if (Common.DATA_TYPE_IMAGE.equals(dataType)){

                                            String bandbox = String.valueOf(annItem.get(DbConstant.ANNOTATION_OBJECT_BNDBOX));
                                            annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_BNDBOX,bandbox);
                                        }
                                        annotationObjectInfos.add(annotationObjectInfo);

                                    }
                                }

                                if (relations != null){

                                    for (Object relation: relations) {

                                        JSONObject annRelation = (JSONObject) relation;

                                        String relationDataId = StringUtil.getUUID();
                                        String labelId = String.valueOf(annRelation.get(DbConstant.ANNOTATION_OBJECT_LABEL_ID));
                                        String fromItemId = String.valueOf(annRelation.get(DbConstant.ANNOTATION_OBJECT_OBJECT_ITEM));
                                        String toItemId = String.valueOf(annRelation.get(DbConstant.ANNOTATION_OBJECT_LABEL_ID));

                                        Map<String,String> annotationObjectRelationInfo = new HashMap<>();
                                        annotationObjectRelationInfo.put(DbConstant.ANNOTATION_OBJECT_RELATION_DATA_ID,relationDataId);
                                        annotationObjectRelationInfo.put(DbConstant.ANNOTATION_OBJECT_LABEL_ID,labelId);
                                        annotationObjectRelationInfo.put(DbConstant.ANNOTATION_OBJECT_OBJECT_DATA_ID,objectDataId);
                                        annotationObjectRelationInfo.put(DbConstant.ANNOTATION_OBJECT_RELATION_FROM_ITEM_ID,fromItemId);
                                        annotationObjectRelationInfo.put(DbConstant.ANNOTATION_OBJECT_RELATION_TO_ITEM_ID,toItemId);

                                        annotationObjectRelationInfos.add(annotationObjectRelationInfo);

                                    }

                                }
//                            }
                        }


                    }
                    taskInfo.setTotalwords(String.valueOf(totalWords));

                    //to copy review data
                    if (!StringUtil.empty(oldTaskId)){

                        List<Map<String, String>> oldTaskReviewDatas = taskReviewInfoMapper.getDatasByTaskId(taskid);
                        for (Map<String, String>oldTaskReviewData:oldTaskReviewDatas) {
                            oldTaskReviewData.put(DbConstant.TASK_ID,taskid);
                            taskReviewInfos.add(oldTaskReviewData);
                        }
                    }
                }
//                else {
                List<String> dataSourceItems = new ArrayList<>();

                for (Map<String,Object> dataItme: dataItmes) {
                    String dataId = String.valueOf(dataItme.get(DbConstant.DATA_ID));
                    if (!dataSourceItems.contains(dataId)){
                        taskDataNumber += 1;
                        dataSourceItems.add(dataId);
                        String dataName = String.valueOf(dataItme.get(DbConstant.DATA_NAME));
                        String dataPath = String.valueOf(dataItme.get(DbConstant.DATA_PATH));

                        Map<String,String> taskAttachInfo = new HashMap<>();

                        taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_ATTACHMENT_ID,dataId);
                        taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_ATTACHMENT_NAME,dataName);
                        taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_ATTACHMENT_TYPE,Common.ATTACHMENT_TYPE_DATA);
                        taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_PATH,dataPath);
                        taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_TASK_ID,taskid);

                        taskAttachInfos.add(taskAttachInfo);
                    }
                }
//                }

            }

            //TODO insert into 任务附件表
            if (!StringUtil.empty(attFiles)){

                String[] attachmentFileNames =  attFiles.split(",");

                for (String attachmentFileName:attachmentFileNames) {

                    String file = attachmentFileName.substring(attachmentFileName.lastIndexOf(File.separator)+1);
                    String realfile = file.substring(file.indexOf('@')+1);

                    Map<String,String> taskAttachInfo = new HashMap<>();
                    String attachmentId = StringUtil.getUUID();
                    taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_ATTACHMENT_ID,attachmentId);
                    taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_ATTACHMENT_NAME,realfile);
                    taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_ATTACHMENT_TYPE,Common.ATTACHMENT_TYPE_DATA);
                    taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_PATH,attachmentFileName);
                    taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_TASK_ID,taskid);
                    taskAttachInfos.add(taskAttachInfo);
                    taskDataNumber += 1;
                }
            }


            taskInfo.setTaskdatanumber(String.valueOf(taskDataNumber));

            //检查相同的数据是否被创建相同的任务给当前用户；

//            map.put("templateid",task.getLabel());
//            map.put("datasourceid",task.getDsid());//此处非必传
//            map.put("datacount",task.getDatacount());
//            taskMapper.saveRelateInfo(map);
//            //查询数据源信息，为下面处理文件作准备
////                Map<String,Object> datasourceInfo = dataSourceMapper.checkDataSourceInfo(map);
//            Map<String,Object> choosedText = null;
//            map.put("dsid",task.getDsid());
//
//            if(! StringUtils.isEmpty(task.getDatatextids())){//用户已经选择了文本
//                map.put("textId",task.getDatatextids());
//                if(!StringUtils.isEmpty(performerid)){
//                    Map<String,String> fileperformermap = new HashMap<>(2);
//                    fileperformermap.put("textid",task.getDatatextids());
//                    fileperformermap.put("performerid",performerid);
//                    List<Map<String,String>> result = taskMapper.checkFilePerformer(fileperformermap);
//                    if(null != result && result.size() > 0){
//                        JsonModel jm = new JsonModel(false, "指定经办人已经处理过此文件,请选择其他经办人或者其他数据源",ReturnCode.ERROR_CODE_1111.getKey(),null);
//                        res = JSON.toJSONString(jm);
//                        return res;
//                    }
//                }
//                choosedText = dataTextMapper.checkTextInfoByTextId(task.getDatatextids());
//                if(null == choosedText){
//                    JsonModel jm = new JsonModel(false, "没有找到处理文本的信息",ReturnCode.ERROR_CODE_1111.getKey(),null);
//                    res = JSON.toJSONString(jm);
//                    return res;
//                }
//                //保存task_text表，存储task和选取的文本之间的关系存储
//                task.setTotalWordCount(Integer.parseInt(choosedText.get("allcounts")+""));//存储任务处理总字数
//                List<List<String>> fileparts =  readFileToList(choosedText.get("fileurl")+"");
//                if(fileparts.size() == 0){
//                    JsonModel jm = new JsonModel(false, "读取处理文本内容失败",ReturnCode.ERROR_CODE_1111.getKey(),null);
//                    res = JSON.toJSONString(jm);
//                    return res;
//                }else{
//                    this.insertIntoTextSentense(task.getTaskid(),choosedText.get("textId")+"",fileparts);
//                }
//                choosedText.put("taskid",task.getTaskid());
//                try {
//                    taskMapper.saveTaskText(choosedText);
//                } catch (Exception e) {
//                    log.error("存储任务选取的处理文本关系失败");
//                }
//            }

            int m = -1;
            if (taskInfo != null){
                m = taskInfoMapper.insertSelective(taskInfo);
            }

            if (taskAnnotationDatas.size() >0){

                m = taskAnnotationDataMapper.batchInsert(taskAnnotationDatas);
            }

            if (taskAttachInfos.size() > 0){
                m = taskAttachInfoMapper.batchInsert(taskAttachInfos);
            }

            if (annotationObjectInfos.size()>0){
                m = annotationObjectInfoMapper.batchInsert(annotationObjectInfos);
            }

            if (annotationObjectRelationInfos.size()>0){
                m = annotationObjectRelationInfoMapper.batchInsert(annotationObjectRelationInfos);
            }

            if (taskReviewInfos.size() > 0){
                m = taskReviewInfoMapper.batchInsert(taskReviewInfos);
            }

            if (m>0){
                JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                        ActionEnum.ADD.getValue(), Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,taskInfo);
                res = JSON.toJSONString(jm);
                return res;
            }
            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.ADD.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            log.error("=========TaskServiceImpl  addTask   occur exception :"+e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//关键
            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.ADD.getValue(),Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            e.printStackTrace();

        }
        return res;
    }



    @Override
    public String editTask(Map<String, String> task) {

        String res = null;
        try{
            Subject subject = SecurityUtils.getSubject();
            String userId = JWTUtil.getUserId(subject.getPrincipals().toString());

            String taskId = task.get(DbConstant.TASK_ID);

            Map<String, Object> oldTaskInfo = taskInfoMapper.getTaskInfo(taskId);

            //beforeValidate(task);


            TaskInfoCoreLogicProcessor taskInfoCoreLogicProcessor = new TaskInfoCoreLogicProcessor();
            TaskInfo taskInfo = new TaskInfo();
            //todo map set value to taskInfo
            if (taskInfoCoreLogicProcessor.needToUpdateNewConent(task.get(DbConstant.TASK_TITLE),
                    String.valueOf(oldTaskInfo.get(DbConstant.TASK_TITLE)))){
                TaskInfo existedTask = taskInfoMapper.getTaskByProjectName(task.get(DbConstant.TASK_TITLE),task.get(DbConstant.PROJECT_ID),userId);
                if (existedTask != null){
                    JsonModel jm = new JsonModel(false, BusinessEnum.TASK_EXISTED.getValue(), Messageconstant.REQUEST_FAILED_CODE,null);
                    res = JSON.toJSONString(jm);
                    return res;
                }
                taskInfo.setTasktitle(task.get(DbConstant.TASK_TITLE));
            }

            if (taskInfoCoreLogicProcessor.needToUpdateNewConent(task.get(DbConstant.PROJECT_ID),
                    String.valueOf(oldTaskInfo.get(DbConstant.PROJECT_ID)))){
                taskInfo.setProjectid(task.get(DbConstant.PROJECT_ID));
            }

            if (taskInfoCoreLogicProcessor.needToUpdateNewConent(task.get(DbConstant.CLASSIFY_CODE),
                    String.valueOf(oldTaskInfo.get(DbConstant.CLASSIFY_CODE)))){
                taskInfo.setClassifycode(task.get(DbConstant.CLASSIFY_CODE));
            }

            String version = String.valueOf(Long.parseLong(String.valueOf(oldTaskInfo.get(DbConstant.TASK_VERSION)))+1);
            taskInfo.setVersion(version);

            if (Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(task.get(DbConstant.CLASSIFY_CODE))){
                if (taskInfoCoreLogicProcessor.needToUpdateNewConent(task.get(DbConstant.TEMPLATE_ID),
                        String.valueOf(oldTaskInfo.get(DbConstant.TEMPLATE_ID)))){
                    taskInfo.setTemplateid(task.get(DbConstant.TEMPLATE_ID));
                }
            }

//            taskInfo.setTasktype(task.get(DbConstant.TASK_TYPE));

            if (taskInfoCoreLogicProcessor.needToUpdateNewConent(task.get(DbConstant.TASK_DESC),
                    String.valueOf(oldTaskInfo.get(DbConstant.TASK_DESC)))){
                taskInfo.setTaskdesc(task.get(DbConstant.TASK_DESC));
            }

            if (taskInfoCoreLogicProcessor.needToUpdateNewConent(task.get(DbConstant.PERFORMER_ID),
                    String.valueOf(oldTaskInfo.get(DbConstant.PERFORMER_ID)))){
                taskInfo.setPerformerid(task.get(DbConstant.PERFORMER_ID));
            }

            if (taskInfoCoreLogicProcessor.needToUpdateNewConent(task.get(DbConstant.FINISH_DEADLINE),
                    String.valueOf(oldTaskInfo.get(DbConstant.FINISH_DEADLINE)))){
                taskInfo.setFinishdeadline(task.get(DbConstant.FINISH_DEADLINE));
            }

            if (taskInfoCoreLogicProcessor.needToUpdateNewConent(task.get(DbConstant.NOTE),
                    String.valueOf(oldTaskInfo.get(DbConstant.NOTE)))){
                taskInfo.setNote(task.get(DbConstant.NOTE));
            }

            taskInfo.setUpdatetime(DateFormatUtil.DateFormat());//更新时间初始化
            taskInfo.setTaskid(taskId);

            //插入任务的数据至相关表中
            List<String> oldDataIds = taskAttachInfoMapper.getDataIdsByTaskId(taskId);

            String dataids = task.get(DbConstant.DATA_IDS);

            String attFiles = task.get(DbConstant.ATT_FILES);//应该是一个JSON数组

            if (StringUtil.empty(dataids) && StringUtil.empty(attFiles)){
                JsonModel jm = new JsonModel(false, BusinessEnum.TASK_DATA_NOT_EXISTED.getValue(),
                        Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }

            List<String> needToAddDataIds = new ArrayList<>();
            List<String> noChangeDataIds = new ArrayList<>();
            List<String> needToDeleteDataIds = new ArrayList<>();
            List<String> noChangeAttFileIds = new ArrayList<>();
            List<String> needToAddAttFileIds = new ArrayList<>();

            List<String> newDataIds = new ArrayList<>();
            List<String> newattFiles = new ArrayList<>();

            if (!StringUtil.empty(dataids)){
                newDataIds.addAll(Arrays.asList(dataids.split(",")));
                for (String newDataId:newDataIds){
                    if (!oldDataIds.contains(newDataId)){
                        needToAddDataIds.add(newDataId);
                    }else {
                        noChangeDataIds.add(newDataId);
                    }
                }
            }

            if (!StringUtil.empty(attFiles)){
                newattFiles.addAll(Arrays.asList(attFiles.split(",")));
                for (String newattFile:newattFiles){
                    if (!oldDataIds.contains(newattFile)){
                        needToAddAttFileIds.add(newattFile);
                    }else {
                        noChangeAttFileIds.add(newattFile);
                    }
                }
            }
            newattFiles.addAll(newDataIds);
            oldDataIds.removeAll(newattFiles);
            needToDeleteDataIds.addAll(oldDataIds);
//            String needToDeleteids = org.apache.commons.lang3.StringUtils.join(needToDeleteDataIds, ",");
            StringBuilder needToDeleteids = new StringBuilder();
            for (String needToDeleteDataId:needToDeleteDataIds) {
                needToDeleteids.append("'").append(needToDeleteDataId).append("'").append(",");
            }
            String deleteIds = needToDeleteids.toString();
            if (deleteIds.length()>0){
                deleteIds = deleteIds.substring(0,deleteIds.length()-1);
            }
            Map<String,String> infos = new HashMap<>();
            infos.put(DbConstant.TASK_ID,taskId);
            infos.put(DbConstant.DATA_IDS,deleteIds);
            if (!StringUtil.empty(deleteIds)){

                taskAttachInfoMapper.deleteByTaskIdAndAttachmentid(infos);
            }



            int taskDataNumber = Integer.parseInt(String.valueOf(oldTaskInfo.get(DbConstant.TASK_DATA_NUMBER)));;
            taskDataNumber = taskDataNumber - needToDeleteDataIds.size()+needToAddDataIds.size()+needToAddAttFileIds.size();

            List<Map<String,String>> taskAttachInfos = new ArrayList<>();
            List<Map<String,String>> taskAnnotationDatas = new ArrayList<>();
            List<Map<String,String>> annotationObjectInfos = new ArrayList<>();
            List<Map<String,String>> annotationObjectRelationInfos = new ArrayList<>();

            if (Common.MODULE_TASK_LABEL_CLASSFYCODE.equals(task.get(DbConstant.CLASSIFY_CODE))){

                //delete data
                if (!StringUtil.empty(deleteIds)){

                    annotationObjectRelationInfoMapper.deleteByTaskIdAndSourceId(infos);
                    annotationObjectInfoMapper.deleteByTaskIdAndSourceId(infos);
                    taskAnnotationDataMapper.deleteByTaskIdAndSourceId(infos);
                }



                int totalWords = Integer.parseInt(String.valueOf(oldTaskInfo.get(DbConstant.TOTAL_WORDS)));
                if (needToDeleteDataIds.size()>0){
                    int deleteWordsNumber = dataInfoMapper.getDataWordsByDataIds(needToDeleteDataIds);
                    totalWords = totalWords - deleteWordsNumber;
                }

                Map<String,Object> params = new HashMap<>();

                Map<String,Object> template = annotationTemplateMapper.getTemplateInfoById(task.get(DbConstant.TEMPLATE_ID));

                String labelType = String.valueOf(template.get(DbConstant.ANNOTATION_TEMPLATE_ANNOTATION_TYPE));
                String templateType = String.valueOf(template.get(DbConstant.ANNOTATION_TEMPLATE_TYPE));
                String annotationDataType = String.valueOf(template.get(DbConstant.ANNOTATION_TEMPLATE_ANNOTATION_OBJECT));
                String annotationType = String.valueOf(template.get(DbConstant.ANNOTATION_TEMPLATE_ANNOTATION_TYPE));

                // String hasRelationType = "通过模板ID获取,是双对象时，为true，其他时为false";
                String hasRelationType = Common.TASK_ANNO_DATA_RELATION_NO;
                if (Common.DOUBLE_OBJECT_ATTRIBUTE_LABEL.equals(templateType)){
                    hasRelationType = Common.TASK_ANNO_DATA_RELATION_YES;
                }

                //TODO insert into biaozhu table
                if (needToAddDataIds.size()>0){

                    List<Map<String,Object>> dataItmes = dataInfoMapper.getDataInfoByDataIds(needToAddDataIds);

                    List<String> dataSourceItems = new ArrayList<>();

                    for (Map<String,Object> dataItme: dataItmes) {
                        String dataId = String.valueOf(dataItme.get(DbConstant.DATA_ID));
                        if (!dataSourceItems.contains(dataId)){
                            taskDataNumber += 1;
                            dataSourceItems.add(dataId);
                        }
                        String dataName = String.valueOf(dataItme.get(DbConstant.DATA_NAME));
                        String dataPath = String.valueOf(dataItme.get(DbConstant.DATA_PATH));
                        String contentId = String.valueOf(dataItme.get(DbConstant.CONTENT_ID));
                        String anninfos = String.valueOf(dataItme.get(DbConstant.ANNINFOS));
                        String dataType = String.valueOf(dataItme.get(DbConstant.DATA_OBJECT_TYPE));
                        if (!annotationDataType.equals(dataType)){
                            continue;
                        }


                        Map<String,String> taskAnnotationData = new HashMap<>();

                        String objectDataId = StringUtil.getUUID();
                        taskAnnotationData.put(DbConstant.TASK_ANNO_OBJECT_DATA_ID,objectDataId);
                        taskAnnotationData.put(DbConstant.TASK_ANNO_DATA_TYPE,dataType);
                        taskAnnotationData.put(DbConstant.TASK_ANNO_TASK_ID,taskId);
                        taskAnnotationData.put(DbConstant.TASK_ANNO_SOURCE_ID,dataId);

                        taskAnnotationData.put(DbConstant.TASK_ANNO_LABEL_TYPE,labelType);
                        taskAnnotationData.put(DbConstant.TASK_ANNO_DATA_ID,contentId);

                        taskAnnotationData.put(DbConstant.TASK_ANNO_RELATION,hasRelationType);
                        taskAnnotationData.put(DbConstant.TASK_ANNO_EXECUTE_STATE,Common.DATA_EXCUTE_STATE_NO);

                        taskAnnotationDatas.add(taskAnnotationData);

                        Map<String,String> taskAttachInfo = new HashMap<>();
//                    String attachmentId = StringUtil.getUUID();
                        taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_ATTACHMENT_ID,dataId);
                        taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_ATTACHMENT_NAME,dataName);
                        taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_ATTACHMENT_TYPE,Common.ATTACHMENT_TYPE_DATA);
                        taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_PATH,dataPath);
                        taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_TASK_ID,taskId);

                        taskAttachInfos.add(taskAttachInfo);

                        //TODO anninfos 解析至标注结果页
                        if (!StringUtil.empty(anninfos)){
                            JSONObject anninfosObject = JSON.parseObject(anninfos);

                            String templateId =anninfosObject.getString(DbConstant.TEMPLATE_ID);
//                            if (templateId.equals(task.get(DbConstant.TEMPLATE_ID))){

                                JSONArray items = anninfosObject.getJSONArray(Common.ITEMS);

                                JSONArray relations = anninfosObject.getJSONArray(Common.RELATIONS);

                                if (items != null){

                                    for (Object item: items) {

                                        JSONObject annItem = (JSONObject)item;

                                        String dataItemId = StringUtil.getUUID();
                                        String itemId = String.valueOf(annItem.get(DbConstant.ANNOTATION_OBJECT_ITEM_ID));
                                        String labelId = String.valueOf(annItem.get(DbConstant.ANNOTATION_OBJECT_LABEL_ID));
                                        String createtime = String.valueOf(annItem.get(DbConstant.ANNOTATION_OBJECT_CREATETIME));

                                        Map<String,String> annotationObjectInfo = new HashMap<>();
                                        annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_DATA_ITEM_ID,dataItemId);
                                        annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_ITEM_ID,itemId);
                                        annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_OBJECT_DATA_ID,objectDataId);
                                        annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_LABEL_ID,labelId);
                                        annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_CREATETIME,createtime);

                                        if(Common.DATA_TYPE_TEXT.equals(dataType)){
                                            String itemContent = String.valueOf(annItem.get(DbConstant.ANNOTATION_OBJECT_OBJECT_ITEM));
                                            String startOffset = String.valueOf(annItem.get(DbConstant.ANNOTATION_OBJECT_START_OFFSET));
                                            String endOffset = String.valueOf(annItem.get(DbConstant.ANNOTATION_OBJECT_END_OFFSET));
                                            annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_OBJECT_ITEM,itemContent);
                                            annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_START_OFFSET,startOffset);
                                            annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_END_OFFSET,endOffset);
                                        }else if (Common.DATA_TYPE_IMAGE.equals(dataType)){

                                            String bandbox = String.valueOf(annItem.get(DbConstant.ANNOTATION_OBJECT_BNDBOX));
                                            annotationObjectInfo.put(DbConstant.ANNOTATION_OBJECT_BNDBOX,bandbox);
                                        }
                                        annotationObjectInfos.add(annotationObjectInfo);

                                    }
                                }

                                if (relations != null){

                                    for (Object relation: relations) {

                                        JSONObject annRelation = (JSONObject) relation;

                                        String relationDataId = StringUtil.getUUID();
                                        String labelId = String.valueOf(annRelation.get(DbConstant.ANNOTATION_OBJECT_LABEL_ID));
                                        String fromItemId = String.valueOf(annRelation.get(DbConstant.ANNOTATION_OBJECT_OBJECT_ITEM));
                                        String toItemId = String.valueOf(annRelation.get(DbConstant.ANNOTATION_OBJECT_LABEL_ID));

                                        Map<String,String> annotationObjectRelationInfo = new HashMap<>();
                                        annotationObjectRelationInfo.put(DbConstant.ANNOTATION_OBJECT_RELATION_DATA_ID,relationDataId);
                                        annotationObjectRelationInfo.put(DbConstant.ANNOTATION_OBJECT_LABEL_ID,labelId);
                                        annotationObjectRelationInfo.put(DbConstant.ANNOTATION_OBJECT_OBJECT_DATA_ID,objectDataId);
                                        annotationObjectRelationInfo.put(DbConstant.ANNOTATION_OBJECT_RELATION_FROM_ITEM_ID,fromItemId);
                                        annotationObjectRelationInfo.put(DbConstant.ANNOTATION_OBJECT_RELATION_TO_ITEM_ID,toItemId);

                                        annotationObjectRelationInfos.add(annotationObjectRelationInfo);

                                    }

                                }
//                            }
                        }

                        if(Common.DATA_TYPE_TEXT.equals(dataType)){
                            int wordCount = Integer.parseInt(String.valueOf(dataItme.get(DbConstant.WORD_COUNT)));
                            totalWords += wordCount;
                        }
                    }
                    taskInfo.setTotalwords(String.valueOf(totalWords));
                }
            }

            for (String attachmentFileName:needToAddAttFileIds) {
                Map<String,String> taskAttachInfo = new HashMap<>();
                String attachmentId = StringUtil.getUUID();
                String file = attachmentFileName.substring(attachmentFileName.lastIndexOf(File.separator)+1);
                String realfile = file.substring(file.indexOf('@')+1);
                taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_ATTACHMENT_ID,attachmentId);
                taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_ATTACHMENT_NAME,realfile);
                taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_ATTACHMENT_TYPE,Common.ATTACHMENT_TYPE_DATA);
                taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_PATH,attachmentFileName);
                taskAttachInfo.put(DbConstant.TASK_ATTACH_INFO_TASK_ID,taskId);
                taskAttachInfos.add(taskAttachInfo);
            }


            taskInfo.setTaskdatanumber(String.valueOf(taskDataNumber));

            int m = -1;
            if (taskInfo != null){
                m = taskInfoMapper.updateByPrimaryKeySelective(taskInfo);
            }

            if (taskAnnotationDatas.size() >0){

                m = taskAnnotationDataMapper.batchInsert(taskAnnotationDatas);
            }

            if (taskAttachInfos.size() > 0){
                m = taskAttachInfoMapper.batchInsert(taskAttachInfos);
            }

            if (annotationObjectInfos.size()>0){
                m = annotationObjectInfoMapper.batchInsert(annotationObjectInfos);
            }

            if (annotationObjectRelationInfos.size()>0){
                m = annotationObjectRelationInfoMapper.batchInsert(annotationObjectRelationInfos);
            }

            if (m>0){
                JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                        ActionEnum.EDIT.getValue(), Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,taskInfo);
                res = JSON.toJSONString(jm);
                return res;
            }
            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.EDIT.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            log.error("=========TaskServiceImpl  addTask   occur exception :"+e);
            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.EDIT.getValue(),Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            e.printStackTrace();

        }
        return res;
    }

    @Override
    public String reverseTask(Map<String, String> infos) {
        String res = null;
        try{
            Subject subject = SecurityUtils.getSubject();
            String token = subject.getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);

            String ids = infos.get(Common.IDS);
            if (StringUtil.empty(ids)){
                JsonModel jm = new JsonModel(true, BusinessEnum.TASK_DELETE_DATA_ID_EMPTY.getValue(),
                        Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }
            String[] str = ids.split(",");
            List<String> taskIdList = Arrays.asList(str);

            String newPerformerId = infos.get(DbConstant.PERFORMER_ID);
            String newFinishDeadline = infos.get(DbConstant.FINISH_DEADLINE);
            String desc = infos.get(DbConstant.NOTE);

            List<String> failedTask = new ArrayList<>();
            List<String> taskNeedToClosed = new ArrayList<>();
            TaskInfoCoreLogicProcessor taskInfoCoreLogicProcessor = new TaskInfoCoreLogicProcessor();

            if (taskIdList.size() == 1){
                Map<String,Object> taskInfo = taskInfoMapper.getTaskInfo(ids);
                List<String> dataids = taskResFileInfoMapper.getResultDataId(ids);
                String newDataIds = org.apache.commons.lang3.StringUtils.join(dataids, ",");
                Map<String,String> newTaskInfo = taskInfoCoreLogicProcessor.getNewTaskInfo(userId,newPerformerId,
                        newFinishDeadline,desc,taskInfo,newDataIds,false);
                String feedback = createTask(newTaskInfo,userId,null);
                if (!StringUtil.empty(feedback)){
                    JSONObject resultMessage = JSON.parseObject(feedback);
                    String code = resultMessage.getString(Common.CODE);
                    if (Messageconstant.REQUEST_FAILED_CODE.equals(code)){
                        failedTask.add(String.valueOf(taskInfo.get(DbConstant.TASK_TITLE)));
                    }else {
                        taskNeedToClosed.add(ids);
                    }

                }else {
                    failedTask.add(String.valueOf(taskInfo.get(DbConstant.TASK_TITLE)));
                }

            }else {
                for (String taskId : taskIdList){
                    Map<String,Object> taskInfo = taskInfoMapper.getTaskInfo(taskId);
                    List<String> dataids = taskResFileInfoMapper.getResultDataId(taskId);
                    String newDataIds = org.apache.commons.lang3.StringUtils.join(dataids, ",");
                    Map<String,String> newTaskInfo = taskInfoCoreLogicProcessor.getNewTaskInfo(userId,newPerformerId,
                            newFinishDeadline,desc,taskInfo,newDataIds,false);

                    String feedback = createTask(newTaskInfo,userId,null);
                    if (!StringUtil.empty(feedback)){
                        JSONObject resultMessage = JSON.parseObject(feedback);
                        String code = resultMessage.getString(Common.CODE);
                        if (Messageconstant.REQUEST_FAILED_CODE.equals(code)){
                            failedTask.add(String.valueOf(taskInfo.get(DbConstant.TASK_TITLE)));
                        }else {
                            taskNeedToClosed.add(taskId);
                        }

                    }else {
                        failedTask.add(String.valueOf(taskInfo.get(DbConstant.TASK_TITLE)));
                    }
                }
            }
            int m = taskInfoMapper.updateTaskToCloseStateByIds(taskNeedToClosed);
            if (m>0){
                JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                        ActionEnum.REVERSE.getValue(), Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,failedTask);
                res = JSON.toJSONString(jm);
                return res;
            }
            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.REVERSE.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);

        }catch (Exception e ){
            log.error("=========TaskServiceImpl  deleteTask   occur exception :"+e);
            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(Common.MODULE_NAME_TASK+
                    ActionEnum.REVERSE.getValue(), Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public String returnTask(Map<String, String> infos) {
        String res = null;
        try{
            Subject subject = SecurityUtils.getSubject();
            String token = subject.getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);

            String taskId  = infos.get(DbConstant.TASK_ID);
            String dataIds = infos.get(DbConstant.DATA_IDS);
            boolean directReturn = StringUtil.empty(infos.get(DbConstant.DIRECT_RETUR))?false:
                    Boolean.parseBoolean(infos.get(DbConstant.DIRECT_RETUR));
            String newFinishDeadline = infos.get(DbConstant.FINISH_DEADLINE);
            String desc = infos.get(DbConstant.TASK_DESC);

            Map<String,Object> taskInfo = taskInfoMapper.getTaskInfo(taskId);
            String newPerformerId = String.valueOf(taskInfo.get(DbConstant.PREV_PERFORMER_ID));
            if (StringUtil.empty(newPerformerId)){
                newPerformerId = String.valueOf(taskInfo.get(DbConstant.PERFORMER_ID));
            }
            if (StringUtil.empty(dataIds)){

                //List<String> temDataIds = taskAttachInfoMapper.getTypeDataByTaskId(taskId);
                List<String> temDataIds = taskResFileInfoMapper.getResultDataId(taskId);

                dataIds = org.apache.commons.lang3.StringUtils.join(temDataIds, ",");

            }
            TaskInfoCoreLogicProcessor taskInfoCoreLogicProcessor = new TaskInfoCoreLogicProcessor();
            Map<String,String> newTaskInfo = taskInfoCoreLogicProcessor.getNewTaskInfo(userId,newPerformerId,newFinishDeadline,desc,taskInfo,dataIds,directReturn);
            String feedback = createTask(newTaskInfo,userId,taskId);
            if (!StringUtil.empty(feedback)){
                JSONObject resultMessage = JSON.parseObject(feedback);
                res = resultMessage.getString(Common.CODE);
            }else {
                res = Messageconstant.REQUEST_FAILED_CODE;
            }
            Map<String,String> map = new HashMap<>();
            map.put(DbConstant.TASK_ID,taskId);
            map.put(DbConstant.TASK_STATE,Common.MODULE_TASK_STATE_CLOSED);
            int m = taskInfoMapper.updateTaskState(map);
            if (m <0){
                res = Messageconstant.REQUEST_FAILED_CODE;
            }

        }catch (Exception e ){
            log.error("=========TaskServiceImpl  returnTask   occur exception :"+e);
            res = Messageconstant.REQUEST_FAILED_CODE;
            e.printStackTrace();
        }
        return res;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String batchReturnTask(String taskids) {
        String res = null;
        try{
            Subject subject = SecurityUtils.getSubject();
            String token = subject.getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);

            String[] tasks = taskids.split(",");
            for(String taskId : tasks){
                Map<String,Object> taskInfo = taskInfoMapper.getTaskInfo(taskId);
                String newPerformerId = String.valueOf(taskInfo.get(DbConstant.PREV_PERFORMER_ID));
                if (StringUtil.empty(newPerformerId)){
                    newPerformerId = String.valueOf(taskInfo.get(DbConstant.PERFORMER_ID));
                }
                //List<String> temDataIds = taskAttachInfoMapper.getTypeDataByTaskId(taskId);
                List<String> temDataIds = taskResFileInfoMapper.getResultDataId(taskId);

                String dataIds = org.apache.commons.lang3.StringUtils.join(temDataIds, ",");

                TaskInfoCoreLogicProcessor taskInfoCoreLogicProcessor = new TaskInfoCoreLogicProcessor();
                Map<String,String> newTaskInfo = taskInfoCoreLogicProcessor.getNewTaskInfo(userId,
                        taskInfo.get(DbConstant.PERFORMER_ID)+"",
                        taskInfo.get(DbConstant.FINISH_DEADLINE)+"",
                        taskInfo.get(DbConstant.TASK_DESC)+"",
                        taskInfo,dataIds,true);

                String feedback = this.createTask(newTaskInfo,userId,taskId);
                if (!StringUtil.empty(feedback)){
                    JSONObject resultMessage = JSON.parseObject(feedback);
                    if("400".equals(resultMessage.getString(Common.CODE))){
                        throw new Exception("批量打回  新建任务失败");
                    }
                }else {
                    throw new Exception("批量打回  新建任务失败");
                }
                Map<String,String> map = new HashMap<>();
                map.put(DbConstant.TASK_ID,taskId);
                map.put(DbConstant.TASK_STATE,Common.MODULE_TASK_STATE_CLOSED);
                map.put(DbConstant.TASK_VERSION,taskInfo.get(DbConstant.TASK_VERSION)+"");
                int m = taskInfoMapper.updateTaskState(map);
                if (m <0){
                    throw new Exception("批量打回  更新任务为关闭状态失败");
                }
            }
            JsonModel jm = new JsonModel(true, "批量打回成功",ReturnCode.SUCESS_CODE_0000.getKey(),null);
            res = JSON.toJSONString(jm);

        }catch (Exception e ){
            log.error("=========TaskServiceImpl  returnTask   occur exception :"+e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//关键
            JsonModel jm = new JsonModel(false, "批量打回失败",Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            e.printStackTrace();
        }
        return res;
    }


    //配置文件中增加一个字段，表示当matchflag为false时，是单个数据建任务还是多个数据建一个任务multiple，true时表示多个文件建一个任务
    //增加一个数据集名称
    @Override
    public String batchAdd(MultipartFile multipartFile) {
        String res = null;
        List<TaskInfo> tasks = new ArrayList<TaskInfo>();
        try{
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);
            InputStream ins = multipartFile.getInputStream();
            JSONArray json = JsonFileUtil.ReadJsonArrayFile(ins);//解析json文件
            Iterator<Object> taskIterator = json.iterator();
            List<Map<String,String>> newTasks = new ArrayList<>();
            List<Map<String,String>> errorTasks = new ArrayList<>();
            while (taskIterator.hasNext()){//循环遍历最外层任务数组
                Map<String,Object> taskmap = (Map<String,Object>)taskIterator.next();
                /**
                 * 校验matchflag字段，若matchflag为true，则要求经办人和处理的文本是一一对应的，如果是false，则每个经办人都处理所有文本
                 */
                boolean matchflag = (boolean)taskmap.get("matchflag");

                JSONArray performers = (JSONArray)taskmap.get("performer");
                JSONArray datafiles = (JSONArray)taskmap.get("datafile");

                if (matchflag && (performers.size()!= datafiles.size())){
                    JsonModel jm = new JsonModel(false, BusinessEnum.TASK_CREATENUMBER_INCONSISTENT.getValue(),
                            Messageconstant.REQUEST_FAILED_CODE,null);
                    res = JSON.toJSONString(jm);
                    return res;
                }
                /**
                 * 校验项目是否存在
                 */
                String projectname = taskmap.get("projectname")+"";//项目名称

                Map<String,Object> params = new HashMap<>();
                params.put(DbConstant.PROJECT_NAME,projectname.trim());
                params.put(DbConstant.PROJECT_OWNER,userId);
                ProjectInfo p = projectInfoMapper.getProjectInfoByPnameAndPowner(params);
                if (p == null){
                    JsonModel jm = new JsonModel(false, "项目名称为："+projectname+"，项目不存在，请先创建项目",Messageconstant.REQUEST_FAILED_CODE,null);
                    res = JSON.toJSONString(jm);
                    return res;
                }
//                String consistent = taskmap.get("consistent")+"";//是否需要进行一致性校验
//                String associate = null;
//                if(consistent.equals("1")){//需要进行一致性校验
//                    associate = StringUtil.getUUID();
//                }
                String taskType = String.valueOf(taskmap.get("tasktype"));
                String templateName = String.valueOf(taskmap.get("model"));
                String title = String.valueOf(taskmap.get("title"));
                String taskDesc = String.valueOf(taskmap.get("taskdesc"));
                String note = String.valueOf(taskmap.get("remark"));
                String deadline = String.valueOf(taskmap.get("deadline"));
                boolean multiple = (boolean)taskmap.get("multiple");
                String datasetName = String.valueOf(taskmap.get("dataset"));
                String resultDataSet = "";
                if(taskmap.get("resultdataset") != null){
                    resultDataSet =  String.valueOf(taskmap.get("resultdataset"));
                }

                String projectId = p.getProjectid();
                String templateId = annotationTemplateMapper.getTemplateIdByName(templateName);

                StringBuilder datafilenames = new StringBuilder();
                Iterator<Object> datafileIterator = datafiles.iterator();
                Iterator<Object> performerIterator = performers.iterator();
                while (datafileIterator.hasNext()){
                    datafilenames.append("'").append(datafileIterator.next()).append("'").append(",");
                }
                String datanames = datafilenames.toString();
                if (datanames.length()>0){
                    datanames = datanames.substring(0,datanames.length()-1);
                }

                Map<String,String> dataQueryParams = new HashMap<>();
                dataQueryParams.put(DbConstant.DATASET_INFO_DS_OWNER,userId);
                dataQueryParams.put(DbConstant.DATA_NAMES,datanames);
                dataQueryParams.put(DbConstant.DATASET_INFO_DS_NAME,datasetName);

                Map<String,Object> dict = taskDictMapper.getTaskDictCodebyName(taskType);
                String classifyCode = String.valueOf(dict.get(DbConstant.TASK_DICT_CODE));

                List<Map<String,Object>> datainfos = dataInfoMapper.getDataIdsByDataNameAndDsName(dataQueryParams);

                if(matchflag){
                    //一一对应创建任务
                    int num = 0;
                    while (performerIterator.hasNext()){
                        String currentPerformer = String.valueOf(performerIterator.next());
                        Map<String,String> checkusermap = new HashMap<>(2);
                        checkusermap.put("projectid",projectId);
                        checkusermap.put("performername",currentPerformer);
                        String performerid = userMapper.checkAndgetUserIdByUserNameAndProject(checkusermap);
//                        for (Map<String,Object> dataInfo:datainfos){
//                            Map<String,String> newTaskInfo = new HashMap<>();
//                            String dataId = String.valueOf(dataInfo.get(DbConstant.DATA_INFO_DATA_ID));
//                            String dataName = String.valueOf(dataInfo.get(DbConstant.DATA_INFO_DATA_NAME));
//
//                            newTaskInfo.put(DbConstant.TASK_TITLE,title+"_"+dataName+"_"+currentPerformer);
//
//                            newTaskInfo.put(DbConstant.TASK_TYPE,Common.MODULE_TASK_TYPE_TASK);
//                            newTaskInfo.put(DbConstant.CLASSIFY_CODE,classifyCode);
//                            newTaskInfo.put(DbConstant.TEMPLATE_ID,templateId);
//                            newTaskInfo.put(DbConstant.PROJECT_ID,projectId);
//
//                            newTaskInfo.put(DbConstant.PERFORMER_ID,performerid);
//                            newTaskInfo.put(DbConstant.FINISH_DEADLINE,deadline);
//                            newTaskInfo.put(DbConstant.CREATEUSER_ID,userId);
//                            newTaskInfo.put(DbConstant.TASKOWNER_ID,p.getProjectowner());
//
//                            newTaskInfo.put(DbConstant.DATA_IDS,dataId);
//
//                            newTasks.add(newTaskInfo);
//                        }
                        if(null == datainfos || datainfos.size() == 0 ){
                            Map<String,String> errorTaskInfo = new HashMap<>();

                            errorTaskInfo.put(DbConstant.TASK_TITLE,title);
                            errorTaskInfo.put("datanames",datanames);
                            errorTaskInfo.put(DbConstant.TASK_TYPE,Common.MODULE_TASK_TYPE_TASK);
                            errorTaskInfo.put(DbConstant.PROJECT_NAME,projectname.trim());
                            errorTaskInfo.put("performername",currentPerformer);
                            errorTasks.add(errorTaskInfo);
                        }else {
                            Map<String,Object> dataInfo = datainfos.get(num);
                            Map<String,String> newTaskInfo = new HashMap<>();
                            String dataId = String.valueOf(dataInfo.get(DbConstant.DATA_INFO_DATA_ID));
                            String dataName = String.valueOf(dataInfo.get(DbConstant.DATA_INFO_DATA_NAME));

                            newTaskInfo.put(DbConstant.TASK_TITLE,title+"_"+dataName+"_"+currentPerformer);

                            newTaskInfo.put(DbConstant.TASK_TYPE,Common.MODULE_TASK_TYPE_TASK);
                            newTaskInfo.put(DbConstant.CLASSIFY_CODE,classifyCode);
                            newTaskInfo.put(DbConstant.TEMPLATE_ID,templateId);
                            newTaskInfo.put(DbConstant.PROJECT_ID,projectId);

                            newTaskInfo.put(DbConstant.PERFORMER_ID,performerid);
                            newTaskInfo.put(DbConstant.FINISH_DEADLINE,deadline);
                            newTaskInfo.put(DbConstant.CREATEUSER_ID,userId);
                            newTaskInfo.put(DbConstant.TASKOWNER_ID,p.getProjectowner());
                            newTaskInfo.put(DbConstant.RESULT_DATASET,resultDataSet);

                            newTaskInfo.put(DbConstant.DATA_IDS,dataId);

                            newTasks.add(newTaskInfo);
                            num++;
                        }
                    }
                }else{
                    if (multiple){
                        if (performers.size() ==0){
                            Map<String,String> newTaskInfo = new HashMap<>();
                            newTaskInfo.put(DbConstant.TASK_TITLE,title);

                            newTaskInfo.put(DbConstant.TASK_TYPE,Common.MODULE_TASK_TYPE_TASK);
                            newTaskInfo.put(DbConstant.CLASSIFY_CODE,classifyCode);
                            newTaskInfo.put(DbConstant.TEMPLATE_ID,templateId);
                            newTaskInfo.put(DbConstant.PROJECT_ID,projectId);

                            newTaskInfo.put(DbConstant.FINISH_DEADLINE,deadline);
                            newTaskInfo.put(DbConstant.CREATEUSER_ID,userId);
                            newTaskInfo.put(DbConstant.TASKOWNER_ID,p.getProjectowner());
                            newTaskInfo.put(DbConstant.RESULT_DATASET,resultDataSet);
                            List<String> dataids = new ArrayList<>();
                            for (Map<String,Object> dataInfo:datainfos){
                                String dataId = String.valueOf(dataInfo.get(DbConstant.DATA_INFO_DATA_ID));
                                dataids.add(dataId);
                            }
                            newTaskInfo.put(DbConstant.DATA_IDS,org.apache.commons.lang3.StringUtils.join(dataids, ","));

                            newTasks.add(newTaskInfo);
                            if(null == datainfos || datainfos.size() == 0 ){

                                Map<String,String> errorTaskInfo = new HashMap<>();

                                errorTaskInfo.put(DbConstant.TASK_TITLE,title);
                                errorTaskInfo.put("datanames",datanames);
                                errorTaskInfo.put(DbConstant.TASK_TYPE,Common.MODULE_TASK_TYPE_TASK);
                                errorTaskInfo.put(DbConstant.PROJECT_NAME,projectname.trim());
                                errorTaskInfo.put("performername","");
                                errorTasks.add(errorTaskInfo);
                            }


                        }else {

                            while (performerIterator.hasNext()){
                                String currentPerformer = String.valueOf(performerIterator.next());
                                Map<String,String> newTaskInfo = new HashMap<>();

                                Map<String,String> checkusermap = new HashMap<>(2);
                                checkusermap.put("projectid",projectId);
                                checkusermap.put("performername",currentPerformer);
                                String performerid = userMapper.checkAndgetUserIdByUserNameAndProject(checkusermap);

                                newTaskInfo.put(DbConstant.TASK_TITLE,title+"_"+currentPerformer);

                                newTaskInfo.put(DbConstant.TASK_TYPE,Common.MODULE_TASK_TYPE_TASK);
                                newTaskInfo.put(DbConstant.CLASSIFY_CODE,classifyCode);
                                newTaskInfo.put(DbConstant.TEMPLATE_ID,templateId);
                                newTaskInfo.put(DbConstant.PROJECT_ID,projectId);

                                newTaskInfo.put(DbConstant.PERFORMER_ID,performerid);
                                newTaskInfo.put(DbConstant.FINISH_DEADLINE,deadline);
                                newTaskInfo.put(DbConstant.CREATEUSER_ID,userId);
                                newTaskInfo.put(DbConstant.TASKOWNER_ID,p.getProjectowner());
                                newTaskInfo.put(DbConstant.RESULT_DATASET,resultDataSet);
                                List<String> dataids = new ArrayList<>();
                                for (Map<String,Object> dataInfo:datainfos){
                                    String dataId = String.valueOf(dataInfo.get(DbConstant.DATA_INFO_DATA_ID));
                                    dataids.add(dataId);
                                }
                                newTaskInfo.put(DbConstant.DATA_IDS,org.apache.commons.lang3.StringUtils.join(dataids, ","));

                                newTasks.add(newTaskInfo);
                                if(null == datainfos || datainfos.size() == 0 ){

                                    Map<String,String> errorTaskInfo = new HashMap<>();

                                    errorTaskInfo.put(DbConstant.TASK_TITLE,title);
                                    errorTaskInfo.put("datanames",datanames);
                                    errorTaskInfo.put(DbConstant.TASK_TYPE,Common.MODULE_TASK_TYPE_TASK);
                                    errorTaskInfo.put(DbConstant.PROJECT_NAME,projectname.trim());
                                    errorTaskInfo.put("performername",currentPerformer);
                                    errorTasks.add(errorTaskInfo);
                                }
                            }
                        }
                    }else {
                        if (performers.size() ==0){

                            for (Map<String,Object> dataInfo:datainfos){
                                Map<String,String> newTaskInfo = new HashMap<>();
                                String dataId = String.valueOf(dataInfo.get(DbConstant.DATA_INFO_DATA_ID));
                                String dataName = String.valueOf(dataInfo.get(DbConstant.DATA_INFO_DATA_NAME));

                                newTaskInfo.put(DbConstant.TASK_TITLE,title+"_"+dataName);

                                newTaskInfo.put(DbConstant.TASK_TYPE,Common.MODULE_TASK_TYPE_TASK);
                                newTaskInfo.put(DbConstant.CLASSIFY_CODE,classifyCode);
                                newTaskInfo.put(DbConstant.TEMPLATE_ID,templateId);
                                newTaskInfo.put(DbConstant.PROJECT_ID,projectId);

                                newTaskInfo.put(DbConstant.FINISH_DEADLINE,deadline);
                                newTaskInfo.put(DbConstant.CREATEUSER_ID,userId);
                                newTaskInfo.put(DbConstant.TASKOWNER_ID,p.getProjectowner());
                                newTaskInfo.put(DbConstant.DATA_IDS,dataId);
                                newTaskInfo.put(DbConstant.RESULT_DATASET,resultDataSet);

                                newTasks.add(newTaskInfo);
                            }
                            if(null == datainfos || datainfos.size() == 0 ){

                                Map<String,String> errorTaskInfo = new HashMap<>();

                                errorTaskInfo.put(DbConstant.TASK_TITLE,title);
                                errorTaskInfo.put("datanames",datanames);
                                errorTaskInfo.put(DbConstant.TASK_TYPE,Common.MODULE_TASK_TYPE_TASK);
                                errorTaskInfo.put(DbConstant.PROJECT_NAME,projectname.trim());
                                errorTaskInfo.put("performername","");
                                errorTasks.add(errorTaskInfo);
                            }

                        }else {
                            while (performerIterator.hasNext()){
                                String currentPerformer = String.valueOf(performerIterator.next());

                                Map<String,String> checkusermap = new HashMap<>(2);
                                checkusermap.put("projectid",projectId);
                                checkusermap.put("performername",currentPerformer);
                                String performerid = userMapper.checkAndgetUserIdByUserNameAndProject(checkusermap);

                                if(null == datainfos || datainfos.size() == 0 ){

                                    Map<String,String> errorTaskInfo = new HashMap<>();

                                    errorTaskInfo.put(DbConstant.TASK_TITLE,title);
                                    errorTaskInfo.put("datanames",datanames);
                                    errorTaskInfo.put(DbConstant.TASK_TYPE,Common.MODULE_TASK_TYPE_TASK);
                                    errorTaskInfo.put(DbConstant.PROJECT_NAME,projectname.trim());
                                    errorTaskInfo.put("performername",currentPerformer);
                                    errorTasks.add(errorTaskInfo);
                                }

                                for (Map<String,Object> dataInfo:datainfos){
                                    Map<String,String> newTaskInfo = new HashMap<>();
                                    String dataId = String.valueOf(dataInfo.get(DbConstant.DATA_INFO_DATA_ID));
                                    String dataName = String.valueOf(dataInfo.get(DbConstant.DATA_INFO_DATA_NAME));

                                    newTaskInfo.put(DbConstant.TASK_TITLE,title+"_"+dataName+"_"+currentPerformer);

                                    newTaskInfo.put(DbConstant.TASK_TYPE,Common.MODULE_TASK_TYPE_TASK);
                                    newTaskInfo.put(DbConstant.CLASSIFY_CODE,classifyCode);
                                    newTaskInfo.put(DbConstant.TEMPLATE_ID,templateId);
                                    newTaskInfo.put(DbConstant.PROJECT_ID,projectId);

                                    newTaskInfo.put(DbConstant.PERFORMER_ID,performerid);
                                    newTaskInfo.put(DbConstant.FINISH_DEADLINE,deadline);
                                    newTaskInfo.put(DbConstant.CREATEUSER_ID,userId);
                                    newTaskInfo.put(DbConstant.TASKOWNER_ID,p.getProjectowner());
                                    newTaskInfo.put(DbConstant.RESULT_DATASET,resultDataSet);

                                    newTaskInfo.put(DbConstant.DATA_IDS,dataId);

                                    newTasks.add(newTaskInfo);
                                }

                            }
                        }

                    }
                }
            }
            List<String> failedTasks = new ArrayList<>();
            JSONObject messageData = new JSONObject();
            for (Map<String,String> newTask : newTasks){
                String feedback = createTask(newTask,userId,null);
                JSONObject jsonObject = JSONObject.parseObject(feedback);
                if((boolean)jsonObject.get("flag")){
                    JSONObject resultMessage = JSON.parseObject(feedback);
                    if (Messageconstant.REQUEST_FAILED_CODE.equals(resultMessage.getString(Common.CODE))){
                        failedTasks.add(newTask.get(DbConstant.TASK_TITLE));
                    }
                }else {
                    failedTasks.add(newTask.get(DbConstant.TASK_TITLE) +jsonObject.get("message") );
                }
            }

            if(errorTasks.size() > 0){
                System.out.println("================批量创建任务时失败的任务,创建时间："+DateFormatUtil.DateFormat()+"  =============");
                for(Map<String,String> errorTaskInfo : errorTasks){
                    System.out.println("项目名：" + errorTaskInfo.get(DbConstant.PROJECT_NAME)+
                            ",任务名：" + errorTaskInfo.get(DbConstant.TASK_TITLE) +
                            ",经办人：" + errorTaskInfo.get("performername") +
                            ",文件 ：" + errorTaskInfo.get("datanames")
                    );

                }
                System.out.println("================批量创建任务时失败的任务,共计 "+errorTasks.size() +"个 =============");
            }

            messageData.put(Common.TOATL,newTasks.size());
            messageData.put(Common.FAILED,failedTasks.size());
            messageData.put(Common.FAILED_DATA,failedTasks);
            if(failedTasks.size() > 0){
                JsonModel jm = new JsonModel(false, "批量创建任务成功", ReturnCode.SUCESS_CODE_0000.getKey(),messageData);
                res = JSON.toJSONString(jm);
            }else{
                JsonModel jm = new JsonModel(true, "批量创建任务成功", ReturnCode.SUCESS_CODE_0000.getKey(),messageData);
                res = JSON.toJSONString(jm);
            }

            return res;
        }catch (Exception e){
            log.error("TaskServiceImpl  uploadDataDistributeFile   occur exception :"+e);
            ServiceException serviceException=(ServiceException) ExceptionUtil.handlerException4biz(e);
            JsonModel jm = new JsonModel(false, serviceException.getErrorMessage(),serviceException.getErrorCode(),null);
            res = JSON.toJSONString(jm);
            e.printStackTrace();
        }finally {
            log.info("uploadDataDistributeFile Response ==>"+res);
        }
        return res;
    }
}
