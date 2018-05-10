package com.gloiot.hygounionmerchant.ui;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.gloiot.chatsdk.SocketEvent;
import com.gloiot.chatsdk.socket.LinkServer;
import com.gloiot.hygounionmerchant.im.ChatEvent;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.zyd.wlwsdk.autolayout.config.AutoLayoutConifg;
import com.zyd.wlwsdk.server.network.HttpManager;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.NoDoubleClickUtils;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Dlt on 2017/8/12 16:04
 */
public class App extends MultiDexApplication {

    public ActivityStack mActivityStack = null; // Activity 栈
    private static App instance;
    // 用于存放倒计时时间
    public static Map<String, Long> timeMap;

    private static List<Activity> lists = new ArrayList<>();

    public static void addActivity(Activity activity) {
        lists.add(activity);
    }

    public static void clearActivity() {
        if (lists != null) {
            for (Activity activity : lists) {
                activity.finish();
            }
            lists.clear();
        }
    }

    /**
     * 分割 Dex 支持
     *
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public synchronized static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 日志调试打印
         * 测试 true，正式 false。
         */
        L.isDebug = true;
        instance = this;
        mActivityStack = new ActivityStack();   // 初始化Activity 栈
        NoDoubleClickUtils.initLastClickTime();
        initImageLoader(getApplicationContext());
        SharedPreferencesUtils.init(this, ConstantUtils.MYSP);
        // 网络请求
        HttpManager.init(ConstantUtils.URL);
        // 自动适配
        AutoLayoutConifg.getInstance().useDeviceSize();
        //Bugly初始化（20170928），在测试阶段建议设置成true，发布时设置为false
//        CrashReport.initCrashReport(getApplicationContext(), "55caa7ae82", false);

        // 连接消息服务器
        LinkServer.init();
        SocketEvent.init(this);

        ChatEvent.init(this);

    }

    /**
     * 缓存图片
     *
     * @param context
     */
    public static void initImageLoader(Context context) {
        //缓存文件的目录
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "hygounionmerchant/ImageLoader/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(3) //线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //将保存的时候的URI名称用MD5 加密
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024) // 内存缓存的最大值
                .diskCacheSize(50 * 1024 * 1024)  // 50 Mb sd卡(本地)缓存的最大值
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // 由原先的discCache -> diskCache
                .diskCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();
        //全局初始化此配置
        ImageLoader.getInstance().init(config);
    }

}
