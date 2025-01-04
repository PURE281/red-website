package com.jbb.library_common.download;

/**
 * Created by nn on 2016/6/16.
 */
public interface DownloadCallBack {
    public void onError(int errorType);
    public void onDownloading(int process);
    public void onSuccess();
}
