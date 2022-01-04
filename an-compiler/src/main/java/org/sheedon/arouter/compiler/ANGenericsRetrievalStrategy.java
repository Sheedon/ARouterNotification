package org.sheedon.arouter.compiler;

import org.sheedon.arouter.model.BindRouterCard;
import org.sheedon.compilationtool.retrieval.core.IGenericsRecord;
import org.sheedon.compilationtool.retrieval.core.IRetrieval;

import java.util.HashSet;
import java.util.Set;

/**
 * 路由通知泛型检索策略
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/4 10:34 上午
 */
public class ANGenericsRetrievalStrategy extends IRetrieval.AbstractRetrieval{

    private final Set<String> packages = new HashSet<String>(){
        {
            add("java.");
        }
    };

    @Override
    public String canonicalName() {
        return BindRouterCard.class.getCanonicalName();
    }

    @Override
    public Set<String> filterablePackages() {
        return packages;
    }

    @Override
    public IGenericsRecord genericsRecord() {
        return new BRGenericsRecord();
    }
}
