package org.sheedon.arouter.compiler;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.sheedon.arouter.model.INotification;
import org.sheedon.arouter.model.ITrigger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

/**
 * 通知调度者构建 处理器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 5:33 下午
 */
public class CommunicantBuilder {

    public static final String ROUTE_ROOT_PACKAGE = "org.sheedon.android.arouter.proxy";
    public static final String NAME_OF_PROXY = "Notification$$PROXY$$";

    // 文件构造者
    private final Filer mFiler;
    // 当前类名
    private String className;

    CommunicantBuilder(Filer filer) {
        this.mFiler = filer;
    }

    /**
     * 拿到类名和包名，创建代理方法
     */
    void addModuleName(String moduleName) {
        className = NAME_OF_PROXY + moduleName;
    }

    /**
     * 构建Class
     *
     * @param attributes 属性
     */
    void buildClass(List<RouterWrapperAttribute> attributes) {
        try {
            String proxyClassName = className + "Proxy";

            List<MethodSpec> methodSpecs = new ArrayList<>();

            methodSpecs.add(buildInterfaceImpl(attributes));

            TypeSpec proxyComponent = TypeSpec.classBuilder(proxyClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(INotification.class)
                    .addField(buildTriggerMap())
                    .addMethods(methodSpecs)
                    .build();


            JavaFile javaFile = JavaFile.builder(ROUTE_ROOT_PACKAGE, proxyComponent)
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
    private MethodSpec buildInterfaceImpl(List<RouterWrapperAttribute> attributes) {

        ParameterizedTypeName typeName = ParameterizedTypeName.get(Map.class, String.class, ITrigger.class);

        MethodSpec.Builder builder = MethodSpec.methodBuilder("attachTrigger")
                .addParameter(typeName, "triggerMap")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        for (RouterWrapperAttribute attribute : attributes) {
            for (String type : attribute.getNotificationType()) {
                builder.addStatement("triggerMap.put(\"$N\", new $L())", type,
                        attribute.getWrapperClassName());
            }
        }

        return builder.build();
    }
}
