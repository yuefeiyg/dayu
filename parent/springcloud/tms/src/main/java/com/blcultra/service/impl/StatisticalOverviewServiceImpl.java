package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.blcultra.cons.Common;
import com.blcultra.dao.ProjectUserMapper;
import com.blcultra.dao.UserRoleMapper;
import com.blcultra.service.StatisticalOverviewService;
import com.blcultra.service.impl.command.AdminStatisticService;
import com.blcultra.service.impl.command.GeneralUserStatisticService;
import com.blcultra.service.impl.command.ManagerStatisticService;
import com.blcultra.service.impl.command.PManagerStatisticService;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.JsonModel;
import com.blcultra.support.ReturnCode;
import com.dayu.util.DateFormatUtil;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 统计概览服务接口
 * Created by sgy05 on 2019/4/9.
 */
@Service(value = "statisticalOverviewService")
public class StatisticalOverviewServiceImpl implements StatisticalOverviewService {

    private static final Logger log = LoggerFactory.getLogger(StatisticalOverviewServiceImpl.class);
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private ProjectUserMapper projectUserMapper;
    @Autowired
    private GeneralUserStatisticService generalUserStatisticService;
    @Autowired
    private PManagerStatisticService pManagerStatisticService;
    @Autowired
    private ManagerStatisticService managerStatisticService;
    @Autowired
    private AdminStatisticService adminStatisticService;
    /**
     * 统计业务逻辑入口服务
     *
     * TODO:系统中manager用户可以有多个，创建各自的项目，目前系统在新增manager时有可见所有用户的功能
     * TODO:如果一个用户A是manager，但在另外的managerB管理的项目中是pmanager
     *
     * @param dimension  统计维度：项目维度、用户维度
     * @param begintime
     * @param endtime
     * @return
     */
    @Override
    public String statistic(String dimension, String begintime, String endtime) {
        String res = null;

        if (dimension == null && "".equals(dimension)){
            JsonModel jm = new JsonModel(true, ReturnCode.ERROR_CODE_1001.getValue(),ReturnCode.ERROR_CODE_1001.getKey(),null);
            res = JSON.toJSONString(jm);
            return res;
        }
        //只要其中一个为空，那么走默认时间段逻辑
        if(begintime==null || endtime == null){
            begintime = DateFormatUtil.getPastDate(7);
            endtime = DateFormatUtil.DateFormat();
        }

        String token = SecurityUtils.getSubject().getPrincipals().toString();
        String userid = JWTUtil.getUserId(token);
        //根据userid查询当前用户的用户角色
        Map<String, Object> roleinfo = userRoleMapper.getUserRoleDetailsByUserid(userid);
        List<Map<String, Object>> pus = projectUserMapper.getPUInfoByUserId(userid);
        String rolekey = String.valueOf(roleinfo.get("rolekey"));
        if (Common.USER_ROLE_ADMIN.equals(rolekey)){
            //当前用户为admin
            res = adminStatisticService.adminStatistic(userid, dimension, begintime, endtime);
            return res;
        }else if (Common.USER_ROLE_MANAGER.equals(rolekey)){
            //当前用户是manager===即super-manager，同时还要考虑其是否为pmanager
            res = managerStatisticService.managerStatistic(userid,dimension,begintime,endtime,pus);
            return res;
        }else if (pus.size()>0 && !Common.USER_ROLE_MANAGER.equals(rolekey)){//如果既不是admin又不是manager那么判断其是否为业务角色pmanager？
            //说明当前用户的业务角色是pmanager
            res = pManagerStatisticService.pManagerStatistic(userid,dimension,begintime,endtime,pus);
            return res;
        }
        //否则是普通用户
        res = generalUserStatisticService.generalUserStatistic(userid, dimension, begintime, endtime, Common.USER_ROLE_USER);
        return res;
    }
}
