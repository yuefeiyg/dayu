package com.blcultra.model;

public class AnnotationObjectInfo {
    private String dataitemid;

    private String itemid;

    private String objectdataid;

    private String item;

    private String labelid;

    private String startoffset;

    private String endoffset;

    private String bndbox;

    private String createtime;

    private String dataid ; //文本id

    private String contentid;//切分单元的id，若句子切分就是句子id（data_content表中的contentid）

    public String getDataid() {
        return dataid;
    }

    public void setDataid(String dataid) {
        this.dataid = dataid;
    }

    public String getContentid() {
        return contentid;
    }

    public void setContentid(String contentid) {
        this.contentid = contentid;
    }

    public String getDataitemid() {
        return dataitemid;
    }

    public void setDataitemid(String dataitemid) {
        this.dataitemid = dataitemid == null ? null : dataitemid.trim();
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid == null ? null : itemid.trim();
    }

    public String getObjectdataid() {
        return objectdataid;
    }

    public void setObjectdataid(String objectdataid) {
        this.objectdataid = objectdataid == null ? null : objectdataid.trim();
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item == null ? null : item.trim();
    }

    public String getLabelid() {
        return labelid;
    }

    public void setLabelid(String labelid) {
        this.labelid = labelid == null ? null : labelid.trim();
    }

    public String getStartoffset() {
        return startoffset;
    }

    public void setStartoffset(String startoffset) {
        this.startoffset = startoffset == null ? null : startoffset.trim();
    }

    public String getEndoffset() {
        return endoffset;
    }

    public void setEndoffset(String endoffset) {
        this.endoffset = endoffset == null ? null : endoffset.trim();
    }

    public String getBndbox() {
        return bndbox;
    }

    public void setBndbox(String bndbox) {
        this.bndbox = bndbox == null ? null : bndbox.trim();
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}