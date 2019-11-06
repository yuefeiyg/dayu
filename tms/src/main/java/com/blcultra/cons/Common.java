package com.blcultra.cons;

public class Common {

    //common
    public final static String HIDDEN = "hidden";

    public final static String ACTIONS = "actions";

    public final static String ROLE = "role";

    public final static String ITEMS = "items";

    public final static String RELATIONS = "relations";

    public final static String DATA = "data";

    public final static String RESULT_DATA = "resultdata";

    public final static String IDS = "ids";

    public final static String AUDIT = "审核";

    public final static String ZERO = "0";

    public final static String ONE = "1";

    public final static String CODE = "code";

    public final static String SUCCESS = "success";

    public final static String FAILED = "failed";

    public final static String TOATL = "total";

    public final static String FAILED_DATA = "faileddata";

    public final static String STATISITIC_DIMENSION = "dimension";

    public final static String CHOOSED_SEARCH_OPTIONS = "choosedSearchOptions";

    public final static String UN_ACCALIMED = "未领取";

    public final static String TASK_STATE_STATISTIC_NAME = "任务状态统计";

    public final static String SYS_ROLE = "sysrole";

    public final static String PROJECT_ROLE = "projectrole";

    public final static String DELETE_STATE_YES = "0";

    public final static String DELETE_STATE_NO = "1";

    //module
    public final static String MODULE_TASK = "task";
    public final  static String MODULE_PROJECT = "project";
    public final  static String MODULE_USER= "user";

    public final static String MODULE_NAME_TASK = "任务";


    //module contnet
    public final static String MODULE_TASK_TABLE_PUBLIC = "public";

    public final static String MODULE_TASK_TABLE_MY = "my";

    public final static String MODULE_TASK_TABLE_RELATED = "related";

    public final static String MODULE_PROJECT_PUBLIC ="public";

    //user role
    public final static String USER_ROLE_ADMIN = "admin";

    public final static String USER_ROLE_MANAGER = "manager";

    public final static String USER_ROLE_USER = "user";

    public final static String BUSINESS_USER_ROLE_PMANAGER = "pmanager";//业务角色pmanager

    //task state 002

    public final static String MODULE_TASK_STATE_CREATE = "002001";

    public final static String MODULE_TASK_STATE_RECEIVE = "002002";

    public final static String MODULE_TASK_STATE_START = "002003";

    public final static String MODULE_TASK_STATE_PAUSE = "002004";

    public final static String MODULE_TASK_STATE_COMPLETE = "002005";

    public final static String MODULE_TASK_STATE_CLOSED = "002006";

    public final static String MODULE_TASK_STATE_DELETE = "002007";

    //任务类型001

    public final static String MODULE_TASK_LABEL_CLASSFYCODE = "001001";

    public final static String MODULE_TASK_DATA_CLASSFYCODE = "001002";

    public final static String MODULE_TASK_OTHER_CLASSFYCODE = "001003";

    public final static String MODULE_TASK_TYPE_TASK = "001004";

    public final static String MODULE_TASK_TYPE_AUDIT = "001005";

    //模板类型 007
    public final static String SINGLE_OBJECT_ATTRIBUTE_LABEL = "单对象属性标注";

    public final static String DOUBLE_OBJECT_ATTRIBUTE_LABEL = "双对象有向关系标注";

    public final static String ANNOTATION_TYPE_SEQUENCE_LABEL = "序列标注";

    public final static String ANNOTATION_TYPE_OVERALL_LABEL = "整体标注";

    public final static String ANNOTATION_TYPE_RELATION_LABEL = "关系标注";

    //数据类型
    public final static String DATA_TYPE_TEXT = "012001";//文本

    public final static String DATA_TYPE_IMAGE = "012002";//图像

    public final static String ATTACHMENT_TYPE_DATA = "data";

    public final static String ATTACHMENT_TYPE_ATT = "att";

    public final static String ATTACHMENT_TYPE_DESC_ATT = "desc";

    public final static String ATTACHMENT_BASE_PATH = "/home/data";

    public final static String DATA_SCENARIO_TYPE_SEQUENCE = "011001";//文本

