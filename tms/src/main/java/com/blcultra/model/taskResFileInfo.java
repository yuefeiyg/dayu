package com.blcultra.model;

public class taskResFileInfo {
    private String dataid;

    private String dataname;

    private String path;

    private String taskid;

    private String filetype;

    private String state;

    private String createtime;

    public String getDataid() {
        return dataid;
    }

    public void setDataid(String dataid) {
        this.dataid = dataid == null ? null : dataid.trim();
    }

    public String getDataname() {
        return dataname;
    }

    public void setDataname(String dataname) {
        this.dataname = dataname == null ? null : dataname.trim();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path == null ? null : path.trim();
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid == null ? null : taskid.trim();
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getState(){return state;}

    public void setState(String state){this.state = state;}

    public String getCreatetime(){return  createtime;}

    public void setCreatetime(String createtime){this.createtime = createtime;}
}