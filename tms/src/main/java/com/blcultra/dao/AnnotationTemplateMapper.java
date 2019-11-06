package com.blcultra.dao;

import com.blcultra.model.AnnotationTemplate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnnotationTemplateMapper {

    /**
     * 添加模板
     * @param template
     * @return
     * @throws Exception
     */
    int insertAnnotationTemplate(Map<String, Object> template) throws Exception;

    /**
     * 检查用户是否已经创建过相同模板名的模板
     * @param checkmap
     * @return
     */
    Map<String,Object> checkTemplateIfExistsByUser(Map<String, String> checkmap);

    /**
     * 获取模板详情信息
     * @param checkmap
     * @return
     */
    Map<String,Object> getTemplateInfo(Map<String, Object> checkmap);

    /**
     * 获取模板列表
     * @param map
     * @return
     */
    List<Map<String,Object>> getTemplateList(Map<String, Object> map);

    /**
     * 选取模板列表
     * @param map
     * @return
     */
    List<Map<String,Object>> selectTemplatelist(Map<String, Object> map);
    /**
     * 获取模板列表数量
     * @param map
     * @return
     */
    Integer getTemplateListCount(Map<String, Object> map);

    int deleteByPrimaryKey(String templateid) throws Exception;

    int updateByPrimaryKeySelective(AnnotationTemplate record) throws Exception;

    /**
     * 获取模板详情信息
     * @param templateId
     * @return
     */
    Map<String,Object> getTemplateInfoById(String templateId);

    String getTemplateIdByName(String templateName);


    int updateDeleteTemplate(String templateid) throws Exception;

    int updateDeleteTemplates(Map<String, String> map) throws Exception;

    Map<String,Object> getTemplateInfoByTaskId(String taskId);

}