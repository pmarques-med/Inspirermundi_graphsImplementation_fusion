package com.bloomidea.inspirers.customViews;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;

/**
 * Created by michellobato on 23/03/17.
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int spaceTop;
    private int spaceBottom;

    public SpacesItemDecoration(int spaceTop, int spaceBottom) {
        this.spaceTop = spaceTop;
        this.spaceBottom = spaceBottom;
    }

    public int getSpaceTop() {
        return spaceTop;
    }

    public void setSpaceTop(int spaceTop) {
        this.spaceTop = spaceTop;
    }

    public int getSpaceBottom() {
        return spaceBottom;
    }

    public void setSpaceBottom(int spaceBottom) {
        this.spaceBottom = spaceBottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view) == 0){
            outRect.top = Math.abs(spaceTop);
        }

        if(parent.getChildAdapterPosition(view) == (parent.getAdapter().getItemCount()-1)){
            boolean put = true;
            Object aux = view.getTag(R.id.tag_is_active);

            if(aux !=null){
                put = !(boolean) aux;
            }

            if(put) {
                outRect.bottom = spaceBottom;
            }
        }
    }
}