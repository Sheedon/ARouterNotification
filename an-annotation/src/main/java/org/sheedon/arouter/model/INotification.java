package org.sheedon.arouter.model;

import java.util.Map;

/**
 * 通知职责
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 4:04 下午
 */
public interface INotification {

    /**
     * 通知触发器 附加到 triggerMap 中
     *
     * @return triggerMap 触发者
     */
    void attachTrigger(Map<String, ITrigger> triggerMap);

}
