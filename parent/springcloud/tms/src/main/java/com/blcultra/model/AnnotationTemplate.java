package com.blcultra.model;

public class AnnotationTemplate {
    private String templateid;

    private String templatename;

    private String templatetype;

    private String annotationobject;

    private String annotationtype;

    private String actions;

    private String modules;

    private String auxshortcutsettings;

    public String getModules() {
        return modules;
    }

    public void setModules(String modules) {
        this.modules = modules;
    }

    public String getAuxshortcutsettings() {
        return auxshortcutsettings;
    }

    public void setAuxshortcutsettings(String auxshortcutsettings) {
        this.auxshortcutsettings = auxshortcutsettings;
    }

    private String createuserid;

    private String createdate;

    private String updatedate;

    private Integer state;

    private String note;

    public String getTemplateid() {
        return templateid;
    }

    public void setTemplateid(String templateid) {
        this.templateid = templateid == null ? null : templateid.trim();
    }

    public String getTemplatename() {
        return templatename;
    }

    public void setTemplatename(String templatename) {
        this.templatename = templatename == null ? null : templatename.trim();
    }

    public String getTemplatetype() {
        return templatetype;
    }

    public void setTemplatetype(String templatetype) {
        this.templatetype = templatetype == null ? null : templatetype.trim();
    }

    public String getAnnotationobject() {
        return annotationobject;
    }

    public void setAnnotationobject(String annotationobject) {
        this.annotationobject = annotationobject == null ? null : annotationobject.trim();
    }

    public String getAnnotationtype() {
        return annotationtype;
    }

    public void setAnnotationtype(String annotationtype) {
        this.annotationtype = annotationtype == null ? null : annotationtype.trim();
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions == null ? null : actions.trim();
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }


}