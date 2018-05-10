package com.gloiot.hygounionmerchant.ui.webview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.abcaaa.photopicker.PhotoPicker;
import com.just.library.AgentWeb;
import com.zyd.wlwsdk.utils.L;

/**
 * Created by cenxiaozhong on 2017/5/14.
 * source CODE  https://github.com/Justson/AgentWeb
 */

public class AndroidInterface {

    private Handler deliver = new Handler(Looper.getMainLooper());
    private AgentWeb agent;
    private Context context;

    public static final int REQUEST_CODE_GOODSPIC = 5;
    private Activity activity;

    private CheckPermListener mListener;

    public interface CheckPermListener {
        //权限通过后的回调方法
        void superPermission();
    }

    public AndroidInterface(AgentWeb agent, Context context, Activity activity) {
        this.agent = agent;
        this.context = context;
        this.activity = activity;
    }

    private void selectPic() {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(false)
                .setPreviewEnabled(true)
                .start(activity, REQUEST_CODE_GOODSPIC);
    }

    @JavascriptInterface
    public void callAndroid(final String msg) {


        deliver.post(new Runnable() {
            @Override
            public void run() {

                Log.i("Info", "main Thread:" + Thread.currentThread());
                L.e("JS-Info", "main Thread:" + Thread.currentThread() + ",--msg=" + msg);
                Toast.makeText(context.getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();


                selectPic();

            }
        });


        Log.i("Info", "Thread:" + Thread.currentThread());

    }

}
