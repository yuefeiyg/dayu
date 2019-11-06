package com.dayu.util;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    /**
     * 创建文件夹
     * @param path
     */
    public static void createFile(String path) {
        File file = new File(path);
        //判断文件是否存在;
        if (!file.exists()) {
            //创建文件;
            file.mkdirs();
        }
    }

    /**
     * 按句子读取文本内容
     * @param filepath
     * @return
     * @throws Exception
     */
    public static List<List<String>> readFileToListBySentence(String filepath) throws Exception{
        List<List<String>> fileparts = new ArrayList<>();
        String filecode = EncodingDetect.detect(filepath);
        try{
            if(filepath.contains(".json")){
                fileparts = JsonFileUtil.ReadJsonToListBySentence(filepath,filecode);
            }else{
                fileparts = TxtFileUtil.ReadTxtFileToListBySentence(filepath,filecode);
            }

        }catch (Exception e){
            throw new Exception("读取文件失败");
        }
        return fileparts;
    }

    /**
     * 判断文件或者文件夹是否存在，若不存在则创建
     * @param filepath
     * @param type
     */
    public  static  void checkFileOrDirExist(String filepath,String type){
        if(type.equals("file")){
            File file=new File(filepath);
            if(!file.exists())
            {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }else if(type.equals("dir")){
            File file =new File(filepath);
            //如果文件夹不存在则创建
            if(!file.exists()  && !file.isDirectory()){
                System.out.println("//文件夹不存在");
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.mkdir();
            }else{
                System.out.println("//目录存在");
            }
        }
    }

    /**
     * 检查文件是否已经存在
     * @param filepath
     */
    public  static  boolean checkFileIfExsit(String filepath){
        File file = new File(filepath);
        if(file.exists())
            return true;
        else
            return false;
    }

    /**
     * multipartfile转换为file
     * @param file
     * @param filedir
     * @return
     * @throws Exception
     */
    public static  File MultipartFileTransforToFile(MultipartFile file, String filedir) throws Exception{
        File f = null;
        if(file.equals("")||file.getSize()<=0){
            file = null;
        }else{
            InputStream ins = file.getInputStream();
            f=new File(filedir+file.getOriginalFilename());
            inputStreamToFile(ins,f,filedir);
        }

//        deleteFile(f);
        return f;
    }

    public static void inputStreamToFile(InputStream ins,File file,String originfilepath) {
        try {
            OutputStream os = null;
            if(file != null){
                os =new FileOutputStream(file);
            }else{
                os =new FileOutputStream(originfilepath);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除
     *
     * @param files
     */
    public static  void deleteFile(File... files) throws Exception{
        for (File file : files) {
            if (file.exists()) {
                if(file.isDirectory()){
                    deleteDir(file,file.listFiles());
                }  else{
                    boolean flag = file.delete();
                    System.out.println("======删除文件==== "+flag);
                }

            }
        }
    }

    private static  void deleteDir(File  parentDir,File... files) throws Exception{
        for (File file : files) {
            if (file.exists()) {
                if(file.isDirectory()){
                    deleteDir(file,file.listFiles());
                }else{
                    boolean flag1 = file.delete();
                    file = null;
                    System.out.println("======删除文件==== "+flag1);
                }
            }
        }
        boolean flag = parentDir.delete();
        System.out.println("======删除文件夹==== "+flag);
    }


    /**
     * 检查文件夹是否是空，如果不为空则清空
     * @param dir
     * @throws Exception
     */
    public static  void checkDirAndEmptyDir(String dir) throws Exception{
       File file = new File(dir);
       if(file.exists() && file.isDirectory()){
           File[] files = file.listFiles();
           if(null != files){
               for(File file1 : files){
                   boolean flag = file1.delete();
                   System.out.println(flag);
               }
           }
       }
    }

    /**
     * 文件复制
     * @param file
     * @throws Exception
     */
    public static File FileCopy(File file,String outdir) throws Exception{
        String outfilepath = outdir+file.getName();
        Files.copy(file.toPath(), new File(outfilepath).toPath());

        return new File(outfilepath);
    }
    /**
     * 文件复制
     * @param file
     * @throws Exception
     */
    public static File FileCopy(File file,long current,String outdir) throws Exception{
        String outfilepath = outdir+current+file.getName();
        Files.copy(file.toPath(), new File(outfilepath).toPath());

        return new File(outfilepath);
    }

    /**
     * 单文件下载
     * @param filepath
     * @param response
     * @throws Exception
     */
    public static void downloadSingleFile(String filepath, HttpServletResponse response) throws Exception{
        InputStream in = new FileInputStream(filepath);
        String filename = filepath.substring(filepath.lastIndexOf("/")+1);
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
    }
}
