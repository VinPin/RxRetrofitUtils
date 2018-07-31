package com.vinpin.network.http;

import okhttp3.OkHttpClient;

/**
 * OkHttp client
 *
 * @author vinpin
 *         create at 2018/03/20 9:57
 */
public class OkHttpBuilder {

    private OkHttpClient.Builder builder;

    private OkHttpBuilder() {
        builder = new OkHttpClient.Builder();
    }

    public static OkHttpBuilder getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final OkHttpBuilder sInstance = new OkHttpBuilder();
    }

    public OkHttpClient.Builder getBuilder() {
        return builder;
    }
}
