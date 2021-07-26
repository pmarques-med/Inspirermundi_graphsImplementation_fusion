package com.bloomidea.inspirers.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;

import java.util.ArrayList;

/**
 * Created by michellobato on 19/04/17.
 */

public class StringProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;

    private ArrayList<String> items;

    private OnRemoveListener onRemoveListener;

    public StringProfileAdapter(Context context, ArrayList<String> items, OnRemoveListener onRemoveListener) {
        this.context = context;
        this.items = items;
        this.onRemoveListener = onRemoveListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_string_with_delete, parent, false);

        return new ViewHolderString(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolderString auxHolder = (ViewHolderString) holder;

        auxHolder.string_text.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items!=null ? items.size():0;
    }

    public void setItems(ArrayList<String> itemsList) {
        this.items = itemsList;

        notifyDataSetChanged();
    }

    public class ViewHolderString extends RecyclerView.ViewHolder{
        public View delete_btn;
        public TextView string_text;

        public ViewHolderString(View v) {
            super(v);

            delete_btn = v.findViewById(R.id.delete_btn);
            string_text = (TextView) v.findViewById(R.id.string_text);
            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRemoveListener != null && getAdapterPosition() != -1) {
                        onRemoveListener.onItemRemoved(getAdapterPosition());
                    }
                }
            });
        }
    }

    public static interface OnRemoveListener {
        void onItemRemoved(int position);
    }
}
