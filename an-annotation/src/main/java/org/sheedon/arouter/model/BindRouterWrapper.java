package org.sheedon.arouter.model;

/**
 * 绑定路由装饰类
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 4:07 下午
 */
public abstract class BindRouterWrapper<T> implements ITrigger<T> {

    // 目标路由
    private String targetRoute;
    // 备用路由
    private String spareRoute;
    // 绑定路由卡片
    private final BindRouterCard<T> routerCard;

    public BindRouterWrapper(BindRouterCard<T> routerCard) {
        this.routerCard = routerCard;
        attachRouter();
    }

    /**
     * 附加路径信息
     */
    protected abstract void attachRouter();

    /**
     * 设置目标路由
     */
    protected void setTargetRoute(String targetRoute) {
        this.targetRoute = targetRoute;
    }

    /**
     * 设置备用路由
     */
    protected void setSpareRoute(String spareRoute) {
        this.spareRoute = spareRoute;
    }

    /**
     * 填充通知数据
     */
    @Override
    public void attachData(T data) {
        routerCard.attachData(data);
    }

    /**
     * 打开 Activity
     */
    @Override
    public void startActivity() {
        boolean isExecuted = routerCard.startActivity(targetRoute, spareRoute);
        if (isExecuted) {
            return;
        }
        startActivity(routerCard, targetRoute, spareRoute);
    }

    /**
     * 根据条件跳转到「目标路径的Activity」、「备用目标路径Activity」或 执行错误提示
     *
     * @param targetRoute 目标路径
     * @param spareRoute  备用路径
     */
    protected abstract void startActivity(BindRouterCard<T> routerCard, String targetRoute, String spareRoute);
}
