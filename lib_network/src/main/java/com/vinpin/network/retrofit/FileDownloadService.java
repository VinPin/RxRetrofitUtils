package com.vinpin.network.retrofit;

import java.util.HashMap;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * <pre>
 *     author: vinpin
 *     time  : 2018/7/28 17:16
 *     desc  : 文件下载服务接口
 * </pre>
 */
public interface FileDownloadService {

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl, @HeaderMap HashMap<String, String> headers);
}
