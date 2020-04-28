package com.blcultra.model;

public class UserBehaviorLogWithBLOBs extends UserBehaviorLog {
    private String content;

    private String beforecontent;

    private String beforesymbolcontent;

    private String symbolcontent;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getBeforecontent() {
        return beforecontent;
    }

    public void setBeforecontent(String beforecontent) {
        this.beforecontent = beforecontent == null ? null : beforecontent.trim();
    }

    public String getBeforesymbolcontent() {
        return beforesymbolcontent;
    }

    public void setBeforesymbolcontent(String beforesymbolcontent) {
        this.beforesymbolcontent = beforesymbolcontent == null ? null : beforesymbolcontent.trim();
    }

    public String getSymbolcontent() {
        return symbolcontent;
    }

    public void setSymbolcontent(String symbolcontent) {
        this.symbolcontent = symbolcontent == null ? null : symbolcontent.trim();
    }

    @Override
    public String toString() {
        return "UserBehaviorLogWithBLOBs{" +
                "content='" + content + '\'' +
                ", beforecontent='" + beforecontent + '\'' +
                ", beforesymbolcontent='" + beforesymbolcontent + '\'' +
                ", symbolcontent='" + symbolcontent + '\'' +
                '}';
    }
}