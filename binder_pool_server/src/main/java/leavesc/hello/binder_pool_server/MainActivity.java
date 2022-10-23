package leavesc.hello.binder_pool_server;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 作者：leavesC
 * 时间：2019/4/4 10:58
 * 描述：
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv =new TextView(this);
        tv.setText("It's in binder_pool_server.MainActivity");
        setContentView(tv);
    }

}
