package com.blcultra.controller;


import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.DbConstant;
import com.blcultra.service.*;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping(value = "task")
@CrossOrigin
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskInfoQueryService taskInfoQueryService;

    @Autowired
    private TaskInfoActionService taskInfoActionService;

    @Autowired
    private TaskInfoDataService taskInfoDataService;

    @Autowired
    private TaskInfoCreateService taskInfoCreateService;

    @Autowired
    private TaskInfoStatisticService taskInfoStatisticService;

    @GetMapping(value = "/list/public",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String getPublicTasks(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam(value = "keyword" ,required =false) String keyword,
                                 @RequestParam(value = "searchConditions" ,required =false) String searchConditions,
                                 @RequestParam (value = "pageSize" ,required =false,defaultValue = "5" ) Integer pageSize,
                                 @RequestParam(value = "pageNow",required = false,defaultValue = "1") Integer pageNow){
        Map<String,String> map =( Map<String,String>)JSONObject.parse(searchConditions);
        return taskInfoQueryService.getPublicTasks(pageNow,pageSize,keyword,map);
    }

    @GetMapping(value = "/list/my",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String getMyTasks(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam(value = "keyword" ,required =false) String keyword,
                                 @RequestParam(value = "searchConditions" ,required =false) String searchConditions,
                                 @RequestParam (value = "pageSize" ,required =false,defaultValue = "5" ) Integer pageSize,
                                 @RequestParam(value = "pageNow",required = false,defaultValue = "1") Integer pageNow){
        Map<String,String> map =( Map<String,String>)JSONObject.parse(searchConditions);
        return taskInfoQueryService.getMyTasks(pageNow,pageSize,keyword,map);
    }

    @GetMapping(value = "/list/related",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String getRelatedTasks(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam(value = "keyword" ,required =false) String keyword,
                                  @RequestParam(value = "searchConditions" ,required =false) String searchConditions,
                                 @RequestParam (value = "pageSize" ,required =false,defaultValue = "5" ) Integer pageSize,
                                 @RequestParam(value = "pageNow",required = false,defaultValue = "1") Integer pageNow){
        Map<String,String> map =( Map<String,String>)JSONObject.parse(searchConditions);
        return taskInfoQueryService.getRelatedTasks(pageNow,pageSize,keyword,map);
    }

    /**
     * 添加创建任务
     * @param request
     * @param response
     * @param task
     * @return
     */
    @PostMapping(value = "/add",produces = "application/json;charset=UTF-8")
//    @ApiOperation(value = "创建任务, 状态值为：002001，待认领")
//    @RequiresAuthentication   //需要认证
//    @RequiresRoles("manager,admin")//需要manager角色：项目管理员
    public String addTask(HttpServletRequest request, HttpServletResponse response,
                          @RequestBody Map task) {
        log.info("☆☆☆TaskController   addTask 方法  ☆☆☆");
        String res = taskInfoCreateService.addTask(task);
        return res;
    }

    @GetMapping(value = "/view",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String getTaskInfo(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam (value = "taskId" ,required =true ) String taskId) {
        log.info("☆☆☆TaskController   getTaskInfo  ☆☆☆");
        String res = taskInfoActionService.getTaskInfo(taskId);
        return res;
    }

    /**
     * 更新任务
     * @param request
     * @param response
     * @param task
     * @return
     */
    @PostMapping(value = "/edit",produces = "application/json;charset=UTF-8")
//    @ApiOperation(value = "更新任务")
    @RequiresAuthentication
    public String editTask(HttpServletRequest request, HttpServletResponse response,@RequestBody Map task) {
        log.info("☆☆☆TaskController   editTask 方法  ☆☆☆");

        String res = taskInfoCreateService.editTask(task);
        return res;
    }

    /**
     * 删除任务（单个删除、批量删除）
     * @param request
     * @param response
     * @param ids
     * @return
     */
    @PostMapping(value = "/delete",produces = "application/json;charset=UTF-8")
//    @ApiOperation(value = "批量删除任务，逻辑删除")
    @RequiresAuthentication
    public String deleteTask(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam(value = "taskid",required = true)String ids) {
        log.info("☆☆☆TaskController   deleteTask 方法  ☆☆☆");

        String res = taskInfoActionService.deleteTask(ids);
        return res;
    }

    /**
     * 领取任务
     * @param request
     * @param response
     * @param taskid
     * @return
     */
    @PostMapping(value = "/receive",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String receiveTask(HttpServletRequest request, HttpServletResponse response,
                                       @RequestParam(value = "taskid",required = true) String taskid,
                              @RequestParam(value = "userid" ,required =false) String userid) {
        log.info("☆☆☆TaskController   doBeginOrReceiveTask 方法  ☆☆☆参数，任务ID： {}， 任务状态： {}");
        String res = taskInfoActionService.receiveTask(taskid);
        return res;
    }

    /**
     * 开始任务
     * @param request
     * @param response
     * @param taskid
     * @return
     */
    @PostMapping(value = "/start",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String startTask(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(value = "taskid",required = true) String taskid,
                            @RequestParam(value = "userid" ,required =false) String userid) {
        log.info("☆☆☆TaskController   doBeginOrReceiveTask 方法  ☆☆☆参数，任务ID： {}");
        String res = taskInfoActionService.startTask(taskid);
        return res;
    }

    /**
     * 暂停任务
     * @param request
     * @param response
     * @param taskid
     * @return
     */
    @PostMapping(value = "/pause",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String pauseTask(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(value = "taskid",required = true) String taskid) {
        log.info("☆☆☆TaskController   doBeginOrReceiveTask 方法  ☆☆☆参数，任务ID： {}");
        String res = taskInfoActionService.pauseTask(taskid);
        return res;
    }

    /**
     * 任务扭转
     * @param request
     * @param response
     * @param
     * @return
     */
    @PostMapping(value = "/reverse",produces = "application/json;charset=UTF-8")
//    @RequiresAuthentication
    public String reverseTask(HttpServletRequest request, HttpServletResponse response,
                              @RequestBody Map infos) {
        log.info("☆☆☆TaskController   doBeginOrReceiveTask 方法  ☆☆☆参数，任务ID： {}");
        String res = taskInfoCreateService.reverseTask(infos);
        return res;
    }

    /**
     * 召回任务
     * @param request
     * @param response
     * @param ids
     * @return
     */
    @PostMapping(value = "/recall",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String recallTask(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam(value = "taskid",required = true)String ids) {
        log.info("☆☆☆TaskController   doBeginOrReceiveTask 方法  ☆☆☆参数，任务ID： {}");
        String res = taskInfoActionService.recallTask(ids);
        return res;
    }

    /**
     * 报废任务
     * @param request
     * @param response
     * @param ids
     * @return
     */
    @PostMapping(value = "/invalidate",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String invalidateTask(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam(value = "taskid",required = true)String ids) {
        log.info("☆☆☆TaskController   doBeginOrReceiveTask 方法  ☆☆☆参数，任务ID： {}");
        String res = taskInfoActionService.invalidateTask(ids);
        return res;
    }

    /**
     * 进入任务
     * @param request
     * @param response
     * @param taskid
     * @return
     */
    @PostMapping(value = "/enter",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String enterTask(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(value = "taskid",required = true) String taskid) {
        log.info("☆☆☆TaskController   doBeginOrReceiveTask 方法  ☆☆☆参数，任务ID： {}");
        String res = taskInfoActionService.enterTask(taskid);
        return res;
    }

    /**
     * 提交任务数据（非标注任务和管理员评价入口）
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/commit/data",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String commitTask(HttpServletRequest request, HttpServletResponse response,
                             @RequestBody Map task) {
        log.info("☆☆☆TaskController   doBeginOrReceiveTask 方法  ☆☆☆参数，任务ID： {}， 任务状态： {}"
                ,task.get(DbConstant.TASK_ID),task.get(DbConstant.TASK_STATE));
        String res = taskInfoActionService.commitTask(task);
        return res;
    }

    /**
     * 批量新建任务（上传模版格式文件，解析新建任务）
     * @param request
     * @param response
     * @param file
     * @return
     */
    @PostMapping(value = "/batchAdd",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String batchAddTasks(HttpServletRequest request, HttpServletResponse response,
                                           @RequestParam(value = "file")MultipartFile file) {
        log.info("☆☆☆TaskController   getPublishedTaskList接口  ☆☆☆");
        String res = taskInfoCreateService.batchAdd(file);
        return res;
    }

    /**
     * 下载文件
     * @param request
     * @param response
     * @param filename
     * @param datatype 数据文件，结果文件，附件文件，模板文件
     * @return
     */
    @CrossOrigin
    @GetMapping(value ="/data/download",produces = "application/json;charset=UTF-8")
    //@RequiresAuthentication
    public String downloadDataFile(HttpServletRequest request, HttpServletResponse response,
                                   @RequestParam(value = "taskid",required = false) String taskId,
                                   @RequestParam(value = "filename",required = true) String filename,
                                   @RequestParam(value = "datatype",required = true) String datatype) {
        log.info("☆☆☆TaskController   downloadResultFile 方法,文件名称：  "+filename);
        String res = taskInfoDataService.downloadFile(taskId,filename, datatype,response);
        return res;
    }

    /**
     * 结果文件上传接口
     * @param request
     * @param response
     * @param file
     * @param datatype 数据文件，结果文件，附件文件
     * @return
     */
    @PostMapping(value = "/data/upload",produces = "application/json;charset=UTF-8")
    @ResponseBody
    @RequiresAuthentication
    public String uploadDataFile(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam(value = "file") MultipartFile file,
                                 @RequestParam(value = "datatype",required = false) String datatype) {

        log.info("☆☆☆TaskController   uploadFile  ☆☆☆上传文件");
        String res = taskInfoDataService.uploadDataFile(file,datatype);
        return res;
    }

    /**
     * 删除文件
     * @param request
     * @param response
     * @param filename 为文件的绝对路径
     * @return
     */
    @CrossOrigin
    @GetMapping(value ="/data/delete",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String deleteDataFile(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam(value = "filename",required = true) String filename) {
        log.info("☆☆☆TaskController   deleteFile 方法,文件名称：  "+filename);
        String res = taskInfoDataService.deleteFile(filename, response);
        return res;
    }

    /**
     * 打回任务
     * @param request
     * @param response taskid,需要打回的数据id（sourceid）可选，return（true|false），deadline，备注taskdesc
     * @return
     */
    @PostMapping(value = "/annotation/return/",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String returnTask(HttpServletRequest request, HttpServletResponse response,
                             @RequestBody Map task) {
        log.info("☆☆☆TaskController   doBeginOrReceiveTask 方法  ☆☆☆参数，任务ID： {}， 任务状态： {}"
                ,task.get(DbConstant.TASK_ID),task.get(DbConstant.TASK_STATE));
        String res = taskInfoCreateService.returnTask(task);
        return res;
    }


    /**
     * 打回任务
     * @return
     */
    @PostMapping(value = "/batchReturn",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String batchReturnTask(@RequestBody String taskids) {
        log.info("☆☆☆TaskController   batchReturnTask 方法  ☆☆☆参数，任务IDs： {}",taskids);
        String res = taskInfoCreateService.batchReturnTask(taskids);
        return res;
    }

    /**
     * 统计
     * @param request
     * @param response
     * @param statisticConditions
     * @return
     */
    @PostMapping(value = "/data/statistic",produces = "application/json;charset=UTF-8")
//    @RequiresAuthentication
    public String statistic(HttpServletRequest request, HttpServletResponse response, @RequestBody Map statisticConditions) {
        log.info("☆☆☆TaskController   searchTasksData 方法  ☆☆☆");

        String res = taskInfoStatisticService.statistic(statisticConditions);
        return res;
    }

}
