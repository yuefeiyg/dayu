package com.blcultra.dao;

import com.blcultra.model.DataInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataInfoMapper {


    List<Map<String,Object>> selectDataInfoByDataIds(List<String> list);

    //根据数据集id列表查询文本信息列表
    List<DataInfo> getDataInfosByDsids(List<String> list);

    int deleteByPrimaryKey(String dataid) throws Exception;

    int insertSelective(Map<String, Object> record) throws Exception;

    int insertDataInfoSelective(DataInfo dataInfo) throws Exception;
    /**
     * 批量新增数据文本入库
     * @param datainfos
     * @return
     */
    int addDataInfoBatch(List<Map<String, Object>> datainfos) throws Exception;

    /**
     * 根据数据集id查询可用数据文件列表
     * @param dsid
     * @return
     */
    List<Map<String,Object>> getDataInfoInUseListByDsid(String dsid);

    /**
     * 根据数据集id查询所有数据文件列表
     * @param dsid
     * @return
     */
    List<Map<String,Object>> getDataInfoListByDsid(String dsid);

    /**
     * 根据数据集id查询可用数据文件列表的文件内容
     * @param dsid
     * @return
     */
    List<Map<String,Object>> getDataInfoContentsInUseListByDsid(String dsid);

    /**
     * 根据数集id查询可用数据文件列表的文件内容
     * @param dataids
     * @return
     */
    List<Map<String,Object>> getDataInfoAllByDataids(String dataids);

    /**
     * 根据数据集id删除数据文件
     * @param dsid
     * @return
     * @throws Exception
     */
    int deleteByDataSetId(String dsid) throws Exception;

    /**
     * 查询归属用户的数据集列表
     * @param map
     * @return
     */
    List<Map<String,Object>> getDataObjectListByDsid(Map<String, Object> map);

    int getDataObjectListCountByDsid(Map<String, Object> map);

    /**
     *  删除具体的数据，可以批量删除(根据owner和dataid进行删除)
     * @param map
     * @return
     */
    int deleteDataObjectByParam(Map<String, String> map) throws Exception;

    /**
     * 根据dataid查询datainfo信息和标注信息
     * @param map
     * @return
     */
    List<Map<String,Object>> getDataInfoByDataId(Map<String, String> map);


    DataInfo selectByPrimaryKey(String dataid);

    /**
     * 根据dsid删除或者根据dataid删除
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(DataInfo record) throws Exception;

    /**
     * 根据dataids查询datainfo信息和标注信息
     * @param
     * @return
     */
    List<Map<String,Object>> getDataInfoByDataIds(List<String> dataids);

    int getDataWordsByDataIds(List<String> dataids);

    List<String> getDataIds(String dsId);

    List<Map<String,Object>> getDataIdsByDataNameAndDsName(Map<String, String> map);

    int updateDeleteByDataSetId(Map<String, Object> map);

    List<Map<String,String>> selectDataUrlByDsids(Map<String, String> map);

    List<Map<String,String>> selectDataUrlByDsid(String dsid);

    int updatedeleteDataObjectByids(Map<String, String> map) throws Exception;

    int updatedeleteDataObjectByid(Map<String, String> dataid) throws Exception;

    List<Map<String,String>> selectDataInfoByids(Map<String, String> map);

    Map<String,Object> selectDataInfoByid(String dataid);

    /**
     * 获取数据源文本列表
     * @param map
     * @return
     */
    List<Map<String,Object>> getDataTextList(Map<String, Object> map);

    int getDataTextListCounts(Map<String, Object> map);

    //获取文本相关信息
    Map<String,Object>getDataInfoBySourceId(String sourceid);

    List<Map<String,Object>> getdatasbytaskid(Map<String, Object> map);

    List<Map<String,Object>> getDataInfos(Map<String, Object> map);

    int getDataInfosCount(Map<String, Object> map);

    List<Map<String,Object>> getImageDataByIndexId(Map<String, Object> map);

}