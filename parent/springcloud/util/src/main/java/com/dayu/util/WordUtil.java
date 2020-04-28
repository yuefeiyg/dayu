package com.dayu.util;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class WordUtil {

    /**
     * 接收上传文件，判断文件类型，读取文件
     * @param file
     * @return
     * @throws Exception
     */
    public static String readWord(MultipartFile file) throws Exception{
        String string = "";
        String filename = file.getOriginalFilename();
        if (filename.endsWith(".doc")) {
            string = readDoc(file.getInputStream());
        } else if (filename.endsWith("docx")) {
            string = readDocx(filename);
        } else {
            System.out.println("此文件不是word文件！");
        }
        return string;
    }

    /**
     * 判断word文件类型，读取文件
     * @param filepath
     * @return
     * @throws Exception
     */
    public static String readWord(String filepath) throws Exception{
        String string = "";
        if (filepath.endsWith(".doc")) {
            string = readDoc(new FileInputStream(new File(filepath)));
        } else if (filepath.endsWith("docx")) {
            string = readDocx(filepath);
        } else {
            System.out.println("此文件不是word文件！");
        }
        return string;
    }

    /**
     * 读取doc文件
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static String readDoc(InputStream inputStream) throws Exception{
        String string = "";
        WordExtractor ex = new WordExtractor(inputStream);
        string = ex.getText();
        ex.close();
        return string;
    }

    /**
     * 读取docx文件
     * @param filepath
     * @return
     * @throws Exception
     */
    public static String readDocx(String filepath) throws Exception{
        String string = "";
        OPCPackage opcPackage = POIXMLDocument.openPackage(filepath);
        POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
        string = extractor.getText();
        extractor.close();
        return string;
    }

    public static void main(String[] args) {
//        try {
//            String str = readWord("/Users/guyuefei/Documents/文档/任务管理标注系统改造(2).docx");
//            System.out.println(str);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }

    }
}
