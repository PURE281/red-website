/**
 * 工程: Coyote
 * 标题: KeyContacts.java
 * 包:   com.niuniucaip.lotto.ui.config
 * 描述: TODO
 * 作者: nn
 * 时间: 2014-7-22 下午4:56:26
 * 版权: Copyright 2014 Shenzhen NiuNiucaip Tech Co.,Ltd.
 * All rights reserved.
 */

package com.jbb.library_common.comfig;

import android.content.Intent;

/**
 * 类: KeyContacts
 * 描述: 用于存放常用字符串
 * 作者: nn
 * 时间: 2014-7-22 下午4:56:26
 */
public class KeyContacts {



    /**公共参数**/
    public static final String KEY_CUSTOM_PARAMS = "Custom-Params";

    public static final String KEY_TITLE = "title";
    public static final String KEY_URL = "url";


    public static final String SP_NAME_USERINFO= "userinfo"; //SP userinfo文件名
    public static final String SP_NAME_COMMON= "common"; //SP 存储常用配置
    public static final String SP_KEY_USERINFO = "key_userinfo";//用户信息sp 中key

    public static String KEY_GUIDE1DIALOG = "key_guide1dialog";
    public static String KEY_GUIDE2DIALOG = "key_guide2dialog";
    public static String KEY_GUIDE3DIALOG = "key_guide3dialog";


    public static final String ACTION_API_KEY_INVALID = "action_api_key_invalid";//token过期






    public static final int STATUS_FORCE_KILLED = -1;//应用在后台被强杀了
    public static final int STATUS_NORMAL = 2; //APP正常态
    public static final String START_LAUNCH_ACTION = "start_launch_action";


    public static final String MAIN_PATH = "/main/main";
    public static final String LOGIN_PATH = "/main/login";
    public static final String SPLASH_PATH = "/main/splash";

    public static final String TOKEN= "TOKEN"; //SP 存储常用配置
    public static String token  = "d02051a65c08f7fc91ea320a62e52c10";
    public static String role ="2";


    public static final String REWARDVIDEOAD_CODEID = "945155186";
    public static String offline_gold_extra = "{\"extraType\":\"offline\"}";
    public static String turntable_box_extra ;
    public static String turntable_coupon_extra = "{\"extraType\":\"gameNum\"}";
    public static String not_enough_gold_extra = "{\"extraType\":\"adPoint\"}";
    public static String click_getgold_extra = "{\"extraType\":\"getPoint\"}";

    public static String offline_gold_adtype = "offline";
    public static String turntable_box_adtype = "treasure";
    public static String turntable_coupon_adtype = "gameNum";
    public static String not_enough_gold_adtype = "adPoint";
    public static String click_getgold_adtype = "getPoint";

}