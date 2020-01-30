package com.blcultra.util;

import com.dayu.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AnnotationItemsHandle {

    Logger logger = LoggerFactory.getLogger(AnnotationItemsHandle.class);

    private static AnnotationItemsHandle annotationItemsHandle = new AnnotationItemsHandle();

    private static final String predicate = "述语";

    private static final String sentenceornaments = "句饰语";

    private String[] splitsymbols = {"，","、","。"};//隔离符号

    /**
     * 找出核心述语
     * @param sentencemap
     * @return
     */
    public static void  findCorepredicate (Map<String,Object> sentencemap) throws Exception{
        List<Map<String,Object>> items = ( List<Map<String,Object>>)sentencemap.get("items");
        for(int i = 0 ; i < items.size(); i ++ ){
            if("述语".equals( items.get(i).get("labelname"))){
                annotationItemsHandle.checkCorepredicate(items.get(i),i,items);
            }else{
                continue;
            }
        }
    }

    /**
     * 判断当前述语是否是核心述语，即其内部是否还存在述语
     * @param item
     * @param n
     * @param items
     * @throws Exception
     */
    private void  checkCorepredicate (Map<String,Object> item,int n,List<Map<String,Object>> items) throws Exception{
        boolean flag1 = true;
        int startIndex1 = Integer.parseInt(item.get("startoffset")+"");
        int endIndex1 = Integer.parseInt(item.get("endoffset") +"");
        for(int j = n + 1; j < items.size() ; j ++ ){
            if(predicate.equals( items.get(j).get("labelname"))){
                int startIndex2 = Integer.parseInt(items.get(j).get("startoffset")+"");
                int endIndex2 = Integer.parseInt(items.get(j).get("endoffset") +"");
                if(startIndex2 >= startIndex1 && endIndex2 <= endIndex1){
                    flag1 = false;
                    checkCorepredicate(items.get(j),j,items);
                }
            }else{
                continue;
            }
        }
        item.put("corepredicate",flag1);
    }

    /**
     * 依存标注预标注根据规则找建立原始关系
     * @return
     */
    public static void  preHandleToFindRelations (Map<String,Object> sentencemap) throws Exception{
        /**
         * 1、句首的补语（用句饰语标注）依存到句子中的所有核心述语2号位
         * 2、补语（用））框起来的）依存到离它最近的左边的核心述语2号位
         * 3、句首的主宾语块依存到没有被分界标点隔开的右边所有核心述语1号位上
         * 4、句中的所有主宾语块依存到左边离它最近的核心述语3号位上
         */
        String content = sentencemap.get("content") + "";
        List<Map<String,Object>> items = ( List<Map<String,Object>>)sentencemap.get("items");

        annotationItemsHandle.handleSentenceornaments(items);//第一条规则

        annotationItemsHandle.handleComlement(items);//第二条

        annotationItemsHandle.handleMainObject(content,items);//第三条和第四条
    }

    /**
     * 在不同的位置设置值
     * @param corepredicate
     * @param itemid
     * @param startoffset
     * @param endoffset
     * @throws Exception
     */
    public void  putValueOnCondition (Map<String,Object> corepredicate,String position,Object itemid,String wid,String type,
                                             Object startoffset,Object endoffset) throws Exception{
        Map<String,Object> relations;
        if(null == corepredicate.get("relation")){
            relations = new HashMap<>(6);
            corepredicate.put("relation",relations);
        }else{
            relations = (Map<String,Object>)corepredicate.get("relation");
        }
        List<Map<String,Object>> corepredicate2;
        if(null == relations.get(position)){
            corepredicate2 = new ArrayList<>();
            relations.put(position,corepredicate2);
        }else{
            corepredicate2 = (List<Map<String,Object>> )relations.get(position);
        }
        Map<String,Object> corepredicate2map = new HashMap<>(7);
        corepredicate2map.put("itemid",itemid);
        corepredicate2map.put("wid",wid);
        corepredicate2map.put("type",type);
        corepredicate2map.put("startoffset",startoffset);
        corepredicate2map.put("endoffset",endoffset);

        corepredicate2.add(corepredicate2map);
    }

    /**
     * 句首的补语（用句饰语标注）依存到句子中的所有核心述语2号位
     * @param items
     * @throws Exception
     */
    public void  handleSentenceornaments (List<Map<String,Object>> items) throws Exception{
        Map<String,Object> firstitem = items.get(0);
        if(sentenceornaments.equals(firstitem.get("labelname"))){//第一条规则
            int itemsize = items.size();
            Map<String,Object> itemn;
            for(int n = 1 ; n < itemsize ; n ++){
                if(judgeIsCorePredicate(items.get(n))){
                    itemn = items.get(n);
                    annotationItemsHandle.putValueOnCondition(itemn,"2",firstitem.get("itemid"), null,"item",
                            firstitem.get("startoffset"),firstitem.get("endoffset"));
                }
            }
        }
    }

    /**
     * 补语（用））框起来的）依存到离它最近的左边的核心述语2号位
     * @param items
     * @throws Exception
     */
    public void  handleComlement (List<Map<String,Object>> items) throws Exception{
        int itemsize = items.size();
        for(int n = 0; n < itemsize ; n ++ ){
            if( n >= 1 && judgeIsCorePredicate(items.get(n))
                    && sentenceornaments.equals(items.get(n - 1).get("labelname"))){
                Object startoffset = items.get(n).get("endoffset");
                Object endoffset = items.get(n - 1).get("endoffset");
                annotationItemsHandle.putValueOnCondition(items.get(n),"2",null, StringUtil.getUUID(),"window",
                        startoffset,endoffset);
            }
        }
    }

    /**
     * 句首的主宾语块依存到没有被分界标点隔开的右边所有核心述语1号位上
     * 句中的所有主宾语块依存到左边离它最近的核心述语3号位上
     * @param content
     * @param items
     * @throws Exception
     */
    public void  handleMainObject (String content,List<Map<String,Object>> items) throws Exception{
        List<int[]> mainObjectPositions = findMainObjectPosition(content,items);//找出所有未被标注过的位置，并且按照分割符号进行分割

        if( judgeIsCorePredicate(items.get(0))){//第三条规则
            int startindex = Integer.parseInt(items.get(0).get("startoffset")+"");
            if(startindex > 0){
                String s1 = content.substring(0,startindex);
                int index = findSymbol(s1,0);
                if(index > 0){
                    return;
                }else{
                    putValueOnCondition(items.get(0),"1",null,StringUtil.getUUID(),"window",
                            0,startindex);
                }
            }
        }

        /**
         * 第四条规则
         */
        mainObjectPositions.forEach(mainObjectPosition -> {
            items.forEach(item -> {
                if( judgeIsCorePredicate(item)){
                    int endindex = Integer.parseInt(item.get("endoffset")+"");
                    if(endindex < mainObjectPosition[0]){
                        try{
                            putValueOnCondition(item,"3",null,StringUtil.getUUID(),"window",
                                    mainObjectPosition[0],mainObjectPosition[1]);
                        }catch (Exception e){
                            logger.error(e.getMessage(),e);
                        }

                    }
                }
            });
        });

    }

    /**
     * 查找没有被标注过的位置
     * @param content
     * @return
     * @throws Exception
     */
    public List<int[]>  findMainObjectPosition (String content,List<Map<String,Object>> items) throws Exception{
        List<int[]> mainObjectPosition = new LinkedList<>();
        int size = items.size();
        int vernier = 0;
        for(int i = 0 ; i < size ; i ++ ){
            int startoffset = Integer.parseInt(items.get(i).get("startoffset")+"");
            int endoffset = Integer.parseInt(items.get(i).get("endoffset")+"");
            if(i == 0 && startoffset > 0 ){
               int[] indexs = {0,startoffset};
               mainObjectPosition.add(indexs);
                vernier = endoffset;
            }else if(i != 0){
                if(startoffset > vernier && endoffset > vernier){
                    int[] indexs = {vernier,startoffset};
                    mainObjectPosition.add(indexs);
                    vernier = endoffset;
                }
            }

        }
        if(vernier < content.length() - 1){
            int[] indexs = {vernier,content.length() - 1};
            mainObjectPosition.add(indexs);
        }

        /**
         * 检查是否包含隔离符号
         */
        int nsize = mainObjectPosition.size();
        for(int i = 0 ; i < nsize ; i ++ ){
            int[] se = mainObjectPosition.get(i);
            String ss = content.substring(se[0],se[1]);
            int aa= se[0];
            aa = this.findSymbol(ss,aa);
            if(aa > se[0]){
                int[] ins1 = {se[0],aa};
                mainObjectPosition.remove(i);
                mainObjectPosition.add(i,ins1);
                int[] ins2 = {aa,se[1]};
                mainObjectPosition.add(i+1,ins2);
            }
        }
        return mainObjectPosition;
    }

    /**
     * 判断是否为核心述语
     * @param item
     * @return
     */
    public boolean judgeIsCorePredicate (Map<String,Object> item) {
        return null != item.get("corepredicate") && (boolean)item.get("corepredicate");
    }

    public int  findSymbol (String content,int index) throws Exception{
        for(String s : splitsymbols){
            if(content.contains(s)){
                index += content.indexOf(s);
                return index;
            }
        }
        return index;
    }

}
