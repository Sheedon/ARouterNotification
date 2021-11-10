package org.sheedon.arouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 通知调度者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 3:52 下午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Communicant {
}
