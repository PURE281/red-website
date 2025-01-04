package com.jbb.library_common.utils;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.jbb.library_common.comfig.AppConfig;
import com.jbb.library_common.comfig.InterfaceConfig;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class DownloadManager {
    private android.app.DownloadManager downloadManager;
    private Context context;
    private long downloadId;
    private String url;
    private String name;

    private String path;

    private AndroidDownloadManagerListener listener;


    private Timer mTimer;


    private android.app.DownloadManager.Query query;

    public DownloadManager setListener(AndroidDownloadManagerListener listener) {
        this.listener = listener;
        return this;
    }

    public DownloadManager(Context context, String url) {
        this(context, url, getFileNameByUrl(url));
        mTimer = new Timer();
    }

    public DownloadManager(Context context, String url, String name) {
        this.context = context;
        this.url = url;
        this.name = name;
        mTimer = new Timer();
    }


    /**
     * 开始下载
     */
    public void download() {
        showProgressDialog();
        //创建对话框
        ProgressDialog dialog = new ProgressDialog(context);
        android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(Uri.parse(url));
        //移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(false);
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle(name);

        if(AppConfig.HOST_ADDRESS_CONFIG_INDEX==1){
            request.addRequestHeader("referer", InterfaceConfig.CDN_URL);
        }

        request.setDescription("正在下载中......");
        request.setVisibleInDownloadsUi(true);

        //设置下载的路径
        File file = new File(FileUtil.getAppCachePath(context), name);
        request.setDestinationUri(Uri.fromFile(file));
        path = file.getAbsolutePath();

        //获取DownloadManager
        if (downloadManager == null) {
            downloadManager = (android.app.DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        }
        //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
        if (downloadManager != null) {
            if (downloadId != 0) {
                downloadManager.remove(downloadId);
            }
            downloadId = downloadManager.enqueue(request);
        }
        query = new android.app.DownloadManager.Query();
        //通过下载的id查找
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        mTimer.schedule(mTimerTask, 0,1000);


        //注册广播接收者，监听下载状态
        context.registerReceiver(receiver, new IntentFilter(android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        context.registerReceiver(receiver, new IntentFilter(android.app.DownloadManager.ACTION_VIEW_DOWNLOADS));
    }

    //广播监听下载的各个状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            android.app.DownloadManager.Query query = new android.app.DownloadManager.Query();
            //通过下载的id查找
            query.setFilterById(downloadId);
            Cursor cursor = downloadManager.query(query);
            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(android.app.DownloadManager.COLUMN_STATUS));
                switch (status) {
                    //下载暂停
                    case android.app.DownloadManager.STATUS_PAUSED:

                        break;
                    //下载延迟
                    case android.app.DownloadManager.STATUS_PENDING:
                        break;
                    //正在下载
                    case android.app.DownloadManager.STATUS_RUNNING:
                        break;
                    //下载完成
                    case android.app.DownloadManager.STATUS_SUCCESSFUL:
                        cursor.close();
                        context.unregisterReceiver(receiver);
                        if (listener != null)
                            listener.onSuccess();
                        handler.removeCallbacksAndMessages(null);
                        installApk(path);

                        break;
                    //下载失败
                    case android.app.DownloadManager.STATUS_FAILED:
                        if (listener != null)
                            listener.onFailed();
                        cursor.close();
                        context.unregisterReceiver(receiver);
                        handler.removeCallbacksAndMessages(null);
                        break;
                }

            }
        }
    };


    // —————————————————————————————————————————————

    /**
     * 通过URL获取文件名
     *
     * @param url
     * @return
     */
    private static final String getFileNameByUrl(String url) {
        String filename = url.substring(url.lastIndexOf("/") + 1);
        filename = filename.substring(0, filename.indexOf("?") == -1 ? filename.length() : filename.indexOf("?"));
        return filename;
    }


    //调用系统安装
    private void installApk(String appFile) {
        context.startActivity(getInstallIntent(context, appFile));
    }

    /**
     * 方法: getInstallIntent <p>
     * 描述: 得到安装apk的intent <p>
     */
    public static Intent getInstallIntent(Context mContext, String fileName) {
        File apkFile = new File(fileName);
        if (apkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileProvider", apkFile);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            return intent;
        }
        return new Intent();
    }

    public interface AndroidDownloadManagerListener {

        void onSuccess();

        void onFailed();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int pro = bundle.getInt("progressMsg");

            progressDialog.setProgress(pro);

            long nowTotalRxBytes = getTotalRxBytes(); // 获取当前数据总量
            long nowTimeStamp = System.currentTimeMillis(); // 当前时间
            // kb/s
            speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp == lastTimeStamp ? nowTimeStamp : nowTimeStamp
                    - lastTimeStamp));// 毫秒转换
            lastTimeStamp = nowTimeStamp;
            lastTotalRxBytes = nowTotalRxBytes;
