package com.blcultra.service.impl.command;

import com.blcultra.cons.ExportTemplateEnum;
import com.blcultra.service.InnerExportCommand;
import com.blcultra.support.SpringBeanFactory;
import com.dayu.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by sgy05 on 2019/2/21.
 */
@Component
public class InnerExportCommandContext {

    private final static Logger LOGGER = LoggerFactory.getLogger(InnerExportCommandContext.class);


    /**
     * 获取执行器实例
     * @param command 执行器实例
     * @return
     */
    public InnerExportCommand getInstance(String command) {

        Map<String, String> allClazz = ExportTemplateEnum.getAllClazz();

        //根据客户端传入参数拼装code值
        String code = command.trim();
        String clazz = allClazz.get(code);
        InnerExportCommand innerCommand = null;
        try {
            if (StringUtil.empty(clazz)){
                //TODO:为空时需要设置默认处理逻辑：设置默认为：文本 单对象  序列标注
                clazz = ExportTemplateEnum.EXPORT_CODE_1.getClazz();
            }
            innerCommand = (InnerExportCommand) SpringBeanFactory.getBean(clazz);
            LOGGER.info("innerCommand :"+innerCommand.getClass().getName());
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }

        return innerCommand;
    }
}
