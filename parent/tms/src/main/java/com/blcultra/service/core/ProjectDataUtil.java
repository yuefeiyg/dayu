package com.blcultra.service.core;

import com.blcultra.dao.TaskAnnotationDataMapper;
import com.blcultra.util.AnnotationLabelParser;
import com.dayu.util.FileUtil;
import com.dayu.util.StringUtil;
import com.dayu.util.TxtFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("projectDataUtil")
public class ProjectDataUtil {

    @Value("${project.data.filepath}")
    private String DATAT = "";

    @Autowired
    TaskAnnotationDataMapper taskAnnotationDataMapper;

    public  String makeDataFilesByProject(List<Map<String,Object>> contents ) throws Exception{
        String uuid = StringUtil.getUUID();
        String dir = DATAT + uuid ;
        String projectfilepath = "";

        FileUtil.checkFileOrDirExist(dir.toString(),"dir");

        List<Map<String,Object>> returList = new ArrayList<>();
        String projectid = "";
        String projectname = "";

        List<Map<String,Object>> projecttext = new ArrayList<>();

        for(Map<String,Object> content : contents){
            if(projectid.equals("")){//初始化
                projectid = content.get("projectid") +"";
                projectname = content.get("projectname") +"_" + content.get("pmanagername") ;//按照项目作区分
                projecttext.add(content);
            }else if(! projectid.equals(content.get("projectid") +"")){//换项目了
                //按照项目创建文件夹，将项目下的文件输出到该文件夹下
                projectfilepath = dir +"/"+ projectname+ "/";
                FileUtil.checkFileOrDirExist(projectfilepath,"dir");
                makeDataFilesByTask(projecttext,projectfilepath);

                projectid = content.get("projectid") +"";
                projectname = content.get("projectname") +"_" + content.get("pmanagername");
                projecttext = new ArrayList<>();
                projecttext.add(content);
            }else if(projectid.equals(content.get("projectid") +"")){
                projecttext.add(content);
            }
        }
        projectfilepath = dir +"/"+ projectname+ "/";
        FileUtil.checkFileOrDirExist(projectfilepath,"dir");
        makeDataFilesByTask(projecttext,projectfilepath);
        return dir;
    }


    public  void makeDataFilesByTask(List<Map<String,Object>> projecttext ,String filepath) throws Exception{

        List<Map<String,Object>> tasktext = new ArrayList<>();
        String taskid ="";
        String performername = "";

        for(Map<String,Object> content : projecttext){
            if(taskid.equals("")){//初始化
                taskid = content.get("taskid") +"";
                performername = content.get("performername") +"";
                tasktext.add(content);
            }else if(! taskid.equals(content.get("taskid") +"")){//换任务了
                //遍历任务下的文本信息
                this.makeDataFilesByDataid(tasktext,filepath,performername);

                taskid = content.get("taskid") +"";
                performername = content.get("performername") +"";
                tasktext = new ArrayList<>();
                tasktext.add(content);
            }else if(taskid.equals(content.get("taskid") +"")){
                tasktext.add(content);
            }

        }
        this.makeDataFilesByDataid(tasktext,filepath,performername);

    }

