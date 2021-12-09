package org.sheedon.arouter.compiler;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;

import org.sheedon.arouter.annotation.BindParameter;
import org.sheedon.arouter.annotation.BindRouter;
import org.sheedon.arouter.annotation.RouteStrategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * ARouter 通知 注解执行器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 2:15 下午
 */
@AutoService(Processor.class)
public class ARouterNotificationProcessor extends AbstractProcessor {

    private Messager mMessager;
    private Filer mFiler;
    private Elements mElementUtils;
    String moduleName = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();

        Map<String, String> options = processingEnv.getOptions();
        moduleName = options.get("AROUTER_MODULE_NAME");
        if (moduleName != null && !moduleName.isEmpty()) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
        }

    }

    /**
     * 获取需要处理的注解内容
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(BindRouter.class.getCanonicalName());
        supportTypes.add(BindParameter.class.getCanonicalName());
        supportTypes.add(RouteStrategy.class.getCanonicalName());
        supportTypes.add(Route.class.getCanonicalName());
        supportTypes.add(Autowired.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnvironment) {

        if (annotations.size() > 0) {
            return processNotification(roundEnvironment);
        } else {
            return false;
        }
    }

    /**
     * 执行通知注解文件生成的流程
     *
     * @param roundEnvironment 当前这一轮的环境
     * @return 执行完成
     */
    private boolean processNotification(RoundEnvironment roundEnvironment) {
        CommunicantBuilder builder = new CommunicantBuilder(mFiler);
        builder.addModuleName(moduleName);

        // 填充Route注解+ Activity全类名，用于后续备份路径查询操作
        Map<String, String> routeValues = new HashMap<>();
        Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(Route.class);
        for (Element element : routeElements) {
            Route route = element.getAnnotation(Route.class);
            routeValues.put(route.path(), ((TypeElement) element).getQualifiedName().toString());
        }

        // 填充备用路由到对应的路由Card上
        Map<String, RouterCardAttribute> routerCardMap = new HashMap<>();

        // 获取路由Card中 所有 @BindParameter （name 和 当前方法返回类型）
        Set<? extends Element> parameterElements = roundEnvironment.getElementsAnnotatedWith(BindParameter.class);
        for (Element element : parameterElements) {
            ExecutableElement executableElement = (ExecutableElement) element;
            TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
            String qualifiedName = typeElement.getQualifiedName().toString();
            BindParameter parameter = element.getAnnotation(BindParameter.class);
            RouterCardAttribute cardAttribute = routerCardMap.computeIfAbsent(qualifiedName, card -> new RouterCardAttribute());
            cardAttribute.addParameter(parameter.name(), executableElement.getReturnType());
        }

        Map<String, ActivityAttribute> autowiredMap = new HashMap<>();
        // 获取Activity中 所有 @Autowired 参数 + Field类型 存到 autowiredMap
        Set<? extends Element> autowiredElements = roundEnvironment.getElementsAnnotatedWith(Autowired.class);
        for (Element element : autowiredElements) {
            VariableElement variableElement = (VariableElement) element;
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            String qualifiedName = typeElement.getQualifiedName().toString();
            Autowired parameter = element.getAnnotation(Autowired.class);
            ActivityAttribute cardAttribute = autowiredMap.computeIfAbsent(qualifiedName, card -> new ActivityAttribute());
            cardAttribute.addFieldAttribute(parameter.name(), element.asType());
        }


        // 路由策略中核实 备用路径有，但是Activity中没有，则编译不过
        // 通知类型为空 编译不过
        // 当前路由策略中的@BindParameter 与备用路径下 Activity 的 @Autowired 对应不上，则编译不过
        Set<? extends Element> spareRouteElements = roundEnvironment.getElementsAnnotatedWith(RouteStrategy.class);
        for (Element element : spareRouteElements) {
            TypeElement typeElement = (TypeElement) element;
            String qualifiedName = typeElement.getQualifiedName().toString();
            RouteStrategy route = element.getAnnotation(RouteStrategy.class);
            String path = route.spareRoute();
            if (!path.equals("") && !routeValues.containsKey(path)) {
                // 路由Card的备用路径，没有找到Route下的路径
                mMessager.printMessage(Diagnostic.Kind.ERROR, "The alternate path of RouterCard where " + qualifiedName
                        + ", the path under Route was not found", element);
                return false;
            }
            String[] notificationType = route.notificationType();
            if (notificationType.length == 0) {
                // 路由策略配置的通知类型，若通知为空，则提示错误
                mMessager.printMessage(Diagnostic.Kind.ERROR, qualifiedName + "'s notificationType cannot empty", element);
                return false;
            }

            RouterCardAttribute cardAttribute = routerCardMap.computeIfAbsent(qualifiedName, card -> new RouterCardAttribute());
            cardAttribute.addRouteInfo(typeElement, path, notificationType);

            String activityQualifiedName = routeValues.get(path);
            // ActivityAttribute 中包含 Field的属性和注解name
            ActivityAttribute activityAttribute = autowiredMap.get(activityQualifiedName);
            // 核实绑定路由卡片类信息
            checkBindRouterCard(activityAttribute, cardAttribute, qualifiedName, activityQualifiedName, element);
        }

        RouterWrapperBuilder wrapperBuilder = new RouterWrapperBuilder(mElementUtils, mFiler, mMessager);
        Set<? extends Element> activityElements = roundEnvironment.getElementsAnnotatedWith(BindRouter.class);
        for (Element element : activityElements) {
            TypeElement typeElement = (TypeElement) element;
            // 获取Activity的全类名，并且在autowiredMap中搜索得到对应 ActivityAttribute
            String activityQualifiedName = typeElement.getQualifiedName().toString();
            // ActivityAttribute 中包含 Field的属性和注解name
            ActivityAttribute activityAttribute = autowiredMap.get(activityQualifiedName);

            // 获取目标路径
            String targetRoute = checkAnnotation(element);
            // 获取 BindRouter 的值，也就是路由Class
            ClassName className = checkBindRouter(element, routerCardMap);
            // 路由Card 全类名
            String canonicalName = Objects.requireNonNull(className).canonicalName();
            RouterCardAttribute cardAttribute = routerCardMap.get(canonicalName);

            // 核实绑定路由卡片类信息
            checkBindRouterCard(activityAttribute, cardAttribute, canonicalName, activityQualifiedName, element);

            // 传入 路由Card 用于构建
            wrapperBuilder.buildRouterWrapper(cardAttribute, targetRoute);
        }

        builder.buildClass(wrapperBuilder.getAttributes());
        return false;
    }

    /**
     * 核实activity 是否有 Route 注解，不存在则编译不过，返回路径
     */
    private String checkAnnotation(Element element) {
        // 获取注解值，若能拿到则返回，否则错误提示
        Route annotation = element.getAnnotation(Route.class);
        if (annotation == null || annotation.path().equals("")) {
            String className = element.getSimpleName().toString();
            mMessager.printMessage(Diagnostic.Kind.ERROR, "please add annotation @Route to " + className, element);
            return null;
        }
        return annotation.path();
    }

    /**
     * 由于element.getAnnotation(BindRouter.class) 拿不到Value 的Class信息
     * 所以采用遍历的方式获取，并且通过ClassName.get(packageName, className) 得到ClassName
     */
    private ClassName checkBindRouter(Element element, Map<String, RouterCardAttribute> routerCardMap) {

        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            DeclaredType annotationType = mirror.getAnnotationType();
            if (!annotationType.toString().equals(BindRouter.class.getName())) {
                continue;
            }

            Map<? extends ExecutableElement, ? extends AnnotationValue> values = mirror.getElementValues();
            for (ExecutableElement executableElement : values.keySet()) {
                if (executableElement.getSimpleName().toString().equals("routerClass")) {
                    AnnotationValue annotationValue = values.get(executableElement);

                    String qualifiedName = annotationValue.getValue().toString();
                    RouterCardAttribute cardAttribute = routerCardMap.get(qualifiedName);
                    if (cardAttribute != null) {
                        TypeElement typeElement = cardAttribute.getTypeElement();
                        return ClassName.get(typeElement);
                    }

                    return ClassNameUtils.loadClassNameByQualifiedName(qualifiedName, mMessager,
                            "Method threw 'java.lang.ClassNotFoundException' exception. " + qualifiedName, executableElement);
                }
            }
        }

        mMessager.printMessage(Diagnostic.Kind.ERROR, "please add annotation @BindRouter " + element.getSimpleName().toString(), element);
        return null;
    }

    /**
     * 目标Activity中的Autowired 和 「绑定路由Class」BindParameter 是否全一致，名称+类型 不一致，编译不过
     *
     * @param activityAttribute     activity 中 @Autowired 的 Filed 信息
     * @param cardAttribute         路由绑定Card @BindParameter 的 Method 信息
     * @param canonicalName         路由Card 全类名
     * @param activityQualifiedName activity 全类名
     */
    private void checkBindRouterCard(ActivityAttribute activityAttribute, RouterCardAttribute cardAttribute,
                                     String canonicalName, String activityQualifiedName, Element element) {
        // 没有 Filed
        if (activityAttribute == null || activityAttribute.getAttributes().isEmpty()) {
            return;
        }
        // 必定有 带@Autowired的Filed
        List<ActivityAttribute.FieldAttribute> attributes = activityAttribute.getAttributes();

        // 绑定Card 中 @BindParameter 的 Method 信息
        if (cardAttribute == null || cardAttribute.getParameters().isEmpty()) {
            // 路由Card的@BindParameter 缺失 与 activity的@Autowired 的对应数据
            mMessager.printMessage(Diagnostic.Kind.ERROR, "The @BindParameter of the " + canonicalName
                    + " is missing and the corresponding data of @Autowired of the " + activityQualifiedName, element);
            return;
        }

        Map<String, String> parameters = cardAttribute.getParameters();
        for (ActivityAttribute.FieldAttribute attribute : attributes) {
            String name = attribute.getName();
            String returnType = parameters.get(name);
            if (returnType == null) {
                // 路由Card的@BindParameter 找不到对应 activity中@Autowired 的 name
                mMessager.printMessage(Diagnostic.Kind.ERROR, "The @BindParameter of the " + canonicalName
                        + " cannot find the " + name + " of @Autowired in the corresponding " + activityQualifiedName, element);
                return;
            }

            if (Objects.equals(returnType, attribute.getTypeName())) {
                continue;
            }

            // 路由Card的@BindParameter注解的Method返回值 与 activity中@Autowired 的 Filed 类型不一致
            mMessager.printMessage(Diagnostic.Kind.ERROR, "The method return value of the @BindParameter annotation of the " + canonicalName
                    + " is inconsistent with the Filed " + name + " type of @Autowired in the " + activityQualifiedName, element);
            return;
        }

    }
}
