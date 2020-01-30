package com.blcultra.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnnotationCustomizeLabelMapper {

    int insertSelective(Map<String, Object> labelinfo) throws Exception;

    List<Map<String,Object>> getLables(Map<String, Object> map);

    Map<String,Object> getParentLabel(Map<String, Object> map);
}