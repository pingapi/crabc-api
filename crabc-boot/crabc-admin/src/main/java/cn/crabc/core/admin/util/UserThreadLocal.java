package cn.crabc.core.admin.util;

import java.util.Map;

/**
 * 上下文 当前用户基本信息
 *
 * @author yuqf
 */
public class UserThreadLocal {

    private static ThreadLocal<Map> userInfo = new ThreadLocal<>();

    /**
     * 用户信息存入 上下文中
     *
     * @param user
     */
    public static void set(Map<String, Object> user) {
        userInfo.set(user);
    }

    /**
     * 用户对象
     *
     * @return
     */
    public static Map<String, Object> get() {
        return userInfo.get();
    }

    /**
     * 获取用户ID
     *
     * @return
     */
    public static String getUserId() {
        Map map = userInfo.get();
        return map == null ? null : map.get("userId").toString();
    }

    /**
     * 清除
     */
    public static void remove() {
        userInfo.remove();
    }
}
