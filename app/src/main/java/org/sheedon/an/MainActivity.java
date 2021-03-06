package org.sheedon.an;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;

import org.sheedon.arouter.launcher.NotificationRouter;

public class MainActivity extends AppCompatActivity {

    private NotificationClient notificationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ARouter.init(getApplication());
        ARouter.openDebug();
        NotificationRouter.openDebug();
        NotificationRouter.init(getApplication());
        notificationClient = new NotificationClient();


    }

    public void onTouchClick(View view) {
        notificationClient.notifyInfo();
//        ARouter.getInstance().build("/Test/TargetActivity").navigation();
    }
}