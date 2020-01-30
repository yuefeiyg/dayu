package com.blcultra.service.core;


import com.alibaba.fastjson.JSONObject;
import com.blcultra.cons.DataConstant;
import com.blcultra.dao.DataContentMapper;
import com.blcultra.dao.DataInfoIndexMapper;
import com.blcultra.dao.DataInfoMapper;
import com.blcultra.dao.DatasetInfoMapper;
import com.blcultra.parser.data.DataParser;
import com.dayu.util.FileUtil;
import com.dayu.util.JsonFileUtil;
import com.dayu.util.StringUtil;
import com.dayu.util.ZipFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("dataServiceUtil")
public class DataServiceUtil {
    private Logger log= LoggerFactory.getLogger(this.getClass());

    @Value("${datasource.origin.filepath}")
    private String datasetfilepath;
    @Value("${tempfiles.path}")
    private String tempfilespath;//存放临时文件

    @Autowired
    private DataInfoMapper dataInfoMapper;
    @Autowired
    private DataContentMapper dataContentMapper;
    @Autowired
    private DatasetInfoMapper datasetInfoMapper;
    @Autowired
    private DataInfoIndexMapper dataInfoIndexMapper;
    @Autowired
    private AnalysisSymbolUtil analysisSymbolUtil;
    @Autowired
    private DataParser DataParser;

