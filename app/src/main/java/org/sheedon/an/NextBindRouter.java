package org.sheedon.an;

import org.sheedon.arouter.annotation.BindParameter;
import org.sheedon.arouter.annotation.RouteStrategy;

/**
 * java类作用描述
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 3:19 下午
 */
@RouteStrategy(spareRoute = "/Test/SpareActivity", notificationType = {"119"})
public class NextBindRouter extends ParentRouterCard {

    @BindParameter(name = "id")
    public long getId() {
        return 666L;
    }

    @BindParameter(name = "name")
    public String getName() {
        return "aaa";
    }


    @Override
    protected String getErrorMessage() {
        return null;
    }

}
