package com.gloiot.hygounionmerchant.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by Dlt on 2017/8/12 16:36
 */
public class CommonUtils {

    private static SharedPreferences preferences = SharedPreferencesUtils.getInstance().getSharedPreferences();
    private static SharedPreferences.Editor editor = preferences.edit();

    /**
     * 设置标题栏
     *
     * @param context
     * @param titleString
     * @param tvmore
     */
    public static void setTitleBar(final Activity context, boolean isBack, String titleString, String tvmore) {
        ImageView back = (ImageView) context.findViewById(R.id.iv_toptitle_back);
        TextView title = (TextView) context.findViewById(R.id.tv_toptitle_title);
        TextView more = (TextView) context.findViewById(R.id.tv_toptitle_right);
        title.setText(titleString);
        more.setText(tvmore);
        if (isBack) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    View view = context.getWindow().peekDecorView();
                    if (view != null) {
                        InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    context.finish();
                }
            });
        } else {
            back.setVisibility(View.GONE);
        }


    }

    /**
     * 设置搜索栏
     *
     * @param context
     * @param titleString
     * @param tvmore
     */
//    public static void setSearchBar(final Activity context, boolean isBack, String titleString, String tvmore) {
//        ImageView back = (ImageView) context.findViewById(R.id.iv_search_back);
//        TextView title = (TextView) context.findViewById(R.id.tv_search_message);
//        TextView more = (TextView) context.findViewById(R.id.tv_search_right);
//        title.setText(titleString);
//        more.setText(tvmore);
//        if (isBack) {
//            back.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//                    View view = context.getWindow().peekDecorView();
//                    if (view != null) {
//                        InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                        inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                    }
//                    context.finish();
//                }
//            });
//        } else {
//            back.setVisibility(View.GONE);
//        }
//    }

    /**
     * 验证身份证号是否符合规范
     *
     * @param s
     * @return
     */
