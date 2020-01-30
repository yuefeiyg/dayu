package com.blcultra.model;

public class DatasetInfo {
    private String dsid;

    private String dsname;

    private String dataobjecttype;

    private String scenariotype;

    private String dsowner;

    private String dscreatetime;

    private String dsstate;

    private String dsusingstate;

    private String note;

    public String getDsid() {
        return dsid;
    }

    public void setDsid(String dsid) {
        this.dsid = dsid == null ? null : dsid.trim();
    }

    public String getDsname() {
        return dsname;
    }

    public void setDsname(String dsname) {
        this.dsname = dsname == null ? null : dsname.trim();
    }

    public String getDataobjecttype() {
        return dataobjecttype;
    }

    public void setDataobjecttype(String dataobjecttype) {
        this.dataobjecttype = dataobjecttype == null ? null : dataobjecttype.trim();
    }

    public String getScenariotype() {
        return scenariotype;
    }

    public void setScenariotype(String scenariotype) {
        this.scenariotype = scenariotype == null ? null : scenariotype.trim();
    }

    public String getDsowner() {
        return dsowner;
    }

    public void setDsowner(String dsowner) {
        this.dsowner = dsowner == null ? null : dsowner.trim();
    }

    public String getDscreatetime() {
        return dscreatetime;
    }

    public void setDscreatetime(String dscreatetime) {
        this.dscreatetime = dscreatetime == null ? null : dscreatetime.trim();
    }

    public String getDsstate() {
        return dsstate;
    }

    public void setDsstate(String dsstate) {
        this.dsstate = dsstate == null ? null : dsstate.trim();
    }

    public String getDsusingstate() {
        return dsusingstate;
    }

    public void setDsusingstate(String dsusingstate) {
        this.dsusingstate = dsusingstate == null ? null : dsusingstate.trim();
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }
}