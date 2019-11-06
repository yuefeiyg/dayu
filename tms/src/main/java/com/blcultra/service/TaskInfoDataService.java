package com.blcultra.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;


public interface TaskInfoDataService {

    //下载文件
    String downloadFile(String taskId, String filename, String datatype, HttpServletResponse response);

    //上传文件
    String uploadDataFile(MultipartFile file, String dataType);

    //删除文件
    String deleteFile(String filename, HttpServletResponse response);
}
