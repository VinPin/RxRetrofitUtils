package com.vinpin.network.exception;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.IOException;
import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * 统一的响应异常
 *
 * @author vinpin
 *         create at 2018/03/20 15:08
 */
public class ApiException extends Exception {

    public int code;
    public String message;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    public ApiException(Throwable throwable, String message) {
        super(throwable);
        this.message = message;
    }

    public ApiException(Throwable throwable, int code, String message) {
        super(throwable);
        this.code = code;
        this.message = message;
    }

    public static ApiException handleException(Throwable e) {
        ApiException ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ApiException(e, ErrorCode.HTTP_ERROR);
            try {
                ex.message = httpException.response().errorBody().string();
            } catch (IOException e1) {
                e1.printStackTrace();
                ex.message = "未知错误";
            }
            return ex;
        } else if (e instanceof ResultException) {
            ResultException resultException = (ResultException) e;
            ex = new ApiException(e, resultException.code);
            ex.message = resultException.message;
            return ex;
        } else if (e instanceof ConnectTimeoutException
                || e instanceof ConnectException
                || e instanceof UnknownHostException) {
            ex = new ApiException(e, ErrorCode.NETWORD_ERROR);
            ex.message = "无网络,请重试!";
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof JsonSerializer
                || e instanceof NotSerializableException
                || e instanceof ParseException) {
            ex = new ApiException(e, ErrorCode.PARSE_ERROR);
            ex.message = "解析异常";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ApiException(e, ErrorCode.SSL_ERROR);
            ex.message = "证书验证异常";
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new ApiException(e, ErrorCode.TIMEOUT_ERROR);
            ex.message = "连接超时";
            return ex;
        } else {
            // 默认未知错误
            ex = new ApiException(e, ErrorCode.UNKNOWN);
            ex.message = "未知错误";
            return ex;
        }
    }

    /**
     * 服务器返回结果的异常
     */
    public static class ResultException extends RuntimeException {

        public int code;
        public String message;

        public ResultException(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    /**
     * 约定的错误码
     */
    public static class ErrorCode {

        public static final int REQUEST_OK = 0;
        /**
         * Access Token无效，或已过期
         */
        public static final int ERROR_CODE_401001 = 401001;
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1001;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;
        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;
        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1006;
    }
}
