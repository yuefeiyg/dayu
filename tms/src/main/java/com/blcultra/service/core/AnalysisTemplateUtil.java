package com.blcultra.service.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dayu.util.JsonFileUtil;
import com.dayu.util.StringUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalysisTemplateUtil {

    public  static JSONObject analysisTemplate(MultipartFile templateJsonFile) throws Exception {
        InputStream ins = templateJsonFile.getInputStream();
        JSONObject json = JsonFileUtil.ReadJsonFile(ins);//解析json文件
        return json;
    }

    /**
     * 解析actions和modules
     * @param jsonObject
     * @throws Exception
     */
    public  static void analysisFuncs(JSONObject jsonObject) throws Exception {
        //判断actions是否为空，若不为空，执行解析方法
        List<String> objects = new ArrayList<>();
        JSONArray actions = (JSONArray) jsonObject.get("actions");
        if(null != actions && actions.size() > 0){

        }

        //判断modules是否为空，若不为空，执行对应的解析方法
        JSONArray modules = (JSONArray) jsonObject.get("modules");
        if(null != modules && modules.size() > 0){

        }

        //判断modules是否为空，若不为空，执行对应的解析方法
        JSONArray auxshortcutsettings = (JSONArray) jsonObject.get("auxshortcutsettings");
        if(null != auxshortcutsettings && auxshortcutsettings.size() > 0){

        }
    }

    /**
     * 返回模板详情时，对数组对象进行整理
     * @return
     */
    public  static Map<String,Object> makeReturnFuncs(Map<String,Object> map) throws Exception {
        List<String> objects = new ArrayList<>();
        JSONArray actions =  JSONArray.parseArray(map.get("actions")+"");
        if(null != actions && actions.size() > 0){
            map.put("actions",makeFuncsList(actions,"actionname"));
        }

        JSONArray modules = JSONArray.parseArray(map.get("modules")+"");
        if(null != modules && modules.size() > 0){
            map.put("modules",makeFuncsList(modules,"modulename"));
        }

        JSONArray auxshortcutsettings = JSONArray.parseArray(map.get("auxshortcutsettings")+"");
        if(null != auxshortcutsettings && auxshortcutsettings.size() > 0){
            map.put("auxshortcutsettings",makeFuncsList(auxshortcutsettings,"auxactionname"));
        }
        return map;
    }


    /**
     * 返回模板详情时，将数组对象转为字符串数组
     * @param jsonArray
     * @param funcname
     * @return
     */
    public static List<String> makeFuncsList(JSONArray jsonArray,String funcname){
        List<String> objects = new ArrayList<>();
        for(Object jsonobject : jsonArray){
            JSONObject json = (JSONObject) jsonobject;
            if((boolean)json.get("activationflag")){
                objects.add(json.get(funcname)+"");
            }
        }
        return objects;
    }

    /**
     * 检查传入的字符串是否为空
     * @param param
     * @param errorMsg
     * @throws Exception
     */
    public  static void checkIfEmpty(String param,String errorMsg) throws Exception {
        if(StringUtil.empty(param)){
            throw new Exception(errorMsg);
        }
    }

    /**
     * 检查传入的数组是否为空
     * @param jsonArray
     * @param errorMsg
     * @throws Exception
     */
    public  static void checkIfEmpty(JSONArray jsonArray,String errorMsg) throws Exception {
        if(null == jsonArray || jsonArray.size() == 0){
            throw new Exception(errorMsg);
        }
    }
}
