package cn.crabc.core.system.util;

import com.alibaba.fastjson2.JSON;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
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
     * 获取body
     *
     * @param req
     * @return
     */
    public static Map<String, Object> getBodyMap(HttpServletRequest req) {
        BufferedReader reader = null;
        try {
            reader = req.getReader();
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            reader.close();
            String bodyString = builder.toString();
            if (!"".equals(bodyString)) {
                Map<String, Object> bodyMap = JSON.parseObject(bodyString, Map.class);
                return bodyMap;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

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
     * @param req
     * @return
     */
    public static String getAppCode(HttpServletRequest req){
        String appCode = req.getHeader("appCode");
        if (appCode == null) {
            appCode = req.getParameter("appCode");
        }
        return appCode;
    }
}
