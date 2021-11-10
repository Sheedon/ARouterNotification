package org.sheedon.arouter.compiler;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.type.TypeMirror;

/**
 * 记录 Activity 中 @Autowired 所记录的参数内容
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/5 10:05 上午
 */
class ActivityAttribute {

    private final List<FieldAttribute> attributes = new LinkedList<>();

    public void addFieldAttribute(String name, TypeMirror typeMirror){
        attributes.add(FieldAttribute.build(name,typeMirror.toString()));
    }

    public List<FieldAttribute> getAttributes() {
        return attributes;
    }

    static class FieldAttribute {
        // 注解内定义的name
        private String name;
        private String typeName;

        public static FieldAttribute build(String name, String typeName) {
            FieldAttribute attribute = new FieldAttribute();
            attribute.name = name;
            attribute.typeName = typeName;
            return attribute;
        }

        public String getName() {
            return name;
        }

        public String getTypeName() {
            return typeName;
        }
    }
}
