package com.blcultra.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface TaskInfoCreateService {

    //增加任务
    String addTask(Map<String, String> task);

    //编辑任务
    String editTask(Map<String, String> task);

    //批量扭转任务
    String reverseTask(Map<String, String> task);

    //审核不通过打回任务
    String returnTask(Map<String, String> task);

    //审核不通过打回任务
    String batchReturnTask(String taskids);

    //上传文件,批量新建
    String batchAdd(MultipartFile file);


}
