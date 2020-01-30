package com.blcultra.dao;

import com.blcultra.model.AnnotationLabelRelationRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnnotationLabelRelationRuleMapper {
    int deleteByPrimaryKey(String labelid) throws Exception;

    int insertLabelRelationRuleBatch(List<Map<String, Object>> labelsRules) throws Exception;

    int insertSelective(AnnotationLabelRelationRule record) throws Exception;

    AnnotationLabelRelationRule selectByPrimaryKey(String labelid);

    int updateByPrimaryKeySelective(AnnotationLabelRelationRule record) throws Exception;

}