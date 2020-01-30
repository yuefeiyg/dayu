package com.blcultra.controller;

import com.blcultra.service.AnnotationDataSubmitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 任务完成后提交标注数据
 * Created by sgy05 on 2019/3/13.
 */
@RestController
@RequestMapping("/annotation/")
public class AnnotationDataSubmitController {

    private static final Logger log = LoggerFactory.getLogger(AnnotationDataSubmitController.class);

    @Autowired
    private AnnotationDataSubmitService annotationDataSubmitService;

    @PostMapping(value = "submit" ,produces = "application/json;charset=UTF-8")
    public String submitLabelData(@RequestBody Map submitinfo){

        log.info("进入AnnotationDataExportController控制器类  submitLabelData  入参==>" +
                "任务taskid:{},标注对象类型annotationobject:{},模板类型templatetype:{},标注类型annotationtype:{}");
        String res = annotationDataSubmitService.submitLabelData(submitinfo);
        return res;
    }


    /**
     * 提交标注任务后转储标注数据为json文件
     * @param taskid  任务IDs

     * @return
     */
    @PostMapping(value = "batchsubmit" ,produces = "application/json;charset=UTF-8")
    public String batchSubmitLabelData(  @RequestParam(value = "taskid",required = true) String taskid){

        log.info("进入AnnotationDataExportController控制器类  submitLabelData  入参==>" +
                "任务taskid:{}"+ taskid) ;
        String res = annotationDataSubmitService.batchSubmitLabelData(taskid);
        return res;
    }


    /**
     * 通用标注数据，提交/审核提交接口
     * @param data
     * taskid
     * tasktype 标注任务或者审核任务
     * ispass 是否通过
     * annotationtype 标注类型
     * note 备注信息
     * @return
     */
    @PostMapping(value = "v2/submit",produces = "application/json;charset=UTF-8")
    public String submit(@RequestBody Map data){

        log.info("进入 AnnotationSubmitController 控制器类  submit 入参：  ==>"+ data.toString());
        String res = annotationDataSubmitService.submitData(data);
        return res;
    }

}
