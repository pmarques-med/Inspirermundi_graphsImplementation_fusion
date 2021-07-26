package com.bloomidea.inspirers.customViews;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by joaoquintao on 03/07/17.
 */

public class VerticalHorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int verticalSpaceHeight;
    private final boolean inverted;

    public VerticalHorizontalSpaceItemDecoration(int verticalSpaceHeight, boolean inverted) {
        this.verticalSpaceHeight = verticalSpaceHeight;
        this.inverted = inverted;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        //Log.d("POS",""+parent.getChildAdapterPosition(view));
        //if (parent.getChildAdapterPosition(view) != (inverted?0:parent.getAdapter().getItemCount() - 1)) {
        outRect.top = verticalSpaceHeight;
        outRect.bottom = verticalSpaceHeight;
        outRect.left = verticalSpaceHeight;
        outRect.right = verticalSpaceHeight;

        //if (parent.getChildAdapterPosition(view) != 0 && parent.getChildAdapterPosition(view) != 1 && parent.getChildAdapterPosition(view) != 2) {

        //    Log.d("POS",""+parent.getChildAdapterPosition(view));

        //}

        //if (((parent.getChildAdapterPosition(view) - 1)%3) == 0) {

        //    Log.d("POS",""+parent.getChildAdapterPosition(view));

        //}
    }
}