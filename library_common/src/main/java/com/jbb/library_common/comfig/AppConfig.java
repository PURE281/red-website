package com.jbb.library_common.comfig;


//import com.jf.lib_common.utils.LogUtil;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.Window;

public class AppConfig {
    public static final String SDCARD_DIR_PATH = "SmartAccessControl_Proprietor";
    public static final String SDCARD_DIR_PICTURE = "picture";
    public static final String SDCARD_DIR_VIDEO = "video";

    public static final int LOG_OFF = -100;
    public static final int LOG_ON = 6;

    /***
     * 日志开关
     ***/
    public static final int LOG_SWITCH_FLAG = LOG_ON;
     //线上地址
    public static final int HOST_INDEX_ONLINE = 0;
     //测试地址
    public static final int HOST_INDEX_PREPARE = 1;
    //修改此字段值切换 接口测试，生产环境
//    public static final int HOST_ADDRESS_CONFIG_INDEX =  HOST_INDEX_ONLINE;
    public static final int HOST_ADDRESS_CONFIG_INDEX =  HOST_INDEX_PREPARE;


    public static String BUGLY_APPID = "938fbee9a4";

    public static String Mi_APP_ID = "2882303761518034732";
    public static String Mi_APP_KEY = "5551803467732";


    //微信
    public static final String WXAPP_ID = "wx7965f98316bd87b4";
    public static final String WXAPP_SECRET = "d6f30654170632c902d87310f86bef38";
    public static final String WXAPP_PARTNERID = "";
    public static String WXAPP_KEY = "";


    /**
     *  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT 竖屏
     *  ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE 横屏
     *
     *(不用)
     *  Configuration.ORIENTATION_LANDSCAPE
     *  Configuration.ORIENTATION_PORTRAIT
     */
    public static final int SCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
}
