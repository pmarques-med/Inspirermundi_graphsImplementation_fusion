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
import com.bloomidea.inspirers.model.UserSOSMedicine;
import com.bloomidea.inspirers.utils.MedicineTypeAux;
import com.bloomidea.inspirers.utils.Utils;

import java.util.ArrayList;

/**
 * Created by michellobato on 26/10/2017.
 */

public class MyMedsAdapter extends RecyclerView.Adapter<MyMedsAdapter.ViewHolderMyMeds>{
    private Activity context;
    private ArrayList<UserMedicine> auxListMeds;
    private MyMedsAdapterListener listener;
    private boolean isActive;

    public MyMedsAdapter(Activity context, ArrayList<UserMedicine> auxListMeds, boolean isActive, MyMedsAdapterListener listener) {
        this.context = context;
        this.auxListMeds = auxListMeds;
        this.listener = listener;
        this.isActive = isActive;
    }

    @Override
    public MyMedsAdapter.ViewHolderMyMeds onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_medicine, parent, false);

        MyMedsAdapter.ViewHolderMyMeds viewHolder = new ViewHolderMyMeds(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyMedsAdapter.ViewHolderMyMeds holder, int position) {
        UserMedicine userMed = auxListMeds.get(position);

        holder.medicine_imageView.setImageResource(MedicineTypeAux.getMedicineTypeIcon(userMed.getMedicineType().getCode()));
        holder.medicine_text_textView.setText(userMed.getMedicineName());

        if (isActive){
            holder.medicine_imageView.setBackgroundResource(R.drawable.medicine_green_back);
        }else{
            holder.medicine_imageView.setBackgroundResource(R.drawable.medicine_gray_back);
        }

        if (userMed instanceof UserNormalMedicine) {
            UserNormalMedicine auxMed = (UserNormalMedicine) userMed;

            for (MedicineSchedule schedule : auxMed.getSchedules()){
                holder.medicine_text_timedays.setText(schedule.getSelection().getDesc() + " | " + schedule.getDays().getDayDescByCode(schedule.getDays().getSelectedOption()));
            }

            String auxstring = AppController.getmInstance().getString(R.string.new_step_num_days) + " " + auxMed.getDuration();
            String auxcont = AppController.getmInstance().getString(R.string.new_step_continuous);
            holder.medicine_text_type.setText(auxMed.getDuration() == 0 ? auxcont : auxstring);
        }

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
        //public View edit_medicine;
        //public View delete_medicine;
        public View showMenu;

        public ViewHolderMyMeds(View v) {
            super(v);

            medicine_text_type = (TextView) v.findViewById(R.id.medicine_text_type);
            medicine_text_timedays = (TextView) v.findViewById(R.id.medicine_text_timedays);
            medicine_imageView = (ImageView) v.findViewById(R.id.medicine_imageView);
            medicine_text_textView = (TextView) v.findViewById(R.id.medicine_text_textView);
            showMenu = v.findViewById(R.id.menu_med);

            showMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, showMenu);
                    //inflating menu from xml resource

                    if (isActive){
                        popup.inflate(R.menu.options_menu);
                    }else{
                        popup.inflate(R.menu.options_menu_disable);
                    }

                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu1:
                                    //handle menu1 click
                                    listener.medicineExtra(auxListMeds.get(getAdapterPosition()));
                                    break;
                                case R.id.menu2:
                                    listener.medicineEdit(auxListMeds.get(getAdapterPosition()));
                                    break;
                                case R.id.menu3:
                                    listener.medicineDelete(auxListMeds.get(getAdapterPosition()));
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });
            //edit_medicine = v.findViewById(R.id.edit_medicine);
            //delete_medicine = v.findViewById(R.id.delete_medicine);

//            edit_medicine.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.medicineEdit(auxListMeds.get(getAdapterPosition()));
//                }
//            });
//
//
//            delete_medicine.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.medicineDelete(auxListMeds.get(getAdapterPosition()));
//                }
//            });


        }
    }

    public interface MyMedsAdapterListener{
        void medicineExtra(UserMedicine medicineExtra);
        void medicineDelete(UserMedicine medicineToDelete);
        void medicineEdit(UserMedicine medicineToEdit);
    }
}
