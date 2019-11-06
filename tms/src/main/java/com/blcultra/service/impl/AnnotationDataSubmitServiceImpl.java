package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.Common;
import com.blcultra.cons.DbConstant;
import com.blcultra.cons.Messageconstant;
import com.blcultra.dao.AnnotationTemplateMapper;
import com.blcultra.dao.DataInfoMapper;
import com.blcultra.dao.TaskInfoMapper;
import com.blcultra.model.TaskInfo;
import com.blcultra.service.AnnotationDataSubmitService;
import com.blcultra.service.InnerExportCommand;
import com.blcultra.service.TaskInfoCreateService;
import com.blcultra.service.impl.command.InnerExportCommandContext;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.AnnotationDataExportFactory;
import com.blcultra.support.JsonModel;
import com.blcultra.support.ReturnCode;
import com.dayu.util.DateFormatUtil;
import com.dayu.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sgy05 on 2019/3/13.
 */
@Service("annotationDataSubmitService")
public class AnnotationDataSubmitServiceImpl implements AnnotationDataSubmitService {
    private static final Logger log = LoggerFactory.getLogger(AnnotationDataSubmitServiceImpl.class);
    @Autowired
    private TaskInfoMapper taskInfoMapper;
    @Autowired
    private InnerExportCommandContext innerExportCommandContext ;
    @Autowired
    private AnnotationTemplateMapper annotationTemplateMapper;
    @Autowired
    private DataInfoMapper dataInfoMapper;
    @Autowired
    private TaskInfoCreateService taskInfoCreateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitLabelData(Map submitinfo) {
        String res = null;
        try {
            if (null == submitinfo.get("taskid") || null == submitinfo.get("tasktype")){
                JsonModel jm = new JsonModel(true, ReturnCode.ERROR_CODE_1001.getValue(),ReturnCode.ERROR_CODE_1001.getKey(),null);
                res = JSON.toJSONString(jm);
                return res;
            }
            //组装导出模板对应code值
            String code = (submitinfo.get("annotationobject")+"").trim()+" "+(submitinfo.get("templatetype")+"").trim()+" "+(submitinfo.get("annotationtype")+"").trim();
            InnerExportCommand instance = innerExportCommandContext.getInstance(code);
            log.info("*******类名："+instance.getClass().getName());
            res = instance.process((submitinfo.get("taskid")+""),(submitinfo.get("note")+""),(submitinfo.get("dataids")+""), (submitinfo.get("tasktype")+""),(submitinfo.get("ispass")+"") ,(submitinfo.get("annotationobject")+""));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//关键
        }
        JsonModel jm = new JsonModel(false, "提交操作失败", Messageconstant.REQUEST_FAILED_CODE,null);
        res = JSON.toJSONString(jm);
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String batchSubmitLabelData(String taskid) {
        String res = null;
        try {
            if (StringUtil.empty(taskid)){
                JsonModel jm = new JsonModel(true, ReturnCode.ERROR_CODE_1001.getValue(),ReturnCode.ERROR_CODE_1001.getKey(),null);
                res = JSON.toJSONString(jm);
                return res;
            }
            String[] taskids = taskid.split(",");
            List<String> notSuccess = new ArrayList<>();
            for (int i = 0;i<taskids.length;i++){
                Map<String,Object> template = annotationTemplateMapper.getTemplateInfoByTaskId(taskids[i]);
                //组装导出模板对应code值
                String annotationobject = String.valueOf(template.get(DbConstant.ANNOTATION_OBJECT));
                String templatetype = String.valueOf(template.get(DbConstant.TEMPLATE_TYPE));
                String annotationtype = String.valueOf(template.get(DbConstant.ANNOTATION_TYPE));
                String code = annotationobject.trim()+" "+templatetype.trim()+" "+annotationtype.trim();
                InnerExportCommand instance = innerExportCommandContext.getInstance(code);
                log.info("*******类名："+instance.getClass().getName());
                res = instance.process(taskids[i],"","", Common.MODULE_TASK_TYPE_AUDIT, Common.ONE,annotationobject);
                JSONObject result = JSON.parseObject(res);
                if (!result.getBoolean("flag")){
                    notSuccess.add(taskids[i]);
                }
            }
            JsonModel jm = new JsonModel(true, "批量审核成功数："+(taskids.length-notSuccess.size())+(notSuccess.size()>0?("失败数："+notSuccess.size()):""), Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//关键
        }
        JsonModel jm = new JsonModel(false, "提交操作失败", Messageconstant.REQUEST_FAILED_CODE,null);
        res = JSON.toJSONString(jm);
        return res;
    }

    @Override
    public String submitData(Map data) {
        String res= null;
        try {
            String userid = JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString());
            String taskid = data.get("taskid")+"";
            String tasktype =  data.get("tasktype")+"";
            String ispass = data.get("ispass")+"";
            String note =  data.get("note")+"";
            String annotationtype = data.get("annotationtype")+"";

            if (StringUtils.isEmpty(taskid)){
                return getErrormessage("缺少任务id");
            }
            if (StringUtils.isEmpty(tasktype)){
                return getErrormessage("缺少任务类型");
            }else {
                if (tasktype.equals(Common.MODULE_TASK_TYPE_AUDIT) && ("".equals(ispass) || null ==ispass)){
                    return getErrormessage("缺少任务是否通过参数");
                }
            }
            TaskInfo task = taskInfoMapper.selectByPrimaryKey(taskid);
            String taskstate = task.getTaskstate();
            TaskInfo updatetask = new TaskInfo();
            if("002005".equals(taskstate) && userid.equals(task.getPerformerid())){
                return getErrormessage("任务已经提交");
            }else if(taskstate.equals("002003")){
                //若任务当前状态为正在进行中，那么任务点击提交时就要计算耗时，
                // 若任务当前状态为暂停，那么就不需要更新耗时了
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = new Date();
                String taskpausetime = task.getPausetime();
                Date taskpausedate = sdf.parse(taskpausetime);
                long taskpausedateTime = taskpausedate.getTime();
                task.setPausetime(sdf.format(now));
                long nowTime = now.getTime();
                Long rate = (nowTime-taskpausedateTime) / 1000;
                String cost =String.valueOf(Long.parseLong(task.getCosttime() == null ? String.valueOf(rate) : task.getCosttime()) + rate);//之前的耗时加上新增耗时
                updatetask.setCosttime(cost);
            }

            if (Common.MODULE_TASK_TYPE_AUDIT.equals(tasktype)){
                updatetask.setComments(note);
                updatetask.setTaskstate("002006");
            }else {
                updatetask.setResultdesc(note);
                updatetask.setTaskstate("002005");
            }
            updatetask.setTaskid(taskid);
            updatetask.setFinishtime(DateFormatUtil.DateFormat());
            int n = taskInfoMapper.updateByPrimaryKeySelective(updatetask);
            log.info("update one text 更新耗时返回值："+n);

            if (tasktype.equals(Common.MODULE_TASK_TYPE_AUDIT) && "0".equals(ispass)){//审核不通过
                return checkNoPass(data,taskid,note);
            }else{
                Map<String,Object> param = new HashMap<>();
                param.put("taskid",taskid);
                param.put("annotationtype",annotationtype);
                param.put("templateid",task.getTemplateid());
                param.put("tasktype",tasktype);

                //工厂模式获取对应场景的导出接口类
                AnnotationDataExportFactory factory = new AnnotationDataExportFactory();
                AnnotationDataExportService exportService = factory.produce(annotationtype);
                boolean flag = exportService.annotatioinDataExport(param);

                if (!flag){
                    log.error("exportService.annotatioinDataExport 业务返回值: ",flag);
                    throw new Exception("标注页面点击提交，逻辑错误。。。");
                }
                JsonModel jm = new JsonModel(true, "提交操作成功", Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            JsonModel jm = new JsonModel(false, "提交操作失败", Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
        }
        return res;
    }

    private String checkNoPass (Map data,String taskid,String note){
        String res;
        List<Map<String,Object>> datalist =  dataInfoMapper.getdatasbytaskid(data);
        StringBuilder datas = new StringBuilder("");
        for (Map<String,Object> datamap : datalist){
            datas.append(datamap.get("dataid")+",");
        }
        //审核不通过逻辑
        Map<String,String> param = new HashMap<>();
        param.put("taskdesc",note);
        param.put("dataids",datas.toString().substring(0,datas.toString().length() - 1));
        param.put("taskid",taskid);
        param.put("return","true");
        String s = taskInfoCreateService.returnTask(param);
        if ("200".equals(s)){
            JsonModel jm = new JsonModel(true, "提交操作成功", Messageconstant.REQUEST_SUCCESSED_CODE,null);
            res = JSON.toJSONString(jm);
        }else{
            JsonModel jm = new JsonModel(false, "提交操作失败", Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
        }

        return res;
    }



    private String getErrormessage (String errormessage){
        JsonModel jm = new JsonModel(false, errormessage, Messageconstant.REQUEST_FAILED_CODE,null);
        return JSON.toJSONString(jm);
    }
}
