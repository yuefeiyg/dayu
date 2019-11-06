package com.blcultra.service.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dayu.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 给定带有标注符号的句子，将标注符号的位置等信息解析出来
 */
@Component
public class FindIndexInSentence {
    Log logger = LogFactory.getLog(FindIndexInSentence.class);
    public JSONArray findLabelIndex(String content,String templateid){
        JSONArray itemsjsonarray = new JSONArray();
        try{
            /**
             * 计算某一个符号，首先把该编码改为符号，其他符号去掉
             *
             */
            makeItemsJson(getLabelItemsDouble(content,"(",")"),"f3a662521733476282a8e163699e204c","述语",itemsjsonarray);
            makeItemsJson(getLabelItemsDouble(content,"[","]"),"3cc97625d2ce4bc680e43be629f45ec6","句饰语",itemsjsonarray);
            makeItemsJson(getLabelItemsDouble(content,"{","}"),"f338081c113c4f809ff22812983b5e2f","主宾语",itemsjsonarray);
            makeItemsJson(getLabelItemsDouble(content,"<",">"),"40920693214c49f8a419efb6bea787a6","衔接语",itemsjsonarray);
            makeItemsJson(getLabelItemsDouble(content,"<<",">>"),"207b83e0a7784a2e9b00d29c729ecf82","辅助语",itemsjsonarray);
            makeItemsJson(getLabelItemsDouble(content,"**","**"),"1af8bc487c9f4f5bbf3f95c36a030480","错符",itemsjsonarray);

            makeItemsJson(getLabelItemsSingle(content,"()"),"65863779cad246c494eb2754ea6e3d82","空述语",itemsjsonarray);
            makeItemsJson(getLabelItemsSingle(content,"||"),"a4fdda8297fc4a9aa8442553b79065b7","双宾",itemsjsonarray);
            makeItemsJson(getLabelItemsSingle(content,"\\\\"),"9c2b7a2b5f08488382a27de33f2a45c2","注销符",itemsjsonarray);
            makeItemsJson(getLabelItemsSingle(content,"|"),"8b962e36362d4b0193984435c75797b3","隔离符",itemsjsonarray);

        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }

        return itemsjsonarray;
    }

    private void makeItemsJson(List<Map<String,Object>> indexMaps, String labelid, String labelname, JSONArray itemsjsonarray) throws Exception{
        if(indexMaps.size() > 0){
            for (Map<String,Object> indexmap : indexMaps ) {
                String dataitemid = StringUtil.getUUID();
                Map<String,Integer> indexs = (Map<String,Integer>)indexmap.get("index");
                JSONObject item = new JSONObject();
                item.put("itemid",dataitemid);
                item.put("item",indexmap.get("item"));
                item.put("labelid",labelid);
                item.put("startoffset",indexs.get("realstart"));
                item.put("endoffset",indexs.get("realend"));
                item.put("dataitemid",dataitemid);
                item.put("labelname",labelname);
                itemsjsonarray.add(item);
            }
        }

    }
    private List<Map<String,Object>> getLabelItemsDouble(String newcontent,String labelsymbol1,String labelsymbol2)throws Exception {
        if(newcontent.contains(labelsymbol1)){
            if("(".equals(labelsymbol1)){
                newcontent = newcontent.replaceAll("[**<>{}|\\\\<<>>||]","");
                newcontent = newcontent.replace("[","");
                newcontent = newcontent.replace("]","");
                newcontent = newcontent.replace("()","");
            }else if("<".equals(labelsymbol1)){
                newcontent = newcontent.replaceAll("[**(){}|\\\\||]","");
                newcontent = newcontent.replace("[","");
                newcontent = newcontent.replace("]","");
                newcontent = newcontent.replace("<<","");
                newcontent = newcontent.replace(">>","");
            }
            else if("**".equals(labelsymbol1)){
                newcontent = newcontent.replaceAll("[<>(){}|\\\\<<>>||]","");
                newcontent = newcontent.replace("[","");
                newcontent = newcontent.replace("]","");
            }
            else if("{".equals(labelsymbol1)){
                newcontent = newcontent.replaceAll("[**<>()|\\\\<<>>||]","");
                newcontent = newcontent.replace("[","");
                newcontent = newcontent.replace("]","");
            }
            else if("<<".equals(labelsymbol1)){
                newcontent = newcontent.replace("<<","@bl@");
                newcontent = newcontent.replace(">>","@bl@");
                newcontent = newcontent.replaceAll("[**<>(){}|\\\\||]","");
                newcontent = newcontent.replace("[","");
                newcontent = newcontent.replace("]","");
                newcontent = newcontent.replace("@bl@","<<");
                newcontent = newcontent.replace("@bl@",">>");
            }else if("[".equals(labelsymbol1)){
                newcontent = newcontent.replaceAll("[**<>(){}|\\\\<<>>||]","");
            }
            List<Map<String,Object>> indexMaps = new ArrayList<>();
            try{
                if("**".equals(labelsymbol1)){
                    LabelPickUp.recursiondoubleofsame("**",newcontent,indexMaps);
                }else{
                    LabelPickUp.recursiondouble(labelsymbol1,labelsymbol2,newcontent,newcontent,0,0,0,null,indexMaps,false,0);
                }

                return indexMaps;
            }catch (Exception e){
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>解析双标签:"+labelsymbol1+labelsymbol2+"出现问题<<<<<<<<<<<<<<<<<<<<<");
            }
        }
        return new ArrayList<>();
    }
    private List<Map<String,Object>> getLabelItemsSingle(String content,String labelsymbol) throws Exception {
        if(content.contains(labelsymbol)){
            if("()".equals(labelsymbol)){
                content = content.replace("()","@bl@");
                content = content.replaceAll("[**<>(){}|\\\\<<>>||]","");
                content = content.replace("[","");
                content = content.replace("]","");
                content = content.replace("@bl@","()");
            }else if("||".equals(labelsymbol)){
                content = content.replace("||","@bl@");
                content = content.replaceAll("[**<>(){}|\\\\<<>>||]","");
                content = content.replace("[","");
                content = content.replace("]","");
                content = content.replace("@bl@","||");
            }
            else if("\\\\".equals(labelsymbol)){
                content = content.replace("\\\\","@bl@");
                content = content.replaceAll("[**<>(){}|\\\\<<>>||]","");
                content = content.replace("[","");
                content = content.replace("]","");
                content = content.replace("@bl@","\\\\");
            }
            else if("|".equals(labelsymbol)){
                content = content.replace("|","@bl@");
                content = content.replaceAll("[**<>(){}|\\\\<<>>||]","");
                content = content.replace("[","");
                content = content.replace("]","");
                content = content.replace("@bl@","|");
            }
            try{
                List<Map<String,Object>> indexMaps =  LabelPickUp.recursionsingle(labelsymbol,content,0,0);
                return indexMaps;
            }catch (Exception e){
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>解析单标签:"+labelsymbol+"出现问题<<<<<<<<<<<<<<<<<<<<<");
            }

        }
        return new ArrayList<>();

    }

    public String removeLables(String content) throws Exception {
        content = content.replaceAll("[**<>(){}|\\\\<<>>||]","");
        content = content.replace("[","");
        content = content.replace("]","");
        return content;
    }
}
