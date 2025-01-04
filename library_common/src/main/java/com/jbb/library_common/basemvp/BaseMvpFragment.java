package com.jbb.library_common.basemvp;

import android.app.Activity;
//import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jbb.library_common.R;

//import android.app.Fragment;
import androidx.fragment.app.Fragment;

public abstract class BaseMvpFragment<V extends BaseMvpView,T extends BaseMvpPresenter<V>> extends Fragment {
    public T presenter;
    protected Activity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initPresenter();
        presenter.attach((V)this);
    }

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated( View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    public void show(String text){
        if(activity instanceof BaseActivity){
            ((BaseActivity)activity).show(text);
        }

        if(activity instanceof BaseMvpActivity){
            ((BaseMvpActivity)activity).show(text);
        }
    }

    public void dismiss(){
        if(activity instanceof BaseActivity){
            ((BaseActivity)activity).dismiss();
        }

        if(activity instanceof BaseMvpActivity){
            ((BaseMvpActivity)activity).dismiss();
        }
    }


    public void goTo(Class<?> to,Bundle bundle){
        Intent it = new Intent(activity,to);
        it.putExtras(bundle);
        startActivity(it);
        entenAnim();
    }


    public void goTo(Class<?> to){
        Intent it = new Intent(activity,to);
        startActivity(it);
        entenAnim();
    }

    public void goToForResult(Class<?> to,int requestCode){
        Intent it = new Intent(activity,to);
        startActivityForResult(it,requestCode);
        entenAnim();
    }

    public void goToForResult(Class<?> to,Bundle bundle,int requestCode){
        Intent it = new Intent(activity,to);
        it.putExtras(bundle);
        startActivityForResult(it,requestCode);
        entenAnim();
    }



    public void entenAnim(){
        activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void exitAnim(){
        activity.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }


    @Override
    public void onDestroy() {
        presenter.dettach();
        activity = null;
        super.onDestroy();
    }
    // 实例化presenter
    public abstract T initPresenter();
    public abstract int getLayoutId();
    public abstract void init(View view);

}
