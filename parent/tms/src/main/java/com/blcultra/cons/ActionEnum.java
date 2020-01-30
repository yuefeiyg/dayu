package com.blcultra.cons;

public enum ActionEnum {
    ADD("add","新建"),SAVE("save","保存"),RESET("reset","重置"),DOWNLOAD("download","下载"),
    VIEW("view","查看"),EDIT("edit","编辑"),DELETE("delete","删除"),RECEIVE("receive","领取"),
    START("start","开始"),PAUSE("pause","暂停"),ENTER("enter","进入"),REVERSE("reverse","流转"),
    RECALL("recall","撤回"),INVALIDATE("invalidate","报废"),QUERY("query","查询"),COMMIT("commit","提交"),
    MODIFYPROJECTADMIN("modifyprojectadmin","修改项目管理员"),RESETPASS("resetpass","重置密码"),
    UPLOAD("upload","上传"),STATISTICS("statistics","统计"),INVALIDREVIEW("invalidreview","审核无效");


    private final String key;
    private final String value;

    private ActionEnum(String key, String value){
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }

}
