package com.bloomidea.inspirers;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.adapter.MyMedsAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.DividerItemDecoration;
import com.bloomidea.inspirers.events.MedicinesChangedEvent;
import com.bloomidea.inspirers.model.MedicineDays;
import com.bloomidea.inspirers.model.UserMedicine;
import com.bloomidea.inspirers.model.UserNormalMedicine;
import com.bloomidea.inspirers.utils.Utils;

import java.util.ArrayList;

public class MyMedsActivity extends AppCompatActivity {
    private RecyclerView medsRecyclerView;
    private RecyclerView disableMedsRecyclerView;
    private MyMedsAdapter adapter;
    private MyMedsAdapter disableAdapter;

    private ArrayList<UserMedicine> myMeds;
    private ArrayList<UserMedicine> myDisableMeds;

    private Boolean showActiveMeds = true;
    private Boolean showNotActiveMeds = true;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_meds);

        configTopMenu();

        medsRecyclerView = (RecyclerView) findViewById(R.id.meds_recyclerView);
        disableMedsRecyclerView = (RecyclerView) findViewById(R.id.notactive_meds_recyclerView);

        loadMyMedsList();

        adapter = new MyMedsAdapter(this, myMeds, true, new MyMedsAdapter.MyMedsAdapterListener() {
            @Override
            public void medicineExtra(UserMedicine medicineExtra) {
                Intent i = new Intent(MyMedsActivity.this, AddSOSMedicineActivity.class);

                i.putExtra(AddSOSMedicineActivity.EXTRA_MY_MEDS_EXTRA, medicineExtra);
                i.putExtra(AddSOSMedicineActivity.EXTRA_MY_MEDS_EXTRA_BOOL, true);

                Utils.openIntent(MyMedsActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
            }

            @Override
            public void medicineDelete(final UserMedicine medicineToDelete) {
                showDialogDisable(medicineToDelete, true);
            }

            @Override
            public void medicineEdit(UserMedicine medicineToEdit) {
                if(medicineToEdit instanceof UserNormalMedicine){
                    Intent i = new Intent(MyMedsActivity.this, AddMedicineActivity.class);
                    i.putExtra(AddMedicineActivity.EXTRA_MEDICINE_EDIT,medicineToEdit);

                    Utils.openIntent(MyMedsActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                }
            }
        });

        disableAdapter = new MyMedsAdapter(this, myDisableMeds, false, new MyMedsAdapter.MyMedsAdapterListener() {

            @Override
            public void medicineExtra(UserMedicine medicineExtra) {

            }

            @Override
            public void medicineDelete(final UserMedicine medicineToDelete) {
                showDialogDisable(medicineToDelete, false);
            }

            @Override
            public void medicineEdit(UserMedicine medicineToEdit) {
                if(medicineToEdit instanceof UserNormalMedicine){
                    Intent i = new Intent(MyMedsActivity.this, AddMedicineActivity.class);
                    i.putExtra(AddMedicineActivity.EXTRA_MEDICINE_EDIT,medicineToEdit);

                    Utils.openIntent(MyMedsActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
                }
            }
        });


        findViewById(R.id.layout_active_med).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showActiveMeds){
                    findViewById(R.id.meds_recyclerView).setVisibility(View.GONE);
                    findViewById(R.id.imageViewArrowActive).setRotation(180);
                }else{
                    findViewById(R.id.meds_recyclerView).setVisibility(View.VISIBLE);
                    findViewById(R.id.imageViewArrowActive).setRotation(0);
                }
                showActiveMeds = !showActiveMeds;
            }
        });

        findViewById(R.id.layout_not_active_med).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showNotActiveMeds){
                    findViewById(R.id.notactive_meds_recyclerView).setVisibility(View.GONE);
                    findViewById(R.id.imageViewArrowNotActive).setRotation(180);
                }else{
                    findViewById(R.id.notactive_meds_recyclerView).setVisibility(View.VISIBLE);
                    findViewById(R.id.imageViewArrowNotActive).setRotation(0);
                }
                showNotActiveMeds = !showNotActiveMeds;
            }
        });

        medsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.line_divider);
        medsRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable,0));

        medsRecyclerView.setAdapter(adapter);

        disableMedsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        disableMedsRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable,0));

        disableMedsRecyclerView.setAdapter(disableAdapter);
    }

    private void showDialogDisable(final UserMedicine medicineToDelete, final boolean isActive) {
        View view = LayoutInflater.from(MyMedsActivity.this).inflate(R.layout.overlay_disable_confirm, null);//criei esta view porque nao estava a conseguir ir buscar as refs com o dialog.findbyID tava a dar null
        AlertDialog.Builder builder = new AlertDialog.Builder(MyMedsActivity.this, R.style.AppCompatAlertDialogStyleNoWindow);
        builder.setView(view);

        if (isActive){
            ((TextView) view.findViewById(R.id.textview_disable)).setText(R.string.overlay_disable_text);
        }else{
            ((TextView) view.findViewById(R.id.textview_disable)).setText(R.string.overlay_active_text);
        }


        view.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMedicine(medicineToDelete, isActive);
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();

    }

    private void loadMyMedsList() {
        myMeds = AppController.getmInstance().getMedicineDataSource().getUserNormalMedicinesAux(AppController.getmInstance().getActiveUser().getId());

        myDisableMeds = AppController.getmInstance().getMedicineDataSource().getUserNormalMedicinesDisable(AppController.getmInstance().getActiveUser().getId());

        verifyInfo(myMeds.isEmpty());
        verifyInfoDisable(myDisableMeds.isEmpty());

        if (myMeds.isEmpty() && myDisableMeds.isEmpty()){
            ((TextView) findViewById(R.id.no_info_textView)).setText(R.string.no_medicines);
            findViewById(R.id.no_info_layout).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.no_info_layout).setVisibility(View.GONE);
        }
    }

    private void verifyInfo(boolean empty) {
        if(empty){
            medsRecyclerView.setVisibility(View.GONE);
            findViewById(R.id.layout_active_med).setVisibility(View.GONE);
        }else{
            medsRecyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.layout_active_med).setVisibility(View.VISIBLE);
        }
    }

    private void verifyInfoDisable(boolean empty) {
        if(empty){
            disableMedsRecyclerView.setVisibility(View.GONE);
            findViewById(R.id.layout_not_active_med).setVisibility(View.GONE);
        }else{
            disableMedsRecyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.layout_not_active_med).setVisibility(View.VISIBLE);
        }
    }

    private void deleteMedicine(final UserMedicine medicineToDelete, final boolean isActive){
        findViewById(R.id.deleting_linearlayout).setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            public void run() {
                if (isActive){
                    if(AppController.getmInstance().deleteMedicine(medicineToDelete)){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AppController.getmInstance().getMyBus().send(new MedicinesChangedEvent());

                                myDisableMeds.add(medicineToDelete);
                                disableAdapter.notifyDataSetChanged();
                                myMeds.remove(medicineToDelete);
                                adapter.notifyDataSetChanged();

                                findViewById(R.id.deleting_linearlayout).setVisibility(View.GONE);

                                verifyInfo(myMeds.isEmpty());
                                verifyInfoDisable(myDisableMeds.isEmpty());
                            }
                        });

                        AppController.getmInstance().forceSyncManual();
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.deleting_linearlayout).setVisibility(View.GONE);
                                Toast.makeText(MyMedsActivity.this, R.string.error_delete_medicine, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else{
                    if(AppController.getmInstance().activateMedicine(medicineToDelete)){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AppController.getmInstance().getMyBus().send(new MedicinesChangedEvent());

                                myMeds.add(medicineToDelete);
                                adapter.notifyDataSetChanged();
                                myDisableMeds.remove(medicineToDelete);
                                disableAdapter.notifyDataSetChanged();

                                findViewById(R.id.deleting_linearlayout).setVisibility(View.GONE);

                                verifyInfo(myMeds.isEmpty());
                                verifyInfoDisable(myDisableMeds.isEmpty());
                            }
                        });

                        AppController.getmInstance().forceSyncManual();
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.deleting_linearlayout).setVisibility(View.GONE);
                                Toast.makeText(MyMedsActivity.this, R.string.error_delete_medicine, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }


            }
        }).start();
    }

    private void configTopMenu() {
        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ((TextView) findViewById(R.id.title_textView)).setText(R.string.my_meds);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadMyMedsList();
        adapter.setNewMedsList(myMeds);
        disableAdapter.setNewMedsList(myDisableMeds);
    }
}
