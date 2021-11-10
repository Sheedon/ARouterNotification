package org.sheedon.arouter.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.sheedon.arouter.model.INotification;
import org.sheedon.arouter.model.ITrigger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * 通知调度者构建 处理器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 5:33 下午
 */
public class CommunicantBuilder {

    // 元素处理工具类
    private final Elements mElementUtils;
    // 文件构造者
    private final Filer mFiler;
    // 消息处理器
    private final Messager mMessager;
    // 通知调度Class
    private ClassAttribute communicantClass;

    CommunicantBuilder(Elements elements, Filer filer, Messager messager) {
        this.mElementUtils = elements;
        this.mFiler = filer;
        this.mMessager = messager;
    }

    /**
     * 拿到类名和包名，创建代理方法
     *
     * @param element 接口信息
     */
    void addTypeElement(TypeElement element) {
        ClassName className = (ClassName) TypeName.get(element.asType());
        communicantClass = new ClassAttribute(className);

    }

    /**
     * 构建Class
     *
     * @param attributes
     */
    void buildClass(List<RouterWrapperAttribute> attributes) {
        try {
            ClassName className = communicantClass.getClassName();
            String proxyClassName = className.simpleName() + "Proxy";

            List<MethodSpec> methodSpecs = new ArrayList<>();

            methodSpecs.add(buildInterfaceImpl());
            methodSpecs.add(buildAttachTrigger(attributes));

            TypeSpec proxyComponent = TypeSpec.classBuilder(proxyClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(INotification.class)
                    .addField(buildTriggerMap())
                    .addMethods(methodSpecs)
                    .build();


            JavaFile javaFile = JavaFile.builder(className.packageName(), proxyComponent)
                    .build();

            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建保存通知策略的Map
     */
    private FieldSpec buildTriggerMap() {
        ParameterizedTypeName typeName = ParameterizedTypeName.get(Map.class, String.class, ITrigger.class);
        return FieldSpec.builder(typeName, "triggerMap", Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T<>()", HashMap.class)
                .build();
    }

    /**
     * 实现接口实现
     */
    private MethodSpec buildInterfaceImpl() {

        return MethodSpec.methodBuilder("findTrigger")
                .addParameter(String.class, "key")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .beginControlFlow("if(triggerMap.size() == 0)")
                .addStatement("attachTrigger()")
                .endControlFlow()
                .addStatement("return triggerMap.get(key)")
                .returns(ITrigger.class)
                .build();
    }

    /**
     * 构建填充触发者
     */
    private MethodSpec buildAttachTrigger(List<RouterWrapperAttribute> attributes) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("attachTrigger")
                .addModifiers(Modifier.PRIVATE);

        for (RouterWrapperAttribute attribute : attributes) {
            builder.addStatement("triggerMap.put(\"$N\", new $L())", attribute.getNotificationType(),
                    attribute.getWrapperClassName());
        }


        return builder.build();
    }
}
