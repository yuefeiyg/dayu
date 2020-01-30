package com.blcultra.controller;

import com.blcultra.service.StandardDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "/standard")
public class StandardDocController {
    @Autowired
    private StandardDocService standardDocService;

    /**
     * 获取标注规范文档目录结构
     * @return
     */
    @GetMapping (value = "/getCatalogue",produces = "application/json;charset=UTF-8")
    public String  getCatalogue(
                                   @RequestParam(value = "docid",required = false ,defaultValue = "1") String docid){

        return standardDocService.getCatalogues(docid);
    }

    /**
     * 根据目录id获取内容
     * @return
     */
    @GetMapping (value = "/getContent",produces = "application/json;charset=UTF-8")
    public String  getContent(
            @RequestParam(value = "docid",required = false) String docid,
            @RequestParam(value = "catalogueid",required = false) String catalogueid,
            @RequestParam(value = "type",required = false) String type){
        Map<String,String> map = new HashMap<>(2);
        if(null != docid) map.put("docid",docid);
        if(null != catalogueid) map.put("catalogueid",catalogueid);
        if(null != type) map.put("type",type);
        return standardDocService.getOneContent(map);
    }

}
