package com.vinpin.network.callback;

import android.support.annotation.NonNull;

import com.vinpin.network.exception.ApiException;

import io.reactivex.disposables.Disposable;

/**
 * <pre>
 *     author: vinpin
 *     time  : 2018/07/23 13:10
 *     desc  : 下载文件的回调
 * </pre>
 */
public interface FileCallBack<T> {
    /**
     * 订阅关系
     */
    void onSubscribe(@NonNull Disposable d);

    /**
     * 下载进度
     */
    void onProgress(int progress, long current, long total);

    /**
     * 成功
     */
    void onSuccess(@NonNull T t);

    /**
     * 失败
     */
    void onError(@NonNull ApiException e);
}
