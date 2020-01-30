package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.Common;
import com.blcultra.cons.Messageconstant;
import com.blcultra.cons.UserRoleConstant;
import com.blcultra.dao.*;
import com.blcultra.model.CompareResult;
import com.blcultra.model.User;
import com.blcultra.service.FileComparisonService;
import com.blcultra.service.core.UserServiceUtil;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.JsonModel;
import com.blcultra.support.Page;
import com.blcultra.support.ReturnCode;
import com.blcultra.util.*;
import com.dayu.util.*;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Created by sgy05 on 2019/1/9.
 */
@Service("fileComparisonService")
public class FileComparisonServiceImpl implements FileComparisonService {

    private static final Logger log = LoggerFactory.getLogger(FileComparisonServiceImpl.class);

    @Value("${file.compare.path}")
    private String comparepath;

    @Value("${file.compate.result}")
    private String compareresult;

    @Value("${script.file.path}")
    private String scriptpath;

    @Value("${tempfiles.path}")
    private String tempfilespath;//存放临时文件

    @Autowired
    private DataInfoMapper dataInfoMapper;

    @Autowired
    private CompareResultMapper compareResultMapper;
    @Autowired
    private CompareResultDataSourcesMapper compareResultDataSourcesMapper;
    @Autowired
    private CompareResultDatacodeMapper compareResultDatacodeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserServiceUtil userServiceUtil;
    /**
     * 文件对比服务
     * @param ids  多个数据集id    或者   多个文件id
     * @param scriptfilename  脚本名称
     * @param  type   类型：是多个数据集id还是多个文件id
     * @return
     */
    @Transactional
    @Override
    public String fileComparison(String ids,String scriptfilename,String type) {


        String res = null;
        try {
            if (StringUtils.isEmpty(type) || StringUtils.isEmpty(scriptfilename) || StringUtils.isEmpty(ids)){
                JsonModel jm = new JsonModel(false,"参数缺失错误", Messageconstant.REQUEST_FAILED_CODE,null);
                return JSON.toJSONString(jm);
            }
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userid = JWTUtil.getUserId(token);
            User user = userMapper.selectByPrimaryKey(userid);

            //选择的是数据集列表
            if (type.trim().equals(Common.COMPARE_TYPE_DATASET)){
                String[] st = ids.split(",");
                List<String> list = Arrays.asList(st);
                long currentTime=System.currentTimeMillis();
                List<Map<String, Object>> dalist = new ArrayList<>();
                for (String dsid : list) {

                    List<Map<String, Object>> datainfos = dataInfoMapper.getDataInfoInUseListByDsid(dsid);


                    for (Map<String, Object> datainfo : datainfos) {
                        //过滤筛选标注后的结果文件
                        String datatype =String.valueOf(datainfo.get("datatype"));//0:原始文件   1：标注后的结果文件
                        if (datatype.trim().equals("1")){
                            String datapath = String.valueOf(datainfo.get("datapath"));
                            String dataname = String.valueOf(datainfo.get("dataname"));
                            String[] split = dataname.split("\\.");
                            String textcode = split[1];//原始文件编码

                            JSONObject jsonObject = JsonFileUtil.readFileToJson(new FileInputStream(new File(datapath)));
                            JSONArray baseelements = jsonObject.getJSONArray("baseelements");
                            String s = JSON.toJSONString(baseelements);
                            List<Map<String,Object>> elements = (List<Map<String, Object>>) JSONObject.parse(s);
                            log.info("elements",JSON.toJSONString(elements));
                            StringBuilder cont = new StringBuilder("");
                            for (Map<String, Object> element : elements) {
                                List<Map<String,Object>> items = (List<Map<String, Object>>) element.get("items");
                                String content = String.valueOf(element.get("content"));

                                String con = AnnotationLabelParser.parse1s(content, items);
                                log.info("替换后的content：",con);
                                cont.append(con+" \n");
                            }

                            String compareurl = comparepath+currentTime+"/";
                            FileUtil.createFile(compareurl);
                            String name = String.valueOf(datainfo.get("dataname"));
                            String sname = name.substring(0,name.lastIndexOf("."))+".txt";
                            String path = compareurl+sname;
                            dalist.add(datainfo);
                            TxtFileUtil.writeStringDataToTxt(String.valueOf(cont),path);
                        }
                    }

                }
                //===============================按原始文件编码进行分组======================================
                //=====================================begin======================================
                log.info("开始 按原始文件编码进行分组");
                Set<String> textcodeset = new HashSet<>();
                for (Map<String, Object> datainfo : dalist) {
                    String dataname = String.valueOf(datainfo.get("dataname"));
                    String[] split = dataname.split("\\.");
                    String textcode = split[1];//原始文件编码
                    textcodeset.add(textcode);
                }
                Iterator<String> it = textcodeset.iterator();
                Map<String,List<Map<String,Object>>> datamap = new HashMap<>();
                while (it.hasNext()){
                    String textcode = it.next();
                    List<Map<String,Object>> dlist = new ArrayList<>();
                    for (Map<String, Object> data : dalist) {
                        String dataname = String.valueOf(data.get("dataname"));
                        String[] split = dataname.split("\\.");
                        String tcode = split[1];//原始文件编码
                        if (tcode.equals(textcode)){
                            dlist.add(data);
                        }
                    }
                    datamap.put(textcode,dlist);
                }
                /**
                 * dataid, dsid, dataname, dataobjecttype, `size`, scenariotype, creator, owner, createtime,
                 datastate, datapath, segmentation, datausingstate, note
                 */
                long current=System.currentTimeMillis();
                String result = compareresult+current+"/";
                FileUtil.createFile(result);
                String resultpathparam = result+"list.txt";
                List<String> paramlist = new ArrayList<>();
                for (Map.Entry<String, List<Map<String,Object>>> entry : datamap.entrySet()) {
                    List<Map<String, Object>> value = entry.getValue();
                    String line = "";
                    for (Map<String, Object> map : value) {
                        String name = String.valueOf(map.get("dataname"));
                        String sname = name.substring(0,name.lastIndexOf("."))+".txt";
                        String compareurl = comparepath+currentTime+"/";
                        String path = compareurl+sname;
                        line+= path+" ";
                    }
                    paramlist.add(line);
                }
                log.info("开始 将文件写入list.txt中");
                TxtFileUtil.writeListDataToTxt(paramlist,resultpathparam);
                //=====================================end======================================

                List<String> list1 = FileComparisonUtil.fileComparison(resultpathparam, result, scriptpath+scriptfilename);

                //插入对比结果表
                CompareResult compareResult = new CompareResult();
                String uuid = StringUtil.getUUID();
                compareResult.setExecutetime(DateFormatUtil.DateFormat());
                compareResult.setExecutor(userid);
                compareResult.setName(user.getUsername()+current);
                compareResult.setResultid(uuid);
                compareResult.setPath(result+"ret.txt");
                int n = compareResultMapper.insertSelective(compareResult);
                log.info("compareResultMapper.insertSelective：",n);
                for (Map<String, Object> data : dalist) {
                    String dataid = String.valueOf(data.get("dataid"));
                    Map<String,Object> map = new HashMap<>();
                    map.put("dataid",dataid);
                    map.put("resultid",uuid);
                    int m = compareResultDataSourcesMapper.insertCompareResultDataSoucrce(map);
                    log.info("compareResultDataSourcesMapper.insertCompareResultDataSoucrce  result：",m);
                }
                for (String code : textcodeset) {
                    Map<String,Object> map = new HashMap<>();
                    map.put("resultid",uuid);
                    map.put("datacode",code);
                    String c = code.substring(code.lastIndexOf("_")+1);
                    map.put("datacode",c);
                    String compareDataCodeFilePath = result+c+".txt";
                    map.put("datacodefilepath",compareDataCodeFilePath);
                    int t = compareResultDatacodeMapper.insertDatacodes(map);
                    log.info("compareResultDatacodeMapper.insertDatacodes  result :",t);
                }
                Map<String,Object> resmap = new HashMap<>();
                resmap.put("resultid",uuid);
                resmap.put("resultdata",list1);
                JsonModel json = new JsonModel(true, "执行对比成功",Messageconstant.REQUEST_SUCCESSED_CODE,resmap);
                return JSON.toJSONString(json);

            }else if (type.trim().equals(Common.COMPARE_TYPE_TEXT)){
                //选择的文件列表
                String[] st = ids.split(",");
                List<String> list = Arrays.asList(st);
                long currentTime=System.currentTimeMillis();
                List<Map<String, Object>> datainfos = dataInfoMapper.selectDataInfoByDataIds(list);
                List<Map<String, Object>> dalist = new ArrayList<>();
                for (Map<String, Object> datainfo : datainfos) {
                    //过滤筛选标注后的结果文件
                    String datatype =String.valueOf(datainfo.get("datatype"));//0:原始文件   1：标注后的结果文件
                    if (datatype.trim().equals("1")){
                        String datapath = String.valueOf(datainfo.get("datapath"));
                        String dataname = String.valueOf(datainfo.get("dataname"));
                        String[] split = dataname.split("\\.");
                        String textcode = split[1];//原始文件编码

                        JSONObject jsonObject = JsonFileUtil.readFileToJson(new FileInputStream(new File(datapath)));
                        JSONArray baseelements = jsonObject.getJSONArray("baseelements");
                        String s = JSON.toJSONString(baseelements);
                        List<Map<String,Object>> elements = (List<Map<String, Object>>) JSONObject.parse(s);
                        log.info("elements",JSON.toJSONString(elements));
                        StringBuilder cont = new StringBuilder("");
                        for (Map<String, Object> element : elements) {
                            List<Map<String,Object>> items = (List<Map<String, Object>>) element.get("items");
                            String content = String.valueOf(element.get("content"));
                            log.info("element get content 的内容： ",content);
                            String con = AnnotationLabelParser.parse1s(content, items);
                            log.info("替换后的content：",con);
                            cont.append(con+" \n");
                        }
                        cont.append("\n");
                        String compareurl = comparepath+currentTime+File.separator;
                        FileUtil.createFile(compareurl);
                        String name = String.valueOf(datainfo.get("dataname"));
                        String sname = name.substring(0,name.lastIndexOf("."))+".txt";
                        String path = compareurl+sname;
                        dalist.add(datainfo);
                        TxtFileUtil.writeStringDataToTxt(String.valueOf(cont),path);
                    }
                }
                //ST_0722.text_130954.json
                Set<String> textcodeset = new HashSet<>();
                for (Map<String, Object> datainfo : dalist) {
                    String dataname = String.valueOf(datainfo.get("dataname"));
                    String[] split = dataname.split("\\.");
                    String textcode = split[1];//原始文件编码
                    textcodeset.add(textcode);
                }
                Iterator<String> it = textcodeset.iterator();
                Map<String,List<Map<String,Object>>> datamap = new HashMap<>();
                while (it.hasNext()){
                    String textcode = it.next();
                    List<Map<String,Object>> dlist = new ArrayList<>();
                    for (Map<String, Object> data : dalist) {
                        String dataname = String.valueOf(data.get("dataname"));
                        String[] split = dataname.split("\\.");
                        String tcode = split[1];//原始文件编码
                        if (tcode.equals(textcode)){
                            dlist.add(data);
                        }
                    }
                    datamap.put(textcode,dlist);
                }
                long current=System.currentTimeMillis();
                String result = compareresult+current+File.separator;
                FileUtil.createFile(result);
                String resultpathparam = result+"list.txt";
                List<String> paramlist = new ArrayList<>();
                for (Map.Entry<String, List<Map<String,Object>>> entry : datamap.entrySet()) {
                    List<Map<String, Object>> value = entry.getValue();
                    String line = "";
                    for (Map<String, Object> map : value) {
                        String name = String.valueOf(map.get("dataname"));
                        String sname = name.substring(0,name.lastIndexOf("."))+".txt";
                        String compareurl = comparepath+currentTime+File.separator;
                        String path = compareurl+sname;
                        line+= path+" ";
                    }
                    paramlist.add(line);
                }

                TxtFileUtil.writeListDataToTxt(paramlist,resultpathparam);
                //=====================================end======================================
                List<String> list1 = FileComparisonUtil.fileComparison(resultpathparam, result, scriptpath+scriptfilename);

                //插入对比结果表
                CompareResult compareResult = new CompareResult();
                String uuid = StringUtil.getUUID();
                compareResult.setExecutetime(DateFormatUtil.DateFormat());
                compareResult.setExecutor(userid);
                compareResult.setName(user.getUsername()+current);
                compareResult.setResultid(uuid);
                compareResult.setPath(result+"ret.txt");
                int n = compareResultMapper.insertSelective(compareResult);
                log.info("compareResultMapper.insertSelective：",n);
                List<Map<String,Object>> crdslist = new ArrayList<>();
                for (Map<String, Object> data : dalist) {
                    String dataid = String.valueOf(data.get("dataid"));
                    Map<String,Object> map = new HashMap<>();
                    map.put("dataid",dataid);
                    map.put("resultid",uuid);
                    int m = compareResultDataSourcesMapper.insertCompareResultDataSoucrce(map);
                    log.info("compareResultDataSourcesMapper.insertCompareResultDataSoucrce  result：",m);
                }
                for (String code : textcodeset) {
                    Map<String,Object> map = new HashMap<>();
                    map.put("resultid",uuid);
                    String c = code.substring(code.lastIndexOf("_")+1);
                    map.put("datacode",c);
                    String compareDataCodeFilePath = result+c+".txt";
                    map.put("datacodefilepath",compareDataCodeFilePath);
                    int t = compareResultDatacodeMapper.insertDatacodes(map);
                    log.info("compareResultDatacodeMapper.insertDatacodes  result :",t);
                }

                List<String> list2 = TxtFileUtil.readTxt2listCharset(result + "ret.txt", "UTF-8");
                Map<String,Object> resmap = new HashMap<>();
                resmap.put("resultid",uuid);
                resmap.put("resultdata",list2);
                JsonModel json = new JsonModel(true, "执行对比成功",Messageconstant.REQUEST_SUCCESSED_CODE,resmap);
                return JSON.toJSONString(json);

            }
            JsonModel json = new JsonModel(false, "选择对比数据类型有误",Messageconstant.REQUEST_FAILED_CODE,null);
            return JSON.toJSONString(json);

        } catch (Exception e) {

            e.printStackTrace();
        }
        JsonModel json = new JsonModel(false, "执行对比失败",Messageconstant.REQUEST_FAILED_CODE,null);
        return JSON.toJSONString(json);
    }

