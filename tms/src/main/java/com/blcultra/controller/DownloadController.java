package com.blcultra.controller;

import com.blcultra.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping(value = "/download")
public class DownloadController {

    @Autowired
    DownloadService downloadService;

    /**
     * 下载模板文件
     * @param request
     * @param response
     * @param filename
     * @return
     */
    @CrossOrigin
    @GetMapping(value ="/downloadModelFile",produces = "application/json;charset=UTF-8")
    public String downloadModelFile(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam(value = "filename",required = true) String filename) {
        String res = downloadService.downloadModelFile(filename, response);
        return res;
    }

}
