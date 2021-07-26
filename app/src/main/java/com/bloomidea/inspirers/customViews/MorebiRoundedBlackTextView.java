package com.bloomidea.inspirers.customViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.bloomidea.inspirers.application.AppController;


/**
 * Created by michellobato on 16/02/17.
 */

public class MorebiRoundedBlackTextView extends androidx.appcompat.widget.AppCompatTextView {
    public MorebiRoundedBlackTextView(Context context) {
        super(context);
    }

    public MorebiRoundedBlackTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MorebiRoundedBlackTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        if(!isInEditMode()){
            super.setTypeface(AppController.getmInstance().getMorebiRoundedBlack(),Typeface.NORMAL);
        }
        else{
            super.setTypeface(tf,style);
        }
    }
}
