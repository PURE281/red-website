package com.jbb.library_common.other;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by lq on 16/5/20.
 */
public class HItemDecoration extends RecyclerView.ItemDecoration {

    private int spacing;
    private boolean includeEdge;

    public HItemDecoration(int spacing, boolean includeEdge) {
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

            if (includeEdge) {
                if(position == 0)
                    outRect.left = spacing;

                outRect.right = spacing ;
                outRect.top = spacing;
                outRect.bottom = spacing;

            } else {
                if(position == 0) {
                    outRect.left = spacing;
                }
//                if(position != parent.getChildCount() -1)
                outRect.right = spacing ;
            }
        }


}
