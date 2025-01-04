package com.jbb.library_common.retrofit.other;

import com.jbb.library_common.comfig.AppConfig;
import com.jbb.library_common.utils.log.LogUtil;
import com.jbb.library_common.utils.ToastUtil;

public abstract class NetListeren<T> {
    public void onStart() {

    }
    public void onEnd() {
    }

    public void onError(Exception e) {
        if(AppConfig.LOG_SWITCH_FLAG == LogUtil.LOG_ON){
            e.printStackTrace();
        }
        if (e instanceof NetException) {
            NetException exception =  (NetException) e;
            if(exception.getCode() != 2001)
                ToastUtil.showCustomToast(exception.message());
        } else {//其他异常
            ToastUtil.showCustomToast(e.getMessage());
        }

        onEnd();
    }

    public abstract void onSuccess(T t);
}
