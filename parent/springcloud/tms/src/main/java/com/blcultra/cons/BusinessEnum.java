package com.blcultra.cons;

public enum BusinessEnum {

    TASK_EXISTED("任务已存在！"),

    TASK_DATA_NOT_EXISTED("任务创建未选择有效数据！"),

    TASK_DELETE_DATA_ID_EMPTY("未选择有效的待删除数据！"),

    TASK_RECALL_DATA_ID_EMPTY("未选择有效的待召回数据！"),

    TASK_INVALIDATE_DATA_ID_EMPTY("未选择有效的待报废数据！"),

    TASK_ALREADY_RECEIVED("任务已被领取！"),

    TASK_ALREADY_CHANGE_CAUSE_RECEIVED_FAILED("任务变更导致领取失败，请重新领取！"),

    DOWNLOAD_SUCCESS("下载成功！"),

    DOWNLOAD_FAILED("下载失败！"),

    TASK_CREATENUMBER_INCONSISTENT("经办人和处理文本数量不一致，请检查是否需要一个经办人处理一个文本，如果不需要，请修改matchflag的值，重新上传");

    private final String value;

    private BusinessEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
