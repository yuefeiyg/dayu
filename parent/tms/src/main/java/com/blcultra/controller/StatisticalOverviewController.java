package com.blcultra.controller;

import com.blcultra.service.StatisticalOverviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 今日概览中的统计服务
 * Created by sgy05 on 2019/4/9.
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/statistic/")
public class StatisticalOverviewController {

    private static final Logger log = LoggerFactory.getLogger(StatisticalOverviewController.class);

    @Autowired
    private StatisticalOverviewService statisticalOverviewService;

    /**
     * 统计概览
     * @param dimension   维度   用户维度:3 ；   项目维度:0
     * @param begintime   开始时间
     * @param endtime     结束时间
     * @return
     */
    @PostMapping(value = "overview",produces = "application/json;charset=UTF-8")
    public String addDataSet(@RequestParam(value="dimension", required=true) String dimension,
                             @RequestParam(value="begintime", required=false) String begintime,
                             @RequestParam(value="endtime", required=false) String endtime){

        String result = statisticalOverviewService.statistic(dimension, begintime, endtime);
        return result;
    }
}
