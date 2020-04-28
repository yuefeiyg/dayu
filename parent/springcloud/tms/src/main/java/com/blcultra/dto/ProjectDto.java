package com.blcultra.dto;

import java.util.List;
import java.util.Map;

/**
 * Created by sgy05 on 2019/3/25.
 */
public class ProjectDto {

    private String projectid;

    private String projectowner;

    private String projectname;

    private String projectdesc;

    private String note;

    private String pmanager;

    private List<Map<String,String>> auditorperformers;

    private String members;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPmanager() {
        return pmanager;
    }

    public void setPmanager(String pmanager) {
        this.pmanager = pmanager;
    }

    public List<Map<String, String>> getAuditorperformers() {
        return auditorperformers;
    }

    public void setAuditorperformers(List<Map<String, String>> auditorperformers) {
        this.auditorperformers = auditorperformers;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getProjectowner() {
        return projectowner;
    }

    public void setProjectowner(String projectowner) {
        this.projectowner = projectowner;
    }

    @Override
    public String toString() {
        return "ProjectDto{" +
                "projectid='" + projectid + '\'' +
                ", projectowner='" + projectowner + '\'' +
                ", projectname='" + projectname + '\'' +
                ", projectdesc='" + projectdesc + '\'' +
                ", note='" + note + '\'' +
                ", pmanager='" + pmanager + '\'' +
                ", auditorperformers=" + auditorperformers +
                ", members='" + members + '\'' +
                '}';
    }
}
