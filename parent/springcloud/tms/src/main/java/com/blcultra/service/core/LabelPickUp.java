package com.blcultra.service.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabelPickUp {
    /**
     * 利用递归算法将字符层级找出
     * start、end是包含符号时的索引位置
     * realstart、realend才是真正原文的索引位置
     * @param startlabel 标签的开始标签
     * @param endlabel 标签的结束标签
     * @param content 原文本
     * @param subss 截取后字符串
     * @param index 字符在当前字符串的位置
     * @param realindex 字符在原文本中的位置
     * @param outmap 存在字符嵌套时，外侧的字符，初始调用时可以为null
     * @param indexMaps 所有字符
     * @param flag 是否是嵌套标签中的子标签,默认是false
     * @param layer 嵌套子标签层数
     * @return
     */
    public static Map<String,Object> recursiondouble (String startlabel,String endlabel,
                                                      String content, String subss, int index, int realindex,
                                                      int count, Map<String,Integer> outmap, List<Map<String,Object>> indexMaps,boolean flag,int layer) throws Exception{
        Map<String,Object> paramap = new HashMap<>(2);
        int sl = startlabel.length();
        int el = endlabel.length();
        while (subss.contains(startlabel) && subss.indexOf(endlabel) > subss.indexOf(startlabel)){//判断子串含有"{"
            count ++;
            /**
             * 放开始标签
             */
            Map<String,Object> itemmap = new HashMap<>(2);
            Map<String,Integer> indexmap = new HashMap<>(2);
            int start = subss.indexOf(startlabel);
            int indexstart = realindex + start;
            indexmap.put("start",indexstart);
            int indexrealstart = 0;
            if(! flag && layer == 0){//不是嵌套标签，当前标签索引位置减去之前标签的占位，即是标注对象的真正位置
                indexrealstart = realindex + start - (count - 1) * (sl+el);
            }else{//是嵌套标签，当前标签索引位置减去一共计算的标签数的占位，但是这样对于嵌套多层的标签，因为只计算了开始标签，所以要把结束标签加回来
                indexrealstart = realindex + start - count * (sl+el) + el * (layer+1) + sl;
            }

            indexmap.put("realstart",indexrealstart);//减去之前的成对标签
            index = start;
            String subs = subss.substring(index + sl,subss.indexOf(endlabel));
            /**
             * 判断结束标签的情况
             */
            if(subs.indexOf(startlabel) == -1){//没有嵌套
                int end = subss.indexOf(endlabel,index);
                int indexend = realindex + end;
                indexmap.put("end", indexend);
                int indexrealend = 0;
                if(! flag && layer == 0){//不是嵌套标签，当前标签索引位置减去之前标签的占位，即是标注对象的真正位置
                    indexrealend = realindex + end - (count -1) * (sl + el) - sl;
                }else{
                    indexrealend = realindex + end - count * (sl + el) + el * (layer + 1);
                }

                indexmap.put("realend", indexrealend);//减去之前的标签
                index = end;
                subss = subss.substring(index + el);
                realindex += index + el;

                /**
                 * 整理标签信息
                 */
                String item = content.substring(Integer.parseInt(indexmap.get("start")+""),Integer.parseInt(indexmap.get("end")+"") + el);
                if("[".equals(startlabel)){
                    itemmap.put("item",item.substring(1,item.length()-1));
                }else{
                    itemmap.put("item",item.replaceAll("["+startlabel+endlabel+"]",""));
                }
                itemmap.put("index",indexmap);
                indexMaps.add(itemmap);
            }else{//存在嵌套的情况 也就是存在 { aaa{bbb 的情况
                layer ++;
                subss = subss.substring(index +sl);
                realindex += index + sl;
                flag = true;
                Map<String,Object> para = recursiondouble(startlabel,endlabel,content,subss,index,realindex,count,indexmap,indexMaps,flag,layer);
                realindex = Integer.parseInt(para.get("realindex")+"");
                index = Integer.parseInt(para.get("index")+"");
                flag = (boolean)para.get("flag");
                count = (int)para.get("count");
                layer = (int)para.get("layer");
                subss = para.get("str")+"";
            }

        }
        /**
         * 外层标签的结束标签
         */
        if(subss.contains(endlabel)) {
            int end = subss.indexOf(endlabel);
            index = end;
            outmap.put("end",realindex + end);
            outmap.put("realend", realindex + end - (count - layer) * (sl + el) - sl * layer);//减去之前的标签

            String item = content.substring(Integer.parseInt(outmap.get("start")+""),realindex + index + el);
            Map<String,Object> itemmap = new HashMap<>(2);
            itemmap.put("item",item.replaceAll("["+startlabel+endlabel+"]",""));
            itemmap.put("index",outmap);
            indexMaps.add(itemmap);
            subss = subss.substring(index + el);
            paramap.put("str",subss);
            realindex += index + el;
            flag = false;//外层标签计算完毕，需要将标记重置
            layer --;
        }
        paramap.put("index",index);
        paramap.put("realindex",realindex);
        paramap.put("flag",flag);
        paramap.put("count",count);
        paramap.put("layer",layer);

        return paramap;
    }

    /**
     * 单标签
     * @param label 标签
     * @param content 文本
     * @param realindex 实际标注位置
     * @param count 标注个数
     * @return
     */
    public static List<Map<String,Object>> recursionsingle (String label,String content,int realindex,int count) throws Exception{
        List<Map<String,Object>> indexMaps = new ArrayList<>();
        Map<String,Object> paramap = new HashMap<>(2);
        int ll = label.length();
        while (content.contains(label)){
            int pos = content.indexOf(label);
            realindex += pos ;

            Map<String,Object> itemmap = new HashMap<>(2);
            Map<String,Integer> indexmap = new HashMap<>(2);
            if(pos == 0){
                indexmap.put("realstart",0);
                indexmap.put("realend",0);
            }else{
                indexmap.put("realstart",realindex - count * ll);
                indexmap.put("realend",realindex - count * ll);
            }

            itemmap.put("item","");
            itemmap.put("index",indexmap);
            indexMaps.add(itemmap);

            content = content.substring(pos + ll);
            realindex += ll;
            count ++;
        }
        return indexMaps;
    }

    public static List<Map<String,Object>> recursiondoubleofsame (String label,String content,List<Map<String,Object>> indexMaps) throws Exception{
        if(content.contains(label)){
            Map<String,Object> itemmap = new HashMap<>(2);
            Map<String,Integer> indexmap = new HashMap<>(2);
            int start = content.indexOf(label);
            int end = content.lastIndexOf(label);
            indexmap.put("realstart",start);
            indexmap.put("realend",end - label.length());
            itemmap.put("item",content.substring(start + label.length(),end));
            itemmap.put("index",indexmap);
            indexMaps.add(itemmap);
        }
        return indexMaps;
    }

}
