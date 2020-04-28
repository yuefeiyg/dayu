package com.dayu.util;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipFileUtil {

    public static final int DEFAULT_BUFSIZE = 1024 * 16;

    public static void unZip(String zipFilePath, String destDir) throws IOException
    {

        File file = new File(zipFilePath);
        ZipFile zipFile = new ZipFile(file);
        unZip(zipFile, destDir);
    }

    public static void unZip(File srcZipFile, String destDir) throws IOException
    {
        ZipFile zipFile = new ZipFile(srcZipFile);
        unZip(zipFile, destDir);
    }

    /**
     * 解压缩zip文件
     * @param zipFile
     * @param destDir
     * @throws IOException
     */
    public static void unZip(ZipFile zipFile, String destDir) throws IOException
    {
        Enumeration<? extends ZipEntry> entryEnum = zipFile.entries();
        ZipEntry entry = null;
        while (entryEnum.hasMoreElements()) {
            entry = entryEnum.nextElement();
            File destFile = new File(destDir + entry.getName());
            if (entry.isDirectory()) {
                destFile.mkdirs();
            }
            else {
                destFile.getParentFile().mkdirs();
                InputStream eis = zipFile.getInputStream(entry);
                write(eis, destFile);
            }
        }
    }


    /**
     *  处理zip压缩包
     * 1、首先解压zip输出到指定位置
     * 2、解析文件，拆分文件，返回content数组
     * 3、删除文件
     * @param outFileDir
     * @param originfilepath
     * @throws Exception
     */
    public  static List<Map<String,Object>>  handleZipFile(String outFileDir,String originfilepath) throws Exception{
        List<Map<String,Object>>  allcontents = new ArrayList<>();
        //解压缩zip包
        FileUtil.checkFileOrDirExist(outFileDir,"dir");
        unZip(originfilepath,outFileDir);

        allcontents = checkFileIsDirOrFile(new File(outFileDir),allcontents);

        return allcontents;

    }

    public static  List<Map<String,Object>>  checkFileIsDirOrFile(File fileDir,List<Map<String,Object>> allcontents) throws  Exception{
        File[] filelist = fileDir.listFiles();
        if(null != filelist  && filelist.length > 0){
            for (int i = 0;i < filelist.length ; i++) {
                if(filelist[i].getName().contains("__MACOSX")){
                    continue;
                }
                if(filelist[i].isDirectory()){//还是文件夹
                    allcontents = checkFileIsDirOrFile(filelist[i],allcontents);
                }else{//文件
                    Map<String,Object> filemap = new HashMap<>(2);
                    String filepath = filelist[i].getAbsolutePath();
                    filemap.put("filepath",filepath);
                    Object fileparts = new ArrayList<>();
                    if(filepath.contains(".json")){
                        JSONObject jsonObject = JsonFileUtil.readJSONFileOfDataSource(filepath,"utf-8");
                        fileparts=  jsonObject.get("contents");
                        filemap.put("filetype","json");
                        filemap.put("filename",jsonObject.get("filename"));
                    }else if(filepath.contains(".txt")){
                        fileparts = TxtFileUtil.ReadTxtFileToListBySentence(filepath,"gbk");
                        filemap.put("filetype","txt");
                    }

                    filemap.put("fileparts",fileparts);
                    allcontents.add(filemap);

                }
            }
        }
        return allcontents;

    }


    /**
     * 将输入流中的数据写到指定文件
     *
     * @param inputStream
     * @param destFile
     */
    public static void write(InputStream inputStream, File destFile) throws IOException
    {
        BufferedInputStream bufIs = null;
        BufferedOutputStream bufOs = null;
        try {
            bufIs = new BufferedInputStream(inputStream);
            bufOs = new BufferedOutputStream(new FileOutputStream(destFile));
            byte[] buf = new byte[DEFAULT_BUFSIZE];
            int len = 0;
            while ((len = bufIs.read(buf, 0, buf.length)) > 0) {
                bufOs.write(buf, 0, len);
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            close(bufOs, bufIs);
        }
    }
    /**
     * 安全关闭多个流
     *
     * @param streams
     */
    public static void close(Closeable... streams)
    {
        try {
            for (Closeable s : streams) {
                if (s != null)
                    s.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
    }

    /**
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下
     * @param sourceFilePath :待压缩的文件路径
     * @param zipFilePath :压缩后存放路径
     * @param fileName :压缩后文件的名称
     * @return
     */
    public static boolean fileToZip(String sourceFilePath,String zipFilePath,String fileName){
        boolean flag = false;
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        if(sourceFile.exists() == false){
            System.out.println("待压缩的文件目录："+sourceFilePath+"不存在.");
        }else{
            try {
                File zipFile = new File(zipFilePath + "/" + fileName +".zip");
                if(zipFile.exists()){
                    System.out.println(zipFilePath + "目录下存在名字为:" + fileName +".zip" +"打包文件.");
                }else{
                    File[] sourceFiles = sourceFile.listFiles();
                    if(null == sourceFiles || sourceFiles.length<1){
                        System.out.println("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩.");
                    }else{
                        fos = new FileOutputStream(zipFile);
                        zos = new ZipOutputStream(new BufferedOutputStream(fos));
                        byte[] bufs = new byte[1024*10];
                        for(int i=0;i<sourceFiles.length;i++){
                            //创建ZIP实体，并添加进压缩包
                            ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                            zos.putNextEntry(zipEntry);
                            //读取待压缩的文件并写进压缩包里
                            fis = new FileInputStream(sourceFiles[i]);
                            bis = new BufferedInputStream(fis, 1024*10);
                            int read = 0;
                            while((read=bis.read(bufs, 0, 1024*10)) != -1){
                                zos.write(bufs,0,read);
                            }
                        }
                        flag = true;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally{
                //关闭流
                try {
                    if(null != bis) bis.close();
                    if(null != zos) zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return flag;
    }
    public static void main(String[] args) {
        try {
            unZip("/Users/guyuefei/归档.zip","/Users/guyuefei/zipfiles/");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    /**
     * 将文件压缩成zip文件
     *  @param sourceFiles :待压缩的文件数组
     *  @param zipFilePath :压缩后存放路径
     */
    public static boolean ZipFiles(List<File> sourceFiles, String zipFilePath) throws Exception{
        boolean flag = false;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            File zipFile = new File(zipFilePath);
            if(zipFile.exists()){
               zipFile.delete();
            }else{
                fos = new FileOutputStream(zipFile);
                zos = new ZipOutputStream(new BufferedOutputStream(fos));
                byte[] bufs = new byte[1024*10];
                for(int i=0;i<sourceFiles.size();i++){
                    //创建ZIP实体，并添加进压缩包
                    if(sourceFiles.get(i).isDirectory()){
                        File[] filesss = sourceFiles.get(i).listFiles();
                        List<File> listfile = new ArrayList<>();
                        for(File file : filesss){
                            listfile.add(file);
                        }
                        ZipFileUtil.ZipFiles(listfile,zipFilePath);

                    }else{
                        ZipEntry zipEntry = new ZipEntry(sourceFiles.get(i).getName());
                        zos.putNextEntry(zipEntry);
                        //读取待压缩的文件并写进压缩包里
                        fis = new FileInputStream(sourceFiles.get(i));
                        bis = new BufferedInputStream(fis, 1024*10);
                        int read = 0;
                        while((read=bis.read(bufs, 0, 1024*10)) != -1){
                            zos.write(bufs,0,read);
                        }
                    }
                }
                flag = true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally{
            //关闭流
            try {
                if(null != bis) bis.close();
                if(null != zos) zos.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return flag;
    }


    public static boolean downloadzipfile(String txttemppath, String zipname, HttpServletResponse response) throws Exception{
        boolean boo = false;
        InputStream in =  new FileInputStream(txttemppath+"/"+zipname); //获取文件的流
        OutputStream os = response.getOutputStream();
        try {

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
        }finally {
            if(null != in){
                in.close();
            }
            if(null != os){
                os.close();
            }
        }
        return boo;
    }

        /**
         * 压缩成ZIP 方法1
         * @param srcDir 压缩文件夹路径
         * @param out    压缩文件输出流
         * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构;
         *                          false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
         * @throws RuntimeException 压缩失败会抛出运行时异常
         */
    public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure)
        throws RuntimeException{
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile,zos,sourceFile.getName(),KeepDirStructure);
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
        /**
         * 压缩成ZIP 方法2
         * @param srcFiles 需要压缩的文件列表
         * @param out           压缩文件输出流
         * @throws RuntimeException 压缩失败会抛出运行时异常
         */
    public static void toZip(List<File> srcFiles , OutputStream out)throws RuntimeException {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[DEFAULT_BUFSIZE];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1){
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
        /**
         * 递归压缩方法
         * @param sourceFile 源文件
         * @param zos        zip输出流
         * @param name       压缩后的名称
         * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构;
         *                          false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
         * @throws Exception
         */
    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStructure) throws Exception{
        byte[] buf = new byte[DEFAULT_BUFSIZE];
        if(sourceFile.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if(KeepDirStructure){
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            }else {
                for (File file : listFiles) {
                    if((file.getName()).equals(name +".zip")){
                        System.out.println("======================");
                        continue;
                    }
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(),KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(),KeepDirStructure);
                    }
                }
            }
        }

    }
}
