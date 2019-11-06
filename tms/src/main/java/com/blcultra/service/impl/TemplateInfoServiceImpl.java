package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.blcultra.cons.AnnotationTemplateConstant;
import com.blcultra.dao.AnnotationLabelMapper;
import com.blcultra.dao.AnnotationTemplateMapper;
import com.blcultra.service.TemplateInfoService;
import com.blcultra.service.core.AnalysisLabelUtil;
import com.blcultra.service.core.AnalysisTemplateUtil;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.JsonModel;
import com.blcultra.support.Page;
import com.blcultra.support.ReturnCode;
import com.dayu.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "templateInfoService")
public class TemplateInfoServiceImpl implements TemplateInfoService {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AnnotationTemplateMapper annotationTemplateMapper;
    @Autowired
    AnnotationLabelMapper annotationLabelMapper;

    /**
     * 获取模板详情
     * @param map
     * @return
     */
    @Override
    public String getTemplateInfo(Map<String, Object> map) {
        String res = null;
        try{
            List<Map<String,Object>> labels ;
            //根据taskid查询模板信息
            Map<String,Object> templateinfo = annotationTemplateMapper.getTemplateInfo(map);
            templateinfo = AnalysisTemplateUtil.makeReturnFuncs(templateinfo);//处理返回的数组对象数据

            if(AnnotationTemplateConstant.ANNOTATION_TYPE_DEPENDENCY.equals(templateinfo.get(AnnotationTemplateConstant.annotationtype))){
                labels = annotationLabelMapper.getLablesByAnnotationType(AnnotationTemplateConstant.ANNOTATION_TYPE_SEQUENCE);
            }else{
                labels = annotationLabelMapper.getLablesByTemplateId(templateinfo.get("templateid")+"");
            }

            List<Map<String,Object>> labelTree = AnalysisLabelUtil.makeLabelTree(labels,"0");//单标签
            List<Map<String,Object>> relationLabelTree = AnalysisLabelUtil.makeLabelTree(labels,"1");//关系标签

            Map<String,Object> returnMap = new HashMap<>();
            returnMap.put("template",templateinfo);
            returnMap.put("labeltree",labelTree);
            returnMap.put("relationlabeltree",relationLabelTree);

            JsonModel jm = new JsonModel(true, "查询模板详情成功", ReturnCode.SUCESS_CODE_0000.getKey(),returnMap);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            log.error("查询模板详情失败",e);
            JsonModel jm = new JsonModel(false, e.getMessage(), ReturnCode.ERROR_CODE_1111.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }
    }

    /**
     * 获取模板列表
     * @param map
     * @return
     */
    @Override
    public String getTemplateList(Map<String, Object> map) {
        String res = null;
        Page page;
        try{
            if(StringUtil.empty(map.get("userid")+"")){
                map.put("userid", JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString()));
            }
            if(StringUtil.empty(map.get("pageNow")+"") && StringUtil.empty(map.get("pageSize")+"")){
                 page = new Page(1,10);
            }else{
                 page = new Page(Integer.parseInt(map.get("pageNow")+""),Integer.parseInt(map.get("pageSize")+""));
            }
            map.put("queryStart",page.getQueryStart());
            map.put("pageSize",page.getPageSize());

            List<Map<String,Object>> templatelist = annotationTemplateMapper.getTemplateList(map);
            List<Map<String,Object>> returnlist = new ArrayList<>();
            for(Map<String,Object> templateinfo : templatelist){

                templateinfo = AnalysisTemplateUtil.makeReturnFuncs(annotationTemplateMapper.getTemplateInfo(templateinfo));//处理返回的数组对象数据

                List<Map<String,Object>> labels = annotationLabelMapper.getLablesByTemplateId(templateinfo.get("templateid")+"");

                List<Map<String,Object>> labelTree = AnalysisLabelUtil.makeLabelTree(labels,"0");//单标签
                List<Map<String,Object>> relationLabelTree = AnalysisLabelUtil.makeLabelTree(labels,"1");//关系标签

                templateinfo.put("labeltree",labelTree);
                templateinfo.put("relationlabeltree",relationLabelTree);
                returnlist.add(templateinfo);
            }

            page.setResultList(returnlist);
            page.setTotal(annotationTemplateMapper.getTemplateListCount(map));
            JsonModel jm = new JsonModel(true, "查询模板列表成功", ReturnCode.SUCESS_CODE_0000.getKey(),page);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            log.error("查询模板列表失败",e);
            JsonModel jm = new JsonModel(false, e.getMessage(), ReturnCode.ERROR_CODE_1111.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }
    }

    @Override
    public String deleteTemplate(Map<String, Object> map) {
        String res = null;
        try{
            if(null != map.get("templateid") && (map.get("templateid")+"").contains(",")){
                StringBuilder builder = new StringBuilder("");
                String[] templateids =(map.get("templateid")+"").split(",");
                for(String id : templateids){
                    builder.append("'"+id + "',");
                }
                Map<String,String> temmap = new HashMap<>(1);
                map.put("templateid",builder.toString().substring(0,builder.toString().length() -1 ));
                annotationTemplateMapper.updateDeleteTemplates(temmap);
            }else{
                annotationTemplateMapper.updateDeleteTemplate(map.get("templateid")+"");
            }

            JsonModel jm = new JsonModel(true, "删除模板成功", ReturnCode.SUCESS_CODE_0000.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            log.error("删除模板失败",e);
            JsonModel jm = new JsonModel(false, e.getMessage(), ReturnCode.ERROR_CODE_1111.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }
    }

    @Override
    public String selectTemplatelist(Map<String, Object> map) {
        String res = null;
        try{
            if(StringUtil.empty(map.get("userid")+"")){
                map.put("userid", JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString()));
            }

            List<Map<String,Object>> templatelist = annotationTemplateMapper.selectTemplatelist(map);
            JsonModel jm = new JsonModel(true, "查询模板列表成功", ReturnCode.SUCESS_CODE_0000.getKey(),templatelist);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            log.error("查询模板列表失败",e);
            JsonModel jm = new JsonModel(false, e.getMessage(), ReturnCode.ERROR_CODE_1111.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }
    }
}
