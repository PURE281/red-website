package com.jbb.library_common.download;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.log.LogUtil;

import java.io.File;

/**
 * Created by nn on 2016/6/17.
 */
public abstract class DownloadBaseIntentService extends IntentService {
    protected String url;

    public DownloadBaseIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        downloadTask(intent);
    }

    /**
     * 方法: downloadTask <p>
     * 描述: 开启一个下载线程 <p>
     */
    protected void downloadTask(Intent intent) {
        if (intent == null) {
            return;
        }
        url = intent.getStringExtra(KeyContacts.KEY_URL);// 下载链接
        LogUtil.d("PluginDownloadService download url:" + url);
        if (TextUtils.isEmpty(url)) {
            downloadCallBack.onError(DownloadUtil.ERROR);
            return;
        }
        //插件更新不会很频繁 产品说暂时可以不做删除  后续版本需要先清空下文件夹
        try {
            String fileName = FileUtil.getFileName(url);
            String filePath = FileUtil.getAppCachePath(this) + File.separator + "download"+ File.separator + fileName;
            DownloadUtil.download(this, downloadCallBack, url, filePath);
        } catch (Exception e) {
            downloadCallBack.onError(DownloadUtil.ERROR);
        }
    }

    public abstract void success();

    public void error(int errorType) {

    }
    public void downloading(int process) {

    }
    protected DownloadCallBack downloadCallBack = new DownloadCallBack() {
        public void onError(int errorType) {
//            if(this.getClass().getName().equals(HTMLDownloadService.class.getName())){
////                ThirdStatistics.htmlDownload("失败");
//            }else{
////                ThirdStatistics.pluginDownload("失败");
//            }

            error(errorType);
        }

        public void onDownloading(int process) {
            downloading(process);
        }

        public void onSuccess() {
//            if(this.getClass().getName().equals(HTMLDownloadService.class.getName())){
////                ThirdStatistics.htmlDownload("成功");
//            }else{
////                ThirdStatistics.pluginDownload("成功");
//            }
            success();
        }
    };
}
