package com.blcultra.service.impl;

import com.blcultra.cons.Common;
import com.blcultra.dao.DataInfoIndexMapper;
import com.blcultra.dao.TaskAnnotationDataMapper;
import com.blcultra.dao.TaskInfoMapper;
import com.blcultra.dao.UserMapper;
import com.blcultra.model.User;
import com.blcultra.service.core.FileDataInfoServiceUtil;
import com.blcultra.shiro.JWTUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service(value = "annotationDataExportServiceImpl")
public class AnnotationDataExportServiceImpl implements AnnotationDataExportService {


    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Autowired
    private TaskAnnotationDataMapper taskAnnotationDataMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileDataInfoServiceUtil fileDataInfoServiceUtil;

    @Autowired
    private DataInfoIndexMapper dataInfoIndexMapper;

    @Override
    public boolean annotatioinDataExport(Map<String, Object> param) throws Exception {

        String taskid = String.valueOf(param.get("taskid"));
        String tasktype = String.valueOf(param.get("tasktype"));

        String token = SecurityUtils.getSubject().getPrincipals().toString();
        String userid = JWTUtil.getUserId(token);
        User user = userMapper.selectByPrimaryKey(userid);
        Map<String, Object> taskInfo = taskInfoMapper.getTaskInfo(taskid);
        List<Map<String, Object>> sources = taskAnnotationDataMapper.getAnnotationDataByTaskId(taskid);
        List<Map<String,Object>> indexs = new ArrayList<>();
        /**
         * 生成task_res_file_info、dataset_info、data_info、data_content表数据
         */
        for (Map<String, Object> source :sources){
            indexs.add(fileDataInfoServiceUtil.createDataFileInfoDatas(source,taskInfo,taskid,user,tasktype));
        }
        /**
         * 添加索引
         */
        if(indexs.size() > 0){
            dataInfoIndexMapper.addDataInfoIndexBatch(indexs);
        }

        if (tasktype.equals(Common.MODULE_TASK_TYPE_AUDIT)){
            List<String> list = new ArrayList<>();
            list.add(taskid);
            taskInfoMapper.updateTaskToCloseStateByIds(list);
        }
        return true;
    }
}
