package com.blcultra.parser.data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.DataConstant;
import com.blcultra.service.core.DataServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataParser {

    @Autowired
    DataServiceUtil dataServiceUtil;

    /**
     * 数据解析器-用于序列标注的数据
     * @param jsonmap
     * @param filem
     * @param filepath
     * @param note
     * @return
     * @throws Exception
     */
    public Map<String,Object> sequenceLabelData(Map<String,Object> jsonmap,Map<String,Object> filem,String filepath,String note) throws Exception{
        List<Map<String,Object>> jsonfileparts = (List<Map<String,Object>>)jsonmap.get("contents");
        String filename = jsonmap.get("filename")+"";
        return dataServiceUtil.insertIntoContentAndDataInfoByJSON(filename,filem, filepath,jsonfileparts,note);
    }

    /**
     * 数据解析器-用于Q-A标注的数据
     *  @param jsonmap 文本信息
     *  @param filem 数据集信息
     *  @param filepath 文件路径
     *  @param note 备注信息
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>> qaData(Map<String,Object> jsonmap,Map<String,Object> filem,String filepath,String note) throws Exception{
        List<Map<String,Object>> jsonfileparts;
        List<Map<String,Object>> returndata = new ArrayList<>();
        String type = jsonmap.get("type")+"";
        if("multi".equals(type)){
            JSONArray qafiles = JSONObject.parseArray(jsonmap.get("data")+"");
            Iterator files = qafiles.iterator();
            while (files.hasNext()){
                jsonfileparts = new ArrayList<>();
                JSONObject qafile =  (JSONObject) files.next();
                String filename = qafile.get("name")+"";

                Map<String,Object> map = new HashMap<>();
                map.put("index",qafile.get("index") + "");
                map.put("content", qafile.toString());
                jsonfileparts.add(map);
                returndata.add(dataServiceUtil.insertIntoContentAndDataInfoByJSON(filename,filem, filepath,jsonfileparts,note));
                jsonfileparts = null;
            }
        }else{
            jsonfileparts = new ArrayList<>();
            JSONObject qafile = JSONObject.parseObject(jsonmap.get("data")+"");
            String filename = jsonmap.get("name")+"";

            Map<String,Object> map = new HashMap<>();
            map.put("index",qafile.get("index") + "");
            map.put("content", qafile.toString());
            jsonfileparts.add(map);
            returndata.add(dataServiceUtil.insertIntoContentAndDataInfoByJSON(filename,filem, filepath,jsonfileparts,note));
        }

        return returndata;
    }

}
