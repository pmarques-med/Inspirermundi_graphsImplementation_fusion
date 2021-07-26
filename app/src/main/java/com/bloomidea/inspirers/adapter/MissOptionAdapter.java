package com.bloomidea.inspirers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.model.Days;
import com.bloomidea.inspirers.model.MissOption;

import java.util.ArrayList;

public class MissOptionAdapter extends RecyclerView.Adapter<MissOptionAdapter.ViewHolderTrips>{
    private Context context;
    private ArrayList<MissOption> listOptions;
    private MissOptionAdapterListener mListener;


    public MissOptionAdapter(Context context, ArrayList<MissOption> options, MissOptionAdapterListener mListener) {
        this.context = context;
        this.listOptions = options;
        this.mListener = mListener;
    }

    @Override
    public ViewHolderTrips onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_miss_option, parent, false);

        ViewHolderTrips viewHolder = new ViewHolderTrips(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolderTrips holder, int position) {
        MissOption item = listOptions.get(position);

        holder.optionName.setText(item.getName());

        if (item.isSelected()){
            holder.imageSelected.setImageResource(R.drawable.asset31);
        }else{
            holder.imageSelected.setImageResource(R.drawable.asset30);
        }

    }

    @Override
    public int getItemCount() {
        return listOptions.size();
    }

    public boolean isEmpty() {
        return listOptions.isEmpty();
    }

    public class ViewHolderTrips extends RecyclerView.ViewHolder{
        private ImageView imageSelected;
        private TextView optionName;

        public ViewHolderTrips(View v){
            super(v);

            imageSelected = v.findViewById(R.id.imageView);
            optionName = v.findViewById(R.id.title_textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onOptionClick(listOptions.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface MissOptionAdapterListener{
        void onOptionClick(MissOption option);
    }
}
