package org.sheedon.arouter.compiler;

import com.squareup.javapoet.ClassName;

/**
 * 路由代理参数
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/5 2:04 下午
 */
public class RouterWrapperAttribute {

    private String notificationType;
    private ClassName wrapperClassName;

    public RouterWrapperAttribute(String notificationType, ClassName wrapperClassName) {
        this.notificationType = notificationType;
        this.wrapperClassName = wrapperClassName;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public ClassName getWrapperClassName() {
        return wrapperClassName;
    }
}
