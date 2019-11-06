package com.blcultra.service;

import javax.servlet.http.HttpServletResponse;

public interface DownloadService {

    //下载文件
    String downloadModelFile(String filename, HttpServletResponse response);
}
