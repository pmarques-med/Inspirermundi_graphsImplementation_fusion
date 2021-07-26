package com.bloomidea.inspirers;

import android.app.Activity;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bloomidea.inspirers.customViews.RangeTimePickerDialog;
import com.bloomidea.inspirers.model.FaseTime;
import com.bloomidea.inspirers.model.MedicineTime;
import com.bloomidea.inspirers.model.MedicineType;
import com.bloomidea.inspirers.model.UserNormalMedicine;
import com.bloomidea.inspirers.utils.MedicineTypeAux;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;


public class AddMedicineStep4 extends Fragment {
    private static final String ARG_MEDICINE_TYPE = "ARG_MEDICINE_TYPE";
    private static final String ARG_MEDICINE_TO_EDIT = "ARG_MEDICINE_TO_EDIT";
    private static final String ARG_START_DATE = "ARG_START_DATE";
    private static final String ARG_TOTAL_DAYS = "ARG_TOTAL_DAYS";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private View rootView;
    private MedicineType medicineType;

    private OnFragmentInteractionListener mListener;



    private HashMap<Integer, LinearLayout> listTimes = new HashMap<>();
    private int codeTagItems = 1;
    private int codeObjects = 1;
    private LinearLayout viewItems;


    private UserNormalMedicine medicineToEdit;

    private GregorianCalendar startDate;
    private int totalDays;
    private GregorianCalendar nowInsertDate;

    public AddMedicineStep4() {
    }

    public static AddMedicineStep4 newInstance(MedicineType medicineType, UserNormalMedicine medicineToEdit, GregorianCalendar startDate, int totalDays) {
        AddMedicineStep4 fragment = new AddMedicineStep4();

        Bundle args = new Bundle();
        args.putSerializable(ARG_MEDICINE_TYPE, medicineType);
        args.putSerializable(ARG_MEDICINE_TO_EDIT, medicineToEdit);
        args.putSerializable(ARG_START_DATE, startDate);
        args.putInt(ARG_TOTAL_DAYS, totalDays);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            medicineType = (MedicineType) getArguments().getSerializable(ARG_MEDICINE_TYPE);
            medicineToEdit = (UserNormalMedicine) getArguments().getSerializable(ARG_MEDICINE_TO_EDIT);
            startDate = (GregorianCalendar) getArguments().getSerializable(ARG_START_DATE);
            totalDays = getArguments().getInt(ARG_TOTAL_DAYS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView==null){
            nowInsertDate = new GregorianCalendar();
            nowInsertDate.add(Calendar.HOUR_OF_DAY, -1);

            rootView = inflater.inflate(R.layout.fragment_add_medicine_step4, container, false);
            viewItems = (LinearLayout) rootView.findViewById(R.id.times_list);

            rootView.findViewById(R.id.add_more_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createNewItemTime(null);
                    clearAllFocusTextView();
                }
            });

            rootView.findViewById(R.id.proceed_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearAllFocusTextView();

                    ArrayList<MedicineTime> listTimeAux = new ArrayList<MedicineTime>();

                    SortedSet<Integer> keys = new TreeSet<Integer>(listTimes.keySet());

                    boolean auxTime = false;
                    boolean auxPoso = false;

                    for(Integer key : keys){
                        Object aux = (listTimes.get(key).findViewWithTag("item_time_box_"+key)).getTag(R.id.tag_selected_fase_time);

                        if(aux!=null) {
                            FaseTime faseTime = (FaseTime) aux;

                            (listTimes.get(key).findViewWithTag("item_podology_" + key)).clearFocus();

                            int total = (int) (listTimes.get(key).findViewWithTag("item_podology_" + key)).getTag(R.id.tag_posology_total);

                            Log.d("ITEM", faseTime.getHourInitDesc() + "-" + total);

                            if(total>0) {
                                listTimeAux.add(new MedicineTime(faseTime, total));
                            }else{
                                auxPoso = true;
                            }
                        }else{
                            auxTime = true;
                        }
                    }

                    if(!listTimeAux.isEmpty()){
                        mListener.step4End(listTimeAux, nowInsertDate);
                    }else{
                        if(auxPoso){
                           Toast.makeText(getActivity(), R.string.one_posology_is_needed, Toast.LENGTH_SHORT).show();
                        }else if(auxTime){
                            //Toast.makeText(getActivity(), R.string.one_time_is_needed, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(), R.string.one_time_posology_are_nedded, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getActivity(), R.string.one_time_posology_are_nedded, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            /*if(medicineToEdit!=null){
                for(MedicineTime time : medicineToEdit.getTimes()){
                    createNewItemTime(time);
                }
            }else{
                createNewItemTime(null);
            }*/
        }

        return rootView;
    }

    private void clearAllFocusTextView(){
        for(Integer key : listTimes.keySet()){
            (listTimes.get(key).findViewWithTag("item_podology_" + key)).clearFocus();
        }
    }

