package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.blcultra.dao.TaskDictMapper;
import com.blcultra.exception.ExceptionUtil;
import com.blcultra.exception.ServiceException;
import com.blcultra.exception.ValidateException;
import com.blcultra.service.TaskDictService;
import com.blcultra.support.JsonModel;
import com.blcultra.support.ReturnCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by sgy05 on 2018/12/5.
 */
@Service("taskDictService")
public class TaskDictServiceImpl implements TaskDictService {

    private Logger log = LoggerFactory.getLogger(TaskDictServiceImpl.class);
    @Autowired
    private TaskDictMapper taskDictMapper;
    /**
     * 根据字典表的父节点码获取任务字典表数据
     * @return
     */
    @Override
    public String getTaskDictData(String parentcode) {
        String res = null;
        try {
            beforeValidateParam(parentcode);
            List<Map<String,Object>> taskDictData = taskDictMapper.getTaskDictData(parentcode);
            JsonModel jm = new JsonModel(true, ReturnCode.SUCESS_CODE_0000.getValue(), ReturnCode.SUCESS_CODE_0000.getKey(),taskDictData);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e ){
            log.error("=========TaskDictServiceImpl  getTaskDictData   occur exception :"+e);
            ServiceException serviceException=(ServiceException) ExceptionUtil.handlerException4biz(e);
            JsonModel jm = new JsonModel(false, serviceException.getErrorMessage(),serviceException.getErrorCode(),null);
            res = JSON.toJSONString(jm);
        }
        return res;
    }
    //校验参数
    private  void beforeValidateParam(String parentcode){
        if(StringUtils.isEmpty(parentcode)){
            throw new ValidateException("参数为空");
        }

    }
}
