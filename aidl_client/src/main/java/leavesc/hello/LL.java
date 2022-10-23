package leavesc.hello;

import android.text.TextUtils;
import android.util.Log;

public class LL {
    private static final String TAG = "sanbo";

    public static void e(Throwable e) {
        Log.e(TAG, Log.getStackTraceString(e));
    }

    public static void d(String tag, String info) {
        if (TextUtils.isEmpty(info)) {
            return;
        }
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        if (!tag.startsWith(TAG)) {
            tag = TAG + "." + tag;
        }
        Log.d(tag, info);
    }
    public static void i(String info) {
      i(TAG,info);
    }
    public static void i(String tag, String info) {
        if (TextUtils.isEmpty(info)) {
            return;
        }
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        if (!tag.startsWith(TAG)) {
            tag = TAG + "." + tag;
        }
        Log.i(tag, info);
    }

    public static void e(String tag, String info) {
        if (TextUtils.isEmpty(info)) {
            return;
        }
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        if (!tag.startsWith(TAG)) {
            tag = TAG + "." + tag;
        }
        Log.e(tag, info);
    }
}
