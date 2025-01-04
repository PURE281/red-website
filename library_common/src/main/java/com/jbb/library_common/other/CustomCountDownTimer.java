package com.jbb.library_common.other;

import android.content.Context;
import android.os.CountDownTimer;

import org.apache.commons.lang3.time.DurationFormatUtils;

/**
 * Created by mikyou on 16-10-22.
 */
public class CustomCountDownTimer extends CountDownTimer{
    private Context mContext;//传入的上下文对象
//    protected TextView mDateTv;//一个TextView实现倒计时
    private long  mGapTime;//传入设置的时间间隔即倒计时的总时长
    private long mCount = 1000;//倒计时的步长　一般为1000代表每隔1s跳一次
    private String mTimePattern = "HH:mm:ss";//timePattern 传入的时间的样式　如: HH:mm:ss HH时mm分ss秒　dd天HH时mm分ss秒
    private String mTimeStr;


    public CustomCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);

    }

    public CustomCountDownTimer(Context mContext, long mGapTime, String mTimePattern) {
        this(mContext,mGapTime,1000,mTimePattern);
    }
    public CustomCountDownTimer(Context mContext, long mGapTime, int mCount, String mTimePattern) {
        super(mGapTime, mCount);
        this.mContext = mContext;
        this.mGapTime = mGapTime;//倒计时总时长
        this.mCount = mCount;//每次倒计时的步长，默认是1000
        this.mTimePattern = mTimePattern;//时间的格式:如HH:mm:ss或者dd天HH时mm分ss秒等

    }

    @Override
    public void onTick(long l) {
        if (l > 0) {
//            mTimeStr = DurationFormatUtils.formatDuration(l, mTimePattern);
            //这是apache中的common的lang包中DurationFormatUtils类中的formatDuration，通过传入
            //一个时间格式就会自动将倒计时转换成相应的mTimePattern的样式(HH:mm:ss或dd天HH时mm分ss秒)
            if(timerOverFace != null)
                timerOverFace.onTick(mTimeStr);
        }
    }

    @Override
    public void onFinish() {
        cancelTimer();
        if(timerOverFace != null)
            timerOverFace.timeOver();
    }

    public void cancelTimer(){
        this.cancel();
    }
    public void startTimer(){
        this.start();
    }

    public String getmTimeStr() {
        return mTimeStr;
    }


    TimerOverFace timerOverFace;
    public interface TimerOverFace{
        void timeOver();
        void onTick(String timeStr);
    }

    public void setTimerOverFace(TimerOverFace timerOverFace) {
        this.timerOverFace = timerOverFace;
    }
}
