package com.vinpin.network.http;

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
public class RetrofitClient {

    private Retrofit.Builder mRetrofitBuilder;
    private okhttp3.OkHttpClient.Builder mOkHttpBuilder;

    private RetrofitClient() {
        mOkHttpBuilder = HttpClient.getInstance().getBuilder();

        mRetrofitBuilder = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    public static RetrofitClient getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final RetrofitClient sInstance = new RetrofitClient();
    }

    public Retrofit.Builder getRetrofitBuilder() {
        return mRetrofitBuilder;
    }

    public Retrofit getRetrofit() {
        return mRetrofitBuilder.client(mOkHttpBuilder.build()).build();
    }
}
