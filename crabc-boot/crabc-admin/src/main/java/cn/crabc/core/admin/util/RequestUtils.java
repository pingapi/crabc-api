package cn.crabc.core.admin.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取body
     *
     * @param req
     * @return
     */
    public static Map<String, Object> getBodyMap(HttpServletRequest req) {
        BufferedReader reader = null;
        Map<String, Object> bodyMap = new HashMap<>();
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
                bodyMap = objectMapper.readValue(bodyString, Map.class);
            }
        } catch (Exception e) {

        }
        return bodyMap;
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
     *
     * @param req
     * @return
     */
    public static String getAppCode(HttpServletRequest req) {
        String appCode = req.getHeader("appCode");
        if (appCode == null) {
            appCode = req.getParameter("appCode");
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
        return ip == null ? "" : ip.trim();
    }

    /**
     * 根据关键字解析json
     *
     * @param @param jsonObject
     * @param @param keyName 支持逗号分隔
     * @return Object
     */
    public static Object decodeJsonObject(String jsonObject, String keyName) {
        Object result = null;
        if (jsonObject == null || "".equals(jsonObject)) {
            return null;
        }
        JsonFactory jasonFactory = new JsonFactory();
        JsonParser parser = null;
        try {
            parser = jasonFactory.createParser(jsonObject);
            JsonToken firstToken = parser.nextToken();
            if (!JsonToken.START_OBJECT.equals(firstToken)) {
                return result;
            }
            while (!parser.isClosed()) {
                JsonToken t = parser.nextToken();
                if (JsonToken.FIELD_NAME.equals(t) && keyName.equals(parser.getCurrentName())) {
                    JsonToken v = parser.nextToken();
                    if (JsonToken.VALUE_NULL.equals(v)) {
                        return null;
                    } else if (JsonToken.VALUE_STRING.equals(v)) {
                        return parser.getValueAsString();
                    } else if (JsonToken.VALUE_TRUE.equals(v) || JsonToken.VALUE_FALSE.equals(v)) {
                        return parser.getBooleanValue();
                    } else if (JsonToken.VALUE_NUMBER_INT.equals(v)) {
                        return parser.getLongValue();
                    } else if (JsonToken.VALUE_NUMBER_FLOAT.equals(v)) {
                        return parser.getDoubleValue();
                    }
                } else if (JsonToken.START_OBJECT.equals(t) || JsonToken.START_ARRAY.equals(t)) {
                    parser.skipChildren();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (parser != null) {
                    parser.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
