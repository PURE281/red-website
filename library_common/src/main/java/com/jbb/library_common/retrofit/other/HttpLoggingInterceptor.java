package com.jbb.library_common.retrofit.other;


import com.jbb.library_common.utils.log.LogUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 类名:HttpLoggingInterceptor
 * 描述:log  参考自官方 简化需求
 */
public final class HttpLoggingInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");


    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        HttpUrl httpUrl = request.url();
//        String url = httpUrl.toString();
//        String host = httpUrl.host();

        Headers headers = request.headers();
        LogUtil.w("当前head参数：");
        for (int i = 0; i < headers.size(); i++) {
            LogUtil.w(headers.name(i) + ": " + headers.value(i));
        }


//        LogUtil.w("当前请求URL：" + url + "----当前URL对应host：" + host);
        // log request body
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            LogUtil.w("打印RequestUrl为" + request.url() + "的Request body：");
            LogUtil.json(buffer.readString(charset));
//            LogUtil.w("Url为" + request.url() + "的RequestBody大小为" + requestBody.contentLength() + "-byte body)");

        } else {
            LogUtil.w("RequestUrl为" + request.url() + "的RequestBody is null 无法打印结果 请检测请求是否合法");
        }

        // log request cost  time
        long startNs = System.nanoTime();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        LogUtil.w("请求URL " + response.request().url() + " 耗费时间 (" + tookMs + "ms)");

//        ThirdStatistics.netCostTimes(response,tookMs);

        // log response body
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();

        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }

        if (contentLength != 0) {
            LogUtil.w("开始打印URL为" + request.url() + "的HTTP Response Body数据");
            LogUtil.json(buffer.clone().readString(charset));
            LogUtil.w("RequestUrl为" + request.url() + "的Response Body大小为 (" + buffer.size() + "-byte)");
        } else {
            LogUtil.w("URL为" + response.request().url() + "的HTTP Response Body数据为空"
            );
        }
        return response;
    }


}