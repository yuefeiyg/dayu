package com.blcultra.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface DataSetService {

    /**
     * 新增数据集
     * @param map
     * @return
     */
    String addDataSet(Map<String, Object> map);

    /**
     * 查询数据集列表
     * @param map
     * @return
     */
    String datasetList(Map<String, Object> map);

    /**
     * 编辑数据集信息
     * @param map
     * @return
     */
    String editDataSet(Map<String, String> map);

    /**
     * 删除数据集及包含的数据（删除前提是数据没有被使用，即所有数据没有被使用）
     * @param map
     * @return
     */
    String deleteDataSet(Map<String, String> map);

    /**
     * 下载数据集中所有的数据
     * @param map
     * @return
     */
    String downloadDataFilesByDsid(HttpServletRequest request, HttpServletResponse response, Map<String, String> map);


    /**
     * 数据集层面的数据对比
     * @param map
     * @return
     */
    String dataSetCompare(Map<String, String> map);

    /**
     * 数据检索
     * @param map
     * @return
     */
    String dataSetSearch(Map<String, Object> map);

    /**
     * 新建任务时选择数据数量时选择处理的源数据
     * @param map
     * @return
     */
    String getDataTextList(Map<String, Object> map);
    /**
     * 新建任务时选择数据集
     * @param map
     * @return
     */
    String getDataSettList(Map<String, Object> map);

}
