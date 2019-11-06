package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.cache.LocalCache;
import com.blcultra.cons.Messageconstant;
import com.blcultra.dao.ProjectInfoMapper;
import com.blcultra.dao.ProjectUserMapper;
import com.blcultra.dao.UserMapper;
import com.blcultra.dao.UserRoleMapper;
import com.blcultra.exception.ExceptionUtil;
import com.blcultra.exception.ServiceException;
import com.blcultra.model.*;
import com.blcultra.service.UserService;
import com.blcultra.shiro.JWTToken;
import com.blcultra.shiro.JWTUtil;
import com.blcultra.support.JsonModel;
import com.blcultra.support.Page;
import com.blcultra.support.ReturnCode;
import com.blcultra.util.MenuTreeUtil;
import com.dayu.util.DateFormatUtil;
import com.dayu.util.MD5Util;
import com.dayu.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by sgy05 on 2019/3/5.
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {
    private  Logger log= LoggerFactory.getLogger(this.getClass());

    @Value(value = "${tms.pass}")
    private String pass;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private ProjectUserMapper projectUserMapper;
    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    /**
     * 用户登录接口服务
     * @param loginname  登录用户名
     * @param loginpass  登录密码
     * @return
     */
    @Override
    public String login(String loginname, String loginpass) {
        String res = null;
        try {
            beforeLoginValidate(loginname,loginpass);
            User user = userMapper.selectByLoginName(loginname);
            if(user == null){
                JsonModel json = new JsonModel(false, "该用户不存在", Messageconstant.REQUEST_FAILED_CODE,null);
                return JSON.toJSONString(json);
            }
            if(user.getUstate().equals("003000")){
                JsonModel json = new JsonModel(false, "该用户已被禁止登录",Messageconstant.REQUEST_FAILED_CODE,null);
                return JSON.toJSONString(json);
            }
            if (!MD5Util.verify(loginpass,loginname,user.getPassword())){
                JsonModel json = new JsonModel(false, "用户名或密码错误",Messageconstant.REQUEST_FAILED_CODE,null);
                return JSON.toJSONString(json);
            }
            String token = JWTUtil.sign(user.getUserid(), MD5Util.md5(loginpass,user.getUsername()));
            JWTToken jwtToken = new JWTToken(token);
            Subject subject = SecurityUtils.getSubject();
            subject.login(jwtToken);
            Map<String,Object> map =  new HashMap<>();
            map.put("token",token);
            Set<Role> roles = user.getRoles();
            Set<Menu> ms = new HashSet<>();
            for (Role role:roles){
                Set<Menu> menus = role.getMenus();
                ms.addAll(menus);
            }
            //获取标签树
            List<Menu> menuTree = MenuTreeUtil.getMenuTree(ms);

            Map<String,Object> resultmap = new HashMap<>();
            //获取该用户的业务角色信息
//            List<ProjectUser> pus = projectUserMapper.getProjectUserByUserId(user.getUserid());
            int pmanagercount = projectUserMapper.checkPmanager(user.getUserid());
            if(pmanagercount > 0){
                resultmap.put("pmanager",true);
            }else{
                resultmap.put("pmanager",false);
            }
//            resultmap.put("businessrole",pus);//将业务角色信息返回，包含用户id，该用户所在项目id，以及改用的业务角色
            resultmap.put("userid",user.getUserid());
            resultmap.put("username",user.getUsername());
            resultmap.put("personaldesc",user.getPersonaldesc());
            resultmap.put("telephone",user.getTelephone());
            resultmap.put("ustate",user.getUstate());
            resultmap.put("roles",roles);
            resultmap.put("menus",menuTree);
            resultmap.put("token",token);
            JsonModel json = new JsonModel(true, "登录成功",Messageconstant.REQUEST_SUCCESSED_CODE,resultmap);
            return JSON.toJSONString(json);
        } catch (Exception e) {

            e.printStackTrace();
        }
        JsonModel json = new JsonModel(false, "登录失败",Messageconstant.REQUEST_FAILED_CODE,null);
        return JSON.toJSONString(json);
    }

    @Override
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        JsonModel jm = new JsonModel(true,ReturnCode.SUCESS_CODE_0000.getValue(),ReturnCode.SUCESS_CODE_0000.getKey(),null);
        return JSON.toJSONString(jm);
    }

    private void beforeLoginValidate(String loginname,String loginpass){
        //验证参数
        if(StringUtils.isEmpty(loginname) || StringUtils.isEmpty(loginpass)){
            JSONObject error = new JSONObject();
            if(StringUtils.isEmpty(loginname)){
                error.put("loginName", "登录账号未填写");
            }
            if(StringUtils.isEmpty(loginpass)){
                error.put("loginPass", "登录密码未填写");
            }
//            return new ResultMsg<JSONObject>(SystemConstant.RESULT_PARAM_ERROR, "参数不完整",error);
        }
    }
    /**
     * 新增用户
     * @param username  用户名
     * @param roles  用户角色
     * @param personaldesc   用户描述
     * @param visible   新增用户，设置为管理员时，此参数限制该管理员是否可见系统所有用户，1：是，0：否
     * @return
     */
    @Override
    public String addUser(String username, String roles, String personaldesc, String visible) {


        try {
            User user = userMapper.selectByLoginName(username);
            if (user !=null){
                JsonModel jm = new JsonModel(false,"该用户名已存在",Messageconstant.REQUEST_FAILED_CODE,null);
                return JSON.toJSONString(jm);
            }
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userid = JWTUtil.getUserId(token);
            User insertuser = new User();
            String uuid = StringUtil.getUUID();
            insertuser.setUserid(uuid);
            insertuser.setUsername(username);
            insertuser.setPassword(MD5Util.md5(pass,username));//第一个参数为123456的md5值，默认密码123456
            insertuser.setCreateuserid(userid);
            insertuser.setPersonaldesc(personaldesc);
            insertuser.setUstate("003001");//添加用户时默认激活用户
            insertuser.setCreatetime(DateFormatUtil.DateFormat());
            userMapper.insertSelective(insertuser);
            String[] rs = roles.split(",");
            List<String> rolesList = Arrays.asList(rs);
            List<UserRoleKey> list = new ArrayList<>();
            for (String roleid:rolesList){
                UserRoleKey userRole = new UserRoleKey();
                userRole.setRoleid(roleid);
                userRole.setUserid(uuid);
                userRole.setVisible(visible);
                list.add(userRole);
            }
            userRoleMapper.batchInsert(list);
            JsonModel jm = new JsonModel(true,"新增用户操作成功",Messageconstant.REQUEST_SUCCESSED_CODE,null);
            return JSON.toJSONString(jm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonModel jm = new JsonModel(false,"新增用户失败",Messageconstant.REQUEST_FAILED_CODE,null);
        return JSON.toJSONString(jm);
    }
    /**
     * 删除一个用户或批量 删除多个用户
     * @param userids
     * @return
     */
    @Override
    public String deleteUsers(String userids) {
        String res = null;
        try {
            if (null == userids || "".equals(userids)){
                JsonModel jm = new JsonModel(false,"请选择要删除的用户",Messageconstant.REQUEST_FAILED_CODE,null);
                return JSON.toJSONString(jm);
            }
            String[] us = userids.split(",");
            List<String> uids = Arrays.asList(us);
            userMapper.updateUserByIds(uids);
            JsonModel jm = new JsonModel(true,"用户删除操作成功",Messageconstant.REQUEST_SUCCESSED_CODE,null);
            return JSON.toJSONString(jm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonModel jm = new JsonModel(false,"用户删除操作失败",Messageconstant.REQUEST_FAILED_CODE,null);
        return JSON.toJSONString(jm);
    }
    /**
     * 编辑用户信息
     * @param userid
     * @param username
     * @param roles
     * @param personaldesc
     * @param visible
     * @return
     */
    @Override
    public String editUser(String userid, String username, String roles, String personaldesc, String visible) {
        String res = null;
        try {
            User u = new User();
            u.setUserid(userid);
            u.setUsername(username);
            u.setPersonaldesc(personaldesc);
            u.setUpdatetime(DateFormatUtil.DateFormat());
            u.setPassword(MD5Util.md5(pass,username));//管理员编辑用户信息时，重置用户密码
            userMapper.updateByPrimaryKeySelective(u);

            String[] rs = roles.split(",");
            List<String> rolesList = Arrays.asList(rs);
            List<UserRoleKey> list = new ArrayList<>();
            for (String roleid:rolesList){
                UserRoleKey userRole = new UserRoleKey();
                userRole.setRoleid(roleid);
                userRole.setUserid(userid);
                userRole.setVisible(visible);
                list.add(userRole);
            }
            //删除该用户对应角色信息
            userRoleMapper.deleteRoleByUserid(userid);
            userRoleMapper.batchInsert(list);
            JsonModel jm = new JsonModel(true,"用户编辑操作成功",Messageconstant.REQUEST_SUCCESSED_CODE,null);
            return JSON.toJSONString(jm);
        } catch (Exception e) {
            log.error("UserServiceImpl  editUser   occur exception :"+e);
            ServiceException serviceException=(ServiceException) ExceptionUtil.handlerException4biz(e);
            JsonModel jm = new JsonModel(false, serviceException.getErrorMessage(),serviceException.getErrorCode(),null);
            res = JSON.toJSONString(jm);
        }
        return res;
    }
    /**
     * 用户修改个人密码
     * @param oldpass
     * @param newpass
     * @param confirmpass
     * @return
     */
    @Override
    public String editPass(String oldpass, String newpass, String confirmpass) {
        String res = null;
        try{
            if(StringUtils.isEmpty(oldpass)|| StringUtils.isEmpty(newpass) || StringUtils.isEmpty(confirmpass)){
                JsonModel jm = new JsonModel(false,"参数不能为空",Messageconstant.REQUEST_FAILED_CODE,null);
                return JSON.toJSONString(jm);
            }
            if (!newpass.equals(confirmpass)){
                JsonModel jm = new JsonModel(false,"新密码与确认密码不一致",Messageconstant.REQUEST_FAILED_CODE,null);
                return JSON.toJSONString(jm);
            }
            Subject subject = SecurityUtils.getSubject();
            String userId = JWTUtil.getUserId(subject.getPrincipals().toString());
            User user = userMapper.selectByPrimaryKey(userId);
            String password = user.getPassword();
            String pass = MD5Util.md5(oldpass, user.getUsername());
            if (!pass.equals(password)){
                JsonModel jm = new JsonModel(false,"原始密码不正确",Messageconstant.REQUEST_FAILED_CODE,null);
                return JSON.toJSONString(jm);
            }
            String npass = MD5Util.md5(newpass,user.getUsername());
            user.setPassword(npass);
            int m = userMapper.updateByPrimaryKeySelective(user);
            if (m>0){
                subject.logout();
                LocalCache.remove(userId);
                JsonModel jm = new JsonModel(true,"修改密码操作成功",Messageconstant.REQUEST_SUCCESSED_CODE,null);
                return JSON.toJSONString(jm);
            }
            JsonModel jm = new JsonModel(false,"修改密码失败",Messageconstant.REQUEST_FAILED_CODE,null);
            return JSON.toJSONString(jm);
        }catch (Exception e){
            log.error("UserServiceImpl  editPass   occur exception :"+e);
            ServiceException serviceException=(ServiceException) ExceptionUtil.handlerException4biz(e);
            JsonModel jm = new JsonModel(false, serviceException.getErrorMessage(),serviceException.getErrorCode(),null);
            res = JSON.toJSONString(jm);
        }
        return res;
    }
    /**
     * 超级管理员重置用户密码
     * @param userid
     * @return
     */
    @Override
    public String resetUserPass(String userid) {

        try {
            User user = userMapper.selectByPrimaryKey(userid);
            user.setPassword(MD5Util.md5(pass,user.getUsername()));
            userMapper.updateByPrimaryKeySelective(user);
            if (LocalCache.containsKey(userid)){//查看缓存中是否存在该用户，如果存在则直接从缓存中获取
                LocalCache.put(userid,user);
            }
            JsonModel jm = new JsonModel(true,"更新用户密码操作成功",Messageconstant.REQUEST_SUCCESSED_CODE,null);
            return JSON.toJSONString(jm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonModel jm = new JsonModel(false,"更新用户密码操作失败",Messageconstant.REQUEST_FAILED_CODE,null);
        return JSON.toJSONString(jm);
    }

    /**
     * 获取用户列表，模糊搜索
     * @param pageNow   当前页码
     * @param pageSize  一页显示多条数
     * @return
     */
    @Override
    public String searchUserList(String query,Integer pageNow, Integer pageSize) {

        String token = SecurityUtils.getSubject().getPrincipals().toString();
        String userid = JWTUtil.getUserId(token);
        UserRoleKey ur = userRoleMapper.getUserRoleInfoByUserid(userid);
        String visible = ur.getVisible();
        Map<String,Object> map = new HashMap<>();
        if(visible != null){
            map.put("visible", Integer.parseInt(visible));
        }
        map.put("createuserid",userid);
        map.put("query",query.trim());
        Page page = new Page();
        page.setPageSize(pageSize);
        page.setPageNow(pageNow);
        map.put("queryStart",page.getQueryStart());
        map.put("pageSize",page.getPageSize());

        User user = userMapper.selectUserDetailsByUserId(userid);
        Set<Role> roles = user.getRoles();
//        UserAuxiliaryCalculation userAuxiliaryCalculation = new UserAuxiliaryCalculation();
        List<Map<String, Object>> userList = userMapper.getSearchUserList(map);
        String role = "";
        for(Role rr : roles){
            role = rr.getRolekey();
            break;
        }
        int count = userMapper.getSearchUserListCounts(map);
        page.setTotal(count);

//        List<Map<String,Object>> newlist = new ArrayList<>();
//        for (Map<String,Object> userinfo:userList){
//            List<Map<String, Object>> actions = userAuxiliaryCalculation.getActions(role, Common.MODULE_USER);
//            userinfo.put(Common.ACTIONS,actions);
//            newlist.add(userinfo);
//        }
        page.setResultList(userList);
        JsonModel jm = new JsonModel(true,"查询用户列表操作成功",Messageconstant.REQUEST_SUCCESSED_CODE,page);
        return JSON.toJSONString(jm);
    }

    /**
     * 根据用户id获取用户详细信息,
     * 包括用户信息和用户角色信息以及所拥有的菜单信息
     * @param userid
     * @return
     */
    @Override
    public User selectUserDetailsByUserId(String userid) {
        log.info("selectUserDetailsByUserId  参数userid:{}",userid);
        User user = userMapper.selectUserDetailsByUserId(userid);
        return user;
    }

    @Override
    public String getLikeUserByUserName(String username) {
        String  res =null;
        try{
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);
            UserRoleKey ur = userRoleMapper.getUserRoleInfoByUserid(userId);
            String visible = ur.getVisible();
            Map<String,Object> map = new HashMap<>();
            if(visible != null){
                map.put("priority", Integer.parseInt(visible));
            }
            map.put("createuserid",userId);
            map.put("query",username.trim());

            List<Map<String, Object>> users = userMapper.getLikeUserByUserName(map);
            JsonModel jm = new JsonModel(true, ReturnCode.SUCESS_CODE_0000.getValue(),ReturnCode.SUCESS_CODE_0000.getKey(),users);
            return JSON.toJSONString(jm);
        }catch (Exception e){
            log.error("login occur exception :"+e);
            ServiceException serviceException=(ServiceException) ExceptionUtil.handlerException4biz(e);
            JsonModel jm = new JsonModel(false, serviceException.getErrorMessage(),serviceException.getErrorCode(),null);
            return JSON.toJSONString(jm);
        }
    }

    @Override
    public String getManagers(String username) {
        String  res =null;
        try{
//            String token = SecurityUtils.getSubject().getPrincipals().toString();
//            String userId = JWTUtil.getUserId(token);
            Map<String,String> map = new HashMap<>();
            if(null != username){
                map.put("username",username.trim());
            }
//            map.put("userid",userId);
            List<Map<String, Object>> users = userMapper.getManagers(map);
            JsonModel jm = new JsonModel(true, ReturnCode.SUCESS_CODE_0000.getValue(),ReturnCode.SUCESS_CODE_0000.getKey(),users);
            return JSON.toJSONString(jm);
        }catch (Exception e){
            log.error("login occur exception :"+e);
            ServiceException serviceException=(ServiceException) ExceptionUtil.handlerException4biz(e);
            JsonModel jm = new JsonModel(false, serviceException.getErrorMessage(),serviceException.getErrorCode(),null);
            return JSON.toJSONString(jm);
        }
    }

    @Override
    public String getAuditorsByTaskId(String taskid) {
        String  res =null;
        try {
            Map<String,String> taskidmap = new HashMap<>(2);
            String token = SecurityUtils.getSubject().getPrincipals().toString();
            String userId = JWTUtil.getUserId(token);
            taskidmap.put("userid",userId);
            if(taskid.contains(",")){
                String[] ids = taskid.split(",");
                StringBuilder idbuilder = new StringBuilder("'");
                for(String id : ids){
                    idbuilder.append(id+"','");
                }
                taskidmap.put("taskid",idbuilder.toString().substring(0,idbuilder.toString().length() - 2));
                //查询这些任务是不是属于同一个项目
                List<String> projects = projectInfoMapper.getProjectidsByTaskids(taskidmap);
                if(projects.size() > 1){
                    taskidmap.put("type","multiproject");
                }else{
                    taskidmap.put("type","singleproject");
                }

            }else{
                taskidmap.put("taskid",taskid);
            }
            List<Map<String,Object>> list = userMapper.getAuditorsByTaskId(taskidmap);
            JsonModel jm = new JsonModel(true,ReturnCode.SUCESS_CODE_0000.getValue(),ReturnCode.SUCESS_CODE_0000.getKey(),list);
            return JSON.toJSONString(jm);
        } catch (Exception e) {
            log.error("TaskUserServiceImpl   getUsersByTaskId occur exception :"+e);
            ServiceException serviceException=(ServiceException) ExceptionUtil.handlerException4biz(e);
            JsonModel jm = new JsonModel(false, serviceException.getErrorMessage(),serviceException.getErrorCode(),null);
            return JSON.toJSONString(jm);
        }
    }
}
