package com.jbb.library_common;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.log.LogUtil;

import org.json.JSONObject;

public class MyJavaScripInterface {

    private Activity activity;

    public MyJavaScripInterface(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void jumpApp(int type){
        // type = 1  悄悄话，  type=2 大擂台，
        LogUtil.w("jumpApp  type = " +type);
        if(type == 1){
            ARouter.getInstance().build(KeyContacts.MAIN_PATH).withInt("index",0).navigation();//
        }else if(type == 2){
            ARouter.getInstance().build(KeyContacts.MAIN_PATH).withInt("index",3).navigation();//
        }else if(type == 3){
            ARouter.getInstance().build(KeyContacts.MAIN_PATH).withInt("index",1).navigation();//
        }else if(type == 4){
            ARouter.getInstance().build(KeyContacts.MAIN_PATH).withInt("index",2).navigation();//
        }else if(type == 5){
            ARouter.getInstance().build(KeyContacts.MAIN_PATH).withInt("index",4).navigation();//
        }else if(type == 10){
            activity.finish();
        }
    }
}
