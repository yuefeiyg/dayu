package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.AnnotationConstant;
import com.blcultra.cons.AnnotationTemplateConstant;
import com.blcultra.dao.*;
import com.blcultra.service.AnnotationDataInfoService;
import com.blcultra.service.core.DataInfoContentUtil;
import com.blcultra.support.JsonModel;
import com.blcultra.support.ReturnCode;
import com.dayu.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service(value = "annotationDataInfoService")
public class AnnotationDataInfoServiceImpl implements AnnotationDataInfoService {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AnnotationTemplateMapper annotationTemplateMapper;
    @Autowired
    DataInfoMapper dataInfoMapper;
    @Autowired
    DataInfoIndexMapper dataInfoIndexMapper;
    @Autowired
    DataInfoContentUtil dataInfoContentUtil;

    @Override
    public String getDataInfo(Map<String, Object> map) {
        String res = null;
        Map<String,Object> returnMap = new HashMap<>();
        try{

            //根据任务id查询标注类型等信息
            Map<String,Object> templateinfo = annotationTemplateMapper.getTemplateInfo(map);

            if(null == templateinfo) throw  new Exception("没有查询到模板信息");

            if(AnnotationConstant.annotation_text.equals(templateinfo.get(AnnotationTemplateConstant.annotationobject))){//文本标注
                dataInfoContentUtil.getDataOfText(map,templateinfo.get(AnnotationTemplateConstant.templatetype)+"",returnMap);
            }else if (AnnotationConstant.annotation_img.equals(templateinfo.get(AnnotationTemplateConstant.annotationobject))){//图像标注数据
                returnMap = dataInfoContentUtil.getLabelImageData(map);
            }

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

    @Override
    public String getAnnotationAndOriginData(Map<String,Object> map) {
        /**
         * 进入比对页面，获取当前标注情况数据和原始数据进行比对,目前只有文字有对比页面
         */
        String res = null;
        Map<String,Object> returnMap = new HashMap<>();
        try{
            /**
             * 查询文本内容
             */
            int pagesize = Integer.parseInt( map.get("pageSize") +"");
            map.put("pageSize",pagesize);
            int pageNow = Integer.parseInt(map.get("pageNow")+"") ;
            map.put("querystart",(pageNow - 1) * pagesize);
            List<Map<String,Object>> contents = dataInfoMapper.getDataInfos(map);
            List<Map<String,Object>> origindata = new ArrayList<>(contents.size());
            contents.forEach(content -> {
                Map<String,Object> originmap = new HashMap<>();
                if(!StringUtil.empty(content.get("anninfos")+"")){
                    JSONObject anninfos = JSON.parseObject(content.get("anninfos")+"");
                    originmap.put("items",anninfos.get("items"));
                }else{
                    originmap.put("items",new ArrayList<>(0));
                }
                content.remove("anninfos");
                originmap.putAll(content);
                origindata.add(originmap);
            });
            dataInfoContentUtil.handleSentenceAndLabelDataItemSingle(contents);

            dataInfoContentUtil.findDifferenceItems(contents,origindata);

            returnMap.put("contents",contents);
            returnMap.put("origincontents",origindata);
            returnMap.put("dataname",contents.get(0).get("dataname"));

            returnMap.put("total",dataInfoMapper.getDataInfosCount(map));//总数
            JsonModel jm = new JsonModel(true, "查询标注信息和原始数据成功", ReturnCode.SUCESS_CODE_0000.getKey(),returnMap);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            log.error("查询模板详情失败",e);
            JsonModel jm = new JsonModel(false, e.getMessage(), ReturnCode.ERROR_CODE_1111.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }
    }

    @Override
    public String getDataIndexByTaskid(Map<String, Object> map) {
        String res = null;
        List<Map<String,Object>> returnMap = new ArrayList<>();
        try{

            //根据任务id查询标注类型等信息
            Map<String,Object> templateinfo = annotationTemplateMapper.getTemplateInfo(map);

            if(null == templateinfo) throw  new Exception("没有查询到模板信息");

            if(AnnotationConstant.annotation_text.equals(templateinfo.get(AnnotationTemplateConstant.annotationobject))){//文本标注
                returnMap =  dataInfoIndexMapper.getDataIndex(map);
            }else if (AnnotationConstant.annotation_img.equals(templateinfo.get(AnnotationTemplateConstant.annotationobject))){//图像标注数据
                map.put(AnnotationTemplateConstant.annotationobject,AnnotationConstant.annotation_img);
                returnMap =  dataInfoIndexMapper.getDataIndex(map);
            }

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

    @Override
    public String getDataByIndexId(Map<String, Object> map) {
        String res = null;
        Map<String,Object> returnMap = new HashMap<>();
        try{

            Map<String,Object> templateinfo = annotationTemplateMapper.getTemplateInfo(map);

            if(null == templateinfo) throw  new Exception("没有查询到模板信息");

            String templatetype = templateinfo.get(AnnotationTemplateConstant.templatetype)+"";

            if(AnnotationConstant.annotation_text.equals(templateinfo.get(AnnotationTemplateConstant.annotationobject))){//文本标注
                dataInfoContentUtil.getDataByIndexIdOfText(map,templatetype,returnMap);
            }else if (AnnotationConstant.annotation_img.equals(templateinfo.get(AnnotationTemplateConstant.annotationobject))){//图像标注数据
                dataInfoContentUtil.getImageDataByIndexId(map,returnMap);
            }

            JsonModel jm = new JsonModel(true, "查询数据成功", ReturnCode.SUCESS_CODE_0000.getKey(),returnMap);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            log.error("查询数据失败",e);
            JsonModel jm = new JsonModel(false, e.getMessage(), ReturnCode.ERROR_CODE_1111.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }
    }


}
