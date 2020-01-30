package com.dayu.util;


import de.innosystec.unrar.Archive;
import de.innosystec.unrar.NativeStorage;
import de.innosystec.unrar.rarfile.FileHeader;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RarFileUtil {

    public static void unRarFile(String filePath, String destDir)throws Exception
    {

        File file = new File(filePath);
        unRarFile(file,destDir);

    }

    public static void unRar(File rarFile, String outDir) throws Exception {
        File outFileDir = new File(outDir);
        if (!outFileDir.exists()) {
            boolean isMakDir = outFileDir.mkdirs();
            if (isMakDir) {
                System.out.println("创建压缩目录成功");
            }
        }
        Archive archive = new Archive(new NativeStorage(rarFile));
        FileHeader fileHeader = archive.nextFileHeader();
        while (fileHeader != null) {
            if (fileHeader.isDirectory()) {
                fileHeader = archive.nextFileHeader();
                continue;
            }
            File out = new File(outDir + fileHeader.getFileNameString());
            if (!out.exists()) {
                if (!out.getParentFile().exists()) {
                    out.getParentFile().mkdirs();
                }
                out.createNewFile();
            }
            FileOutputStream os = new FileOutputStream(out);
            archive.extractFile(fileHeader, os);
            os.close();
            fileHeader = archive.nextFileHeader();
        }
        archive.close();
    }

    /**
     * 根据原始rar路径，解压到指定文件夹下.
     * @param file 压缩文件
     * @param dstDirectoryPath 解压到的文件夹
     */
    public static void unRarFile (File file,String dstDirectoryPath)throws Exception {
        File dstDiretory = new File(dstDirectoryPath);
        if (!dstDiretory.exists()) {// 目标目录不存在时，创建该文件夹
            dstDiretory.mkdirs();
        }
        File fol=null,out=null;
        Archive a = null;
        try {
            a = new Archive(new NativeStorage(file));
            if (a != null){
                a.getMainHeader().print(); // 打印文件信息.
                FileHeader fh = a.nextFileHeader();
                while (fh != null){
                    if (fh.isDirectory()) { // 文件夹
                        // 如果是中文路径，调用getFileNameW()方法，否则调用getFileNameString()方法，还可以使用if(fh.isUnicode())
                        if(existZH(fh.getFileNameW())){
                            fol = new File(dstDirectoryPath + File.separator
                                    + fh.getFileNameW());
                        }else{
                            fol = new File(dstDirectoryPath + File.separator
                                    + fh.getFileNameString());
                        }
                        fol.mkdirs();
                    } else { // 文件
                        if(existZH(fh.getFileNameW())){
                            out = new File(dstDirectoryPath + File.separator
                                    + fh.getFileNameW().trim());
                        }else{
                            out = new File(dstDirectoryPath + File.separator
                                    + fh.getFileNameString().trim());
                        }
                        //System.out.println(out.getAbsolutePath());
                        try {// 之所以这么写try，是因为万一这里面有了异常，不影响继续解压.
                            if (!out.exists()) {
                                if (!out.getParentFile().exists()){// 相对路径可能多级，可能需要创建父目录.
                                    out.getParentFile().mkdirs();
                                }
                                out.createNewFile();
                            }
                            FileOutputStream os = new FileOutputStream(out);
                            a.extractFile(fh, os);
                            os.close();
                        } catch (Exception ex) {

                            ex.printStackTrace();
                        }
                    }
                    fh = a.nextFileHeader();
                }
                a.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*

     * 判断是否是中文

     */

    public static boolean existZH(String str){
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            unRarFile("/Users/guyuefei/patent.rar","/Users/guyuefei/rarfiles/");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}
