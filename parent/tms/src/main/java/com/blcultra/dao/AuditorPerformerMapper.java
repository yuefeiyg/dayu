package com.blcultra.dao;

import com.blcultra.model.AuditorPerformer;

import java.util.List;
import java.util.Map;

public interface AuditorPerformerMapper {

    //批量插入
    int batchInsert(List<AuditorPerformer> list) throws Exception;

    int insert(AuditorPerformer record);

    int insertSelective(AuditorPerformer record);

    List<Map<String,String>> selectAuditorandPerformerByProjectid(String projectid);

    int deleteByProjectid(String projectid) throws Exception;
}