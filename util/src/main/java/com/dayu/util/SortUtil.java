package com.dayu.util;

import java.util.*;

/**
 * 排序工具
 * Created by sgy
 */
public class SortUtil {
    /**
     * 排序
     * @param list   map集合
     * @param dimension   按哪个维度进行排序，即按map中的哪个字段进行排序
     * @return
     */
    public static List<Map<String,Object>> SortByCompletion(List<Map<String,Object>> list,String dimension){

        Collections.sort(list, new Comparator<Map<String,Object>>(){
            @Override
            public int compare(Map<String,Object> o1, Map<String,Object> o2) {
                int r = 0;
                if(o1!=null && o2!=null) {
                    if(Double.parseDouble(o1.get(dimension)+"") > Double.parseDouble(o2.get(dimension)+"")) {
                        r=-1;
                    } else if(Double.parseDouble(o1.get(dimension)+"") < Double.parseDouble(o2.get(dimension)+"")){
                        r = 1;
                    }
                }
                return r;
            }
        });

        return list;
    }

    public static void main(String[] args) {
        List<Map<String,Object>> list = new ArrayList<>();

        Map<String,Object> overallmap1 = new HashMap<>();
        overallmap1.put("completion",0.5);

        Map<String,Object> overallmap2 = new HashMap<>();
        overallmap2.put("completion",0.6);

        Map<String,Object> overallmap3 = new HashMap<>();
        overallmap3.put("completion",0.8);
        list.add(overallmap2);
        list.add(overallmap1);
        list.add(overallmap3);
        System.out.println(list);
        System.out.println("=========================");
        List<Map<String, Object>> list1 = SortByCompletion(list,"completion");
        System.out.println(list);
        System.out.println("===========================");
        System.out.println(list1);

    }

}
