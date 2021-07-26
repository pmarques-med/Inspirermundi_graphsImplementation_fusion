package com.bloomidea.inspirers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SectionIndexer;

import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michellobato on 19/04/17.
 */

public class LanguageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionIndexer {
    private CharSequence[] list;
    private boolean[] selectedPositions;
    private Context context;
    private LanguageListAdapterListener listener;
    private ArrayList<Integer> mSectionPositions;

    public LanguageListAdapter(CharSequence[] list,boolean[] selectedPositions,  Context context, LanguageListAdapterListener listener) {
        this.list = list;
        this.selectedPositions = selectedPositions;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_language, parent, false);

        RecyclerView.ViewHolder viewHolder = new ViewHolderLanguage(view);;

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolderLanguage auxHolder = (ViewHolderLanguage) holder;
        CharSequence item = list[position];

        auxHolder.language_checkbox.setOnCheckedChangeListener(null);
        auxHolder.language_checkbox.setText(item);
        auxHolder.language_checkbox.setChecked(selectedPositions[position]);
        auxHolder.language_checkbox.setTag(position);
        auxHolder.language_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos = (Integer) buttonView.getTag();

                selectedPositions[pos] = isChecked;

                listener.languageSelectionChanged(pos,isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>(26);
        mSectionPositions = new ArrayList<>(26);
        for (int i = 0, size = list.length; i < size; i++) {
            String section = String.valueOf(list[i].charAt(0)).toUpperCase();
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

    private class ViewHolderLanguage extends RecyclerView.ViewHolder {
        public CheckBox language_checkbox;

        public ViewHolderLanguage(View v) {
            super(v);

            language_checkbox = (CheckBox) v.findViewById(R.id.language_checkbox);
        }
    }

    public interface LanguageListAdapterListener{
        void languageSelectionChanged(int selectedLanguagePos, boolean selected);
    }
}
