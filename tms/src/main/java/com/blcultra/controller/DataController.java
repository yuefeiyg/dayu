package com.blcultra.controller;

import com.blcultra.cons.DataConstant;
import com.blcultra.service.DataInfoService;
import com.blcultra.service.DataSetService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/data/dataset")
@CrossOrigin
public class DataController {

    private static final Logger log = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private  DataSetService dataSetService;
    @Autowired
    private DataInfoService dataInfoService;

    /**
     * 新增数据集
     * @param dsname  数据集名称
     * @param dataobjecttype  数据对象类型
     * @param scenariotype   适用场景类型
     * @param datafilelist   文件列表
     * @param note  备注信息
     * @return
     */
    @PostMapping(value = "/add",produces = "application/json;charset=UTF-8")
    @Transactional
    public String addDataSet(
                                @RequestParam(value="dsname", required=false) String dsname,
                                @RequestParam(value="dataobjecttype", required=false) String dataobjecttype,
                                @RequestParam(value="scenariotype", required=false) String scenariotype,
                                @RequestParam(value="datafilelist", required=false) String datafilelist,
                                 @RequestParam(value="segmentation", required=false) String segmentation,
                                @RequestParam(value="note", required=false) String note
    ){
        Map<String,Object> datasetmap = new HashMap<>(10);
        datasetmap.put("dsname",dsname);
        datasetmap.put("dataobjecttype",dataobjecttype);
        datasetmap.put("scenariotype",scenariotype);
        datasetmap.put("datafilelist",datafilelist);
        if(null == segmentation)
            datasetmap.put("segmentation", DataConstant.SEGMENTATION_PARAGRAPH);
        else
            datasetmap.put("segmentation",segmentation);
        datasetmap.put("note",note);

        return dataSetService.addDataSet(datasetmap);
    }


    /**
     * 上传数据源文件
     * @return
     */
    @PostMapping(value = "/dataobject/uploaddatafile",produces = "application/json;charset=UTF-8")
    public String uploadDatafile(
                             @RequestParam(value = "file") MultipartFile file,
                             @RequestParam(value = "dsid" ,required = false) String dsid
    ){
        return dataInfoService.uploadDatafile(file,dsid);
    }


    /**
     * 获取数据集列表
     * @param casetype 适用场景（1：数据集列表，2：创建任务时选择数据集列表）
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/list",produces = "application/json;charset=UTF-8")
    public String datasetList(
                              @RequestParam(value = "casetype",required =false) String casetype,
                              @RequestParam(value = "dsname",required =false) String dsname,
                              @RequestParam (value = "starttime" ,required =false) String starttime,
                              @RequestParam (value = "endtime" ,required =false) String endtime,
                              @RequestParam (value = "dataobjecttype" ,required =false) String dataobjecttype,
                              @RequestParam (value = "scenariotype" ,required =false) String scenariotype,
                              @RequestParam(value = "pageNo",required =false) String pageNo,
                              @RequestParam(value = "pageSize",required =false) String pageSize
    ){
        Map<String,Object> datasetmap = new HashMap<>(10);
        datasetmap.put("casetype",casetype);
        datasetmap.put("dsname",dsname);
        datasetmap.put("starttime",starttime);
        datasetmap.put("endtime",endtime);
        datasetmap.put("dataobjecttype",dataobjecttype);
        datasetmap.put("scenariotype",scenariotype);
        datasetmap.put("pageNo",pageNo);
        datasetmap.put("pageSize",pageSize);
        return dataSetService.datasetList(datasetmap);
    }


    /**
     * 数据集追加文件
     * @return
     */
    @PostMapping(value = "/upload",produces = "application/json;charset=UTF-8")
    public String addDatafileToDataSet(
                                 @RequestParam (value = "dsid" ,required =true) String dsid,
                                 @RequestParam (value = "datafilelist" ,required =true) String datafilelist,
                                 @RequestParam(value=  "scenariotype", required=false) String scenariotype,
                                 @RequestParam(value=  "segmentation", required=false) String segmentation,
                                 @RequestParam (value = "note" ,required =false) String note
    ){
        Map<String,Object> datasetmap = new HashMap<>(5);
        datasetmap.put("dsid",dsid);
        datasetmap.put("datafilelist",datafilelist);
        if(null != segmentation) datasetmap.put("scenariotype",scenariotype);
        if(null != segmentation) datasetmap.put("segmentation",segmentation);
        if(null != note)  datasetmap.put("note",note);
        return dataInfoService.addDatafileToDataSet(datasetmap);
    }


