package org.sheedon.arouter.compiler;

import com.squareup.javapoet.ClassName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 类属性，包括：包名、类名、构造方法参数信息
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 6:54 下午
 */
class ClassAttribute {

    // 包名
    private ClassName className;
    // 字段名称
    private String fieldName;
    // 构造参数
    private List<String> parameters;

    ClassAttribute(ClassName className) {
        this(className,null);
    }

    ClassAttribute(ClassName className,String fieldName) {
        this.className = className;
        this.fieldName = fieldName;
        this.parameters = new ArrayList<>();
    }

    /**
     * 追加参数
     *
     * @param parameters 参数内容
     */
    void addParameters(String... parameters) {
        if (parameters == null || parameters.length == 0) {
            return;
        }
        this.parameters.addAll(Arrays.asList(parameters));
    }

    /**
     * 追加参数
     *
     * @param parameters 参数内容
     */
    void addParameters(List<String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return;
        }
        this.parameters.addAll(parameters);
    }

    /**
     * 追加参数
     *
     * @param parameter 参数内容
     */
    void addParameters(String parameter) {
        if (parameter == null || parameter.isEmpty()) {
            return;
        }
        this.parameters.add(parameter);
    }

    ClassName getClassName() {
        return className;
    }

    String getFieldName() {
        return fieldName;
    }

    List<String> getParameters() {
        return parameters;
    }
}
