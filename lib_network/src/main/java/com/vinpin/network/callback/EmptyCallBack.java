package com.vinpin.network.callback;

import android.support.annotation.NonNull;

import com.vinpin.network.exception.ApiException;

import io.reactivex.disposables.Disposable;

/**
 * <pre>
 *     author: vinpin
 *     time  : 2018/03/20 15:56
 *     desc  : 网络请求空回调
 * </pre>
 */
public interface EmptyCallBack {
    /**
     * 订阅关系
     */
    void onSubscribe(@NonNull Disposable d);

    /**
     * 成功
     */
    void onSuccess();

    /**
     * 失败
     */
    void onError(@NonNull ApiException e);
}
