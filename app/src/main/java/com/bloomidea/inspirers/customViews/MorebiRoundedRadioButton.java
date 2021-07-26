package com.bloomidea.inspirers.customViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.bloomidea.inspirers.application.AppController;

/**
 * Created by michellobato on 26/04/17.
 */

public class MorebiRoundedRadioButton extends androidx.appcompat.widget.AppCompatRadioButton {

    public MorebiRoundedRadioButton(Context context) {
        super(context);
        init();
    }

    public MorebiRoundedRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MorebiRoundedRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if(!isInEditMode()) {
            Typeface aux = getTypeface();
            if (aux == null) {
                setTypeface(AppController.getmInstance().getMorebiRoundedRegular());
            } else {
                setTypeface(getTypeface(), getTypeface().getStyle());
            }
        }
    }

    @Override
    public void setTypeface(Typeface tf, int style) {

        if(!isInEditMode()){
            super.setTypeface(AppController.getmInstance().getMorebiRoundedRegular());
        }
        else{
            super.setTypeface(tf,style);
        }


    }
}