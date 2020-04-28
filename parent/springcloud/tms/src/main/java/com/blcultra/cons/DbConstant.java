package com.blcultra.cons;

public class DbConstant {

    //common
    public final static String STATISTIC_GRAPH_TYPE_LINE = "line";

    public final static String STATISTIC_GRAPH_TYPE_BAR = "bar";

    public final static String STATISTIC_GRAPH_TYPE_PIE = "pie";

    public final static String TYPE = "type";

    public final static String DATA = "data";

    public final static String CODE = "code";

    public final static String LEGEND = "legend";

    public final static String X_DATA = "xdata";

    public final static String SERIES_DATA = "sdata";

    public final static String COMMIT_TYPE = "committype";

    public final static String SELECT_WHOLE_DATASET = "whole";

    public final static String DIRECT_RETUR = "return";

    public final static String DATA_NAMES = "datanames";

    public final static String P_START_TIME = "pstarttime";

    public final static String P_END_TIME = "pendtime";

    public final static String F_START_TIME = "fstarttime";

    public final static String F_END_TIME = "fendtime";

    public final static String TASK_NUMBER = "tasknumber";

    public final static String PROCESSING_TASK_NUMBER = "processingtasknumber";

    public final static String FINISH_TASK_NUMBER = "finishtasknumber";

    public final static String UN_RECEIVE_TASK_NUMBER = "unreceivetasknumber";

    public final static String RECEIVE_TASK_NUMBER = "receivetasknumber";

    public final static String DAY = "day";

    public final static String NAME = "name";

    public final static String VALUE = "value";

    //statistic task state
    public final static String STATISTIC_CREATE_TASK = "任务待领取";

    public final static String STATISTIC_RECEIVE_TASK = "任务已领取";

    public final static String STATISTIC_PROCESSING_TASK = "任务进行中";

    public final static String STATISTIC_FINISH_TASK = "任务已完成";

    public final static String STATISTIC_CLOSED_TASK = "任务已关闭";

    //task dict table
    public final static String TASK_DICT_CODE = "dictcode";

    //data info table
    public final static String DATA_INFO_OWNER = "owner";

    public final static String DATA_INFO_DATA_ID = "dataid";

    public final static String DATA_INFO_DS_ID = "dsid";

    public final static String DATA_INFO_DATA_NAME = "dataname";

    public final static String DATA_INFO_DATA_OBJECTTYPE = "dataobjecttype";

    //dataset info table
    public final static String DATASET_INFO_DS_NAME = "dsname";

    public final static String DATASET_INFO_DS_OWNER = "dsowner";


    // task info table field
    public final static String TASK_ID = "taskid";

    public final static String TASK_STATE = "taskstate";

    public final static String PERFORMER_ID = "performerid";

    public final static String PERFORMER = "performer";

    public final static String PERFORMER_NAME = "performername";

    public final static String PREV_PERFORMER_ID = "prevperformerid";

    public final static String PROJECT_ID = "projectid";

    public final static String TASKOWNER_ID = "taskownerid";

    public final static String TASKOWNER = "taskowner";

    public final static String PROJECT_OWNER = "projectowner";

    public final static String CREATEUSER_ID = "createuserid";

    public final static String CREATEUSER_NAME = "createuser";

    public final static String TASK_TITLE = "tasktitle";

    public final static String TASK_NAME = "taskname";

    public final static String TASK_OWNER = "taskowner";

    public final static String CLASSIFY_CODE = "classifycode";

    public final static String CLASSIFY_NAME = "classifyname";

    public final static String TEMPLATE_ID = "templateid";

    public final static String TASK_DESC = "taskdesc";

    public final static String FINISH_DEADLINE = "finishdeadline";

    public final static String CREATE_TIME = "createtime";

    public final static String RECEIVE_TIME = "receivetime";

    public final static String BEGIN_TIME = "begintime";

    public final static String PAUSE_TIME = "pausetime";

    public final static String FINISH_TIME = "finishtime";

    public final static String TASK_TYPE = "tasktype";

    public final static String  COST_TIME= "costtime";

    public final static String NOTE = "note";

    public final static String USER_ID = "userid";

    public final static String DATA_IDS = "dataids";

