package com.vinpin.network.retrofit;

import android.support.annotation.NonNull;

import java.io.IOException;

import io.reactivex.annotations.Nullable;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * <pre>
 *     author: vinpin
 *     time  : 2018/07/23 13:28
 *     desc  : 下载文件带进度的ResponseBody
 * </pre>
 */
public class ProgressResponseBody extends ResponseBody {

    private ResponseBody responseBody;
    private OnProgressListener progressListener;
    private BufferedSource bufferedSource;

    public interface OnProgressListener {

        void update(long bytesRead, long contentLength);
    }

    public ProgressResponseBody(ResponseBody responseBody, OnProgressListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long bytesReaded = 0L;

            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                bytesReaded += bytesRead != -1 ? bytesRead : 0;
                if (progressListener != null) {
                    progressListener.update(bytesReaded, responseBody.contentLength());
                }
                return bytesRead;
            }
        };
    }
}
