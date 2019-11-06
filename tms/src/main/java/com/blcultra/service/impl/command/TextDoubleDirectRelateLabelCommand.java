package com.blcultra.service.impl.command;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.Common;
import com.blcultra.cons.Messageconstant;
import com.blcultra.cons.TaskConstant;
import com.blcultra.dao.*;
import com.blcultra.model.*;
import com.blcultra.service.InnerExportCommand;
import com.blcultra.service.TaskInfoCreateService;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.JsonModel;
import com.dayu.util.DateFormatUtil;
import com.dayu.util.JsonFileUtil;
import com.dayu.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文本 双对象 有向关系标注
 * Created by sgy05 on 2019/2/21.
 */
@Service(value = "textDoubleDirectRelateLabelCommand")
public class TextDoubleDirectRelateLabelCommand implements InnerExportCommand {

    private static final Logger log = LoggerFactory.getLogger(TextDoubleDirectRelateLabelCommand.class);

    @Value("${task.text.result.jsonfile.path}")
    private String datasourcefilepath;

    @Autowired
    private TaskAnnotationDataMapper taskAnnotationDataMapper;
    @Autowired
    private AnnotationObjectInfoMapper annotationObjectInfoMapper;

    @Autowired
    private AnnotationLabelMapper annotationLabelMapper;
    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Autowired
    private DataContentMapper dataContentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private taskResFileInfoMapper taskResFileInfoMapper;

    @Autowired
    private DataInfoMapper dataInfoMapper;

