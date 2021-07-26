package com.bloomidea.inspirers.customViews;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by michellobato on 15/02/17.
 */

public class ProgressBar extends android.widget.ProgressBar {

    public ProgressBar(Context context) {
        super(context);
    }

    public ProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setVisibility(int v) {
        super.setVisibility(v);
        if(v == GONE) {
            this.setIndeterminate(false);
        }
    }
}