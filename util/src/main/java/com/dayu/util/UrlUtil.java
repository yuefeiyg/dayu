package com.dayu.util;

import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.util.Map;

public class UrlUtil {
	

	private static String mapToString(Map<String, Object> params,String encode) {

		if (params == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			try {
				sb.append(entry.getKey()).append("=");
				if (entry.getValue() instanceof String) {

					sb.append(URLEncoder.encode((String) entry.getValue(),
							encode));
				} else {
					sb.append(entry.getValue());
				}
				sb.append("&");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (sb.length() > 1) {
			// 删掉最后一个&字符
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public static String parseGetUrl(String urlStr, Map<String, Object> params,String encode) {
		
		String paramsStr = mapToString(params,encode);
		
		if (!StringUtils.isEmpty(paramsStr)) {
			if (urlStr.indexOf("?") > -1) {
				urlStr = urlStr + "&" + paramsStr;
			} else {
				urlStr = urlStr + "?" + paramsStr;
			}
		}
		return urlStr;
	}
	
}
