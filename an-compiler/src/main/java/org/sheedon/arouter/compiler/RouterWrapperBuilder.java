package org.sheedon.arouter.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

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

    // 元素处理工具类
    private final Elements mElementUtils;
    // 文件构造者
    private final Filer mFiler;
    // 消息处理器
    private final Messager mMessager;
    // 全部构建的包装类 参数
    private List<RouterWrapperAttribute> attributes = new ArrayList<>();

    RouterWrapperBuilder(Elements elements, Filer filer, Messager messager) {
        this.mElementUtils = elements;
        this.mFiler = filer;
        this.mMessager = messager;
    }


    /**
     * 构建路由装饰类
     *
     * @param cardAttribute   路由卡片参数
     * @param targetRoutePath 目标路径
     */
    void buildRouterWrapper(RouterCardAttribute cardAttribute, String targetRoutePath) {
        try {
            TypeElement typeElement = cardAttribute.getTypeElement();
            String className = typeElement.getSimpleName().toString();

            TypeName superclassTypeName = loadSuperclass(typeElement);
            String wrapperClassName = className + "Wrapper";

            List<MethodSpec> methodSpecList = new ArrayList<>();
            methodSpecList.add(createBuildMethod(ClassName.get(typeElement)));
            methodSpecList.add(buildMethodImpl(targetRoutePath, cardAttribute.getSpareRoute()));

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
                    "generic exception. ");
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

    List<RouterWrapperAttribute> getAttributes() {
        return attributes;
    }
}
