package com.bloomidea.inspirers;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;

import com.bloomidea.inspirers.model.HealthServices;
import com.bloomidea.inspirers.utils.HealthServicesAux;


public class AddSOSMedicineStep4 extends Fragment {
    private View rootView;

    private OnFragmentInteractionListener mListener;

    private int selectedHealthServicePos;

    private boolean userSelectedSeverity = false;

    public AddSOSMedicineStep4() {
    }

    public static AddSOSMedicineStep4 newInstance() {
        AddSOSMedicineStep4 fragment = new AddSOSMedicineStep4();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(R.layout.fragment_add_sosmedicine_step4, container, false);

            selectedHealthServicePos = -1;

            rootView.findViewById(R.id.sos_need_doctor_box).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());

                    adb.setTitle(R.string.sos_need_doctor_title);
                    adb.setSingleChoiceItems(HealthServicesAux.getHealthServicesNames(getActivity()), selectedHealthServicePos, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setSelectedHelthServiceName(i);
                            dialogInterface.dismiss();
                        }
                    });

                    adb.setNegativeButton(getString(R.string.cancel), null);

                    adb.show();
                }
            });

            rootView.findViewById(R.id.end_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HealthServices auxHealthServices = selectedHealthServicePos != -1 ? HealthServicesAux.getHealthServices(getActivity()).get(selectedHealthServicePos) : null;
                    String trigger = ((EditText) rootView.findViewById(R.id.sos_trigger_editText)).getText().toString();

                    if (checkInfo(auxHealthServices, trigger, userSelectedSeverity)) {
                        int severity = ((SeekBar) rootView.findViewById(R.id.seekBar)).getProgress();
                        boolean shareDoctor = ((SwitchCompat) rootView.findViewById(R.id.sos_share_doctor_switch)).isChecked();
                        String obs = ((EditText) rootView.findViewById(R.id.sos_obs_editText)).getText().toString();

                        mListener.stepSOS4End(severity,trigger,obs,shareDoctor,auxHealthServices);
                    }
                }
            });

            ((SeekBar) rootView.findViewById(R.id.seekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if(b) {
                        userSelectedSeverity = true;
                        Drawable thumb = getResources().getDrawable( R.drawable.custom_seek_thumb );
                        ((SeekBar) rootView.findViewById(R.id.seekBar)).setThumb(thumb);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        return rootView;
    }

    private boolean checkInfo(HealthServices auxHealthServices, String trigger, boolean userSelectedSeverity) {
        boolean ok = false;

        if(auxHealthServices==null){
            Toast.makeText(getActivity(),R.string.medicine_empty_error,Toast.LENGTH_SHORT).show();
        }else if(!userSelectedSeverity){
            Toast.makeText(getActivity(),R.string.medicine_empty_error,Toast.LENGTH_SHORT).show();
        }/*else if(trigger == null || trigger.isEmpty()){
            Toast.makeText(getActivity(),R.string.medicine_empty_error,Toast.LENGTH_SHORT).show();
        }*/else {
            ok = true;
        }

        return ok;
    }

    private void setSelectedHelthServiceName(int posSelected){
        selectedHealthServicePos = posSelected;
        HealthServices aux = HealthServicesAux.getHealthServices(getActivity()).get(selectedHealthServicePos);

        ((TextView) rootView.findViewById(R.id.sos_need_doctor_textView)).setText(aux.getName());
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
        void stepSOS4End(int severity, String trigger, String obs, boolean shareWithDoctor, HealthServices healthServices);
    }
}
