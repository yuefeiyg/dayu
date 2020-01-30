package com.blcultra.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by sgy05 on 2019/1/9.
 */
public interface FileComparisonService {


    String fileComparison(String textids, String scriptfilename, String type);

    String compareResultView(String resultid);

    String compareDataCodeFileResultView(String resultid, String datacode);

    String getCompareResults(Map<String, Object> map, int pageNow, int pageSize);

    String downloadCompareResults(HttpServletRequest request, HttpServletResponse response, String resultids);

    String scriptList();
}