    /**
     * 编辑数据集信息，数据集中无数据时，可编辑所有信息；有数据时，只能编辑名称和备注信息
     * @param dsname  数据集名称
     * @param dataobjecttype  数据对象类型
     * @param scenariotype   适用场景类型
     * @param datafilelist   文件列表
     * @param note  备注信息
     * @return
     */
    @PostMapping(value = "/edit",produces = "application/json;charset=UTF-8")
    public String editDataSet(
                              @RequestParam (value = "dsid" ,required =true) String dsid,
                             @RequestParam(value="dsname", required=false) String dsname,
                             @RequestParam(value="dataobjecttype", required=false) String dataobjecttype,
                             @RequestParam(value="scenariotype", required=false) String scenariotype,
                             @RequestParam(value="datafilelist", required=false) String datafilelist,
                             @RequestParam(value="segmentation", required=false) String segmentation,
                              @RequestParam(value="dsstate", required=false) String dsstate,
                              @RequestParam(value="dsusingstate", required=false) String dsusingstate,
                             @RequestParam(value="note", required=false) String note
    ){
        Map<String,String> datasetmap = new HashMap<>(10);
        datasetmap.put("dsid",dsid);
        datasetmap.put("dsname",dsname);
        datasetmap.put("dataobjecttype",dataobjecttype);
        datasetmap.put("scenariotype",scenariotype);
        datasetmap.put("datafilelist",datafilelist);
        datasetmap.put("segmentation",segmentation);
        if(null != dsstate){
            datasetmap.put("dsstate",dsstate);
        }
        if(null != dsusingstate){
            datasetmap.put("dsusingstate",dsusingstate);
        }
        datasetmap.put("note",note);

        return dataSetService.editDataSet(datasetmap);
    }

    /**
     * 删除数据集及包含的数据（删除前提是数据没有被使用，即所有数据没有被使用）
     * @param dsids 可以是多个数据集id，以逗号隔开
     * @return
     */
    @RequestMapping(value = "/delete",produces = "application/json;charset=UTF-8")
    public String deleteDataSet(@RequestParam (value = "dsids" ,required =true) String dsids
    ){
        Map<String,String> datasetmap = new HashMap<>(1);
        datasetmap.put("dsids",dsids);
        return dataSetService.deleteDataSet(datasetmap);
    }

    /**
     * 下载数据集中所有的数据
     * @param request
     * @param response
     * @param dsids
     * @return
     */
    @RequestMapping(value = "/download",produces = "application/json;charset=UTF-8")
    public String downloadDataFilesByDsid(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam (value = "dsids" ,required =true) String dsids
    ){
        Map<String,String> datasetmap = new HashMap<>(5);
        datasetmap.put("dsids",dsids);
        return dataSetService.downloadDataFilesByDsid(request,response,datasetmap);
    }


