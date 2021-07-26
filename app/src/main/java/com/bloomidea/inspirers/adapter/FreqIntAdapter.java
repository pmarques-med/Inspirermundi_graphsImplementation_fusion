package com.bloomidea.inspirers.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.model.ScheduleAux;

import java.util.ArrayList;

public class FreqIntAdapter extends RecyclerView.Adapter<FreqIntAdapter.ViewHolderRecomm>{
    private Context context;
    private ArrayList<ScheduleAux> listDesc;
    private FreqIntAdapterListener mListener;

    public FreqIntAdapter(Context context, ArrayList<ScheduleAux> list, FreqIntAdapterListener mListener) {
        this.context = context;
        this.listDesc = list;
        this.mListener = mListener;
    }

    @Override
    public ViewHolderRecomm onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_option, parent, false);

        ViewHolderRecomm viewHolder = new ViewHolderRecomm(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolderRecomm holder, int position) {

        holder.textDesc.setText(listDesc.get(position).getDesc());

    }

    @Override
    public int getItemCount() {
        return listDesc.size();
    }

    public boolean isEmpty() {
        return listDesc.isEmpty();
    }

    public class ViewHolderRecomm extends RecyclerView.ViewHolder{

        private TextView textDesc;

        public ViewHolderRecomm(View v){
            super(v);

            textDesc = v.findViewById(R.id.title_textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(listDesc.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface FreqIntAdapterListener{
        void onItemClick(ScheduleAux item);
    }

}
