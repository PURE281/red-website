package com.jbb.library_common.retrofit;


import android.os.Bundle;

import com.jbb.library_common.comfig.InterfaceConfig;
import com.jbb.library_common.other.OkHttpClineUtils;
import com.jbb.library_common.retrofit.other.HttpLoggingInterceptor;
import com.jbb.library_common.retrofit.other.ParamsInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private static RetrofitManager instance;
    private Retrofit mRetrofit;


    private RetrofitManager(){
        init();
    }



    public static RetrofitManager getInstance(){
        if(instance == null){
            synchronized (RetrofitManager.class){
                if(instance == null){
                    instance = new RetrofitManager();
                }
            }
        }
        return instance;
    }

    private void init() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(new HttpLoggingInterceptor());
        builder.addInterceptor(new ParamsInterceptor());
        builder.connectTimeout(20,TimeUnit.SECONDS);
        builder.sslSocketFactory(OkHttpClineUtils.getSSLSocketFactory(),OkHttpClineUtils.getX509TrustManager());
        builder.hostnameVerifier(OkHttpClineUtils.getHostnameVerifier());

        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(InterfaceConfig.BASEURL)
                .build();

//        apiService = retrofit.create(ApiService.class);
    }


    public <T> T getService(Class<T> service){
        return mRetrofit.create(service);
    }

}