    /**
     * 获取当前用户下指定数据集下所有的数据信息
     * @param dsid
     * @param starttime
     * @param endtime
     * @param dataname
     * @param pageNow
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/dataobject/list",produces = "application/json;charset=UTF-8")
    public String dataObjectList(
            @RequestParam (value = "dsid" ,required =true) String dsid,
            @RequestParam (value = "keyword" ,required =false) String keyword,
            @RequestParam (value = "scenariotype" ,required =false) String scenariotype,
            @RequestParam (value = "dataobjecttype" ,required =false) String dataobjecttype,
            @RequestParam (value = "starttime" ,required =false) String starttime,
            @RequestParam (value = "endtime" ,required =false) String endtime,
            @RequestParam (value = "dataname" ,required =false) String dataname,
            @RequestParam(value = "pageNow",required =false) String pageNow,
            @RequestParam(value = "pageSize",required =false) String pageSize
    ){
        Map<String,Object> datasetmap = new HashMap<>(15);
        datasetmap.put("dsid",dsid);
        datasetmap.put("scenariotype",scenariotype);
        datasetmap.put("dataobjecttype",dataobjecttype);
        datasetmap.put("dataname",dataname);
        datasetmap.put("starttime",starttime);
        datasetmap.put("endtime",endtime);
        datasetmap.put("keyword",keyword);
        datasetmap.put("pageNow",pageNow);
        datasetmap.put("pageSize",pageSize);
        return dataInfoService.dataObjectList(datasetmap);
    }

    /**
     * 新增数据集下的数据文件
     * @param dsid
     * @param dataobjecttype
     * @param scenariotype
     * @param datafilelist
     * @param segmentation
     * @param note
     * @return
     */
    @PostMapping(value = "/dataobject/add",produces = "application/json;charset=UTF-8")
    public String addDataInfos(
            @RequestParam (value = "dsid" ,required =true) String dsid,
            @RequestParam(value="dataobjecttype", required=false) String dataobjecttype,
            @RequestParam(value="scenariotype", required=false) String scenariotype,
            @RequestParam(value="datafilelist", required=false) String datafilelist,
            @RequestParam(value="segmentation", required=false) String segmentation,
            @RequestParam(value="note", required=false) String note
    ){
        Map<String,String> datasetmap = new HashMap<>(10);
        datasetmap.put("dsid",dsid);
        datasetmap.put("dataobjecttype",dataobjecttype);
        datasetmap.put("scenariotype",scenariotype);
        datasetmap.put("datafilelist",datafilelist);
        datasetmap.put("segmentation",segmentation);
        datasetmap.put("note",note);

        return dataInfoService.addDataInfos(datasetmap);
    }


    /**
     * 删除具体的数据，可以批量删除
     * @param dataids 数据ID
     * @return
     */
    @RequestMapping(value = "/dataobject/delete",produces = "application/json;charset=UTF-8")
    public String updatedeleteDataObject(@RequestParam (value = "dataids" ,required =true) String dataids
    ){
        Map<String,String> datasetmap = new HashMap<>(1);
        datasetmap.put("dataids",dataids);
        return dataInfoService.updatedeleteDataObject(datasetmap);
    }


    /**
     * 下载选中数据的数据(未完)
     * @param request
     * @param response
     * @param dataids
     * @return
     */
    @RequestMapping(value = "/dataobject/download",produces = "application/json;charset=UTF-8")
    public String downloadDataFiles(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam (value = "dataids" ,required =true) String dataids,
                                    @RequestParam (value = "type" ,required =false) String type
    ){
        Map<String,String> datasetmap = new HashMap<>(1);
        datasetmap.put("dataids",dataids);
        datasetmap.put("type",type);
        return dataInfoService.downloadDataFiles(request,response,datasetmap);
    }

    /**
     *
     * @param request
     * @param response
     * @param dataid
     * @return
     */
    @RequestMapping(value = "/dataobject/info",produces = "application/json;charset=UTF-8")
    public String dataInfoByDataId(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam (value = "dataid" ,required =true) String dataid
    ){
        Map<String,String> datasetmap = new HashMap<>(1);
        datasetmap.put("dataid",dataid);
        return dataInfoService.getDataInfoByDataId(datasetmap);
    }

    /**
     * 数据集层面的数据对比
     * @param request
     * @param response
     * @param dsids 可以是多数据集
     * @return
     */
    @RequestMapping(value = "/compare",produces = "application/json;charset=UTF-8")
    public String dataSetCompare(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam (value = "dsids" ,required =true) String dsids
    ){
        Map<String,String> datasetmap = new HashMap<>(1);
        datasetmap.put("dsids",dsids);
        return dataSetService.dataSetCompare(datasetmap);
    }

    /**
     *  数据层面的数据对比
     * @param request
     * @param response
     * @param dataids
     * @return
     */
    @RequestMapping(value = "/dataobject/compare",produces = "application/json;charset=UTF-8")
    public String dataCompare(HttpServletRequest request, HttpServletResponse response,
                                   @RequestParam (value = "dataids" ,required =true) String dataids
    ){
        Map<String,String> datasetmap = new HashMap<>(1);
        datasetmap.put("dataids",dataids);
        return dataInfoService.dataCompare(datasetmap);
    }


