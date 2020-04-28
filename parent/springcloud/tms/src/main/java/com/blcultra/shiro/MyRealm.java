package com.blcultra.shiro;

import com.blcultra.cache.LocalCache;
import com.blcultra.model.Menu;
import com.blcultra.model.Role;
import com.blcultra.model.User;
import com.blcultra.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class MyRealm extends AuthorizingRealm {
    private static final Logger log = LoggerFactory.getLogger(MyRealm.class);

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("@@@@@@@@请求进入doGetAuthorizationInfo方法。。。");
        String userid = JWTUtil.getUserId(principals.toString());
        User user = null;
        if (LocalCache.containsKey(userid)){//查看缓存中是否存在该用户，如果存在则直接从缓存中获取
            user = (User) LocalCache.get(userid);
            log.info("########[缓存]中取出user对象信息"+user.getUsername());
        }else {
            user = userService.selectUserDetailsByUserId(userid);
            log.info("*******【数据库】中取出user对象信息"+user.getUsername());
            LocalCache.put(userid,user);
        }

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        List<String> menuList = new ArrayList<>();
        List<String> roleNameList = new ArrayList<>();
        Set<Role> roleSet =user.getRoles();
        if (!CollectionUtils.isEmpty(roleSet)) {
            for(Role role : roleSet) {
                roleNameList.add(role.getRolekey());
                Set<Menu> menuSet = role.getMenus();
                if (!CollectionUtils.isEmpty(menuSet)) {
                    for (Menu menu : menuSet) {
                        menuList.add(menu.getMenuname());
                    }
                }
            }
        }
        simpleAuthorizationInfo.addRoles(roleNameList);
        simpleAuthorizationInfo.addStringPermissions(menuList);
        return simpleAuthorizationInfo;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        // 解密获得username，用于和数据库进行对比
        String userId = JWTUtil.getUserId(token);
        if (userId == null) {
            throw new AuthenticationException("token invalid");
        }
        User userBean = null;
        if (LocalCache.containsKey(userId)){//查看缓存中是否存在该用户，如果存在则直接从缓存中获取
            userBean = (User) LocalCache.get(userId);
        }else {
            userBean = userService.selectUserDetailsByUserId(userId);
            LocalCache.put(userId,userBean);
        }
        if (userBean == null) {

            throw new AuthenticationException("用户不存在。");
        }

        if (! JWTUtil.verify(token, userId, userBean.getPassword())) {
            throw new AuthenticationException("用户名或密码错误");
        }

        return new SimpleAuthenticationInfo(token, token, "my_realm");
    }
}
