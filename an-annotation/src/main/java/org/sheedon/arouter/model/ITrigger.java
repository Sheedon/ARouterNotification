package org.sheedon.arouter.model;

/**
 * 通知触发动作
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 4:21 下午
 */
public interface ITrigger<T> {


    /**
     * 附加通知信息
     *
     * @param data 通知信息
     */
    void attachData(T data);

    /**
     * 打开Activity
     */
    void startActivity();
}
