package com.blcultra.controller;

import com.blcultra.service.FileComparisonService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件对比服务接口
 * Created by sgy05 on 2019/1/9.
 */
@RestController
@RequestMapping(value = "/data/compare/")
@CrossOrigin
public class FileComparisonController {

    private static final Logger log = LoggerFactory.getLogger(FileComparisonController.class);

    @Autowired
    FileComparisonService fileComparisonService;

    /**
     * 文件对比，输出对比结果报告
     * @param ids 多个文本id或者多个数据集id，用逗号隔开
     * @param scriptfilename 脚本名称
     * @param type 区别是数据集id还是文本id：1：数据集类型   0：文件类型
     * @return
     */
    @PostMapping(value = "excute" ,produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
//    @RequiresRoles(value={"admin","manager"},logical= Logical.OR)
    public String fileComparison(@RequestParam(value = "ids",required = true) String ids,
                                 @RequestParam(value = "scriptfilename",required = true) String scriptfilename,
                                 @RequestParam(value = "type",required = true) String type){
        String res = fileComparisonService.fileComparison(ids,scriptfilename,type);
        return res;

    }

    /**
     * 查询对比结果信息
     * @param resultid   结果文件ret.txt的id
     * @return
     */
    @GetMapping(value = "view" ,produces = "application/json;charset=UTF-8")
    public String compareResultView(@RequestParam(value = "resultid",required = true) String resultid){

        String res = fileComparisonService.compareResultView(resultid);
        return res;

    }

    /**
     * 查询同源文件对比结果文件信息
     * @param resultid   结果文件ret.txt的id
     * @param datacode   同源文件标识码（例子：E:\BLCUpload\result\1556088593332\ST_0722.text_130954.json   datacode:130954)
     * @return
     */
    @GetMapping(value = "view/sameorigin" ,produces = "application/json;charset=UTF-8")
    public String compareDataCodeFileResultView(@RequestParam(value = "resultid",required = true) String resultid,
                                    @RequestParam(value = "datacode",required = false) String datacode){

        String res = fileComparisonService.compareDataCodeFileResultView(resultid,datacode);
        return res;

    }

    @GetMapping(value = "list" ,produces = "application/json;charset=UTF-8")
    public String getCompareResults(@RequestParam(value = "query",required = false) String query,
                                    @RequestParam(value = "filename",required = false) String filename,
                                    @RequestParam(value = "starttime",required = false) String starttime,
                                    @RequestParam(value = "endtime",required = false) String endtime,
                                    @RequestParam(value = "excutor",required = false) String excutor,
                                    @RequestParam(value = "pageNow",required = false,defaultValue = "1") Integer pageNow,
                                    @RequestParam (value = "pageSize" ,required =false,defaultValue = "5" ) Integer pageSize){

        Map<String,Object> map = new HashMap<>();
        map.put("query",query);
        map.put("filename",filename);
        map.put("starttime",starttime);
        map.put("endtime",endtime);
        map.put("excutor",excutor);
        map.put("pageNow",pageNow);
        map.put("pageSize",pageSize);
        String res = fileComparisonService.getCompareResults(map,pageNow,pageSize);
        return res;

    }

    @GetMapping(value = "download" ,produces = "application/json;charset=UTF-8")
    public String downloadCompareResults(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam(value = "resultids",required = true) String resultids){

        String res = fileComparisonService.downloadCompareResults(request, response,resultids);
        return res;

    }

    /**
     * 查询脚本列表
     * @return
     */
    @PostMapping(value = "getscriptList" ,produces = "application/json;charset=UTF-8")
    public String getscriptList(){

        String res = fileComparisonService.scriptList();
        return res;

    }
}
