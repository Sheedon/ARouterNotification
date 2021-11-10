package org.sheedon.an;

import org.sheedon.arouter.launcher.NotificationRouter;
import org.sheedon.arouter.model.ITrigger;

/**
 * 通知处理者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 3:56 下午
 */
public class NotificationClient {



    @SuppressWarnings("unchecked")
    public void notifyInfo() {
        ITrigger<String> trigger = NotificationRouter.findTrigger("144");
        trigger.attachData("服务器拿到的数据");

        trigger.startActivity();
    }



}
