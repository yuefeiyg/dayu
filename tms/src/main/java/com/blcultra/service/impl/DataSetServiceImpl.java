package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.blcultra.cons.Common;
import com.blcultra.cons.Messageconstant;
import com.blcultra.dao.DataInfoMapper;
import com.blcultra.dao.DatasetInfoMapper;
import com.blcultra.exception.ExceptionUtil;
import com.blcultra.exception.ServiceException;
import com.blcultra.service.DataSetService;
import com.blcultra.service.core.DataServiceUtil;
import com.blcultra.service.core.UserServiceUtil;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.JsonModel;
import com.blcultra.support.Page;
import com.blcultra.support.ReturnCode;
import com.dayu.util.DateFormatUtil;
import com.dayu.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("dataSetService")
@Transactional(rollbackFor=Exception.class)
public class DataSetServiceImpl implements DataSetService {
    private  Logger log= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DatasetInfoMapper datasetInfoMapper;
    @Autowired
    private DataServiceUtil dataServiceUtil;
    @Autowired
    private DataInfoMapper dataInfoMapper;
    @Autowired
    private UserServiceUtil userServiceUtil;

    /**
     * 新增数据集
     * @param map
     * @return
     */
    @Override
    public String addDataSet(Map<String, Object> map) {
        try {
            String dsid = StringUtil.getUUID();
            String createuserid = JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString());
            map.put("dsowner",createuserid);
            //数据集名称的一致性校验问题
            Map<String,Object> checkmap = datasetInfoMapper.checkIfExsit(map);
            if(null != checkmap){
                return  new JsonModel(false,"已经创建过相同名称的数据集",Messageconstant.REQUEST_SUCCESSED_CODE).toString();
            }

            map.put("dsid",dsid);
            map.put("dscreatetime", DateFormatUtil.DateFormat());
            map.put("dsstate",1);
            map.put("dsusingstate",0);

            String datafilelist = map.get("datafilelist")+"";//数据文件列表
            /**
             * 添加数据集文件，按照不同的切分力度，将文本进行切分，数据存入数据库中
             */
            if(null != map.get("datafilelist")){
                dataServiceUtil.addDataFiles(datafilelist,map);
            }

            datasetInfoMapper.addDataSetByMap(map);

            return  JSON.toJSONString(new JsonModel(true,"新增数据集成功",Messageconstant.REQUEST_SUCCESSED_CODE));
        }catch (Exception e){
            log.error(e.getMessage());
            return  JSON.toJSONString(new JsonModel(false,"新增数据集失败",Messageconstant.REQUEST_FAILED_CODE));
        }
    }

    /**
     * 获取数据集列表
     * @param map
     * @return
     */
    @Override
    public String datasetList(Map<String, Object> map) {
        Page page = new Page();
        try {
            String createuserid = JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString());
            /**
             * 查询用户角色，如果用户是admin可以看到所有，如果用户是manager可以看到数据集归属于他并且他创建项目指定的pmanager创建的数据集，
             * 如果用户是pmanager，只能查看他自己创建的数据集，普通用户没有查询权限
             */
            String sysrole = userServiceUtil.getUserMaxSysRoleByUserId(createuserid);
            String businessrole = userServiceUtil.getUserMaxBusinessRoleByUserId(createuserid);

            map.put("dsowner",createuserid);

            map.put(Common.SYS_ROLE,sysrole);
            map.put(Common.PROJECT_ROLE,businessrole);

            if(null != map.get("pageNo") && null != map.get("pageSize")){
                page.setPageNow(Integer.parseInt(map.get("pageNo")+""));
                page.setPageSize(Integer.parseInt(map.get("pageSize")+""));
                map.put("queryStart",page.getQueryStart());
                map.put("pageSize",page.getPageSize());
            }

            List<Map<String,Object>> returnlist = datasetInfoMapper.getDatasetListByOwner(map);
            int count = datasetInfoMapper.getDatasetListCountByOwner(map);

            page.setTotal(count);
            page.setResultList(returnlist);

            return JSON.toJSONString(new JsonModel(true,"查询数据集列表成功",Messageconstant.REQUEST_SUCCESSED_CODE,page));
        }catch (Exception e){
            log.error(e.getMessage());
            return  JSON.toJSONString(new JsonModel(false,"查询数据集列表失败",Messageconstant.REQUEST_FAILED_CODE));
        }
    }

    /**
     * 编辑数据集信息，数据集中无数据时，可编辑所有信息；有数据时，只能编辑名称和备注信息
     * 修改数据集下的数据文件应该单独走删除和新增接口
     * @param map
     * @return
     */
    @Override
    public String editDataSet(Map<String, String> map) {
        Map<String, String> paramap = new HashMap<>(8);
        try {
            /**
             * 校验，当前人是否具有修改权限，查询数据集下是否存在数据文件，或者存在的数据文件状态是否是不可用
             */
            String userid = JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString());
            Map<String,Object> dataset = dataServiceUtil.getDataSetInfoByDsid(map.get("dsid"));
            if(! userid.equals(dataset.get("dsowner"))){
                return  new JsonModel(false,"当前用户没有修改数据集权限",Messageconstant.REQUEST_FAILED_CODE).toString();
            }

            List<Map<String,Object>> datasetinfo = dataServiceUtil.getDataInfoInUseListByDsid(map.get("dsid"));
            paramap.put("dsid",map.get("dsid"));

            if(null != datasetinfo && datasetinfo.size() > 0 ){
                if(! StringUtil.empty( map.get("dsname") )) paramap.put("dsname",map.get("dsname"));
                if(! StringUtil.empty( map.get("note") ))  paramap.put("note",map.get("note"));
                if(! StringUtil.empty( map.get("dataobjecttype") )) paramap.put("dataobjecttype",map.get("dataobjecttype"));
                if(! StringUtil.empty( map.get("scenariotype") ))  paramap.put("scenariotype",map.get("scenariotype"));
            }else{
                if(! StringUtil.empty( map.get("dsname") )) paramap.put("dsname",map.get("dsname"));
                if(! StringUtil.empty( map.get("note") ))  paramap.put("note",map.get("note"));
                if(! StringUtil.empty( map.get("dataobjecttype") )) paramap.put("dataobjecttype",map.get("dataobjecttype"));
                if(! StringUtil.empty( map.get("scenariotype") ))  paramap.put("scenariotype",map.get("scenariotype"));
                if(! StringUtil.empty( map.get("dsusingstate") )) paramap.put("dsusingstate",map.get("dsusingstate"));
                if(! StringUtil.empty( map.get("dsstate") ))  paramap.put("dsstate",map.get("dsstate"));
            }

            paramap.put("dsupdatetime",DateFormatUtil.DateFormat());
            datasetInfoMapper.updateByPrimaryKeySelective(paramap);

            return  JSON.toJSONString(new JsonModel(true,"修改数据集成功",Messageconstant.REQUEST_SUCCESSED_CODE));
        }catch (Exception e){
            log.error(e.getMessage());
            return  JSON.toJSONString(new JsonModel(false,"修改数据集失败",Messageconstant.REQUEST_FAILED_CODE));
        }

    }

    /**
     * 删除数据集及包含的数据（删除前提是数据没有被使用，即所有数据没有被使用）
     * @param map
     * @return
     */
    @Override
    @Transactional
    public String deleteDataSet(Map<String, String> map) {
        Map<String, String> paramap = new HashMap<>(8);
        try {
            /**
             * 校验，当前人是否具有修改权限，查询数据集下是否存在数据文件，或者存在的数据文件状态是否是不可用
             */
            String userid = JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString());

            String[] dsids = map.get("dsids").split(",");

            for(String dsid : dsids){
                Map<String,Object> dataset = dataServiceUtil.getDataSetInfoByDsid(dsid);
                if(! userid.equals(dataset.get("dsowner"))){
                    return new JsonModel(false,"当前用户没有修删除"+dataset.get("dsname")+"数据集权限",Messageconstant.REQUEST_FAILED_CODE).toString();
                }
                List<Map<String,Object>> datainfos = dataServiceUtil.getDataInfoInUseListByDsid(map.get("dsid"));
                if(null != datainfos && datainfos.size() > 0){
                    return  new JsonModel(false,"删除数据集失败，"+dataset.get("dsname")+"下存在正在使用的数据文件",Messageconstant.REQUEST_FAILED_CODE).toString();
                }else{
                    Map<String,Object> map1 = new HashMap<>(2);
                    map1.put("dsid",dsid);
                    map1.put("datastate","0");
                    map1.put("dsstate","0");
                    //更新数据文件、数据集为删除状态
                    dataInfoMapper.updateDeleteByDataSetId(map1);
                    datasetInfoMapper.updateDeleteByPrimaryKey(map1);
                }
            }
            return  JSON.toJSONString(new JsonModel(true,"删除数据集成功",Messageconstant.REQUEST_SUCCESSED_CODE));
        }catch (Exception e){
            log.error(e.getMessage());
            return  JSON.toJSONString(new JsonModel(false,"删除数据集失败",Messageconstant.REQUEST_FAILED_CODE));
        }

    }

    /**
     * 下载数据集中所有的数据
     * @param response
     * @param map
     * @return
     */
    @Override
    public String downloadDataFilesByDsid(HttpServletRequest request, HttpServletResponse response, Map<String, String> map) {
        /**
         * 根据数据集id-dsid查询所有数据文件，获取文件路径，找到文件打包下载
         */
        try {
            if(null != map.get("dsids") && map.get("dsids").contains(",")){//多数据集下载
                String[] dsids = map.get("dsids").split(",");
                dataServiceUtil.downloadMultiDataSet(dsids,response);
            }else if (null != map.get("dsids")){
                dataServiceUtil.downloadDataSet(map.get("dsids"),response);
            }
            return  JSON.toJSONString(new JsonModel(true,"下载数据集成功",Messageconstant.REQUEST_SUCCESSED_CODE));
        }catch (Exception e){
            log.error(e.getMessage());
            return  JSON.toJSONString(new JsonModel(false,"下载数据集失败",Messageconstant.REQUEST_FAILED_CODE));
        }
    }

    /**
     * 数据集层面的数据对比
     * @param map
     * @return
     */
    @Override
    public String dataSetCompare(Map<String, String> map) {
        return null;
    }

    /**
     * 数据检索
     * @param map
     * @return
     */
    @Override
    public String dataSetSearch(Map<String, Object> map) {
        Page page = new Page(1,10);
        try {
            map.put("dsowner",JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString()));

            if(null != map.get("pageNo") && null != map.get("pageSize")){
                page.setPageNow(Integer.parseInt(map.get("pageNo")+""));
                page.setPageSize(Integer.parseInt(map.get("pageSize")+""));
            }
            map.put("queryStart",page.getQueryStart());
            map.put("pageSize",page.getPageSize());

            String sysrole = userServiceUtil.getUserMaxSysRoleByUserId(JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString()));
            map.put("dsowner",JWTUtil.getUserId(SecurityUtils.getSubject().getPrincipals().toString()));
            map.put(Common.SYS_ROLE,sysrole);

            List<Map<String,Object>> returnlist = datasetInfoMapper.dataSetSearch(map);
            int count = datasetInfoMapper.dataSetSearchCount(map);

            page.setTotal(count);
            page.setResultList(returnlist);

            return JSON.toJSONString(new JsonModel(true,"查询数据集列表成功",Messageconstant.REQUEST_SUCCESSED_CODE,page));
        }catch (Exception e){
            log.error(e.getMessage());
            return  JSON.toJSONString(new JsonModel(false,"查询数据集列表失败",Messageconstant.REQUEST_FAILED_CODE));
        }
    }

    @Override
    public String getDataTextList(Map<String, Object> map) {
        /*查询数据源文本信息，区分创建人*/
        String res = null;
        try{
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);

            Page page = new Page();
            page.setPageNow(Integer.parseInt(map.get("pageNow")+""));
            page.setPageSize(Integer.parseInt(map.get("pageSize")+""));
            map.put("queryStart",page.getQueryStart());
            map.put("pageSize",page.getPageSize());
            if(StringUtil.empty(map.get("dsid")+"")){
                map.put("userid",userId);
            }

            List<Map<String,Object>> texts= dataInfoMapper.getDataTextList(map);
            page.setResultList(texts);
            int count = dataInfoMapper.getDataTextListCounts(map);
            page.setTotal(count);
            JsonModel jm = new JsonModel(true, ReturnCode.SUCESS_CODE_0000.getValue(),ReturnCode.SUCESS_CODE_0000.getKey(),page);
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

    @Override
    public String getDataSettList(Map<String, Object> map) {

        /*查询数据源文本信息，区分创建人*/
        String res = null;
        try{
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);
            String sysrole = userServiceUtil.getUserMaxSysRoleByUserId(userId);
            String businessrole = userServiceUtil.getUserMaxBusinessRoleByUserId(userId);

            map.put(Common.SYS_ROLE,sysrole);
            map.put(Common.PROJECT_ROLE,businessrole);

            Page page = new Page();
            if(null != map.get("pageNow") && null != map.get("pageSize")){
                page.setPageNow(Integer.parseInt(map.get("pageNow")+""));
                page.setPageSize(Integer.parseInt(map.get("pageSize")+""));
                map.put("queryStart",page.getQueryStart());
                map.put("pageSize",page.getPageSize());
            }
            map.put("userid",userId);
            List<Map<String,Object>> texts= datasetInfoMapper.getDataSettList(map);
            page.setResultList(texts);
            int count = datasetInfoMapper.getDataSettListCounts(map);
            page.setTotal(count);
            JsonModel jm = new JsonModel(true, ReturnCode.SUCESS_CODE_0000.getValue(),ReturnCode.SUCESS_CODE_0000.getKey(),page);
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
