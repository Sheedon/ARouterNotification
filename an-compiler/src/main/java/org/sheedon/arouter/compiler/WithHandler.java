package org.sheedon.arouter.compiler;

import java.util.HashMap;
import java.util.Map;

/**
 * Checker and container that contains the roadmap
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/12/25 12:06 下午
 */
class WithHandler {

    private static final Map<String, String> requireMap = new HashMap<String, String>() {
        {
            put(String.class.getCanonicalName(), "(%s != null && !%s.isEmpty())");
            put("short", "%s != 0");
            put(Short.class.getCanonicalName(), "%s != 0");
            put("int", "%s != 0");
            put(Integer.class.getCanonicalName(), "%s != 0");
            put("long", "%s != 0");
            put(Long.class.getCanonicalName(), "%s != 0");
            put("double", "%s != 0");
            put(Double.class.getCanonicalName(), "%s != 0");
            put("byte", "%s != 0");
            put(Byte.class.getCanonicalName(), "%s != 0");
            put("char", "%s != 0");
            put(Character.class.getCanonicalName(), "%s != 0");
            put("float", "%s != 0");
            put(Float.class.getCanonicalName(), "%s != 0");
            put(CharSequence.class.getCanonicalName(), "(%s != null || !%s.isEmpty())");
            put(Object.class.getCanonicalName(), "%s != null");
            put("boolean", "%s");
        }
    };

    private static final Map<String, String> withMap = new HashMap<String, String>() {
        {
            put(String.class.getCanonicalName(), "withString(%s, %s)");
            put("boolean", "withBoolean(%s, %s)");
            put(Boolean.class.getCanonicalName(), "withBoolean(%s, %s)");
            put("short", "withShort(%s, %s)");
            put(Short.class.getCanonicalName(), "withShort(%s, %s)");
            put("int", "withInt(%s, %s)");
            put(Integer.class.getCanonicalName(), "withInt(%s, %s)");
            put("long", "withLong(%s, %s)");
            put(Long.class.getCanonicalName(), "withLong(%s, %s)");
            put("double", "withDouble(%s, %s)");
            put(Double.class.getCanonicalName(), "withDouble(%s, %s)");
            put("byte", "withByte(%s, %s)");
            put(Byte.class.getCanonicalName(), "withByte(%s, %s)");
            put("char", "withChar(%s, %s)");
            put(Character.class.getCanonicalName(), "withChar(%s, %s)");
            put("float", "withFloat(%s, %s)");
            put(Float.class.getCanonicalName(), "withFloat(%s, %s)");
            put(CharSequence.class.getCanonicalName(), "withCharSequence(%s, %s)");
            put(Object.class.getCanonicalName(), "withObject(%s, %s)");
        }
    };

    /**
     * 核实不为空
     *
     * @param methodName 方法名
     * @param returnType 返回类型
     * @return String 合并方法名
     */
    String requireNonNull(String methodName, String returnType) {
        String result = requireMap.get(returnType);
        if (result == null) {
            return "";
        }
        Object[] methods = getMethods(result, methodName);
        return String.format(result, methods);
    }

    String withParameter(String key, String methodName, String returnType) {
        String result = withMap.get(returnType);
        if (result == null) {
            return "";
        }
        Object[] methods = getMethods(result, methodName, key);
        return String.format(result, methods);
    }


    /**
     * 根据%s的个数组合方法
     */
    private String[] getMethods(String result, String methodName) {
        int size = result.split("%s").length - 1;
        size = size <= 0 ? 1 : size;
        String[] methodNames = new String[size];
        for (int index = 0; index < size; index++) {
            methodNames[index] = methodName;
        }
        return methodNames;
    }

    /**
     * 根据%s的个数组合方法
     */
    private String[] getMethods(String result, String methodName, String key) {
        int size = result.split("%s").length;
        String[] methodNames = new String[size];
        methodNames[0] = String.format("\"%s\"", key);
        for (int index = 0; index < size - 1; index++) {
            methodNames[index + 1] = methodName;
        }
        return methodNames;
    }


}
