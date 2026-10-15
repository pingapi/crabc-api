package cn.crabc.core.app.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 上下文 当前用户基本信息
 *
 * @author yuqf
 */
public class UserThreadLocal {

    private static final ThreadLocal<Map<String, Object>> userInfo = new ThreadLocal<>();

    /**
     * 用户信息存入 上下文中
     *
     * @param user
     */
    public static void set(Map<String, Object> user) {
        userInfo.set(user);
    }

    /**
     * 写入当前用户身份
     * <p>
     * 只保存识别与鉴权所需字段，不放入密码等敏感信息；调用方必须保证取值来自服务端（数据库），
     * 而不是未经验证的令牌声明。
     *
     * @param userId   用户ID
     * @param userName 用户账号
     * @param role     角色
     */
    public static void set(Long userId, String userName, String role) {
        Map<String, Object> user = new HashMap<>(4);
        user.put("userId", userId);
        user.put("userName", userName);
        user.put("role", role);
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
        Object userId = value("userId");
        return userId == null ? "0" : userId.toString();
    }

    /**
     * 获取用户账号
     *
     * @return 账号，未登录返回 null
     */
    public static String getUserName() {
        Object userName = value("userName");
        return userName == null ? null : userName.toString();
    }

    /**
     * 是否管理员
     * <p>
     * 角色取服务端注入的身份，令牌内的 role 声明不参与判断。
     *
     * @return
     */
    public static boolean isAdmin() {
        Object role = value("role");
        return role != null && "admin".equalsIgnoreCase(role.toString());
    }

    /**
     * 是否超级管理员
     *
     * @return
     */
    public static boolean isSuperAdmin() {
        return "1".equals(getUserId()) && isAdmin();
    }

    /**
     * 清除
     */
    public static void remove() {
        userInfo.remove();
    }

    private static Object value(String key) {
        Map<String, Object> user = userInfo.get();
        return user == null ? null : user.get(key);
    }
}
