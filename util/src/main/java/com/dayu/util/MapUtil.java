package com.dayu.util;

import java.util.Map;

public class MapUtil {

    public  static  Map<String,Object> checkEmptyAndPutValue(Map<String,Object> map,String key,String value,Object defaultValue){
        if(StringUtil.empty(value))
            map.put(key,defaultValue);
        else
            map.put(key,value);
        return  map;
    }
}
