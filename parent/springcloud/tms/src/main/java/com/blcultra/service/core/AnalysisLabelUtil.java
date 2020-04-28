package com.blcultra.service.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.DbConstant;
import com.dayu.util.DateFormatUtil;
import com.dayu.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisLabelUtil {

    /**
     * 递归解析标签叶子节点
     * @param parentLabelId
     * @param labeltree
     * @param createuserid
     * @return
     * @throws Exception
     */
    public static Map<String,List<Map<String,Object>>> labelLeafLoop(Map<String,List<Map<String,Object>>> returndata,
                                                              String parentLabelId, JSONArray labeltree, String createuserid, String templateid) throws Exception{

        if(labeltree != null && labeltree.size() > 0){
            int sequence = 1;
            for(Object label : labeltree){
                JSONObject labelinfo = (JSONObject) label;
                AnalysisTemplateUtil.checkIfEmpty(labelinfo.get("labelname")+"","标签名称不能为空");
                AnalysisTemplateUtil.checkIfEmpty(labelinfo.get("colour")+"","标签颜色不能为空");
                AnalysisTemplateUtil.checkIfEmpty(labelinfo.get("depth")+"","标签层级不能为空");
                AnalysisTemplateUtil.checkIfEmpty(labelinfo.get("isleaf")+"","标签是否为叶子标志不能为空");
                String labelid = StringUtil.getUUID();

                if(! (boolean)labelinfo.get("isleaf") && ! StringUtil.empty(labelinfo.get("leaves")+"")){//不是叶子节点，即还存在下层叶子
                    JSONArray labelLeaves = (JSONArray) labelinfo.get("leaves");
                    AnalysisTemplateUtil.checkIfEmpty(labelLeaves,labelinfo.get("labelname") + "没有叶子节点");
                    returndata = labelLeafLoop(returndata,labelid,labelLeaves,createuserid,templateid);
                }

                Map<String,Object> map = new HashMap<>();
                map.put("templateid" , templateid);
                map.put("labelname" , labelinfo.get("labelname"));
                map.put("shortcut" , labelinfo.get("shortcuts"));
                map.put("colour" , labelinfo.get("colour"));
                map.put("symbol" , null == labelinfo.get("symbol") ? null : JSONObject.toJSONString(labelinfo.get("symbol")));
                map.put("depth" , labelinfo.get("depth"));
                map.put("isleaf" , labelinfo.get("isleaf"));
                map.put("code" , labelinfo.get("code"));
                map.put("labelid" , labelid);
                map.put("createuserid" , createuserid);
                String now = DateFormatUtil.DateFormat();
                map.put("createdate" , now);
                map.put("updatedate" , now);
                map.put("parentlabelid" , parentLabelId);
                map.put("labelstate" , 1);
                map.put("sequence" , sequence);

                JSONArray limitrules = (JSONArray) labelinfo.get("limitrules");

                if(null != limitrules && limitrules.size() > 0){
                    JSONObject rulemap = (JSONObject)limitrules.get(0);
                    map.put("relationlabel" , true);
                    Map<String,Object> ruleMap = new HashMap<>();
                    ruleMap.put("labelid",labelid);
                    ruleMap.put("templateid",templateid);
                    ruleMap.put("arg1type",rulemap.get("arg1type"));
                    ruleMap.put("arg2type",rulemap.get("arg2type"));
                    returndata.get("labelsRules").add(ruleMap);
                }else{
                    map.put("relationlabel" , false);
                }
                returndata.get("labels").add(map);

                sequence ++;
            }

        }
        return  returndata;
    }

    /**
     * 将label数组整理成为树结构的数组
     * @param labels
     * @return
     * @throws Exception
     */
    public  static List<Map<String,Object>> makeLabelTree(List<Map<String,Object>> labels,String relationlabel) throws Exception {

        List<Map<String,Object>> result = new ArrayList<>();
        for(Map<String,Object> label : labels){
            String code = String.valueOf(label.get(DbConstant.CODE));
            label.put(DbConstant.CODE,code.toLowerCase());
            if(relationlabel.equals(label.get("relationlabel"))){
                String parentlabelid = label.get("parentlabelid")+"";
                if("1".equals(relationlabel)){
                    label = makeRelationRules(label);
                }
                if(StringUtil.empty(parentlabelid)){//第一层
                    result.add(label);
                }else{//递归查找
                    result = searchParentLabel(result,label);
                }
            }

        }

        return result;
    }

    /**
     * 反显树结构时，查找叶子节点的父节点
     * @param labels
     * @param label
     * @return
     * @throws Exception
     */
    public  static List<Map<String,Object>> searchParentLabel(List<Map<String,Object>> labels,Map<String,Object> label)
            throws Exception {

        for(Map<String,Object> parentlabel : labels){
            if(label.get("parentlabelid").equals(parentlabel.get("labelid"))){//找到父级节点
                if(null == parentlabel.get("children")){
                    List<Map<String,Object>> children = new ArrayList<>();
                    children.add(label);
                    parentlabel.put("children",children);
                }else{
                    ((List)parentlabel.get("children")).add(label);
                }
            }else{
                if(null != parentlabel.get("children")){
                    searchParentLabel((List<Map<String, Object>>) parentlabel.get("children"),label);
                }
            }
        }

        return labels;
    }

    /**
     * 组装关系标签的映射规则
     * @param labels
     * @return
     * @throws Exception
     */
    public  static List<Map<String,Object>> makeRelationRules(List<Map<String,Object>> labels) throws Exception {

        for(Map<String,Object> label : labels){
            String parentlabelid = label.get("relationlabel")+"";

            if("1".equals(parentlabelid)){//是关系标签
                List<Map<String,Object>> rules = new ArrayList<>(1);
                Map<String,Object> rulemap =  new HashMap<>(2);
                rulemap.put("arg1type",label.get("arg1type"));
                rulemap.put("arg2type",label.get("arg2type"));
                rules.add(rulemap);
                label.put("limitrules",rules);
                label.remove("arg1type");
                label.remove("arg2type");

            }

        }

        return labels;
    }

    /**
     * 关系标签 规则中的标签匹配
     * @param labels
     * @param labelrules
     * @return
     */
    public  static  List<Map<String,Object>>  matchLabel(List<Map<String,Object>> labels,List<Map<String,Object>> labelrules){
        for(Map<String,Object> rule : labelrules){
            for(Map<String,Object> label : labels){
                if(rule.get("arg1type").equals(label.get("labelname"))){
                    rule.put("arg1type",label.get("labelid"));
                }
                if(rule.get("arg2type").equals(label.get("labelname"))){
                    rule.put("arg2type",label.get("labelid"));
                }
            }
        }
        return labelrules;
    }

    public  static Map<String,Object> makeRelationRules(Map<String,Object> label) throws Exception {

        List<Map<String,Object>> rules = new ArrayList<>(1);
        Map<String,Object> rulemap =  new HashMap<>(2);
        rulemap.put("arg1type",label.get("arg1type"));
        rulemap.put("arg2type",label.get("arg2type"));
        rules.add(rulemap);
        label.put("limitrules",rules);
        label.remove("arg1type");
        label.remove("arg2type");

        return label;
    }

}
