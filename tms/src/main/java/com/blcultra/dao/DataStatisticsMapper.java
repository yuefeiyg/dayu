package com.blcultra.dao;

import org.apache.ibatis.annotations.Mapper;
import java.util.Map;

@Mapper
public interface DataStatisticsMapper {
    Map<String,Integer> getProjectStatisticCountByUser(Map<String, String> param);

    Map<String,Integer> getTaskStatisticCountByUser(Map<String, String> param);

}