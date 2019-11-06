package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.blcultra.dao.StandardDocMapper;
import com.blcultra.service.StandardDocService;
import com.blcultra.support.JsonModel;
import com.blcultra.support.ReturnCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("standardDocService")
public class StandardDocServiceImpl implements StandardDocService {

    private Logger log = LoggerFactory.getLogger(StandardDocServiceImpl.class);

    @Autowired
    StandardDocMapper standardDocMapper;

    @Override
    public String getCatalogues(String docid) {
        String res = null;
        List<Map<String,Object>>  catalogues = standardDocMapper.getCatalogues(docid);

        List<Map<String,Object>> result = getlayers(catalogues);


        JsonModel jm = new JsonModel(true, "查询模板详情成功", ReturnCode.SUCESS_CODE_0000.getKey(),result);
        res = JSON.toJSONString(jm);
        return res;
    }

    @Override
    public String getContent(Map<String, String> map) {
        String res = null;
        List<Map<String,Object>> result = standardDocMapper.getContent(map);
        if(null == map.get("catalogueid")){//全显示
            List<Map<String,Object>> result2 = new ArrayList<>();
            result = getlayers(result);

            result = flatLayers(result,result2);
        }else{//查询当前层级以及子集
            result = getChildren(result);
            List<Map<String,Object>> result2 = new ArrayList<>();
            result = flatLayers(result,result2);
        }
        if(null != map.get("type")){//树结构
            result = getlayers(result);

        }
        JsonModel jm = new JsonModel(true, "查询模板详情成功", ReturnCode.SUCESS_CODE_0000.getKey(),result);
        res = JSON.toJSONString(jm);
        return res;
    }

    @Override
    public String getOneContent(Map<String, String> map) {
        String res = null;
        Map<String,Object> result = standardDocMapper.getOneContent(map);
        String catalogue = result.get("catalogue")+"";
        String content = result.get("content")+"";
        if(null != map.get("catalogueid") && content.contains(catalogue)){
            int start = content.substring(0,content.indexOf(catalogue)).lastIndexOf("<h");
            content = content.substring(start);
            result.put("content",content);
        }
        List<Map<String,Object>> returnlist = new ArrayList<>(1);
        returnlist.add(result);
        JsonModel jm = new JsonModel(true, "查询模板详情成功", ReturnCode.SUCESS_CODE_0000.getKey(),returnlist);
        res = JSON.toJSONString(jm);
        return res;
    }

    public List<Map<String,Object>> getChildren (List<Map<String,Object>> children) {
        for (Map<String,Object> cata : children) {
            Map<String, String> map = new HashMap<>(1);
            map.put("parentcatalogueid",cata.get("catalogueid")+"");
            List<Map<String,Object>> child =standardDocMapper.getContent(map);
            if(null != child){
                getChildren(child);
                cata.put("child",child);
            }else{
                cata.put("child",new ArrayList<>(0));
            }

        }

        return children;
    }

    public List<Map<String,Object>> getlayers (List<Map<String,Object>>  catalogues) {
        List<Map<String,Object>> result = new ArrayList<>();
        for(Map<String,Object> catalogue : catalogues){
            if("1".equals(catalogue.get("layer"))){
                Map<String,Object> cata = new HashMap<>(3);
                cata.put("id",catalogue.get("catalogueid"));
                cata.put("title",catalogue.get("title"));
                cata.put("child",new ArrayList<>());
                if(null != catalogue.get("content")){
                    cata.put("content",catalogue.get("content"));
                }
                result.add(cata);
            }else{
                result = recursion(result,catalogue);
            }

        }
        return result;
    }

    public List<Map<String,Object>> flatLayers(List<Map<String,Object>>  catalogues,List<Map<String,Object>> result) {

        for(Map<String,Object> catalogue : catalogues){
            result.add(catalogue);
            if(null != catalogue.get("child") && ((List)catalogue.get("child")).size() > 0){
                flatLayers((List<Map<String,Object>>)catalogue.get("child"),result);
            }
            catalogue.remove("child");
        }
        return result;
    }


    public List<Map<String,Object>> recursion(List<Map<String,Object>> result,Map<String,Object> catalogue) {
        for (Map<String, Object> cata : result) {
            if (cata.get("id").equals(catalogue.get("parentcatalogueid"))) {
                Map<String, Object> cata2 = new HashMap<>(3);
                cata2.put("id", catalogue.get("catalogueid"));
                cata2.put("title", catalogue.get("title"));
                cata2.put("child", new ArrayList<>());
                if(null != catalogue.get("content")){
                    cata2.put("content",catalogue.get("content"));
                }
                ((List) cata.get("child")).add(cata2);
            }else{
                recursion((List) cata.get("child"), catalogue);
            }
        }
        return result;
    }

}
