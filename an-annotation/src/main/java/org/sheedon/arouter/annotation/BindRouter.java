package org.sheedon.arouter.annotation;

import org.sheedon.arouter.model.BindRouterCard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 绑定路由，包含路由配置信息
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 1:57 下午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface BindRouter {

    // 路由卡片Model类
    Class<? extends BindRouterCard> routerClass();

    // 字段说明
    String desc() default "";
}
