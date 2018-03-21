package com.vinpin.network;

import android.annotation.SuppressLint;
import android.content.Context;

import com.vinpin.network.http.GlobalRxRetrofit;
import com.vinpin.network.http.SingleRxRetrofit;

/**
 * 基于 Retrofit+Rxjava 封装的网络请求工具类
 *
 * @author vinpin
 *         create at 2018/03/21 9:47
 */
public class RxRetrofitUtils {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private RxRetrofitUtils() {
    }

    public static RxRetrofitUtils getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        @SuppressLint("StaticFieldLeak")
        private static final RxRetrofitUtils sInstance = new RxRetrofitUtils();
    }

    /**
     * 必须在全局Application先调用，获取context上下文
     *
     * @param app Application
     */
    public static void init(Context app) {
        context = app;
    }

    public static Context getContext() {
        checkInitialize();
        return context;
    }

    private static void checkInitialize() {
        if (context == null) {
            throw new ExceptionInInitializerError("请先在全局Application中调用 RxRetrofitUtils.init() 初始化！");
        }
    }

    /**
     * 开始全局参数的配置
     */
    public GlobalRxRetrofit config() {
        return GlobalRxRetrofit.getInstance();
    }

    /**
     * 使用全局参数创建请求
     */
    public static <T> T createApi(Class<T> cls) {
        return GlobalRxRetrofit.createApi(cls);
    }

    /**
     * 获取单个请求配置实例
     *
     * @return SingleRxHttp
     */
    public static SingleRxRetrofit getSingleInstance() {
        return new SingleRxRetrofit();
    }
}
