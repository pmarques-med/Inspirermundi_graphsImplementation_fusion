package com.bloomidea.inspirers.customViews;

import android.content.Context;
import android.util.AttributeSet;

import com.github.lzyzsd.circleprogress.DonutProgress;

/**
 * Created by michellobato on 03/04/17.
 */

public class SquareDonutProgress extends DonutProgress {
    public SquareDonutProgress(Context context) {
        super(context);
    }

    public SquareDonutProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareDonutProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
