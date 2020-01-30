package com.blcultra.cons;

import java.util.HashMap;
import java.util.Map;

/**
 * 导出json文件逻辑匹配枚举值
 * Created by sgy05 on 2019/2/21.
 */
public enum ExportTemplateEnum {

    EXPORT_CODE_1("文本 单对象属性标注 序列标注","textSingleSequenceLabelCommand"),//TextSingleSequenceLabelCommand
    EXPORT_CODE_2("文本 单对象属性标注 整体标注","textSingleIntegralLabelCommand"),//TextSingleIntegralLabelCommand
    EXPORT_CODE_3("图像 单对象属性标注 矩形框标注","picSingleRectangleLabelCommand"),//PicSingleRectangleLabelCommand
    EXPORT_CODE_4("图像 单对象属性标注 整体标注","picSingleIntegralLabelCommand"),//PicSingleIntegralLabelCommand
    EXPORT_CODE_5("文本 双对象有向关系标注 有向关系标注","textDoubleDirectRelateLabelCommand"),// TextDoubleDirectRelateLabelCommand
    Export_CODE_6("图像 双对象有向关系标注 有向关系标注","picDoubleDirectRelateLabelCommand");//PicDoubleDirectRelateLabelCommand



    /** 枚举值码 */
    private final String commandType;

    /**
     * 实现类
     */
    private final String clazz ;


    /**
     * 构建一个 。
     * @param commandType 枚举值码。
     */
    private ExportTemplateEnum(String commandType, String clazz) {
        this.commandType = commandType;
        this.clazz = clazz ;
    }

    /**
     * 得到枚举值码。
     * @return 枚举值码。
     */
    public String getCommandType() {
        return commandType;
    }
    /**
     * 获取 class。
     * @return class。
     */
    public String getClazz() {
        return clazz;
    }


    /**
     * 得到枚举值码。
     * @return 枚举值码。
     */
    public String code() {
        return commandType;
    }


    /**
     * 获取全部枚举值码。
     *
     * @return 全部枚举值码。
     */
    /*public static Map<String,String> getAllStatusCode() {
        Map<String,String> map = new HashMap<String, String>(16) ;
        for (ExportTemplateEnum status : values()) {
            map.put(status.getCommandType(),status.getDesc()
            );
        }
        return map;
    }*/

    public static Map<String,String> getAllClazz() {
        Map<String,String> map = new HashMap<String, String>(16) ;
        for (ExportTemplateEnum status : values()) {
            map.put(status.getCommandType().trim(),status.getClazz()) ;
        }
        return map;
    }
}
