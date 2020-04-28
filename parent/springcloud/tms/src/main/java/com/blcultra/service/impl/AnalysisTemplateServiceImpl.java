package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.AnnotationEnum;
import com.blcultra.cons.AnnotationTemplateConstant;
import com.blcultra.dao.AnnotationLabelMapper;
import com.blcultra.dao.AnnotationLabelRelationRuleMapper;
import com.blcultra.dao.AnnotationTemplateMapper;
import com.blcultra.service.AnalysisTemplateService;
import com.blcultra.service.core.AnalysisLabelUtil;
import com.blcultra.service.core.AnalysisTemplateUtil;
import com.blcultra.shiro.JWTUtil;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "analysisTemplateService")
public class AnalysisTemplateServiceImpl implements AnalysisTemplateService {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AnnotationTemplateMapper annotationTemplateMapper;
    @Autowired
    AnnotationLabelMapper annotationLabelMapper;
    @Autowired
    AnnotationLabelRelationRuleMapper annotationLabelRelationRuleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String analysisTemplate(Map<String,Object> map) {
        String res = null;
        try{
            MultipartFile templateJsonFile = (MultipartFile)map.get("templateJsonFile");
            String userid = JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString());

            //解析模板
            JSONObject jsonObject = AnalysisTemplateUtil.analysisTemplate(templateJsonFile);

            //检验该用户是否创建过相同模板名的模板
            this.checkTemplateIfExistsByUser(jsonObject.get(AnnotationTemplateConstant.templatename)+"",userid);

            //解析模板支持的功能模块
            AnalysisTemplateUtil.analysisFuncs(jsonObject);

            //保存数据
            String templateid = StringUtil.getUUID();
            jsonObject.put(AnnotationTemplateConstant.templateid ,templateid);
            this.insertAnnotationTemplate(jsonObject,userid);
            this.insertAnnotationLabel(jsonObject,userid);

            JsonModel jm = new JsonModel(true, "模板上传成功", ReturnCode.SUCESS_CODE_0000.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            log.error("文件解析失败",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//关键
            JsonModel jm = new JsonModel(false, e.getMessage(), ReturnCode.ERROR_CODE_1111.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }
    }

    /**
     * 检查该用户是否上传过相同模板名称的模板
     * @param templatename
     * @param createuserid
     * @throws Exception
     */
    public void  checkTemplateIfExistsByUser(String templatename,String createuserid) throws Exception{
        Map<String,String> checkmap = new HashMap<>(2);
        checkmap.put(AnnotationTemplateConstant.templatename,templatename);
        checkmap.put(AnnotationTemplateConstant.createuserid,createuserid);
        Map<String,Object> result = annotationTemplateMapper.checkTemplateIfExistsByUser(checkmap);
        if(null != result && result.size() > 0 ){
            throw new Exception("名字为"+templatename+"的模板已存在");
        }

    }

    /**
     * 保存模板信息
     * @param jsonObject
     * @param createuserid
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void  insertAnnotationTemplate(JSONObject jsonObject,String createuserid) throws Exception{
        Map<String,Object> map = new HashMap<>();
        map.put(AnnotationTemplateConstant.templatename , jsonObject.get(AnnotationTemplateConstant.templatename));
        map.put(AnnotationTemplateConstant.templatetype , jsonObject.get(AnnotationTemplateConstant.templatetype));
        map.put(AnnotationTemplateConstant.annotationobject , AnnotationEnum.getValue(AnnotationTemplateConstant.annotationobject));
        map.put(AnnotationTemplateConstant.annotationtype , jsonObject.get(AnnotationTemplateConstant.annotationtype));
        map.put(AnnotationTemplateConstant.note, jsonObject.get(AnnotationTemplateConstant.note));
        map.put(AnnotationTemplateConstant.createuserid , createuserid);
        map.put(AnnotationTemplateConstant.actions, jsonObject.get(AnnotationTemplateConstant.actions) + "");
        map.put(AnnotationTemplateConstant.modules, jsonObject.get(AnnotationTemplateConstant.modules) + "");
        map.put(AnnotationTemplateConstant.auxshortcutsettings , jsonObject.get(AnnotationTemplateConstant.auxshortcutsettings) + "");
        map.put(AnnotationTemplateConstant.templateid , jsonObject.get(AnnotationTemplateConstant.templateid));
        String now = DateFormatUtil.DateFormat();
        map.put("createdate" , now);
        map.put("updatedate" , now);
        map.put(AnnotationTemplateConstant.state , 1);

        annotationTemplateMapper.insertAnnotationTemplate(map);

    }


    /**
     * 保存标签信息
     * @param jsonObject
     * @param createuserid
     * @throws Exception
     */
    public void  insertAnnotationLabel(JSONObject jsonObject,String createuserid) throws Exception{

        String templatetype = jsonObject.get("templatetype")+"";
        JSONArray labeltree ;
        JSONArray relationlabeltree ;

        labeltree = (JSONArray) jsonObject.get(AnnotationTemplateConstant.labeltree);
        relationlabeltree = (JSONArray) jsonObject.get(AnnotationTemplateConstant.relationlabeltree);

        if(AnnotationTemplateConstant.TEMPLATETYPE_STRUCTURE_ANNOTATION.equals(templatetype)){
            AnalysisTemplateUtil.checkIfEmpty(labeltree, "模板类型为单对象标注时，labeltree内容不能为空");
        }else if(AnnotationTemplateConstant.TEMPLATETYPE_RELATION_ANNOTATION.equals(templatetype)){
            AnalysisTemplateUtil.checkIfEmpty(relationlabeltree, "模板类型为双对象关系标注时，relationlabeltree内容不能为空");
        }

        Map<String,List<Map<String,Object>>> returndata = new HashMap<>(2);
        returndata.put("labels",new ArrayList<>());
        returndata.put("labelsRules",new ArrayList<>());
        if(null != labeltree && labeltree.size() > 0){
            returndata = AnalysisLabelUtil.labelLeafLoop(returndata,null,labeltree,createuserid,
                    jsonObject.get(AnnotationTemplateConstant.templateid)+"");
            annotationLabelMapper.insertLabelBatch(returndata.get("labels"));
        }
        if(null != relationlabeltree && relationlabeltree.size() > 0){
            returndata = AnalysisLabelUtil.labelLeafLoop(returndata,null,relationlabeltree,createuserid,
                    jsonObject.get(AnnotationTemplateConstant.templateid)+"");
            if(null != returndata.get("labelsRules") && returndata.get("labelsRules").size() > 0){
                //根据标签名查询标签id，插入id
                annotationLabelRelationRuleMapper.insertLabelRelationRuleBatch(
                        AnalysisLabelUtil.matchLabel(returndata.get("labels"),returndata.get("labelsRules")));
            }
        }
    }





}
