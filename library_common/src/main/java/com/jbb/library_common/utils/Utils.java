package com.jbb.library_common.utils;

import android.app.AppOpsManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


/**
 * Created by vik on 2017/8/4.
 */

public class Utils {


    public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LETTERCHAR = "abcdefghijkllmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERCHAR = "0123456789";

    public static String channelName;
    public static String defaulChannelName = "self";

    /**
     * 返回一个定长的随机字符串(只包含大小写字母、数字)
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return sb.toString();
    }

    /**
     * 返回一个定长的随机纯字母字符串(只包含大小写字母)
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateMixString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(LETTERCHAR.length())));
        }
        return sb.toString();
    }

    /**
     * 返回一个定长的随机纯大写字母字符串(只包含大小写字母)
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateLowerString(int length) {
        return generateMixString(length).toLowerCase();
    }


    public static long getTime() {
        long l = System.currentTimeMillis();
        return l / 1000;
    }


    //图库选择图片返回的图片编号uri 转化为实际路径
    public static String getRealPathFromURI(Uri contentUri, Context context) { //传入图片uri地址
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    private static final double EARTH_RADIUS = 6378137.0;

    // 返回单位是米
    public static double getDistance(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }


    public static File saveBitmap(String filePath, Bitmap mBitmap) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }


            FileOutputStream fOut = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            if (fOut != null) {
                fOut.close();
            }
            return file;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    /**
     * 获取渠道信息
     */
    public static String getChannel(Context context) {
        if (TextUtils.isEmpty(channelName)) {
            try {
                PackageManager pm = context.getPackageManager();
                ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                channelName = appInfo.metaData.getString("CHANNEL");
//                channelName =  "self";
                if (TextUtils.isEmpty(channelName)) {
                    channelName = defaulChannelName;
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        return channelName;
    }


    public static String getPushTag(Context context) {
        StringBuffer sf = new StringBuffer();
        sf.append("Android")
                .append("_")
                .append(getChannel(context))
                .append("_")
                .append(getVerName(context));
        return sf.toString();
    }


//    public static List<CityBean> getCityJson(Context context, String fileName) {
//        ArrayList<CityBean> cityList = new ArrayList<>();
//        StringBuilder sb = new StringBuilder();
//        try {
//            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
//            String next = "";
//            while (null != (next = br.readLine())) {
//                sb.append(next);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            sb.delete(0, sb.length());
//        }
//
//        //解析城市数据
//        try {
//            JSONArray array = new JSONArray(sb.toString().trim());
//            for (int i = 0; i < array.length(); i++) {
//                JSONObject obj = array.optJSONObject(i);
//                JSONArray cities = obj.optJSONArray("cities");
//                if (cities != null && cities.length() > 0) {
//
//                    for (int j = 0; j < cities.length(); j++) {
//                        JSONObject city = cities.optJSONObject(j);
//                        CityBean citybean = new CityBean();
//                        citybean.setCityName(city.optString("city"));
//                        cityList.add(citybean);
//                    }
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return cityList;
//    }


//    public static String getFirstPinyin(String hanzi){
//        String convert = "";
//        if(!TextUtils.isEmpty(hanzi)){
//            char c = hanzi.charAt(0);
//            String[] strings = PinyinHelper.toHanyuPinyinStringArray(c);
//            if(strings != null){
//                convert  =  String.valueOf(strings[0].charAt(0)).toUpperCase();
//            }
//        }
//        return convert;
//    }


    public static int getAgeByIdCard(String idCard) {
        if (TextUtils.isEmpty(idCard) || idCard.length() < 18)
            return 0;
        int iAge = 0;
        Calendar cal = Calendar.getInstance();
        int iCurrYear = cal.get(Calendar.YEAR);
        String year = idCard.substring(6, 10);
        iAge = iCurrYear - Integer.valueOf(year);
        return iAge;
    }

    /**
     * 复制到剪切板
     *
     * @param context
     * @param text
     */
    public static void CopyToClipboard(Context context, String text) {
        ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", text);
        clip.setPrimaryClip(myClip);

    }


//    public static boolean checkNeedLogin(Activity activity,String className,JSONObject params){
//        if(!UserInfoInstance.getInstance().hasLogin()){//没登录
//            if(CommUtil.getClipboardUserId() != 0){//剪切板密令不为空，可以不登陆（贷超，信用卡）
//                return false;
//            }else{//没密令，需要登录
//                SYLoginUtils loginUtils = new SYLoginUtils(activity,className,params);
//                loginUtils.initSY();
//                return true;
//            }
//        }else{//不需要登录
//            return false;
//        }
//    }


    //获取设备唯一标识
    public static String getUniqueId(Context context) {
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;//adnroidId + 硬件序列号
//        return id;
        return MD5Util.md5(id);

    }

    /**
     * 获取已安装的应用
     *
     * @param context
     */
    public static void getAppList(Context context) {
        List<AppInfo> appInfos = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            AppInfo info = new AppInfo();
            info.appName = packageInfo.applicationInfo.loadLabel(pm).toString();
            info.packageName = packageInfo.packageName;
            info.versionName = packageInfo.versionName;
            info.versionCode = packageInfo.versionCode;
            info.appIcon = packageInfo.applicationInfo.loadIcon(pm);
            appInfos.add(info);

            // 判断系统/非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { // 非系统应用
                Log.d("info", "packageInfo=" + info.packageName + "---- appName = " + info.appName);
            } else {
                // 系统应用
                Log.d("info", "系统应用  packageInfo=" + info.packageName + "---- appName = " + info.appName);
            }
        }
    }

    public static void callPhone(Context mContext, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }


    /*
     * 判断通知权限是否打开
     */
    private boolean isNotificationEnable(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();

            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;

            Class appOpsClass = null; /* Context.APP_OPS_MANAGER */

            try {
                appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);

                Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
                int value = (int) opPostNotificationValue.get(Integer.class);
                return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();
            return areNotificationsEnabled;
        }
        return true;
    }

    /***
     *
     * @param view 页面的edittext
     */
    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String formatTwoDecimal(Double num) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(num);
    }




}
