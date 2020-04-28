package com.blcultra.exception;

import com.blcultra.cons.Messageconstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常处理工具类
 * Created by sgy05 on 2018/11/26.
 */
public class ExceptionUtil{

    private static Logger logger = LoggerFactory.getLogger(ExceptionUtil.class);

    /**
     * 将下层抛出的异常转换为resp返回码
     *
     * @param e Exception
     * @return
     */
    public static Exception handlerException4biz(Exception e) {
        Exception ex = null;
        if (!(e instanceof Exception)) {
            return null;
        }
        if (e instanceof ValidateException) {
            ex = new ServiceException(((ValidateException) e).getErrorCode(), ((ValidateException) e).getErrorMessage());
        }else if (e instanceof Exception) {
            ex = new ServiceException(Messageconstant.REQUEST_FAILED_CODE,
                    "操作失败，系统内部错误");
        }
        logger.error("ExceptionUtil.handlerException4biz,Exception=" + e.getMessage(), e);
        return ex;
    }
}
