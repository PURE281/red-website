package com.jbb.library_common.retrofit.other;

/**
 * 类: HttpResStatus <p>
 */
public class HttpRespStatus {

    /**
     * 正常
     **/
    public static final int SC_OK = 100;

    /*****************************************
     * 网络错误码使用范围 5000-6000以上
     *****************************************/
    /**
     * 未知错误
     **/
    public static final int SC_UNKNOWN_NET_ERROR = 5000;
    /**
     * 网络无连接
     **/
    public static final int SC_NET_NO_CONNECTION_ERROR = 5001;
    /**
     * 网络连接超时  connectionTimeout
     **/
    public static final int SC_NET_CONNECTION_TIME_OUT_ERROR = 5002;
    /**
     * 网络通信超时  socketTimeout
     **/
    public static final int SC_NET_SOCKET_TIME_OUT_ERROR = 5003;
    /**
     * 500错误
     **/
    public static final int SC_SERVER_500_ERROR = 5004;
    /**
     * 404错误
     **/
    public static final int SC_SERVER_404_ERROR = 5005;
    /**
     * 400错误
     **/
    public static final int SC_SERVER_400_ERROR = 5006;
    /**
     * 无效数据
     **/
    public static final int SC_SERVER_RESPONSE_INVALID_DATA = 5007;

    /**
     * 数据解析错误
     **/
    public static final int SC_DATA_PARSE_ERROR = 5050;
    /**
     * 数据令牌获取失败
     **/
    public static final int SC_GET_TOKEN_FAIL = 5051;
    /**
     * 登录超时
     **/
    public static final int SC_SESSION_TIME_OUT_ERROR = 5052;
    /**
     * 本地用户信息错误
     **/
    public static final int SC_LOGIN_INFO_ERROR = 5053;

    /*************************************
     * 逻辑状态码
     ************************************/


    /**
     * ==========================网络请求提示=========================================
     **/


    public static final String MSG_SERVICE_ERROR = "呜呜呜，服务器挤爆了，请您稍后再试";                       //"服务端异常";
//    public static final String MSG_UNKNOWN_NET_ERROR = "好多客官，小二忙晕了,请您稍后再试";                 //"数据请求失败";
    public static final String MSG_UNKNOWN_NET_ERROR = "服务器繁忙,请您稍后再试";                 //"数据请求失败";
    public static final String MSG_NET_NO_CONNECTION_ERROR = "连接失败，请检查您的网络连接";                     //"网络无连接";
    public static final String MSG_NET_CONNECTION_TIME_OUT_ERROR = "连接超时,请您稍后再试";         //"连接超时";
    public static final String MSG_NET_SOCKET_TIME_OUT_ERROR = "网络不稳定,请您稍后再试";             //"通信超时";
    public static final String MSG_GET_TOKEN_ERROR = "网络不流畅,试试刷新吧";                                //"数据令牌获取失败";
    public static final String MSG_DATA_PARSE_ERROR = "访问人数太多了,请您稍后再试";                          //"数据解析错误";
    public static final String MSG_SESSION_TIME_OUT_ERROR = "登录状态过期,请重新登录";                        //"登录态失效，请退出重新登录";
    public static final String MSG_UNKNOWN_ERROR = "哇呜，小二忙晕了,请您稍后再试";                   //"未知错误";
    public static final String MSG_LOGIN_INFO_ERROR = "账户信息有误，请重新登录";                           //本地账户信息错误
    /**==========================网络请求提示=========================================**/
}
