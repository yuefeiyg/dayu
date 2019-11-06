package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.blcultra.cons.DataConstant;
import com.blcultra.cons.Messageconstant;
import com.blcultra.dao.DataInfoMapper;
import com.blcultra.dao.DatasetInfoMapper;
import com.blcultra.dao.UserMapper;
import com.blcultra.exception.ExceptionUtil;
import com.blcultra.exception.ServiceException;
import com.blcultra.service.DataInfoService;
import com.blcultra.service.core.DataServiceUtil;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.JsonModel;
import com.blcultra.support.Page;
import com.blcultra.support.ReturnCode;
import com.blcultra.util.FileTypeCheckUtil;
import com.dayu.util.DateFormatUtil;
import com.dayu.util.FileUtil;
import com.dayu.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("dataInfoService")
@Transactional
public class DataInfoServiceImpl implements DataInfoService {
    private  Logger log= LoggerFactory.getLogger(this.getClass());

    @Value("${datasource.origin.filepath}")
    private String datasetfilepath;

    @Autowired
    DataServiceUtil dataServiceUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DataInfoMapper dataInfoMapper;
    @Autowired
    private DatasetInfoMapper datasetInfoMapper;


    /**
     * 上传数据文件到本地服务器的指定位置下
     * @param file
     * @return
     */
    @Override
    public String uploadDatafile(MultipartFile file,String dsid) {
        JsonModel returnJson = new JsonModel();
        try{
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);

            String filename = file.getOriginalFilename();
            //校验文件上传格式
            boolean flag = FileTypeCheckUtil.checkFileTypeBySuffix(filename);

            if(!flag){
                return JSON.toJSONString(new JsonModel(false,"文件不符合格式要求",Messageconstant.REQUEST_SUCCESSED_CODE));
            }

            Map<String,String> checkmap = new HashMap<>(2);

            String filedir = "";
            if(null != dsid){
                Map<String,Object> datasetmap = datasetInfoMapper.getDataSetInfoByDsid(dsid);
                filedir = StringUtil.concat(datasetfilepath,datasetmap.get("dsowner")+"","/");
            }else{
                filedir = StringUtil.concat(datasetfilepath,userId,"/");
            }

            FileUtil.checkFileOrDirExist(filedir,"dir");
            String filepath = StringUtil.concat(filedir,filename);

            if(FileUtil.checkFileIfExsit(filepath))
                return JSON.toJSONString(new JsonModel(false,"文件已存在",Messageconstant.REQUEST_SUCCESSED_CODE));

            File file2 = FileUtil.MultipartFileTransforToFile(file,filedir);
            //FileUtil.deleteFile(file2);

            Map<String,String> returnmap = new HashMap<>(1);
            returnmap.put(DataConstant.PARAM_FILE_NAME,filename);

            return JSON.toJSONString(new JsonModel(true,"上传数据文件成功",Messageconstant.REQUEST_SUCCESSED_CODE,returnmap));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return JSON.toJSONString(new JsonModel(false,"上传数据文件失败",Messageconstant.REQUEST_SUCCESSED_CODE));
        }
    }

    /**
     * 向指定数据集中追加数据文件
     * @param datamap
     * @return
     */
    @Override
    public String addDatafileToDataSet(Map<String, Object> datamap) {
        JsonModel returnJson = new JsonModel();
        try{
            if(null == datamap.get(DataConstant.PARAM_DATAFILELIST)){
                returnJson.setCode(Messageconstant.REQUEST_SUCCESSED_CODE);
                returnJson.setMessage("未选择文件");
                return JSON.toJSONString(returnJson);
            }
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);
            /**
             * 首先根据dsid-数据集id查询数据集相关信息，得到数据集相关信息将其保存到数据文件的信息中
             */
            Map<String,Object> datasetinfo = dataServiceUtil.getDataSetInfoByDsid(datamap.get(DataConstant.DATASET_ID)+"");

            //对需要修改的字段的值进行覆盖
            datasetinfo.put(DataConstant.DATASET_DS_CREATETIME, DateFormatUtil.DateFormat());
            if(null != datamap.get(DataConstant.DATASET_SCENARIO_TYPE)) datasetinfo.put(DataConstant.DATASET_SCENARIO_TYPE, datamap.get(DataConstant.DATASET_SCENARIO_TYPE));
            if(null != datamap.get(DataConstant.DATASET_NOTE)) datasetinfo.put(DataConstant.DATASET_NOTE, datamap.get(DataConstant.DATASET_NOTE));
            if( null !=  datamap.get(DataConstant.DATASET_SEGMENTATION))
                datasetinfo.put(DataConstant.DATASET_SEGMENTATION, datamap.get(DataConstant.DATASET_SEGMENTATION));
            else
                datasetinfo.put(DataConstant.DATASET_SEGMENTATION, DataConstant.SEGMENTATION_PARAGRAPH);

            dataServiceUtil.addDataFiles(datamap.get(DataConstant.PARAM_DATAFILELIST)+"",datasetinfo);

            returnJson.setCode(Messageconstant.REQUEST_SUCCESSED_CODE);
            returnJson.setMessage("上传数据文件成功");
        }catch (Exception e){
            log.error(e.getMessage(),e);
            returnJson.setCode(Messageconstant.REQUEST_FAILED_CODE);
            returnJson.setMessage("上传数据文件失败");
        }
        return JSON.toJSONString(returnJson);
    }

    /**
     * 获取当前用户下指定数据集下所有的数据信息
     * @param datamap
     * @return
     */
    @Override
    public String dataObjectList(Map<String, Object> datamap) {
        Page page = new Page();
        try {
            String createuserid = JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString());
            if(null == createuserid){
                return  JSON.toJSONString(new JsonModel(false,"用户未登录",Messageconstant.REQUEST_FAILED_CODE,null));
            }
//            datamap.put("owner",createuserid);//因为当用户是manager时，查看pmanager创建的数据集就不行了

            if(null != datamap.get("pageNow") && null != datamap.get("pageSize")){
                page.setPageSize(Integer.parseInt(datamap.get("pageSize")+""));
                page.setPageNow(Integer.parseInt(datamap.get("pageNow")+""));
                datamap.put("queryStart",page.getQueryStart());
                datamap.put("pageSize",page.getPageSize());
            }

            List<Map<String,Object>> returnlist = dataInfoMapper.getDataObjectListByDsid(datamap);
            int count = dataInfoMapper.getDataObjectListCountByDsid(datamap);

            page.setTotal(count);
            page.setResultList(returnlist);

            return  JSON.toJSONString(new JsonModel(true,"查询数据列表成功",Messageconstant.REQUEST_SUCCESSED_CODE,page));
        }catch (Exception e){
            log.error(e.getMessage());
            return  JSON.toJSONString(new JsonModel(false,"查询数据列表失败",Messageconstant.REQUEST_FAILED_CODE));
        }
    }

    /**
     * 新增数据集下的数据文件
     * @param map
     * @return
     */
    @Override
    public String addDataInfos(Map<String, String> map) {
        try{
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);
            /**
             * 首先根据dsid-数据集id查询数据集相关信息，得到数据集相关信息将其保存到数据文件的信息中
             */
            Map<String,Object> datasetinfo = dataServiceUtil.getDataSetInfoByDsid(map.get(DataConstant.DATASET_ID));

            //对需要修改的字段的值进行覆盖
            datasetinfo.put(DataConstant.DATASET_DS_CREATETIME, DateFormatUtil.DateFormat());
            if(null != map.get(DataConstant.DATASET_NOTE)) datasetinfo.put(DataConstant.DATASET_NOTE, map.get(DataConstant.DATASET_NOTE));
            if(null != map.get(DataConstant.DATASET_SEGMENTATION)) datasetinfo.put(DataConstant.DATASET_SEGMENTATION, map.get(DataConstant.DATASET_SEGMENTATION));
            dataServiceUtil.addDataFiles(map.get(DataConstant.PARAM_DATAFILELIST),datasetinfo);

            return JSON.toJSONString(new JsonModel(true,"上传数据文件成功",Messageconstant.REQUEST_SUCCESSED_CODE));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return JSON.toJSONString(new JsonModel(false,"上传数据文件失败",Messageconstant.REQUEST_FAILED_CODE));
        }

    }

    /**
     * 删除数据文件
     * @param map
     * @return
     */
    @Override
    public String updatedeleteDataObject(Map<String, String> map) {
        try {
            /**
             * 删除时传入ownerid，具备校验的功能
             */
            String userid = JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString());

            if(null != map.get("dataids") && map.get("dataids").contains(",")){
                String[] dataids = map.get("dataids").split(",");

                StringBuilder ids = new StringBuilder("");
                for(String dataid : dataids){
                    ids.append("'"+dataid+"',");
                }
                map.put("dataid",ids.toString().substring(0,ids.toString().length() - 1));
                map.put("owner",userid);
                map.put("datastate","0");

                dataInfoMapper.updatedeleteDataObjectByids(map);
            }else if(null != map.get("dataids")){
                map.put("datastate","0");
                map.put("dataid",map.get("dataids"));
                dataInfoMapper.updatedeleteDataObjectByid(map);
            }

            return  JSON.toJSONString(new JsonModel(true,"删除数据集成功",Messageconstant.REQUEST_SUCCESSED_CODE));
        }catch (Exception e){
            log.error(e.getMessage());
            return  JSON.toJSONString(new JsonModel(false,"删除数据集失败",Messageconstant.REQUEST_FAILED_CODE));
        }
    }

    /**
     * 下载选中的数据
     * @param request
     * @param response
     * @param map
     * @return
     */
    @Override
    public String downloadDataFiles(HttpServletRequest request, HttpServletResponse response, Map<String, String> map) {
        /**
         * 根据数据集id-dsid查询所有数据文件，获取文件路径，找到文件打包下载
         */
        try {
            if(null != map.get("dataids") && map.get("dataids").contains(",")){//多数据集下载
                String[] dsids = map.get("dataids").split(",");
                dataServiceUtil.downloadMultiData(dsids,response);
            }else if (null != map.get("dataids")){
                //首先查询datainfo获取datapath，然后直接单文件下载
                Map<String,Object>  datainfo = dataInfoMapper.selectDataInfoByid(map.get("dataids"));
                FileUtil.downloadSingleFile(datainfo.get("datapath")+"",response);
            }

            return  JSON.toJSONString(new JsonModel(true,"下载数据成功",Messageconstant.REQUEST_SUCCESSED_CODE));
        }catch (Exception e){
            log.error(e.getMessage());
            return  JSON.toJSONString(new JsonModel(false,"下载数据失败",Messageconstant.REQUEST_FAILED_CODE));
        }
    }

    /**
     *  查看数据详情信息
     * @param map
     * @return
     */
    @Override
    public String getDataInfoByDataId(Map<String, String> map) {
        /**
         * 根据dataid查询datainfo信息和标注信息
         */
        try {
            List<Map<String,Object>> datainfo = dataInfoMapper.getDataInfoByDataId(map);
            //处理标注数据

            return JSON.toJSONString( new JsonModel(true,"下载数据集成功",Messageconstant.REQUEST_SUCCESSED_CODE,datainfo));
        }catch (Exception e){
            log.error(e.getMessage());
            return  JSON.toJSONString(new JsonModel(false,"下载数据集失败",Messageconstant.REQUEST_FAILED_CODE));
        }

    }

    /**
     *  数据层面的数据对比
     * @param map
     * @return
     */
    @Override
    public String dataCompare(Map<String, String> map) {
        return null;
    }


    @Override
    public String deleteondisk(Map<String, String> map) {
        String res = null;
        try{
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);
            String filedir = StringUtil.concat(datasetfilepath,userId,"/");
            FileUtil.checkFileOrDirExist(filedir,"dir");
            String filepath = StringUtil.concat(filedir,map.get("filename"));
            File file = new File(filepath);
            if(file.exists()){
                file.delete();
            }
            JsonModel jm = new JsonModel(true, ReturnCode.SUCESS_CODE_0000.getValue(),ReturnCode.SUCESS_CODE_0000.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            log.error("TaskServiceImpl  getDataTextList   occur exception :"+e);
            ServiceException serviceException=(ServiceException) ExceptionUtil.handlerException4biz(e);
            JsonModel jm = new JsonModel(false, serviceException.getErrorMessage(),serviceException.getErrorCode(),null);
            res = JSON.toJSONString(jm);
        }finally {
            log.info("getDataTextList Response ==>"+res);
        }
        return res;
    }

}
