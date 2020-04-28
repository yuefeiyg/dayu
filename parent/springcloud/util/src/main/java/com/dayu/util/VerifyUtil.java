package com.dayu.util;

import java.util.regex.Pattern;


/**
 * 校验工具类
 * Created by sgy05 on 2018/11/27.
 */
public class VerifyUtil {

    /**
     * 正则表达式：只能输入数字
     */
    private static final String REGEX_NUMBER = "^\\d+$";

    /**
     * 正则表达式：只能输入n个数字
     */
    private static final String REGEX_NUMBER_LENGTH = "^\\d{%d}$";

    /**
     * 正则表达式：只能由英文、数字、下划线组成
     */
    private static final String REGEX_USERNAME = "^\\w+$";

    /**
     * 正则表达式：验证密码
     */
    private static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,16}$";

    /**
     * 正则表达式：验证手机号
     */
    private static final String REGEX_MOBILE = "^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$";

    /**
     * 正则表达式：验证邮箱
     */
    private static final String REGEX_EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

    /**
     * 正则表达式：验证汉字
     */
    private static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";

    /**
     * 正则表达式：验证身份证
     */
    private static final String REGEX_ID_CARD = "^(^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$)|(^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}[Xx])$)$";

    /**
     * 正则表达式：验证URL
     */
    private static final String REGEX_URL = "^https?:\\/\\/(([a-zA-Z0-9_-])+(\\.)?)*(:\\d+)?(\\/((\\.)?(\\?)?=?&?[a-zA-Z0-9_-](\\?)?)*)*$";

    /**
     * 正则表达式：验证IP地址
     */
    private static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";

    /**
     * 正则表达式：邮编
     */
    private static final String REGEX_ZIPCODE = "^[1-9]\\d{5}(?!\\d)$";

    /**
     * 正则表达式：日期 yyyyMMdd
     */
    private static final String REGEX_DATE = "^\\d{4}((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3([0|1])))$";

    /**
     * 正则表达式：时间
     */
    private static final String REGEX_TIME = "(([01]\\d)|(2[0-3]))[0-5]\\d([0-5]\\d)?";

    /**
     * 正则表达式：日期时间
     */
    private static final String REGEX_DATETIME = "^\\d{4}((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3([0|1])))((0[0-9])|([1-2][0-9]))((0[0-9])|([1-5][0-9]))((0[0-9])|([1-5][0-9]))$";

    /**
     * 校验是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    /**
     * 校验用户名
     *
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUsername(String username) {
        return Pattern.matches(REGEX_USERNAME, username);
    }

    /**
     * 校验密码
     *
     * @param password
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }

    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    /**
     * 校验邮箱
     *
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    /**
     * 校验汉字
     *
     * @param chinese
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isChinese(String chinese) {
        return Pattern.matches(REGEX_CHINESE, chinese);
    }

    /**
     * 校验身份证
     *
     * @param idCard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }

    /**
     * 校验URL
     *
     * @param url
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUrl(String url) {
        return Pattern.matches(REGEX_URL, url);
    }

    /**
     * 校验IP地址
     *
     * @param ipAddr
     * @return
     */
    public static boolean isIPAddr(String ipAddr) {
        return Pattern.matches(REGEX_IP_ADDR, ipAddr);
    }


    /**
     * 校验数字
     *
     * @param number
     * @return
     */
    public static boolean isNumber(String number) {
        return Pattern.matches(REGEX_NUMBER, number);
    }

    /**
     * 校验N个数字
     *
     * @param number
     * @param length
     * @return
     */
    public static boolean isNumberLength(String number, int length) {
        return Pattern.matches(String.format(REGEX_NUMBER_LENGTH, length), number);
    }

    /**
     * 校验邮编
     *
     * @param zipCode
     * @return
     */
    public static boolean isZipCode(String zipCode) {
        return Pattern.matches(REGEX_ZIPCODE, zipCode);
    }

    /**
     * 校验日期格式
     *
     * @param date
     * @return
     */
    public static boolean isDate(String date) {
        return Pattern.matches(REGEX_DATE, date);
    }

    /**
     * 校验时间格式
     *
     * @param time
     * @return
     */
    public static boolean isTime(String time) {
        return Pattern.matches(REGEX_TIME, time);
    }

    /**
     * 校验日期时间格式
     *
     * @param dateTime
     * @return
     */
    public static boolean isDateTime(String dateTime) {
        return Pattern.matches(REGEX_DATETIME, dateTime);
    }
}
