package com.dayu.util;

import java.io.*;

/**
 * Created by sgy05 on 2019/2/26.
 */
public class ImageCopyUtil {

    public static void copyImage(String originurl,String newurl){
        //1.1  创建图片存放路径
        File filefrom=new File(originurl);
        //1.2  创建图片移动后存放的路径
        File fileto=new File(newurl);
        InputStream input=null;
        OutputStream output=null;
        try {
            //将原本图片文件路径作为输入流
            input=new FileInputStream(filefrom);
            //将移动后图片路径作为输出流
            output=new FileOutputStream(fileto);
            byte[] by=new byte[1024];
            //遍历读写
            try {
                while(input.read()!=-1){
                    output.write(by);
                }
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally{
            try {
                //关闭流
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("拷贝成功！！");
        }
    }
}
