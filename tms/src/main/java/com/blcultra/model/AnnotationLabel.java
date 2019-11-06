package com.blcultra.model;

public class AnnotationLabel {
    private String labelid;

    private String templateid;

    private String labelname;

    private String shortcut;

    private String colour;

    private String symbol;

    private String code;

    private Integer depth;

    private String isleaf;

    private String parentlabelid;

    private String relationlabel;

    private String createuserid;

    private String createdate;

    private String updatedate;

    private Integer labelstate;

    public String getLabelid() {
        return labelid;
    }

    public void setLabelid(String labelid) {
        this.labelid = labelid == null ? null : labelid.trim();
    }

    public String getTemplateid() {
        return templateid;
    }

    public void setTemplateid(String templateid) {
        this.templateid = templateid == null ? null : templateid.trim();
    }

    public String getLabelname() {
        return labelname;
    }

    public void setLabelname(String labelname) {
        this.labelname = labelname == null ? null : labelname.trim();
    }

    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut == null ? null : shortcut.trim();
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour == null ? null : colour.trim();
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public String getIsleaf() {
        return isleaf;
    }

    public void setIsleaf(String isleaf) {
        this.isleaf = isleaf == null ? null : isleaf.trim();
    }

    public String getParentlabelid() {
        return parentlabelid;
    }

    public void setParentlabelid(String parentlabelid) {
        this.parentlabelid = parentlabelid == null ? null : parentlabelid.trim();
    }

    public String getRelationlabel() {
        return relationlabel;
    }

    public void setRelationlabel(String relationlabel) {
        this.relationlabel = relationlabel == null ? null : relationlabel.trim();
    }

    public String getCreateuserid() {
        return createuserid;
    }

    public void setCreateuserid(String createuserid) {
        this.createuserid = createuserid == null ? null : createuserid.trim();
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate == null ? null : createdate.trim();
    }

    public String getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(String updatedate) {
        this.updatedate = updatedate == null ? null : updatedate.trim();
    }

    public Integer getLabelstate() {
        return labelstate;
    }

    public void setLabelstate(Integer labelstate) {
        this.labelstate = labelstate;
    }
}