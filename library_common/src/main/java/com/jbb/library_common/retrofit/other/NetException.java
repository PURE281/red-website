package com.jbb.library_common.retrofit.other;

import android.text.TextUtils;


/**
 * 类名:NetException
 */
public class NetException extends Exception {
    private int code;
    private String message;


    public NetException(int code) {
        super( code + " ");
        this.code = code;
        this.message = getErrorMessgae(code, "");
    }

    public NetException(int code, String message) {
        super( code + " " + message);
        this.message = getErrorMessgae(code, message);
        this.code = code;

    }


    public String message() {
        return message;
    }

    public int getCode() {
        return code;
    }


    /**
     * 描述:根据错误得到信息
     */
    private String getErrorMessgae(int code, String message) {
        String failMsg = "";
        switch (code) {
            /**
             * 真实网络错误码
             */
            case 404:
                //   this.code = HttpRespStatus.SC_SERVER_404_ERROR;
                failMsg = HttpRespStatus.MSG_SERVICE_ERROR;
                break;
            case 500:
                //  this.code = HttpRespStatus.SC_SERVER_500_ERROR;
                failMsg = HttpRespStatus.MSG_SERVICE_ERROR;
                break;
            case 400:
                //     this.code = HttpRespStatus.SC_SERVER_400_ERROR;
                failMsg = HttpRespStatus.MSG_SERVICE_ERROR;
                break;

            /**
             * 1.与服务端通信失败错误码
             * 2.数据解析失败错误码
             */
            case HttpRespStatus.SC_GET_TOKEN_FAIL:
                //数据临牌获取失败
                failMsg = HttpRespStatus.MSG_GET_TOKEN_ERROR;
                break;
            case HttpRespStatus.SC_DATA_PARSE_ERROR:
                //数据解析失败
                failMsg = HttpRespStatus.MSG_DATA_PARSE_ERROR;
                break;
            case HttpRespStatus.SC_SESSION_TIME_OUT_ERROR:
                failMsg = HttpRespStatus.MSG_SESSION_TIME_OUT_ERROR;
                break;
            case HttpRespStatus.SC_NET_CONNECTION_TIME_OUT_ERROR:
                failMsg = HttpRespStatus.MSG_NET_CONNECTION_TIME_OUT_ERROR;
                break;
            case HttpRespStatus.SC_NET_SOCKET_TIME_OUT_ERROR:
                failMsg = HttpRespStatus.MSG_NET_SOCKET_TIME_OUT_ERROR;
                break;
            case HttpRespStatus.SC_UNKNOWN_NET_ERROR:
                failMsg = HttpRespStatus.MSG_UNKNOWN_NET_ERROR;
                break;
            case HttpRespStatus.SC_NET_NO_CONNECTION_ERROR:
                failMsg = HttpRespStatus.MSG_NET_NO_CONNECTION_ERROR;
                break;
            case HttpRespStatus.SC_LOGIN_INFO_ERROR:
                failMsg = HttpRespStatus.MSG_LOGIN_INFO_ERROR;
                break;
            default:
                if (TextUtils.isEmpty(message)) {
                    failMsg = HttpRespStatus.MSG_UNKNOWN_NET_ERROR;
                } else {
                    failMsg = message;
                }

                break;

        }
        return failMsg;
    }


}
