package com.blcultra.model;

public class ProjectInfo {
    private String projectid;

    private String projectname;

    private String projectdesc;

    private String projectowner;

    private String createtime;

    private String updatetime;

    private String projectstate;

    private String note;

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid == null ? null : projectid.trim();
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname == null ? null : projectname.trim();
    }

    public String getProjectdesc() {
        return projectdesc;
    }

    public void setProjectdesc(String projectdesc) {
        this.projectdesc = projectdesc == null ? null : projectdesc.trim();
    }

    public String getProjectowner() {
        return projectowner;
    }

    public void setProjectowner(String projectowner) {
        this.projectowner = projectowner == null ? null : projectowner.trim();
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime == null ? null : createtime.trim();
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime == null ? null : updatetime.trim();
    }

    public String getProjectstate() {
        return projectstate;
    }

    public void setProjectstate(String projectstate) {
        this.projectstate = projectstate == null ? null : projectstate.trim();
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }
}