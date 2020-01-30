package com.blcultra.service;

/**
 * 内部导出json文件逻辑抽象接口
 * Created by sgy05 on 2019/2/21.
 */
public interface InnerExportCommand {

    /**
     * 执行
     * @param taskid
     * @param annotationobject
     * @return
     */
    String process(String taskid, String note, String dataids, String tasktype, String ispass, String annotationobject) throws Exception;
}
