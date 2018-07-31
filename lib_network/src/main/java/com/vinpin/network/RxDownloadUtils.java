package com.vinpin.network;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.vinpin.commonutils.FileUtils;
import com.vinpin.network.callback.FileCallBack;
import com.vinpin.network.exception.ApiException;
import com.vinpin.network.http.DownloadRxRetrofit;
import com.vinpin.network.retrofit.DownloadTask;
import com.vinpin.network.retrofit.FileDownloadService;
import com.vinpin.network.retrofit.ProgressResponseBody;
import com.vinpin.network.rxjava.RxSchedulerHepler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * <pre>
 *     author: vinpin
 *     time  : 2018/07/23 13:10
 *     desc  : 基于 Retrofit+Rxjava 封装的文件下载网络请求工具类
 * </pre>
 */
public class RxDownloadUtils {

    private RxDownloadUtils() {
    }

    public static RxDownloadUtils getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final RxDownloadUtils sInstance = new RxDownloadUtils();
    }

    public DownloadRxRetrofit getDownloadRxRetrofit() {
        return new DownloadRxRetrofit();
    }


    /**
     * 下载文件（不支持断点续传，下载之前会删除已存在的文件）
     *
     * @param filePath 下载文件存储路径
     * @param callBack 下载回调
     */
    public void download(String downloadUrl, String filePath, @NonNull final FileCallBack<File> callBack) {
        download(downloadUrl, new HashMap<String, String>(0), filePath, callBack);
    }

    /**
     * 下载文件（不支持断点续传，下载之前会删除已存在的文件）
     *
     * @param downloadUrl 下载文件地址
     * @param headers     请求头
     * @param filePath    下载文件存储路径
     * @param callBack    下载回调
     */
    public void download(String downloadUrl, @NonNull HashMap<String, String> headers, final String filePath, @NonNull final FileCallBack<File> callBack) {
        FileUtils.createFileByDeleteOldFile(filePath);
        getDownloadRxRetrofit().sslSocketFactory().setOnProgressListener(new ProgressResponseBody.OnProgressListener() {
            @SuppressLint("CheckResult")
            @Override
            public void update(long bytesRead, long contentLength) {
                // update()是运行在子线程中
                if (contentLength != 0) {
                    int progress = (int) (((bytesRead + 0f) / contentLength) * 100);
                    // 切换到主线程中更新UI
                    Observable.just(new DownloadTask(progress, bytesRead, contentLength))
                            .compose(RxSchedulerHepler.<DownloadTask>io_main())
                            .subscribe(new Consumer<DownloadTask>() {
                                @Override
                                public void accept(DownloadTask task) {
                                    callBack.onProgress(task.progress, task.current, task.total);
                                }
                            });
                }
            }
        }).createApi(FileDownloadService.class).downloadFile(downloadUrl, headers)
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody body) throws Exception {
                        writeResponeBodyToDisk(filePath, body);
                    }
                })
                .compose(RxSchedulerHepler.<ResponseBody>io_main())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        callBack.onSubscribe(d);
                    }

                    @Override
                    public void onNext(ResponseBody body) {
                        callBack.onSuccess(FileUtils.getFileByPath(filePath));
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(ApiException.handleException(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 将ResponseBody写入磁盘
     *
     * @param filePath 文件保存路径
     * @param body     ResponseBody
     */
    public void writeResponeBodyToDisk(@NonNull String filePath, @NonNull ResponseBody body) {
        File output = new File(filePath);
        BufferedSink sink = null;
        BufferedSource source = null;
        try {
            if (!output.exists()) {
                output.createNewFile();
            }
            sink = Okio.buffer(Okio.sink(output));
            byte[] buffer = new byte[4096];
            int len;
            source = body.source();
            while ((len = source.read(buffer)) != -1) {
                sink.write(buffer, 0, len);
            }
            sink.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sink != null) {
                try {
                    sink.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
