package org.sheedon.arouter.launcher;

import android.app.Application;

import org.sheedon.arouter.core.LogisticsCenter;
import org.sheedon.arouter.model.ITrigger;

/**
 * 通知路由
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/10 3:18 下午
 */
public class NotificationRouter {

    private volatile static NotificationRouter instance = null;
    private volatile static boolean hasInit = false;
    private volatile static boolean debuggable = false;

    private NotificationRouter() {
    }

    public static void init(Application application) {
        if (!hasInit) {
            hasInit = LogisticsCenter.init(application);
        }
    }

    public static synchronized void openDebug() {
        debuggable = true;
    }

    /**
     * 根据通知key 获取 触发动作内容
     */
    public static <T> ITrigger<T> findTrigger(String key) {
        return LogisticsCenter.findTrigger(key);
    }

    public static boolean debuggable() {
        return debuggable;
    }

}
