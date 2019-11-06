package com.blcultra.parser.annotation;

import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.AnnotationLableConstant;

import java.util.Map;

public class AnnotationDataParser {

    /**
     * 数据解析器-用于序列标注的数据
     * @param jsonmap
     * @return
     * @throws Exception
     */
    public static Map<String,Object> sequenceLabelData(Map<String,Object> jsonmap) throws Exception{
        JSONObject datainfo = JSONObject.parseObject(jsonmap.get("datainfo")+"");
        jsonmap.put(AnnotationLableConstant.labelid,datainfo.get(AnnotationLableConstant.labelid));
        jsonmap.put(AnnotationLableConstant.itemid,datainfo.get(AnnotationLableConstant.itemid));
        jsonmap.put(AnnotationLableConstant.item,datainfo.get(AnnotationLableConstant.item));
        jsonmap.put(AnnotationLableConstant.startoffset,datainfo.get(AnnotationLableConstant.startoffset));
        jsonmap.put(AnnotationLableConstant.endoffset,datainfo.get(AnnotationLableConstant.endoffset));
        jsonmap.remove("datainfo");
        return jsonmap;
    }

    /**
     * 数据解析器-用于Q-A标注的数据
     * @param jsonmap
     * @return
     * @throws Exception
     */
    public static Map<String,Object> qaData(Map<String,Object> jsonmap) throws Exception{
        return jsonmap;
    }

}
