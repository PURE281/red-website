package com.jbb.library_common.widght;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

//import androidx.viewpager.widget.ViewPager;

/**
 * Created by ${lhh} on 2018/7/20.
 */

public class NoScrollViewPager extends ViewPager {

    private boolean scroll ;

    public NoScrollViewPager(Context context) {
        this(context , null);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScroll(boolean scroll) {
        this.scroll = scroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (!scroll)
            return false;
        else
            return super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (!scroll)
            return false;
        else
            return super.onInterceptTouchEvent(arg0);
    }

}
