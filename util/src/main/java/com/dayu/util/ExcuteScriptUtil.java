package com.dayu.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExcuteScriptUtil {
    /**
     *
     * @param compareurl 需要对比的文件目录地址
     * @param resultpath   比对完成后结果文件的存储地址
     * @param scriptpath  Perl脚本存储路径
     * @return
     */
    public static  List<String> fileComparison(String compareurl, String resultpath,String scriptpath){
        List<String> reslist =null;
        try {
            String commandStr = new String(
                    "perl  "+ scriptpath+" "+compareurl+" "+resultpath);
            Process pr = Runtime.getRuntime().exec(commandStr);

//            String[] args1 = new String[] {"perl "+ scriptpath,compareurl,resultpath};
//            Process pr=Runtime.getRuntime().exec(args1);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    pr.getInputStream()));
            String line;
            reslist = new ArrayList<>();
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                if (line==null || line.equals("")){
                    reslist.add(line);
                }
            }
            in.close();
            pr.waitFor();
            System.out.println("end");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reslist;
    }

    public static void main(String[] args) {
//        fileComparison();

    }

}
