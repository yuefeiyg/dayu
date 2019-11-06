package com.blcultra.model;

public class AnnotationObjectRelationInfo {
    private String relationdataid;

    private String objectdataid;

    private String labelid;

    private String fromitemid;

    private String toitemid;

    private String dataid;
    private String contentid;

    public String getDataid() {
        return dataid;
    }

    public void setDataid(String dataid) {
        this.dataid = dataid;
    }

    public String getContentid() {
        return contentid;
    }

    public void setContentid(String contentid) {
        this.contentid = contentid;
    }

    public String getRelationdataid() {
        return relationdataid;
    }

    public void setRelationdataid(String relationdataid) {
        this.relationdataid = relationdataid == null ? null : relationdataid.trim();
    }

    public String getObjectdataid() {
        return objectdataid;
    }

    public void setObjectdataid(String objectdataid) {
        this.objectdataid = objectdataid == null ? null : objectdataid.trim();
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