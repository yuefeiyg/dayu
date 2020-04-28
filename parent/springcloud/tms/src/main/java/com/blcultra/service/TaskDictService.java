package com.blcultra.service;

/**
 *
 * 字典表服务接口
 * Created by sgy05 on 2018/12/5.
 */

public interface TaskDictService {
    //根据字典表的父节点码获取任务字典表数据
    String getTaskDictData(String parentcode);

}
