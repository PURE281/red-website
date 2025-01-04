package com.jbb.library_common.widght;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jbb.library_common.R;
import com.jbb.library_common.utils.CommUtil;
import com.jbb.library_common.utils.GlideUtils;

public class ImgTextView extends LinearLayout {

    private ImageView imageView;
    private TextView textView;

    private int defauleImageSize = CommUtil.dp2px(23);
    private int defauleTextSize = 13;
    private int defauleTextColor = Color.parseColor("#333333");
    private int defauleMarginTop = CommUtil.dp2px(12);
    private int direction = LinearLayout.VERTICAL;//方向

    public ImgTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImgTextView);
        direction = typedArray.getInt(R.styleable.ImgTextView_direction, direction);
        float imageSizeWidth = typedArray.getDimension(R.styleable.ImgTextView_cimageSizeWidth, defauleImageSize);
        float imageSizeHeight = typedArray.getDimension(R.styleable.ImgTextView_cimageSizeHeight, defauleImageSize);
        int src = typedArray.getResourceId(R.styleable.ImgTextView_csrc, 0);
        float textSize = typedArray.getDimension(R.styleable.ImgTextView_ctextSize, defauleTextSize);
        String text = typedArray.getString(R.styleable.ImgTextView_ctext);
        int textColor = typedArray.getColor(R.styleable.ImgTextView_ctextColor,defauleTextColor);
        float marginTop = typedArray.getDimension(R.styleable.ImgTextView_cmarginTop, defauleMarginTop);

        typedArray.recycle();

        setOrientation(direction);
        setGravity(Gravity.CENTER);

        imageView  = new ImageView(context);
        imageView.setImageResource(src);
        LayoutParams params = new LayoutParams((int)imageSizeWidth,(int)imageSizeHeight);
        imageView.setLayoutParams(params);
        addView(imageView);

        textView = new TextView(context);
//        textView.setTextSize(textSize);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        textView.setText(text);
        textView.setTextColor(textColor);
        LayoutParams params2 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        if(direction == LinearLayout.VERTICAL)
            params2.topMargin = (int) marginTop;
        else{
            params2.leftMargin = (int) marginTop;
        }
        textView.setLayoutParams(params2);
        addView(textView);

    }


    public void setText(String text) {
        textView.setText(text);
    }

    public void setImageResouse(int id,int imageSize){
        imageView.setImageResource(id);
        LayoutParams params = new LayoutParams((int)imageSize,(int)imageSize);
        imageView.setLayoutParams(params);
    }

    public void setImage(String url){
        GlideUtils.display(getContext(),url,imageView);
    }

    public void displayImage(String url){
        GlideUtils.display(getContext(),url,imageView);
    }

}
