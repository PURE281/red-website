package com.jbb.library_common.basemvp;

/**
 * Created by ${lhh} on 2017/11/27.
 */

public interface BaseMvpView {
    void showLoading();
    void hideLoading();
    void error(Exception e);
}
