package com.jbb.library_common.download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.MD5Util;
import com.jbb.library_common.utils.MyX509TrustManager;
import com.jbb.library_common.utils.log.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * 类: AppUpdateUtil <p>
 * 描述: APP更新时候需要的工具类 <p>
 */
public class DownloadUtil {


    public static final int NOTIFY_ID_DOWNLOAD = 10001;
    public static final int NOTIFY_ID_FINISHED = 10002;
    public static final int NOTIFY_ID_ERROR = 10003;
    public static final int FINISH = 1;
    public static final int ERROR = 2;
    public static final int DOWNLOADING = 3;
    //SD卡无法使用
    public static final int SDCARDNOUSE = 4;

    /**
     * 方法: getTargetFile <p>
     * 描述: 得到下载的目标文件 如果为空则为UUID<p>
     */
    public static String getTargetFile(Context mContext, String url) {

        String fileName = FileUtil.getFileName(url);
        fileName = MD5Util.md5(fileName);
        if(!fileName.endsWith(".apk")){
            fileName = fileName + ".apk";
        }
//        if (TextUtils.isEmpty(fileName)) {
//            fileName = UUID.randomUUID().toString();
//        }
        String filePath = FileUtil.getAppCachePath(mContext);
        if (TextUtils.isEmpty(filePath)) {
            return "";
        } else {
            return filePath + File.separator + fileName;
        }
    }

    /**
     * 方法: installApk <p>
     * 描述: 发送安装APK指令 <p>
     */
    public static void installApk(Context mContext, String fileName) {
        mContext.startActivity(getInstallIntent(mContext, fileName));

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


//            Uri uri = Uri.fromFile(apkFile);
//            Intent installIntent = new Intent(Intent.ACTION_VIEW);
//            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
            return intent;
        }
        return new Intent();
    }

    public static void download(Context mContext, DownloadCallBack downloadCallBack, String url, String filePath) {
        int errorNumber = 1;
        //下载失败尝试重新下载（只尝试2次）
        while (!downloadInternal(mContext, downloadCallBack, url, filePath) && errorNumber < 2) {
            errorNumber++;
        }
    }

    /**
     * 方法: download <p>
     * 描述: 下载方法 <p>
     */
    protected static boolean downloadInternal(Context mContext, DownloadCallBack downloadCallBack, String url, String filePath) {
        int downLoadFileSize;
        int fileSize = 0;
        FileOutputStream fos = null;
        InputStream is = null;

        boolean success = true;
        try {
            if(url.startsWith("https://")){
                SSLContext sslContext = SSLContext.getInstance("SSL");//第一个参数为 返回实现指定安全套接字协议的SSLContext对象。第二个为提供者
                TrustManager[] tm = {new MyX509TrustManager()};
                sslContext.init(null, tm, new SecureRandom());
                HttpsURLConnection localURLConnection = (HttpsURLConnection) new URL(url).openConnection();
                localURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                localURLConnection.setReadTimeout(100000);
                localURLConnection.setConnectTimeout(100000);
                localURLConnection.setRequestMethod("GET");
                localURLConnection.setRequestProperty("Accept-Language", "zh-CN");
                localURLConnection.setRequestProperty("Charset", "UTF-8");
                localURLConnection.setRequestProperty("Connection", "Keep-Alive");
                localURLConnection.setRequestProperty("Accept-Encoding", "identity");
//            localURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0(Macintosh;U;IntelMacOSX10_6_8;en-us)AppleWebKit/534.50(KHTML,likeGecko)Version/5.1Safari/534.50");
                localURLConnection.connect();
                is = localURLConnection.getInputStream();
                fileSize = localURLConnection.getContentLength();
            }else {
                HttpURLConnection localURLConnection = (HttpURLConnection) new URL(url).openConnection();
                localURLConnection.setReadTimeout(100000);
                localURLConnection.setConnectTimeout(100000);
                localURLConnection.setRequestMethod("GET");
                localURLConnection.setRequestProperty("Accept-Language", "zh-CN");
                localURLConnection.setRequestProperty("Charset", "UTF-8");
                localURLConnection.setRequestProperty("Connection", "Keep-Alive");
                localURLConnection.setRequestProperty("Accept-Encoding", "identity");
//            localURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0(Macintosh;U;IntelMacOSX10_6_8;en-us)AppleWebKit/534.50(KHTML,likeGecko)Version/5.1Safari/534.50");
                localURLConnection.connect();
                is = localURLConnection.getInputStream();
                fileSize = localURLConnection.getContentLength();
            }

//            if (fileSize <= 0) {
//                LogUtil.e("downloadTask ERROR url:" + url + "  无法获知文件大小 ");
//                downloadCallBack.onError(ERROR);
//                return false;
//            }

            if (is == null) {
                downloadCallBack.onError(ERROR);
                return false;
            }

            //filePath = DownloadUtil.getTargetFile(mContext,url);
            if (TextUtils.isEmpty(filePath)) {
                LogUtil.e("downloadTask STORE ERROR  URL:" + url);
                downloadCallBack.onError(SDCARDNOUSE);
                return false;
            }
            File apkFile = new File(filePath);
            File parentFile=apkFile.getParentFile();
            if (!apkFile.exists()) {
                if (!parentFile.exists()){
                    parentFile.mkdirs();
                }
                apkFile.createNewFile();
            }

            fos = new FileOutputStream(filePath, false);
            // 把数据存入路径+文件名
            byte buf[] = new byte[1024 * 4];
            downLoadFileSize = 0;
            downloadCallBack.onDownloading(0);

            int readCount = 0;
            int count =0;
            long start = System.currentTimeMillis();
            do {
                // 循环读取
                int numread = 0;
                numread = is.read(buf);
                if (numread == -1) {
                    break;
                }
                fos.write(buf, 0, numread);
                downLoadFileSize += numread;

                if(fileSize <= 0){//做假效果
                    long now = System.currentTimeMillis();
                    if((now - start) % 1000 == 0){
                        count += 5 + (now - start) / 1000;
                        if(count > 98)
                            count = 98;
                        downloadCallBack.onDownloading(count);
                    }
                }else{
                    if (readCount % 10 == 0 ) {
                        downloadCallBack.onDownloading(downLoadFileSize * 100 / fileSize);
                    }
                }

                readCount++;
            } while (true);
            downloadCallBack.onSuccess();
        } catch (Exception e) {
            downloadCallBack.onError(ERROR);
            success = false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return success;
        }
    }
}
