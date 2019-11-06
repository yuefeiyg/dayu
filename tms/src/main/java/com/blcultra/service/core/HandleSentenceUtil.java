package com.blcultra.service.core;

import com.blcultra.cons.AnnotationConstant;

import java.util.*;
import java.util.stream.Collectors;

public class HandleSentenceUtil {

    /**
     * 计算页码大小
     * @param total
     * @param size
     * @return
     */
    public static int caculatePageNum(int total,int size){
        if(total % size != 0){
            return total / size + 1;
        }
        return total / size;
    }


    /**
     * 计算条数
     * @param sentencemaps
     * @param type
     * @return
     */
    public static int countNum(List<Map<String,Object>> sentencemaps, String type){
        int num = 0;
        String current = "";

        if(AnnotationConstant.RETURNDATATYPE_SENTENCE.equals(type)){
            num = sentencemaps.size();
        }else if (AnnotationConstant.RETURNDATATYPE_PARAGRAPH.equals(type)){
            for(Map<String,Object> sentencemap : sentencemaps) {
                String nowparagraph = (sentencemap.get("dataid") + "_") + (sentencemap.get("pn") + "");
                if ("".equals(current) || ! current.equals(nowparagraph)) {
                    num++;
                    current = nowparagraph;
                }
            }
        }else if(AnnotationConstant.RETURNDATATYPE_TEXT.equals(type)){
            for(Map<String,Object> sentencemap : sentencemaps) {
                if ("".equals(current) || ! current.equals(sentencemap.get("dataid") + "")) {
                    num++;
                    current = sentencemap.get("dataid") + "";
                }
            }
        }

        return num;
    }

    /**
     * 计算分页数据的开始和结束位置
     * @return
     * @throws Exception
     */
    public static Map<String,Integer> caculatePageStartAndEnd(int pageNow,int pageSize,int total) throws Exception{
        Map<String,Integer> returnMap = new HashMap<>(2);
        int max = 0;
        if(pageNow == 0 && pageSize == 0){
            returnMap.put("start",0);
            returnMap.put("end",total);
            return returnMap;
        }else{
            int limit = pageNow * pageSize;
            if(total > 0){
                int pagenum = HandleSentenceUtil.caculatePageNum(total , pageSize);
                if(pagenum < pageNow){
                    throw new Exception("超出页码大小");
                }
                if(total < limit){
                    max = total;
                }else{
                    max = limit;
                }
            }
            if(total < pageNow * pageSize){
                returnMap.put("start",max == 0 ? 0 : (pageNow - 1) * pageSize);
            }else{
                returnMap.put("start",max == 0 ? 0 : max - pageSize);
            }

            returnMap.put("end",max );
            return returnMap;
        }
    }

    /**
     * 获取分页后的数据
     * @param start
     * @param end
     * @param sentences
     * @return
     * @throws Exception
     */
    public static List<Map<String,Object>> getDatas(int start,int end,List<Map<String,Object>> sentences,String type) throws Exception{
        List<Map<String,Object>> returList = new ArrayList<>();
        int len = sentences.size();
        if(AnnotationConstant.RETURNDATATYPE_SENTENCE.equals(type)){
            for(int i = start;i < end;i ++){
                returList.add(sentences.get(i));
            }
        }else if(AnnotationConstant.RETURNDATATYPE_PARAGRAPH.equals(type)){//按照段落分页
            List<String> text_paragraph = new ArrayList<>();
            List<String> newtext_paragraph = new ArrayList<>();
            boolean flag = false;
            for(Map<String,Object> sentence : sentences){
                String now = sentence.get("dataid")+"_"+sentence.get("pn");
                for(String ss : text_paragraph){
                    if(ss.equals(now)){
                        flag = true;
                    }

                }
                if(! flag ){
                    text_paragraph.add(now);
                }
                flag = false;
            }

            for(;start < end;start ++){
                newtext_paragraph.add(text_paragraph.get(start));
            }

            sentences = sentences.stream().filter(
                    (Map<String,Object> map) ->{
                        for(String ss : newtext_paragraph){
                            if(ss.equals(map.get("dataid")+"_"+map.get("pn"))){
                                return true;
                            }
                        }
                        return false;
                    }
            ).collect(Collectors.toList());

            returList.addAll(sentences);
        }else if(AnnotationConstant.RETURNDATATYPE_TEXT.equals(type)){
            int textnum = 0;
            String current = "";
            for(int i = 0;i < len;i ++){

                if("".equals(current) || ! current.equals(sentences.get(i).get("dataid")+"")){
                    textnum ++;
                    current = sentences.get(i).get("dataid")+"";
                }
                if(textnum > start && textnum <= end){
                    returList.add(sentences.get(i));
                }

            }
        }

        return returList;
    }

}
