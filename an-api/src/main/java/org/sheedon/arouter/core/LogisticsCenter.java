package org.sheedon.arouter.core;

import static org.sheedon.arouter.utils.Consts.*;

import android.content.Context;

import org.sheedon.arouter.launcher.NotificationRouter;
import org.sheedon.arouter.model.INotification;
import org.sheedon.arouter.model.ITrigger;
import org.sheedon.arouter.utils.ClassUtils;
import org.sheedon.arouter.utils.PackageUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 路径持有者中心
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/10 3:24 下午
 */
public class LogisticsCenter {

    public synchronized static boolean init(Context context) {
        try {
            Set<String> routerMap;

            // It will rebuild router map every times when debuggable.
            if (NotificationRouter.debuggable() || PackageUtils.isNewVersion(context)) {
                // These class was generated by arouter-compiler.
                routerMap = ClassUtils.getFileNameByPackageName(context, ROUTE_ROOT_PACKAGE);
                if (!routerMap.isEmpty()) {
                    context.getSharedPreferences(NOTIFICATION_ROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE).edit()
                            .putStringSet(NOTIFICATION_ROUTER_SP_KEY_MAP, routerMap).apply();
                }

                PackageUtils.updateVersion(context);    // Save new version name when router map update finishes.
            } else {
                routerMap = new HashSet<>(context.getSharedPreferences(NOTIFICATION_ROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE)
                        .getStringSet(NOTIFICATION_ROUTER_SP_KEY_MAP, new HashSet<String>()));
            }

            for (String className : routerMap) {
                if (className.startsWith(ROUTE_ROOT_PACKAGE)) {
                    // This one of root elements, load root.
                    ((INotification) (Class.forName(className).getConstructor().newInstance())).attachTrigger(Warehouse.notificationMap);
                }
            }
        } catch (Exception ignored) {
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    public static <T> ITrigger<T> findTrigger(String key) {
        return (ITrigger<T>) Warehouse.notificationMap.get(key);
    }
}
