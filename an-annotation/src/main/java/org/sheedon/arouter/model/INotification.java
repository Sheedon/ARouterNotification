package org.sheedon.arouter.model;

/**
 * 通知职责
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 4:04 下午
 */
public interface INotification {

    /**
     * 获取触发器
     *
     * @param key 消息类型key
     * @return Trigger 触发者
     */
    ITrigger findTrigger(String key);

}
