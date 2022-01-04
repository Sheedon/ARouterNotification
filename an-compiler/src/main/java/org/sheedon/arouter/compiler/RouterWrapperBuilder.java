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
import org.sheedon.compilationtool.retrieval.core.RetrievalClassModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
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
    // 全部构建的包装类 参数
    private final List<RouterWrapperAttribute> attributes = new ArrayList<>();
    // 字段核实并且填充的处理者
    private final WithHandler mWithHandler;

    RouterWrapperBuilder(Elements elements, Filer filer) {
        this.mElementUtils = elements;
        this.mFiler = filer;
        this.mWithHandler = new WithHandler();
    }


    /**
     * 构建路由装饰类
     *
     * @param cardAttribute   路由卡片参数
     * @param targetRoutePath 目标路径
     * @param strategy        路由检索者策略
     */
    void buildRouterWrapper(RouterCardAttribute cardAttribute, String targetRoutePath,
                            ActivityAttribute targetActivity, ANGenericsRetrievalStrategy strategy) {
        try {
            TypeElement typeElement = cardAttribute.getTypeElement();
            String className = typeElement.getSimpleName().toString();

            // 获取泛型
            RetrievalClassModel retrievalClassModel = strategy.retrievalClassMap().get(typeElement.getQualifiedName().toString());
            String genericName = retrievalClassModel.getRecord().get(BRGenericsRecord.T);

            TypeName superclassTypeName = loadSuperclass(genericName);
            String wrapperClassName = className + "Wrapper";

            // 路由适配器类
            ClassName routerAdapter = ClassName.get(typeElement);

            List<MethodSpec> methodSpecList = new ArrayList<>();
            methodSpecList.add(createBuildMethod(routerAdapter));
            methodSpecList.add(buildMethodImpl(targetRoutePath, cardAttribute.getSpareRoute()));


            methodSpecList.add(buildStartActivity(genericName, routerAdapter,
                    cardAttribute, targetActivity));

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
     *
     * @param genericName 泛型名
     */
    private TypeName loadSuperclass(String genericName) {
        return ParameterizedTypeName.get(ClassName.get(BindRouterWrapper.class), ClassName.bestGuess(genericName));
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
     * @param genericName         泛型名
     * @param routerAdapter       路由适配器类
     * @param routerCardAttribute 路由适配器参数
     * @param targetActivityAttr  目标Activity参数
     * @return MethodSpec
     */
    private MethodSpec buildStartActivity(String genericName, ClassName routerAdapter,
                                          RouterCardAttribute routerCardAttribute,
                                          ActivityAttribute targetActivityAttr) {
        TypeName routerClassName = ParameterizedTypeName.get(ClassName.get(BindRouterCard.class), ClassName.bestGuess(genericName));

        MethodSpec.Builder builder = MethodSpec.methodBuilder("startActivity")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(routerClassName, "routerAdapter").build())
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "targetRoutePath").build())
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "spareRoutePath").build())
                .addStatement("$T adapter = ($T) routerAdapter", routerAdapter, routerAdapter);


        if (targetActivityAttr == null) {
            builder.addStatement("$T.getInstance().build(targetRoutePath).navigation()", aRouterClassName);
            return builder.build();
        }

        addWithParameter(targetActivityAttr, routerCardAttribute, "targetRoutePath", false, builder);
        addWithParameter(routerCardAttribute.getSpareActivityAttribute(),
                routerCardAttribute, "spareRoutePath",
                routerCardAttribute.getSpareRoute() == null || routerCardAttribute.getSpareRoute().isEmpty(), builder);


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
                                  boolean isEmpty,
                                  MethodSpec.Builder builder) {

        if (isEmpty) {
            return;
        }

        if (activityAttr == null) {
            builder.addStatement("$T.getInstance().build($N).navigation()", aRouterClassName, routerPath);
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
            builder.addStatement("$T.getInstance().build($N)$N.navigation()", aRouterClassName,
                    routerPath, conditionBuilder.toString());
            builder.addStatement("return");
            builder.endControlFlow();
        }
    }

    List<RouterWrapperAttribute> getAttributes() {
        return attributes;
    }
}
