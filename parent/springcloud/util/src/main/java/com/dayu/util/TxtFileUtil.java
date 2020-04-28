package com.dayu.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TxtFileUtil {

    private InputStream eis;

    /**
     * 将list数据写入txt文件中
     * @param sentences
     * @param path
     */
    public static void writeListDataToTxt(List<String> sentences,String path){
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(path));
            for (String s : sentences) {
                bw.write(s);
                bw.newLine();
                bw.flush();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != bw){
                try{
                    bw.close();
                }catch (Exception e){

                }

            }
        }
    }
    public static void writeStringDataToTxt(String sentence,String path){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            bw.write(sentence);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     *
     * @param filename 文件路径
     * @param charsetName 设置字符集
     * @return 字符串列表
     */
    public static List<String> readTxt2listCharset(String filename,String charsetName) {
        String filePath = filename; // 文件和该类在同个目录下
        List<String> list = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charsetName)); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
            String str = null;
            while ((str = reader.readLine()) != null) {
                //System.out.println(str);
                if (str!=null || !"".equals(str)){//空行跳过
                    list.add(str);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
    /**
     * 读取txt文本
     * @param filepath
     * @param character
     * @return
     */
    public static List<String> ReadTxtFile(String filepath, String character){
        String filePath = filepath; // 文件和该类在同个目录下
        List<String> list = new ArrayList<>();
        BufferedReader reader = null;

        try {
            if(null != character){
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),character)); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
            }else{
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath))); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
            }

            String str = null;
            while ((str = reader.readLine()) != null) {
               list.add(str);
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
    /**
     * 读取txt文本
     * @param filepath
     * @param character
     * @return
     */
    public static List<List<String>> ReadTxtFileToListBySentence(String filepath, String character){
        String filePath = filepath; // 文件和该类在同个目录下
        List<String> list = new ArrayList<>();
        List<List<String>> returnList = new ArrayList<>();
        BufferedReader reader = null;

        try {
            if(null != character){
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),character)); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
            }else{
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath))); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
            }

            String str = null;
            while ((str = reader.readLine()) != null) {
                if(str.equals("")){//遇到空行
                    if(list.size() > 0){
                        returnList.add(list);
                    }
                    list = new ArrayList<>();
                }else{
                    list.add(str);
                }
            }
            if(list.size() > 0){
                returnList.add(list);
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnList;
    }

    /**
     * 读取txt文本
     * @param character
     * @return
     */
    public static List<List<String>> ReadTxtFile(File file, String character){
        List<String> list = new ArrayList<>();
        List<List<String>> returnList = new ArrayList<>();
        BufferedReader reader = null;

        try {
            if(null != character){
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),character)); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
            }else{
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file))); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
            }

            String str = null;
            while ((str = reader.readLine()) != null) {
                if(str.equals("")){//遇到空行
                    if(list.size() > 0){
                        returnList.add(list);
                    }
                    list = new ArrayList<>();
                }else{
                    list.add(str);
                }
            }
            returnList.add(list);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnList;
    }

    /**
     * 判断txt文件编码
     * @param file
     * @return
     * @throws Exception
     */
    public static String checkTxtCharset(File file) throws Exception{
        InputStream inputStream = null;
        // 判断的文件输入流
         inputStream = new FileInputStream(file);
         byte[] head = new byte[3];
         inputStream.read(head); //判断TXT文件编码格式
         if (head[0] == -1 && head[1] == -2 ){
//         Unicode         -1,-2,84
             return "Unicode";
         }else if (head[0] == -2 && head[1] == -1 ){
             return "UTF-16";
             //Unicode big endian   -2,-1,0,84
         }else if(head[0]==-17 && head[1]==-69 && head[2] ==-65) {
             return "UTF-8";
//             UTF-8                -17,-69,-65,84
         }else { //ANSI                  84 = T
            return "gb2312";
         }

    }

    /**
     * 计算文本的字数
     * @param file
     * @return
     */
    public static Map<String,Integer> wordCount(InputStream file){
        BufferedReader reader = null;
        int wordcount = 0;
        int part = 1;
        Map<String,Integer> map = new HashMap<>(2);
        try {
            reader = new BufferedReader(new InputStreamReader(file,"gbk")); // 指定读取文件的编码格式，要和写入的格式一致，以免出现中文乱码,
            String str = null;

            //unicode码 参考：https://blog.csdn.net/leopadus/article/details/68961344
//            String Reg="^[\u4e00-\u9fa5|\\！|\\,|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】|\uff01-\uff5e|\u00A0,\u0020,\u3000|" +
//                    "\u3000-\u303F|\u3200-\u32FF|\uFE30-\uFE4F]$";

            while ((str = reader.readLine()) != null) {
                if(StringUtil.empty(str)){
                    part ++;
                }else{
                    for(int i=0;i<str.length();i++){
//                        String b=Character.toString(str.charAt(i));
//                        if(b.matches(Reg))
//                            wordcount++;
                        char c = str.charAt(i);
                        if(c > 255){
                            wordcount ++;
                        }
                    }
                }
            }
            map.put("wordcount",wordcount);
            map.put("part",part);

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static Map<String,Integer> wordCounts() throws IOException {

        File file = new File("E:\\BLCUpload\\origindata\\864.AC00494.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"gbk"));

//         BufferedReader br = new BufferedReader(
//                 new FileReader(new File("E:\\BLCUpload\\origindata\\864.AC00494.txt")));
         String tempstr; //临时字符串
         int num_of_words = 0; //总汉字数
         int num_of_wordsAndPunctuation = 0; //汉字+标点
         int num_blank = 0;
        // 空格字符
         Pattern pattern = Pattern.compile("([\u4e00-\u9fa5]{1})");
        // 定义匹配模式:1个汉字
         Pattern pattern2 = Pattern.compile("([\u4e00-\u9fa5,，.。、/<>?？;；'‘’:\"【】{}]{1})");
         //定义匹配模式：汉字或标点符号
         Pattern pattern3 = Pattern.compile("[\\s]");
         while((tempstr = br.readLine()) != null && tempstr != ""){
            // 汉字匹配，统计字数
             Matcher matcher = pattern.matcher(tempstr);
             while(matcher.find()) num_of_words++;
             //汉字标点匹配，统计字数
             Matcher matcher2 = pattern2.matcher(tempstr);
             while(matcher2.find()) num_of_wordsAndPunctuation++;
             // 空格匹配，统计字数
             Matcher matcher3 = pattern3.matcher(tempstr);
             while(matcher3.find()) num_blank++;
             tempstr = "";
         }
         br.close();
         //关闭文件
         System.out.println("总汉字数：" + num_of_words);
         System.out.println("总汉字标点数：" + num_of_wordsAndPunctuation); System.out.println("总空格数：" + num_blank);
        return null;
    }

    public static  boolean writeToTempFiles(List<String> sentences,String textname,String tasktitle,String path){
        try {
            File file = new File(path);
            //判断文件是否存在;
            if (!file.exists()) {
                //创建文件;
                file.mkdirs();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(path+"\\"+textname));
            for (String sentece:sentences){
                bw.write(sentece);
                bw.newLine();
                bw.flush();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }


}