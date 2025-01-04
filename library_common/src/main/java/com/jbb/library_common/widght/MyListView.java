package com.jbb.library_common.widght;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;


public class MyListView extends ListView {
    private Context mContext;
    private int mMaxXOverscrollDistance;
    private static final int MAX_X_OVERSCROLL_DISTANCE = 100;

    public MyListView(Context context) {
        super(context);
        this.mContext = context;
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }



}