    public void makeDataFilesByDataid(List<Map<String,Object>> tasktext ,
                                                                 String filepath,String performername) throws Exception{

        List<Map<String,Object>> datatext = new ArrayList<>();
        String dataid ="";
        String dataname ="";
        String finishtime ="";
        String finishdeadline ="";
        String callbacktimes ="";

        for(Map<String,Object> content : tasktext){
            if(dataid.equals("")){//初始化
                dataid = content.get("dataid") +"";
                dataname = content.get("dataname") +"";
                finishtime = content.get("finishtime") +"";
                finishdeadline = content.get("finishdeadline") +"";
                callbacktimes = content.get("callbacktimes") +"";

                datatext.add(content);
            }else if(! dataid.equals(content.get("dataid") +"")){//生成文本
                //按照文本进行文件夹分类
                String datafilepath = makeNewFilePath(filepath,dataname,performername,
                        StringUtil.empty(finishdeadline) ? finishtime : finishdeadline,callbacktimes);

                List<String> datalist = this.makeDataFile(datatext);

                TxtFileUtil.writeListDataToTxt(datalist,datafilepath);

                dataid = content.get("dataid") +"";
                dataname = content.get("dataname") +"";
                finishtime = content.get("finishtime") +"";
                finishdeadline = content.get("finishdeadline") +"";
                datatext = new ArrayList<>();
                datatext.add(content);
            }else if(dataid.equals(content.get("dataid") +"")){
                datatext.add(content);
            }

        }
        if(! "".equals(dataname)){
            String datafilepath = makeNewFilePath(filepath,dataname,performername,
                    StringUtil.empty(finishdeadline) ? finishtime : finishdeadline,callbacktimes);


            List<String> datalist = this.makeDataFile(datatext);

            TxtFileUtil.writeListDataToTxt(datalist,datafilepath);
        }

    }

    public  String makeNewFilePath(String filepath,String dataname,String performername,String data,String callbacktimes) throws Exception{
       String originfiklename = dataname;
        StringBuilder datafilepath = new StringBuilder(filepath);
        if(dataname.contains(".txt")){
            String str1 = StringUtil.leftsubstringmatch(dataname,".txt");
            datafilepath.append(str1.substring(str1.lastIndexOf("_") + 1));
            datafilepath.append( "/");
        }else if(dataname.contains(".json")){
            String str1 = StringUtil.leftsubstringmatch(dataname,".json");
            datafilepath.append(str1.substring(str1.lastIndexOf("_") + 1));
            datafilepath.append( "/");
        }else{
            datafilepath.append(dataname);
            datafilepath.append("/");
        }
        FileUtil.checkFileOrDirExist(datafilepath.toString(),"dir");
        datafilepath.append(data);
        datafilepath.append("_");
        if(null== callbacktimes ||  "0".equals(callbacktimes)){
            datafilepath.append(performername);
            datafilepath.append("_");
        }
        if(dataname.contains(".")){
            datafilepath.append(dataname.substring(0,dataname.lastIndexOf(".")));
        }else{
            datafilepath.append(dataname);
        }
        if(null!= callbacktimes && ! "0".equals(callbacktimes)){
            datafilepath.append("("+callbacktimes+")");
        }
        datafilepath.append(".txt");

        return datafilepath.toString();

    }

    public List<String>  makeDataFile(List<Map<String,Object>> datatext) throws Exception{
        List<String> sentences = new ArrayList<>();

        String taskid = datatext.get(0).get("taskid")+"";
        String dataid = datatext.get(0).get("dataid")+"";
        Map<String,Object> param = new HashMap<>(2);
        param.put("taskid",taskid);
        param.put("sourceid",dataid);
        List<Map<String,Object>> annodatas = taskAnnotationDataMapper.selectAnnotationDataByTaskid(param);

        int pn = 0;
        for(Map<String,Object> sentencemap : datatext){
            int pnow = Integer.parseInt(sentencemap.get("pn")+"");
            if(pn != 0 && pn != pnow){
                sentences.add("\n");
            }
            List<Map<String,Object>> items = new ArrayList<>();
            annodatas.forEach(annodata ->{
                if(sentencemap.get("objectdataid").equals(annodata.get("objectdataid"))){
                    items.add(annodata);
                }

            });
            if(items.size() > 0){
                sentences.add(new AnnotationLabelParser().parse1(sentencemap.get("content")+"",items));
            }else{
                sentences.add(sentencemap.get("content")+"");
            }

            pn = pnow;
        }

        return  sentences;

    }

    /**
     * 生成项目下成员标注字数统计文件
     * @param absoluteFilePath
     * @param content
     * @return
     * @throws Exception
     */
    public  void makeWordStatisticOfMembersFile(String absoluteFilePath,String content) throws Exception{
        TxtFileUtil.writeStringDataToTxt(content,absoluteFilePath);
    }

    public String getGeneralFilePath() throws Exception{
        return DATAT;
    }
}
