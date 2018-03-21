package com.vinpin.network.http;

import okhttp3.OkHttpClient;

/**
 * OkHttp client
 *
 * @author vinpin
 *         create at 2018/03/20 9:57
 */
public class HttpClient {

    private OkHttpClient.Builder builder;

    private HttpClient() {
        builder = new OkHttpClient.Builder();
    }

    public static HttpClient getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final HttpClient sInstance = new HttpClient();
    }

    public OkHttpClient.Builder getBuilder() {
        return builder;
    }
}
