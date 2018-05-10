package com.zyd.wlwsdk.widge;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.youth.banner.Banner;
import com.zyd.wlwsdk.widge.pulltorefresh.PullableScrollView;

/**
 * Created by jinzlin on 17/6/10.
 * 解决PullableScrollView与Banner滑动冲突
 */

public class MBanner extends Banner{

    private float mDownX, mDownY;

    private View parentView;

    public void setParentView(View parentView) {
        this.parentView = parentView;
    }

    public MBanner(Context context) {
        super(context);
    }

    public MBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录下按下时当前的xy左标
                mDownX = ev.getX();
                mDownY = ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                ((PullableScrollView) parentView).setCanPullDown(false);
                break;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                ((PullableScrollView) parentView).setCanPullDown(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                ((PullableScrollView) parentView).setCanPullDown(true);
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                ((PullableScrollView) parentView).setCanPullDown(true);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
