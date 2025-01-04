package com.jbb.library_common.widght;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.jbb.library_common.R;
import com.jbb.library_common.utils.CommUtil;


/**
 * Created by lhh
 * DATE 2017/5/31.
 */

public class DoubleButtonDialog extends Dialog implements View.OnClickListener {

    private TextView title,contentTv;
    private Button cancelBtn,confirmBtn;
    ClickCallback clickBack;

    public DoubleButtonDialog(Context context) {
        super(context);
        init(context);
    }

    public DoubleButtonDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected DoubleButtonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }



    public void init(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.commit_dialog, null);
        title = view.findViewById(R.id.update_title_dialog);
        contentTv = view.findViewById(R.id.content_tv);


        confirmBtn = view.findViewById(R.id.btn_confirm_dialog);
        cancelBtn = view.findViewById(R.id.btn_cancel_dialog);

        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        setContentView(view);
        setCancelable(false);
        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawableResource(R.color.common_transparent);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();


        lp.width = CommUtil.getScreenWidth(context) * 5/6; //
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);

    }

    public void showDialog(){
        if (!this.isShowing()) {
            this.show();

        } else {
            this.dismiss();
        }
    }



    public DoubleButtonDialog setDatas(String title){
        this.title.setText(title);
        return this;
    }


    public DoubleButtonDialog setDatas(String title,String content){
        this.title.setText(title);
        this.contentTv.setText(content);
        contentTv.setVisibility(View.VISIBLE);
        return this;
    }

    public DoubleButtonDialog setClickBack(ClickCallback clickBack){
        this.clickBack = clickBack;
        return this;
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_confirm_dialog){
            dismiss();
            if(clickBack != null)
                clickBack.clickBack(v);
        }else if(v.getId() == R.id.btn_cancel_dialog){
            dismiss();
        }
    }
}
