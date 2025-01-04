package com.jbb.library_common;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;


public class BaseApplication extends MultiDexApplication {



    public static Context mContext;
    public static String mobileName;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mobileName = Build.MANUFACTURER.toLowerCase();

    }


}
