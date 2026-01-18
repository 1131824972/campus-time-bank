package com.campus.timebank.common;

/**
 * 线程隔离的上下文，用于在请求处理的任意地方获取当前用户ID
 */
public class UserContext {
    private static final ThreadLocal<Long> userThreadLocal = new ThreadLocal<>();

    /**
     * 存入用户ID
     */
    public static void setUserId(Long userId) {
        userThreadLocal.set(userId);
    }

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        return userThreadLocal.get();
    }

    /**
     * 清理
     */
    public static void remove() {
        userThreadLocal.remove();
    }
}