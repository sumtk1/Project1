package com.gloiot.hygounionmerchant.ui;

import android.app.Activity;

import java.util.LinkedList;

/**
 * Created by JinzLin on 2017/4/28.
 * 管理activity
 */
public class ActivityStack {

    /**
     * 存activity的list，方便管理activity
     */
    public LinkedList<Activity> activityList = null;

    public ActivityStack() {
        activityList = new LinkedList<Activity>();
    }

    /**
     * 自定义-存activity的list，用于部分Activity的销毁
     */
    public LinkedList<Activity> customActivityList = new LinkedList<Activity>();

    /**
     * 自定义添加-存放的Activity
     */
    public void customAddActivity(Activity activity) {
        customActivityList.add(activity);
    }

    /**
     * 自定义删除-所有存放的Activity
     */
    public void customRemoveActivity() {
        for (int i = 0; i < customActivityList.size(); i++) {
            customActivityList.remove(i);
        }
    }

    /**
     * 将Activity添加到activityList中
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    /**
     * 获取栈顶Activity
     *
     * @return
     */
    public Activity getLastActivity() {
        return activityList.getLast();
    }

    /**
     * 将Activity移除
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        if (null != activity && activityList.contains(activity)) {
            activityList.remove(activity);
        }
    }

    /**
     * 判断某一Activity是否在运行
     *
     * @param className
     * @return
     */
    public boolean isActivityRunning(String className) {
        if (className != null) {
            for (Activity activity : activityList) {
                if (activity.getClass().getName().equals(className)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 退出所有的Activity
     */
    public void finishAllActivity() {
        for (Activity activity : activityList) {
            if (null != activity) {
                activity.finish();
            }
        }
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
            // 杀死该应用进程
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
        }
    }

}
