package org.sheedon.arouter.compiler;

import org.sheedon.compilationtool.retrieval.core.IGenericsRecord;

import java.util.Objects;

import javax.lang.model.type.TypeMirror;

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
public class BRGenericsRecord implements IGenericsRecord, Cloneable {

    // 请求卡片
    public static final String T = "T";

    // 泛型组
    private TypeMirror typeMirror;
    // 标志 false：没有填充，true：填充
    private boolean sign = false;

    /**
     * BindRouterCard 中定义了一个泛型：「T」
     * 当前需要匹配同类型，并且设置到genericsArray制定的位置上
     * genericsArray : T
     *
     * @param typeName   泛型类型
     * @param typeMirror 实体类型
     */
    @Override
    public void put(String typeName, TypeMirror typeMirror) {
        if (Objects.equals(typeName, T)) {
            this.typeMirror = typeMirror;
            sign = true;
        }
    }

    @Override
    public TypeMirror get(String typeName) {
        return typeMirror;
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
            record.typeMirror = typeMirror;
            return record;
        } catch (CloneNotSupportedException e) {
            BRGenericsRecord record = new BRGenericsRecord();
            record.sign = sign;
            record.typeMirror = typeMirror;
            return record;
        }
    }

    /**
     * 获取泛型集合
     */
    public TypeMirror getGenericsArray() {
        return typeMirror;
    }
}
