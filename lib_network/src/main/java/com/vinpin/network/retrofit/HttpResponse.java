package com.vinpin.network.retrofit;

/**
 * 网络请求返回解析对象
 *
 * @author vinpin
 *         create at 2018/03/19 16:06
 */
public class HttpResponse<T> {

    public int code;
    public String msg;
    public T result;
}
