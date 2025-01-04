package com.jbb.library_common;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;
import com.jbb.library_common.basemvp.BaseActivity;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.CommUtil;
import com.jbb.library_common.utils.FileUtil;
import com.jbb.library_common.utils.ToastUtil;
import com.jbb.library_common.utils.Utils;
import com.jbb.library_common.widght.TakePhotoUtil;
import com.jbb.library_common.widght.XWebView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jbb.library_common.widght.TakePhotoUtil.PHOTO_REQUEST_TAKEPHOTO;

public class WebViewActivity extends BaseActivity implements XWebView.WebViewBackFace {

    private LinearLayout parentLayout;
    private LinearLayout titleLayout;
    private ImageView closeIv;
    private XWebView webView;
    private List<String> titleList = new ArrayList<String>();

    private boolean first = true;
    private String url;
    private String title;

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private File tempFile;
    Uri captureUri;
    private TakePhotoUtil photoUtil;

    private boolean videoType;
    private int appTitle; //1跳转后页面带有原生返回栏目，0不要原生返回栏

    @Override
    public void title(String title) {
//        if(!first) {
//            mTitle.setText(title);
//            if(!titleList.contains(title))
//                titleList.add(title);
//        }

//        first = false;
    }

    @Override
    public void progress(int progress) {
//        if(progress < 100){
//            show("");
//        }else{
//            dismiss();
//        }
    }

