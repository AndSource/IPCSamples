package leavesc.hello.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 作者：leavesC
 * 时间：2019/4/4 10:46
 * 描述：
 */
public class MyService extends Service {

    public MyService() {
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        String stringField, int intField, boolean booleanField
        Demo demo =new Demo("what", 99,true);
        Log.i("sanbo",demo.toString());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}