//    public static boolean isIDCardNumTrue(String s) throws ParseException {
//
//        IDCard ic = new IDCard();
//        if (ic.IDCardValidate(s).equals("")) {
//            return true;
//        } else {
//            return false;
//        }
//    }


    /**
     * 保存HashMap
     *
     * @param status
     * @param hashMap
     * @return
     */
    public static boolean saveMap(String status, HashMap<String, Object> hashMap) {
        editor.putInt(status, hashMap.size());
        int i = 0;
        for (HashMap.Entry<String, Object> entry : hashMap.entrySet()) {
            editor.remove(status + "_key_" + i);
            editor.putString(status + "_key_" + i, entry.getKey());
            editor.remove(status + "_value_" + i);
            editor.putString(status + "_value_" + i, (String) entry.getValue());
//            Log.e("save", entry.getKey()+"="+entry.getValue());
            i++;
        }
        return editor.commit();
    }

    /**
     * 取出HashMap
     *
     * @param status
     * @return
     */
    public static HashMap<String, Object> loadMap(String status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        int size = preferences.getInt(status, 0);
        for (int i = 0; i < size; i++) {
            String key = preferences.getString(status + "_key_" + i, null);
            String value = preferences.getString(status + "_value_" + i, null);
            hashMap.put(key, value);
        }
        return hashMap;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * encode编译URL
     *
     * @param kv
     * @return
     */
    public static String encodeUtli(String kv) {
        try {
            kv = java.net.URLEncoder.encode(kv, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return kv;
    }

    /**
     * decode编译URL
     *
     * @param kv
     * @return
     */
    public static String decodeUtli(String kv) {
        try {
            kv = java.net.URLDecoder.decode(kv, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return kv;
    }

    /**
     * 根据当前的ListView的列表项计算列表的尺寸
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    /**
     * 被ScrollView包含的GridView高度设置为wrap_content时只显示一行
     * 此方法用于动态计算GridView的高度(根据item的个数)
     */
    public static void reMesureGridViewHeight(GridView gridView) {
        // 获取GridView对应的Adapter
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int rows;
        int columns = 0;
        int horizontalBorderHeight = 0;
        Class<?> clazz = gridView.getClass();
        try {
            // 利用反射，取得每行显示的个数
            Field column = clazz.getDeclaredField("mRequestedNumColumns");
            column.setAccessible(true);
            columns = (Integer) column.get(gridView);

            // 利用反射，取得横向分割线高度
            Field horizontalSpacing = clazz.getDeclaredField("mRequestedHorizontalSpacing");
            horizontalSpacing.setAccessible(true);
            horizontalBorderHeight = (Integer) horizontalSpacing.get(gridView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 判断数据总数除以每行个数是否整除。不能整除代表有多余，需要加一行
        if (listAdapter.getCount() % columns > 0) {
            rows = listAdapter.getCount() / columns + 1;
        } else {
            rows = listAdapter.getCount() / columns;
        }
        int totalHeight = 0;
        for (int i = 0; i < rows; i++) { // 只计算每项高度*行数
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + horizontalBorderHeight * (rows - 1);// 最后加上分割线总高度
        gridView.setLayoutParams(params);
    }

    /**
     * dpתpx
     */
    public static int dip2px(Context ctx, float dpValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * pxתdp
     */
    public static int px2dip(Context ctx, float pxValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 强制隐藏输入法键盘
     */
    public static void hideInput(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 设置图片(自定义获取失败显示头像)
     *
     * @param imageView
     * @param imgUrl
     */
    public static void setDisplayImage(ImageView imageView, String imgUrl, int round, int resid) {

        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(resid) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(resid) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(resid) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(round)) // 设置成圆角图片
                .build(); // 构建完成

        ImageLoader.getInstance().displayImage(imgUrl, imageView, options);
    }

    /**
     * 设置图片
     *
     * @param imageView
     * @param imgUrl
     */
    public static void setDisplayImageOptions(ImageView imageView, String imgUrl, int round) {

        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_image) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(Color.argb(0, 0, 0, 0)) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(Color.argb(0, 0, 0, 0)) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(round)) // 设置成圆角图片
                .build(); // 构建完成

        ImageLoader.getInstance().displayImage(imgUrl, imageView, options);
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String currentTime = formatter.format(curDate);//获取手机系统时间是个漏洞，用户可手动更改时间。
        return currentTime;
    }

    /**
     * 比较两个时间字符串(比较日期前后),s1在s2之后返回true
     */
    public static boolean dateCompare(String s1, String s2) {

        try {
            //设定时间的模板
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //得到指定模范的时间
            Date d1 = sdf.parse(s1);
            Date d2 = sdf.parse(s2);
//            L.e("日期", "d1=" + d1 + ",d2=" + d2);
            //比较
            //d1在d2之后或相等
            return d1.getTime() >= d2.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 比较两个时间字符串(用于判断接收到的消息是否过时，过时不打印)
     */
    public static boolean timeCompare(String s1, String s2) throws Exception {

        //设定时间的模板
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //得到指定模范的时间
        Date d1 = sdf.parse(s1);
        Date d2 = sdf.parse(s2);
        //比较
        //d1比d2大10分钟
        return Math.abs(((d1.getTime() - d2.getTime()) / (1 * 1 * 60 * 1000))) >= 10;

    }

    /**
     * 获取版本名
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    /**
     * 用于虚拟键适配PopupWindow显示位置
     *
     * @param context
     * @return
     */
    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            } catch (NoSuchMethodException e) {
            }
        }

        return size;
    }

    /**
     * 清除账号个人数据
     */
    public static void clearPersonalData() {
        // 账号
        editor.putString(ConstantUtils.SP_USERACCOUNT, "");
        //随机码
        editor.putString(ConstantUtils.SP_RANDOMCODE, "");
        // 头像
        editor.putString(ConstantUtils.SP_ACCOUNTPORTRAIT, "");
        // 昵称
        editor.putString(ConstantUtils.SP_ACCOUNTNICKNAME, "");
        //账号名称（商户名称）
        editor.putString(ConstantUtils.SP_ACCOUNTNAME, "");
        //用户支付密码状态（已设置/未设置）
        editor.putString(ConstantUtils.SP_ACCOUNTPAYPWDSTATE, "");
        //和环游购绑定状态
        editor.putString(ConstantUtils.SP_BINDINGHYGOSTATE, "");
        //省
        editor.putString(ConstantUtils.SP_ACCOUNTSHENG, "");
        //市
        editor.putString(ConstantUtils.SP_ACCOUNTSHI, "");
        //区
        editor.putString(ConstantUtils.SP_ACCOUNTQU, "");
        //经度
        editor.putString(ConstantUtils.SP_ACCOUNTLONGITUDE, "");
        //纬度
        editor.putString(ConstantUtils.SP_ACCOUNTLATITUDE, "");
        //账号区域-位置信息详细描述
        editor.putString(ConstantUtils.SP_ACCOUNTLOCATIONDETAILINFO, "");
        //环游购账号
        editor.putString(ConstantUtils.SP_HYGOACCOUNT, "");
        //资质认证状态
        editor.putString(ConstantUtils.SP_ZIZHIRENZHENGSTATE, "");
        //环游购姓名
        editor.putString(ConstantUtils.SP_HYGONAME, "");
        //环游购手机号
        editor.putString(ConstantUtils.SP_HYGOPHONENUM, "");
    }

    /**
     * 判断是否为整数
     *
     * @param str 传入的字符串
     * @return 是整数返回true, 否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断是否为邮箱(这个方法貌似有问题，不用)
     *
     * @param str 传入的字符串
     * @return 是邮箱返回true, 否则返回false
     */
    public static boolean isEmail(String str) {
        Pattern pattern = Pattern.compile("[\\\\w]+@[\\\\w]+.[\\\\w]+");
        return pattern.matcher(str).matches();
    }

}