    /**
     * 新增数据源文件，将文件元信息保存，同时将文件进行处理保存处理后的数据
     * @param datafilelist
     * @param datasetmap
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void addDataFiles(String datafilelist, Map<String,Object> datasetmap) throws Exception{
        String[] filelist = datafilelist.split(",");
        List<Map<String,Object>> files = new ArrayList<>();
        List<Map<String,Object>> indexs = new ArrayList<>();

        for(String file : filelist){
            Map<String,Object> filem = new HashMap<>();
            filem.put(DataConstant.DATASET_ID,datasetmap.get(DataConstant.DATASET_ID));
            if(null != datasetmap.get(DataConstant.DATASET_SCENARIO_TYPE)){
                filem.put(DataConstant.DATASET_SCENARIO_TYPE,datasetmap.get(DataConstant.DATASET_SCENARIO_TYPE));
            }
            filem.put(DataConstant.DATASET_DATA_OBJECT_TYPE,datasetmap.get(DataConstant.DATASET_DATA_OBJECT_TYPE));

            filem.put(DataConstant.DATASET_CREATOR,datasetmap.get(DataConstant.DATASET_DS_OWNER));
            filem.put(DataConstant.DATASET_OWNER,datasetmap.get(DataConstant.DATASET_DS_OWNER));
            filem.put(DataConstant.DATASET_CREATETIME,datasetmap.get(DataConstant.DATASET_DS_CREATETIME));
            filem.put(DataConstant.DATASET_SEGMENTATION,datasetmap.get(DataConstant.DATASET_SEGMENTATION));

            String filepath = StringUtil.concat(datasetfilepath,datasetmap.get("dsowner")+"","/",file);
            /**
             * 解析数据，生成数据库数据
             */
            this.analysisFiles(file,filepath,datasetmap,files,indexs,filem);
        }

        dataInfoMapper.addDataInfoBatch(files);

        if (indexs.size() > 0 ){
            dataInfoIndexMapper.addDataInfoIndexBatch(indexs);
        }
    }


    private void analysisFiles(String file, String filepath,Map<String,Object> datasetmap,
                               List<Map<String,Object>> files,List<Map<String,Object>> indexs,Map<String,Object> filem) throws Exception{
        /**
         * 判断文件类型，如果是压缩包的话，首先解压文件夹，然后解析
         */
        String subffix = file.substring(file.lastIndexOf(".")+1,file.length());
        if(subffix.equals("zip")){
            FileUtil.checkFileOrDirExist(StringUtil.concat(datasetfilepath,datasetmap.get(DataConstant.DATASET_DS_OWNER)+"/","zip/"),"dir");
            String zipoutfiledir = StringUtil.concat(datasetfilepath,datasetmap.get(DataConstant.DATASET_DS_OWNER)+"/","zip/",StringUtil.getUUID()+"/");

            List<Map<String,Object>> filepartsbytext = ZipFileUtil.handleZipFile(zipoutfiledir,filepath);
            for(Map<String,Object> fileparts : filepartsbytext){
                String filetype = fileparts.get("filetype") +"";
                if("txt".equals(filetype)){
                    Map<String,Object> filetext = insertIntoContentAndDataInfo(filem, fileparts.get("filepath")+"",(List<List<String>>)fileparts.get("fileparts"),
                            datasetmap.get(DataConstant.DATASET_SEGMENTATION) + "",datasetmap.get(DataConstant.DATASET_NOTE)+"");
                    files.add(filetext);
                }else if("json".equals(filetype)){
                    /**
                     * 判断数据应用场景，不同的应用场景使用的数据格式不一样,调用不同的解析器
                     */
                    String scenariotype = fileparts.get(DataConstant.DATASET_SCENARIO_TYPE) + "";
                    if(DataConstant.DATASET_SCENARIO_QA.equals(scenariotype)){//做QA问答使用数据
                        files.addAll(DataParser.qaData(fileparts,filem, filepath,datasetmap.get(DataConstant.DATASET_NOTE)+""));
                    }else if(DataConstant.DATASET_SCENARIO_SEMANTIC.equals(scenariotype)){//新华社数据
                        files.addAll(DataParser.qaData(fileparts,filem, filepath,datasetmap.get(DataConstant.DATASET_NOTE)+""));
                    }else{
                        files.add(DataParser.sequenceLabelData(fileparts,filem, filepath,datasetmap.get(DataConstant.DATASET_NOTE)+""));
                    }

                }else if("jpg".equals(filetype) || "png".equals(filetype) || "jpeg".equals(filetype) ||
                        "JPG".equals(filetype) || "PNG".equals(filetype) || "JPEG".equals(filetype)){
                    /**
                     * 图片格式
                     */
                    Map<String,Object> filetext = insertIntoContentAndDataInfo(filem, fileparts.get("filepath")+"",null,
                            null,datasetmap.get(DataConstant.DATASET_NOTE)+"");
                    files.add(filetext);
                    /**
                     * 添加索引
                     */
                    indexs.add(indexMapPutValue(StringUtil.getUUID(),filetext.get("dataid")+"",null,filetext.get("dataname")+"",1));
                }

            }

            //删除原始zip文件
            new File(filepath).delete();
        }else if(subffix.equals("txt")) {//单个txt文件
            /**
             * 添加解析文件的方法，解析文件大小，获取文件路径，拆分文件存入数据库中,同时生成索引
             */
            List<List<String>> fileparts = FileUtil.readFileToListBySentence(filepath);
            Map<String,Object> filetext = insertIntoContentAndDataInfo(filem, filepath,fileparts,
                    datasetmap.get(DataConstant.DATASET_SEGMENTATION) + "",datasetmap.get(DataConstant.DATASET_NOTE)+"");
            files.add(filetext);
        }else if(subffix.equals("json")) {//单个json文件
            /**
             * 添加解析文件的方法，解析文件大小，获取文件路径，拆分文件存入数据库中,
             * 同时，添加对应的index（索引）解析
             */
            JSONObject jsonmap = JsonFileUtil.readJSONFileOfDataSource(filepath,"utf-8");
            String scenariotype = jsonmap.get(DataConstant.DATASET_SCENARIO_TYPE) + "";
            if(DataConstant.DATASET_SCENARIO_QA.equals(scenariotype)){//做QA问答使用数据
                files.addAll(DataParser.qaData(jsonmap,filem, filepath,datasetmap.get(DataConstant.DATASET_NOTE)+""));
            }else if(DataConstant.DATASET_SCENARIO_SEMANTIC.equals(scenariotype)){//新华社数据
                files.addAll(DataParser.qaData(jsonmap,filem, filepath,datasetmap.get(DataConstant.DATASET_NOTE)+""));
            }else{
                files.add(DataParser.sequenceLabelData(jsonmap,filem, filepath,datasetmap.get(DataConstant.DATASET_NOTE)+""));
            }

        }else if("jpg".equals(subffix) || "png".equals(subffix) || "jpeg".equals(subffix) ||
                "JPG".equals(subffix) || "PNG".equals(subffix) || "JPEG".equals(subffix)){
            /**
             * 图片格式
             */
            Map<String,Object> filetext = insertIntoContentAndDataInfo(filem, filepath,null,
                    null,datasetmap.get(DataConstant.DATASET_NOTE)+"");
            files.add(filetext);
            /**
             * 添加索引
             */
            indexs.add(indexMapPutValue(StringUtil.getUUID(),filetext.get("dataid")+"",null,filetext.get("dataname")+"",1));
        }else{
            throw new Exception("解析文件失败");
        }
    }

    public Map<String,Object> insertIntoContentAndDataInfoByJSON(String filename ,Map<String,Object> filem,String filepath,List<Map<String,Object>> fileparts,String note) throws Exception{
        Map<String,Object> filetext = new HashMap<>();
        filetext.putAll(filem);
        String dataid = StringUtil.getUUID();
        int wordcount = 0;
        if (fileparts.size() > 0) {
            wordcount = insertIntoContentByJSON(dataid, fileparts);
        }

        filetext.put("dataid", dataid);
        filetext.put("dataname", filename);
        filetext.put("size", wordcount);
        filetext.put("datastate", 1);
        filetext.put("datapath", filepath);
        filetext.put("datausingstate", 0);
        filetext.put("note", note);
        filetext.put("datatype", 0);
        return filetext;
    }

    /**
     * 将文本数据按照文本存储
     * @throws IOException
     */
    public int insertIntoContentByJSON(String dataid,List<Map<String,Object>> fileparts) throws Exception {
        List<Map<String,Object>> sentences = new ArrayList<>();
        List<Map<String,Object>> indexs = new ArrayList<>();
        int wordcount = 0;
        int pn = 1;
        int sn = 1;
        for(Map<String,Object> parts : fileparts){
            String str = parts.get("content")+"";
            String index = parts.get("index")+"";
            String contentid = StringUtil.getUUID();

            sentences.add(contentMapPutValue(
                    contentid,dataid,str,pn,sn,null,StringUtil.countChineseInString(str)));
            indexs.add(indexMapPutValue(StringUtil.getUUID(),dataid,contentid,index,sn));
            sn ++;
            pn ++;
            wordcount += StringUtil.countChineseInString(str);
        }
        /**
         * 添加内容
         */
        dataContentMapper.addDataContentBatch(sentences);
        /**
         * 添加索引
         */
        dataInfoIndexMapper.addDataInfoIndexBatch(indexs);
        return wordcount;
    }

    public Map<String,Object> indexMapPutValue(String dataindexid,String dataid,String contentid,String dataindexname,
                                               int sequence){
        Map<String,Object> map = new HashMap();
        map.put("dataindexid", dataindexid);
        map.put("dataid",dataid);
        map.put("contentid",contentid);
        map.put("dataindexname",dataindexname);
        map.put("sequence",sequence);
        return map;
    }

    public Map<String,Object> insertIntoContentAndDataInfo(Map<String,Object> filem,String filepath,List<List<String>> fileparts,String segmentation,String note) throws Exception{
        Map<String,Object> filetext = new HashMap<>();
        filetext.putAll(filem);
        String dataid = StringUtil.getUUID();
        if (fileparts.size() != 0) {
            insertIntoContent(dataid, segmentation, fileparts);
        }

        String filename = filepath.substring(filepath.lastIndexOf("/") + 1);
        filetext.put("dataid", dataid);
        filetext.put("dataname", filename);
        filetext.put("size", countChineseInText(fileparts));
        filetext.put("datastate", 1);
        filetext.put("datapath", filepath);
        filetext.put("datausingstate", 0);
        filetext.put("note", note);
        filetext.put("datatype", 0);
        return filetext;
    }

    /**
     * 将文本数据按照文本存储,在处理文本时，需要注意如果文本中含有标注符号，则需要解析保存
     * @throws IOException
     */
    public void insertIntoContent(String dataid,String segmentation,List<List<String>> fileparts) throws Exception {
        List<Map<String,Object>> sentences = new ArrayList<>();
        List<Map<String,Object>> indexs = new ArrayList<>();
        int pn = 1;
        int sn = 1;

        if(DataConstant.SEGMENTATION_TEXT.equals(segmentation)){//将文本数据按照文本存储
            StringBuilder str = new StringBuilder("");
            for(List<String> parts : fileparts){
                for(String sentence : parts){
                    str.append(sentence+"\n");
                }
                str.append("\n");
            }
            String contentid = StringUtil.getUUID();
            String newsentence = str.toString();
            sentences.add(contentMapPutValue(
                    contentid,dataid,analysisSymbolUtil.removeSymbol(newsentence),pn,sn,
                    analysisSymbolUtil.analysis(str.toString()),StringUtil.countChineseInString(str.toString())));
            String index = str.length() > DataConstant.DATA_INDEX_LENGTH ? str.substring(0,DataConstant.DATA_INDEX_LENGTH) : str.toString();
            indexs.add(indexMapPutValue(StringUtil.getUUID(),dataid,contentid,index,sn));
        }else if(DataConstant.SEGMENTATION_PARAGRAPH.equals(segmentation)){//将文本数据按照段落存储
            for(List<String> parts : fileparts){
                StringBuilder str = new StringBuilder("");
                for(String sentence : parts){
                    str.append(sentence + "\n");
                }
                String contentid = StringUtil.getUUID();
                String newsentence = str.toString();
                sentences.add(contentMapPutValue(
                        contentid,dataid,analysisSymbolUtil.removeSymbol(newsentence),pn,sn,
                        analysisSymbolUtil.analysis(str.toString()),StringUtil.countChineseInString(str.toString())));
                String index = str.length() > DataConstant.DATA_INDEX_LENGTH ? str.substring(0,DataConstant.DATA_INDEX_LENGTH) : str.toString();
                indexs.add(indexMapPutValue(StringUtil.getUUID(),dataid,contentid,index,sn));
                sn ++;
                pn ++;
            }
        }else if(DataConstant.SEGMENTATION_SENTENCE.equals(segmentation)){//将文本数据按照句子存储
            for(List<String> parts : fileparts){
                for(String sentence : parts){
                    String contentid = StringUtil.getUUID();
                    String newsentence = sentence;
                    sentences.add(contentMapPutValue(
                            contentid,dataid,analysisSymbolUtil.removeSymbol(newsentence),pn,sn,
                            analysisSymbolUtil.analysis(sentence),StringUtil.countChineseInString(sentence.toString())));
                    String index = sentence.length() > DataConstant.DATA_INDEX_LENGTH ? sentence.substring(0,DataConstant.DATA_INDEX_LENGTH) : sentence.toString();
                    indexs.add(indexMapPutValue(StringUtil.getUUID(),dataid,contentid,index,sn));
                    sn ++;
                }
                pn ++;
            }
        }

        /**
         * 添加内容
         */
        dataContentMapper.addDataContentBatch(sentences);
        /**
         * 添加索引
         */
        dataInfoIndexMapper.addDataInfoIndexBatch(indexs);

    }

    public Map<String,Object> contentMapPutValue(String contentid,String dataid,String content,int pn,
                                                 int sn,String anninfos,int size){
        Map<String,Object> map = new HashMap();
        map.put("contentid", contentid);
        map.put("dataid",dataid);
        map.put("content",content);
        map.put("pn",pn);
        map.put("sn",sn);
        map.put("anninfos",anninfos);
        map.put("size",size);
        return map;
    }

    /**
     * 计算文本中的中文字符数
     * @param content
     * @return
     * @throws Exception
     */
    public int countChineseInText(List<List<String>> content) throws Exception{
        int count = 0;
        if(null != content && content.size() > 0){
            for(List<String> parts : content){
                for(String sentence : parts){
                    count += StringUtil.countChineseInString(sentence);
                }
            }
        }

        return count;
    }

    /**
     * 根据数据集id查询数据集信息详情
     * @param dsid
     * @return
     */
    public Map<String,Object> getDataSetInfoByDsid(String dsid) throws Exception{
        return datasetInfoMapper.getDataSetInfoByDsid(dsid);
    }

    /**
     * 根据数据集id查询可用数据文件列表
     * @param dsid
     * @return
     */
    public List<Map<String,Object>> getDataInfoInUseListByDsid(String dsid) throws Exception{
        return dataInfoMapper.getDataInfoInUseListByDsid(dsid);
    }

    /**
     * 根据数据集id查询所有数据文件列表
     * @param dsid
     * @return
     */
    public List<Map<String,Object>> getDataInfoListByDsid(String dsid) throws Exception{
        return dataInfoMapper.getDataInfoListByDsid(dsid);
    }


    /**
     * 单数据集下载
     * 按照数据集进行文件夹命名
     * @param response
     * @throws Exception
     */
    public void downloadDataSet(String dsid, HttpServletResponse response) throws Exception{
        List<Map<String,String>> urlsmap = dataInfoMapper.selectDataUrlByDsid(dsid);

        String uuid = StringUtil.getUUID();//压缩包名，下载完成后进行删除

        FileUtil.checkDirAndEmptyDir(tempfilespath);//清空文件夹
        String tempfilepath = StringUtil.concat(tempfilespath,uuid +"/");
        FileUtil.checkFileOrDirExist(tempfilepath,"dir");
        List<File> file = getCopyFiles(urlsmap,tempfilepath);

        if(file.size() > 0)
            ZipFileUtil.ZipFiles(file,StringUtil.concat(tempfilepath,uuid,".zip"));

        String zipname = uuid+".zip";
        ZipFileUtil.downloadzipfile(tempfilepath,zipname, response);

    }


    /**
     * 选择多数据集进行下载
     * 按照数据集进行文件夹命名
     * @param dsids
     * @param response
     * @throws Exception
     */
    public void downloadMultiDataSet(String[] dsids, HttpServletResponse response) throws Exception{
        StringBuilder builder = new StringBuilder("");
        for(String id : dsids){
            builder.append("'"+id + "',");
        }
        Map<String,String> map = new HashMap<>(1);
        map.put("dsids",builder.toString().substring(0,builder.toString().length() -1 ));
        List<Map<String,String>> urlsmap = dataInfoMapper.selectDataUrlByDsids(map);

        String uuid = StringUtil.getUUID();//压缩包名，下载完成后进行删除
        FileUtil.checkDirAndEmptyDir(tempfilespath);//清空文件夹
        String tempfilepath = StringUtil.concat(tempfilespath,uuid +"/");
        FileUtil.checkFileOrDirExist(tempfilepath,"dir");
        List<File> file = getCopyFiles(urlsmap,tempfilepath);

        if(file.size() > 0)
            ZipFileUtil.ZipFiles(file,StringUtil.concat(tempfilepath,uuid,".zip"));

        String zipname = uuid+".zip";
        ZipFileUtil.downloadzipfile(tempfilepath,zipname, response);

    }

    /**
     * 下载多文件
     * @param dsids
     * @param response
     * @throws Exception
     */
    public void downloadMultiData(String[] dsids, HttpServletResponse response) throws Exception{
        StringBuilder builder = new StringBuilder("");
        for(String id : dsids){
            builder.append("'"+id + "',");
        }
        Map<String,String> map = new HashMap<>(1);
        map.put("dataids",builder.toString().substring(0,builder.toString().length() -1 ));
        List<Map<String,String>> urlsmap = dataInfoMapper.selectDataInfoByids(map);

        String uuid = StringUtil.getUUID();//压缩包名，下载完成后进行删除
        FileUtil.checkDirAndEmptyDir(tempfilespath);//清空文件夹
        String tempfilepath = StringUtil.concat(tempfilespath,uuid +"/");
        FileUtil.checkFileOrDirExist(tempfilepath,"dir");
        List<File> file = getCopyFiles(urlsmap,tempfilepath);

        if(file.size() > 0)
            ZipFileUtil.ZipFiles(file,StringUtil.concat(tempfilepath,uuid,".zip"));

        String zipname = uuid+".zip";
        ZipFileUtil.downloadzipfile(tempfilepath,zipname, response);

    }


    /**
     * 复制文件，进行打包
     * @param urlsmap
     * @throws Exception
     */
    public List<File> getCopyFiles(List<Map<String,String>> urlsmap,String tempfilepath) throws Exception{
        List<File> file = new ArrayList<>();

        for(Map<String,String> urlmap : urlsmap){
            File file1 = new File(urlmap.get("datapath"));
            if(file1.exists()){
                try{
                    file.add(FileUtil.FileCopy(file1,tempfilepath));
                }catch (Exception e){
                    log.error("读取文件失败，文件名为: "+file1.getName());
                }

            }
        }
        return file;
    }
    /**
     * 对多数据集的数据按照数据集id进行分组
     * @param datainfos
     * @return
     * @throws Exception
     */
    public List<List<Map<String,String>>> groupByDataInfoByDsid(List<Map<String,String>> datainfos) throws Exception{
        List<List<Map<String,String>>> result = new ArrayList<>();
        if(null != datainfos){
            String dsid = "";
            List<Map<String,String>> datainfomaps = null;

            for(Map<String,String> datainfo : datainfos){
                if(! dsid.equals(datainfo.get("dsid"))){
                    dsid = datainfo.get("dsid");
                    result.add(datainfomaps);
                    datainfomaps = new ArrayList<>();
                }
                datainfomaps.add(datainfo);
            }
            result.add(datainfomaps);
        }
        return result;
    }
}
