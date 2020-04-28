package com.blcultra.service;

import com.blcultra.dto.LabelDataDto;

import java.util.Map;

/**
 * 标注数据存储服务接口
 * Created by sgy05 on 2019/2/15.
 */
public interface AnnotationDataSaveService {

    //对整体标注数据的保存
    String gloableSaveAnnotationData(LabelDataDto labelDataDto);

    //保存
    String saveAnnotationData(Map data);

    String deleteAnnotationData(String dataitemid);

    String saveAnnotationAllData(Map data);
}
