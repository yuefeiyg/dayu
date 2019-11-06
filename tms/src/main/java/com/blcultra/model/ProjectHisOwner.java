package com.blcultra.model;

public class ProjectHisOwner {
    private Integer id;

    private String projectid;

    private String formerowner;

    private String departuretime;

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

    public String getFormerowner() {
        return formerowner;
    }

    public void setFormerowner(String formerowner) {
        this.formerowner = formerowner == null ? null : formerowner.trim();
    }

    public String getDeparturetime() {
        return departuretime;
    }

    public void setDeparturetime(String departuretime) {
        this.departuretime = departuretime == null ? null : departuretime.trim();
    }
}