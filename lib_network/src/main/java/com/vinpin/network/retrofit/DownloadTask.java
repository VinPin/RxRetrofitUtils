package com.vinpin.network.retrofit;

/**
 * <pre>
 *     author: vinpin
 *     time  : 2018/07/28 17:00
 *     desc  : 下载任务对象
 * </pre>
 */
public class DownloadTask {

    public int progress;
    public long current;
    public long total;

    public DownloadTask(int progress, long current, long total) {
        this.progress = progress;
        this.current = current;
        this.total = total;
    }
}
