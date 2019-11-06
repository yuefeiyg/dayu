package com.blcultra.controller;

import com.blcultra.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/upload")
@CrossOrigin
public class UploadController {

    @Autowired
    UploadService uploadService;

    /**
     * 上传接口
     * @param request
     * @param response
     * @param file
     * @return
     */
    @PostMapping(value = "picture",produces = "application/json;charset=UTF-8")
    @ResponseBody
//    @RequiresAuthentication
    public String uploadDataFile(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam(value = "file") MultipartFile file) {

        String res = uploadService.uploadDataFile(file);
        return res;
    }
}
