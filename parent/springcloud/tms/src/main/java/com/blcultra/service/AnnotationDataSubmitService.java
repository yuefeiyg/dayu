package com.blcultra.service;

import java.util.Map;

/**
 * 用户完成任务提交标注数据
 * Created by sgy05 on 2019/3/13.
 */
public interface AnnotationDataSubmitService {

    String submitLabelData(Map submitinfo);

    String batchSubmitLabelData(String taskid);

    String submitData(Map data);

}
