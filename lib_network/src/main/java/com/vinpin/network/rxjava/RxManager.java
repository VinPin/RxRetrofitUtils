package com.vinpin.network.rxjava;

import io.reactivex.disposables.Disposable;

/**
 * Retrofit+RxJava取消请求管理接口
 *
 * @author vinpin
 *         create at 2018/03/20 13:41
 */
public interface RxManager<T> {

    void add(T tag, Disposable disposable);

    void remove(T tag);

    void clear(T tag);

    void clearAll();
}
