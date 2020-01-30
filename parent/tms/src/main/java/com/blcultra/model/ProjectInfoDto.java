package com.blcultra.model;

import java.util.List;

/**
 * Created by sgy05 on 2019/3/7.
 */
public class ProjectInfoDto {

    private String projectid;

    private String projectname;

    private String projectdesc;

    private String projectowner;

    private String createtime;

    private String updatetime;

    private String projectstate;

    private String note;

    private List<User> users;

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public String getProjectdesc() {
        return projectdesc;
    }

    public void setProjectdesc(String projectdesc) {
        this.projectdesc = projectdesc;
    }

    public String getProjectowner() {
        return projectowner;
    }

    public void setProjectowner(String projectowner) {
        this.projectowner = projectowner;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getProjectstate() {
        return projectstate;
    }

    public void setProjectstate(String projectstate) {
        this.projectstate = projectstate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "ProjectInfoDto{" +
                "projectid='" + projectid + '\'' +
                ", projectname='" + projectname + '\'' +
                ", projectdesc='" + projectdesc + '\'' +
                ", projectowner='" + projectowner + '\'' +
                ", createtime='" + createtime + '\'' +
                ", updatetime='" + updatetime + '\'' +
                ", projectstate='" + projectstate + '\'' +
                ", note='" + note + '\'' +
                ", users=" + users +
                '}';
    }
}
