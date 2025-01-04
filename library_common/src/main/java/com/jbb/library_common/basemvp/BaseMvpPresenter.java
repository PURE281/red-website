package com.jbb.library_common.basemvp;

/**
 * Created by ${lhh} on 2017/11/27.
 */
public abstract class BaseMvpPresenter<V extends BaseMvpView> {
    public V mView;//具体实现中去判断mView是否为空

    public void attach(V mView) {
        this.mView = mView;
    }

    public void dettach() {
        mView = null;
    }
}