package org.sheedon.an;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import org.sheedon.arouter.annotation.BindRouter;

@Route(path = "/Test/TargetActivity")
@BindRouter(routerClass = TestBindRouter.class)
public class TargetActivity extends AppCompatActivity {

    @Autowired(name = "id", desc = "设备ID")
    long id;

    @Autowired(name = "name", desc = "设备名称")
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        ARouter.getInstance().inject(this);

        Log.v("SXD", "id:" + id);
        Log.v("SXD", "name:" + name);
    }
}