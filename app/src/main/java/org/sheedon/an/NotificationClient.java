package org.sheedon.an;

import org.sheedon.arouter.annotation.Communicant;
import org.sheedon.arouter.model.ITrigger;

/**
 * 通知处理者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 3:56 下午
 */
@Communicant
public class NotificationClient {

    private final NotificationClientProxy proxy = new NotificationClientProxy();


    public void notifyInfo() {
        ITrigger<String> trigger = proxy.findTrigger("132");
        trigger.attachData("服务器拿到的数据");

        trigger.startActivity();
    }



}
