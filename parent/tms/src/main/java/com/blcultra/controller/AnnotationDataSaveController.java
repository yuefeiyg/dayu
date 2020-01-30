package com.blcultra.controller;

import com.blcultra.dto.LabelDataDto;
import com.blcultra.service.AnnotationDataSaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 标注数据保存控制器类
 * Created by sgy05 on 2019/2/15.
 */
@RestController
@RequestMapping(value = "/annotation/")
@CrossOrigin
public class AnnotationDataSaveController {
    private static final Logger log = LoggerFactory.getLogger(AnnotationDataSaveController.class);

    @Autowired
    private AnnotationDataSaveService annotationDataSaveService;

    /**
     * 小保存接口
     * @param labelDataDto
     * @return
     */
    @RequestMapping(value = "save" ,method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public String gloableSaveAnnotationData(@RequestBody LabelDataDto labelDataDto){

        log.info("进入LabelDataSaveController控制器类  saveLabelData入参：  ==>"+ labelDataDto.toString());
        String res = annotationDataSaveService.gloableSaveAnnotationData(labelDataDto);
        return res;
    }

    /**
     * 大保存接口
     * @param labelDataDto
     * @return
     */
    @RequestMapping(value = "saveall" ,method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public String saveall(@RequestBody LabelDataDto labelDataDto){

        log.info("进入LabelDataSaveController控制器类  saveLabelData入参：  ==>"+ labelDataDto.toString());
        labelDataDto.setSavetype("saveall");
        String res = annotationDataSaveService.gloableSaveAnnotationData(labelDataDto);
        return res;
    }

    /**
     * 通用标注数据，保存接口
     * taskid
     * objectdataid
     * item
     * annotationtype
     * @param data
     * @return
     */
    @PostMapping(value = "v2/save",produces = "application/json;charset=UTF-8")
    public String saveAnnotationData(@RequestBody Map data){

        log.info("进入 AnnotationSaveController 控制器类  saveAnnotationData 入参：  ==>"+ data.toString());
        String res = annotationDataSaveService.saveAnnotationData(data);
        return res;
    }

    /**
     * 删除标注信息
     * @param dataitemid
     * @return
     */
    @GetMapping (value = "v2/delete",produces = "application/json;charset=UTF-8")
    public String deleteAnnotationData(@RequestParam(value = "dataitemid" ) String dataitemid){

        log.info("进入 AnnotationSaveController 控制器类  saveAnnotationData 入参：  ==>"+ dataitemid);
        String res = annotationDataSaveService.deleteAnnotationData(dataitemid);
        return res;
    }

    /**
     * 当前页面全部保存
     * 本页面内容已保存过的不再保存
     * @return
     */
    @PostMapping(value = "v2/saveall",produces = "application/json;charset=UTF-8")
    public String saveAnnotationAllData(@RequestBody Map data){
        log.info("进入 AnnotationSaveController 控制器类  saveAnnotationAllData 入参：  ==>"+ data.toString());
        String res = annotationDataSaveService.saveAnnotationAllData(data);
        return res;
    }

}
