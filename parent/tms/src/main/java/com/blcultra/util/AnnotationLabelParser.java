package com.blcultra.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.AnnotationConstant;
import com.dayu.util.JsonFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class AnnotationLabelParser {
    private static final Logger log = LoggerFactory.getLogger(AnnotationLabelParser.class);
    private static Map<Integer,Integer> symbolIndex = new HashMap<Integer, Integer>();

    public  String parse(String contentdata,List<Map<String,Object>> items){

        StringBuilder newContent = new StringBuilder();

        JSONObject jsonObject = null;
        try {
            int cursor = 0;

            for (Map<String,Object> item : items) {

                String labelsymbol = item.get("symbol") +"";
                Integer startIndex = Integer.parseInt(item.get("startoffset")+"");
                Integer endIndex = Integer.parseInt(item.get("endoffset") +"");

                System.out.println("内容："+contentdata+"");
                System.out.println("============截取前=======");
                System.out.println("============截取位置======="+cursor+"  ，"+startIndex);
                System.out.println("============字串长度======="+contentdata +"  ，"+contentdata.length());
                newContent.append(contentdata.substring(cursor,startIndex));
                System.out.println("============截取后======="+contentdata.substring(cursor,startIndex));
                if (endIndex != 0){
                    String[] symbols = {};
                    if(! "".equals(labelsymbol)){
                        List jsonArray = (List<String>)JSONArray.parse(labelsymbol);
                        symbols = (String[]) jsonArray.toArray(new String[jsonArray.size()]);
                    }
                    if(symbols.length > 1){
                        int begin = startIndex+symbols[0].length();
                        for (int i=begin;i<contentdata.length();i++){
                            if (symbolIndex.containsKey(i)){
                                int newvalue = symbolIndex.get(i)+symbols[0].length();
                                symbolIndex.put(i,newvalue);
                            }else {
                                symbolIndex.put(i,symbols[0].length());
                            }

                        }
                        begin = endIndex+symbols[1].length();
                        for (int i=begin;i<contentdata.length();i++){
                            if (symbolIndex.containsKey(i)){
                                int newvalue = symbolIndex.get(i)+symbols[1].length();
                                symbolIndex.put(i,newvalue);
                            }else {
                                symbolIndex.put(i,symbols[1].length());
                            }

                        }
                        newContent.append(symbols[0]).append(contentdata.substring(startIndex,endIndex)).append(symbols[1]);
                        cursor = endIndex;
                    }else{
                        System.out.println("======单标签====== "+symbols[0]);
                    }

                }else {
                    int begin = startIndex+labelsymbol.length();
                    for (int i=begin;i<contentdata.length();i++){
                        if (symbolIndex.containsKey(i)){
                            int newvalue = symbolIndex.get(i)+labelsymbol.length();
                            symbolIndex.put(i,newvalue);
                        }else {
                            symbolIndex.put(i,labelsymbol.length());
                        }
                    }
                    newContent.append(labelsymbol);
                    cursor = startIndex;
                }
            }
            newContent.append(contentdata.substring(cursor));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  newContent.toString();

    }


    public  String parse(String path){

        StringBuilder newContent = new StringBuilder();

        JSONObject jsonObject = null;
        try {
            jsonObject = JsonFileUtil.readFileToJson(new FileInputStream(new File(path)));
            String contentdata = jsonObject.getString("content");
            JSONArray items = jsonObject.getJSONArray("items");
            int cursor = 0;

            for (Object item : items) {

                String labelsymbol = ((JSONObject)item).getString("labelsymbol");
                Integer startIndex = ((JSONObject)item).getInteger("startoffset");
                Integer endIndex = ((JSONObject)item).getInteger("endoffset");

                newContent.append(contentdata.substring(cursor,startIndex));
                if (endIndex != 0){
                    String[] symbols = labelsymbol.split(",");
                    int begin = startIndex+symbols[0].length();
                    for (int i=begin;i<contentdata.length();i++){
                        if (symbolIndex.containsKey(i)){
                            int newvalue = symbolIndex.get(i)+symbols[0].length();
                            symbolIndex.put(i,newvalue);
                        }else {
                            symbolIndex.put(i,symbols[0].length());
                        }

                    }
                    begin = endIndex+symbols[1].length();
                    for (int i=begin;i<contentdata.length();i++){
                        if (symbolIndex.containsKey(i)){
                            int newvalue = symbolIndex.get(i)+symbols[1].length();
                            symbolIndex.put(i,newvalue);
                        }else {
                            symbolIndex.put(i,symbols[1].length());
                        }

                    }
                    newContent.append(symbols[0]).append(contentdata.substring(startIndex,endIndex)).append(symbols[1]);
                    cursor = endIndex;
                }else {
                    int begin = startIndex+labelsymbol.length();
                    for (int i=begin;i<contentdata.length();i++){
                        if (symbolIndex.containsKey(i)){
                            int newvalue = symbolIndex.get(i)+labelsymbol.length();
                            symbolIndex.put(i,newvalue);
                        }else {
                            symbolIndex.put(i,labelsymbol.length());
                        }
                    }
                    newContent.append(labelsymbol);
                    cursor = startIndex;
                }
            }
            newContent.append(contentdata.substring(cursor));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  newContent.toString();

    }

    public   List<Map<String,Object>> sortitems(List<Map<String,Object>> items){
        /**
         * items 已经在sql中进行过排序，排序规则为首先按照startoffset进行升序排序，当startoffset相同时，
         * 按照endoffset进行降序排序，但是当遇到单标签时，因为startoffset = endoffset 会造成单标签在双标签
         * 的开始标签内，所以需要再处理一下
         */
        List<Map<String,Object>> newitems = new ArrayList<>();
        boolean flag = false;
        int length = items.size();
        for(int n = 0 ;n < length ; n ++){
            if(n != 0){
                int startIndex = Integer.parseInt(items.get(n).get("startoffset")+"");
                int endIndex = Integer.parseInt(items.get(n).get("endoffset") +"");

                int prestartIndex = Integer.parseInt(items.get(n -1).get("startoffset")+"");
                int preendIndex = Integer.parseInt(items.get(n -1).get("endoffset")+"");

                /**
                 * 遇到前一个符号是全角左括号或者全角左中括号，和当前符号替换
                 */
                if (startIndex == prestartIndex && (AnnotationConstant.ANNOTATION_LABEL_ZKH.equals(items.get(n -1).get("labelname"))
                        || AnnotationConstant.ANNOTATION_LABEL_ZZKH.equals(items.get(n -1).get("labelname")))){
                    Map<String,Object> temp = newitems.get(n -1);
                    newitems.remove(n - 1);
                    newitems.add(items.get(n));
                    newitems.add(temp);
                }
                /**
                 * 当前标签是全角右括号或者全角右中括号，需要遍历之前所有标签的结束位置是否和当前标签位置相同，如果相同，则替换
                 */
                else if (AnnotationConstant.ANNOTATION_LABEL_YKH.equals(items.get(n).get("labelname"))
                        || AnnotationConstant.ANNOTATION_LABEL_YZKH.equals(items.get(n ).get("labelname"))){
                    newitems = recursionEndLabel(newitems,items.get(n));
                }
                /**
                 * 遇到单标签
                 * 遍历单标签之前添加到数组的元素，遇到相同位置有双标签的，交换位置
                 * 首先先将单标签加入到数组中，然后进行排序
                 */
                else if(startIndex == endIndex && newitems.size() > 0 && !AnnotationConstant.ANNOTATION_LABEL_ZKH.equals(items.get(n).get("labelname"))
                    && !AnnotationConstant.ANNOTATION_LABEL_ZZKH.equals(items.get(n).get("labelname"))
                    && !AnnotationConstant.ANNOTATION_LABEL_YKH.equals(items.get(n).get("labelname"))
                    && !AnnotationConstant.ANNOTATION_LABEL_YZKH.equals(items.get(n ).get("labelname"))){
                    newitems.add(items.get(n));
                    int nreitemsize = newitems.size();
                    for(int j = nreitemsize -2 ;j >= 0 ; j --){//从倒数第二个元素开始逆向遍历
                        int pstartIndex = Integer.parseInt(newitems.get(j).get("startoffset")+"");
                        if(startIndex == pstartIndex){
                            Map<String,Object> pitem = newitems.get(j);
                            newitems.set(j,items.get(n));
                            newitems.set(j+1,pitem);
                        }
                    }
                }

                /**
                 *特殊顺序符号顺序颠倒,因为之前没有记录打标签的时间顺序，所以只能按照规则进行排序，当添加时间排序后此部分不需要了
                 */
                else if(prestartIndex == startIndex && preendIndex == endIndex){//相同位置出现多标签
                    String labelname = items.get(n).get("labelname")+"";
                    if(AnnotationConstant.ANNOTATION_LABEL_ZBY.equals(labelname)){//主宾语需要在前边
                        Map<String,Object> temp = newitems.get(n -1);
                        newitems.remove(n - 1);
                        newitems.add(items.get(n));
                        newitems.add(temp);
                        newitems = labelrecursion(newitems,n - 1);
                    }else{
                        newitems.add(items.get(n));
                    }
                }

                else{
                    newitems.add(items.get(n));
                }
            }else{
                newitems.add(items.get(n));
            }

        }
        return  newitems;
    }

    /**
     * 递归改变标签的顺序
     * @param items 标签数组
     * @param index 当前标签在数组的索引
     * @return
     */
    public   List<Map<String,Object>> labelrecursion(List<Map<String,Object>> items,int index){
        if(index >= 1){
            int prestartIndex = Integer.parseInt(items.get(index -1).get("startoffset")+"");
            int preendIndex = Integer.parseInt(items.get(index -1).get("endoffset")+"");

            int startIndex = Integer.parseInt(items.get(index).get("startoffset")+"");
            int endIndex = Integer.parseInt(items.get(index).get("endoffset") +"");

            if(prestartIndex == startIndex && preendIndex == endIndex){//相同位置出现多标签
                Map<String,Object> temp = items.get(index -1);
                items.remove(index - 1);
                items.add(items.get(index));
                items.add(temp);
                items = labelrecursion(items,index - 1);
            }

        }

        return items;
    }


    /**
     * 递归查询之前所有标签和当前标签的结束位置是否相同
     * @param items 所有 标签
     * @param item 当前标签
     * @return
     */
    public   List<Map<String,Object>> recursionEndLabel(List<Map<String,Object>> items,Map<String,Object> item){
        int size = items.size();
        int endIndex = Integer.parseInt(item.get("endoffset") +"");
        for(int i = 0;i < size ; i ++ ){
            if(endIndex ==  Integer.parseInt(items.get(i).get("endoffset")+"")){//找到最近的
                List<Map<String,Object>> newitems = new ArrayList<>(size + 1);
                for(int j = 0 ; j < i ; j ++){
                    newitems.add(items.get(j));
                }
                newitems.add(item);
                for(int j = i ; j < size ; j ++){
                    newitems.add(items.get(j));
                }
                return newitems;
            }
        }
        items.add(item);
        return  items;
    }

//    public   List<Map<String,Object>> recursionEndLabel(List<Map<String,Object>> items,Map<String,Object> item,int n){
//        int endIndex = Integer.parseInt(item.get("endoffset") +"");
//        int preendIndex = Integer.parseInt(items.get(n).get("endoffset")+"");
//        if(endIndex == preendIndex){
//            int size = items.size();
//            List<Map<String,Object>> newitems = new ArrayList<>(size + 1);
//            for(int i = 0 ; i < n ; i ++){
//                newitems.add(items.get(i));
//            }
//            newitems.add(item);
//            for(int i = n ; i < size ; i ++){
//                newitems.add(items.get(i));
//            }
//            return newitems;
//        }else{
//            if(n == 0){
//                items.add(item);
//                return items;
//            }
//            return  recursionEndLabel(items,item,n -1);
//        }
//
//    }

    public  String parse1(String contentdata,List<Map<String,Object>> items){

        StringBuilder newContent = new StringBuilder();

        JSONObject jsonObject = null;
        try {
            Map<Integer, List<String>> symbolsmap = new HashMap<Integer, List<String>>();

            items = sortitems(items);

            for (Map<String,Object> item : items) {

                String labelsymbol = item.get("symbol") +"";
                int startIndex = Integer.parseInt(item.get("startoffset")+"");
                int endIndex = Integer.parseInt(item.get("endoffset") +"");

//                System.out.println("内容："+contentdata+"");

                if (endIndex != startIndex){
                    String[] symbols = {};
                    if(! "".equals(labelsymbol)){
                        if(labelsymbol.contains("\\")){
                            symbols = new String[1];
                            symbols[0] = "\\\\";
                        }else{
                            List jsonArray = (List<String>)JSONArray.parse(labelsymbol);
                            symbols = (String[]) jsonArray.toArray(new String[jsonArray.size()]);
                        }
                    }

                    if (symbolsmap.containsKey(startIndex)){
                        symbolsmap.get(startIndex).add(symbols[0]);

                    }else{
                        List<String> list = new ArrayList<String>();
                        list.add(symbols[0]);
                        symbolsmap.put(startIndex,list);

                    }

                    if (symbolsmap.containsKey(endIndex)){
                        if(symbols.length > 1){
                            //向前补位增加
                            List<String> symbollist = symbolsmap.get(endIndex);

                            List<String> newSymbollist = new ArrayList<>();
                            newSymbollist.add(symbols[1]);
                            for(int m = 0;m <symbollist.size();m ++){
                                newSymbollist.add(symbollist.get(m));
                            }
                            symbolsmap.put(endIndex,newSymbollist);

                        }

                    }else{
                        List<String> list = new ArrayList<String>();
                        if(symbols.length > 1){
                            list.add(symbols[1]);
                            symbolsmap.put(endIndex,list);
                        }
                    }
                }else {
                    if(labelsymbol.contains("\\")){
                        labelsymbol = "\\\\";
                    }else{
                        List jsonArray = (List<String>)JSONArray.parse(labelsymbol);
                        labelsymbol = jsonArray.get(0)+"";
                    }
                    if (symbolsmap.containsKey(startIndex)){
                        symbolsmap.get(startIndex).add(labelsymbol);

                    }else{
                        List<String> list = new ArrayList<String>();
                        list.add(labelsymbol);
                        symbolsmap.put(startIndex,list);
                    }
                }
            }
            Map<Integer,List<String>> resultMap = sortMapByKey(symbolsmap);
            int cursor = 0;
            if(null != resultMap){
                for (Map.Entry<Integer,List<String>> entry : resultMap.entrySet()) {
                    int end = entry.getKey();
                    newContent.append(contentdata.substring(cursor,end));
                    List<String> list = entry.getValue();
                    int symbollength = 0;
                    for (String symbol:list) {
                        newContent.append(symbol);
                        symbollength += symbol.length();
                    }
                    cursor = end;

                    int begin = 0;
                    if (symbolIndex.containsKey(end)){
                        begin = end+symbolIndex.get(end)+symbollength;
                    }else {
                        begin = end+symbollength;
                    }
                    for (int i=begin;i<contentdata.length();i++){
                        if (symbolIndex.containsKey(i)){
                            int newvalue = symbolIndex.get(i)+symbollength;
                            symbolIndex.put(i,newvalue);
                        }else {
                            symbolIndex.put(i,symbollength);
                        }

                    }



                }
                newContent.append(contentdata.substring(cursor));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  newContent.toString();

    }
    public static String parse1s(String contentdata,List<Map<String,Object>> items){

        StringBuilder newContent = new StringBuilder();

        JSONObject jsonObject = null;
        try {
            Map<Integer, List<String>> symbolsmap = new HashMap<Integer, List<String>>();

            items = new AnnotationLabelParser().sortitems(items);

            log.info("parsels  contentdata：",contentdata);
            for (Map<String,Object> item : items) {

                JSONArray symbol = (JSONArray) item.get("labelsymbols");
                JSONArray jsonArray1 = (JSONArray)JSONArray.parse(symbol.get(0)+"");

                String labelsymbol = jsonArray1.toJSONString();

                int startIndex = Integer.parseInt(item.get("startoffset")+"");
                int endIndex = Integer.parseInt(item.get("endoffset") +"");

//                System.out.println("内容："+contentdata+"");

                if (endIndex != startIndex){
                    String[] symbols = {};
                    if(! "".equals(labelsymbol)){
                        if(labelsymbol.contains("\\")){
                            symbols = new String[1];
                            symbols[0] = "\\\\";
                        }else{
                            List jsonArray = (List<String>)JSONArray.parse(labelsymbol);
                            symbols = (String[]) jsonArray.toArray(new String[jsonArray.size()]);
                        }
                    }

                    if (symbolsmap.containsKey(startIndex)){
                        symbolsmap.get(startIndex).add(symbols[0]);

                    }else{
                        List<String> list = new ArrayList<String>();
                        list.add(symbols[0]);
                        symbolsmap.put(startIndex,list);

                    }

                    if (symbolsmap.containsKey(endIndex)){
                        if(symbols.length > 1){
                            symbolsmap.get(endIndex).add(symbols[1]);
                        }

                    }else{
                        List<String> list = new ArrayList<String>();
                        if(symbols.length > 1){
                            list.add(symbols[1]);
                            symbolsmap.put(endIndex,list);
                        }
                    }
                }else {
                    if(labelsymbol.contains("\\")){
                        labelsymbol = "\\\\";
                    }
                    if (symbolsmap.containsKey(startIndex)){
                        symbolsmap.get(startIndex).add(labelsymbol);

                    }else{
                        List<String> list = new ArrayList<String>();
                        list.add(labelsymbol);
                        symbolsmap.put(startIndex,list);
                    }
                }
            }
            Map<Integer,List<String>> resultMap = sortMapByKey(symbolsmap);
            int cursor = 0;
            if(null != resultMap){
                for (Map.Entry<Integer,List<String>> entry : resultMap.entrySet()) {
                    int end = entry.getKey();
                    newContent.append(contentdata.substring(cursor,end));
                    List<String> list = entry.getValue();
                    int symbollength = 0;
                    for (String symbol:list) {
                        newContent.append(symbol);
                        symbollength += symbol.length();
                    }
                    cursor = end;

                    int begin = 0;
                    if (symbolIndex.containsKey(end)){
                        begin = end+symbolIndex.get(end)+symbollength;
                    }else {
                        begin = end+symbollength;
                    }
                    for (int i=begin;i<contentdata.length();i++){
                        if (symbolIndex.containsKey(i)){
                            int newvalue = symbolIndex.get(i)+symbollength;
                            symbolIndex.put(i,newvalue);
                        }else {
                            symbolIndex.put(i,symbollength);
                        }

                    }



                }
                newContent.append(contentdata.substring(cursor));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  newContent.toString();

    }

    public static  Map<Integer,List<String>> sortMapByKey(Map<Integer,List<String>> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<Integer,List<String>> sortMap = new TreeMap<Integer,List<String>>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }

    static class MapKeyComparator implements Comparator<Integer>{


        public int compare(Integer str1, Integer str2) {

            return str1.compareTo(str2);
        }
    }

    public String assemble(String content,int beginIndex,int endindex){

        String item = content.substring(beginIndex,endindex);
        if (symbolIndex.containsKey(beginIndex)){
            beginIndex = beginIndex - symbolIndex.get(beginIndex);
        }
        if (symbolIndex.containsKey(endindex)){
            endindex = endindex - symbolIndex.get(endindex);
        }

        return  null;
    }


}
