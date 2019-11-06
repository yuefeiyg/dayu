package com.blcultra.service.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.Common;
import com.blcultra.cons.TaskConstant;
import com.blcultra.dao.DataContentMapper;
import com.blcultra.dao.DataInfoMapper;
import com.blcultra.dao.DatasetInfoMapper;
import com.blcultra.model.DataContentWithBLOBs;
import com.blcultra.model.DataInfo;
import com.blcultra.model.User;
import com.blcultra.model.taskResFileInfo;
import com.dayu.util.DateFormatUtil;
import com.dayu.util.FileUtil;
import com.dayu.util.JsonFileUtil;
import com.dayu.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sgy05 on 2019/5/31.
 */
@Service(value = "fileDataInfoServiceUtil")
public class FileDataInfoServiceUtil {

    @Value("${task.text.result.jsonfile.path}")
    private String datasourcefilepath;

    @Autowired
    private com.blcultra.dao.taskResFileInfoMapper taskResFileInfoMapper;

    @Autowired
    private DataInfoMapper dataInfoMapper;

    @Autowired
    private DataContentMapper dataContentMapper;

    @Autowired
    private DatasetInfoMapper datasetInfoMapper;

    @Autowired
    private DataServiceUtil dataServiceUtil;

    public Map<String,String> generateTaskResFilePath(Map<String, Object> taskInfo,String username,String tname,String tasktype) throws Exception {
        Map<String,String> fileparam = new HashMap<>(2);
        String filepath;
        String filename;
        if (tasktype.equals(Common.MODULE_TASK_TYPE_AUDIT)){
            String prevperformerUserName = (String) taskInfo.get("prevperformerusername");
            String performerUserName = (String) taskInfo.get("performerusername");
            if (StringUtil.empty(prevperformerUserName)){
                prevperformerUserName = performerUserName;
                performerUserName = username;
            }
            int audittimes = Integer.parseInt(String.valueOf(taskInfo.get("audittimes")))+1;
            filename = prevperformerUserName+"_"+tname+"."+performerUserName+"("+audittimes+")"+"_"+"pass"+".json";
            filepath= datasourcefilepath +filename;
        }else {
            filename = username+"_"+tname;
            filepath= datasourcefilepath +filename;
        }
        fileparam.put("filename",filename);
        fileparam.put("filepath",filepath);
        return fileparam;
    }


    public Map<String,Object> createDataFileInfoDatas(Map<String,Object> source, Map<String, Object> taskInfo,
                                                      String taskid, User user, String tasktype) throws Exception {
        String sourceid = (String) source.get("sourceid");
        String textname = (String) source.get("dataname");
        String userid = user.getUserid();
        //dataitemid,itemid,objectdataid,datainfo,sourceid
        //dataname,datapath,dataobjecttype,size,scenariotype,owner
        String datainfo = JSONObject.parseObject(source.get("datainfo")+"").toJSONString();

        JSONObject jsonObject1 = JSONObject.parseObject(String.valueOf(source.get("datainfo")));
        Object dataobjecttype = source.get("dataobjecttype");
        Map<String,Object> filemap = new HashMap<>();
        filemap.put("source",textname);
        filemap.put("sourceid",sourceid);
        filemap.put("datainfo",jsonObject1);
        filemap.put("dataobjecttype",dataobjecttype);
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(filemap));

        String tname = textname.substring(0,textname.lastIndexOf(".")) + ".json";
        /**
         * 组装结果文件存储路径
         */
        Map<String,String> fileparam = generateTaskResFilePath(taskInfo,user.getUsername(),tname,tasktype);
        String filename = fileparam.get("filename");
        String filepath = fileparam.get("filepath");

        FileUtil.checkFileOrDirExist(datasourcefilepath,"dir");
        JsonFileUtil.exportJsonFileLocal(jsonObject,filepath,"utf-8");

        Map<String,String> params = new HashMap<>();
        params.put("taskid",taskid);
        params.put("filename",filename);
        params.put("filepath",filepath);
        params.put("resultdataset",(String) taskInfo.get("resultdataset"));
        params.put("dsowner",(String) taskInfo.get("pmanager"));
        params.put("dataobjecttype",String.valueOf(dataobjecttype));
        params.put("scenariotype",String.valueOf(source.get("scenariotype")));
        params.put("projectname",String.valueOf(taskInfo.get("projectname")));
        params.put("userid",userid);
        params.put("owner",String.valueOf(source.get("owner")));
        params.put("segmentation",String.valueOf(source.get("segmentation")));
        params.put("size",String.valueOf(source.get("size")));
        String dataid = StringUtil.getUUID();
        params.put("dataid",dataid);
        //创建提交后生成的文件信息，包含：taskResFileInfo、datasetInfo、DataInfo
        this.createFileInfoDatas(params);

