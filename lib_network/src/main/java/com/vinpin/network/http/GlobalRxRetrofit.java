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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * 全局的配置的网络请求工具类
 *
 * @author vinpin
 *         create at 2018/03/21 10:05
 */
@SuppressWarnings("unused")
public class GlobalRxRetrofit {

    private GlobalRxRetrofit() {
    }

    public static GlobalRxRetrofit getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final GlobalRxRetrofit sInstance = new GlobalRxRetrofit();
    }

    /**
     * 设置baseUrl
     */
    public GlobalRxRetrofit setBaseUrl(@NonNull String baseUrl) {
        getGlobalRetrofitBuilder().baseUrl(baseUrl);
        return this;
    }

    /**
     * 设置自定义的OkHttpClient
     */
    public GlobalRxRetrofit setOkHttpClient(OkHttpClient okClient) {
        getGlobalRetrofitBuilder().client(okClient);
        return this;
    }

    /**
     * 添加统一的请求头
     */
    public GlobalRxRetrofit setHeaders(Map<String, String> headerMaps) {
        getGlobalOkHttpBuilder().addInterceptor(new HeaderInterceptor(headerMaps));
        return this;
    }

    /**
     * 是否开启请求日志
     */
    public GlobalRxRetrofit setLog(boolean isShowLog) {
        if (isShowLog) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(@NonNull String message) {
                    Log.e("RxRetrofitUtils", message);
                }
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            getGlobalOkHttpBuilder().addInterceptor(loggingInterceptor);
        }
        return this;
    }

    /**
     * 开启缓存，缓存到默认路径
     */
    public GlobalRxRetrofit setCache() {
        CacheInterceptor cacheInterceptor = new CacheInterceptor();
        Cache cache = new Cache(new File(Environment.getExternalStorageDirectory().getPath() + "/rxRetrofitCacheData")
                , 1024 * 1024 * 100);
        getGlobalOkHttpBuilder()
                .addInterceptor(cacheInterceptor)
                .addNetworkInterceptor(cacheInterceptor)
                .cache(cache);
        return this;
    }

    /**
     * 设置缓存路径及缓存文件大小
     */
    public GlobalRxRetrofit setCache(String cachePath, long maxSize) {
        if (!TextUtils.isEmpty(cachePath) && maxSize > 0) {
            CacheInterceptor cacheInterceptor = new CacheInterceptor();
            Cache cache = new Cache(new File(cachePath), maxSize);
            getGlobalOkHttpBuilder()
                    .addInterceptor(cacheInterceptor)
                    .addNetworkInterceptor(cacheInterceptor)
                    .cache(cache);
        }
        return this;
    }

    /**
     * 持久化保存cookie保存到sp文件中
     */
    public GlobalRxRetrofit setCookie(boolean saveCookie) {
        if (saveCookie) {
            getGlobalOkHttpBuilder()
                    .addInterceptor(new AddCookiesInterceptor())
                    .addInterceptor(new SaveCookiesInterceptor());
        }
        return this;
    }

    /**
     * 设置读取超时时间
     */
    public GlobalRxRetrofit setReadTimeout(long second) {
        getGlobalOkHttpBuilder().readTimeout(second, TimeUnit.SECONDS);
        return this;
    }

    /**
     * 设置写入超时时间
     */
    public GlobalRxRetrofit setWriteTimeout(long second) {
        getGlobalOkHttpBuilder().readTimeout(second, TimeUnit.SECONDS);
        return this;
    }

    /**
     * 设置连接超时时间
     */
    public GlobalRxRetrofit setConnectTimeout(long second) {
        getGlobalOkHttpBuilder().readTimeout(second, TimeUnit.SECONDS);
        return this;
    }

    /**
     * 信任所有证书,不安全有风险
     */
    public GlobalRxRetrofit setSslSocketFactory() {
        SSLManager.SSLSocketParams sslParams = SSLManager.getSslSocketFactory();
        getGlobalOkHttpBuilder().sslSocketFactory(sslParams.sSLSocketFactory, sslParams.sTrustManager);
        return this;
    }

    /**
     * 使用bks证书和密码管理客户端证书（双向认证）
     */
    public GlobalRxRetrofit setSslSocketFactory(InputStream[] certificates, InputStream bksFile, String password) {
        SSLManager.SSLSocketParams sslParams = SSLManager.getSslSocketFactory(certificates, bksFile, password);
        getGlobalOkHttpBuilder().sslSocketFactory(sslParams.sSLSocketFactory, sslParams.sTrustManager);
        return this;
    }

    public static Retrofit getGlobalRetrofit() {
        return RetrofitClient.getInstance().getRetrofit();
    }

    public Retrofit.Builder getGlobalRetrofitBuilder() {
        return RetrofitClient.getInstance().getRetrofitBuilder();
    }

    public OkHttpClient.Builder getGlobalOkHttpBuilder() {
        return HttpClient.getInstance().getBuilder();
    }

    /**
     * 使用全局变量的请求
     */
    public static <T> T createApi(final Class<T> cls) {
        return getGlobalRetrofit().create(cls);
    }
}