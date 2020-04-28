package com.blcultra.dao;

import com.blcultra.model.LogAnnotationRelations;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LogAnnotationRelationsMapper {

    int batchInsertLogRelations(List<LogAnnotationRelations> logaoinfos);


    int deleteByPrimaryKey(String logid);

    int insert(LogAnnotationRelations record);

    int insertSelective(LogAnnotationRelations record);

    LogAnnotationRelations selectByPrimaryKey(String logid);

    int updateByPrimaryKeySelective(LogAnnotationRelations record);

    int updateByPrimaryKey(LogAnnotationRelations record);
}