    /**
     * 查询对比结果信息
     * @param resultid
     * @return
     */
    @Override
    public String compareResultView(String resultid) {
        String res = null;
        try {
            CompareResult compareResult = compareResultMapper.selectByPrimaryKey(resultid);
            String path = compareResult.getPath();
            List<String> list = TxtFileUtil.readTxt2listCharset(path, "UTF-8");
            JsonModel jm = new JsonModel(true, "查询对比结果信息成功","200",list);
            return JSON.toJSONString(jm);

        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonModel jm = new JsonModel(false, "查询对比结果失败","400",null);
        return JSON.toJSONString(jm);
    }

    /**
     *查询同源文件对比结果信息
     * @param resultid
     * @param datacode
     * @return
     */
    @Override
    public String compareDataCodeFileResultView(String resultid, String datacode) {

        try {
            Map<String,Object> param = new HashMap<>();
            param.put("resultid",resultid);
            param.put("datacode",datacode);
            Map<String, Object> datacodefile = compareResultDatacodeMapper.getDatacodeFilePath(param);
            String path = String.valueOf(datacodefile.get("datacodefilepath"));

            List<String> list = TxtFileUtil.readTxt2listCharset(path, "UTF-8");
            JsonModel jm = new JsonModel(true, "查询同源文件对比结果信息成功",Messageconstant.REQUEST_SUCCESSED_CODE,list);
            return JSON.toJSONString(jm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonModel jm = new JsonModel(false, "查询同源文件对比结果失败",Messageconstant.REQUEST_FAILED_CODE,null);
        return JSON.toJSONString(jm);
    }

    /**
     * 获取当前用户对比结果列表信息
     * @param pageNow
     * @param pageSize
     * @return
     */
    @Override
    public String getCompareResults( Map<String,Object> map,int pageNow, int pageSize) {
        String res = null;
        try {
            Page page = new Page();
            page.setPageSize(pageSize);
            page.setPageNow(pageNow);
            map.put("queryStart",page.getQueryStart());
            map.put("pageSize",page.getPageSize());
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userid = JWTUtil.getUserId(token);

            String sysrole = userServiceUtil.getUserMaxSysRoleByUserId(userid);
            String businessRole = userServiceUtil.getUserMaxBusinessRoleByUserId(userid);
            if( UserRoleConstant.SYS_USER.equals(sysrole) && !UserRoleConstant.PROJECT_MANAGER.equals(businessRole)){
                JsonModel jm = new JsonModel(false, "没有查询权限","400",null);
            }
            map.put("sysrole",sysrole);
            map.put("role",businessRole);

            map.put("executor",userid);
            List<Map<String, Object>> results = compareResultMapper.getResultInfosByExcutor(map);
            int count = compareResultMapper.countResults(map);
            page.setTotal(count);
            page.setResultList(results);

            JsonModel jm = new JsonModel(true, "查询对比结果列表成功","200",page);
            res = JSON.toJSONString(jm);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonModel jm = new JsonModel(false, "查询对比结果列表失败","400",null);
        res = JSON.toJSONString(jm);
        return res;
    }

    /**
     * 下载对比结果文件
     * @param resultids
     * @return
     */
    @Override
    public String downloadCompareResults(HttpServletRequest request, HttpServletResponse response, String resultids) {


        try {
            String[] split = resultids.split(",");
            List<String> list = Arrays.asList(split);
            List<Map<String, Object>> results = compareResultMapper.batchSelectCompareResultsByResultids(list);

            long currentTime=System.currentTimeMillis();
            FileUtil.checkDirAndEmptyDir(tempfilespath);//清空文件夹
            String tempfilepath = StringUtil.concat(tempfilespath,currentTime +File.separator);
            FileUtil.checkFileOrDirExist(tempfilepath,"dir");
            List<File> file = getCopyFiles(results,tempfilepath);
            if(file.size() > 0)
                ZipFileUtil.ZipFiles(file,StringUtil.concat(tempfilepath,currentTime+"",".zip"));

            String zipname = currentTime+".zip";
            boolean flag = ZipFileUtil.downloadzipfile(tempfilepath,zipname, response);
            if(flag){
                return  JSON.toJSONString(new JsonModel(true,"下载对比结果数据成功",Messageconstant.REQUEST_SUCCESSED_CODE));
            }else{
                return  JSON.toJSONString(new JsonModel(false,"下载对比结果数据失败",Messageconstant.REQUEST_FAILED_CODE));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return  JSON.toJSONString(new JsonModel(false,"下载对比结果数据异常",Messageconstant.REQUEST_FAILED_CODE));

    }

    public List<File> getCopyFiles(List<Map<String,Object>> urlsmap,String tempfilepath) throws Exception{
        List<File> file = new ArrayList<>();

        for(Map<String,Object> urlmap : urlsmap){
            File file1 = new File(String.valueOf(urlmap.get("path")));
            if(file1.exists()){
                try{
                    long currentTime=System.currentTimeMillis();
                    long current = currentTime +10;
                    file.add(FileUtil.FileCopy(file1,current,tempfilepath));
                }catch (Exception e){
                    log.error("读取文件失败，文件名为: "+file1.getName());
                }

            }
        }
        return file;
    }

    /**
     * 获取脚本列表
     * @return
     */
    @Override
    public String scriptList() {
        String res = null;
        try {
            File[] files = null;
            File dir = new File(scriptpath);
            if (dir != null && dir.exists() && dir.isDirectory()) {
                files = dir.listFiles();
            }
            List<String> list = new ArrayList<>();
            if(null != files){
                for (File file : files) {
                    String fileName = file.getName();
                    list.add(fileName);
                }
            }
            JsonModel jm = new JsonModel(true, ReturnCode.SUCESS_CODE_0000.getValue(),ReturnCode.SUCESS_CODE_0000.getKey(),list);
            res = JSON.toJSONString(jm);
            return res;
        } catch (Exception e) {
            log.error("=========FileComparisonServiceImpl  scriptList  occur exception :"+e);
            JsonModel jm = new JsonModel(false, ReturnCode.ERROR_CODE_1111.getValue(),ReturnCode.ERROR_CODE_1111.getKey(),null);
            res = JSON.toJSONString(jm);
            e.printStackTrace();
        }finally {
            log.info("scriptList Response : "+res);
        }
        return res;
    }
}
