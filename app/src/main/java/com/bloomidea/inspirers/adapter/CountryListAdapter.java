package com.bloomidea.inspirers.adapter;

import android.content.Context;
//import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.model.Country;
import com.bloomidea.inspirers.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michellobato on 19/04/17.
 */

public class CountryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionIndexer {
    private ArrayList<Country> list;
    private Context context;
    private CountryListAdapterListener listener;
    private ArrayList<Integer> mSectionPositions;

    public CountryListAdapter(ArrayList<Country> list, Context context, CountryListAdapterListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_country, parent, false);

        RecyclerView.ViewHolder viewHolder = new ViewHolderCountry(view);;

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolderCountry auxHolder = (ViewHolderCountry) holder;
        Country item = list.get(position);

        Utils.loadImageView(context, auxHolder.country_flag_imageView, null,item.getFlag(), null, R.drawable.no_photo, null);

        auxHolder.country_name_textView.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>(26);
        mSectionPositions = new ArrayList<>(26);
        for (int i = 0, size = list.size(); i < size; i++) {
            String section = String.valueOf(list.get(i).getName().charAt(0)).toUpperCase();
            if (!sections.contains(section)) {
                sections.add(section);
                mSectionPositions.add(i);
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    private class ViewHolderCountry extends RecyclerView.ViewHolder {
        public ImageView country_flag_imageView;
        public TextView country_name_textView;

        public ViewHolderCountry(View v) {
            super(v);

            country_flag_imageView = (ImageView) v.findViewById(R.id.country_flag_imageView);
            country_name_textView = (TextView) v.findViewById(R.id.country_name_textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.countrySelected(getAdapterPosition());
                }
            });
        }
    }

    public interface CountryListAdapterListener{
        void countrySelected(int selectedCountryPos);
    }
}
