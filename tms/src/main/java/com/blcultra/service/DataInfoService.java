package com.blcultra.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface DataInfoService {

    /**
     * 上传数据文件 到服务器指定位置
     * @param file
     * @return
     */
    String uploadDatafile(MultipartFile file, String dsid);

    /**
     * 数据集追加数据文件
     * @param datamap
     * @return
     */
    String addDatafileToDataSet(Map<String, Object> datamap);

    /**
     * 获取当前用户下指定数据集下所有的数据信息
     * @param datamap
     * @return
     */
    String dataObjectList(Map<String, Object> datamap);

    /**
     * 新增数据集下的数据文件
     * @param map
     * @return
     */
    String addDataInfos(Map<String, String> map);

    /**
     * 删除具体的数据，可以批量删除
     * @param map
     * @return
     */
    String updatedeleteDataObject(Map<String, String> map);

    /**
     * 下载选中的数据
     * @param map
     * @return
     */
    String downloadDataFiles(HttpServletRequest request, HttpServletResponse response, Map<String, String> map);

    /**
     * 查看数据详情信息
     * @param map
     * @return
     */
    String getDataInfoByDataId(Map<String, String> map);

    /**
     * 数据层面的数据对比
     * @param map
     * @return
     */
    String dataCompare(Map<String, String> map);


    /**
     * 从磁盘上删除文件
     * @param map
     * @return
     */
    String deleteondisk(Map<String, String> map);

}
