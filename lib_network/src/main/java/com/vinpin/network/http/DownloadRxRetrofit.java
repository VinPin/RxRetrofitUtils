package com.vinpin.network.http;

import com.vinpin.network.interceptor.HeaderInterceptor;
import com.vinpin.network.interceptor.ProgressInterceptor;
import com.vinpin.network.retrofit.ProgressResponseBody;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * <pre>
 *     author: vinpin
 *     time  : 2018/07/23 13:10
 *     desc  : 可单独配置参数的网络请求工具类
 * </pre>
 */
@SuppressWarnings("unused")
public class DownloadRxRetrofit {

    private Map<String, String> headerMaps = new HashMap<>();

    private ProgressResponseBody.OnProgressListener progressListener;

    private long readTimeout;
    private long writeTimeout;
    private long connectTimeout;

    private SSLManager.SSLSocketParams sslParams;

    public DownloadRxRetrofit addHeaders(Map<String, String> headerMaps) {
        this.headerMaps = headerMaps;
        return this;
    }

    public DownloadRxRetrofit setOnProgressListener(ProgressResponseBody.OnProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    public DownloadRxRetrofit readTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public DownloadRxRetrofit writeTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    public DownloadRxRetrofit connectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public DownloadRxRetrofit sslSocketFactory() {
        sslParams = SSLManager.getSslSocketFactory();
        return this;
    }

    public DownloadRxRetrofit sslSocketFactory(InputStream[] certificates, InputStream bksFile, String password) {
        sslParams = SSLManager.getSslSocketFactory(certificates, bksFile, password);
        return this;
    }

    public <T> T createApi(final Class<T> cls) {
        return getSingleRetrofitBuilder().build().create(cls);
    }

    /**
     * 获取单个 Retrofit.Builder
     */
    public Retrofit.Builder getSingleRetrofitBuilder() {
        Retrofit.Builder singleRetrofitBuilder = new Retrofit.Builder();
        singleRetrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        singleRetrofitBuilder.baseUrl(RetrofitBuilder.getInstance().getRetrofit().baseUrl());
        singleRetrofitBuilder.client(getSingleOkHttpBuilder().build());
        return singleRetrofitBuilder;
    }

    /**
     * 获取单个 OkHttpClient.Builder
     */
    private OkHttpClient.Builder getSingleOkHttpBuilder() {
        OkHttpClient.Builder singleOkHttpBuilder = new OkHttpClient.Builder();

        singleOkHttpBuilder.retryOnConnectionFailure(true);
        if (headerMaps != null && !headerMaps.isEmpty()) {
            singleOkHttpBuilder.addInterceptor(new HeaderInterceptor(headerMaps));
        }
        singleOkHttpBuilder.addNetworkInterceptor(new ProgressInterceptor(new ProgressResponseBody.OnProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength) {
                if (progressListener != null) {
                    progressListener.update(bytesRead, contentLength);
                }
            }
        }));

        singleOkHttpBuilder.readTimeout(readTimeout > 0 ? readTimeout : 10, TimeUnit.SECONDS);
        singleOkHttpBuilder.writeTimeout(writeTimeout > 0 ? writeTimeout : 10, TimeUnit.SECONDS);
        singleOkHttpBuilder.connectTimeout(connectTimeout > 0 ? connectTimeout : 10, TimeUnit.SECONDS);

        if (sslParams != null) {
            singleOkHttpBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.sTrustManager);
        }

        return singleOkHttpBuilder;
    }
}
