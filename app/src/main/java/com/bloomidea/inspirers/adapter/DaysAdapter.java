package com.bloomidea.inspirers.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.model.Days;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.ViewHolderTrips>{
    private Context context;
    private ArrayList<Days> listDays;
    private DaysAdapterListener mListener;


    public DaysAdapter(Context context, ArrayList<Days> trips, DaysAdapterListener mListener) {
        this.context = context;
        this.listDays = trips;
        this.mListener = mListener;
    }

    @Override
    public ViewHolderTrips onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_day, parent, false);

        ViewHolderTrips viewHolder = new ViewHolderTrips(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolderTrips holder, int position) {
        Days item = listDays.get(position);

        holder.dayName.setText(item.getName());

        if (item.isSelected()){
            holder.imageSelected.setImageResource(R.drawable.asset26);
        }else{
            holder.imageSelected.setImageResource(R.drawable.asset24);
        }

    }

    @Override
    public int getItemCount() {
        return listDays.size();
    }

    public boolean isEmpty() {
        return listDays.isEmpty();
    }

    public class ViewHolderTrips extends RecyclerView.ViewHolder{
        private ImageView imageSelected;
        private TextView dayName;

        public ViewHolderTrips(View v){
            super(v);

            imageSelected = v.findViewById(R.id.imageView);
            dayName = v.findViewById(R.id.title_textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDaysClick(listDays.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface DaysAdapterListener{
        void onDaysClick(Days day);
    }
}
