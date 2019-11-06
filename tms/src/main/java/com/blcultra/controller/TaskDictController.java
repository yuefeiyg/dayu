package com.blcultra.controller;

import com.blcultra.service.TaskDictService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sgy05 on 2018/12/5.
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/dict")
public class TaskDictController {

    private static final Logger log = LoggerFactory.getLogger(TaskDictController.class);

    @Autowired
    private TaskDictService taskDictService;

    /**
     * 根据字典表的父节点码获取任务字典表数据
     * @param request
     * @param response
     * @param parentcode
     * @return
     */
    @GetMapping(value = "/getTaskDictData",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String getTaskDictData(HttpServletRequest request, HttpServletResponse response,String parentcode) {
        log.info("☆☆☆TaskDictController   getTaskTypes 方法  ☆☆☆");
        String res = taskDictService.getTaskDictData(parentcode);
        return res;

    }
}
