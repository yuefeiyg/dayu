package com.blcultra.model;

public class ProjectUser {
    private Integer id;

    private String projectid;

    private String member;

    private String username;

    private String prolekey;

    public String getProlekey() {
        return prolekey;
    }

    public void setProlekey(String prolekey) {
        this.prolekey = prolekey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid == null ? null : projectid.trim();
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member == null ? null : member.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}