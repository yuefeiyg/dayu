package com.blcultra.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 上传文件相关
 */
public interface UploadService {

    String uploadDataFile(MultipartFile file);
}
