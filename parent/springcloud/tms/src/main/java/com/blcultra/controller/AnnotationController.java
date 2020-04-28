package com.blcultra.controller;

import com.blcultra.service.*;
import com.dayu.util.MapUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "/annotation")
public class AnnotationController {
    @Autowired
    private AnalysisTemplateService analysisTemplateService;
    @Autowired
    TemplateInfoService templateInfoService;
    @Autowired
    private AnnotationDataInfoService annotationDataInfoService;
    @Autowired
    private TaskInfoQueryService taskInfoQueryService;

    /**
     * 上传模板配置文件，解析配置文件，保存模板信息
     * @param userid
     * @param templateJsonFile
     * @return
     */
    @PostMapping(value = "/template/uploadJsonFile",produces = "application/json;charset=UTF-8")
    @Transactional(rollbackFor = Exception.class)
    public String  analysisTemplate(
                                   @RequestParam(value = "userid",required = false) String userid,
                                   @RequestParam(value = "templateJsonFile" ,required = true) MultipartFile templateJsonFile){
        Map<String,Object> map = new HashMap<>(2);
//        map.put("userid","111111");
        map.put("templateJsonFile",templateJsonFile);

        return analysisTemplateService.analysisTemplate(map);
    }

    /**
     * 获取模板列表
     * @param userid
     * @param keyword
     * @param pageNow
     * @param pageSize
     * @return
     */
    @RequestMapping(method = {RequestMethod.GET},value = "/template/templatelist",produces = "application/json;charset=UTF-8")
    public String templatelist(
            @RequestParam(value = "userid",required = false) String userid,
            @RequestParam(value = "keyword", required = false)String keyword,
            @RequestParam(value = "pageNow", required = false, defaultValue = "1") Integer pageNow,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize){
        Map<String,Object> map = new HashMap<>(2);
        map.put("userid",userid);
        map.put("keyword",keyword);
        map.put("pageNow",pageNow);
        map.put("pageSize",pageSize);

        return templateInfoService.getTemplateList(map);
    }

    /**
     * 删除模板
     * @param templateid
     * @return
     */
    @PostMapping(value = "/template/delete",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    @RequiresRoles(value={"admin"})
    public String  deleteTemplate(
            @RequestParam(value = "templateid" ,required = true) String templateid){
        Map<String,Object> map = new HashMap<>(2);
        map.put("templateid",templateid);

        return templateInfoService.deleteTemplate(map);
    }

    /**
     * 选取模板下拉选列表
     * @param userid
     * @return
     */
    @RequestMapping(method = {RequestMethod.GET},value = "/template/selectTemplatelist",produces = "application/json;charset=UTF-8")
    public String selectTemplatelist(
            @RequestParam(value = "userid",required = false) String userid){
        Map<String,Object> map = new HashMap<>(2);
        map.put("userid",userid);

        return templateInfoService.selectTemplatelist(map);
    }

    /**
     * 获取模板详情
     * @param taskid
     * @return
     */
    @RequestMapping(method = {RequestMethod.GET},value = "/template/templateinfo",produces = "application/json;charset=UTF-8")
    public String analysisTemplate(
            @RequestParam(value = "taskid" ,required =true) String taskid){
        Map<String,Object> map = new HashMap<>(2);
        map.put("taskid",taskid);

        return templateInfoService.getTemplateInfo(map);
    }

    /**
     * 进入标注页面查询任务标注数据信息
     * @param taskid
     * @param pageSize
     * @param pageNow
     * @param datatype 文本返回数据组装类型： sentence、paragraph、text
     *                 分页是针对数据组装类型做的
     * @param contenttype content目录层级类型：single、multi
     * @return
     */
    @RequestMapping(value = "/data/datainfo",produces = "application/json;charset=UTF-8",method = RequestMethod.GET)
    public String getDataInfo(
            @RequestParam(value = "taskid" ,required =true) String taskid,
            @RequestParam(value = "pageSize" ,required =false) String pageSize,//标注对象
            @RequestParam(value = "pageNow" ,required =false) String pageNow,//标注类型
            @RequestParam(value = "datatype" ,required =false) String datatype,//返回数据格式
            @RequestParam(value = "contenttype" ,required =false) String contenttype
    ){
        Map<String,Object> map = new HashMap<>(1);
        map.put("taskid",taskid);
        MapUtil.checkEmptyAndPutValue(map,"pageNow",pageNow,"1");
        MapUtil.checkEmptyAndPutValue(map,"pageSize",pageSize,"1");
        MapUtil.checkEmptyAndPutValue(map,"datatype",datatype,"text");
        MapUtil.checkEmptyAndPutValue(map,"contenttype",contenttype,"single");

        return annotationDataInfoService.getDataInfo(map);
    }

    /**
     * 进入标注页面查询任务下的索引数据
     * @param taskid
     * @return
     */
    @RequestMapping(value = "/data/getindex",produces = "application/json;charset=UTF-8")
    public String getDataIndexByTaskid(
            @RequestParam(value = "taskid" ,required =true) String taskid
    ){
        Map<String,Object> map = new HashMap<>(1);
        map.put("taskid",taskid);
        return annotationDataInfoService.getDataIndexByTaskid(map);
    }

    /**
     * 根据索引获取文本信息
     * @param dataindexid
     * @return
     */
    @RequestMapping(value = "/data/getDataByIndexId",produces = "application/json;charset=UTF-8")
    public String getDataByIndexId(
            @RequestParam(value = "taskid" ,required =true) String taskid,
            @RequestParam(value = "dataindexid" ,required =true) String dataindexid
    ){
        Map<String,Object> map = new HashMap<>(1);
        map.put("taskid",taskid);
        map.put("dataindexid",dataindexid);
        return annotationDataInfoService.getDataByIndexId(map);
    }

    /**
     * 获取任务详情
     * @param request
     * @param response
     * @param taskid
     * @return
     */
    @GetMapping(value = "/getTaskInfo",produces = "application/json;charset=UTF-8")
    public String getTaskInfo(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam (value = "taskid" ,required =true ) String taskid) {
        String res = taskInfoQueryService.getTaskInfo(taskid);
        return res;
    }

    /**
     * 获取标注数据与原始数据
     * 做对比
     * @param request
     * @param response
     * @param taskid
     * @return
     */
    @GetMapping(value = "/getAnnotationAndOriginData",produces = "application/json;charset=UTF-8")
    public String getAnnotationAndOriginData(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(value = "pageSize" ,required =false) String pageSize,//标注对象
                              @RequestParam(value = "pageNow" ,required =false) String pageNow,//标注类型
                              @RequestParam (value = "taskid" ,required =true ) String taskid) {
        Map<String,Object> map = new HashMap<>(1);
        map.put("taskid",taskid);
        MapUtil.checkEmptyAndPutValue(map,"pageNow",pageNow,"1");
        MapUtil.checkEmptyAndPutValue(map,"pageSize",pageSize,"1");
        String res = annotationDataInfoService.getAnnotationAndOriginData(map);
        return res;
    }


}
