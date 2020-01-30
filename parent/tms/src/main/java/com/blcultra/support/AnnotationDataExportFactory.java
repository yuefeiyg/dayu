package com.blcultra.support;

import com.blcultra.cons.AnnotationTemplateConstant;
import com.blcultra.service.impl.AnnotationDataExportService;
import com.dayu.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 工厂模式创建数据提交导出处理类
 * Created by sgy05 on 2019/5/31.
 */
public class AnnotationDataExportFactory {
    private static Logger log = LoggerFactory.getLogger(AnnotationDataExportFactory.class);
    public AnnotationDataExportService produce(String annotationtype){
        if (annotationtype.equals(AnnotationTemplateConstant.ANNOTATION_TYPE_QA_TYPE)
        || annotationtype.equals(AnnotationTemplateConstant.ANNOTATION_TYPE_SEMANTICS)
        || annotationtype.equals(AnnotationTemplateConstant.ANNOTATION_TYPE_DEPENDENCY)){
            return (AnnotationDataExportService) SpringUtil.getBean("annotationDataExportServiceImpl");
        }else {
            log.info("请输入正确的标注类型");
            return null;
        }
    }

}
