package org.sheedon.arouter.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.sheedon.arouter.model.BindRouterCard;
import org.sheedon.arouter.model.BindRouterWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * 路由装饰类的构建
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 10:00 下午
 */
class RouterWrapperBuilder {

    private final static String A_ROUTER_PACKAGE = "com.alibaba.android.arouter.launcher";

    private final ClassName aRouterClassName = ClassName.get(A_ROUTER_PACKAGE, "ARouter");

    // 元素处理工具类
    private final Elements mElementUtils;
    // 文件构造者
    private final Filer mFiler;
    // 消息处理器
    private final Messager mMessager;
    // 全部构建的包装类 参数
    private List<RouterWrapperAttribute> attributes = new ArrayList<>();
    // 字段核实并且填充的处理者
    private final WithHandler mWithHandler;

    RouterWrapperBuilder(Elements elements, Filer filer, Messager messager) {
        this.mElementUtils = elements;
        this.mFiler = filer;
        this.mMessager = messager;
        this.mWithHandler = new WithHandler();
    }


    /**
     * 构建路由装饰类
     *
     * @param cardAttribute   路由卡片参数
     * @param targetRoutePath 目标路径
     */
    void buildRouterWrapper(RouterCardAttribute cardAttribute, String targetRoutePath, ActivityAttribute targetActivity) {
        try {
            TypeElement typeElement = cardAttribute.getTypeElement();
            String className = typeElement.getSimpleName().toString();

            TypeName superclassTypeName = loadSuperclass(typeElement);
            String wrapperClassName = className + "Wrapper";

            // 路由适配器类
            ClassName routerAdapter = ClassName.get(typeElement);

            List<MethodSpec> methodSpecList = new ArrayList<>();
            methodSpecList.add(createBuildMethod(routerAdapter));
            methodSpecList.add(buildMethodImpl(targetRoutePath, cardAttribute.getSpareRoute()));
            ParameterizedTypeName typeName = (ParameterizedTypeName) superclassTypeName;
            methodSpecList.add(buildStartActivity(typeName.typeArguments, routerAdapter,
                    targetRoutePath, cardAttribute, targetActivity));

            TypeSpec wrapperTypeSpec = TypeSpec.classBuilder(wrapperClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .superclass(superclassTypeName)
                    .addMethods(methodSpecList)
                    .build();

            String packageName = mElementUtils.getPackageOf(typeElement).asType().toString();
            JavaFile javaFile = JavaFile.builder(packageName, wrapperTypeSpec)
                    .build();


            javaFile.writeTo(mFiler);

            attributes.add(new RouterWrapperAttribute(cardAttribute.getNotificationType(),
                    ClassName.get(packageName, wrapperClassName)));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 继承父类
     */
    private TypeName loadSuperclass(TypeElement typeElement) {
        TypeName superclassTypeName;
        TypeMirror superclass = typeElement.getSuperclass();
        String superclassInfo = superclass.toString();
        int genericStart = superclassInfo.indexOf("<");
        if (genericStart != -1) {
            String generic = superclassInfo.substring(genericStart + 1, superclassInfo.length() - 1);
            ClassName genericClassName = ClassNameUtils.loadClassNameByQualifiedName(generic, mMessager,
                    "generic exception. ", typeElement);
            superclassTypeName = ParameterizedTypeName.get(ClassName.get(BindRouterWrapper.class), genericClassName);
        } else {
            superclassTypeName = ParameterizedTypeName.get(BindRouterWrapper.class);
        }
        return superclassTypeName;
    }

    /**
     * 创建构造方法
     * public TestBindRouterWrapper() {
     * super(new TestBindRouter());
     * }
     */
    private MethodSpec createBuildMethod(ClassName className) {

        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("super(new $T())", className)
                .build();
    }


    /**
     * 构建抽象方法实现
     *
     * @param targetRoutePath 目标路径
     * @param spareRoutePath  备用路径
     * @return MethodSpec
     */
    private MethodSpec buildMethodImpl(String targetRoutePath, String spareRoutePath) {

        MethodSpec.Builder builder = MethodSpec.methodBuilder("attachRouter")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addStatement(String.format("setTargetRoute(\"%s\")", targetRoutePath));

        if (spareRoutePath != null && !spareRoutePath.isEmpty()) {
            builder.addStatement(String.format("setSpareRoute(\"%s\")", spareRoutePath));
        }

        return builder.build();
    }

    /**
     * 启动Activity
     *
     * @param typeArguments       泛型类型
     * @param routerAdapter       路由适配器类
     * @param targetRoutePath     目标路由路径
     * @param routerCardAttribute 路由适配器参数
     * @param targetActivityAttr  目标Activity参数
     * @return MethodSpec
     */
    private MethodSpec buildStartActivity(List<TypeName> typeArguments, ClassName routerAdapter,
                                          String targetRoutePath,
                                          RouterCardAttribute routerCardAttribute,
                                          ActivityAttribute targetActivityAttr) {
        TypeName routerClassName;
        if (typeArguments != null && !typeArguments.isEmpty()) {
            TypeName typeName = typeArguments.get(0);
            routerClassName = ParameterizedTypeName.get(ClassName.get(BindRouterCard.class), typeName);
        } else {
            routerClassName = ClassName.get(BindRouterCard.class);
        }

        MethodSpec.Builder builder = MethodSpec.methodBuilder("startActivity")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(routerClassName, "routerAdapter").build())
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "targetRoutePath").build())
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "spareRoute").build())
                .addStatement("$T adapter = ($T) routerAdapter", routerAdapter, routerAdapter);


        if (targetActivityAttr == null) {
            builder.addStatement("$T.getInstance().build(\"$N\").navigation()", aRouterClassName, targetRoutePath);
            return builder.build();
        }

        addWithParameter(targetActivityAttr, routerCardAttribute, targetRoutePath, builder);
        addWithParameter(routerCardAttribute.getSpareActivityAttribute(),
                routerCardAttribute, routerCardAttribute.getSpareRoute(), builder);


        return builder.build();
    }

    /**
     * 添加参数
     *
     * @param activityAttr        activity 中的自动绑定的字段
     * @param routerCardAttribute 路由适配器参数
     * @param builder             方法构建者
     */
    private void addWithParameter(ActivityAttribute activityAttr,
                                     RouterCardAttribute routerCardAttribute,
                                     String routerPath,
                                     MethodSpec.Builder builder) {

        if (activityAttr == null) {
            builder.addStatement("$T.getInstance().build(\"$N\").navigation()", aRouterClassName, routerPath);
            return;
        }

        List<ActivityAttribute.FieldAttribute> attributes = activityAttr.getAttributes();

        if (attributes != null && !attributes.isEmpty()) {

            StringBuilder conditionBuilder = new StringBuilder();
            for (ActivityAttribute.FieldAttribute attribute : attributes) {
                String methodName = routerCardAttribute.getParameters().get(attribute.getName()).toString();
                String condition = mWithHandler.requireNonNull("adapter." + methodName, attribute.getTypeName());
                if (condition == null || condition.isEmpty()) {
                    continue;
                }

                conditionBuilder.append(condition).append(" && ");
            }

            if (conditionBuilder.length() > 0) {
                conditionBuilder.delete(conditionBuilder.length() - 4, conditionBuilder.length());
            }

            builder.beginControlFlow("if($N)", conditionBuilder.toString());

            // with parameter
            conditionBuilder = new StringBuilder();
            for (ActivityAttribute.FieldAttribute attribute : attributes) {
                String methodName = routerCardAttribute.getParameters().get(attribute.getName()).toString();
                String withParameter = mWithHandler.withParameter(attribute.getName(), "adapter." + methodName, attribute.getTypeName());
                if (withParameter == null || withParameter.isEmpty()) {
                    continue;
                }

                conditionBuilder.append(".").append(withParameter);
            }
            builder.addStatement("$T.getInstance().build(\"$N\")$N.navigation()", aRouterClassName,
                    routerPath, conditionBuilder.toString());
            builder.addStatement("return");
            builder.endControlFlow();
        }
    }

    List<RouterWrapperAttribute> getAttributes() {
        return attributes;
    }
}
