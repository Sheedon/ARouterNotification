package org.sheedon.an;

import com.alibaba.android.arouter.launcher.ARouter;

import org.sheedon.arouter.annotation.BindParameter;
import org.sheedon.arouter.annotation.RouteStrategy;
import org.sheedon.arouter.model.BindRouterCard;

import java.util.Random;

/**
 * java类作用描述
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/11/4 3:19 下午
 */
@RouteStrategy(spareRoute = "/Test/SpareActivity", notificationType = "132")
public class TestBindRouter extends BindRouterCard<String> {

    @BindParameter(name = "id")
    public long getId() {
        return 666L;
    }

    @BindParameter(name = "name")
    public String getName() {
        return "aaa";
    }

    @Override
    protected void startActivity(String targetRoute, String spareRoute) {
        boolean nextBoolean = new Random().nextBoolean();
        if (nextBoolean) {
            ARouter.getInstance().build(targetRoute)
                    .withLong("id", getId())
                    .withString("name", getName())
                    .navigation();
        } else {
            ARouter.getInstance().build(spareRoute).navigation();
        }
    }


    @Override
    protected String getErrorMessage() {
        return null;
    }

}