    private void createNewItemTime(MedicineTime time){
        FaseTime auxSelectedFaseTime = null;
        int auxDosage = 0;

        if(time!=null){
            auxDosage = time.getDosage();
            auxSelectedFaseTime =time.getFaseTime();
        }


        LinearLayout viewAux = (LinearLayout) View.inflate(getActivity(),R.layout.item_posology, null);

        codeTagItems++;
        String tag = "item_"+codeTagItems;
        viewAux.setTag(tag);

        String tagTimeBox = "item_time_box_"+codeTagItems;
        String tagPodology = "item_podology_"+codeTagItems;
        String tagTime = "item_text_"+codeTagItems;
        String tagDelete = "item_delete_"+codeTagItems;

        viewAux.findViewById(R.id.medicine_time_box).setTag(tagTimeBox);
        viewAux.findViewById(R.id.medicine_time_box).setTag(R.id.tag_selected_fase_time,auxSelectedFaseTime);
        viewAux.findViewById(R.id.medicine_time_box).setTag(R.id.tag_textView,viewAux.findViewById(R.id.medicine_time_textView));

        if(auxSelectedFaseTime!=null) {
            ((TextView) viewAux.findViewById(R.id.medicine_time_textView)).setText(auxSelectedFaseTime.getDesc());
        }

        viewAux.findViewById(R.id.medicine_time_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                RangeTimePickerDialog mTimePicker;
                mTimePicker = new RangeTimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        FaseTime auxFaseTime = new FaseTime(selectedHour,selectedMinute);
                        view.setTag(R.id.tag_selected_fase_time,auxFaseTime);

                        ((TextView) view.getTag(R.id.tag_textView)).setText(auxFaseTime.getDesc());
                    }
                }, hour, minute, true);//Yes 24 hour time

                mTimePicker.setTitle(getString(R.string.medicine_day_fase_title));
                //mTimePicker.setMax(21,59);
                mTimePicker.setMax(23,59);

                if(dateFormat.format(startDate.getTime()).equals(dateFormat.format(nowInsertDate.getTime())) && totalDays == 1){
                    mTimePicker.setMin(nowInsertDate.get(Calendar.HOUR_OF_DAY),nowInsertDate.get(Calendar.MINUTE));
                }

                mTimePicker.show();
            }
        });
        viewAux.findViewById(codeObjects++);

        viewAux.findViewById(R.id.medicine_time_textView).setTag(tagTime);
        viewAux.findViewById(R.id.medicine_time_textView).setId(codeObjects++);


        viewAux.findViewById(R.id.medicine_posology_editText).setTag(tagPodology);
        viewAux.findViewById(R.id.medicine_posology_editText).setTag(R.id.tag_posology_total, auxDosage);

        if(auxDosage>0) {
            ((EditText) viewAux.findViewById(R.id.medicine_posology_editText)).setText(getResources().getQuantityString(MedicineTypeAux.getTextResourceForTotal(medicineType.getCode()), auxDosage, auxDosage));
        }

        ((EditText) viewAux.findViewById(R.id.medicine_posology_editText)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        viewAux.findViewById(R.id.medicine_posology_editText).setId(codeObjects++);
        viewAux.findViewById(R.id.delete_item_btn).setTag(tagDelete);
        viewAux.findViewById(R.id.delete_item_btn).setTag(R.id.tag_item_code,codeTagItems);
        viewAux.findViewById(R.id.delete_item_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout aux = listTimes.get((Integer) view.getTag(R.id.tag_item_code));
                viewItems.removeView(aux);
                viewItems.invalidate();

                listTimes.remove((Integer) view.getTag(R.id.tag_item_code));
                treatDeleteBtns();
            }
        });
        viewAux.findViewById(R.id.delete_item_btn).setId(codeObjects++);

        viewItems.addView(viewAux);

        listTimes.put(codeTagItems,viewAux);

        treatDeleteBtns();
    }

    private void treatDeleteBtns(){
        if(listTimes.size()>1){
            rootView.findViewById(R.id.aux_delete_btn).setVisibility(View.VISIBLE);

            for(Integer code : listTimes.keySet()){
                listTimes.get(code).findViewWithTag("item_delete_"+code).setVisibility(View.VISIBLE);
            }
        }else{
            rootView.findViewById(R.id.aux_delete_btn).setVisibility(View.GONE);

            for(Integer code : listTimes.keySet()){
                listTimes.get(code).findViewWithTag("item_delete_"+code).setVisibility(View.GONE);
            }
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

    public void setNewValues(GregorianCalendar medicineStartDate, int medicineTotalDays) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEDICINE_TYPE, medicineType);
        args.putSerializable(ARG_MEDICINE_TO_EDIT, medicineToEdit);
        args.putSerializable(ARG_START_DATE, medicineStartDate);
        args.putInt(ARG_TOTAL_DAYS, medicineTotalDays);

        setArguments(args);

    }

    public interface OnFragmentInteractionListener {
        void step4End(ArrayList<MedicineTime> listTimes, GregorianCalendar insertTimeNowDate);
    }
}
