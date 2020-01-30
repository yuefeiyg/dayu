package com.blcultra.dao;

import com.blcultra.model.AnnotationLabel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnnotationLabelMapper {
    int deleteByPrimaryKey(String labelid)throws Exception;

    int insertSelective(Map<String, Object> label)throws Exception;

    /**
     * 批量添加标签信息
     * @param labels
     * @return
     */
    int insertLabelBatch(List<Map<String, Object>> labels) throws Exception;

    /**
     * 根据模板id查询标签列表信息
     * @param templateid
     * @return
     */
    List<Map<String,Object>> getLablesByTemplateId(String templateid);

    List<Map<String,Object>> getLablesByAnnotationType(String annotationtype);

    AnnotationLabel selectByPrimaryKey(String labelid);

    int updateByPrimaryKeySelective(AnnotationLabel record) throws Exception;

}