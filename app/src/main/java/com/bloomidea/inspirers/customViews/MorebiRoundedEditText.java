package com.bloomidea.inspirers.customViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.bloomidea.inspirers.application.AppController;


/**
 * Created by michellobato on 16/02/17.
 */

public class MorebiRoundedEditText extends androidx.appcompat.widget.AppCompatEditText {
    public MorebiRoundedEditText(Context context) {
        super(context);
    }

    public MorebiRoundedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MorebiRoundedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        if(!isInEditMode()){
            if (style == Typeface.BOLD) {
                super.setTypeface(AppController.getmInstance().getMorebiRoundedBold(),Typeface.BOLD);
            }else if(style == Typeface.ITALIC){
                super.setTypeface(AppController.getmInstance().getMorebiRoundedMedium(),Typeface.ITALIC);
            }else {
                super.setTypeface(AppController.getmInstance().getMorebiRoundedRegular(),Typeface.NORMAL);
            }
        }
        else{
            super.setTypeface(tf,style);
        }
    }
}