    //DATA state 004
    public final static String DATA_EXCUTE_STATE_YES = "004001";

    public final static String DATA_EXCUTE_STATE_NO = "004000";

    //是否
    public final static String TASK_ANNO_DATA_RELATION_YES = "004002";
    public final static String TASK_ANNO_DATA_RELATION_NO = "004003";

    //commit type
    public final static String COMMIT_TYPE_RESULT = "result";

    public final static String COMMIT_TYPE_EVALUATION = "evaluation";

    //upload or download data type
    public final static String UP_DOWNLOAD_DATA_TYPE_DATA = "data";

    public final static String UP_DOWNLOAD_DATA_TYPE_ATT = "att";

    public final static String UP_DOWNLOAD_DATA_TYPE_RESULT = "result";

    public final static String UP_DOWNLOAD_DATA_TYPE_TEMPLATE = "template";

    //statisitic dimension
//    public final static String STATISTIC_DIMENSION_PROJECT = "project";
//
//    public final static String STATISTIC_DIMENSION_TASK = "task";
//
//    public final static String STATISTIC_DIMENSION_TASK_STATE = "taskstate";
//
//    public final static String STATISTIC_DIMENSION_USER = "user";

    public final static String STATISTIC_DIMENSION_PROJECT = "0";//项目

    public final static String STATISTIC_DIMENSION_TASK = "1";//任务

    public final static String STATISTIC_DIMENSION_TASK_STATE = "2";//任务状态

    public final static String STATISTIC_DIMENSION_USER = "3";//用户

    public final static String STATISTIC_DIMENSION_TASK_CLASSIFY = "classify";

    public final static String STATISTIC_LEGEND_TASK_NUMBER = "任务量";

    public final static String STATISTIC_LEGEND_PROCESSING_TASK_NUMBER = "进行中任务量";

    public final static String STATISTIC_LEGEND_COMPLETE_TASK_NUMBER = "已完成任务量";


    //用户点击对比时，选择的是数据集id列表  还是 数据文件id列表
    public  final static String COMPARE_TYPE_DATASET = "1";//需要对比的数据是数据集列表

    public final  static String COMPARE_TYPE_TEXT = "0";//需要对比的数据是文件列表

    //用户业务角色
    public final static String BUSINESS_ROLE_PMANAGER = "pmanager";//项目管理者
    public final static String BUSINESS_ROLE_AUDITOR = "auditor";//任务审核人
    public final static String BUSINESS_ROLE_MEMBER = "member";//普通成员

    //保存标注数据时，为兼容V1.0改造的逻辑，需要设置此标识
    public final static int ANNOTATIONDATASAVE_V1_FLAG = 1;//若为 1 则表示兼容V1.0改造逻辑，删除列表中的id是objectdataid
    public final static int ANNOTATIONDATASAVE_V2_FLAG = 0;//若为 0 则表示按V2.0的逻辑进行删除，删除列表中的id还是itemid


    /**
     * 代表此条数据是初步提交的结果数据还是最终数据，
     * 当用户标注完提交产生的结果设置为preliminary，
     * 审核通过产生的结果设置为final
     */
    public final static String TASK_RES_FILE_PRELIMINARY = "preliminary";
    public final static String TASK_RES_FILE_FINAL = " final";

    /**
     * 需要在任务表中增加一列删除原因，当任务被删除时，需要指明删除原因:
     * 1)报废操作导致的删除设置为报废，
     * 2)删除操作导致的删除设置为删除
     */
    public final static String TASK_DELETE_TYPE_INVALIDATE = "invalidate";//报废
    public final static String TASK_DELETE_TYPE_DELETE = "delete";//删除

    public final static String FILE_SEGMENTATION_SENTENCE = "sentence";//句子切分
    public final static String FILE_SEGMENTATION_PARAGRAPH = "paragraph";//段落切分
    public final static String FILE_SEGMENTATION_TEXT= "text";//篇章切分

//    public final static String STATISTIC_DIMENSION_MEMBER="member";
//    public final static String STATISTIC_DIMENSION_PROJECT="project";




}
