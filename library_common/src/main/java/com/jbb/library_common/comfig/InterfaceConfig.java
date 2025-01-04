package com.jbb.library_common.comfig;

public class InterfaceConfig {

//    public static final String [] URLS = {"http://ameng.iqweb.net" ,"http://ameng.iqweb.net"};//
    //0是线上环境,1是测试环境
    public static final String [] URLS = {"http://ameng.szlxkj.com" ,"https://4w02562b54.yicp.fun"};//

    public static final String BASEURL = URLS[AppConfig.HOST_ADDRESS_CONFIG_INDEX];



    public static final String WX_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?";//微信 根据 code 获取 access_token
    public static final String WX_USERINFO = "https://api.weixin.qq.com/sns/userinfo?";//微信根据 access_token 获取 用户信息

    public static final String POLICY_URL = "http://ameng.szlxkj.com/index.php?m=Home&c=CustomView&a=Index&id=10";//8、隐私政策
    public static final String ABOUT_URL = "http://ameng.szlxkj.com/index.php?m=Home&c=CustomView&a=Index&id=4";//9、关于我们
    public static final String REGIST_URL = "http://ameng.szlxkj.com/index.php?m=Home&c=CustomView&a=Index&id=3";//9、用户注册协议

    public static final String CDN_URL = "http://download.szlxkj.com";


}
