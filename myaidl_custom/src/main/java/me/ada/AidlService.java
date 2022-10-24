package me.ada;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class AidlService extends Service {


    private Ipp.Stub ss = new Ipp.Stub() {
        @Override
        public int add(int a, int b) throws RemoteException {
            LL.i("AidlService add in stub()");
            return 2 * (a + b);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return ss;
    }


}