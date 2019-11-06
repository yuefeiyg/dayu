package com.blcultra.model;

public class AnnotationLabelRelationRule {
    private String labelid;

    private String arg1type;

    private String arg2type;

    private String templateid;

    public String getLabelid() {
        return labelid;
    }

    public void setLabelid(String labelid) {
        this.labelid = labelid == null ? null : labelid.trim();
    }

    public String getTemplateid() {
        return templateid;
    }

    public void setTemplateid(String templateid) {
        this.templateid = templateid;
    }

    public String getArg1type() {
        return arg1type;
    }

    public void setArg1type(String arg1type) {
        this.arg1type = arg1type == null ? null : arg1type.trim();
    }

    public String getArg2type() {
        return arg2type;
    }

    public void setArg2type(String arg2type) {
        this.arg2type = arg2type == null ? null : arg2type.trim();
    }
}