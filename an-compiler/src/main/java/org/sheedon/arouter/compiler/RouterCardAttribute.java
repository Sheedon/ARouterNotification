package org.sheedon.arouter.compiler;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * 路由卡片参数
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/5 9:13 上午
 */
class RouterCardAttribute {

    private TypeElement typeElement;
    private String spareRoute;
    private ActivityAttribute spareActivityAttribute;
    private String[] notificationType;
    private Map<String, ExecutableElement> parameters = new HashMap<>();

    public void addRouteInfo(TypeElement typeElement, String path,
                             String[] notificationType) {
        this.typeElement = typeElement;
        this.spareRoute = path;
        this.notificationType = notificationType;
    }

    public void addParameter(String name, ExecutableElement typeMirror) {
        parameters.put(name, typeMirror);
    }

    public String getSpareRoute() {
        return spareRoute;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public String[] getNotificationType() {
        return notificationType;
    }

    public Map<String, ExecutableElement> getParameters() {
        return parameters;
    }


    public void addSpareActivity(ActivityAttribute activityAttribute) {
        this.spareActivityAttribute = activityAttribute;
    }

    public ActivityAttribute getSpareActivityAttribute() {
        return spareActivityAttribute;
    }
}
