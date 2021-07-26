package com.bloomidea.inspirers.customViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.bloomidea.inspirers.application.AppController;


/**
 * Created by michellobato on 16/02/17.
 */

public class MorebiRoundedMediumSwitch extends androidx.appcompat.widget.SwitchCompat {
    public MorebiRoundedMediumSwitch(Context context) {
        super(context);
    }

    public MorebiRoundedMediumSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MorebiRoundedMediumSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
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
}
