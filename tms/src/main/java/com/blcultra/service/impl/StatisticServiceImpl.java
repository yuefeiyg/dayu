package com.blcultra.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.blcultra.service.StatisticService;
import com.blcultra.service.StatisticalOverviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service(value = "statisticService")
@Transactional
public class StatisticServiceImpl implements StatisticService {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StatisticalOverviewService statisticalOverviewService;

    @Override
    public String exportExcel(Map<String, Object> map) {
        String response = statisticalOverviewService.statistic(map.get("dimension")+"",map.get("begintime")+"",map.get("endtime")+"");
        JSONObject jsonObject = JSONObject.parseObject(response);

        return null;
    }
}