//            handler.postDelayed(runnable, 1000);// 每1秒执行一次runnable.

            if(speed==0) speed=1;
            progressDialog.setMessage("不要退出QAQ "+"\n"+"已下载 "+pro+"% "+"\n"+"当前下载速度为 "+ speed + "/kb 还剩 "+stringForTime((totalSize-bytesDownload)/speed) +"秒");
            System.out.println("不要退出QAQ "+"\n"+"已下载 "+pro+"%了 当前下载速度为 "+ speed + "/kb 还剩 "+stringForTime((totalSize-bytesDownload)/speed) +"秒");
//            System.out.println("pure download progress is " + pro);

        }
    };
    private long lastTotalRxBytes = 0; // 最后缓存的字节数
    private long lastTimeStamp = 0; // 当前缓存时间
    private long bytesDownload = 0; // 已下载的数据
    private long totalSize = 0; // 总共需要下载的数据

    private long speed = 0;


    private long getTotalRxBytes() {
        // 得到整个手机的流量值
        return TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0
                : (TrafficStats.getTotalRxBytes() / 1024);// 转为KB

    }
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            Cursor cursor = downloadManager.query(query.setFilterById(downloadId));

            if (cursor != null && cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(android.app.DownloadManager.COLUMN_STATUS));
//                Log.d("down", "status:" + status);
                switch (status) {
                    //下载暂停
                    case android.app.DownloadManager.STATUS_PAUSED:
//                        System.out.println("pure stop");
                        break;
                    //下载延迟
                    case android.app.DownloadManager.STATUS_PENDING:
//                        Log.e("down", "====下载延迟=====");
                        break;
                    //正在下载
                    case android.app.DownloadManager.STATUS_RUNNING:
                        //Log.e("down", "====正在下载中=====");
//                        System.out.println("pure downloading");
                        break;
                    //下载完成
                    case android.app.DownloadManager.STATUS_SUCCESSFUL:
                        //下载完成安装APK
                        //Log.e("down", "====下载完成=====");
                        progressDialog.cancel();
                        mTimerTask.cancel();

//                        System.out.println("pure down");
                        break;
                    //下载失败
                    case android.app.DownloadManager.STATUS_FAILED:
//                        System.out.println("pure fail");
                        progressDialog.cancel();
                        mTimerTask.cancel();
                        break;
                }


                bytesDownload = cursor.getLong(cursor.getColumnIndex(android.app.DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                String descrition = cursor.getString(cursor.getColumnIndex(android.app.DownloadManager.COLUMN_DESCRIPTION));
                String id = cursor.getString(cursor.getColumnIndex(android.app.DownloadManager.COLUMN_ID));
                String localUri = cursor.getString(cursor.getColumnIndex(android.app.DownloadManager.COLUMN_LOCAL_URI));
                String mimeType = cursor.getString(cursor.getColumnIndex(android.app.DownloadManager.COLUMN_MEDIA_TYPE));
                String title = cursor.getString(cursor.getColumnIndex(android.app.DownloadManager.COLUMN_TITLE));
                totalSize = cursor.getLong(cursor.getColumnIndex(android.app.DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

//                Log.d("down", "bytesDownload:" + bytesDownload);
//                Log.d("down", "totalSize:" + totalSize);

                int progress = (int) (bytesDownload * 100 / totalSize);
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putInt("progressMsg", progress);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
            cursor.close();
        }
    };

//    ProgressDialog downProgress;
    ProgressDialog progressDialog;
    private void showProgressDialog(){
        int MAX = 100;
        progressDialog  = new ProgressDialog(context);
//        downProgress = progressDialog;
        progressDialog.setTitle("正在下载新版本.....");

        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("123");
//        progressDialog.setIndeterminate(true);
        progressDialog.setMax(MAX);

//        progressDialog.setIcon(android.R.drawable.btn_star);

        progressDialog.setCancelable(false);

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                downloadManager.remove(downloadId);

                progressDialog.cancel();
                mTimerTask.cancel();
                handler.removeCallbacksAndMessages(null);
            }
        };

        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消", onClickListener);

        progressDialog.show();

    }


    StringBuilder mFormatBuilder = new StringBuilder();
    Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    //将长度转换为时间
    private String stringForTime(long timeMs) {
        long totalSeconds = timeMs / 1000;

        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
}

