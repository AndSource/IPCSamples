package leavesc.hello.ipc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 作者：leavesC
 * 时间：2019/4/4 10:46
 * 描述：
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv =new TextView(this);
        tv.setText("It's in app.MainActivity");
        setContentView(tv);
        startService(new Intent(this, leavesc.hello.ipc.MyService.class));
    }

}