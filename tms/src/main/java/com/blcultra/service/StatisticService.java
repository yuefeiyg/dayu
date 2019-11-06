package com.blcultra.service;

import java.util.Map;

public interface StatisticService {

    /**
     * 统计信息导出excel
     * @return
     */
    String exportExcel(Map<String, Object> map);
}
