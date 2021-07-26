package com.bloomidea.inspirers;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.bloomidea.inspirers.model.MedicineType;
import com.bloomidea.inspirers.model.UserNormalMedicine;
import com.bloomidea.inspirers.utils.MedicineTypeAux;


public class AddMedicineStep2 extends Fragment {
    private static final String ARG_MEDICINE_NAME = "ARG_MEDICINE_NAME";
    private static final String ARG_MEDICINE_TYPE = "ARG_MEDICINE_TYPE";
    private static final String ARG_IS_SOS = "ARG_IS_SOS";
    private static final String ARG_MEDICINE_TO_EDIT = "ARG_MEDICINE_TO_EDIT";

    private String medicineName;
    private MedicineType medicineType;
    private boolean isSOS;

    private View rootView;

    private OnFragmentInteractionListener mListener;


    private UserNormalMedicine medicineToEdit = null;

    public AddMedicineStep2() {
    }

    public static AddMedicineStep2 newInstance(String medicineName, MedicineType medicineType, boolean isSOS, UserNormalMedicine medicineToEdit) {
        AddMedicineStep2 fragment = new AddMedicineStep2();
        Bundle args = new Bundle();
        args.putString(ARG_MEDICINE_NAME, medicineName);
        args.putSerializable(ARG_MEDICINE_TYPE, medicineType);
        args.putBoolean(ARG_IS_SOS, isSOS);
        args.putSerializable(ARG_MEDICINE_TO_EDIT, medicineToEdit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            medicineName = getArguments().getString(ARG_MEDICINE_NAME);
            medicineType = (MedicineType) getArguments().getSerializable(ARG_MEDICINE_TYPE);
            isSOS = getArguments().getBoolean(ARG_IS_SOS);
            medicineToEdit = (UserNormalMedicine) getArguments().getSerializable(ARG_MEDICINE_TO_EDIT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(R.layout.fragment_add_medicine_step2, container, false);

            if(isSOS){
                rootView.findViewById(R.id.share_doctor_box).setVisibility(View.GONE);
            }

            loadMedicineInfo();


            rootView.findViewById(R.id.proceed_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.step2End(((SwitchCompat) rootView.findViewById(R.id.share_doctor_switch)).isChecked());
                }
            });


            rootView.findViewById(R.id.change_medicine_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onGoBack();
                }
            });

            if(medicineToEdit!=null){
                ((SwitchCompat) rootView.findViewById(R.id.share_doctor_switch)).setChecked(medicineToEdit.isShareWithDoctor());
            }
        }

        return rootView;
    }

    private void loadMedicineInfo() {
        ((ImageView) rootView.findViewById(R.id.medicine_type_icon_imageView)).setImageResource(MedicineTypeAux.getMedicineTypeIcon(medicineType.getCode()));
        ((TextView) rootView.findViewById(R.id.medicine_name_textView)).setText(medicineName);
        ((TextView) rootView.findViewById(R.id.medicine_type_name_textView)).setText(medicineType.getName());
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

    public void updateMedicine(String medicineName, MedicineType medicineType) {
        this.medicineName = medicineName;
        this.medicineType = medicineType;

        loadMedicineInfo();
    }

    public interface OnFragmentInteractionListener {
        void step2End(boolean shareDoctor);
        void onGoBack();
    }
}
