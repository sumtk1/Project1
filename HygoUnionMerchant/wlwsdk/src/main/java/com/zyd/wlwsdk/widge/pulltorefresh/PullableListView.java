package com.zyd.wlwsdk.widge.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

public class PullableListView extends ListView implements Pullable {


    private boolean isCanPullDown = true;//是否允许滑动

    public PullableListView(Context context) {
        super(context);
    }

    public PullableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {
        try {
            if (isCanPullDown) {
                if (getCount() == 0) {
                    // 没有item的时候也可以下拉刷新
                    return true;
                } else // 滑到ListView的顶部了
                    return getFirstVisiblePosition() == 0 && getChildAt(0).getTop() >= 0;
            } else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean canPullUp() {
        if (getCount() == 0) {
            // 没有item的时候也可以上拉加载
            return true;
        } else if (getLastVisiblePosition() == (getCount() - 1)) {
            // 滑到底部了
            if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
                    && getChildAt(
                    getLastVisiblePosition()
                            - getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
                return true;
        }
        return false;
    }

    public void setCanPullDown(boolean canPullDown) {
        isCanPullDown = canPullDown;
    }
}
