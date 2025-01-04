package com.jbb.library_common.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;


import androidx.core.content.FileProvider;

import com.jbb.library_common.BaseApplication;
import com.jbb.library_common.comfig.AppConfig;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * 类: FileUtil <p>
 * 描述: 文件操作相关类 <p>
 */
@SuppressWarnings("deprecation")
public class FileUtil {


    /**
     * 方法: isExternalStorageCanUse <p>
     */
    public static boolean isExternalStorageCanUse() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long availCount = sf.getAvailableBlocks();
            long blocSize = sf.getBlockSize();
            //外部存储空间10M的话可行
            if (availCount * blocSize > 1024 * 1024 * 10) {
                return true;
            }
        }
        return false;
    }

    /**
     * 方法: isRootStorageCanUse <p>
     * 描述: 判断手机存储是否可用 <p>
     */

    public static boolean isRootStorageCanUse() {

        File root = Environment.getRootDirectory();
        StatFs sf = new StatFs(root.getPath());
        long availCount = sf.getAvailableBlocks();
        long blocSize = sf.getBlockSize();
        //内部部存储空间10M的话也可行
        if (availCount * blocSize > 1024 * 1024 * 10) {
            return true;
        }
        return false;
    }


    //根据url查返回缓存文件路径
    public static String getTargetFile(Context mContext, String url) {
        String fileName = FileUtil.getFileName(url);

        String filePath = FileUtil.getAppCachePath(mContext);
        if (TextUtils.isEmpty(filePath)) {
            return "";
        } else {
            return filePath + File.separator + fileName;
        }
    }

    /**
     * 方法: getFileName <p>
     * 描述: 从URL中获取filename <p>
     */
    public static String getFileName(String url) {
        String filename = "";
        // 从路径中获取
        if (TextUtils.isEmpty(filename)) {
            filename = url.substring(url.lastIndexOf("/") + 1);
        }
        return filename;
    }

    /**
     * 方法: isFileExit <p>
     * 描述: 判断下载的url对应的文件是否存在 <p>
     */
    public static boolean isFileExit(String url, String desDir) {
        String fileName = getFileName(url);
        String filePath = desDir + File.separator + fileName;
        File file = new File(filePath);
        return file.exists();
    }


    /**
     * 删除应用data下的cache缓存文件
     *
     * @param context
     */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /**
     * 删除SD卡cache缓存文件
     *
     * @param context
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * 删除某个文件夹下的文件
     *
     * @param dir
     */
    public static boolean deleteFilesByDirectory(File dir) {
        if (dir != null) {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteFilesByDirectory(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            } else {
                dir.delete();
            }
        }
        return true;
    }



    /**
     * 得到cache size大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static String getAppCacheSize(File file) {
        return getFormatSize(getFolderSize(file));
    }

    /**
     * 获取文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            return 0;
        }
        return size;
    }

    /**
     * 格式化size数据
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        if (size == 0) {
            return "0KB";
        }
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }








    /**
     * 描述:删除文件
     */
    public static void deleteFile(List<String> fileList) {
        Observable.fromArray(fileList).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object s) throws Exception {

                    }

                });
    }









    /**
     * @return
     */

    public static String getCachePath() {

        String directoryPath = "";

        //判断SD卡是否可用

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            directoryPath = Environment.getExternalStorageDirectory().getPath();
        } else {
            //没内存卡就存机身内存
            directoryPath = BaseApplication.getContext().getFilesDir().getPath();
        }

        File file = new File(directoryPath,AppConfig.SDCARD_DIR_PATH + "/" + AppConfig.SDCARD_DIR_PATH );

        if (!file.exists()) {//判断文件目录是否存在
            file.mkdirs();
        }

        return directoryPath;

    }


    /**
     *  app缓存文件目录
     *
     * @param context
     * @return
     */
    public static String getAppCachePath(Context context) {
        File tempPath =  context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (!tempPath.exists()) {
            tempPath.mkdirs();
        }
        return tempPath.getAbsolutePath();


//        if (FileUtil.isExternalStorageCanUse()) {
//            File tempPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +AppConfig.SDCARD_DIR_PATH);
//            if (!tempPath.exists()) {
//                tempPath.mkdirs();
//            }
//
//            return tempPath.getPath();
//        } else if (FileUtil.isRootStorageCanUse()) {
//            return context.getCacheDir().getAbsolutePath();
//        } else {
//            //磁盘空间不足
//            return "";
//        }
    }

    /**
     * app 图片路径文件目录
     *
     * @param context
     * @return
     */
    public static String getAppPicturePath(Context context) {
        File tempPath =  context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!tempPath.exists()) {
            tempPath.mkdirs();
        }
        return tempPath.getAbsolutePath();


//        if (FileUtil.isExternalStorageCanUse()) {
//            File tempPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +AppConfig.SDCARD_DIR_PATH +File.separator + AppConfig.SDCARD_DIR_PICTURE);
//            if (!tempPath.exists()) {
//                tempPath.mkdirs();
//            }
//
//            return tempPath.getPath();
//        } else if (FileUtil.isRootStorageCanUse()) {
//            return context.getCacheDir().getAbsolutePath();
//        } else {
//            //磁盘空间不足
//            return "";
//        }
    }


    /**
     * app 视频路径文件目录
     *
     * @param context
     * @return
     */
    public static String getAppVideoPath(Context context) {
        if (FileUtil.isExternalStorageCanUse()) {
            File tempPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +AppConfig.SDCARD_DIR_PATH +File.separator + AppConfig.SDCARD_DIR_VIDEO);
            if (!tempPath.exists()) {
                tempPath.mkdirs();
            }

            return tempPath.getPath();
        } else if (FileUtil.isRootStorageCanUse()) {
            return context.getCacheDir().getAbsolutePath();
        } else {
            //磁盘空间不足
            return "";
        }
    }


    public static Uri getFileProviderPath(Context mContext, File file){
        Uri contentUri = Uri.fromFile(file);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileProvider", file);
        }
        return contentUri;
    }
}
