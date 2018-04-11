package com.vinpin.network.callback;

import android.support.annotation.NonNull;

import com.vinpin.network.exception.ApiException;

import io.reactivex.disposables.Disposable;

/**
 * 统一的网络请求转换后的回调
 *
 * @author vinpin
 * create at 2018/03/20 15:56
 */
public interface ConvertCallBack<T> {
    /**
     * 订阅关系
     */
    void onSubscribe(@NonNull Disposable d);

    /**
     * 成功
     */
    void onSuccess(@NonNull T t);

    /**
     * 失败
     */
    void onError(@NonNull ApiException e);
}
