/**
 * 工程: Coyote<p>
 * 标题: CommUtil.java<p>
 * 包:   com.niuniucaip.lotto.ui.util.other<p>
 * 描述: TODO<p>
 * 作者: nn<p>
 * 时间: 2014-7-24 下午5:01:45<p>
 * 版权: Copyright 2014 Shenzhen NiuNiucaip Tech Co.,Ltd.<p>
 * All rights reserved.<p>
 */

package com.jbb.library_common.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.NotificationManagerCompat;

import com.jbb.library_common.BaseApplication;
import com.jbb.library_common.comfig.AppConfig;
import com.jbb.library_common.utils.log.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 */
public class CommUtil {




    /**
     * convert dp to its equivalent px
     */
    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, BaseApplication.getContext().getResources().getDisplayMetrics());
    }

    /**
     * convert sp to its equivalent px
     */
    public static int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, BaseApplication.getContext().getResources().getDisplayMetrics());
    }


    /**
     * 方法: dip2px <p>
     */
    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 方法: px2dip <p>
     * 描述: 将像素转换成dp <p>
     */
    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);

    }

    /**
     * 方法: px2sp <p>
     * 描述: 将px值转换为sp值，保证文字大小不变<p>
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 方法: sp2px <p>
     * 描述: 将sp值转换为px值，保证文字大小不变<p>
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 方法: getVersionName <p>
     * 描述: 获取版本名 <p>
     */
    public static String getVersionName() {
        try {
            return BaseApplication.getContext().getPackageManager().getPackageInfo(BaseApplication.getContext().getPackageName(), 0).versionName;
        } catch (Exception e) {
            //			/e.printStackTrace();
            return "";
        }
    }

    /**
     * 方法: getVersionCode <p>
     * 描述: 获取版本号 <p>
     */
    public static int getVersionCode() {
        try {
            return BaseApplication.getContext().getPackageManager().getPackageInfo(BaseApplication.getContext().getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return 0;
    }

    /**
     * 方法: getPackageName <p>
     */
    public static String getPackageName() {
        try {
            PackageManager packageManager = BaseApplication.getContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(BaseApplication.getContext().getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 方法: isIntentSafe <p>
     * 描述: 检测某个启动外部的Activity是否是可行的
     * eg 如果没有安装市场  评分时候肯定打不开<p>
     */
    public static boolean isIntentSafe(Activity activity, Uri uri) {
        boolean isIntentSafe = false;
        try {
            Intent mapCall = new Intent(Intent.ACTION_VIEW, uri);
            PackageManager packageManager = activity.getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(mapCall, 0);
            isIntentSafe = activities.size() > 0;
        } catch (Exception e) {
            isIntentSafe = false;
            //	e.printStackTrace();
        }
        return isIntentSafe;
    }



    /**
     * 方法: getRandomString <p>
     * 描述: 获取随机唯一字符串<p>
     */
    public static String getRandomString() {
        String hexUUID = new String(UUID.randomUUID().toString());
        String namiaoString = System.nanoTime() + "";
        return hexUUID + namiaoString;
    }

    /**
     * 方法: getStatusBarHeight <p>
     * 描述: 获取通知栏高度<p>
     */
    public static int getStatusBarHeight(Activity mActivity) {
        int statueBarHeight = 0;
        int resourceId = mActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statueBarHeight = mActivity.getResources().getDimensionPixelSize(resourceId);
            LogUtil.e("res barHeight:::" + statueBarHeight);
        }
        if (statueBarHeight == 0) {
            Rect rect = new Rect();
            mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            statueBarHeight = rect.top;
            LogUtil.e("getStatusBarHeight barHeight:::" + statueBarHeight);
        }
        return statueBarHeight;
    }



    /**
     * 方法: closeSoftInput <p>
     * 描述: 关闭键盘输入法<p>
     */
    public static void closeSoftInput(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * 方法: getFromAssets <p>
     * 描述: 读取asset下文件内容<p>
     */
    public static String getContentFromAssets(String fileName) {
        String result = "";
        try {
            InputStream in = BaseApplication.getContext().getResources().getAssets().open(fileName);
            //获取文件的字节数
            int lenght = in.available();
            //创建byte数组
            byte[] buffer = new byte[lenght];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            result = new String(buffer, "utf-8");
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 方法: deleteFile <p>
     * 描述: 删除文件<p>
     */
    public static void deleteFile(File file) {
        try {
            LogUtil.i("delete file path=" + file.getAbsolutePath());
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                } else if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFile(files[i]);
                    }
                }
                file.delete();
            } else {
                LogUtil.e("delete file no exists " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法: gradeApp <p>
     * 描述: 应用评分<p>
     */
    public static void gradeApp(Activity activity) {
        Uri uri = Uri.parse(String.format("market://details?id=%s", CommUtil.getPackageName()));
        try {
            if (isIntentSafe(activity, uri)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
            // 没有安装市场
            else {
                ToastUtil.showCustomToast("您未安装任何应用市场，感谢您对我们的肯定");
            }
        } catch (Exception e) {
            ToastUtil.showCustomToast("无法打开应用市场，请确定您是否安装应用市场");
        }

    }


    /**
     * 描述:判断某个packgae是否存在
     */
    public static boolean isAvilible(String packageName) {
        final PackageManager packageManager = BaseApplication.getContext().getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<String>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    /**
     * 方法: getMd5ByFile <p>
     * 描述: 获取文件的MD5 <p>
     */
    public static String getMd5ByFile(File file) {
        String value = "";
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(byteBuffer);
            byte[] bytes = md5.digest();
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result = result + temp;
            }
            value = result;
        } catch (Exception e) {
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        LogUtil.e("md5" + value);
        return value;
    }



    /**
     * 方法: isExternalStorageCanUse <p>
     * 描述: TODO<p>
     * 参数: @param 最小容量 M 单位m sizeM
     */
    public static boolean isExternalStorageCanUse(int sizeM) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long availCount = sf.getAvailableBlocks();
            long blocSize = sf.getBlockSize();
            //外部存储空间10M的话可行
            if (availCount * blocSize > 1024 * 1024 * sizeM) {
                return true;
            }
        }
        return false;
    }

    /**
     * 方法: isRootStorageCanUse <p>
     * 描述: TODO<p>
     * 参数: @param 最小容量 M 单位m sizeM
     */
    public static boolean isRootStorageCanUse(int sizeM) {
        File root = Environment.getRootDirectory();
        StatFs sf = new StatFs(root.getPath());
        long availCount = sf.getAvailableBlocks();
        long blocSize = sf.getBlockSize();
        //内部部存储空间10M的话也可行
        if (availCount * blocSize > 1024 * 1024 * sizeM) {
            return true;
        }
        return false;
    }


    /**
     * 方法: createTempFile <p>
     * 描述: 生成临时文件<p>
     */
    public static File createTempFile() {
        File imgFile = null;
        String imgPath = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = timeStamp + System.nanoTime() + ".jpg";
        if (CommUtil.isExternalStorageCanUse(10)) {
            String imgDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + AppConfig.SDCARD_DIR_PATH + "/tempFile/";
            File imgDir = new File(imgDirPath);
            if (!imgDir.exists()) {
                imgDir.mkdirs();
            }
            imgPath = imgDirPath + fileName;
        } else if (CommUtil.isRootStorageCanUse(5)) {
            imgPath = BaseApplication.getContext().getCacheDir().getAbsolutePath() + "/" + fileName;
        } else {
            ToastUtil.showCustomToast("存储空间不足");
            return null;
        }
        imgFile = new File(imgPath);
        try {
            Runtime.getRuntime().exec("chmod 777 " + imgFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imgFile;
    }


    /**
     *  触摸点是否在view范围上
     * @param view
     * @param ev
     * @return
     */
    public static boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }




    // 获取屏幕的宽度
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    // 获取屏幕的宽度
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }


    private static String[] telFirst = "130,131,132,133,134,135,136,137,138,139,150,151,152,153,155,156,157,158,159,170,178,180,182,183,185,186,187,188,189,196,198,199".split(",");

    public static String getTelephone() {
        int index = getNum(0, telFirst.length - 1);
        String first = telFirst[index];
        String second = String.valueOf(getNum(1, 888) + 10000).substring(1);
        String thrid = String.valueOf(getNum(1, 9100) + 10000).substring(1);
        return first + second + thrid;
    }

    public static int getNum(int start, int end) {
        return (int) (Math.random() * (end - start + 1) + start);
    }


    /**
     * 获取硬件制造厂商
     *
     * @return
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm !=null){
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    public static boolean checkNotifySetting(Context mContext) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(mContext);
        // areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。

        return manager.areNotificationsEnabled();
    }

    public static void jumpNotity(Context mContext) {
        try {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //5.0
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", getPackageName());
                intent.putExtra("app_uid", mContext.getApplicationInfo().uid);

            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {  //4.4
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
            } else if (Build.VERSION.SDK_INT >= 15) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", getPackageName(), null));
            }
            mContext.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
            Intent intent = new Intent();

            //下面这种方案是直接跳转到当前应用的设置界面。
            //https://blog.csdn.net/ysy950803/article/details/71910806
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            mContext.startActivity(intent);
        }
    }


    /**
     * 复制数据到剪切板
     *
     * @param data
     * @param pasteLable
     */
    public static void copyDataToPasteBoard(String pasteLable, String data) {
        ClipboardManager clipboardManager = (ClipboardManager) BaseApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(pasteLable, data); //文本型数据 clipData 的构造方法。
        clipboardManager.setPrimaryClip(clipData); // 将 字符串 str 保存 到剪贴板。
    }

}
