package com.vinpin.network.interceptor;

import android.support.annotation.NonNull;

import com.vinpin.network.cookie.CookieManger;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 接受服务器发的cookie拦截器
 *
 * @author vinpin
 *         create at 2018/03/20 17:17
 */
public class SaveCookiesInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        //这里获取请求返回的cookie
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();
            cookies.addAll(originalResponse.headers("Set-Cookie"));
            CookieManger.getInstance().saveCookies(cookies);
        }
        return originalResponse;
    }
}
