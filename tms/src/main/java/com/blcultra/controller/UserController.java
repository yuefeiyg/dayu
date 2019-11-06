package com.blcultra.controller;

import com.blcultra.service.UserService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sgy05 on 2019/3/8.
 */
@RestController
@RequestMapping("/user/")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param request
     * @param response
     * @param loginname  登录用户名
     * @param loginpass  登录密码
     * @return
     */
    @PostMapping(value = "login",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String login(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam(value="loginname",required = true) String loginname,
                        @RequestParam(value="loginpass" ,required = true) String loginpass) {//密码
        log.info("UserController  login接口：用户名：{}, 密码：{} ",loginname,loginpass);
        String res = userService.login(loginname.trim(),loginpass.trim());
        return res;
    }

    /**
     * 用户退出登陆
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/logout",produces = "application/json;charset=UTF-8")
    @ResponseBody
    @RequiresAuthentication
    public String logout(HttpServletRequest request,HttpServletResponse response) {
        log.info("logout.... ");
        String res = userService.logout();
        return res;
    }
    /**
     * 添加用户
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "add",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    @RequiresRoles(value={"admin","manager"},logical= Logical.OR)
    public String addUser(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(value="username",required = true) String username,
                          @RequestParam(value="roles",required = true) String roles,
                          @RequestParam(value="personaldesc",required = false) String personaldesc,
                          @RequestParam(value="visible",required = false) String visible) {//是否可见所有有用户：1：可见  0：不可见
        log.info("☆☆☆UserController   addUser 方法 : username:{} , roles:{}, personaldesc:{} , visible:{} ",
                                     username,roles,personaldesc,visible);
        String res = userService.addUser(username.trim(),roles,personaldesc,visible);
        return res;
    }
    /**
     * 删除或批量删除用户
     * @param request
     * @param response
     * @param userids  用户id，如："1,2,5,12" ，英文逗号分隔的字符串
     * @return
     */
    @PostMapping(value = "delete",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    @RequiresRoles(value={"admin","manager"},logical= Logical.OR)
    public String deleteUser(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(value="userids",required = true) String userids) {
        log.info("☆☆☆UserController   deleteUser 方法 : userids :{} ",userids);
        String res = userService.deleteUsers(userids);
        return res;
    }

    /**
     * 用户编辑
     * @param request
     * @param response
     * @param userid
     * @param username
     * @param roles
     * @param personaldesc
     * @param visible
     * @return
     */
    @PostMapping(value = "edit",produces = "application/json;charset=UTF-8")
//    @RequiresAuthentication
//    @RequiresRoles(value={"admin","manager"},logical= Logical.OR)
    public String editUser(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam(value="userid",required = true) String userid,
                           @RequestParam(value="username",required = true) String username,
                           @RequestParam(value="roles",required = true) String roles,
                           @RequestParam(value="personaldesc",required = false) String personaldesc,
                           @RequestParam(value="visible",required = false) String visible) {
        log.info("☆☆☆UserController editUser 方法 : userid :{} ,username:{} , roles:{}, personaldesc:{},visible:{}"
                            ,userid,username,roles,personaldesc,visible);
        String res = userService.editUser(userid,username,roles,personaldesc,visible);
        return res;
    }

    /**
     * 用户修改密码
     * @param oldpass  旧密码
     * @param newpass  新密码
     * @param confirmpass  确认新密码
     * @return
     */
    @PostMapping(value = "pass/edit",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String editPass(@RequestParam (value = "oldpass" ,required =true) String oldpass,
                           @RequestParam(value = "newpass",required = true) String newpass,
                           @RequestParam(value = "confirmpass",required = true) String confirmpass) {
        log.info("☆☆☆UserController   editPass 方法  ☆☆☆");
        String res = userService.editPass(oldpass,newpass,confirmpass);
        return res;
    }

    /**
     * 超级管理员重置用户密码
     * @param userid   用户id
     * @return
     */
    @PostMapping(value = "pass/reset",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    @RequiresRoles(value={"admin","manager"},logical= Logical.OR)
    public String resetPass(@RequestParam (value = "userid" ,required =true) String userid) {
        log.info("☆☆☆UserController   editPass 方法  ☆☆☆");
        String res = userService.resetUserPass(userid);
        return res;
    }

    /**
     * 用户搜索
     * @param query
     * @param pageNow
     * @param pageSize
     * @return
     */
    @GetMapping(value = "search",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    @RequiresRoles(value={"admin","manager"},logical= Logical.OR)
    public String getUserList(@RequestParam(value = "query",required = false) String query,
                              @RequestParam(value = "pageNow",required = false,defaultValue = "1") Integer pageNow,
                              @RequestParam (value = "pageSize" ,required =false,defaultValue = "5" ) Integer pageSize) {
        log.info("☆☆☆UserController   search 方法  ☆☆☆");
        String res = userService.searchUserList(query,pageNow,pageSize);
        System.out.println(res);
        return res;
    }

    /**
     * 根据用户名模糊查询用户相关信息
     * @param request
     * @param response
     * @param username
     * @return
     */
    @GetMapping(value = "likeSearchUserByUserName",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String getLikeUserByUserName(HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam(value = "username",required = false)String username) {
        String res = userService.getLikeUserByUserName(username);
        return res;
    }

    /**
     *  获取所有项目管理员
     * @param request
     * @param response
     * @param username
     * @return
     */
    @GetMapping(value = "getManagers",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String getManagers(HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam(value = "username",required = false)String username) {
        String res = userService.getManagers(username);
        return res;
    }

    /**
     * 根据任务ID查询项目组成员列表：在任务流转时，
     * 选择经办人即任务执行人时，需要有该项目组成员下拉列表
     * @param request
     * @param response
     * @param taskid
     * @return
     */
    @GetMapping(value = "/getAuditorsByTaskId",produces = "application/json;charset=UTF-8")
    @RequiresAuthentication
    public String getAuditorsByTaskId(HttpServletRequest request, HttpServletResponse response,
                                      @RequestParam(value = "taskid",required = true) String taskid) {
        String res = userService.getAuditorsByTaskId(taskid);
        return res;
    }


}