    @Autowired
    private  DatasetInfoMapper datasetInfoMapper;
    @Autowired
    private TaskInfoCreateService taskInfoCreateService;
    @Autowired
    private AnnotationObjectRelationInfoMapper annotationObjectRelationInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String process(String taskid, String note, String dataids, String tasktype, String ispass, String annotationobject) throws Exception {

        String  res = null;
        TaskInfo task = taskInfoMapper.selectByPrimaryKey(taskid);
        Map<String,Object> map = new HashMap<>();
        String state = "002005";
        if (Common.MODULE_TASK_TYPE_AUDIT.equals(tasktype)){
            state = "002006";
            map.put("comments",note);
        }else {
            map.put("resultdesc",note);
        }
        map.put("taskid",taskid);
        map.put("taskstate",state);

        map.put("finishtime", DateFormatUtil.DateFormat());//更新任务结束时间

        int m = taskInfoMapper.updateTaskInfoState(map);
        log.info("更新任务状态返回值： "+m);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        Map<String,Object> map1 = new HashMap<>();
        String taskstate = task.getTaskstate();
        if (taskstate.equals("002003")){
            //若任务当前状态为正在进行中，那么任务点击提交时就要计算耗时，
            // 若任务当前状态为暂停，那么就不需要更新耗时了
            String taskpausetime = task.getPausetime();
            Date taskpausedate = sdf.parse(taskpausetime);
            long taskpausedateTime = taskpausedate.getTime();
            task.setPausetime(sdf.format(now));
            long nowTime = now.getTime();
            Long rate = (nowTime-taskpausedateTime) / 1000;
            String cost =String.valueOf(Long.parseLong(task.getCosttime() == null ? String.valueOf(rate) : task.getCosttime()) + rate);//之前的耗时加上新增耗时
            map1.put("costtime",cost);
            map1.put("taskid",taskid);
            int n = taskInfoMapper.updateTaskCostTimeByTaskId(map1);
            log.info("update one text 更新耗时返回值："+n);
        }


        if (tasktype.equals(Common.MODULE_TASK_TYPE_AUDIT)){
            if ("".equals(ispass) || null ==ispass){
                JsonModel jm = new JsonModel(false, "参数校验错误", Messageconstant.REQUEST_FAILED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }
            //判断任务类型，是否为审核任务，001005审核任务
            //判断该审核任务，是否通过
            if ("1".equals(ispass.trim())){
                //1是已通过审核,
                /**
                 *  逻辑：
                 *  1.通过taskid获取对应文本信息列表。
                 *  2.遍历循环文本列表，获取文件信息，
                 *  3.根据文件信息获取标注信息item列表信息，
                 *  4.将该文件信息插入到任务结果文件表task_res_file_info表中
                 *  5.生成审核通过后的pass json文件，并将文件信息插入data_info 表和data_content表中。
                 *  6.文件名：经办人_文件名_审核人（审核次数）_pass
                 */
                List<Map<String, Object>> sources = taskAnnotationDataMapper.getSourceIdAndSourceNameByTaskId(taskid);
                boolean flag = auditExport(task.getTemplateid(),taskid, annotationobject, sources);
                if (flag){
                    JsonModel jm = new JsonModel(true, "提交操作成功", Messageconstant.REQUEST_SUCCESSED_CODE,null);
                    res = JSON.toJSONString(jm);
                    return res;
                }
                JsonModel jm = new JsonModel(false, "提交操作失败", Messageconstant.REQUEST_FAILED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }else if ("0".equals(ispass)){
                //审核不通过逻辑
                Map<String,String> param = new HashMap<>();
                param.put("taskdesc",note);
                param.put("dataids",dataids);
                param.put("taskid",taskid);
                param.put("return","true");
                String s = taskInfoCreateService.returnTask(param);
                if ("200".equals(s)){
                    JsonModel jm = new JsonModel(true, "提交操作成功", Messageconstant.REQUEST_SUCCESSED_CODE,null);
                    res = JSON.toJSONString(jm);
                    return res;
                }
                JsonModel jm = new JsonModel(false, "提交操作失败", Messageconstant.REQUEST_FAILED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }

        }
        List<Map<String, Object>> sources = taskAnnotationDataMapper.getSourceIdAndSourceNameByTaskId(taskid);
        boolean flag = normalExport(task.getTemplateid(),taskid, annotationobject, sources);
        if (flag){
            JsonModel jm = new JsonModel(true, "提交操作成功", Messageconstant.REQUEST_SUCCESSED_CODE,null);
            res = JSON.toJSONString(jm);
            return res;
        }
        JsonModel jm = new JsonModel(false, "提交操作失败", Messageconstant.REQUEST_FAILED_CODE,null);
        res = JSON.toJSONString(jm);
        return res;
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean auditExport(String templateid,String taskid,String  annotationobject,List<Map<String, Object>> sources) throws Exception {

        String token = SecurityUtils.getSubject().getPrincipals().toString();
        String userid = JWTUtil.getUserId(token);
        User user = userMapper.selectByPrimaryKey(userid);

        Map<String, Object> taskInfo = taskInfoMapper.getTaskInfo(taskid);

        for (Map<String, Object> source :sources){
            //                String dsid = (String) source.get("dsid");//数据集id
            String sourceid = (String) source.get("sourceid");
            String textname = (String) source.get("dataname");
            List<DataContentWithBLOBs> contents = dataContentMapper.getContentDataByDataId(sourceid);

            List<Map<String,Object>> baseelements = new ArrayList<>();
            Map<String,Object> resmap = new HashMap<>();

            List<DataContentWithBLOBs> contentlist = new ArrayList<>();
            for (DataContentWithBLOBs datacontent:contents){
                Map<String,Object> basemap = new HashMap<>();
                List<Map<String,Object>> itemlist = new ArrayList<>();
                List<Map<String,Object>> relations = new ArrayList<>();


                String contentid = datacontent.getContentid();
                Map<String,Object> param = new HashMap<>();
                param.put("taskid",taskid);
                param.put("sourceid",sourceid);
                param.put("dataid",contentid);
                List<AnnotationObjectInfo> annotationObjectInfos = taskAnnotationDataMapper.getAnnotationObjectInfos(param);
                if (annotationObjectInfos.size()>0 && annotationObjectInfos != null){
                    for (AnnotationObjectInfo annotationObjectInfo:annotationObjectInfos){
                        String labelid = annotationObjectInfo.getLabelid();
                        String[] split = labelid.split(",");
                        List<String> labelidlist = Arrays.asList(split);
                        List<String> labelnames = new ArrayList<>();
                        List<String> labelcodes = new ArrayList<>();
                        List<String> labelsymbols = new ArrayList<>();
                        for (String id:labelidlist){
                            AnnotationLabel annotationLabel = annotationLabelMapper.selectByPrimaryKey(id);
                            labelnames.add(annotationLabel.getLabelname());
                            labelcodes.add(annotationLabel.getCode());
                            labelsymbols.add(annotationLabel.getSymbol());
                        }
                        Map<String,Object> itemDto = new HashMap<>();
                        itemDto.put("itemid",annotationObjectInfo.getItemid());
                        itemDto.put("item",annotationObjectInfo.getItem());
                        itemDto.put("labelid",labelid);
                        itemDto.put("labelname",labelnames);
                        itemDto.put("labelcode",labelcodes);
                        itemDto.put("labelsymbols",labelsymbols);
                        itemDto.put("startoffset",annotationObjectInfo.getStartoffset());
                        itemDto.put("endoffset",annotationObjectInfo.getEndoffset());
                        itemlist.add(itemDto);
                    }
                }

                Map<String,Object> querymap = new HashMap<>();
                querymap.put("dataid",contentid);
                querymap.put("taskid",taskid);
                querymap.put("sourceid",sourceid);
                List<Map<String, Object>> relateItems = annotationObjectRelationInfoMapper.getRelateLableDataItems(querymap);
                if (relateItems.size()>0 && relateItems != null){
                    for (Map<String,Object> m:relateItems){
                        String labelid = (String) m.get("labelid");
                        String[] lids = labelid.split(",");
                        List<String> labelidlist = Arrays.asList(lids);
                        List<String> labelnames = new ArrayList<>();
                        for (String lid:labelidlist){
                            AnnotationLabel annotationLabel = annotationLabelMapper.selectByPrimaryKey(lid);
                            labelnames.add(annotationLabel.getLabelname());
                        }
                        Map<String,Object> relatemap = new HashMap<>();
                        relatemap.put("relation",labelnames);
                        relatemap.put("labelid",labelid);
                        relatemap.put("from",m.get("fromitemid"));
                        relatemap.put("to",m.get("toitemid"));
                        relations.add(relatemap);
                    }
                }
                basemap.put("content",datacontent.getContent());
                basemap.put("items",itemlist);
                basemap.put("relations",relations);
                baseelements.add(basemap);

                Map<String,Object> relatemap = new HashMap<>();
                relatemap.put("templateid",templateid);
                relatemap.put("items",itemlist);
                relatemap.put("relations",relations);

                datacontent.setAnninfos(JSON.toJSONString(relatemap));
                contentlist.add(datacontent);
            }

//                List<Map<String,Object>> labels = annotationLabelMapper.getLablesByTemplateId(templateid);
//                List<Map<String,Object>> labelTree = AnalysisLabelUtil.makeLabelTree(labels,"0");//单标签

            resmap.put("source",source);
            resmap.put("dataobjecttype",annotationobject);
            resmap.put("baseelements",baseelements);
//                resmap.put("labeltree",labelTree);

            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(resmap));
            String tname = textname.substring(0,textname.lastIndexOf("."));
            // ".json";
            //文件名：经办人_文件名_审核人（审核次数）_pass
            String prevperformerUserName = (String) taskInfo.get("prevperformerUserName");
            String performerUserName = (String) taskInfo.get("performerUserName");
            int audittimes = (int) taskInfo.get("audittimes")+1;
            String username = user.getUsername();
            String filename = prevperformerUserName+"_"+tname+"."+performerUserName+"("+audittimes+")"+"_"+"pass"+".json";
            String filepath= datasourcefilepath +filename;
            JsonFileUtil.exportJsonFileLocal(jsonObject,filepath,"utf-8");
            //导出json文件后，将该文件信息插入到任务结果文件表task_res_file_info表中
            taskResFileInfo trfi = new taskResFileInfo();
            String dataid = StringUtil.getUUID();
            trfi.setDataid(dataid);
            trfi.setTaskid(taskid);
            trfi.setDataname(filename);
            trfi.setPath(filepath);
            trfi.setState(Common.DELETE_STATE_NO);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            trfi.setCreatetime(sdf.format(now));
            trfi.setFiletype(TaskConstant.TASK_RES_FILE_TYPE_PASS);
            int m = taskResFileInfoMapper.insertSelective(trfi);
            //将该文件信息插入data_info表中
            //数据集判断有没有，将该数据添加至该数据集下
            String resultdataset = (String) taskInfo.get("resultdataset");
            String dsowner = (String) taskInfo.get("pmanager");
            String datasetid ="";
            if (! StringUtils.isEmpty(resultdataset)){
                //如果当前数据集不为空，那么根据projectowner也即dsowner和dsname查询数据集信息表，
                // 若查得到，则将该结果文件放置在该数据集下，
                // 若查不到，那么新建数据集：数据集名称还是当前名称只不过dsowner是当前项目owner
                Map<String,Object> param = new HashMap<>();
                param.put("dsname",resultdataset);
                param.put("dsowner",dsowner);
                Map<String, Object> datasetinfo = datasetInfoMapper.checkIfExsit(param);
                if (datasetinfo !=null){
                    //有这个数据集直接使用
                    datasetid = (String) datasetinfo.get("dsid");
                }else {
                    //若没有则新建
                    datasetid = StringUtil.getUUID();
                    Map<String,Object> map = new HashMap<>();
                    map.put("dsid",datasetid);
                    map.put("dsname",resultdataset);
                    map.put("dsowner",dsowner);
                    map.put("dscreatetime", DateFormatUtil.DateFormat());
                    map.put("dataobjecttype",source.get("dataobjecttype"));
                    map.put("dsstate","1");
                    map.put("dsusingstate","1");
                    map.put("scenariotype",source.get("scenariotype"));
                    datasetInfoMapper.addDataSetByMap(map);
                }
            }else {
                //如果为空，则新建，数据集名称：项目名+"结果集"
                Object projectname = taskInfo.get("projectname");
                String dsname = projectname+"结果集";
                // 若查不到，那么新建数据集：数据集名称还是当前名称只不过dsowner是当前项目owner
                Map<String,Object> param = new HashMap<>();
                param.put("dsname",resultdataset);
                param.put("dsowner",dsowner);
                Map<String, Object> datasetinfo = datasetInfoMapper.checkIfExsit(param);
                if (datasetinfo !=null){
                    //有这个数据集直接使用
                    datasetid = (String) datasetinfo.get("dsid");
                }else {
                    //若没有则新建
                    datasetid = StringUtil.getUUID();
                    Map<String, Object> map = new HashMap<>();
                    map.put("dsid", datasetid);
                    map.put("dsname", dsname);
                    map.put("dsowner", dsowner);
                    map.put("dscreatetime", DateFormatUtil.DateFormat());
                    map.put("dataobjecttype", source.get("dataobjecttype"));
                    map.put("dsstate", "1");
                    map.put("dsusingstate", "1");
                    map.put("scenariotype", source.get("scenariotype"));
                    datasetInfoMapper.addDataSetByMap(map);
                }
            }

            DataInfo dataInfo = new DataInfo();
            dataInfo.setDataid(dataid);
            dataInfo.setCreatetime(DateFormatUtil.DateFormat());
            dataInfo.setDataname(filename);
            dataInfo.setDsid(datasetid);
            dataInfo.setCreator(userid);
            dataInfo.setSize((String) source.get("size"));
            dataInfo.setDataobjecttype((String) source.get("dataobjecttype"));
            dataInfo.setDatapath(filepath);
            dataInfo.setDatastate("1");
            dataInfo.setDatausingstate("0");//默认未使用
            dataInfo.setOwner((String) source.get("owner"));
            dataInfo.setSegmentation((String) source.get("segmentation"));
            dataInfo.setScenariotype((String)source.get("scenariotype"));
            int n = dataInfoMapper.insertDataInfoSelective(dataInfo);

            List<DataContentWithBLOBs> cs = new ArrayList<>();
            for (DataContentWithBLOBs content :contentlist){
                content.setDataid(dataid);
                content.setContentid(StringUtil.getUUID());
                cs.add(content);
            }
            //批量插入data_content表中
            int i = dataContentMapper.batchInsertDataContent(cs);

        }
        List<String> list = new ArrayList<>();
        list.add(taskid);
        taskInfoMapper.updateTaskToCloseStateByIds(list);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean normalExport(String templateid,String taskid,String annotationobject,List<Map<String, Object>> sources) throws Exception {
        String token = SecurityUtils.getSubject().getPrincipals().toString();
        String userid = JWTUtil.getUserId(token);
        User user = userMapper.selectByPrimaryKey(userid);
        Map<String, Object> taskInfo = taskInfoMapper.getTaskInfo(taskid);
        for (Map<String, Object> source :sources){
            //                String dsid = (String) source.get("dsid");//数据集id
            String sourceid = (String) source.get("sourceid");
            String textname = (String) source.get("dataname");
            List<DataContentWithBLOBs> contents = dataContentMapper.getContentDataByDataId(sourceid);

            List<Map<String,Object>> baseelements = new ArrayList<>();
            Map<String,Object> resmap = new HashMap<>();

            List<DataContentWithBLOBs> contentlist = new ArrayList<>();
            for (DataContentWithBLOBs datacontent:contents){
                Map<String,Object> basemap = new HashMap<>();
                List<Map<String,Object>> itemlist = new ArrayList<>();
                List<Map<String ,Object>> relations = new ArrayList<>();


                String contentid = datacontent.getContentid();
                Map<String,Object> param = new HashMap<>();
                param.put("taskid",taskid);
                param.put("sourceid",sourceid);
                param.put("dataid",contentid);
                List<AnnotationObjectInfo> annotationObjectInfos = taskAnnotationDataMapper.getAnnotationObjectInfos(param);
                if (annotationObjectInfos.size()>0 && annotationObjectInfos != null){
                    for (AnnotationObjectInfo annotationObjectInfo:annotationObjectInfos){
                        String labelid = annotationObjectInfo.getLabelid();
                        String[] split = labelid.split(",");
                        List<String> labelidlist = Arrays.asList(split);
                        List<String> labelnames = new ArrayList<>();
                        List<String> labelcodes = new ArrayList<>();
                        List<String> labelsymbols = new ArrayList<>();
                        for (String id:labelidlist){
                            AnnotationLabel annotationLabel = annotationLabelMapper.selectByPrimaryKey(id);
                            labelnames.add(annotationLabel.getLabelname());
                            labelcodes.add(annotationLabel.getCode());
                            labelsymbols.add(annotationLabel.getSymbol());
                        }
                        Map<String,Object> itemDto = new HashMap<>();
                        itemDto.put("itemid",annotationObjectInfo.getItemid());
                        itemDto.put("item",annotationObjectInfo.getItem());
                        itemDto.put("labelid",labelid);
                        itemDto.put("labelname",labelnames);
                        itemDto.put("labelcode",labelcodes);
                        itemDto.put("labelsymbols",labelsymbols);
                        itemDto.put("startoffset",annotationObjectInfo.getStartoffset());
                        itemDto.put("endoffset",annotationObjectInfo.getEndoffset());
                        itemlist.add(itemDto);
                    }
                }

                Map<String,Object> querymap = new HashMap<>();
                querymap.put("contentid",contentid);
                querymap.put("taskid",taskid);
                querymap.put("sourceid",sourceid);
                List<Map<String, Object>> relateItems = annotationObjectRelationInfoMapper.getRelateLableDataItems(querymap);
                if (relateItems.size()>0 && relateItems != null){
                    for (Map<String,Object> m:relateItems){
                        String labelid = (String) m.get("labelid");
                        String[] lids = labelid.split(",");
                        List<String> labelidlist = Arrays.asList(lids);
                        List<String> labelnames = new ArrayList<>();
                        for (String lid:labelidlist){
                            AnnotationLabel annotationLabel = annotationLabelMapper.selectByPrimaryKey(lid);
                            labelnames.add(annotationLabel.getLabelname());
                        }
                        Map<String,Object> relatemap = new HashMap<>();
                        relatemap.put("relation",labelnames);
                        relatemap.put("labelid",labelid);
                        relatemap.put("from",m.get("fromitemid"));
                        relatemap.put("to",m.get("toitemid"));
                        relations.add(relatemap);
                    }
                }

                basemap.put("content",datacontent.getContent());
                basemap.put("items",itemlist);
                basemap.put("relations",relations);
                baseelements.add(basemap);
                datacontent.setAnninfos(JSON.toJSONString(itemlist));
                datacontent.setAnnrelateinfos(JSON.toJSONString(relations));
                contentlist.add(datacontent);
            }
            resmap.put("source",source);
            resmap.put("dataobjecttype",annotationobject);
            resmap.put("baseelements",baseelements);

            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(resmap));
            String tname = textname.substring(0,textname.lastIndexOf(".")) + ".json";
            String username = user.getUsername();
            String filename = username+"_"+tname;
            String filepath= datasourcefilepath +filename;
            JsonFileUtil.exportJsonFileLocal(jsonObject,filepath,"utf-8");
            //导出json文件后，将该文件信息插入到任务结果文件表task_res_file_info表中
            taskResFileInfo trfi = new taskResFileInfo();
            String dataid = StringUtil.getUUID();
            trfi.setDataid(dataid);
            trfi.setTaskid(taskid);
            trfi.setDataname(filename);
            trfi.setPath(filepath);
            trfi.setState(Common.DELETE_STATE_NO);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            trfi.setCreatetime(sdf.format(now));
            trfi.setFiletype(TaskConstant.TASK_RES_FILE_TYPE_FINAL);
            int m = taskResFileInfoMapper.insertSelective(trfi);
            //将该文件信息插入data_info表中
            //数据集判断有没有，将该数据添加至该数据集下
            String resultdataset = (String) taskInfo.get("resultdataset");
            String dsowner = (String) taskInfo.get("pmanager");
            String datasetid ="";
            if (StringUtils.isEmpty(resultdataset)){
                //如果当前数据集不为空，那么根据projectowner也即dsowner和dsname查询数据集信息表，
                // 若查得到，则将该结果文件放置在该数据集下，
                // 若查不到，那么新建数据集：数据集名称还是当前名称只不过dsowner是当前项目owner
                Map<String,Object> param = new HashMap<>();
                param.put("dsname",resultdataset);
                param.put("dsowner",dsowner);
                Map<String, Object> datasetinfo = datasetInfoMapper.checkIfExsit(param);
                if (datasetinfo !=null){
                    //有这个数据集直接使用
                    datasetid = (String) datasetinfo.get("dsid");
                }else {
                    //若没有则新建
                    datasetid = StringUtil.getUUID();
                    Map<String,Object> map = new HashMap<>();
                    map.put("dsid",datasetid);
                    map.put("dsname",resultdataset);
                    map.put("dsowner",dsowner);
                    map.put("dscreatetime", DateFormatUtil.DateFormat());
                    map.put("dataobjecttype",source.get("dataobjecttype"));
                    map.put("dsstate","1");
                    map.put("dsusingstate","1");
                    map.put("scenariotype",source.get("scenariotype"));
                    datasetInfoMapper.addDataSetByMap(map);
                }
            }else {
                //如果为空，则新建，数据集名称：项目名+"结果集"
                Object projectname = taskInfo.get("projectname");
                String dsname = projectname+"结果集";
                // 若查不到，那么新建数据集：数据集名称还是当前名称只不过dsowner是当前项目owner
                Map<String,Object> param = new HashMap<>();
                param.put("dsname",resultdataset);
                param.put("dsowner",dsowner);
                Map<String, Object> datasetinfo = datasetInfoMapper.checkIfExsit(param);
                if (datasetinfo !=null){
                    //有这个数据集直接使用
                    datasetid = (String) datasetinfo.get("dsid");
                }else {
                    //若没有则新建
                    datasetid = StringUtil.getUUID();
                    Map<String, Object> map = new HashMap<>();
                    map.put("dsid", datasetid);
                    map.put("dsname", dsname);
                    map.put("dsowner", dsowner);
                    map.put("dscreatetime", DateFormatUtil.DateFormat());
                    map.put("dataobjecttype", source.get("dataobjecttype"));
                    map.put("dsstate", "1");
                    map.put("dsusingstate", "1");
                    map.put("scenariotype", source.get("scenariotype"));
                    datasetInfoMapper.addDataSetByMap(map);
                }
            }

            DataInfo dataInfo = new DataInfo();
            dataInfo.setDataid(dataid);
            dataInfo.setCreatetime(DateFormatUtil.DateFormat());
            dataInfo.setDataname(filename);
            dataInfo.setDsid(datasetid);
            dataInfo.setCreator(userid);
            dataInfo.setSize((String) source.get("size"));
            dataInfo.setDataobjecttype((String) source.get("dataobjecttype"));
            dataInfo.setDatapath(filepath);
            dataInfo.setDatastate("1");
            dataInfo.setDatausingstate("0");//默认未使用
            dataInfo.setOwner((String) source.get("owner"));
            dataInfo.setSegmentation((String) source.get("segmentation"));
            dataInfo.setScenariotype((String)source.get("scenariotype"));
            dataInfo.setDatatype("1");
            int n = dataInfoMapper.insertDataInfoSelective(dataInfo);

            List<DataContentWithBLOBs> cs = new ArrayList<>();
            for (DataContentWithBLOBs content :contentlist){
                content.setDataid(dataid);
                content.setContentid(StringUtil.getUUID());
                cs.add(content);
            }
            //批量插入data_content表中
            int i = dataContentMapper.batchInsertDataContent(cs);

        }
        return true;
    }

}
