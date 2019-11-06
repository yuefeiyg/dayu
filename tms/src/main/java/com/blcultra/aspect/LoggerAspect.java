package com.blcultra.aspect;

import com.alibaba.fastjson.JSONObject;
import com.blcultra.dao.LogUserBehaviorMapper;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.util.ClientUtil;
import com.dayu.util.DateFormatUtil;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


@Component
@Aspect
public class LoggerAspect {
    private final static Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

    @Autowired
    LogUserBehaviorMapper logUserBehaviorMapper;

    @Pointcut("execution( * com.blcultra.controller.DataController.downloadDataFiles(..))")
    public void downloadDataFiles(){}

    @Pointcut("execution( * com.blcultra.controller.DataController.downloadDataFilesByDsid(..))")
    public void downloadDataFilesByDsid(){}

    @Pointcut("execution( * com.blcultra.controller.ProjectInfoController.generateDatasByProject(..))")
    public void generateDatasByProject(){}


    @Pointcut("downloadDataFiles() || downloadDataFilesByDsid()")
    public void downloadDataFileNoToken(){}

    @AfterReturning("downloadDataFileNoToken()")
    public void downloadDataFileNoTokenDoAfterReturning(JoinPoint joinPoint) throws Throwable {
        try{
            JSONObject jsonObject = new JSONObject();
            StringBuilder params =new StringBuilder("请求头部参数为：");
            Signature signature = joinPoint.getSignature();
            String[] parameterNames  = ((MethodSignature)signature).getParameterNames();
            int n = 0;
            for (Object arg : joinPoint.getArgs()) {
                // request/response无法使用toJSON
                if (arg instanceof HttpServletRequest) {
                    HttpServletRequest httpServletRequest = (HttpServletRequest) arg;
                    String ip = ClientUtil.getRealIpAddr(httpServletRequest);
                    String equipment = ClientUtil.getEquipment(httpServletRequest);
                    jsonObject.put("userUniqueKey",ip);
                    jsonObject.put("equipment",equipment);

                    //获取所有的头部参数
                    Enumeration<String> headerNames=httpServletRequest.getHeaderNames();
                    for(Enumeration<String> eh=headerNames;eh.hasMoreElements();){
                        String thisName=eh.nextElement().toString();
                        String thisValue=httpServletRequest.getHeader(thisName);
                        params.append(thisName+"="+thisValue);
                    }
                    params.append("\n传入参数为：");
                }else if(! (arg instanceof HttpServletResponse)){
                    jsonObject.put(parameterNames[n],arg);
                }
                n ++;
            }
            jsonObject.put("agentinfo",params);

            insertLog("downloadDatas",null,jsonObject);
        }catch (Exception e){
            logger.error("记录用户下载数据行为日志出错 ：\n"+e.getMessage(),e);
        }
    }

    @AfterReturning("generateDatasByProject()")
    public void generateDatasByProjectDoAfterReturning(JoinPoint joinPoint){

        try{
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userid = JWTUtil.getUserId(token);

            Signature signature = joinPoint.getSignature();
            String methodname = signature.getName();
            String serviceName = signature.getDeclaringTypeName();
            String[] parameterNames  = ((MethodSignature)signature).getParameterNames();

            JSONObject info = new JSONObject();
            // 构造参数组集合
            int n = 0;
            for (Object arg : joinPoint.getArgs()) {
                // request/response无法使用toJSON
                if ( !(arg instanceof HttpServletRequest || arg instanceof HttpServletResponse)) {
                    info.put(parameterNames[n],arg);
                }
                n ++;
            }
            info.put("method",serviceName+"."+methodname);
            insertLog("downloadDatasByProject",userid,info);
        }catch (Exception e){
            logger.error("记录用户从项目下下载数据行为日志出错 ：\n"+e.getMessage(),e);
        }

    }


    private void insertLog(String logtype,String userid,JSONObject info) throws Exception{

        Map<String,Object> loginfo = new HashMap<>(4);
        loginfo.put("logtype",logtype);
        loginfo.put("info",info.toJSONString());
        loginfo.put("createtime", DateFormatUtil.DateFormat());
        loginfo.put("userid",userid);

        logUserBehaviorMapper.insertLogUseBehaviorSelective(loginfo);
    }

}
