package com.dayu.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonFileUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * 生成.json格式文件
     */
    public static boolean createJsonFile(String jsonString, String filePath, String fileName) {
        // 标记文件生成是否成功
        boolean flag = true;

        // 拼接文件完整路径
        String fullPath = filePath + File.separator + fileName + ".json";

        // 生成json格式文件
        try {
            // 保证创建一个新文件
            File file = new File(fullPath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();

//            if(jsonString.indexOf("'")!=-1){
//                //将单引号转义一下，因为JSON串中的字符串类型可以单引号引起来的
//                jsonString = jsonString.replaceAll("'", "\\'");
//            }
//            if(jsonString.indexOf("\"")!=-1){
//                //将双引号转义一下，因为JSON串中的字符串类型可以单引号引起来的
//                jsonString = jsonString.replaceAll("\"", "\\\"");
//            }
//
//            if(jsonString.indexOf("\r\n")!=-1){
//                //将回车换行转换一下，因为JSON串中字符串不能出现显式的回车换行
//                jsonString = jsonString.replaceAll("\r\n", "\\u000d\\u000a");
//            }
//            if(jsonString.indexOf("\n")!=-1){
//                //将换行转换一下，因为JSON串中字符串不能出现显式的换行
//                jsonString = jsonString.replaceAll("\n", "\\u000a");
//            }

            // 格式化json字符串
            jsonString = formatJson(jsonString);

            // 将格式化后的字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(jsonString);
            write.flush();
            write.close();
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        // 返回是否成功的标记
        return flag;
    }


    /**
     * 格式化json字符串
     * @param jsonStr
     * @return
     */
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr))
            return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        boolean isInQuotationMarks = false;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '"':
                    if (last != '\\'){
                        isInQuotationMarks = !isInQuotationMarks;
                    }
                    sb.append(current);
                    break;
                case '{':
                case '[':
                    sb.append(current);
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent++;
                        addIndentBlank(sb, indent);
                    }
                    break;
                case '}':
                case ']':
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent--;
                        addIndentBlank(sb, indent);
                    }
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\' && !isInQuotationMarks) {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    /**
     * 读取数据源json文件
     * @param filepath
     * @param charsetname
     * @return
     */
    public static JSONObject readJSONFileOfDataSource(String filepath, String charsetname) throws  Exception{
        String filePath = filepath; // 文件和该类在同个目录下
        List<Map<String,Object>> returnList = new ArrayList<>();
        JSONObject json  = new JSONObject();
        try {
            json = ReadJsonFile(new FileInputStream(filePath));

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;

    }

    /**
     * 读取json文本
     * @param file 文件
     * @return
     */
    public static JSONObject ReadJsonFile(File file) throws Exception{
        InputStream inputStream =  new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String str = new String(bytes);
        JSONObject jsonObject = JSONObject.parseObject(str);
        inputStream.close();
        return jsonObject;

    }


    /**
     * 读取json文本
     * @param inputStream 文件
     * @return
     */
    public static JSONObject ReadJsonFile(InputStream inputStream) throws Exception{
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String str = new String(bytes);
        str = StringUtil.replaceBlank(str);
        JSONObject jsonObject = JSONObject.parseObject(str);
        return jsonObject;

    }

    /**
     * 读取json文本,jsonArray
     * @param inputStream 文件
     * @return
     */
    public static JSONArray ReadJsonArrayFile(InputStream inputStream) throws Exception{
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String str = new String(bytes);
        JSONArray jsonArray = JSONArray.parseArray(str);
        return jsonArray;

    }

    /**
     * 读取json文本数据到txt文件
     * @param filepath
     * @param charsetname
     * @return
     */
    public static List<List<String>> ReadJsonToListBySentence(String filepath, String charsetname) throws  Exception{
        String filePath = filepath; // 文件和该类在同个目录下
        List<String> list = new ArrayList<>();
        List<List<String>> returnList = new ArrayList<>();
        try {
            JSONObject json = ReadJsonFile(new FileInputStream(filePath));
            JSONArray jsonArray = json.getJSONArray("content");
            Iterator<Object> contents = jsonArray.iterator();//获取段落
            JSONArray sentences = null;//获取句子
            String str = null;
            while (contents.hasNext()) {
                sentences = (JSONArray)contents.next();
                Iterator<Object> sentence = sentences.iterator();//遍历句子
                while (sentence.hasNext()){
                    str = sentence.next()+"";
                    if(! str.equals("")){//遇到空行
                        list.add(str);
                    }
                }
                returnList.add(list);
                list = new ArrayList<>();
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnList;

    }
    /**
     * 添加space
     *
     * @param sb
     * @param indent
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }
    /**
     * 将json文件内容读取转换成json对象
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static JSONObject readFileToJson(InputStream inputStream) throws Exception{
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String str = new String(bytes);
        JSONObject jsonObject = JSONObject.parseObject(str);
        inputStream.close();
        return jsonObject;
    }

    /**
     * 将json文件内容读取转换成json数组对象
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static JSONArray readFileToJsonArray(InputStream inputStream) throws Exception{
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String str = new String(bytes);
        JSONArray jsonObject = JSONArray.parseArray(str);
        inputStream.close();
        return jsonObject;
    }

    /**
     * 对map进行赋值操作
     * @param jsonmap
     * @param key
     * @param defaultValue
     * @param value
     * @return
     */
    public static JSONObject setValue(JSONObject jsonmap,String key,Object defaultValue,Object value){
        if(null == value || value.toString().length() == 0){
            jsonmap.put(key,defaultValue);
        }else{
            jsonmap.put(key,value);
        }
        return jsonmap;

    }

    public static String toString(Object obj){
        return toJson(obj);
    }

    public static String toJson(Object obj){
        try{
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, obj);
            return writer.toString();
        }catch(Exception e){
            throw new RuntimeException("序列化对象【"+obj+"】时出错", e);
        }
    }

    public static <T> T toBean(Class<T> entityClass, String jsonString){
        try {
            return mapper.readValue(jsonString, entityClass);
        } catch (Exception e) {
            throw new RuntimeException("JSON【"+jsonString+"】转对象时出错", e);
        }
    }


    public static Object parseIfException(Object obj){
        if(obj instanceof Exception){
            return getErrorMessage((Exception) obj, null);
        }
        return obj;
    }

    public static String getErrorMessage(Exception e, String defaultMessage){
        return defaultMessage != null ? defaultMessage : null;
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }
    /**
     * 导出数据为json文件，存储到指定路径下
     * @param filepath 存储路径
     * @param charsetname 输出编码字符集
     * @return
     * @throws Exception
     */
    public static  void exportJsonFileLocal(JSONObject jsonObject,String filepath,String charsetname) throws Exception{
        BufferedWriter fw = null;
        try {
            File file = new File(filepath);
            if(file.exists()){
                file.delete();
            }
            // 指定编码格式，以免读取时中文字符异常
            if(charsetname != null){
                fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), charsetname));
            }else{
                fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
            }
            fw.write(JsonFileUtil.formatJson(jsonObject.toJSONString()));
            fw.flush(); // 全部写入缓存中的内容
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
