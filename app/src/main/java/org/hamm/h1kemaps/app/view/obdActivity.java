package org.hamm.h1kemaps.app.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.hamm.h1kemaps.app.R;
public class obdActivity  extends MapActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obd);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 通过包名获取要跳转的app，创建intent对象
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.github.pires.obd.reader");
                // 这里如果intent为空，就说名没有安装要跳转的应用嘛
                if (intent != null) {
                    // 这里跟Activity传递参数一样的嘛，不要担心怎么传递参数，还有接收参数也是跟Activity和Activity传参数一样
                    intent.putExtra("name", "Liu xiang");
                    intent.putExtra("birthday", "1983-7-13");
                    startActivity(intent);
                } else {
                    // 没有安装要跳转的app应用，提醒一下
                    Toast.makeText(getApplicationContext(), "你没有权限使用该功能 ", Toast.LENGTH_LONG).show();
                    //Intent intent1 = new Intent();
                    //intent1.setAction("android.intent.action.VIEW");
                   // Uri content_url = Uri.parse("https://pan.baidu.com/s/1oq8LyvXVPUStH8IPA6MJZQ/");//此处链接
                    //intent1.setData(content_url);
                    //startActivity(intent1);


                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
        System.exit(0);
    }
}
