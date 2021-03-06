package com.blcultra.service.impl.command;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.blcultra.cons.Common;
import com.blcultra.dao.*;
import com.blcultra.support.JsonModel;
import com.blcultra.support.ReturnCode;
import com.dayu.util.SortUtil;
import com.dayu.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务角色为pmanager的用户统计服务
 * Created by sgy
 */
@Service(value = "pManagerStatisticService")
public class PManagerStatisticService {

    @Autowired
    private TaskInfoMapper taskInfoMapper;
    @Autowired
    private taskResFileInfoMapper trfim;
    @Autowired
    private TaskAnnotationDataMapper taskAnnotationDataMapper;
    @Autowired
    private DataInfoMapper dataInfoMapper;
    @Autowired
    private ProjectUserMapper projectUserMapper;
    /**
     *
     * @param userid
     * @param dimension   维度：用户、项目
     * @param begintime   统计时间：开始时间
     * @param endtime    统计时间：结束时间
     * @param pus    projectuser信息列表：projectid，member，prolekey
     * @return
     */
    public String pManagerStatistic(String userid,String dimension,String begintime,String endtime,List<Map<String, Object>> pus){
        String res = null;
        try {
            Map<String,Object> resmap = new HashMap<>();

            if (dimension.equals(Common.STATISTIC_DIMENSION_USER)){
                //用户维度
                List<String> proids = new ArrayList<>();
                for (Map<String,Object> pu : pus){
                    String projectid = String.valueOf(pu.get("projectid"));
                    proids.add(projectid);
                }
                Map<String,Object> params = new HashMap<>(4);
                params.put("starttime",begintime);
                params.put("endtime",endtime);
                params.put("list",proids);
                List<Map<String, Object>> tasklist = taskInfoMapper.pManagerStatisticTaskSearch(params);

                List<Map<String,Object>> tasktotaldatas = new ArrayList<>();//任务总数据量，不包含报废的任务
                List<Map<String,Object>> taskannotationdatas = new ArrayList<>();//标注总数据量
                List<Map<String,Object>> passdatas = new ArrayList<>();//通过数据量
                List<Map<String,Object>> returndatas = new ArrayList<>();//打回数据量
                List<Map<String,Object>> invalidatedatas = new ArrayList<>();//报废数据量

                for (Map<String,Object> taskinfo:tasklist){
                    String taskid = String.valueOf(taskinfo.get("taskid"));
                    String deletetype =String.valueOf(taskinfo.get("deletetype"));//删除类型：是报废，还是直接删除
                    int callbacktimes = Integer.parseInt(taskinfo.get("callbacktimes")+"");//打回次数
                    String tasktype = String.valueOf(taskinfo.get("tasktype"));//任务类型：是审核任务还是普通标注任务

                    //统计任务总数量，不包括报废的数据量，所以需要排除报废的数据量
                    if (! deletetype.equals(Common.TASK_DELETE_TYPE_INVALIDATE)){
                        tasktotaldatas.add(taskinfo);//将非报废任务添加至任务总数据列表中
                    }
                    if (callbacktimes == 0 ){
                        //不包括统计打回重标的任务涉及的数据量
                        taskannotationdatas.add(taskinfo);
                    }
                    List<Map<String, Object>> resdatas = trfim.getFinalDatasByTaskId(taskid);//当前任务对应的结果文件信息
                    if (resdatas != null && resdatas.size()>0){
                        passdatas.add(taskinfo);
                    }
                    if (callbacktimes == 1){
                        returndatas.add(taskinfo);
                    }
                    if (deletetype.equals(Common.TASK_DELETE_TYPE_INVALIDATE)){
                        invalidatedatas.add(taskinfo);
                    }
                }
                List<Map<String ,Object>>  totalresmap = taskDatasHandle(tasktotaldatas);
                List<Map<String ,Object>>  annotationresmap = taskDatasHandle(taskannotationdatas);
                List<Map<String ,Object>>  passresmap = taskDatasHandle(passdatas);
                List<Map<String ,Object>>  returnresmap = taskDatasHandle(returndatas);
                List<Map<String ,Object>>  invalidateresmap = taskDatasHandle(invalidatedatas);

                List<Map<String, Object>> returndata = taskReturnAndInvalidDatasHandle(returndatas);
                List<Map<String, Object>> invaliddatas = taskReturnAndInvalidDatasHandle(invalidatedatas);


                double total = 0;
                if (totalresmap!=null && totalresmap.size()>0){
                    for (Map<String ,Object> map:totalresmap){
                        int wordcount =Integer.parseInt(map.get("wordcount")+"");
                        total += wordcount;
                    }
                }
                double annotationcount = 0;
                if (annotationresmap != null && annotationresmap.size()>0){
                    for (Map<String ,Object> map:annotationresmap){
                        int wordcount =Integer.parseInt(map.get("wordcount")+"");
                        annotationcount += wordcount;
                    }
                }
                double passcount = 0;
                if (passresmap!=null && passresmap.size()>0 ){
                    for (Map<String ,Object> map:passresmap){
                        int wordcount =Integer.parseInt(map.get("wordcount")+"");
                        passcount += wordcount;
                    }
                }
                //完成度（标注总量/任务总量），通过率（通过数量/标注总量）
                DecimalFormat df = new DecimalFormat("0.00");
                String complete = 0+"";
                String pass ="";
                if (total != 0){
                    double completerate = annotationcount/total;
                    complete = df.format(completerate);
                }else {
                    double completerate =0;
                }
                if (annotationcount != 0){
                    double passrate = passcount/annotationcount;
                    pass = df.format(passrate);
                }else {
                    double passrate = 0;
                    pass = df.format(passrate);
                }

                resmap.put("type","member");

                List<Map<String,Object>> overall = new ArrayList<>();
                Map<String,Object> overallmap = new HashMap<>();
                overallmap.put("completion",complete);
                overallmap.put("passrate",pass);
                overallmap.put("total",totalresmap==null?new ArrayList():totalresmap);
                overallmap.put("annotation",annotationresmap==null?new ArrayList():annotationresmap);
                overallmap.put("pass",passresmap==null?new ArrayList():passresmap);
                overallmap.put("return",returnresmap==null?new ArrayList():returnresmap);
                overallmap.put("invalidate",invalidateresmap==null?new ArrayList():invalidateresmap);
                overallmap.put("returndata",returndata==null?new ArrayList():returndata);
                overallmap.put("invalidatedata",invaliddatas==null?new ArrayList():invaliddatas);
                overall.add(overallmap);
                SortUtil.SortByCompletion(overall,"completion");//按完成率排序：降序
                resmap.put("overall",overall);
                //计算detail详细数据
                List<Map<String, Object>> users = projectUserMapper.getUserInfosByProjectids(proids);
                Map<String, Object> detailmap = userDimensionDetailHandle(begintime, endtime, users, proids);
                resmap.put("detail",detailmap);
                JsonModel jm = new JsonModel(true, "查询统计成功", ReturnCode.SUCESS_CODE_0000.getKey(),resmap);
                res = JSON.toJSONString(jm, SerializerFeature.DisableCircularReferenceDetect);
                return res;

            }else if (dimension.equals(Common.STATISTIC_DIMENSION_PROJECT)){
                //处理pmanager角色对应项目维度下的总览数据量统计
                List<Map<String, Object>> overalllist = pmanagerProjectOverall(begintime, endtime, pus);
                List<Map<String, Object>> detaillist = pmanagerProjectDetail(begintime, endtime, pus);

                Map<String,Object> resultmap = new HashMap<>();
                resultmap.put("type","project");
                resultmap.put("overall",overalllist);
                resultmap.put("detail",detaillist);
                JsonModel jm = new JsonModel(true, "查询统计成功", ReturnCode.SUCESS_CODE_0000.getKey(),resultmap);
                res = JSON.toJSONString(jm, SerializerFeature.DisableCircularReferenceDetect);
                return res;

            }
            JsonModel jm = new JsonModel(false, "统计维度选择有误", "400",null);
            res = JSON.toJSONString(jm);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonModel jm = new JsonModel(false, "查询统计失败", ReturnCode.SUCESS_CODE_0000.getKey(),null);
        res = JSON.toJSONString(jm);
        return res;
    }
    //pmanager角色项目维度detail计算统计数据逻辑
    private List<Map<String,Object>> pmanagerProjectDetail(String begintime , String endtime , List<Map<String, Object>> pus) throws Exception {

        List<Map<String,Object>> projectlist = new ArrayList<>();
        for (Map<String,Object> project : pus){
            Map<String,Object> pmap = new HashMap<>();

            String projectid = String.valueOf(project.get("projectid"));
            String projectname = String.valueOf(project.get("projectname"));
            //查询该项目下对应所有用户
            List<Map<String, Object>> projectusers = projectUserMapper.getUserInfosByProjectid(projectid);
            Map<String,Object> datamap = new HashMap<>();

            List<Map<String,Object>> dalist = new ArrayList<>();//detail中data中对应项目下的用户数据列表
            for (Map<String,Object> user:projectusers){
                String userid = String.valueOf(user.get("userid"));
                String username = String.valueOf(user.get("username"));

                Map<String,Object> params = new HashMap<>(4);
                params.put("projectid",projectid);
                params.put("performerid",userid);
                params.put("role","user");
                params.put("starttime",begintime);
                params.put("endtime",endtime);
                //每个项目对应的任务数据
                List<Map<String, Object>> tasklist = taskInfoMapper.statisticSearchTask(params);
                if (tasklist != null){
                    List<Map<String,Object>> tasktotaldatas = new ArrayList<>();//任务总数据量，不包含报废的任务
                    List<Map<String,Object>> taskannotationdatas = new ArrayList<>();//标注总数据量
                    List<Map<String,Object>> passdatas = new ArrayList<>();//通过数据量
                    List<Map<String,Object>> returndatas = new ArrayList<>();//打回数据量
                    List<Map<String,Object>> invalidatedatas = new ArrayList<>();//报废数据量
                    for (Map<String,Object> taskinfo :tasklist){
                        String taskid = String.valueOf(taskinfo.get("taskid"));
                        String deletetype =String.valueOf(taskinfo.get("deletetype"));//删除类型：是报废，还是直接删除
                        int callbacktimes = Integer.parseInt(taskinfo.get("callbacktimes")+"");//打回次数
                        String tasktype = String.valueOf(taskinfo.get("tasktype"));//任务类型：是审核任务还是普通标注任务

                        //统计任务总数量，不包括报废的数据量，所以需要排除报废的数据量
                        if (! deletetype.equals(Common.TASK_DELETE_TYPE_INVALIDATE)){
                            tasktotaldatas.add(taskinfo);//将非报废任务添加至任务总数据列表中
                        }
                        if (callbacktimes == 0 ){
                            //不包括统计打回重标的任务涉及的数据量
                            taskannotationdatas.add(taskinfo);
                        }
                        List<Map<String, Object>> resdatas = trfim.getFinalDatasByTaskId(taskid);//当前任务对应的结果文件信息
                        if (resdatas != null && resdatas.size()>0){
                            passdatas.add(taskinfo);
                        }
                        if (callbacktimes == 1){
                            returndatas.add(taskinfo);
                        }
                        if (deletetype.equals(Common.TASK_DELETE_TYPE_INVALIDATE)){
                            invalidatedatas.add(taskinfo);
                        }
                    }
                    List<Map<String ,Object>>  totalresmap = taskDatasHandle(tasktotaldatas);
                    List<Map<String ,Object>>  annotationresmap = taskDatasHandle(taskannotationdatas);
                    List<Map<String ,Object>>  passresmap = taskDatasHandle(passdatas);
                    List<Map<String ,Object>>  returnresmap = taskDatasHandle(returndatas);
                    List<Map<String ,Object>>  invalidateresmap = taskDatasHandle(invalidatedatas);

                    List<Map<String, Object>> returndata = taskReturnAndInvalidDatasHandle(returndatas);
                    List<Map<String, Object>> invaliddatas = taskReturnAndInvalidDatasHandle(invalidatedatas);


                    double total = 0;
                    if(null != totalresmap){
                        for (Map<String ,Object> map:totalresmap){
                            int wordcount =Integer.parseInt(map.get("wordcount")+"");
                            total += wordcount;
                        }
                    }
                    double annotationcount = 0;
                    if(null != annotationresmap){
                        for (Map<String ,Object> map:annotationresmap){
                            int wordcount =Integer.parseInt(map.get("wordcount")+"");
                            annotationcount += wordcount;
                        }
                    }
                    double passcount = 0;
                    if(null != passresmap){
                        for (Map<String ,Object> map:passresmap){
                            int wordcount =Integer.parseInt(map.get("wordcount")+"");
                            passcount += wordcount;
                        }
                    }
                    //完成度（标注总量/任务总量），通过率（通过数量/标注总量）
                    double completerate = annotationcount/total;
                    double passrate = passcount/annotationcount;
                    DecimalFormat df = new DecimalFormat("0.00");
                    String complete = df.format(completerate);
                    String pass = df.format(passrate);

                    Map<String,Object> damap = new HashMap<>();
                    damap.put("id", StringUtil.getUUID());
                    damap.put("userid",userid);
                    damap.put("username",username);
                    damap.put("completion",complete);
                    damap.put("passrate",pass);
                    damap.put("total",totalresmap==null?new ArrayList():totalresmap);
                    damap.put("annotation",annotationresmap==null?new ArrayList():annotationresmap);
                    damap.put("pass",passresmap==null?new ArrayList():passresmap);
                    damap.put("return",returnresmap==null?new ArrayList():returnresmap);
                    damap.put("invalidate",invalidateresmap==null?new ArrayList():invalidateresmap);
                    damap.put("returndata",returndata==null?new ArrayList():returndata);
                    damap.put("invalidatedata",invaliddatas==null?new ArrayList():invaliddatas);

                    dalist.add(damap);

                }
            }
            Map<String,Object> projectmap = new HashMap<>();
            projectmap.put("projectid",projectid);
            projectmap.put("projectname",projectname);
            projectmap.put("data",dalist);
            projectlist.add(projectmap);
        }

        return projectlist;
    }

    //pmanager项目维度总览计算
    private List<Map<String, Object>> pmanagerProjectOverall(String begintime , String endtime , List<Map<String, Object>> pus) throws Exception {

        List<Map<String,Object>> overall = new ArrayList<>();
        //项目维度--总览：以项目为区分单位，计算每个项目对应所有用户的任务处理数据量
        for (Map<String,Object> project : pus){
            Map<String,Object> pmap = new HashMap<>();

            String projectid = String.valueOf(project.get("projectid"));
            String projectname = String.valueOf(project.get("projectname"));

            Map<String,Object> params = new HashMap<>(4);
            params.put("projectid",projectid);
            params.put("starttime",begintime);
            params.put("endtime",endtime);
            //每个项目对应的任务数据
            List<Map<String, Object>> list = taskInfoMapper.statisticSearchTask(params);
            if (list != null && list.size()>0){
                List<Map<String,Object>> tasktotaldatas = new ArrayList<>();//任务总数据量，不包含报废的任务
                List<Map<String,Object>> taskannotationdatas = new ArrayList<>();//标注总数据量
                List<Map<String,Object>> passdatas = new ArrayList<>();//通过数据量
                List<Map<String,Object>> returndatas = new ArrayList<>();//打回数据量
                List<Map<String,Object>> invalidatedatas = new ArrayList<>();//报废数据量
                for (Map<String,Object> taskinfo :list){
                    String taskid = String.valueOf(taskinfo.get("taskid"));
                    String deletetype =String.valueOf(taskinfo.get("deletetype"));//删除类型：是报废，还是直接删除
                    int callbacktimes = Integer.parseInt(taskinfo.get("callbacktimes")+"");//打回次数
                    String tasktype = String.valueOf(taskinfo.get("tasktype"));//任务类型：是审核任务还是普通标注任务

                    //统计任务总数量，不包括报废的数据量，所以需要排除报废的数据量
                    if (! deletetype.equals(Common.TASK_DELETE_TYPE_INVALIDATE)){
                        tasktotaldatas.add(taskinfo);//将非报废任务添加至任务总数据列表中
                    }
                    if (callbacktimes == 0 ){
                        //不包括统计打回重标的任务涉及的数据量
                        taskannotationdatas.add(taskinfo);
                    }
                    List<Map<String, Object>> resdatas = trfim.getFinalDatasByTaskId(taskid);//当前任务对应的结果文件信息
                    if (resdatas != null && resdatas.size()>0){
                        passdatas.add(taskinfo);
                    }
                    if (callbacktimes == 1){
                        returndatas.add(taskinfo);
                    }
                    if (deletetype.equals(Common.TASK_DELETE_TYPE_INVALIDATE)){
                        invalidatedatas.add(taskinfo);
                    }
                }
                List<Map<String ,Object>>  totalresmap = taskDatasHandle(tasktotaldatas);
                List<Map<String ,Object>>  annotationresmap = taskDatasHandle(taskannotationdatas);
                List<Map<String ,Object>>  passresmap = taskDatasHandle(passdatas);
                List<Map<String ,Object>>  returnresmap = taskDatasHandle(returndatas);
                List<Map<String ,Object>>  invalidateresmap = taskDatasHandle(invalidatedatas);

                List<Map<String, Object>> returndata = taskReturnAndInvalidDatasHandle(returndatas);
                List<Map<String, Object>> invaliddatas = taskReturnAndInvalidDatasHandle(invalidatedatas);

                double total = 0;
                if (totalresmap!=null && totalresmap.size()>0){
                    for (Map<String ,Object> map:totalresmap){
                        int wordcount =Integer.parseInt(map.get("wordcount")+"");
                        total += wordcount;
                    }
                }
                double annotationcount = 0;
                if (annotationresmap != null && annotationresmap.size()>0){
                    for (Map<String ,Object> map:annotationresmap){
                        int wordcount =Integer.parseInt(map.get("wordcount")+"");
                        annotationcount += wordcount;
                    }
                }
                double passcount = 0;
                if (passresmap!=null && passresmap.size()>0 ){
                    for (Map<String ,Object> map:passresmap){
                        int wordcount =Integer.parseInt(map.get("wordcount")+"");
                        passcount += wordcount;
                    }
                }
                //完成度（标注总量/任务总量），通过率（通过数量/标注总量）
                DecimalFormat df = new DecimalFormat("0.00");
                String complete = 0+"";
                String pass ="";
                if (total != 0){
                    double completerate = annotationcount/total;
                    complete = df.format(completerate);
                }else {
                    double completerate =0;
                }
                if (annotationcount != 0){
                    double passrate = passcount/annotationcount;
                    pass = df.format(passrate);
                }else {
                    double passrate = 0;
                    pass = df.format(passrate);
                }

                pmap.put("projectname",projectname);
                pmap.put("projectid",projectid);
                pmap.put("completion",complete);
                pmap.put("passrate",pass);
                pmap.put("total",totalresmap==null?new ArrayList():totalresmap);
                pmap.put("annotation",annotationresmap==null?new ArrayList():annotationresmap);
                pmap.put("pass",passresmap==null?new ArrayList():passresmap);
                pmap.put("return",returnresmap==null?new ArrayList():returnresmap);
                pmap.put("invalidate",invalidateresmap==null?new ArrayList():invalidateresmap);
                pmap.put("returndata",returndata==null?new ArrayList():returndata);
                pmap.put("invalidatedata",invaliddatas==null?new ArrayList():invaliddatas);
                overall.add(pmap);
            }
        }

        return overall;
    }

    //用户维度detail数据计算封装
    private Map<String,Object> userDimensionDetailHandle(String begintime , String endtime,
                                               List<Map<String, Object>> users, List<String> proids) throws Exception {

        Map<String,Object> detailmap = new HashMap<>();
        List<Map<String,Object>> userDDList = new ArrayList<>();
        for (Map<String, Object> u :users){
            String username =String.valueOf(u.get("username"));
            String userid =String.valueOf(u.get("userid"));
            Map<String,Object> param = new HashMap<>(4);
            param.put("starttime",begintime);
            param.put("endtime",endtime);
            param.put("role","user");
            param.put("performerid",userid);
            param.put("list",proids);
            List<Map<String, Object>> tasks = taskInfoMapper.pManagerStatisticTaskSearch(param);
            if (tasks != null && tasks.size()>0){
                List<Map<String,Object>> tasktotaldatas = new ArrayList<>();//任务总数据量，不包含报废的任务
                List<Map<String,Object>> taskannotationdatas = new ArrayList<>();//标注总数据量
                List<Map<String,Object>> passdatas = new ArrayList<>();//通过数据量
                List<Map<String,Object>> returndatas = new ArrayList<>();//打回数据量
                List<Map<String,Object>> invalidatedatas = new ArrayList<>();//报废数据量

                for (Map<String,Object> taskinfo:tasks){
                    String taskid = String.valueOf(taskinfo.get("taskid"));
                    String deletetype =String.valueOf(taskinfo.get("deletetype"));//删除类型：是报废，还是直接删除
                    int callbacktimes = Integer.parseInt(taskinfo.get("callbacktimes")+"");//打回次数
                    String tasktype = String.valueOf(taskinfo.get("tasktype"));//任务类型：是审核任务还是普通标注任务

                    //统计任务总数量，不包括报废的数据量，所以需要排除报废的数据量
                    if (! deletetype.equals(Common.TASK_DELETE_TYPE_INVALIDATE)){
                        tasktotaldatas.add(taskinfo);//将非报废任务添加至任务总数据列表中
                    }
                    if (callbacktimes == 0 ){
                        //不包括统计打回重标的任务涉及的数据量
                        taskannotationdatas.add(taskinfo);
                    }
                    List<Map<String, Object>> resdatas = trfim.getFinalDatasByTaskId(taskid);//当前任务对应的结果文件信息
                    if (resdatas != null && resdatas.size()>0){
                        passdatas.add(taskinfo);
                    }
                    if (callbacktimes == 1){
                        returndatas.add(taskinfo);
                    }
                    if (deletetype.equals(Common.TASK_DELETE_TYPE_INVALIDATE)){
                        invalidatedatas.add(taskinfo);
                    }
                }

                List<Map<String ,Object>>  totalresmap = taskDatasHandle(tasktotaldatas);
                List<Map<String ,Object>>  annotationresmap = taskDatasHandle(taskannotationdatas);
                List<Map<String ,Object>>  passresmap = taskDatasHandle(passdatas);
                List<Map<String ,Object>>  returnresmap = taskDatasHandle(returndatas);
                List<Map<String ,Object>>  invalidateresmap = taskDatasHandle(invalidatedatas);

                List<Map<String, Object>> returndata = taskReturnAndInvalidDatasHandle(returndatas);
                List<Map<String, Object>> invaliddatas = taskReturnAndInvalidDatasHandle(invalidatedatas);


                double total = 0;
                if (totalresmap!=null && totalresmap.size()>0){
                    for (Map<String ,Object> map:totalresmap){
                        int wordcount =Integer.parseInt(map.get("wordcount")+"");
                        total += wordcount;
                    }
                }
                double annotationcount = 0;
                if (annotationresmap != null && annotationresmap.size()>0){
                    for (Map<String ,Object> map:annotationresmap){
                        int wordcount =Integer.parseInt(map.get("wordcount")+"");
                        annotationcount += wordcount;
                    }
                }
                double passcount = 0;
                if (passresmap!=null && passresmap.size()>0 ){
                    for (Map<String ,Object> map:passresmap){
                        int wordcount =Integer.parseInt(map.get("wordcount")+"");
                        passcount += wordcount;
                    }
                }
                //完成度（标注总量/任务总量），通过率（通过数量/标注总量）
                DecimalFormat df = new DecimalFormat("0.00");
                String complete = 0+"";
                String pass ="";
                if (total != 0){
                    double completerate = annotationcount/total;
                    complete = df.format(completerate);
                }else {
                    double completerate =0;
                }
                if (annotationcount != 0){
                    double passrate = passcount/annotationcount;
                    pass = df.format(passrate);
                }else {
                    double passrate = 0;
                    pass = df.format(passrate);
                }
                Map<String,Object> usermap = new HashMap<>();
                usermap.put("id", StringUtil.getUUID());
                usermap.put("username",username);
                usermap.put("userid",userid);
                usermap.put("completion",complete);
                usermap.put("passrate",pass);
                usermap.put("total",totalresmap ==null?new ArrayList():totalresmap);
                usermap.put("annotation",annotationresmap ==null?new ArrayList():annotationresmap);
                usermap.put("pass",passresmap == null?new ArrayList():passresmap);
                usermap.put("return",returnresmap==null?new ArrayList():returnresmap);
                usermap.put("invalidate",invalidateresmap==null?new ArrayList():invalidateresmap);
                usermap.put("returndata",returndata==null?new ArrayList():returndata);
                usermap.put("invalidatedata",invaliddatas==null?new ArrayList():invaliddatas);
                userDDList.add(usermap);
            }
        }
        Map<String ,Object> datamap = new HashMap<>();
        datamap.put("data",userDDList);
        List<Map<String,Object>> delist = new ArrayList<>();
        delist.add(datamap);
        detailmap.put("detail",delist);

        return detailmap;
    }

    //处理任务
    private List<Map<String ,Object>>   taskDatasHandle(List<Map<String,Object>> tasktotaldatas) throws Exception{

        /**
         * =============================BEGIN================================
         */
        if (tasktotaldatas !=null && tasktotaldatas.size()>0){
            int tosennumber = 0;
            int tosenwordcount = 0;

            int topanumber = 0;
            int topawordcount = 0;

            int totxtnumber = 0;
            int totxtwordcount = 0;

            for (Map<String,Object> task :tasktotaldatas){
                String taskid = String.valueOf(task.get("taskid"));
                /**
                 * 1、根据任务id在task_annotation_data表中查询出对应的文本id---sourceid
                 * 2、根据sourceids列表查询data_info表、data_content表，查询其：dataname、本文当的字数size 、segmentation以及本文档的句子数
                 * 3、根据segmentation切分方式进行分组计算对应的字数和句子总数
                 *
                 */

                List<String> sourceids = taskAnnotationDataMapper.getSourceIdsByTaskId(taskid);
                for (String sourceid :sourceids){
                    Map<String, Object> datainfo = dataInfoMapper.getDataInfoBySourceId(sourceid);
                    String segmentation = String.valueOf(datainfo.get("segmentation"));
                    if (Common.FILE_SEGMENTATION_SENTENCE.equals(segmentation)){
                        int num = Integer.parseInt(datainfo.get("num")+"");
                        int size = Integer.parseInt(datainfo.get("size")+"");
                        tosennumber += num;
                        tosenwordcount += size;
                    }
                    //目前存储结构，以段落切分、篇章切分，计算句子数没意义
                    if (Common.FILE_SEGMENTATION_PARAGRAPH.equals(segmentation)){
                        int num = Integer.parseInt(datainfo.get("num")+"");
                        int size = Integer.parseInt(datainfo.get("size")+"");
                        topanumber += num;
                        topawordcount += size;
                    }
                    if (Common.FILE_SEGMENTATION_TEXT.equals(segmentation)){
                        int num = Integer.parseInt(datainfo.get("num")+"");
                        int size = Integer.parseInt(datainfo.get("size")+"");
                        totxtnumber += num;
                        totxtwordcount += size;
                    }
                }
            }
            /**
             * =============================================END=================================================
             */
            List<Map<String ,Object>> list = new ArrayList<>();
            Map<String,Object> senmap = new HashMap<>();
            senmap.put("segmentation",Common.FILE_SEGMENTATION_SENTENCE);
            senmap.put("number",tosennumber);
            senmap.put("wordcount",tosenwordcount);
            Map<String,Object> pamap = new HashMap<>();
            pamap.put("segmentation",Common.FILE_SEGMENTATION_PARAGRAPH);
            pamap.put("number",topanumber);
            pamap.put("wordcount",topawordcount);
            Map<String,Object> txtmap = new HashMap<>();
            txtmap.put("segmentation",Common.FILE_SEGMENTATION_TEXT);
            txtmap.put("number",totxtnumber);
            txtmap.put("wordcount",totxtwordcount);

            list.add(senmap);
            list.add(pamap);
            list.add(txtmap);
            return list;
        }

        return null;
    }

    private List<Map<String ,Object>>   taskReturnAndInvalidDatasHandle(List<Map<String,Object>> tasktotaldatas) throws Exception{

        if (tasktotaldatas.size()>0 && tasktotaldatas !=null){
            List<Map<String, Object>> sourceinfos = new ArrayList<>();
            for (Map<String,Object> map:tasktotaldatas){
                String taskid = String.valueOf(map.get("taskid"));
                sourceinfos = taskAnnotationDataMapper.getSourceinfoByTaskId(taskid);
            }
            return sourceinfos;
        }
        return null;
    }


}
