package com.blcultra.controller;

import com.blcultra.model.UserBehaviorLogWithBLOBs;
import com.blcultra.service.UserBehaviorLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 暂时没有用到该接口（2019-04-29）
 * 用户操作行为日志服务
 * Created by sgy05 on 2019/3/27.
 */
@RestController
@RequestMapping("/log/")
public class UserBehaviorLogController {
    private static final Logger log = LoggerFactory.getLogger(UserBehaviorLogController.class);

    @Autowired
    private UserBehaviorLogService userBehaviorLogService;

    @RequestMapping(value = "save" ,method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
//    @RequiresAuthentication
    public void saveLogs(@RequestBody UserBehaviorLogWithBLOBs ubl){

        /**
         * taskid, textid, sentenceid, pn, sn, hasmarked, markinfo, operatetime,operation,
         * createordellable, content, beforecontent, beforesymbolcontent, symbolcontent
         *
         */
        log.info("进入LabelDataSaveController控制器类  saveLabelData入参：  ==>"+ ubl.toString());
        userBehaviorLogService.saveLogs(ubl);
        log.info("UserBehaviorLogController  saveLogs 保存日志结束。。。。。。。");
    }

}
