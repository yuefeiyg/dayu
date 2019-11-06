package com.blcultra.dao;

import com.blcultra.model.LogAnnotationObjectInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LogAnnotationObjectInfoMapper {

    int batchInsertLogAnnotationObjectInfo(List<LogAnnotationObjectInfo> list);

    int deleteByPrimaryKey(String logid);

    int insert(LogAnnotationObjectInfo record);

    int insertSelective(LogAnnotationObjectInfo record);

    LogAnnotationObjectInfo selectByPrimaryKey(String logid);

    int updateByPrimaryKeySelective(LogAnnotationObjectInfo record);

    int updateByPrimaryKey(LogAnnotationObjectInfo record);
}