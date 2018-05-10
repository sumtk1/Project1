package com.gloiot.chatsdk.DataBase;

import android.util.Log;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 创建人： zengming on 2017/9/25.
 * 功能：
 */

public class BaseObserver<T> implements Observer<T> {

    public static final String TAG = "BaseObserver";

    public BaseObserver() {
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull T t) {
    }

    @Override
    public void onError(@NonNull Throwable e) {
        Log.e(TAG, e.toString());
    }

    @Override
    public void onComplete() {
        Log.e(TAG, "耗时操作完成");
    }
}
