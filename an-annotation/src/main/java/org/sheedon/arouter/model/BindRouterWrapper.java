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
        routerCard.startActivity(targetRoute, spareRoute);
    }
}
