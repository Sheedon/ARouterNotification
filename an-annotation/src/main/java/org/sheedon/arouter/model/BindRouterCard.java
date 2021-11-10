package org.sheedon.arouter.model;

/**
 * 所绑定的路由model
 * 负责真实跳转动作
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 2:01 下午
 */
public abstract class BindRouterCard<T> {

    private T data;

    /**
     * 附加数据
     *
     * @param data 通知信息
     */
    protected void attachData(T data) {
        this.data = data;
    }

    /**
     * 获取通知信息
     */
    public T getData() {
        return data;
    }

    /**
     * 根据条件跳转到「目标路径的Activity」、「备用目标路径Activity」或 执行错误提示
     *
     * @param targetRoute 目标路径
     * @param spareRoute  备用路径
     */
    protected abstract void startActivity(String targetRoute, String spareRoute);

    /**
     * 获取错误信息
     */
    protected abstract String getErrorMessage();
}
