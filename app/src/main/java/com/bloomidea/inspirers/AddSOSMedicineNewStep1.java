package com.bloomidea.inspirers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.adapter.MyActiveMedsAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.DividerItemDecoration;
import com.bloomidea.inspirers.model.UserMedicine;

import java.util.ArrayList;

public class AddSOSMedicineNewStep1 extends Fragment {
    private RecyclerView medsRecyclerView;
    private MyActiveMedsAdapter adapter;
    private AddSOSMedicineNewStep1.OnFragmentInteractionListener mListener;
    private View rootView;

    private ArrayList<UserMedicine> myMeds;


    private AlertDialog dialog;


    public AddSOSMedicineNewStep1() {
        // Required empty public constructor
    }

    public static AddSOSMedicineNewStep1 newInstance() {
        AddSOSMedicineNewStep1 fragment = new AddSOSMedicineNewStep1();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(rootView==null) {
            rootView = inflater.inflate(R.layout.fragment_add_sosmedicine_new_step1, container, false);

            medsRecyclerView = (RecyclerView) rootView.findViewById(R.id.meds_recyclerView);

            loadMyMedsList();

            adapter = new MyActiveMedsAdapter(getActivity(), myMeds, true, new MyActiveMedsAdapter.MyActiveMedsAdapterListener() {
                @Override
                public void medicineSelected(UserMedicine medicineToEdit) {
                    mListener.stepSOS1End(medicineToEdit);
                }
            });

            medsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.line_divider);
            medsRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable,0));

            medsRecyclerView.setAdapter(adapter);
        }

        return rootView;
    }

    private void loadMyMedsList() {
        myMeds = AppController.getmInstance().getMedicineDataSource().getUserNormalMedicinesAux(AppController.getmInstance().getActiveUser().getId());

        verifyInfo(myMeds.isEmpty());
    }

    private void verifyInfo(boolean empty) {
        if(empty){
            ((TextView) rootView.findViewById(R.id.no_info_textView)).setText(R.string.no_medicines);
            rootView.findViewById(R.id.no_info_layout).setVisibility(View.VISIBLE);
            medsRecyclerView.setVisibility(View.GONE);
            rootView.findViewById(R.id.layout_active_med).setVisibility(View.GONE);
        }else{
            rootView.findViewById(R.id.no_info_layout).setVisibility(View.GONE);
            medsRecyclerView.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.layout_active_med).setVisibility(View.VISIBLE);
        }
    }

    private void verifyInfoDisable(boolean empty) {
        if(empty){
            ((TextView) rootView.findViewById(R.id.no_info_textView)).setText(R.string.no_medicines);
            rootView.findViewById(R.id.no_info_layout).setVisibility(View.VISIBLE);
            medsRecyclerView.setVisibility(View.GONE);
        }else{
            rootView.findViewById(R.id.no_info_layout).setVisibility(View.GONE);
            medsRecyclerView.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void stepSOS1End(UserMedicine medicineSelected);
    }
}
