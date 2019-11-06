package com.blcultra.service.impl;

import com.blcultra.dao.UserBehaviorLogMapper;
import com.blcultra.model.UserBehaviorLogWithBLOBs;
import com.blcultra.service.UserBehaviorLogService;
import com.dayu.util.DateFormatUtil;
import com.dayu.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 记录日志接口服务
 * Created by sgy05 on 2019/3/27.
 */
@Service("userBehaviorLogService")
public class UserBehaviorLogServiceImpl implements UserBehaviorLogService {
    private static final Logger log = LoggerFactory.getLogger(UserBehaviorLogServiceImpl.class);
    @Autowired
    private UserBehaviorLogMapper userBehaviorLogMapper;

    /**
     * 日志记录服务
     * @param userBehaviorLogWithBLOBs  需要记录的信息实体
     * @return
     */
    @Override
    public void saveLogs(UserBehaviorLogWithBLOBs userBehaviorLogWithBLOBs) {
        log.info("日志保存接口接收的参数：",userBehaviorLogWithBLOBs.toString());
        try {
//            String token = SecurityUtils.getSubject().getPrincipals().toString();
//            String userid = JWTUtil.getUserId(token);
            userBehaviorLogWithBLOBs.setLogid(StringUtil.getUUID());
            userBehaviorLogWithBLOBs.setOperatetime(DateFormatUtil.DateFormat());
//            userBehaviorLogWithBLOBs.setUserid(userid);
            log.info("保存日志开始。。。。。。");
//            DisruptorUtil.producer(userBehaviorLogWithBLOBs);
            log.info("保存日志结束。。。。。。");
        } catch (Exception e) {
            log.info("saveLogs 保存日志的方法报错：",e.getMessage());
            e.printStackTrace();
        }
    }
}
