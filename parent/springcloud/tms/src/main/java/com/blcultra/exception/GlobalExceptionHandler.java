package com.blcultra.exception;

import com.alibaba.fastjson.JSON;
import com.blcultra.cons.Messageconstant;
import com.blcultra.support.JsonModel;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by sgy05 on 2018/12/1.
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger("GlobalExceptionHandler");


    /**
     * 权限不足报错拦截
     *
     * @return
     * @throws Exception
     */
    @ExceptionHandler(UnauthorizedException.class)
    public String unauthorizedExceptionHandler() throws Exception {
        JsonModel jm = new JsonModel(false, "暂无权限操作", Messageconstant.REQUEST_FAILED_CODE,null);
        String res = JSON.toJSONString(jm);

        return res;
    }

    /**
     * 未登录报错拦截
     * 在请求需要权限的接口,而连登录都还没登录的时候,会报此错
     *
     * @return
     * @throws Exception
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public String unauthenticatedException() throws Exception {
        JsonModel jm = new JsonModel(false, "用户未登录",Messageconstant.REQUEST_FAILED_CODE,null);
        String res = JSON.toJSONString(jm);
        return res;
    }
}
