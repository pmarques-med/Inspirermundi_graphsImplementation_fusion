package com.bloomidea.inspirers.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.model.MedicineSchedule;
import com.bloomidea.inspirers.model.UserMedicine;
import com.bloomidea.inspirers.model.UserNormalMedicine;
import com.bloomidea.inspirers.utils.MedicineTypeAux;

import java.util.ArrayList;

/**
 * Created by michellobato on 26/10/2017.
 */

public class MyActiveMedsAdapter extends RecyclerView.Adapter<MyActiveMedsAdapter.ViewHolderMyMeds>{
    private Activity context;
    private ArrayList<UserMedicine> auxListMeds;
    private MyActiveMedsAdapterListener listener;
    private boolean isActive;

    public MyActiveMedsAdapter(Activity context, ArrayList<UserMedicine> auxListMeds, boolean isActive, MyActiveMedsAdapterListener listener) {
        this.context = context;
        this.auxListMeds = auxListMeds;
        this.listener = listener;
        this.isActive = isActive;
    }

    @Override
    public MyActiveMedsAdapter.ViewHolderMyMeds onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_medicine, parent, false);

        MyActiveMedsAdapter.ViewHolderMyMeds viewHolder = new ViewHolderMyMeds(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyActiveMedsAdapter.ViewHolderMyMeds holder, int position) {
        final UserMedicine userMed = auxListMeds.get(position);

        holder.medicine_imageView.setImageResource(MedicineTypeAux.getMedicineTypeIcon(userMed.getMedicineType().getCode()));
        holder.medicine_text_textView.setText(userMed.getMedicineName());

        holder.medicine_imageView.setBackgroundResource(R.drawable.medicine_green_back);

        holder.menu_med.setVisibility(View.GONE);

        if (userMed instanceof UserNormalMedicine) {
            UserNormalMedicine auxMed = (UserNormalMedicine) userMed;

            for (MedicineSchedule schedule : auxMed.getSchedules()){
                holder.medicine_text_timedays.setText(schedule.getSelection().getDesc() + " | " + schedule.getDays().getDayDescByCode(schedule.getDays().getSelectedOption()));
            }

            String auxstring = AppController.getmInstance().getString(R.string.new_step_num_days) + " " + auxMed.getDuration();
            String auxcont = AppController.getmInstance().getString(R.string.new_step_continuous);
            holder.medicine_text_type.setText(auxMed.getDuration() == 0 ? auxcont : auxstring);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.medicineSelected(userMed);
            }
        });

//        if(userMed instanceof UserSOSMedicine){
//            holder.edit_medicine.setVisibility(View.GONE);
//            Utils.changeBtnBackgroundMedicine(holder.medicine_imageView, R.drawable.item_medicine_sos_btn_back);
//        }else{
//            holder.edit_medicine.setVisibility(View.VISIBLE);
//            Utils.changeBtnBackgroundMedicine(holder.medicine_imageView, R.drawable.item_medicine_normal_btn_back_green);
//        }
    }

    @Override
    public int getItemCount() {
        return auxListMeds.size();
    }

    public void setNewMedsList(ArrayList<UserMedicine> newMedsList) {
        this.auxListMeds = newMedsList;
        notifyDataSetChanged();
    }

    public class ViewHolderMyMeds extends RecyclerView.ViewHolder {
        public ImageView medicine_imageView;
        public TextView medicine_text_textView;
        public TextView medicine_text_timedays;
        public TextView medicine_text_type;
        public ImageView menu_med;

        //public View edit_medicine;
        //public View delete_medicine;
        public View showMenu;

        public ViewHolderMyMeds(View v) {
            super(v);

            medicine_text_type = (TextView) v.findViewById(R.id.medicine_text_type);
            medicine_text_timedays = (TextView) v.findViewById(R.id.medicine_text_timedays);
            medicine_imageView = (ImageView) v.findViewById(R.id.medicine_imageView);
            medicine_text_textView = (TextView) v.findViewById(R.id.medicine_text_textView);
            menu_med = (ImageView) v.findViewById(R.id.menu_med);

        }
    }

    public interface MyActiveMedsAdapterListener{
        void medicineSelected(UserMedicine medicineToEdit);
    }
}
