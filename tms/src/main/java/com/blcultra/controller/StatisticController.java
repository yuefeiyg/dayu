package com.blcultra.controller;


import com.blcultra.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "/statistic")
public class StatisticController {

    @Autowired
    StatisticService statisticService;

    /**
     * 导出统计信息
     * @return
     */
    @GetMapping(value = "/export/excel",produces = "application/json;charset=UTF-8")
    @Transactional(rollbackFor = Exception.class)
    public String  exportExcel(
            @RequestParam(value="dimension", required=true) String dimension,
            @RequestParam(value = "begintime",required = false) String begintime,
            @RequestParam(value = "endtime" ,required = false) String endtime){
        Map<String,Object> map = new HashMap<>(2);
        map.put("begintime",begintime);
        map.put("endtime",endtime);
        map.put("dimension",dimension);

        return statisticService.exportExcel(map);
    }

}