    public final static String ATT_FILES = "attfiles";

    public final static String DATA_OBJECT_TYPE = "dataobjecttype";

    public final static String DATA_ID = "dataid";

    public final static String DATA_NAME = "dataname";

    public final static String DATA_PATH = "datapath";

    public final static String CONTENT_ID = "contentid";

    public final static String ANNINFOS = "anninfos";

    public final static String TOTAL_WORDS = "totalwords";

    public final static String WORD_COUNT = "wordcount";

    public final static String TASK_DATA_NUMBER = "taskdatanumber";

    public final static String TASK_VERSION = "version";

    public final static String CALL_BACK_TIMES = "callbacktimes";

    public final static String AUDIT_TIMES = "audittimes";

    public final static String RESULT_DESC = "resultdesc";

    public final static String RESULT_DATASET = "resultdataset";

    public final static String SCORE = "score";

    public final static String COMMENTS = "comments";

    public final static String TASK_STATE_NAME ="taskstatename";


    //task annotation DATA table
    public final static String TASK_ANNO_OBJECT_DATA_ID = "objectdataid";

    public final static String TASK_ANNO_DATA_TYPE = "datatype";

    public final static String TASK_ANNO_TASK_ID = "taskid";

    public final static String TASK_ANNO_SOURCE_ID = "sourceid";

    public final static String TASK_ANNO_LABEL_TYPE = "labeltype";

    public final static String TASK_ANNO_DATA_ID = "dataid";

    public final static String TASK_ANNO_RELATION = "relation";

    public final static String TASK_ANNO_EXECUTE_STATE = "executestate";

    //task attach info table
    public final static String TASK_ATTACH_INFO_ATTACHMENT_ID = "attachmentid";

    public final static String TASK_ATTACH_INFO_ATTACHMENT_NAME = "attachmentname";

    public final static String TASK_ATTACH_INFO_ATTACHMENT_TYPE = "attachmenttype";

    public final static String TASK_ATTACH_INFO_PATH = "path";

    public final static String TASK_ATTACH_INFO_TASK_ID = "taskid";

    //annotation template table
    public final static String ANNOTATION_TEMPLATE_TYPE = "templatetype";

    public final static String ANNOTATION_TEMPLATE_ANNOTATION_OBJECT = "annotationobject";

    public final static String ANNOTATION_TEMPLATE_ANNOTATION_TYPE = "annotationtype";

    //annotation object info table
    public final static String ANNOTATION_OBJECT_DATA_ITEM_ID = "dataitemid";

    public final static String ANNOTATION_OBJECT_ITEM_ID = "itemid";

    public final static String ANNOTATION_OBJECT_OBJECT_DATA_ID = "objectdataid";

    public final static String ANNOTATION_OBJECT_OBJECT_ITEM = "item";

    public final static String ANNOTATION_OBJECT_LABEL_ID = "labelid";

    public final static String ANNOTATION_OBJECT_START_OFFSET = "startoffset";

    public final static String ANNOTATION_OBJECT_END_OFFSET = "endoffset";

    public final static String ANNOTATION_OBJECT_BNDBOX = "bndbox";

    public final static String ANNOTATION_OBJECT_CREATETIME = "createtime";

    //annotation object relation info table
    public final static String ANNOTATION_OBJECT_RELATION_DATA_ID = "relationdataid";

    public final static String ANNOTATION_OBJECT_RELATION_FROM_ITEM_ID = "fromitemid";

    public final static String ANNOTATION_OBJECT_RELATION_TO_ITEM_ID = "toitemid";

    //annotation template table
    public final static String ANNOTATION_OBJECT = "annotationobject";

    public final static String ANNOTATION_TYPE = "annotationtype";

    public final static String TEMPLATE_TYPE = "templatetype";

    //task res file table
    public final static String TASK_RES_FILE_DATA_ID = "dataid";

    public final static String TASK_RES_FILE_DATA_NAME = "dataname";

    public final static String TASK_RES_FILE_PATH = "path";

    public final static String TASK_RES_FILE_TASK_ID = "taskid";

    //project info table
    public final static String PROJECT_NAME = "projectname";

    //project user table
    public final static String P_MANAGER = "pmanager";


}
