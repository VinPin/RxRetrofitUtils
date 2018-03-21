package com.vinpin.network.rxjava;

import android.support.annotation.NonNull;

import com.vinpin.network.exception.ApiException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 封装的请求回调
 *
 * @author vinpin
 *         create at 2018/03/20 15:56
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
        if (e instanceof ApiException) {
            onError((ApiException) e);
        } else {
            onError(new ApiException(e, ApiException.ErrorCode.UNKNOWN, "未知错误"));
        }
    }

    @Override
    public void onComplete() {

    }

    public abstract void onSuccess(@NonNull T t);

    public abstract void onError(@NonNull ApiException e);
}
