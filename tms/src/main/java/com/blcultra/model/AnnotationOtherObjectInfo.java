package com.blcultra.model;

public class AnnotationOtherObjectInfo {
    private String dataitemid;

    private String itemid;

    private String objectdataid;

    private Object datainfo;

    private String createtime;

    private String annotationtype;

    public String getAnnotationtype() {
        return annotationtype;
    }

    public void setAnnotationtype(String annotationtype) {
        this.annotationtype = annotationtype;
    }

    public String getDataitemid() {
        return dataitemid;
    }

    public void setDataitemid(String dataitemid) {
        this.dataitemid = dataitemid == null ? null : dataitemid.trim();
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid == null ? null : itemid.trim();
    }

    public String getObjectdataid() {
        return objectdataid;
    }

    public void setObjectdataid(String objectdataid) {
        this.objectdataid = objectdataid == null ? null : objectdataid.trim();
    }

    public Object getDatainfo() {
        return datainfo;
    }

    public void setDatainfo(Object datainfo) {
        this.datainfo = datainfo == null ? null : datainfo;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime == null ? null : createtime.trim();
    }
}