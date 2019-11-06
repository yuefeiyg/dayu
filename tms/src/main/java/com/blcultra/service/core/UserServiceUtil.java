package com.blcultra.service.core;

import com.blcultra.cons.UserRoleConstant;
import com.blcultra.dao.ProjectUserMapper;
import com.blcultra.dao.UserMapper;
import com.blcultra.model.ProjectUser;
import com.blcultra.model.Role;
import com.blcultra.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("userServiceUtil")
public class UserServiceUtil {

    @Autowired
    UserMapper userMapper;
    @Autowired
    ProjectUserMapper projectUserMapper;

    /**
     * 获取用户系统最大角色，查询时只需要判断用户最大的角色就可以
     * @param userid
     * @return
     */
    public String getUserMaxSysRoleByUserId(String userid){
        String maxSysRole = null;
        User user = userMapper.selectUserDetailsByUserId(userid);
        Set<Role> roles = user.getRoles();
        boolean managerFlag = false;
        boolean userFlag = false;
        boolean adminFlag = false;
        for (Role role :roles){
            if(role.getRolekey().equals(UserRoleConstant.SYS_ADMIN)){
                adminFlag = true;
                maxSysRole = UserRoleConstant.SYS_ADMIN;
            }
            if(role.getRolekey().equals(UserRoleConstant.SYS_MANAGER)){
                managerFlag = true;
            }
            if(role.getRolekey().equals(UserRoleConstant.SYS_USER)){
                userFlag = true;
            }
        }
        if(adminFlag){
            maxSysRole = UserRoleConstant.SYS_ADMIN;
        }else if(managerFlag){
            maxSysRole = UserRoleConstant.SYS_MANAGER;
        }else if(userFlag){
            maxSysRole = UserRoleConstant.SYS_USER;
        }
        return maxSysRole;
    }


    /**
     * 获取用户业务最大角色
     * @param userid
     * @return
     */
    public String getUserMaxBusinessRoleByUserId(String userid){
        String maxRole = null;
        List<ProjectUser> pus = projectUserMapper.getProjectUserByUserId(userid);
        List<String> ids = new ArrayList<>();
        boolean auditorFlag = false;
        boolean memeberFlag = false;
        boolean managerFlag = false;
        for (ProjectUser pu:pus){
            String projectid = pu.getProjectid();
            String prolekey = pu.getProlekey();
            if (null != prolekey && prolekey.equals(UserRoleConstant.PROJECT_MANAGER)){
                managerFlag = true;
            }
            if (null != prolekey && prolekey.equals(UserRoleConstant.PROJECT_AUDITOR)){
                auditorFlag = true;
            }
            if(null != prolekey && prolekey.equals(UserRoleConstant.PROJECT_MEMBER)){
                memeberFlag = true;
            }
        }
        if(managerFlag){
            maxRole = UserRoleConstant.PROJECT_MANAGER;
        }else if(auditorFlag){
            maxRole = UserRoleConstant.PROJECT_AUDITOR;
        }else if(memeberFlag){
            maxRole = UserRoleConstant.PROJECT_MEMBER;
        }
        return maxRole;
    }
}
