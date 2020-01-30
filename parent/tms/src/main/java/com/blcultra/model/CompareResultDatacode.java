package com.blcultra.model;

public class CompareResultDatacode {
    private String resultid;

    private String datacode;
    private String datacodefilepath;

    public String getDatacodefilepath() {
        return datacodefilepath;
    }

    public void setDatacodefilepath(String datacodefilepath) {
        this.datacodefilepath = datacodefilepath;
    }

    public String getResultid() {
        return resultid;
    }

    public void setResultid(String resultid) {
        this.resultid = resultid == null ? null : resultid.trim();
    }

    public String getDatacode() {
        return datacode;
    }

    public void setDatacode(String datacode) {
        this.datacode = datacode == null ? null : datacode.trim();
    }
}