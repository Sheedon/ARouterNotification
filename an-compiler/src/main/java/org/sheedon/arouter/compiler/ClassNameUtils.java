package org.sheedon.arouter.compiler;

import com.squareup.javapoet.ClassName;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * 类名工具类
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/5 1:16 下午
 */
class ClassNameUtils {

    /**
     * 根据全类名加载类
     *
     * @param qualifiedName 全类名
     * @param mMessager     消息处理者
     * @param info          错误消息
     * @return ClassName
     */
    static ClassName loadClassNameByQualifiedName(String qualifiedName, Messager mMessager, String info) {
        int index = qualifiedName.lastIndexOf(".");

        if (index == -1) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, info);
            return null;
        }

        String packageName = qualifiedName.substring(0, index);
        String className = qualifiedName.substring(index + 1);
        return ClassName.get(packageName, className);
    }
}
