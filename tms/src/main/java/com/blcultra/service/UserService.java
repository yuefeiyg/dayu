package com.blcultra.service;

import com.blcultra.model.User;

/**
 *用户相关服务接口
 * Created by sgy05 on 2019/3/5.
 */
public interface UserService {
    //用户登录
    String login(String loginname, String loginpass);

    //用户退出登录
    String logout();

    //用户添加
    String addUser(String username, String roles, String personaldesc, String visible);

    //删除用户（包含删除一个用户和批量删除多个用户）
    String deleteUsers(String userids);

    //编辑用户信息
    String editUser(String userid, String username, String roles, String personaldesc, String visible);

    //用户修改密码
    String editPass(String oldpass, String newpass, String confirmpass);

    //超级管理员重置用户密码
    String resetUserPass(String userid);

    //获取用户列表
    String searchUserList(String query, Integer pageNow, Integer pageSize);


    //根据用户id获取用户详细信息包括用户信息和用户角色信息以及所拥有的菜单信息
    User selectUserDetailsByUserId(String userid);

//    根据字符模糊查询包含字符的用户名
    String getLikeUserByUserName(String username);

    String getManagers(String username);

    String getAuditorsByTaskId(String taskid);
}
