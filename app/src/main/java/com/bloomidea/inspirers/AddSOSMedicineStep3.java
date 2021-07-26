package com.bloomidea.inspirers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bloomidea.inspirers.customViews.RangeTimePickerDialog;
import com.bloomidea.inspirers.model.MedicineType;
import com.bloomidea.inspirers.utils.MedicineTypeAux;
import com.bloomidea.inspirers.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class AddSOSMedicineStep3 extends Fragment {
    private static final String ARG_MEDICINE_TYPE = "ARG_MEDICINE_TYPE";

    private View rootView;
    private MedicineType medicineType;

    private OnFragmentInteractionListener mListener;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private GregorianCalendar dateSelected = new GregorianCalendar();

    public AddSOSMedicineStep3() {
    }

    public static AddSOSMedicineStep3 newInstance(MedicineType medicineType) {
        AddSOSMedicineStep3 fragment = new AddSOSMedicineStep3();

        Bundle args = new Bundle();
        args.putSerializable(ARG_MEDICINE_TYPE, medicineType);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            medicineType = (MedicineType) getArguments().getSerializable(ARG_MEDICINE_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_add_sosmedicine_step3, container, false);

            rootView.findViewById(R.id.sos_date_box).setOnClickListener(new View.OnClickListener() {
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


            rootView.findViewById(R.id.sos_time_box).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*TimePickerDialog datePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                            dateSelected.set(Calendar.HOUR_OF_DAY,hour);
                            dateSelected.set(Calendar.MINUTE,minute);
                            dateSelected.set(Calendar.SECOND,0);
                            dateSelected.set(Calendar.MILLISECOND,0);

                            setTimeText();
                        }
                    }, dateSelected.get(Calendar.HOUR_OF_DAY), dateSelected.get(Calendar.MINUTE), true);

                    datePickerDialog.show();
                    */
                    //Calendar mcurrentTime = Calendar.getInstance();
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

            rootView.findViewById(R.id.sos_dosage_editText).setTag(R.id.tag_posology_total,0);
            ((EditText) rootView.findViewById(R.id.sos_dosage_editText)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                            ((EditText) view).setText(getResources().getQuantityString(MedicineTypeAux.getTextResourceForTotal(medicineType.getCode()),totalInt,totalInt));
                        }else{
                            ((EditText)view).setText("");
                        }
                    }
                }
            });

            rootView.findViewById(R.id.proceed_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rootView.findViewById(R.id.sos_dosage_editText).clearFocus();

                    int totalAux = (int) rootView.findViewById(R.id.sos_dosage_editText).getTag(R.id.tag_posology_total);

                    if(totalAux>0){
                        mListener.step3SOSEnd(dateSelected,totalAux);
                    }else{
                        Toast.makeText(getActivity(),R.string.medicine_empty_error,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        setDayText();
        setTimeText();

        return rootView;
    }

    private void setDayText() {
        ((TextView) rootView.findViewById(R.id.sos_date_textView)).setText(dateFormat.format(dateSelected.getTime()));
    }

    private void setTimeText() {
        ((TextView) rootView.findViewById(R.id.sos_time_textView)).setText(timeFormat.format(dateSelected.getTime()));
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
        void step3SOSEnd(GregorianCalendar when, int dosage);
    }
}
