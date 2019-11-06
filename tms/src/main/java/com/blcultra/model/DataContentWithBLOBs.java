package com.blcultra.model;

public class DataContentWithBLOBs extends DataContent {
    private String content;

    private String anninfos;

    private String annrelateinfos;

    public String getAnnrelateinfos() {
        return annrelateinfos;
    }

    public void setAnnrelateinfos(String annrelateinfos) {
        this.annrelateinfos = annrelateinfos == null ? null : annrelateinfos.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getAnninfos() {
        return anninfos;
    }

    public void setAnninfos(String anninfos) {
        this.anninfos = anninfos == null ? null : anninfos.trim();
    }
}