        DataContentWithBLOBs dataContentWithBLOBs = new DataContentWithBLOBs();
        String contentid = StringUtil.getUUID();
        dataContentWithBLOBs.setDataid(dataid);
        dataContentWithBLOBs.setContentid(contentid);
        dataContentWithBLOBs.setContent(datainfo);
        List<DataContentWithBLOBs> cs = new ArrayList<>();
        cs.add(dataContentWithBLOBs);
        dataContentMapper.batchInsertDataContent(cs);

        Map<String,Object> index = dataServiceUtil.indexMapPutValue(StringUtil.getUUID(),dataid,contentid,source.get("dataindexname")+"",1);
        return index;
    }


    public void createFileInfoDatas(Map<String,String> params) throws Exception {

        //导出json文件后，将该文件信息插入到任务结果文件表task_res_file_info表中
        taskResFileInfo trfi = new taskResFileInfo();
        String dataid = params.get("dataid");
        trfi.setDataid(dataid);
        trfi.setTaskid(params.get("taskid"));
        trfi.setDataname(params.get("filename"));
        trfi.setPath(params.get("filepath"));
        trfi.setState(Common.DELETE_STATE_NO);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        trfi.setCreatetime(sdf.format(now));
        trfi.setFiletype(TaskConstant.TASK_RES_FILE_TYPE_PASS);
        int m = taskResFileInfoMapper.insertSelective(trfi);
        //将该文件信息插入data_info表中
        //数据集判断有没有，将该数据添加至该数据集下
        String resultdataset = params.get("resultdataset");
        String dsowner = params.get("dsowner");
        String datasetid ="";
        if (!StringUtil.empty(resultdataset)){
            //如果当前数据集不为空，那么根据projectowner也即dsowner和dsname查询数据集信息表，
            // 若查得到，则将该结果文件放置在该数据集下，
            // 若查不到，那么新建数据集：数据集名称还是当前名称只不过dsowner是当前项目owner
            Map<String,Object> p = new HashMap<>();
            p.put("dsname",resultdataset);
            p.put("dsowner",dsowner);
            Map<String, Object> datasetinfo = datasetInfoMapper.checkIfExsit(p);
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
                map.put("dataobjecttype",params.get("dataobjecttype"));
                map.put("dsstate","1");
                map.put("dsusingstate","1");
                map.put("scenariotype",params.get("scenariotype"));
                datasetInfoMapper.addDataSetByMap(map);
            }
        }else {
            //如果为空，则新建，数据集名称：项目名+"结果集"

            Object projectname = params.get("projectname");
            String dsname = projectname+"通过结果集";
            Map<String,Object> pa = new HashMap<>();
            pa.put("dsname",dsname);
            pa.put("dsowner",dsowner);
            Map<String, Object> datasetinfo = datasetInfoMapper.checkIfExsit(pa);
            if (datasetinfo !=null) {
                //有这个数据集直接使用
                datasetid = (String) datasetinfo.get("dsid");
            }else {
                datasetid = StringUtil.getUUID();
                Map<String, Object> map = new HashMap<>();
                map.put("dsid", datasetid);
                map.put("dsname", dsname);
                map.put("dsowner", dsowner);
                map.put("dscreatetime", DateFormatUtil.DateFormat());
                map.put("dataobjecttype", params.get("dataobjecttype"));
                map.put("dsstate", "1");
                map.put("dsusingstate", "1");
                map.put("scenariotype", params.get("scenariotype"));
                datasetInfoMapper.addDataSetByMap(map);
            }
        }

        DataInfo dataInfo = new DataInfo();
        dataInfo.setDataid(dataid);
        dataInfo.setCreatetime(DateFormatUtil.DateFormat());
        dataInfo.setDataname(params.get("filename"));
        dataInfo.setDsid(datasetid);
        dataInfo.setCreator(params.get("userid"));
        dataInfo.setSize(params.get("size"));
        dataInfo.setDataobjecttype(params.get("dataobjecttype"));
        dataInfo.setDatapath(params.get("filepath"));
        dataInfo.setDatastate("1");
        dataInfo.setDatatype("1");//数据类型，‘0’-原始文件 ‘1’-标注结果提交的文件
        dataInfo.setDatausingstate("0");//默认未使用
        dataInfo.setOwner(params.get("owner"));
        dataInfo.setSegmentation(params.get("segmentation"));
        dataInfo.setScenariotype(params.get("scenariotype"));
        int n = dataInfoMapper.insertDataInfoSelective(dataInfo);

    }


}
