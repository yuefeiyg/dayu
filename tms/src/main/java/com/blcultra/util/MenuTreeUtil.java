package com.blcultra.util;

import com.blcultra.model.Menu;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 生成菜单树
 * Created by sgy05 on 2019/3/8.
 */
public class MenuTreeUtil {

    public static List<Menu> getMenuTree(Set<Menu> menus){

        List<Menu> menuList = new ArrayList<Menu>();
        for (Menu menu:menus){
            //获取一级菜单
            if (StringUtils.isBlank(menu.getParentid())){
                menuList.add(menu);
            }
        }
        for (Menu m:menuList){
            m.setChildmenus(getChild(m.getMenuid(), menus));
        }
        return menuList;
    }
    //递归查找子菜单
    private static List<Menu>  getChild(String menuid,Set<Menu> menus){
        // 子菜单
        List<Menu> childList = new ArrayList<>();
        for (Menu menu : menus) {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (StringUtils.isNotBlank(menu.getParentid())) {
                if (menu.getParentid().equals(menuid)) {
                    childList.add(menu);
                }
            }
        }
        // 把子菜单的子菜单再循环一遍
        for (Menu menu : childList) {// 没有url子菜单还有子菜单
            if (StringUtils.isBlank(menu.getMenuurl())) {
                // 递归
                menu.setChildmenus(getChild(menu.getMenuid(), menus));
            }
        } // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }


}
