package com.blcultra.util;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class DownloadUtil {

    /**
     * 将传入字符串在浏览器下载
     * @param response
     * @return
     */
    public static boolean downloadDataSourceTxtFile(String filename,String content,String character, HttpServletResponse response) throws  Exception{
        boolean boo = false;
        try {
            OutputStream os = response.getOutputStream();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream; charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment; filename=\""+
                    new String(filename.getBytes("UTF-8"),"UTF-8")+"\";");
            os = response.getOutputStream();//输出流
            os.write(content.getBytes());//向客户端输出，实际是把数据存放在response中，然后web服务器再去response中读取
            os.close();
            return true;
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return boo;
    }

    public static boolean downloadzipfile(String txttemppath,String zipname,HttpServletResponse response){
        boolean boo = false;
        try {
            InputStream in =  in = new FileInputStream(txttemppath+"//"+zipname); //获取文件的流
            OutputStream os = response.getOutputStream();
            int len = 0;
            byte buf[] = new byte[1024];//缓存作用
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream; charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment; filename=\""+
                    new String(zipname.getBytes("UTF-8"),"UTF-8")+"\";");
            os = response.getOutputStream();//输出流
            while( (len = in.read(buf)) > 0 ) //切忌这后面不能加 分号 ”;“
            {
                os.write(buf, 0, len);//向客户端输出，实际是把数据存放在response中，然后web服务器再去response中读取
            }
            in.close();
            os.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return boo;
    }

    /**
     * 将磁盘指定位置文件下载
     * @param filepath
     * @param response
     * @return
     */
    public static boolean downloadFile(String filepath, HttpServletResponse response){
        boolean boo = false;
        try {
            String filename = filepath.substring(filepath.lastIndexOf("/")+1,filepath.length());
            InputStream in =  in = new FileInputStream(filepath); //获取文件的流
            OutputStream os = response.getOutputStream();
            int len = 0;
            byte buf[] = new byte[1024];//缓存作用
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream; charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment; filename=\""+
                    new String(filename.getBytes("UTF-8"),"UTF-8")+"\";");
            os = response.getOutputStream();//输出流
            while( (len = in.read(buf)) > 0 ) //切忌这后面不能加 分号 ”;“
            {
                os.write(buf, 0, len);//向客户端输出，实际是把数据存放在response中，然后web服务器再去response中读取
            }
            in.close();
            os.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return boo;
    }
}
