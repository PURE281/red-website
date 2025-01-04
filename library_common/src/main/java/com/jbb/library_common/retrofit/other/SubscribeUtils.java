package com.jbb.library_common.retrofit.other;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.LongSerializationPolicy;
import com.jbb.library_common.BaseApplication;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.DeviceUtil;
import com.jbb.library_common.utils.ToastUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class SubscribeUtils {

    private static Gson gson;

    //data为集合时T传string类型
    public static <T> void subscribe(Observable<ResponseBody> observable, final Class<T> resultClass, final NetListeren<T> listeren) {
        if (!DeviceUtil.isNetWorkEnable()) {
            if (listeren != null) {
                listeren.onError(new NetException(HttpRespStatus.SC_NET_NO_CONNECTION_ERROR));
            }
            return;
        }
        if (listeren != null) {
            listeren.onStart();
        }
        observable.flatMap(new Function<ResponseBody, Observable<T>>() {

            @Override
            public Observable apply(ResponseBody response) throws Exception {
//                        if (response.isSuccessful() && response.code() == 200) {
                BaseBean baseBean = null;
                String result = response.string();
                baseBean = JSON.parseObject(result, BaseBean.class);
                if (baseBean == null) {
                    return Observable.error(new Throwable(result));
                }

                if (baseBean.getCode() == 200) {
                    T bean = JSON.parseObject(result, resultClass);
//                    T bean =  getMapGson().fromJson(result, resultClass);
                    return Observable.just(bean);
                } else {
                    if (baseBean.getCode() == -200 || baseBean.getCode() == -300 ) {// token 过期， Token 过期
                        Intent it = new Intent(KeyContacts.ACTION_API_KEY_INVALID);
                        it.putExtra("code", baseBean.getCode());
                        BaseApplication.getContext().sendBroadcast(it);
                    }
                    return Observable.error(new NetException(baseBean.getCode(), baseBean.getMsg()));
                }

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(T o) {
                        if (listeren != null) {
                            listeren.onSuccess(o);
                            listeren.onEnd();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (listeren != null)
                            listeren.onError((Exception) e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    //data为集合时T传string类型
    public static <T> void subscribe2(Observable<ResponseBody> observable, final Class<T> resultClass) {
        if (!DeviceUtil.isNetWorkEnable()) {
            return;
        }

        observable.flatMap(new Function<ResponseBody, Observable<T>>() {

            @Override
            public Observable apply(ResponseBody response) throws Exception {
//                        if (response.isSuccessful() && response.code() == 200) {
                BaseBean baseBean = null;
                String result = response.string();
                baseBean = JSON.parseObject(result, BaseBean.class);
                if (baseBean == null) {
                    return Observable.error(new Throwable(result));
                }

                if (baseBean.getCode() == 200) {
                    T bean = JSON.parseObject(result, resultClass);
                    return Observable.just(bean);
                } else {
                    if (baseBean.getCode() == 2001 || baseBean.getCode() == 2002) {//2001 token 过期， 2002 Refresh Token 过期
                        Intent it = new Intent(KeyContacts.ACTION_API_KEY_INVALID);
                        it.putExtra("code", baseBean.getCode());
                        BaseApplication.getContext().sendBroadcast(it);
                    }
                    return Observable.error(new NetException(baseBean.getCode(), baseBean.getMsg()));
                }

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(T o) {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public static Gson getMapGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(Map.class, new JsonDeserializer<Map>() {
                        public Map deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                                throws JsonParseException {
                            HashMap<String, Object> resultMap = new HashMap<>();
                            JsonObject jsonObject = json.getAsJsonObject();
                            Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                            for (Map.Entry<String, JsonElement> entry : entrySet) {
                                resultMap.put(entry.getKey(), entry.getValue());
                            }
                            return resultMap;
                        }
                    })
                    .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                    .create();
        }
        return gson;
    }


}
