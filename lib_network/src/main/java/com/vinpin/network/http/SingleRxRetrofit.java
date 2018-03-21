package com.vinpin.network.http;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.vinpin.network.interceptor.AddCookiesInterceptor;
import com.vinpin.network.interceptor.CacheInterceptor;
import com.vinpin.network.interceptor.HeaderInterceptor;
import com.vinpin.network.interceptor.SaveCookiesInterceptor;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 可单独配置参数的网络请求工具类
 *
 * @author vinpin
 *         create at 2018/03/21 10:41
 */
@SuppressWarnings("unused")
public class SingleRxRetrofit {

    private String baseUrl;

    private Map<String, String> headerMaps = new HashMap<>();

    private boolean isShowLog = true;
    private boolean cache = false;
    private boolean saveCookie = true;

    private String cachePath;
    private long cacheMaxSize;

    private long readTimeout;
    private long writeTimeout;
    private long connectTimeout;

    private OkHttpClient okHttpClient;

    private SSLManager.SSLSocketParams sslParams;

    private List<Converter.Factory> converterFactories = new ArrayList<>();
    private List<CallAdapter.Factory> adapterFactories = new ArrayList<>();

    public SingleRxRetrofit baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * 局部设置Converter.Factory,默认GsonConverterFactory.create()
     */
    public SingleRxRetrofit addConverterFactory(Converter.Factory factory) {
        if (factory != null) {
            converterFactories.add(factory);
        }
        return this;
    }

    /**
     * 局部设置CallAdapter.Factory,默认RxJavaCallAdapterFactory.create()和ScalarsConverterFactory.create()
     */
    public SingleRxRetrofit addCallAdapterFactory(CallAdapter.Factory factory) {
        if (factory != null) {
            adapterFactories.add(factory);
        }
        return this;
    }

    public SingleRxRetrofit addHeaders(Map<String, String> headerMaps) {
        this.headerMaps = headerMaps;
        return this;
    }

    public SingleRxRetrofit log(boolean isShowLog) {
        this.isShowLog = isShowLog;
        return this;
    }

    public SingleRxRetrofit cache(boolean cache) {
        this.cache = cache;
        return this;
    }

    public SingleRxRetrofit saveCookie(boolean saveCookie) {
        this.saveCookie = saveCookie;
        return this;
    }

    public SingleRxRetrofit cachePath(String cachePath, long maxSize) {
        this.cachePath = cachePath;
        this.cacheMaxSize = maxSize;
        return this;
    }

    public SingleRxRetrofit readTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public SingleRxRetrofit writeTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    public SingleRxRetrofit connectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public SingleRxRetrofit sslSocketFactory() {
        sslParams = SSLManager.getSslSocketFactory();
        return this;
    }

    public SingleRxRetrofit sslSocketFactory(InputStream[] certificates, InputStream bksFile, String password) {
        sslParams = SSLManager.getSslSocketFactory(certificates, bksFile, password);
        return this;
    }

    public SingleRxRetrofit client(@NonNull OkHttpClient okClient) {
        this.okHttpClient = okClient;
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
        if (converterFactories.isEmpty()) {
            //获取全局的对象重新设置
            List<Converter.Factory> listConverterFactory = RetrofitClient.getInstance().getRetrofit().converterFactories();
            for (Converter.Factory factory : listConverterFactory) {
                singleRetrofitBuilder.addConverterFactory(factory);
            }
        } else {
            for (Converter.Factory converterFactory : converterFactories) {
                singleRetrofitBuilder.addConverterFactory(converterFactory);
            }
        }

        if (adapterFactories.isEmpty()) {
            //获取全局的对象重新设置
            List<CallAdapter.Factory> listAdapterFactory = RetrofitClient.getInstance().getRetrofit().callAdapterFactories();
            for (CallAdapter.Factory factory : listAdapterFactory) {
                singleRetrofitBuilder.addCallAdapterFactory(factory);
            }
        } else {
            for (CallAdapter.Factory adapterFactory : adapterFactories) {
                singleRetrofitBuilder.addCallAdapterFactory(adapterFactory);
            }
        }

        if (TextUtils.isEmpty(baseUrl)) {
            singleRetrofitBuilder.baseUrl(RetrofitClient.getInstance().getRetrofit().baseUrl());
        } else {
            singleRetrofitBuilder.baseUrl(baseUrl);
        }

        singleRetrofitBuilder.client(okHttpClient == null ? getSingleOkHttpBuilder().build() : okHttpClient);

        return singleRetrofitBuilder;
    }

    /**
     * 获取单个 OkHttpClient.Builder
     */
    private OkHttpClient.Builder getSingleOkHttpBuilder() {
        OkHttpClient.Builder singleOkHttpBuilder = new OkHttpClient.Builder();

        singleOkHttpBuilder.retryOnConnectionFailure(true);
        singleOkHttpBuilder.addInterceptor(new HeaderInterceptor(headerMaps));

        if (cache) {
            CacheInterceptor cacheInterceptor = new CacheInterceptor();
            Cache cache;
            if (!TextUtils.isEmpty(cachePath) && cacheMaxSize > 0) {
                cache = new Cache(new File(cachePath), cacheMaxSize);
            } else {
                cache = new Cache(new File(Environment.getExternalStorageDirectory().getPath() + "/rxRetrofitCacheData")
                        , 1024 * 1024 * 100);
            }
            singleOkHttpBuilder.addInterceptor(cacheInterceptor)
                    .addNetworkInterceptor(cacheInterceptor)
                    .cache(cache);
        }

        if (isShowLog) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.e("RxRetrofitUtils", message);
                }
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            singleOkHttpBuilder.addInterceptor(loggingInterceptor);
        }

        if (saveCookie) {
            singleOkHttpBuilder
                    .addInterceptor(new AddCookiesInterceptor())
                    .addInterceptor(new SaveCookiesInterceptor());
        }

        singleOkHttpBuilder.readTimeout(readTimeout > 0 ? readTimeout : 10, TimeUnit.SECONDS);
        singleOkHttpBuilder.writeTimeout(writeTimeout > 0 ? writeTimeout : 10, TimeUnit.SECONDS);
        singleOkHttpBuilder.connectTimeout(connectTimeout > 0 ? connectTimeout : 10, TimeUnit.SECONDS);

        if (sslParams != null) {
            singleOkHttpBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.sTrustManager);
        }

        return singleOkHttpBuilder;
    }
}
