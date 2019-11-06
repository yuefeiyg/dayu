package com.blcultra.model;

public class LogAnnotationRelations {
    private String logid;

    private String taskid;

    private String userid;

    private String createordellable;

    private String operatetime;

    private String objectdataid;

    private String relationdataid;

    private String labelid;

    private String fromitemid;

    private String toitemid;

    public String getLogid() {
        return logid;
    }

    public void setLogid(String logid) {
        this.logid = logid == null ? null : logid.trim();
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid == null ? null : taskid.trim();
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid == null ? null : userid.trim();
    }


    public String getCreateordellable() {
        return createordellable;
    }

    public void setCreateordellable(String createordellable) {
        this.createordellable = createordellable == null ? null : createordellable.trim();
    }

    public String getOperatetime() {
        return operatetime;
    }

    public void setOperatetime(String operatetime) {
        this.operatetime = operatetime == null ? null : operatetime.trim();
    }

    public String getObjectdataid() {
        return objectdataid;
    }

    public void setObjectdataid(String objectdataid) {
        this.objectdataid = objectdataid == null ? null : objectdataid.trim();
    }

    public String getRelationdataid() {
        return relationdataid;
    }

    public void setRelationdataid(String relationdataid) {
        this.relationdataid = relationdataid == null ? null : relationdataid.trim();
    }

    public String getLabelid() {
        return labelid;
    }

    public void setLabelid(String labelid) {
        this.labelid = labelid == null ? null : labelid.trim();
    }

    public String getFromitemid() {
        return fromitemid;
    }

    public void setFromitemid(String fromitemid) {
        this.fromitemid = fromitemid == null ? null : fromitemid.trim();
    }

    public String getToitemid() {
        return toitemid;
    }

    public void setToitemid(String toitemid) {
        this.toitemid = toitemid == null ? null : toitemid.trim();
    }
}