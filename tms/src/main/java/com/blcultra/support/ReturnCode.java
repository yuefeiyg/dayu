package com.blcultra.support;
//
public enum ReturnCode {
    SUCESS_CODE_0000("0000","操作成功"),

    //系统错误
    ERROR_CODE_1111("1111","系统繁忙"),
    //数据错误
    ERROR_CODE_1001("1001","请求参数错误"),


    //用户错误
    ERROR_CODE_0005("2001","用户不存在"),
    ERROR_CODE_2002("2002","用户未登陆"),
    ERROR_CODE_2003("2003","密码错误"),
    ERROR_CODE_2004("2004","用户账户已被禁用"),
    ERROR_CODE_2005("2005","用户已存在"),
    ERROR_CODE_2006("2006","密码输入有误"),
    ERROR_CODE_2007("2007","项目不存在"),
    ERROR_CODE_2008("2008","项目已存在"),
    //认证错误
    ERROR_CODE_4001("4001","token过期"),
    ERROR_CODE_4002("4002","非法请求"),

    ERROR_CODE_5001("5001","暂无权限操作");

     ReturnCode(String key, String value){
        this.key = key;
        this.value = value;
    }
    private String key;

    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
