package com.blcultra.model;

public class Action {
    private String actionid;

    private String action;

    private String actionname;

    private String actionurl;

    private String actionredirecturl;

    private String moudle;

    private Integer order;

    public String getActionid() {
        return actionid;
    }

    public void setActionid(String actionid) {
        this.actionid = actionid == null ? null : actionid.trim();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action == null ? null : action.trim();
    }

    public String getActionname() {
        return actionname;
    }

    public void setActionname(String actionname) {
        this.actionname = actionname == null ? null : actionname.trim();
    }

    public String getActionurl() {
        return actionurl;
    }

    public void setActionurl(String actionurl) {
        this.actionurl = actionurl == null ? null : actionurl.trim();
    }

    public String getActionredirecturl() {
        return actionredirecturl;
    }

    public void setActionredirecturl(String actionredirecturl) {
        this.actionredirecturl = actionredirecturl == null ? null : actionredirecturl.trim();
    }

    public String getMoudle() {
        return moudle;
    }

    public void setMoudle(String moudle) {
        this.moudle = moudle == null ? null : moudle.trim();
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}