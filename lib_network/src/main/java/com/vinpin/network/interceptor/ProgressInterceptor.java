package com.vinpin.network.interceptor;

import android.support.annotation.NonNull;

import com.vinpin.network.retrofit.ProgressResponseBody;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * <pre>
 *     author: vinpin
 *     time  : 2018/07/23 13:39
 *     desc  : 下载进度的拦截器
 * </pre>
 */
public class ProgressInterceptor implements Interceptor {

    private ProgressResponseBody.OnProgressListener mProgressListener;

    private ProgressInterceptor() {
    }

    public ProgressInterceptor(@NonNull ProgressResponseBody.OnProgressListener listener) {
        mProgressListener = listener;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(originalResponse.body(), mProgressListener))
                .build();
    }
}
