package com.bloomidea.inspirers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bloomidea.inspirers.adapter.MyActiveMedsAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.DividerItemDecoration;
import com.bloomidea.inspirers.customViews.RangeTimePickerDialog;
import com.bloomidea.inspirers.model.FaseTime;
import com.bloomidea.inspirers.model.MedicineType;
import com.bloomidea.inspirers.model.UserMedicine;
import com.bloomidea.inspirers.utils.MedicineTypeAux;
import com.bloomidea.inspirers.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddSOSMedicineNewStep2 extends Fragment {
    private static final String ARG_MEDICINE_SELECTED = "ARG_MEDICINE_SELECTED";

    private AddSOSMedicineNewStep2.OnFragmentInteractionListener mListener;
    private View rootView;

    private UserMedicine selectedMedicine;

    private GregorianCalendar dateSelected = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private int totalSOSDosages = 0;

    android.content.res.Resources res;

    public AddSOSMedicineNewStep2() {
        // Required empty public constructor
    }

    public static AddSOSMedicineNewStep2 newInstance(UserMedicine medicine) {
        AddSOSMedicineNewStep2 fragment = new AddSOSMedicineNewStep2();

        Bundle args = new Bundle();
        args.putSerializable(ARG_MEDICINE_SELECTED, medicine);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            selectedMedicine = (UserMedicine) getArguments().getSerializable(ARG_MEDICINE_SELECTED);
        }

        res = getResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(rootView==null) {
            rootView = inflater.inflate(R.layout.fragment_add_sosmedicine_new_step2, container, false);

            loadMedicineInfo();

            dateSelected = new GregorianCalendar();
            ((TextView) rootView.findViewById(R.id.med_sos_start_date)).setText(dateFormat.format(dateSelected.getTime()));

            rootView.findViewById(R.id.med_sos_start_date).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                            dateSelected = new GregorianCalendar(year,monthOfYear,dayOfMonth,dateSelected.get(Calendar.HOUR_OF_DAY),dateSelected.get(Calendar.MINUTE));

                            setDayText();
                        }
                    }, dateSelected.get(Calendar.YEAR), dateSelected.get(Calendar.MONTH), dateSelected.get(Calendar.DAY_OF_MONTH));

                    GregorianCalendar maxDate = new GregorianCalendar();
                    maxDate.set(Calendar.HOUR_OF_DAY,0);
                    maxDate.set(Calendar.MINUTE,0);
                    maxDate.set(Calendar.SECOND,0);
                    maxDate.set(Calendar.MILLISECOND,0);

                    datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

                    datePickerDialog.show();
                }
            });

            ((TextView) rootView.findViewById(R.id.time_dosage_textView_sos)).setText(timeFormat.format(dateSelected.getTime()));

            rootView.findViewById(R.id.time_dosage_textView_sos).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int hour = dateSelected.get(Calendar.HOUR_OF_DAY);
                    int minute = dateSelected.get(Calendar.MINUTE);
                    RangeTimePickerDialog mTimePicker;
                    mTimePicker = new RangeTimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            dateSelected.set(Calendar.HOUR_OF_DAY,selectedHour);
                            dateSelected.set(Calendar.MINUTE,selectedMinute);
                            dateSelected.set(Calendar.SECOND,0);
                            dateSelected.set(Calendar.MILLISECOND,0);

                            setTimeText();
                        }
                    }, hour, minute, true);//Yes 24 hour time

                    if(Utils.isToday(dateSelected)) {
                        mTimePicker.setMax(hour, minute);
                    }

                    mTimePicker.show();
                }
            });

            ((EditText) rootView.findViewById(R.id.medicine_posology_editText_sos)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b){
                        int total = (int) view.getTag(R.id.tag_posology_total);
                        if(total>0){
                            ((EditText)view).setText(""+total);
                        }else{
                            ((EditText)view).setText("");
                        }
                    }else{
                        String totalAux = ((EditText) view).getText().toString();
                        int totalInt = 0;

                        if(!totalAux.isEmpty()){
                            try {
                                totalInt = Integer.parseInt(totalAux);
                            } catch (NumberFormatException e) {
                                Log.d("Exception", e.toString());
                            }
                        }

                        view.setTag(R.id.tag_posology_total, totalInt);

                        if(totalInt>0){
                            ((EditText) view).setText(getResources().getQuantityString(MedicineTypeAux.getTextResourceForTotal(selectedMedicine.getMedicineType().getCode()),totalInt,totalInt));
                        }else{
                            ((EditText)view).setText("");
                        }
                    }
                }
            });

            rootView.findViewById(R.id.proceed_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int totalAux = (int) rootView.findViewById(R.id.medicine_posology_editText_sos).getTag(R.id.tag_posology_total);

                    String notes = ((TextView) rootView.findViewById(R.id.med_sos_notes)).getText().toString();

                    if (totalAux != 0){
                        mListener.stepSOS2End(selectedMedicine, dateSelected, totalAux, notes);
                    }else{
                        Toast.makeText(getActivity(),R.string.medicine_empty_error,Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        return rootView;
    }

    private void setDayText() {
        ((TextView) rootView.findViewById(R.id.med_sos_start_date)).setText(dateFormat.format(dateSelected.getTime()));
    }

    private void setTimeText() {
        ((TextView) rootView.findViewById(R.id.time_dosage_textView_sos)).setText(timeFormat.format(dateSelected.getTime()));
    }


    private void loadMedicineInfo() {
        ((ImageView) rootView.findViewById(R.id.medicine_type_icon_imageView)).setImageResource(MedicineTypeAux.getMedicineTypeIcon(selectedMedicine.getMedicineType().getCode()));
        ((TextView) rootView.findViewById(R.id.medicine_name_textView)).setText(selectedMedicine.getMedicineName());

        ((EditText) rootView.findViewById(R.id.medicine_posology_editText_sos)).setTag(R.id.tag_posology_total, selectedMedicine.getTotalSOSDosages());
        if (selectedMedicine.getTotalSOSDosages() != 0){
            String aux = res.getQuantityString(MedicineTypeAux.getTextResourceForTotal(selectedMedicine.getMedicineType().getCode()),selectedMedicine.getTotalSOSDosages(),selectedMedicine.getTotalSOSDosages());
            ((EditText) rootView.findViewById(R.id.medicine_posology_editText_sos)).setText(aux);
        }else{
            ((EditText) rootView.findViewById(R.id.medicine_posology_editText_sos)).setText("");
        }
    }

    public void updateMedicine(UserMedicine medicine) {
        this.selectedMedicine = medicine;
        loadMedicineInfo();
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
        void stepSOS2End(UserMedicine selectedMed, GregorianCalendar dateTimeSelected, int totalDoses, String notes);
    }

}
