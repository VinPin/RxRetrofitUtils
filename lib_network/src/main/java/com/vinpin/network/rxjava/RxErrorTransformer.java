package com.vinpin.network.rxjava;


import com.vinpin.network.exception.ApiException;
import com.vinpin.network.retrofit.HttpResponse;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

/**
 * 预处理异常错误转换器
 *
 * @author vinpin
 * create at 2018/03/20 14:17
 */
public class RxErrorTransformer<T> implements ObservableTransformer<HttpResponse<T>, T> {

    @Override
    public ObservableSource<T> apply(Observable<HttpResponse<T>> upstream) {
        return upstream.map(new Function<HttpResponse<T>, T>() {
            @Override
            public T apply(HttpResponse<T> tHttpResponse) throws Exception {
                if (tHttpResponse.code != ApiException.ErrorCode.REQUEST_OK) {
                    throw new ApiException(tHttpResponse.code, tHttpResponse.msg);
                }
                return tHttpResponse.result;
            }
        }).onErrorResumeNext(new Function<Throwable, ObservableSource<? extends T>>() {
            @Override
            public ObservableSource<? extends T> apply(Throwable throwable) throws Exception {
                throwable.printStackTrace();
                return Observable.error(ApiException.handleException(throwable));
            }
        }).compose(RxSchedulerHepler.<T>io_main());
    }

    public static RxErrorTransformer create() {
        return new RxErrorTransformer();
    }

    private RxErrorTransformer() {

    }
}
