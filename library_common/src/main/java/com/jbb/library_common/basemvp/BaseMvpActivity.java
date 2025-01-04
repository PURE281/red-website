package com.jbb.library_common.basemvp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gyf.barlibrary.ImmersionBar;
import com.jbb.library_common.R;
import com.jbb.library_common.comfig.AppConfig;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.StatusUtil;
import com.jbb.library_common.utils.log.LogUtil;
import com.jbb.library_common.widght.LoadDialog;


/**
 * Created by ${lhh} on 2017/11/27.
 */

public abstract class BaseMvpActivity<V extends BaseMvpView, T extends BaseMvpPresenter<V>> extends FragmentActivity implements View.OnClickListener {
    public T presenter;

    private LoadDialog loadDialog;

    protected TextView mTitle;

    protected int animType;// 1 底部进入，退出

    protected ImageView backIv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if(this.getResources().getConfiguration().orientation != AppConfig.SCREEN_ORIENTATION){
        setRequestedOrientation(AppConfig.SCREEN_ORIENTATION);
//        }

        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        ActivityManager.getInstance().pushActivity(this);
        setStatusColor();

        presenter = initPresenter();
        presenter.attach((V) this);

        mTitle = (TextView) findViewById(R.id.title);
        if (mTitle != null)
            mTitle.setText(getATitle());

        backIv = (ImageView) findViewById(R.id.back_iv);
        if (backIv != null)
            backIv.setOnClickListener(this);

        init();

        //内存被回收后，重走app
        switch (ActivityManager.getInstance().getAppStatus()) {
            case KeyContacts.STATUS_FORCE_KILLED:
                restartApp();
                break;
            case KeyContacts.STATUS_NORMAL:
                break;
            default:
                break;
        }


    }

    protected void restartApp() {
        ARouter.init(getApplication());
        ARouter.getInstance().build(KeyContacts.MAIN_PATH)
                .withInt(KeyContacts.START_LAUNCH_ACTION, KeyContacts.STATUS_FORCE_KILLED).navigation();//

//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra(KeyContacts.START_LAUNCH_ACTION,KeyContacts.STATUS_FORCE_KILLED);
//        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        presenter.dettach();
        dismiss();
        ActivityManager.getInstance().popActivity(this);
        ImmersionBar.with(this).destroy();
        super.onDestroy();
    }


    public void show(String text) {
        if (loadDialog == null)
            loadDialog = new LoadDialog(this, text);

        if (!TextUtils.isEmpty(text))
            loadDialog.setText(text);
        loadDialog.show();
    }

    public void dismiss() {
        if (loadDialog != null)
            loadDialog.dismiss();
    }


    //不同页面需要改状态栏颜色时重写此方法
    protected void setStatusColor() {
        ImmersionBar.with(this)
                .statusBarColor(R.color.common_white, 0.2f)
                .fitsSystemWindows(true)
                .statusBarDarkFont(true)
                .init();
    }


    public void goTo(Class<?> to) {
        Intent it = new Intent(this, to);
        startActivity(it);
        entenAnim();
    }

    public void goTo(Class<?> to, Bundle bundle) {
        Intent it = new Intent(this, to);
        it.putExtras(bundle);
        startActivity(it);
        entenAnim();
    }

    public void goToForResult(Class<?> to, int requestCode) {
        Intent it = new Intent(this, to);
        startActivityForResult(it, requestCode);
        entenAnim();
    }

    public void goToForResult(Class<?> to, Bundle bundle, int requestCode) {
        Intent it = new Intent(this, to);
        it.putExtras(bundle);
        startActivityForResult(it, requestCode);
        entenAnim();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_iv) {
            finish();
            exitAnim();

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        exitAnim();
    }

    public void entenAnim() {
        if (animType == 1)
            overridePendingTransition(R.anim.menu_bottombar_in, R.anim.no_anim);
        else
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void exitAnim() {
        if (animType == 1)
            overridePendingTransition(R.anim.no_anim, R.anim.menu_bottombar_out);
        else
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // 实例化presenter
    public abstract T initPresenter();

    public abstract int getLayoutId();

    public abstract void init();

    public abstract String getATitle();
}