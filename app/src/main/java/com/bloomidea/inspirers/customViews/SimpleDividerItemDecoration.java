package com.bloomidea.inspirers.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;

import java.util.ArrayList;

/**
 * Created by michellobato on 04/04/17.
 */

public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;
    private ArrayList<Integer> positionsNo = new ArrayList<>();

    public SimpleDividerItemDecoration(Context context) {
        mDivider = ContextCompat.getDrawable(context,R.drawable.line_divider);
    }

    public SimpleDividerItemDecoration(Context context, ArrayList<Integer> positionsNo) {
        mDivider = ContextCompat.getDrawable(context,R.drawable.line_divider);
        this.positionsNo = positionsNo;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            if(positionsNo!=null && !positionsNo.contains(new Integer(parent.getChildAdapterPosition(child)))) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}