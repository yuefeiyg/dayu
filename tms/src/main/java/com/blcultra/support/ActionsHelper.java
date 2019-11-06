package com.blcultra.support;

import com.alibaba.fastjson.JSON;
import com.blcultra.service.ActionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgy05 on 2019/3/6.
 */
@Component
public class ActionsHelper {
    private static final Logger log = LoggerFactory.getLogger(ActionsHelper.class);
    //
    public static Map<String,Map<String,Object>> actionDictionaries = new LinkedHashMap<String,Map<String,Object>>();
    private static ActionsHelper actionsHelper;

    private static ActionService actionService;
    @Autowired
    public void setUserService(ActionService actionService) {
        this.actionService = actionService;
    }
    //私有化构造函数
    private ActionsHelper(){}

    public static ActionsHelper getInstance(){
        if(ActionsHelper.actionsHelper==null){
            ActionsHelper.actionsHelper = new ActionsHelper();
        }
        return ActionsHelper.actionsHelper;
    }
    /**
     * 初始化数据字典
     */
    public void init(){
        if(ActionsHelper.actionDictionaries.size()==0){
            initDataDictionaries();
        }
    }

    /**
     * 初始化action
     */
    private void initDataDictionaries(){
        log.info("--------------action数据字典初始化开始---------------");
        Long startTime = System.currentTimeMillis();

        List<String> actionMoudles = actionService.getActionMoudles();
        for (String moudle :actionMoudles){
            List<Map<String ,Object>> actionsList =actionService.getActionsListByMoudle(moudle);

            for (Map<String,Object> action :actionsList){
                String ac = (String) action.get("action");
                action.put("hidden",false);
                ActionsHelper.actionDictionaries.put(moudle+"_"+ac,action);
            }
        }
        log.info("action初始化完成后的数据："+ JSON.toJSONString(ActionsHelper.actionDictionaries));
        log.info("--------------action数据字典完成初始化,共用时"+(System.currentTimeMillis()-startTime)+"ms---------------");
    }



}
