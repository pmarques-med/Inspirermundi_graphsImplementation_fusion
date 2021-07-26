package com.bloomidea.inspirers.customViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.bloomidea.inspirers.application.AppController;


/**
 * Created by michellobato on 16/02/17.
 */

public class MorebiRoundedSquareTextView extends androidx.appcompat.widget.AppCompatTextView {
    public MorebiRoundedSquareTextView(Context context) {
        super(context);
    }

    public MorebiRoundedSquareTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MorebiRoundedSquareTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int max = Math.max(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(max, max);
    }
}