    @Override
    public void faceOpenFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        this.mUploadMessage = uploadMsg;
        choosePhoto(acceptType);
    }

    @Override
    public void faceOnShowFileChooser(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        this.mUploadCallbackAboveL = filePathCallback;
        String acceptType = "image/*";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            acceptType = fileChooserParams.getAcceptTypes()[0];
        }
        choosePhoto(acceptType);
    }

    @Override
    public void onPageFinished() {
        dismiss();
    }

    @Override
    public void onPageStart() {
        show("");
    }


    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        if (webView.canGoBack()) {
            webView.goBack();
//            if(titleList.size()>0)
//                titleList.remove(titleList.size()-1);
        } else {
            this.finish();
            exitAnim();
        }
    }


    @Override
    public void onClick(View v) {
//        super.onClick(v);
        if (v.getId() == R.id.close_iv) {
            this.finish();
            exitAnim();
        } else if (v.getId() == R.id.back_iv) {
            this.finish();
            exitAnim();
        }
    }


    @Override
    public void init() {
        parentLayout = findViewById(R.id.parent_ll);
        titleLayout = findViewById(R.id.title_layout);
        closeIv = findViewById(R.id.close_iv);
        webView = findViewById(R.id.webview);
        webView.setWebViewBackFace(this);

        initIntent(getIntent());

        closeIv.setOnClickListener(this);
//        setStatusHeight();
    }

    private void setStatusHeight() {
        int actionBarHeight = ImmersionBar.getActionBarHeight(this);
        int statusBarHeight = CommUtil.getStatusBarHeight(this);
        titleLayout.setPadding(0, statusBarHeight, 0, 0);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initIntent(intent);
    }

    private void initIntent(Intent intent) {
        if (intent == null || intent.getExtras() == null)
            return;
        url = intent.getStringExtra(KeyContacts.KEY_URL);
        title = intent.getStringExtra(KeyContacts.KEY_TITLE);
        appTitle = intent.getExtras().getInt("appTitle");
//        webView.setTitle(title);
//        titleList.add(title);
        mTitle.setText(title);

        titleLayout.setVisibility(appTitle == 0 ? View.GONE : View.VISIBLE);

        if (appTitle == 0) {
//            setStatusBar();
        }
        webView.setParentLayout(parentLayout, appTitle, titleLayout);
        webView.loadURL(url);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    public String getATitle() {
        return "";
    }

    @Override
    protected void onDestroy() {

        dismiss();
        super.onDestroy();
        if (webView != null) {
            webView.setWebViewBackFace(null);
            webView.destroy();
            webView = null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
//            case 10:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    webView.startDownServer();
//                } else {
////                    ToastUtil.showCustomToast("拒绝");
////                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
////                    startActivityForResult(intent, 100);
//
//                    Uri packageURI = Uri.parse("package:" + getPackageName());//设置包名，可直接跳转当前软件的设置页面
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
//                    startActivityForResult(intent, XWebView.GET_UNKNOWN_APP_SOURCES);
//
//                }
//                break;
            case PHOTO_REQUEST_TAKEPHOTO:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (videoType) {
                        startVideo();
                    } else {
                        if (photoUtil != null)
                            photoUtil.takePhoto();
                    }
                } else {
                    ToastUtil.showCustomToast("拒绝了权限可能导致部分功能异常！");
                }
                break;
            case TakePhotoUtil.PHOTO_REQUEST_GALLERY:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (photoUtil != null)
                        photoUtil.choosePhoto();
                } else {
                    ToastUtil.showCustomToast("拒绝了权限可能导致部分功能异常！");
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case XWebView.GET_UNKNOWN_APP_SOURCES:
                webView.checkIsInstalls();
                break;

            case PHOTO_REQUEST_TAKEPHOTO:
                if (videoType) {
                    if (mUploadCallbackAboveL != null) {
                        onActivityResultAboveL(requestCode, resultCode, data);
                    } else {
                        if (mUploadMessage != null) {
                            if (resultCode == Activity.RESULT_OK) {
                                mUploadMessage.onReceiveValue(null);
                                mUploadMessage = null;
                            } else {
                                mUploadMessage.onReceiveValue(captureUri);
                                mUploadMessage = null;
                            }
                        }
                    }
                } else {
                    if (resultCode == Activity.RESULT_OK) {
                        if ("zte".equals(BaseApplication.mobileName)) {
                            Bundle bundle = data.getExtras();
                            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
                            Utils.saveBitmap(tempFile.getPath(), bitmap);
                        }
                    }
                    Uri mOriginUri = photoUtil.getImageUri();
                    if (mUploadCallbackAboveL != null) {
                        onActivityResultAboveL(requestCode, resultCode, data);
                    } else {
                        if (mUploadMessage != null) {
                            if (resultCode == Activity.RESULT_OK) {
                                mUploadMessage.onReceiveValue(null);
                                mUploadMessage = null;
                            } else {
                                mUploadMessage.onReceiveValue(mOriginUri);
                                mUploadMessage = null;
                            }
                        }
                    }
                }


                break;
            case TakePhotoUtil.PHOTO_REQUEST_GALLERY:
//                if (data == null || "".equals(data)) {
//                    webView.setUploadMessage(null);
//                    return;
//                }
                if (mUploadCallbackAboveL != null) {
                    onActivityResultAboveL(requestCode, resultCode, data);
                } else {
                    if (mUploadMessage != null) {
                        if (data == null) {
                            mUploadMessage.onReceiveValue(null);
                            mUploadMessage = null;
                        } else {
                            Uri uri = data.getData();
                            mUploadMessage.onReceiveValue(uri);
                            mUploadMessage = null;
                        }
                    }
                }
//                Uri selectedImage = data.getData();
//                String picturePath = "";
//                if(selectedImage.toString().contains("content://")) { //如果包含有content开头，需要转化为其实际路径，
//                    picturePath = Utils.getRealPathFromURI(selectedImage,this);
//                }else{
//                    picturePath = selectedImage.getPath();
//                }

                break;

        }
    }


    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {

        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PHOTO_REQUEST_TAKEPHOTO) {
                if (videoType) {
                    mUploadCallbackAboveL.onReceiveValue(new Uri[]{captureUri});
                    mUploadCallbackAboveL = null;
                } else {
                    Uri imageUri = photoUtil.getImageUri();
                    mUploadCallbackAboveL.onReceiveValue(new Uri[]{imageUri});
                    mUploadCallbackAboveL = null;
                }

            } else {
                if (intent != null) {
                    Uri uri = intent.getData();
                    ClipData clipData = intent.getClipData();
                    if (clipData != null) {
                        results = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            results[i] = item.getUri();
                        }
                    }
                    if (uri != null)
                        results = new Uri[]{uri};
                }
                mUploadCallbackAboveL.onReceiveValue(results);
                mUploadCallbackAboveL = null;
            }

        } else {
            mUploadCallbackAboveL.onReceiveValue(null);
            mUploadCallbackAboveL = null;
        }

    }


    private void choosePhoto(String acceptType) {

        if ("video/*".equals(acceptType)) {
            videoType = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PHOTO_REQUEST_TAKEPHOTO);
                } else {
                    startVideo();
                }
            } else {
                startVideo();
            }

        } else {
            videoType = false;
            if (photoUtil == null) {
                photoUtil = new TakePhotoUtil(this, R.style.common_loading_dialog);
                tempFile = new File(FileUtil.getAppCachePath(this), "htmlPhoto.jpg");
//                Uri mOriginUri = PermissionUtil.getFileProviderPath(this, tempFile);
                photoUtil.setTempFile(tempFile);
            }
            photoUtil.showPop();
            photoUtil.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (photoUtil.isActiveCancel()) {
                        if (mUploadCallbackAboveL != null) {
                            mUploadCallbackAboveL.onReceiveValue(null);
//                        mUploadCallbackAboveL = null;
                        }
                        if (mUploadMessage != null) {
                            mUploadMessage.onReceiveValue(null);
//                        mUploadMessage = null;
                        }
                    }
                }
            });
        }

    }


    private void startVideo() {

        tempFile = new File(FileUtil.getAppCachePath(this), "auth.mp4");
        if (!tempFile.exists()) {
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            captureUri = FileUtil.getFileProviderPath(this, tempFile);
        } else {
            captureUri = Uri.fromFile(tempFile);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, captureUri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
    }


    @Override
    public void setStatusColor() {
        ImmersionBar.with(this)
                .statusBarColor(R.color.title_bar, 0.1f)
                .fitsSystemWindows(true)
//                .statusBarDarkFont(true)
                .init();
//        setTransparentStatusBar();
    }



    protected void setTransparentStatusBar() {
        ImmersionBar.with(this)
                .transparentStatusBar()
                .statusBarDarkFont(false)
                .fitsSystemWindows(false)
                .fullScreen(true)
                .init();
    }


    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setTransparentStatusBar();

                break;
            case Configuration.ORIENTATION_PORTRAIT:
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                setStatusColor();
                break;
        }
    }
}
