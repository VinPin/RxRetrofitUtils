
# RxRetrofitUtils
基于Retrofit和RxJava封装的网络请求库

#### 开始使用
1. 在Application中初始化，并设置全局的配置
 ```
RxRetrofitUtils.init(this);
RxRetrofitUtils.getInstance().config()
                .setBaseUrl(baseUrl)
                .setHeaders(hashmap)
                .setCookie(false)
                .setSslSocketFactory()
                .setReadTimeout(10)
                .setWriteTimeout(10)
                .setConnectTimeout(10)
                .setLog(BuildConfig.DEBUG);
 ```
2. 在你的module中新建，举例：
 ```
public interface AppApiService {
    /**
     * 检查新版本
     *
     * @param versionCode 当前应用版本码
     * @return
     */
    @FormUrlEncoded
    @POST("user/version")
    Observable<String> checkUpdate(@Field("versionCode") String versionCode);
}
 ```
3. 使用RxRetrofitUtils发起网络请求
 ```
RxRetrofitUtils.createApi(AppApiService.class).checkUpdate(currentCode)
                .compose(RxErrorTransformer.<String>create())
                .subscribe(new RxCallBack<String>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        super.onSubscribe(d);
                    }

                    @Override
                    public void onSuccess(@NonNull String s) {
                        
                    }

                    @Override
                    public void onError(@NonNull ApiException e) {
                        
                    }
                });
 ```
4. 使用RxDownloadUtils下载文件
```
RxDownloadUtils.getInstance().download(url, filePath, new FileCallBack<File>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onProgress(int progress, long current, long total) {
                
            }

            @Override
            public void onSuccess(@NonNull File file) {
                
            }

            @Override
            public void onError(@NonNull ApiException e) {
               
            }
        });

```

#### 如何定制
将lib_network作为Module导入你的项目中

有几个地方需要根据你项目需求自定义：
1. 定制接口返回对象HttpResponse中属性名与接口返回的json字段对应上
 ```
public class HttpResponse<T> {

    public int code; // 错误码
    public String msg; // 错误信息
    public T result; // 返回数据对象
}
 ```
2. 定制ApiException中内部类ErrorCode，以下是我列举出的，可根据实际需求修改
 ```
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
 ```
3. 还有其他地方，你也可以随意修改。
