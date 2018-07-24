package com.vinpin.network.callback;

import android.support.annotation.NonNull;

import com.vinpin.network.exception.ApiException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * <pre>
 *     author: vinpin
 *     time  : 2018/03/20 15:56
 *     desc  : 封装的请求回调
 * </pre>
 */
public abstract class RxCallBack<T> implements Observer<T> {

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull T t) {
        onSuccess(t);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        onError(ApiException.handleException(e));
    }

    @Override
    public void onComplete() {

    }

    public abstract void onSuccess(@NonNull T t);

    public abstract void onError(@NonNull ApiException e);
}
