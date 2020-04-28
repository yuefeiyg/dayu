package com.blcultra.model;

public class UserBehaviorLog {
    private String logid;

    private String userid;

    private String taskid;

    private String textid;

    private String sentenceid;

    private String pn;

    private String sn;

    private Integer hasmarked;

    private String markinfo;

    private String operatetime;

    private String operation;

    private String createordellable;

    public String getLogid() {
        return logid;
    }

    public void setLogid(String logid) {
        this.logid = logid == null ? null : logid.trim();
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid == null ? null : userid.trim();
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid == null ? null : taskid.trim();
    }

    public String getTextid() {
        return textid;
    }

    public void setTextid(String textid) {
        this.textid = textid == null ? null : textid.trim();
    }

    public String getSentenceid() {
        return sentenceid;
    }

    public void setSentenceid(String sentenceid) {
        this.sentenceid = sentenceid == null ? null : sentenceid.trim();
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn == null ? null : pn.trim();
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn == null ? null : sn.trim();
    }

    public Integer getHasmarked() {
        return hasmarked;
    }

    public void setHasmarked(Integer hasmarked) {
        this.hasmarked = hasmarked;
    }

    public String getMarkinfo() {
        return markinfo;
    }

    public void setMarkinfo(String markinfo) {
        this.markinfo = markinfo == null ? null : markinfo.trim();
    }

    public String getOperatetime() {
        return operatetime;
    }

    public void setOperatetime(String operatetime) {
        this.operatetime = operatetime == null ? null : operatetime.trim();
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation == null ? null : operation.trim();
    }

    public String getCreateordellable() {
        return createordellable;
    }

    public void setCreateordellable(String createordellable) {
        this.createordellable = createordellable == null ? null : createordellable.trim();
    }

    @Override
    public String toString() {
        return "UserBehaviorLog{" +
                "logid='" + logid + '\'' +
                ", userid='" + userid + '\'' +
                ", taskid='" + taskid + '\'' +
                ", textid='" + textid + '\'' +
                ", sentenceid='" + sentenceid + '\'' +
                ", pn='" + pn + '\'' +
                ", sn='" + sn + '\'' +
                ", hasmarked=" + hasmarked +
                ", markinfo='" + markinfo + '\'' +
                ", operatetime='" + operatetime + '\'' +
                ", operation='" + operation + '\'' +
                ", createordellable='" + createordellable + '\'' +
                '}';
    }
}