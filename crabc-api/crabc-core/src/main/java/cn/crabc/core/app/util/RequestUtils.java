package cn.crabc.core.app.util;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 请求工具类
 *
 * @author yuqf
 */
public class RequestUtils {

    /**
     * 获取 params 参数
     *
     * @param req
     * @return
     */
    public static Map<String, Object> getParameters(HttpServletRequest req) {
        Map<String, Object> params = new HashMap<>();
        Map<String, String[]> parameterMap = req.getParameterMap();
        if (parameterMap.isEmpty()) {
            return params;
        }
        Set<String> keys = parameterMap.keySet();
        for (String key : keys) {
            String[] values = parameterMap.get(key);
            if (values.length == 1) {
                params.put(key, values[0]);
            } else {
                params.put(key, values);
            }
        }
        return params;
    }

    /**
     * 获取请求头和URL参数中的认证code
     *
     * @param req
     * @return
     */
    public static String getAppCode(HttpServletRequest req) {
        String appCode = req.getHeader("appCode");
        if (appCode == null) {
            String[] codeNames = new String[]{"appCode","AppCode", "app_code"};
            for(String codeName : codeNames) {
                appCode = req.getParameter(codeName);
                if (appCode != null) {
                    break;
                }
            }
        }
        return appCode;
    }

    /**
     * 获取请求头和URL参数中的认证AppKey
     *
     * @param req
     * @return
     */
    public static String getAppKey(HttpServletRequest req) {
        String appCode = req.getHeader("X-AppKey");
        if (appCode == null) {
            String[] codeNames = new String[]{"appKey","AppKey", "app_key","appkey"};
            for(String codeName : codeNames) {
                appCode = req.getParameter(codeName);
                if (appCode != null) {
                    break;
                }
            }
        }
        return appCode;
    }

    /**
     * 获取IP地址
     *
     * @param request
     * @return
     */
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip) || "localhost".equals(ip)) {
            ip ="127.0.0.1";
        }
        return ip == null ? "" : ip.trim();
    }
}
