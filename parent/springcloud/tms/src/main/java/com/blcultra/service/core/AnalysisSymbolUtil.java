package com.blcultra.service.core;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class AnalysisSymbolUtil {
    private Logger log= LoggerFactory.getLogger(this.getClass());
    @Autowired
    FindIndexInSentence findIndexInSentence;
    /**
     * 解析带有标注
     * @param sentence
     * @return
     * @throws Exception
     */
    public String analysis(String sentence) {
        /**
         * 重新计算索引位置,并保存
         */
        try{
            JSONObject anninfo = new JSONObject();
            String templateid = "f7dfee4af8cd4c7482ef1e9a77bcfd94";
            anninfo.put("templateid",templateid);
            anninfo.put("items",findIndexInSentence.findLabelIndex(sentence,templateid));
            return anninfo.toJSONString();
        }catch (Exception e){
            log.error("解析带有标注符号的文本异常");
            log.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * 移除标注符号
     */
    public String removeSymbol(String sentence) {
        try{
            return findIndexInSentence.removeLables(sentence);
        }catch (Exception e){
            log.error("移除带有标注符号的文本标注符号异常");
            log.error(e.getMessage(),e);
            return sentence;
        }
    }

}
