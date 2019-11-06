package com.blcultra.model;

import java.util.List;

public class Menu {
    private String menuid;

    private String menuname;

    private String menuurl;

    private String iconurl;

    private String parentid;

    private String enabled;

    private Integer level;

    private List<Menu> childmenus;

    public List<Menu> getChildmenus() {
        return childmenus;
    }

    public void setChildmenus(List<Menu> childmenus) {
        this.childmenus = childmenus;
    }

    public String getMenuid() {
        return menuid;
    }

    public void setMenuid(String menuid) {
        this.menuid = menuid == null ? null : menuid.trim();
    }

    public String getMenuname() {
        return menuname;
    }

    public void setMenuname(String menuname) {
        this.menuname = menuname == null ? null : menuname.trim();
    }

    public String getMenuurl() {
        return menuurl;
    }

    public void setMenuurl(String menuurl) {
        this.menuurl = menuurl == null ? null : menuurl.trim();
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl == null ? null : iconurl.trim();
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid == null ? null : parentid.trim();
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled == null ? null : enabled.trim();
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Menu)) return false;

        Menu menu = (Menu) o;

        if (!menuid.equals(menu.menuid)) return false;
        if (!menuname.equals(menu.menuname)) return false;
        if (!menuurl.equals(menu.menuurl)) return false;
        if (!iconurl.equals(menu.iconurl)) return false;
        if (!parentid.equals(menu.parentid)) return false;
        if (!enabled.equals(menu.enabled)) return false;
        if (!level.equals(menu.level)) return false;
        return childmenus.equals(menu.childmenus);
    }

    @Override
    public int hashCode() {
        int result = menuid.hashCode();
        result = 31 * result + menuname.hashCode();
        result = 31 * result + menuurl.hashCode();
        result = 31 * result + iconurl.hashCode();
        result = 31 * result + parentid.hashCode();
        result = 31 * result + enabled.hashCode();
        result = 31 * result + level.hashCode();
        result = 31 * result + childmenus.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "menuid='" + menuid + '\'' +
                ", menuname='" + menuname + '\'' +
                ", menuurl='" + menuurl + '\'' +
                ", iconurl='" + iconurl + '\'' +
                ", parentid='" + parentid + '\'' +
                ", enabled='" + enabled + '\'' +
                ", level=" + level +
                ", childmenus=" + childmenus +
                '}';
    }
}