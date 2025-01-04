package com.jbb.library_common.other;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by lq on 16/5/20.
 */
public class VItemDecoration extends RecyclerView.ItemDecoration {

    private int spacing;
    private boolean includeEdge;

    public VItemDecoration(int spacing, boolean includeEdge) {
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

            if (includeEdge) {
                if(position == 0)
                    outRect.top = spacing;

                outRect.right = spacing ;
                outRect.left = spacing;
                outRect.bottom = spacing;

            } else {
                if(position != 0) {
                    outRect.top = spacing;
                }
//                if(position != parent.getChildCount() -1)
//                    outRect.bottom = spacing ;
            }
        }


}
