package com.vinpin.network.interceptor;

import android.support.annotation.NonNull;

import com.vinpin.network.cookie.CookieManger;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求头里边添加cookie拦截器
 *
 * @author vinpin
 *         create at 2018/03/20 16:59
 */
public class AddCookiesInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        HashSet<String> cookies = CookieManger.getInstance().getCookies();
        for (String cookie : cookies) {
            builder.addHeader("Cookie", cookie);
        }
        return chain.proceed(builder.build());
    }
}
