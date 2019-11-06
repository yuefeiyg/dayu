package com.blcultra.dao;

import com.blcultra.model.DatasetInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DatasetInfoMapper {
    /**
     * 根据数据集id删除数据集
     * @param dsid
     * @return
     * @throws Exception
     */
    int deleteByPrimaryKey(String dsid) throws Exception;

    int addDataSetByMap(Map<String, Object> map) throws Exception;

    DatasetInfo selectByPrimaryKey(String dsid);

    /**
     * 查询归属用户的数据集列表
     * @param map
     * @return
     */
    List<Map<String,Object>> getDatasetListByOwner(Map<String, Object> map);

    int getDatasetListCountByOwner(Map<String, Object> map);

    /**
     * 根据数据集id查询数据集信息详情
     * @param dsid
     * @return
     */
    Map<String,Object> getDataSetInfoByDsid(String dsid);

    /**
     * 更新数据集信息
     * @param map
     * @return
     * @throws Exception
     */
    int updateByPrimaryKeySelective(Map<String, String> map) throws Exception;

    /**
     * 检查用户是否创建过相同的数据集
     * @param map
     * @return
     */
    Map<String,Object> checkIfExsit(Map<String, Object> map);

    /**
     * 获取数据源文本列表
     * @param map
     * @return
     */
    List<Map<String,Object>> getDataSettList(Map<String, Object> map);


    int getDataSettListCounts(Map<String, Object> map);

    int updateDeleteByPrimaryKey(Map<String, Object> map) throws Exception;


    /**
     * 查询归属用户的数据集列表
     * @param map
     * @return
     */
    List<Map<String,Object>> dataSetSearch(Map<String, Object> map);

    int dataSetSearchCount(Map<String, Object> map);


}