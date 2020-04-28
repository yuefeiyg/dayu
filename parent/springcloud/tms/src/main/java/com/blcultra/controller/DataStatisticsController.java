package com.blcultra.controller;

import com.blcultra.service.DataStatisticsService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/datastatistics")
public class DataStatisticsController {
    @Autowired
    DataStatisticsService dataStatisticsService;
    /**
     * 上传模板配置文件，解析配置文件，保存模板信息
     * @return
     */
    @GetMapping(value = "/getProjectCounts",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String  getProjectCounts(){
        return dataStatisticsService.getProjectCounts();
    }


}
