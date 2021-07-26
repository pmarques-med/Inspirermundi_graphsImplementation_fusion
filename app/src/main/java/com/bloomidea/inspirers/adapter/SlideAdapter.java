package com.bloomidea.inspirers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.bloomidea.inspirers.R;

import java.util.ArrayList;

/**
 * Created by michellobato on 18/01/2018.
 */

public class SlideAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<String> mTextList;

    public SlideAdapter(Context context, ArrayList<String> list) {
        this.mContext = context;
        this.mTextList = list;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view =  inflater.inflate(mContext.getResources().getLayout(R.layout.item_slide), null);

        collection.addView(view);

        ((TextView) view.findViewById(R.id.textView)).setText(mTextList.get(position));
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return mTextList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

}