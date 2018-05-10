package com.zyd.wlwsdk.widge.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class PullableScrollView extends ScrollView implements Pullable
{

	private boolean isCanPullDown = true;//是否允许滑动

	public PullableScrollView(Context context)
	{
		super(context);
	}

	public PullableScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullableScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown()
	{

		if (isCanPullDown) {
            return getScrollY() == 0;
		} else
			return false;
	}

	@Override
	public boolean canPullUp()
	{
        return getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight());
	}


	public void setCanPullDown(boolean canPullDown) {
		isCanPullDown = canPullDown;
	}
}