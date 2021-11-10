package org.sheedon.arouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法需要绑定的参数
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 2:47 下午
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface BindParameter {

    // 方法所需要对应接收的字段名
    String name() default "";

    // 方法说明
    String desc() default "";
}
