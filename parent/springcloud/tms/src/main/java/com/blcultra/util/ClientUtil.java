package com.blcultra.util;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取浏览器相关信息
 * 参考：
 * https://blog.csdn.net/l1028386804/article/details/46049885
 */
public class ClientUtil {
    /**
     * 获取客户端ip地址
     * @param request
     * @return
     */
    public static String getRealIpAddr(HttpServletRequest request){
        /*String ip = request.getHeader("X-Real-IP");
        if (ip!= null && !"".equals(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }*/
        String ip = request.getHeader("X-Forwarded-For");
        if (ip!= null && !"".equals(ip)  && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }

    public static String getEquipment(HttpServletRequest request){
        return request.getHeader("User-Agent");
    }
    /**
     * 获取来访者的浏览器版本
     * @param request
     * @return
     */
    public static String getRequestBrowserInfo(HttpServletRequest request){
        String browserVersion = null;
        String header = request.getHeader("user-agent");
        if(header == null || header.equals("")){
            return "";
        }
        if(header.indexOf("MSIE")>0){
            browserVersion = "IE";
        }else if(header.indexOf("Firefox")>0){
            browserVersion = "Firefox";
        }else if(header.indexOf("Chrome")>0){
            browserVersion = "Chrome";
        }else if(header.indexOf("Safari")>0){
            browserVersion = "Safari";
        }else if(header.indexOf("Camino")>0){
            browserVersion = "Camino";
        }else if(header.indexOf("Konqueror")>0){
            browserVersion = "Konqueror";
        }
        return browserVersion;
    }

    /**
     * 获取来访者的主机名称
     * @param ip
     * @return
     */
    public static String getHostName(String ip){
        InetAddress inet;
        try {
            inet = InetAddress.getByName(ip);
            return inet.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * Windows环境下使用
     * 调用命令
     * @param cmd
     * @return
     */
    public static String callCmd(String[] cmd) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *Windows环境下使用
     * @param cmd 第一个命令
     * @param another 第二个命令
     * @return 第二个命令的执行结果
     */
    public static String callCmd(String[] cmd, String[] another) {
        String result = "";
        String line = "";
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd);
            proc.waitFor(); // 已经执行完第一个命令，准备执行第二个命令
            proc = rt.exec(another);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *Windows环境下使用
     * @param ip 目标ip,一般在局域网内
     * @param sourceString 命令处理的结果字符串
     * @param macSeparator mac分隔符号
     * @return mac地址，用上面的分隔符号表示
     */
    public static String filterMacAddress(final String ip, String sourceString, final String macSeparator) {
        String result = "";
        int index = sourceString.indexOf(ip);
        if (index == -1) {
            index = 0;
        }
        sourceString = sourceString.substring(index, sourceString.length() - 1);
        String regExp = "((([0-9,A-F,a-f]{1,2}" + macSeparator + "){1,5})[0-9,A-F,a-f]{1,2})";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(sourceString);
        while (matcher.find()) {
            result = matcher.group(1);
            if (sourceString.indexOf(ip) <= sourceString.lastIndexOf(matcher.group(1))) {
                break; // 如果有多个IP,只匹配本IP对应的Mac.
            }
        }
        return result;
    }

    /**
     * @param ip 目标ip
     * @return Mac Address
     */
    public static String getMacInWindows(final String ip) {
        String result = "";
        String[] cmd = { "cmd", "/c", "ping " + ip };
        String[] another = { "cmd", "/c", "arp -a" };
        String cmdResult = callCmd(cmd, another);
        result = filterMacAddress(ip, cmdResult, "-");
        return result;
    }

    /**
     * Linux环境下使用
     * @param ip 目标ip
     * @return Mac Address
     *
     */
    public static String getMacInLinux(final String ip) {
        String result = "";
        String[] cmd = { "/bin/sh", "-c", "ping " + ip + " -c 2 && arp -a" };
        String cmdResult = callCmd(cmd);
        result = filterMacAddress(ip, cmdResult, ":");

        return result;
    }

    /**
     * 获取MAC地址
     * @return 返回MAC地址
     */
    public static String getMacAddress(String ip) {
        String macAddress = "";
        macAddress = getMacInWindows(ip).trim();
        if (macAddress == null || "".equals(macAddress)) {
            macAddress = getMacInLinux(ip).trim();
        }
        return macAddress;
    }

    /**
     * 通用获取mac方法
     * @param ip
     * @return
     * @throws IOException
     */
    public static String getMac(String ip) throws IOException {
        String mac = "not found!";
        if (ip != null) {

            try {
                Process process = Runtime.getRuntime().exec("arp "+ip);
                InputStreamReader ir = new InputStreamReader(process.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);
                String line;
                StringBuffer s = new StringBuffer();
                while ((line = input.readLine()) != null) {
                    s.append(line);
                }
                mac = s.toString();
                if (mac != null) {

                    mac = mac.substring(mac.indexOf(":") - 2, mac.lastIndexOf(":") + 3);

                } else {
                    mac = "not found!";
                }
                return mac;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mac;

    }
    /**
     * 获取统一局域网的所有IP地址
     * @return 所有IP地址的List集合
     */
    public static List<String> getIPs() {
        List<String> list = new ArrayList<String>();
        Runtime r = Runtime.getRuntime();
        Process p;
        try {
            p = r.exec("arp -a");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inline;
            while ((inline = br.readLine()) != null) {
                if(!"".equals(inline.trim())){
                    if (inline.indexOf("---") > -1) {
                        continue;
                    }
                    if(inline.indexOf("Internet") > -1){
                        continue;
                    }
                    // 有效IP
                    String[] str = inline.split(" {4}");
                    list.add(str[0]);
//                  System.out.println(inline);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取同一局域网内的IP和Mac
     * @return 以IP地址为Key, Mac地址为Value的Map
     */
    public static Map<String, String> getAllIPAndMac(){
        Map<String,String> map = new HashMap<String,String>();
        Runtime r = Runtime.getRuntime();
        Process p;
        try {
            p = r.exec("arp -a");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inline;
            while ((inline = br.readLine()) != null) {
                if(!"".equals(inline.trim())){
                    if (inline.indexOf("---") > -1) {
                        continue;
                    }
                    if(inline.indexOf("Internet") > -1){
                        continue;
                    }
                    // 有效IP
                    String[] arr = inline.split(" {4}");
                    String ip = arr[0].trim();
                    String mac = "00-00-00-00-00-00";
                    for(int i = 1; i < arr.length; i ++){
                        String str = arr[i].trim();
                        if(stringIsMac(str)){
                            mac = str;
                            break;
                        }
                    }
                    map.put(ip, mac);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 根据正则表达式判断是否为Mac地址
     * @param val
     * @return true:是Mac地址，false：不是Mac地址
     */
    private static boolean stringIsMac(String val) {
        String trueMacAddress = "^([0-9a-fA-F]{2})(([/\\s:-][0-9a-fA-F]{2}){5})$";
        // 这是真正的MAC地址；正则表达式；
        return val.matches(trueMacAddress);
    }

    /**
     * 根据IP提取主机名
     * @param ips
     * @return 以IP地址为Key,主机名为Value的Map
     */
    public static Map<String, String> getHostnames(List<String> ips){
        Map<String,String> map = new HashMap<String,String>();
        System.out.println("正在提取hostname...");
        for(String ip : ips){
            String command = "ping -a " + ip;
            Runtime r = Runtime.getRuntime();
            Process p;
            try {
                p = r.exec(command);
                BufferedReader br = new BufferedReader(new InputStreamReader(p .getInputStream()));
                String inline;
                while ((inline = br.readLine()) != null) {
                    if(inline.indexOf("[") > -1){
                        int start = inline.indexOf("Ping ");
                        int end = inline.indexOf("[");
                        String hostname = inline.substring(start+"Ping ".length(),end-1);
                        System.out.println(hostname);
                        map.put(ip,hostname);
                    }
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("提取结束！");
        return map;
    }
}
