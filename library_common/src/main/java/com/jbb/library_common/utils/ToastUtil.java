package com.jbb.library_common.utils;

import android.widget.Toast;

import com.jbb.library_common.BaseApplication;


public class ToastUtil {

    private static Toast customToast;


    /**
     * 描述:显示自定义toast  无需context
     * 作者:nn
     * 时间:2016/5/5 17:12
     * 版本:3.2.3
     */
    public static void showCustomToast(String msg) {
//        if (customToast == null) {
////            customToast = new Toast(MyApplication.getContext());
////            LayoutInflater inflate = (LayoutInflater)
////                    MyApplication.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////            View v = inflate.inflate(com.android.internal.R.layout.transient_notification, null);
////            TextView tv = (TextView)v.findViewById(com.android.internal.R.id.message);
////            tv.setText(text);
////        } else {
////            ((TextView) customToast.getView().findViewById(R.id.toast_message_tv)).setText(msg);
////        }
//        customToast.show();
        if (customToast == null) {
//            customToast = Toast.makeText(BaseApplication.getContext(),msg,Toast.LENGTH_SHORT);
            System.out.println("customToast = null");
        }
//        customToast.setText(msg);
//        customToast.show();
    }


}