package com.bloomidea.inspirers.customViews;

import android.content.Context;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

import com.bloomidea.inspirers.application.AppController;

import java.text.DecimalFormat;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;


/**
 * Created by michellobato on 16/02/17.
 */

public class MorebiRoundedMediumTimerTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static DecimalFormat timerFormatter = new DecimalFormat("00");

    private Object objectAux;

    private CountDownTimer timer;
    private GregorianCalendar timerDate;

    private TimerTextViewListener timerTextViewListener;

    public MorebiRoundedMediumTimerTextView(Context context) {
        super(context);
    }

    public MorebiRoundedMediumTimerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MorebiRoundedMediumTimerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        if(!isInEditMode()){
            super.setTypeface(AppController.getmInstance().getMorebiRoundedMedium(),Typeface.NORMAL);
        }
        else{
            super.setTypeface(tf,style);
        }
    }

    public void setTimerDate(GregorianCalendar date, Object object, TimerTextViewListener listener){
       if(timer!=null){
            timer.cancel();
        }

        this.objectAux = object;
        this.timerTextViewListener = listener;


        if(date!=null) {
            GregorianCalendar today = new GregorianCalendar();

            timerDate = new GregorianCalendar();
            timerDate.setTimeInMillis(date.getTimeInMillis());

            if (timerDate.getTimeInMillis() > today.getTimeInMillis()) {
                long difInicical = timerDate.getTimeInMillis() - today.getTimeInMillis();

                timer = new CountDownTimer(difInicical, 1000) {
                    public void onTick(long millisUntilFinished) {
                        long d = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.DAYS.toMillis(d);
                        long h = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.HOURS.toMillis(h);
                        long m = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.MINUTES.toMillis(m);
                        long s = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                        MorebiRoundedMediumTimerTextView.this.setText(timerFormatter.format(h)+":"+timerFormatter.format(m)+":"+timerFormatter.format(s)+" ");
                    }

                    public void onFinish() {
                        if(timerTextViewListener!=null) {
                            timerTextViewListener.onTimeEnd(objectAux);
                        }

                        MorebiRoundedMediumTimerTextView.this.setText("");
                    }
                }.start();
            } else {
                this.setText("");

                if(timerTextViewListener!=null){
                    timerTextViewListener.onTimeEnd(objectAux);
                }
            }
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if(visibility != View.VISIBLE && timer!=null){
            timer.cancel();
        }
    }

    public interface TimerTextViewListener{
        void onTimeEnd(Object obj);
    }
}
