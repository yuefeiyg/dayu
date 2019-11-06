package com.blcultra.service;

import java.util.Map;

public interface StandardDocService {

    String getCatalogues(String docid);

    String getContent(Map<String, String> map);

    String getOneContent(Map<String, String> map);

}
