package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.blcultra.cons.LinuxScpConstant;
import com.blcultra.cons.Messageconstant;
import com.blcultra.service.UploadService;
import com.blcultra.support.JsonModel;
import com.dayu.util.FileUtil;
import com.dayu.util.LinuxSCP2Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service(value = "uploadService")
public class UploadServiceImpl implements UploadService {
    private Logger log= LoggerFactory.getLogger(this.getClass());

    @Value("${forum.upload.static.path}")
    private String uploadfilepath;

    @Override
    public String uploadDataFile(MultipartFile file) {
        try {
            File file1= FileUtil.MultipartFileTransforToFile(file,uploadfilepath);
            String filepath = file1.getAbsolutePath();
            String filename =file1.getName();

            LinuxSCP2Util scp = LinuxSCP2Util.getInstance(LinuxScpConstant.IP, LinuxScpConstant.PORT,
                    LinuxScpConstant.USERNAME,"123@abc");
            scp.putFile(filepath, filename, LinuxScpConstant.REMOTEURL, null);

            String absolutefilepath = LinuxScpConstant.ABSOLUTEURL + filename;
            JsonModel jm = new JsonModel(true, "上传成功！", Messageconstant.REQUEST_SUCCESSED_CODE,absolutefilepath);
            String res = JSON.toJSONString(jm);
            return res;
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return  JSON.toJSONString(new JsonModel(false,"上传失败", Messageconstant.REQUEST_FAILED_CODE));
    }
}
