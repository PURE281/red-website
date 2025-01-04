package com.jbb.library_common.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.jbb.library_common.R;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.ToastUtil;
import com.jbb.library_common.utils.log.LogUtil;


/**
 * 类: DownloadService <p>
 * 描述: 下载service  下载后会自动安装APK  传递url和title进来就可以自动下载 <p>
 */
public class AppDownloadService extends DownloadBaseIntentService {
    // 通知栏
    private NotificationManager notificationManger;
    private Notification notification;
    private NotificationCompat.Builder mBuilder;
    private String title;
    private boolean isRunning = false;

    private static final int PUSH_NOTIFICATION_ID = (0x001);
    private static final String PUSH_CHANNEL_ID = "PUSH_NOTIFY_ID";
    private static final String PUSH_CHANNEL_NAME = "PUSH_NOTIFY_NAME";


    public AppDownloadService() {
        super(AppDownloadService.class.getSimpleName());
    }

    public AppDownloadService(String name) {
        super(name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ( isRunning) {
            return super.onStartCommand(intent, flags, startId);
        }
        ToastUtil.showCustomToast("下载中，请稍候");
        notificationManger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            if (notificationManger != null) {
                notificationManger.createNotificationChannel(channel);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 方法: downloadTask <p>
     * 描述: 开启一个下载线程 <p>
     */
    protected void downloadTask(Intent intent) {
        if (intent == null && isRunning) {
            return;
        }

        isRunning = true;
        title = intent.getStringExtra(KeyContacts.KEY_TITLE);// 下载链接
        displayNotificationMessage(title);
        url = intent.getStringExtra(KeyContacts.KEY_URL);// 下载链接
        LogUtil.e("downloadTask download url:" + url);
        if (TextUtils.isEmpty(url)) {
            downloadCallBack.onError(DownloadUtil.ERROR);
            return;
        }
        try {
            String filePath = DownloadUtil.getTargetFile(AppDownloadService.this, url);
            DownloadUtil.download(AppDownloadService.this, downloadCallBack, url, filePath);
        } catch (Exception e) {
            downloadCallBack.onError(DownloadUtil.ERROR);
        }
    }



//    public void checkIsInstalls() {
//        if (Build.VERSION.SDK_INT >= 26) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
//                //请求安装未知应用来源的权限
//                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 10);
//            } else {
//                DownloadUtil.installApk(AppDownloadService.this, DownloadUtil.getTargetFile(AppDownloadService.this, url));
//            }
//        } else {
//            DownloadUtil.installApk(AppDownloadService.this, DownloadUtil.getTargetFile(AppDownloadService.this, url));
//        }
//    }

    @Override
    public void success() {
        updateNotification(DownloadUtil.FINISH, 0);
        DownloadUtil.installApk(AppDownloadService.this, DownloadUtil.getTargetFile(AppDownloadService.this, url));
    }

    public void error(int errorType) {
        if (errorType == DownloadUtil.ERROR) {
            updateNotification(DownloadUtil.ERROR, 0);
        } else if (errorType == DownloadUtil.SDCARDNOUSE) {
            notificationManger.cancel(DownloadUtil.NOTIFY_ID_DOWNLOAD);
            notificationManger.cancel(DownloadUtil.NOTIFY_ID_FINISHED);
            ToastUtil.showCustomToast(getString(R.string.module_lib_cd_card_cannot_be_used));
        }
    }

    public void downloading(int process) {
        updateNotification(DownloadUtil.DOWNLOADING, process);
    }

    /**
     * 方法: displayNotificationMessage <p>
     * 描述: 下载时候显示一个通知栏 <p>
     */
    private void displayNotificationMessage(String title) {
        Intent notificationIntent = new Intent();
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setContentText(url)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(contentIntent)
                .setChannelId(PUSH_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setTicker(title);

        notification = mBuilder.build();

        notification.flags |= Notification.FLAG_NO_CLEAR;

        notificationManger.notify(DownloadUtil.NOTIFY_ID_DOWNLOAD, notification);

    }

    /**
     * 方法: displayCancelNotification <p>
     * 描述: 显示一个提示完成 或者出错的通知栏 <p>
     */
    private void displayCancelNotification(String title, String content, int id) {
        Intent notificationIntent = new Intent();

        if (id == DownloadUtil.NOTIFY_ID_FINISHED) {
            notificationIntent = DownloadUtil.getInstallIntent(this, DownloadUtil.getTargetFile(AppDownloadService.this, url));
        }
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(contentIntent)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setTicker(content);
        notification = mBuilder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManger.notify(id, notification);

    }

    /**
     * 方法: updateNotification <p>
     * 描述: 更新通知栏 <p>
     */
    protected void updateNotification(int type, int progress) {
        LogUtil.d("type=" + type + "---progress=" + progress);
        switch (type) {
            case DownloadUtil.DOWNLOADING:
                notification.flags = Notification.FLAG_NO_CLEAR;
                mBuilder.setProgress(100, progress, false)
                        .setContentInfo(progress + "%");
                notificationManger.notify(DownloadUtil.NOTIFY_ID_DOWNLOAD, mBuilder.build());
                break;
            case DownloadUtil.FINISH:
                notificationManger.cancel(DownloadUtil.NOTIFY_ID_DOWNLOAD);
                displayCancelNotification(title, getString(R.string.module_lib_download_completed_click_to_install), DownloadUtil.NOTIFY_ID_FINISHED);
                //notificationManger.cancel(NOTIFY_ID_FINISHED);
                return;
            case DownloadUtil.ERROR:
                displayCancelNotification(title, getString(R.string.module_lib_download_failed), DownloadUtil.NOTIFY_ID_ERROR);
                notificationManger.cancel(DownloadUtil.NOTIFY_ID_DOWNLOAD);
                notificationManger.cancel(DownloadUtil.NOTIFY_ID_FINISHED);
                break;
            default:
                break;
        }

    }

}
