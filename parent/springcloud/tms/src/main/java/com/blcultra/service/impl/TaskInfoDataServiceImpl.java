package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.blcultra.cons.*;
import com.blcultra.dao.TaskAttachInfoMapper;
import com.blcultra.dao.taskResFileInfoMapper;
import com.blcultra.exception.ExceptionUtil;
import com.blcultra.exception.ServiceException;
import com.blcultra.service.TaskInfoDataService;
import com.blcultra.support.JsonModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service("taskInfoDataService")
public class TaskInfoDataServiceImpl implements TaskInfoDataService {

    private Logger log= LoggerFactory.getLogger(this.getClass());

    @Value("${file.uploadpath}")
    private String uploadpath;

    @Value("${resultfile.uploadpath}")
    private String resultfilepath;

    @Value("${attachment.uploadpath}")
    private String attachmentfilepath;

    @Value("${temp.uploadpath}")
    private String tempfilepath;

    @Autowired
    private taskResFileInfoMapper taskResFileInfoMapper;

    @Autowired
    private TaskAttachInfoMapper taskAttachInfoMapper;


    @Override
    public String downloadFile(String taskId, String filename, String datatype, HttpServletResponse response) {


        String downlaodFilePath = "";
        Map<String,String> params = new HashMap<>();
        params.put(DbConstant.TASK_ID,taskId);
        params.put(DbConstant.DATA_ID,filename);

        String downloadFileName = filename;

        InputStream fis = null;
        BufferedInputStream bis = null;
        try {
            if (Common.UP_DOWNLOAD_DATA_TYPE_TEMPLATE.equals(datatype)){
                fis = this.getClass().getResourceAsStream("/file/"+filename);

            }else if (Common.UP_DOWNLOAD_DATA_TYPE_RESULT.equals(datatype)){
                Map<String,Object> resultData = taskResFileInfoMapper.getDataByTaskIdAndDataId(params);
                downlaodFilePath = String.valueOf(resultData.get(DbConstant.TASK_RES_FILE_PATH));
                downloadFileName = String.valueOf(resultData.get(DbConstant.TASK_RES_FILE_DATA_NAME));
                File file = new File(downlaodFilePath);
                if (file.exists()) {
                    fis = new FileInputStream(file);
                }

            }else {
                Map<String,Object> resultData = taskAttachInfoMapper.getDataByTaskIdAndDataId(params);
                downlaodFilePath = String.valueOf(resultData.get(DbConstant.TASK_ATTACH_INFO_PATH));
                downloadFileName = String.valueOf(resultData.get(DbConstant.TASK_ATTACH_INFO_ATTACHMENT_NAME));
                File file = new File(downlaodFilePath);
                if (file.exists()) {
                    fis = new FileInputStream(file);
                }
            }

            if (fis == null){
                return BusinessEnum.DOWNLOAD_FAILED.getValue();
            }
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + downloadFileName);// 设置文件名
            byte[] buffer = new byte[1024];
            bis = new BufferedInputStream(fis);
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
            os.flush();
            os.close();
            return BusinessEnum.DOWNLOAD_SUCCESS.getValue();
        }catch (Exception e) {
            return BusinessEnum.DOWNLOAD_FAILED.getValue();

        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    @Override
    public String uploadDataFile(MultipartFile uploadfile, String dataType) {
        String res = null;
        try {
            if(!uploadfile .isEmpty()) {
                String taskDescfileName = uploadfile.getOriginalFilename();
                //将文件打上时间戳标记，以防止相同文件名出现
                long time = System.currentTimeMillis();
                StringBuilder filepath = new StringBuilder();
                if (Common.UP_DOWNLOAD_DATA_TYPE_DATA.equals(dataType)){
                    filepath.append(uploadpath).append(File.separator);
                }else if (Common.UP_DOWNLOAD_DATA_TYPE_ATT.equals(dataType)){
                    filepath.append(attachmentfilepath).append(File.separator);
                }else if (Common.UP_DOWNLOAD_DATA_TYPE_RESULT.equals(dataType)){
                    filepath.append(resultfilepath).append(File.separator);
                }else {
                    filepath.append(tempfilepath).append(File.separator);
                }
                filepath.append(time);
                filepath.append("@");
                filepath.append(taskDescfileName);
                File file = new File(filepath.toString());
                if (!file.getParentFile().exists()){
                    file.getParentFile().mkdir();
                }
                uploadfile.transferTo(file);
                Map<String,Object> map = new HashMap<>();
                map.put("filepath",filepath.toString());
                JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(ActionEnum.UPLOAD.getValue(),
                        Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,map);
                res = JSON.toJSONString(jm);
                return res;
            }
            JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(ActionEnum.UPLOAD.getValue(),
                    Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
            return res;
        } catch (IOException e) {
            log.error("TaskServiceImpl  uploadDataFile   occur exception :"+e);
            JsonModel jm = new JsonModel(false, MessageInfo.actionInfo(ActionEnum.UPLOAD.getValue(),
                    Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
        }
        return res;
    }

    @Override
    public String deleteFile(String filename, HttpServletResponse response) {
        String res = null;
        File file = new File(filename);
        if (file.exists()) {
            if (file.delete()) {
                JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(ActionEnum.DELETE.getValue(),
                        Messageconstant.REQUEST_SUCCESSED_CODE),Messageconstant.REQUEST_SUCCESSED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            } else {
                JsonModel jm = new JsonModel(true, MessageInfo.actionInfo(ActionEnum.DELETE.getValue(),
                        Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
                res = JSON.toJSONString(jm);
                return res;
            }
        } else {
            JsonModel jm = new JsonModel(true,MessageInfo.actionInfo(ActionEnum.DELETE.getValue(),
                    Messageconstant.REQUEST_FAILED_CODE),Messageconstant.REQUEST_FAILED_CODE,null);
            res = JSON.toJSONString(jm);
        }

        return res;
    }
}
