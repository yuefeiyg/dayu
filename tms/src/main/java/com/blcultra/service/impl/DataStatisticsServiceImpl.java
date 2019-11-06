package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.blcultra.dao.DataStatisticsMapper;
import com.blcultra.service.DataStatisticsService;
import com.blcultra.service.core.UserServiceUtil;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.JsonModel;
import com.blcultra.support.ReturnCode;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service(value = "dataStatisticsService")
@Transactional
public class DataStatisticsServiceImpl implements DataStatisticsService {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserServiceUtil userServiceUtil;
    @Autowired
    DataStatisticsMapper dataStatisticsMapper;

    @Override
    public String getProjectCounts() {
        String res = null;
        try{
            String userid = JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString());
            Map<String,String> map = new HashMap<>(3);
            map.put("userid",userid);
            Map<String,Integer> result = dataStatisticsMapper.getProjectStatisticCountByUser(map);
            result.putAll(dataStatisticsMapper.getTaskStatisticCountByUser(map));
            JsonModel jm = new JsonModel(true, "统计数据成功", ReturnCode.SUCESS_CODE_0000.getKey(),result);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            log.error("统计查询错误",e);
            JsonModel jm = new JsonModel(false, e.getMessage(), ReturnCode.ERROR_CODE_1111.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }
    }
}
