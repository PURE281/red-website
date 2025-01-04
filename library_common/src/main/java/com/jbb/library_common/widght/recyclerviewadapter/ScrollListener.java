package com.jbb.library_common.widght.recyclerviewadapter;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by lq on 16/6/30.
 */
public abstract class ScrollListener extends HidingScrollListener {
    public ScrollListener(RecyclerView.LayoutManager layoutManager) {
        super(layoutManager);
        loadMore = true;

    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    //加载更多
    public abstract void onLoadMore();
}
