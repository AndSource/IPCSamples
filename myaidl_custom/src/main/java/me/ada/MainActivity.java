package me.ada;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("Hello!");
        tv.setTextSize(20);
        tv.setGravity(Gravity.CENTER);
        setContentView(tv);
        call(tv);
    }

    private void call(TextView tv) {
        LL.i("----->" + Ipp.Stub.class);
        if (ipp == null) {
            bindService();
        }
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tv.setText("加法(1+2):" + ipp.add(1, 2));
                } catch (Throwable e) {
                    LL.e(e);
                }
            }
        });

    }

    private me.ada.Ipp ipp = null;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LL.i("onServiceConnected  name:" + name + "\r\n\tservice:" + service);
            ipp = Ipp.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LL.i("onServiceDisconnected  name:" + name);
            ipp = null;
        }
    };

    private void bindService() {
        LL.i("inside bindservice");
        Intent in = new Intent();
        in.setComponent(new ComponentName("me.ada", "me.ada.AidlService"));
        bindService(in, serviceConnection, Context.BIND_AUTO_CREATE);
    }
}