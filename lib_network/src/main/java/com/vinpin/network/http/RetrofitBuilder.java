package com.vinpin.network.http;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Retrofit client
 *
 * @author zwp
 *         create at 2017/3/30 13:56
 */
public class RetrofitBuilder {

    private Retrofit.Builder mRetrofitBuilder;
    private OkHttpClient.Builder mOkHttpBuilder;

    private RetrofitBuilder() {
        mOkHttpBuilder = OkHttpBuilder.getInstance().getBuilder();

        mRetrofitBuilder = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    public static RetrofitBuilder getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final RetrofitBuilder sInstance = new RetrofitBuilder();
    }

    public Retrofit.Builder getRetrofitBuilder() {
        return mRetrofitBuilder;
    }

    public Retrofit getRetrofit() {
        return mRetrofitBuilder.client(mOkHttpBuilder.build()).build();
    }
}
