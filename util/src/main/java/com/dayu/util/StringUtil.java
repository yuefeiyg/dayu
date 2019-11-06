package com.dayu.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能描述:
 * 关于String 类型的操作的util方法
 * @param:
 * @return:
 * @auther: guyuefei
 * @date:
 */

public class StringUtil {

    /**
     * 验证字符串是否为空
     *
     * @param param
     * @return
     */
    public static boolean empty(String param) {
        return param == null || "".equals(param) || param.trim().length() < 1 || param.equals("null");
    }

    /**
     * 比较两个字符串是否相等
     * @param str1、str2  待比较字符窜
     */
    public static boolean isSame(String str1, String str2){
        if(empty(str1) && empty(str2))
            return true;
        else if(!empty(str1) && !empty(str2))
            return str1.equals(str2);
        else
            return false;
    }

    /**
     * 生成主键uuid
     *
     * @return
     */
    public static String getUUID() {
        //UUID.randomUUID()生成的32位字符串每隔8位中间以 “-”连接，去掉“-”换成“”
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 替换特殊字符
     * @param word
     * @return
     */
    public static String regAndReplace(String word) {
        String patten = "\\[t,n]+";
        return word.replaceAll(patten,"")
                .trim();
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 正则匹配
     * @param str
     * @param reg
     * @return
     */
    public static String regx(String str,String reg) {
        Pattern pattern = Pattern.compile(reg);// 匹配的模式
        Matcher m = pattern.matcher(str);
        String total = "";
        if (m.find()){
            total = m.group(1);
        }
        return total;
    }

    /**
     * 正则匹配字符串//后续放到StringUtil中
     * @param content
     * @param pattern
     * @return
     */
    public static List<String> find(String content,String pattern){
        Pattern mpattern = Pattern.compile(pattern);
        Matcher mmatcher = mpattern.matcher(content);
        List<String> results = new ArrayList<>();
        while (mmatcher.find()) {
            int total = mmatcher.groupCount();
            for (int i = 1; i <= total; i++) {
                results.add(mmatcher.group(i));
            }
        }
        return results;
    }

    /**
     * 计算中文字符数
     * @param string
     * @return
     * @throws Exception
     */
    public static int countChineseInString(String string) throws Exception{
        int wordcount = 0;
        for(int i=0;i<string.length();i++){
//                        String b=Character.toString(str.charAt(i));
//                        if(b.matches(Reg))
//                            wordcount++;
            char c = string.charAt(i);
            if(c > 255){
                wordcount ++;
            }
        }
        return  wordcount;
    }

    /**
     * 字符串拼接
     * @param strings
     * @return
     */
    public static String concat(String ... strings){
       StringBuilder stringBuilder = new StringBuilder(strings.length);
       for(String string : strings){
           stringBuilder.append(string);
       }
        return stringBuilder.toString();
    }

    /**
     * 按照截取字符串截取左面字符
     * @param str
     * @param match
     * @return
     */
    public static String leftsubstringmatch(String str,String match){

        if(str.contains(match)){
            return str.substring(0,str.indexOf(match));

        }
        return str;

    }


    /**
     * 按照截取字符串截取右面字符
     * @param str
     * @param match
     * @return
     */
    public static String rightsubstringmatch(String str,String match){

        if(str.contains(match)){
            return str.substring(str.indexOf(match) + match.length());

        }
        return str;

    }

}
