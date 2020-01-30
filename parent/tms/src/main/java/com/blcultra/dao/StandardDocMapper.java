package com.blcultra.dao;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface StandardDocMapper {

    List<Map<String,Object>> getCatalogues(String docid);

    List<Map<String,Object>> getContent(Map<String, String> map);

    Map<String,Object> getOneContent(Map<String, String> map);

}