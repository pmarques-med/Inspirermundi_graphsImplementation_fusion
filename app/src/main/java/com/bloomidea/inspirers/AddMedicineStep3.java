package com.bloomidea.inspirers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bloomidea.inspirers.model.UserNormalMedicine;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class AddMedicineStep3 extends Fragment {
    public static String ARG_MEDICINE_TO_EDIT = "ARG_MEDICINE_TO_EDIT";

    private View rootView;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private GregorianCalendar dateSelected = null;

    private OnFragmentInteractionListener mListener;

    private UserNormalMedicine medicineToEdit;

    public AddMedicineStep3() {
    }

    public static AddMedicineStep3 newInstance(UserNormalMedicine medicineToEdit) {
        AddMedicineStep3 fragment = new AddMedicineStep3();
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
            rootView = inflater.inflate(R.layout.fragment_add_medicine_step3, container, false);

            rootView.findViewById(R.id.medicine_start_date_box).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GregorianCalendar dateAux = dateSelected;
                    if(dateAux == null)
                        dateAux = new GregorianCalendar();

                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                            dateSelected = new GregorianCalendar(year,monthOfYear,dayOfMonth);
                            dateSelected.set(Calendar.HOUR_OF_DAY,0);
                            dateSelected.set(Calendar.MINUTE,0);
                            dateSelected.set(Calendar.SECOND,0);
                            dateSelected.set(Calendar.MILLISECOND,0);

                            ((TextView) rootView.findViewById(R.id.medicine_start_date_textView)).setText(dateFormat.format(dateSelected.getTime()));
                        }
                    }, dateAux.get(Calendar.YEAR), dateAux.get(Calendar.MONTH), dateAux.get(Calendar.DAY_OF_MONTH));

                    GregorianCalendar minDate = new GregorianCalendar();
                    //minDate.add(Calendar.DAY_OF_MONTH,1);
                    minDate.set(Calendar.HOUR_OF_DAY,0);
                    minDate.set(Calendar.MINUTE,0);
                    minDate.set(Calendar.SECOND,0);
                    minDate.set(Calendar.MILLISECOND,0);

                    datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

                    datePickerDialog.show();
                }
            });

            rootView.findViewById(R.id.proceed_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String totalDays = ((EditText) rootView.findViewById(R.id.medicine_total_days_editText)).getText().toString();

                    if (checkInfo(totalDays, dateSelected)) {
                        int totalDaysInt = 0;

                        try {
                            totalDaysInt = Integer.parseInt(totalDays);
                        } catch (NumberFormatException e) {
                            Log.d("Exception", e.toString());
                        }

                        mListener.step3End(dateSelected, totalDaysInt);
                    }
                }
            });

            if(medicineToEdit!=null){
                ((EditText) rootView.findViewById(R.id.medicine_total_days_editText)).setText("" + medicineToEdit.getDuration());
                dateSelected = medicineToEdit.getStartDate();
                /*
                if(medicineToEdit.getStartDate().before(new GregorianCalendar())) {
                    dateSelected = new GregorianCalendar();
                    dateSelected.set(Calendar.HOUR_OF_DAY,0);
                    dateSelected.set(Calendar.MINUTE,0);
                    dateSelected.set(Calendar.SECOND,0);
                    dateSelected.set(Calendar.MILLISECOND,0);
                    dateSelected.add(Calendar.DAY_OF_MONTH,1);
                }else{

                }*/

                ((TextView) rootView.findViewById(R.id.medicine_start_date_textView)).setText(dateFormat.format(dateSelected.getTime()));
            }
        }

        return rootView;
    }

    private boolean checkInfo(String totalDays, GregorianCalendar dateSelected) {
        boolean ok = false;

        if(dateSelected==null){
            Toast.makeText(getActivity(),R.string.medicine_empty_error,Toast.LENGTH_SHORT).show();
        }else if(totalDays == null || totalDays.isEmpty()){
            Toast.makeText(getActivity(),R.string.medicine_empty_error,Toast.LENGTH_SHORT).show();
        }else {
            int total = -1;
            try{
                total = Integer.parseInt(totalDays);
            }catch (Exception e){

            }

            if(total==-1 || total>366){
                Toast.makeText(getActivity(),R.string.medicine_days_error,Toast.LENGTH_SHORT).show();
            } else {
                ok = true;
            }
        }

        return ok;
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
        void step3End(GregorianCalendar startDate,int totalDays);
    }
}
