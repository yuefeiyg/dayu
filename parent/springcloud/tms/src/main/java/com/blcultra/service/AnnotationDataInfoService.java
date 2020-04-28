package com.blcultra.service;

import java.util.Map;

public interface AnnotationDataInfoService {

    String getDataInfo(Map<String, Object> map);

    String getAnnotationAndOriginData(Map<String, Object> map);

    String getDataIndexByTaskid(Map<String, Object> map);

    String getDataByIndexId(Map<String, Object> map);
}
