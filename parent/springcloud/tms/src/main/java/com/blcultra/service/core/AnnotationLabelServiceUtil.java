package com.blcultra.service.core;

import com.blcultra.dao.AnnotationCustomizeLabelMapper;
import com.blcultra.dao.AnnotationLabelMapper;
import com.dayu.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service(value = "annotationLabelServiceUtil")
public class AnnotationLabelServiceUtil {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AnnotationLabelMapper annotationLabelMapper;
    @Autowired
    AnnotationCustomizeLabelMapper annotationCustomizeLabelMapper;

    /**
     * 根据templateid或者taskid或者userid查询标签
     * @param map
     * @return
     */
    public List<Map<String,Object>> getLables(Map<String, Object> map) {
        List<Map<String,Object>> labels = new ArrayList<>();
        try {
            if(! StringUtil.empty(map.get("templateid")+"")){
                labels.addAll(annotationLabelMapper.getLablesByTemplateId(map.get("templateid")+""));
            }
            List<Map<String,Object>> customelabel = annotationCustomizeLabelMapper.getLables(map);
            labels.addAll(customelabel);
            return labels;
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return labels;
    }
}
