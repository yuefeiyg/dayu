package com.blcultra.model;

public class CompareResultDataSources {
    private String resultid;

    private String dataid;

    public String getResultid() {
        return resultid;
    }

    public void setResultid(String resultid) {
        this.resultid = resultid == null ? null : resultid.trim();
    }

    public String getDataid() {
        return dataid;
    }

    public void setDataid(String dataid) {
        this.dataid = dataid == null ? null : dataid.trim();
    }
}