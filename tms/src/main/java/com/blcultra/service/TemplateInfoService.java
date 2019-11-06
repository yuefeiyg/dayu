package com.blcultra.service;

import java.util.Map;

public interface TemplateInfoService {

    String getTemplateInfo(Map<String, Object> map);

    String getTemplateList(Map<String, Object> map);

    String deleteTemplate(Map<String, Object> map);

    String selectTemplatelist(Map<String, Object> map);
}
