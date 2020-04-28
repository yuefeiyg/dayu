package com.blcultra.model;

public class AuditorPerformer {
    private String auditorid;

    private String performerid;

    private String projectid;

    public String getAuditorid() {
        return auditorid;
    }

    public void setAuditorid(String auditorid) {
        this.auditorid = auditorid == null ? null : auditorid.trim();
    }

    public String getPerformerid() {
        return performerid;
    }

    public void setPerformerid(String performerid) {
        this.performerid = performerid == null ? null : performerid.trim();
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid == null ? null : projectid.trim();
    }
}