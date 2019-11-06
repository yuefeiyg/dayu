package com.blcultra.model;

public class DataInfo {
    private String dataid;

    private String dsid;

    private String dataname;

    private String dataobjecttype;

    private String size;

    private String scenariotype;

    private String creator;

    private String owner;

    private String createtime;

    private String datastate;

    private String datapath;

    private String segmentation;

    private String datausingstate;

    private String datatype;

    private String note;

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getDataid() {
        return dataid;
    }

    public void setDataid(String dataid) {
        this.dataid = dataid == null ? null : dataid.trim();
    }

    public String getDsid() {
        return dsid;
    }

    public void setDsid(String dsid) {
        this.dsid = dsid == null ? null : dsid.trim();
    }

    public String getDataname() {
        return dataname;
    }

    public void setDataname(String dataname) {
        this.dataname = dataname == null ? null : dataname.trim();
    }

    public String getDataobjecttype() {
        return dataobjecttype;
    }

    public void setDataobjecttype(String dataobjecttype) {
        this.dataobjecttype = dataobjecttype == null ? null : dataobjecttype.trim();
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size == null ? null : size.trim();
    }

    public String getScenariotype() {
        return scenariotype;
    }

    public void setScenariotype(String scenariotype) {
        this.scenariotype = scenariotype == null ? null : scenariotype.trim();
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner == null ? null : owner.trim();
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime == null ? null : createtime.trim();
    }

    public String getDatastate() {
        return datastate;
    }

    public void setDatastate(String datastate) {
        this.datastate = datastate == null ? null : datastate.trim();
    }

    public String getDatapath() {
        return datapath;
    }

    public void setDatapath(String datapath) {
        this.datapath = datapath == null ? null : datapath.trim();
    }

    public String getSegmentation() {
        return segmentation;
    }

    public void setSegmentation(String segmentation) {
        this.segmentation = segmentation == null ? null : segmentation.trim();
    }

    public String getDatausingstate() {
        return datausingstate;
    }

    public void setDatausingstate(String datausingstate) {
        this.datausingstate = datausingstate == null ? null : datausingstate.trim();
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }
}