    /**
     * 按照关键字检索，支持数据集层面的检索和数据层面的检索。支持普通检索和高级检索模式，
     * 普通检索为按照关键字在单维度上进行检索，高级检索为组合维度上进行检索
     * @param dsname
     * @param dataobjecttype
     * @param scenariotype
     * @param dataname
     * @param starttime
     * @param endtime
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/search",produces = "application/json;charset=UTF-8")
    public String dataSetSearch(
                                @RequestParam(value = "keyword",required =false) String keyword,
                                @RequestParam(value = "dsname",required =false) String dsname,
                                @RequestParam (value = "dataobjecttype" ,required =false) String dataobjecttype,
                                @RequestParam (value = "scenariotype" ,required =false) String scenariotype,
                                @RequestParam (value = "dataname" ,required =false) String dataname,
                                @RequestParam (value = "note" ,required =false) String note,
                                @RequestParam (value = "starttime" ,required =false) String starttime,
                                @RequestParam (value = "endtime" ,required =false) String endtime,
                                @RequestParam(value = "pageNo",required =false) String pageNo,
                                @RequestParam(value = "pageSize",required =false) String pageSize


    ){
        Map<String,Object> datasetmap = new HashMap<>(15);
        datasetmap.put("keyword",keyword);
        datasetmap.put("dsname",dsname);
        datasetmap.put("dataobjecttype",dataobjecttype);
        datasetmap.put("scenariotype",scenariotype);
        datasetmap.put("note",note);
        datasetmap.put("starttime",starttime);
        datasetmap.put("endtime",endtime);
        datasetmap.put("dataname",dataname);
        datasetmap.put("pageNo",pageNo);
        datasetmap.put("pageSize",pageSize);
        return dataSetService.dataSetSearch(datasetmap);
    }

    /**
     * 新建任务时选择数据数量时选择处理的源数据
     * @param request
     * @param response
     * @return
     */
    @GetMapping(value = "/getDataList",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String getDataTextList(HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam(value = "dsid",required = false) String dsid,
                                  @RequestParam(value = "keyword",required = false) String keyword,
                                  @RequestParam (value = "pageSize" ,required =false,defaultValue = "5" ) Integer pageSize,
                                  @RequestParam(value = "pageNow",required = false,defaultValue = "1") Integer pageNow) {
        log.info("☆☆☆DataTextController   getDataTextList接口  ☆☆☆");
        Map<String,Object> map = new HashMap<>();
        map.put("pageNow",pageNow);
        map.put("pageSize",pageSize);
        map.put("dsid",dsid);
        map.put("keyword",keyword);
        String res = dataSetService.getDataTextList(map);
        return res;
    }

    /**
     * 新建任务时选择数据集
     * @param request
     * @param response
     * @param keyword
     * @param pageSize
     * @param pageNow
     * @return
     */
    @GetMapping(value = "/getDataSetList",produces = "application/json;charset=UTF-8")
//    @RequiresAuthentication
    public String getDataSetList(HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam(value = "keyword",required = false) String keyword,
                                  @RequestParam (value = "pageSize" ,required =false ) Integer pageSize,
                                  @RequestParam(value = "pageNow",required = false) Integer pageNow) {
        log.info("☆☆☆DataTextController   getDataTextList接口  ☆☆☆");
        Map<String,Object> map = new HashMap<>();
        map.put("pageNow",pageNow);
        map.put("pageSize",pageSize);
        map.put("keyword",keyword);
        String res = dataSetService.getDataSettList(map);
        return res;
    }


    /**
     * 从磁盘上删除文件
     * @param filename
     * @return
     */
    @RequestMapping(value = "/deleteondisk",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String deleteondisk(@RequestParam (value = "filename" ,required =true) String filename
    ){
        Map<String,String> datasetmap = new HashMap<>(1);
        datasetmap.put("filename",filename);
        return dataInfoService.deleteondisk(datasetmap);
    }

}
