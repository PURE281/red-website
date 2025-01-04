package com.jbb.library_common.widght;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jbb.library_common.BaseApplication;
import com.jbb.library_common.R;
import com.jbb.library_common.utils.CommUtil;
import com.jbb.library_common.utils.FileUtil;

import java.io.File;


/**
 * Created by ${lhh} on 2017/8/16.
 */

public class TakePhotoUtil extends Dialog implements View.OnClickListener {

    public static final int PHOTO_REQUEST_TAKEPHOTO = 1001;// 拍照
    public static final int PHOTO_REQUEST_GALLERY = 1002;// 从相册中选择
    public static final int PHOTO_REQUEST_CROP = 1003;// 裁剪
    private File tempFile;
    private String mobileName;

    private TextView paizhaoBtn, tukuBtn, cancelBtn;
    private Activity activity;
    private Uri imageUri;

    private boolean isActiveCancel = true;//是否是主动点击取消关闭的 dialog

    public boolean isActiveCancel() {
        return isActiveCancel;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public TakePhotoUtil(Context context) {
        this(context, 0);
    }

    public TakePhotoUtil(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
        tempFile = new File(FileUtil.getAppPicturePath(context), "headPhoto.jpg");
    }

    public void setTempFile(File tempFile) {
        this.tempFile = tempFile;
    }

    public File getTempFile() {
        return tempFile;
    }

    public void init(Context context) {

        this.activity = (Activity) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_paizhao_view, null);
        paizhaoBtn = (TextView) view.findViewById(R.id.btn_paizhao);
        tukuBtn = (TextView) view.findViewById(R.id.btn_tuku);
        cancelBtn = (TextView) view.findViewById(R.id.btn_cancel);

        paizhaoBtn.setOnClickListener(this);
        tukuBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        // 设置SelectPicPopupWindow的View
        this.setContentView(view);

        setCanceledOnTouchOutside(true);


        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setWindowAnimations(R.style.bottomdialogAnim);
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setBackgroundDrawableResource(R.color.common_transparent);


        lp.width = CommUtil.getScreenWidth(context); // 高度设置为屏幕
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);


    }

    public void showPop() {
        if (!this.isShowing()) {
            this.show();
            isActiveCancel = true;
        } else {
            this.dismiss();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_paizhao) {
            isActiveCancel = false;
            takePhoto();
            dismiss();
        }else if(v.getId() ==  R.id.btn_tuku){
            isActiveCancel = false;
            choosePhoto();
            dismiss();
        }else if(v.getId() == R.id.btn_cancel){
            isActiveCancel = true;
            dismiss();
        }
    }


    public void takePhoto() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PHOTO_REQUEST_TAKEPHOTO);
            } else {
                takeCamara();
            }
        } else {
            takeCamara();
        }
    }

    private void takeCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if ("zte".equals(BaseApplication.mobileName)) {
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                imageUri = FileUtil.getFileProviderPath(activity, tempFile);
            }else{
                imageUri = Uri.fromFile(tempFile);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }
        activity.startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
    }


    public void choosePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PHOTO_REQUEST_GALLERY);
            } else {
                openPhoto();
            }
        } else {
            openPhoto();
        }

    }

    private void openPhoto() {
        // 从文件中选择图片
        Intent intent = new Intent();
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                intent.setType("image/*");//相片类型
        intent.setAction(Intent.ACTION_PICK); //
        activity.startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }


    /**
     * 调用系统照片的裁剪功能，修改编辑头像的选择模式(适配Android7.0)
     */
    public Intent invokeSystemCrop(File file) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(FileUtil.getFileProviderPath(activity, file), "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        intent.putExtra("scale", true);

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);


        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        return intent;
    }


}
