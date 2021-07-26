package com.bloomidea.inspirers.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.model.Inhaler;
import com.bloomidea.inspirers.model.UserMedicine;
import com.bloomidea.inspirers.utils.MedicineTypeAux;

import java.util.ArrayList;

/**
 * Created by michellobato on 26/10/2017.
 */

public class InhalerAdapter extends RecyclerView.Adapter<InhalerAdapter.ViewHolderInhaler>{
    private Activity context;
    private ArrayList<Inhaler> inhalerList;
    private InhalerAdapterListener listener;
    private boolean containsSelection = false;
    private boolean isMore;

    public InhalerAdapter(Activity context, ArrayList<Inhaler> inhalerList, boolean isMore, InhalerAdapterListener listener) {
        this.context = context;
        this.inhalerList = inhalerList;
        this.listener = listener;
        this.isMore = isMore;
    }

    @Override
    public InhalerAdapter.ViewHolderInhaler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medicine_inhaler, parent, false);

        InhalerAdapter.ViewHolderInhaler viewHolder = new ViewHolderInhaler(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InhalerAdapter.ViewHolderInhaler holder, int position) {
        Inhaler auxInhaler = inhalerList.get(position);

        String uri = "@drawable/"+auxInhaler.getImageUrl();  // where myresource (without the extension) is the file

        int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());

        Drawable res = context.getResources().getDrawable(imageResource);

        holder.inhalerImage.setImageDrawable(res);
        holder.inhalerName.setText(auxInhaler.getName());
        holder.viewContainer.setBackgroundColor(Color.parseColor(auxInhaler.getColor()));

        holder.inhalerName.setTextColor(Color.WHITE);
        if ((inhalerList.size() - 1) == position && isMore){
            holder.inhalerName.setTextColor(Color.parseColor("#333333"));
        }

        if (auxInhaler.isSelected()){
            holder.selectedView.setVisibility(View.VISIBLE);
            holder.overlayUnselected.setVisibility(View.GONE);
        }else if (containsSelection){
            holder.overlayUnselected.setVisibility(View.VISIBLE);
            holder.selectedView.setVisibility(View.GONE);
        }else{
            holder.overlayUnselected.setVisibility(View.GONE);
            holder.selectedView.setVisibility(View.GONE);
        }

        holder.itemView.setTag(R.id.tag_position,position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag(R.id.tag_position);
                listener.onClickInhaler(inhalerList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return inhalerList.size();
    }

    public void setNewInhalerList(ArrayList<Inhaler> newInhalerList) {
        this.inhalerList = newInhalerList;
        notifyDataSetChanged();
    }

    public void setContainsSelection(boolean contains){
        this.containsSelection = contains;
        notifyDataSetChanged();
    }

    public class ViewHolderInhaler extends RecyclerView.ViewHolder {
        public ImageView inhalerImage;
        public TextView inhalerName;
        public RelativeLayout viewContainer;
        public LinearLayout selectedView;
        public View overlayUnselected;

        public ViewHolderInhaler(View v) {
            super(v);

            inhalerImage = (ImageView) v.findViewById(R.id.inhalerImage);
            inhalerName = (TextView) v.findViewById(R.id.inhalerName);
            viewContainer = (RelativeLayout) v.findViewById(R.id.viewContainer);
            selectedView = (LinearLayout) v.findViewById(R.id.selectedInhaler);
            overlayUnselected = (View) v.findViewById(R.id.overlayUnselected);


        }
    }

    public interface InhalerAdapterListener{
        void onClickInhaler(Inhaler inhaler);
    }
}
