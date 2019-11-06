package com.blcultra.model;

public class LogAnnotationObjectInfo {
    private String logid;

    private String taskid;

    private String userid;

//    private String dataid;
//
//    private String contentid;
//
//    private String pn;
//
//    private String sn;

    private String createordellable;

    private String operatetime;

    private String objectdataid;

    private String itemid;

    private String item;

    private String labelid;

    private String startoffset;

    private String endoffset;

    private String bndbox;

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

//    public String getDataid() {
//        return dataid;
//    }
//
//    public void setDataid(String dataid) {
//        this.dataid = dataid == null ? null : dataid.trim();
//    }
//
//    public String getContentid() {
//        return contentid;
//    }
//
//    public void setContentid(String contentid) {
//        this.contentid = contentid == null ? null : contentid.trim();
//    }
//
//    public String getPn() {
//        return pn;
//    }
//
//    public void setPn(String pn) {
//        this.pn = pn == null ? null : pn.trim();
//    }
//
//    public String getSn() {
//        return sn;
//    }
//
//    public void setSn(String sn) {
//        this.sn = sn == null ? null : sn.trim();
//    }

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

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid == null ? null : itemid.trim();
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item == null ? null : item.trim();
    }

    public String getLabelid() {
        return labelid;
    }

    public void setLabelid(String labelid) {
        this.labelid = labelid == null ? null : labelid.trim();
    }

    public String getStartoffset() {
        return startoffset;
    }

    public void setStartoffset(String startoffset) {
        this.startoffset = startoffset == null ? null : startoffset.trim();
    }

    public String getEndoffset() {
        return endoffset;
    }

    public void setEndoffset(String endoffset) {
        this.endoffset = endoffset == null ? null : endoffset.trim();
    }

    public String getBndbox() {
        return bndbox;
    }

    public void setBndbox(String bndbox) {
        this.bndbox = bndbox == null ? null : bndbox.trim();
    }
}