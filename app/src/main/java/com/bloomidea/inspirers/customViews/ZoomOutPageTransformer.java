package com.bloomidea.inspirers.customViews;

import android.util.Log;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by michellobato on 24/04/17.
 */

public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.95f;
    private static final float MIN_ALPHA = 0.8f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        ViewPager parent = (ViewPager) view.getParent();
        position -= (parent.getPaddingRight()) / (float) pageWidth;

        Log.d("POS",""+position);

        //Your transformation with the new position.
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(MIN_ALPHA);

            view.setScaleX(MIN_SCALE);
            view.setScaleY(MIN_SCALE);
        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position*MIN_SCALE));
            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(MIN_ALPHA);

            view.setScaleX(MIN_SCALE);
            view.setScaleY(MIN_SCALE);
        }
    }
}
