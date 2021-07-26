package com.bloomidea.inspirers;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bloomidea.inspirers.model.UserNormalMedicine;


public class AddMedicineStep5 extends Fragment {
    private static final String ARG_MEDICINE_TO_EDIT = "ARG_MEDICINE_TO_EDIT";

    private View rootView;
    private OnFragmentInteractionListener mListener;

    private UserNormalMedicine medicineToEdit;

    public AddMedicineStep5() {
    }

    public static AddMedicineStep5 newInstance(UserNormalMedicine medicineToEdit) {
        AddMedicineStep5 fragment = new AddMedicineStep5();

        Bundle args = new Bundle();
        args.putSerializable(ARG_MEDICINE_TO_EDIT, medicineToEdit);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            medicineToEdit = (UserNormalMedicine) getArguments().getSerializable(ARG_MEDICINE_TO_EDIT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView==null) {
            rootView = inflater.inflate(R.layout.fragment_add_medicine_step5, container, false);
            rootView.findViewById(R.id.end_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.step5End(getNoteText());
                }
            });

            if(medicineToEdit!=null){
                ((EditText)rootView.findViewById(R.id.medicine_note_editText)).setText(medicineToEdit.getNote());
            }
        }

        return rootView;
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

    public String getNoteText() {
        return ((EditText)rootView.findViewById(R.id.medicine_note_editText)).getText().toString();
    }

    public interface OnFragmentInteractionListener {
        void step5End(String comment);
    }
}
