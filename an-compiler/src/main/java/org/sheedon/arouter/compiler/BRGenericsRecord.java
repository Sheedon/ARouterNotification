package org.sheedon.arouter.compiler;

import org.sheedon.compilationtool.retrieval.core.IGenericsRecord;

import java.util.Objects;

/**
 * BindRouterCard 泛型记录
 * 包含对应位置下的泛型内容
 * BindRouterCard 中定义了一个泛型：「T」
 * 当前需要匹配同类型，并且设置到genericsArray制定的位置上
 * genericsArray : T
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/12/5 2:15 下午
 */
public class BRGenericsRecord implements IGenericsRecord,Cloneable {

    // 请求卡片
    public static final String T = "T";

    // 泛型组
    private String genericsArray = "";
    // 标志 false：没有填充，true：填充
    private boolean sign = false;

    /**
     * BindRouterCard 中定义了一个泛型：「T」
     * 当前需要匹配同类型，并且设置到genericsArray制定的位置上
     * genericsArray : T
     *
     * @param typeName        泛型类型
     * @param entityClassName 实体类型
     */
    public void put(String typeName, String entityClassName) {
        if (Objects.equals(typeName, T)) {
            genericsArray = entityClassName;
            sign = true;
        }
    }

    @Override
    public String get(String typeName) {
        return genericsArray;
    }

    /**
     * 获取 AbstractRequestRouter中泛型所绑定的实体类全类名
     *
     * @return 实体类全类名
     */
    public String get() {
        return genericsArray;
    }

    public boolean isCompeted() {
        return sign;
    }

    /**
     * 复制IGenericsRecord
     */
    @Override
    public BRGenericsRecord clone() {
        try {
            BRGenericsRecord record = (BRGenericsRecord) super.clone();
            record.genericsArray = genericsArray;
            return record;
        } catch (CloneNotSupportedException e) {
            BRGenericsRecord record = new BRGenericsRecord();
            record.sign = sign;
            record.genericsArray = genericsArray;
            return record;
        }
    }

    /**
     * 获取泛型集合
     */
    public String getGenericsArray() {
        return genericsArray;
    }
}
