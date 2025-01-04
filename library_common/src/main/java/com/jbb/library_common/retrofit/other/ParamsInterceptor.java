package com.jbb.library_common.retrofit.other;


import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.SharePreferensUtil;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 类名:ParamsInterceptor
 * 描述:给OkHttpClient增加公共头部参数
 */
public class ParamsInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        HttpUrl httpUrl = originalRequest.url();
        String url = httpUrl.toString();
//        if(!url.contains("?")){
//            url = url  + "?";
//        }
//        if(!url.endsWith("&")&& !url.endsWith("?")){
//            url = url  + "&";
//        }
//        url = url + "os=android&appName=xhyp";//url后面拼接公共参数
//
        String host = httpUrl.host();
        Request.Builder newRequestBuilder = originalRequest.newBuilder()
                .url(url)
                .header("Content-Type", "application/json")//请求的与实体对应的MIME信息
                .header("Accept", "application/json")//客户端能接受的类型
                .header("Content-Encoding", "gzip");
//                .header(KeyContacts.TOKEN, KeyContacts.token);



        Request newRequest = newRequestBuilder.method(originalRequest.method(), originalRequest.body()).build();
        return chain.proceed(newRequest);
    }


}
