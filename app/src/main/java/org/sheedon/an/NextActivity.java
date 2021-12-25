package org.sheedon.an;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;

import org.sheedon.arouter.annotation.BindRouter;

@Route(path = "/Test/NextActivity")
@BindRouter(routerClass = NextBindRouter.class)
public class NextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
    }
}