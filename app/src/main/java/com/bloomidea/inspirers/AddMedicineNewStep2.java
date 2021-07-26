package com.bloomidea.inspirers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.adapter.DaysAdapter;
import com.bloomidea.inspirers.adapter.FreqIntAdapter;
import com.bloomidea.inspirers.customViews.RangeTimePickerDialog;
import com.bloomidea.inspirers.model.Days;
import com.bloomidea.inspirers.model.FaseTime;
import com.bloomidea.inspirers.model.MedicineDays;
import com.bloomidea.inspirers.model.MedicineSchedule;
import com.bloomidea.inspirers.model.MedicineTime;
import com.bloomidea.inspirers.model.MedicineType;
import com.bloomidea.inspirers.model.ScheduleAux;
import com.bloomidea.inspirers.model.UserNormalMedicine;
import com.bloomidea.inspirers.utils.MedicineTypeAux;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;


public class AddMedicineNewStep2 extends Fragment {
    private static final String ARG_MEDICINE_NAME = "ARG_MEDICINE_NAME";
    private static final String ARG_MEDICINE_TYPE = "ARG_MEDICINE_TYPE";
    private static final String ARG_IS_SOS = "ARG_IS_SOS";
    private static final String ARG_MEDICINE_TO_EDIT = "ARG_MEDICINE_TO_EDIT";

    private AlertDialog dialog;

    private String medicineName;
    private MedicineType medicineType;
    private boolean isSOS;
    private DaysAdapter daysAdapter;
    private FreqIntAdapter freqAdapter;
    private FreqIntAdapter intAdapter;

    private View rootView;

    private LinearLayout viewSchedule;
    private LinearLayout viewItemsTiems;
    private HashMap<Integer, LinearLayout> listSchedule = new HashMap<>();

    private HashMap<Integer, HashMap<Integer, LinearLayout>> listScheTimesDos = new HashMap<>();

    private int codeTagItemsTimes = 0;
    private int codeObjectsTimes = 0;
    private int codeTagItems = 0;
    private int codeObjects = 0;

    private OnFragmentInteractionListener mListener;

    private UserNormalMedicine medicineToEdit = null;


    // set total dosages
    private EditText editTotalDosages;
    private int totalDosages = 1;
    private int totalSOSDosages = 0;
    private GregorianCalendar lastInsertDate;

    // set initial date and duration
    private int intDays = 2;
    private GregorianCalendar dateSelected = null;
    private GregorianCalendar startDate;
    private GregorianCalendar nowInsertDate;
    private int totalDays = 0;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM");

    public AddMedicineNewStep2() {

    }

    public static AddMedicineNewStep2 newInstance(String medicineName, MedicineType medicineType, boolean isSOS, UserNormalMedicine medicineToEdit) {
        AddMedicineNewStep2 fragment = new AddMedicineNewStep2();
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
            rootView = inflater.inflate(R.layout.fragment_add_medicine_new_step, container, false);

            viewSchedule = (LinearLayout) rootView.findViewById(R.id.times_list);

            editTotalDosages = rootView.findViewById(R.id.medicine_total_dosage);

            dateSelected = new GregorianCalendar();
            ((TextView) rootView.findViewById(R.id.med_start_date)).setText(dateFormat.format(dateSelected.getTime()));

            startDate = new GregorianCalendar();
            nowInsertDate = new GregorianCalendar();
            nowInsertDate.add(Calendar.HOUR_OF_DAY, -1);

            ScrollView sv = (ScrollView) rootView.findViewById(R.id.scrollViewStep);
            sv.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
            sv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.requestFocusFromTouch();
                    return false;
                }
            });

            loadMedicineInfo();


            // DOSAGE TOTAL LESS AND MORE ----------------------


            rootView.findViewById(R.id.btn_doses_less).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (totalDosages > 0){
                        totalDosages = totalDosages - 1;
                    }
                    editTotalDosages.setText(""+totalDosages);
                }
            });

            rootView.findViewById(R.id.btn_doses_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    totalDosages = totalDosages + 1;
                    editTotalDosages.setText(""+totalDosages);
                }
            });

            TextWatcher inputTextWatcher = new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    String totalAux = s.toString();
                    if (!totalAux.equals("")){
                        totalDosages = Integer.parseInt(totalAux);
                    }else{
                        totalDosages = 1;
                    }
                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after){
                }
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
            };

            editTotalDosages.addTextChangedListener(inputTextWatcher);

            //------------------------

            // CHANGE SCHE TO SOS --------------------------

            rootView.findViewById(R.id.btn_schedule).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.view_content_sche).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.view_content_sos).setVisibility(View.GONE);
                    ((TextView) rootView.findViewById(R.id.sche_textview)).setTextColor(Color.parseColor("#33cc99"));
                    ((TextView) rootView.findViewById(R.id.sos_textview)).setTextColor(Color.parseColor("#999999"));
                    ((View) rootView.findViewById(R.id.sche_view_under)).setBackgroundColor(Color.parseColor("#33cc99"));
                    ((View) rootView.findViewById(R.id.sos_view_under)).setBackgroundColor(Color.parseColor("#cccccc"));

                    View view = rootView.findViewById(R.id.sche_view_under);
                    ViewGroup.LayoutParams layoutParams =  view.getLayoutParams();
                    layoutParams.height = 3;
                    view.setLayoutParams(layoutParams);

                }
            });

            rootView.findViewById(R.id.btn_sos_extra).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.view_content_sche).setVisibility(View.GONE);
                    rootView.findViewById(R.id.view_content_sos).setVisibility(View.VISIBLE);
                    ((TextView) rootView.findViewById(R.id.sche_textview)).setTextColor(Color.parseColor("#999999"));
                    ((TextView) rootView.findViewById(R.id.sos_textview)).setTextColor(Color.parseColor("#33cc99"));
                    ((View) rootView.findViewById(R.id.sche_view_under)).setBackgroundColor(Color.parseColor("#cccccc"));
                    ((View) rootView.findViewById(R.id.sos_view_under)).setBackgroundColor(Color.parseColor("#33cc99"));

                    View view = rootView.findViewById(R.id.sche_view_under);
                    ViewGroup.LayoutParams layoutParams =  view.getLayoutParams();
                    layoutParams.height = 1;
                    view.setLayoutParams(layoutParams);

                }
            });

            ((EditText) rootView.findViewById(R.id.medicine_sos_posology)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b){
                        if(totalSOSDosages > 0){
                            ((EditText)view).setText(""+totalSOSDosages);
                        }else{
                            ((EditText)view).setText("");
                        }
                    }else{
                        String totalAux = ((EditText) view).getText().toString();
                        totalSOSDosages = 0;

                        if(!totalAux.isEmpty()){
                            try {
                                totalSOSDosages = Integer.parseInt(totalAux);
                            } catch (NumberFormatException e) {
                                Log.d("Exception", e.toString());
                            }
                        }


                        if(totalSOSDosages>0){
                            ((EditText) view).setText(getResources().getQuantityString(MedicineTypeAux.getTextResourceForTotal(medicineType.getCode()),totalSOSDosages,totalSOSDosages));
                        }else{
                            ((EditText)view).setText("");
                        }
                    }
                }
            });

            // ------------------------

            // START DAYS AND DURATION

            rootView.findViewById(R.id.med_start_date).setOnClickListener(new View.OnClickListener() {
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

                            ((TextView) rootView.findViewById(R.id.med_start_date)).setText(dateFormat.format(dateSelected.getTime()));
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

            rootView.findViewById(R.id.select_continuous).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setNumberDayMedicine(0);
                }
            });

            rootView.findViewById(R.id.select_number_days).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setNumberDayMedicine(1);

                }
            });

            ((EditText) rootView.findViewById(R.id.text_number_days)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b){
                        if(totalDays>0){
                            ((EditText)view).setText(""+totalDays);
                        }else{
                            ((EditText)view).setText("");
                        }
                    }else{

                        String totalAux = ((EditText) view).getText().toString();

                        if(!totalAux.isEmpty()){
                            try {
                                totalDays = Integer.parseInt(totalAux);
                            } catch (NumberFormatException e) {
                                Log.d("Exception", e.toString());
                            }
                        }

                        if(totalDays>0){
                            String text = getResources().getString(R.string.text_total_days, totalDays);
                            ((EditText) view).setText(text);
                        }else{
                            ((EditText)view).setText("");
                        }
                    }
                }
            });

            // ------------------------------

            rootView.findViewById(R.id.btn_add_time).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<MedicineTime> times = new ArrayList<MedicineTime>();
                    times.add(new MedicineTime(new FaseTime(8,00),1));
                    createNewItemTime(new MedicineSchedule(new ScheduleAux(ScheduleAux.ONE_A_DAY,ScheduleAux.getDescCode(ScheduleAux.ONE_A_DAY),0),times,new MedicineDays(MedicineDays.ALL_DAYS_OPTION, new ArrayList<Days>(), 0)));
                }
            });

            if(medicineToEdit!=null){

                editTotalDosages.setText(""+medicineToEdit.getInhalers().get(0).getDosage());

                setNumberDayMedicine(medicineToEdit.getDuration());
                ((EditText)rootView.findViewById(R.id.textview_notes)).setText(medicineToEdit.getNote());
                totalDays = medicineToEdit.getDuration();
                String text = getResources().getString(R.string.text_total_days, medicineToEdit.getDuration());
                ((EditText) rootView.findViewById(R.id.text_number_days)).setText(text);
                dateSelected = medicineToEdit.getStartDate();
                ((TextView) rootView.findViewById(R.id.med_start_date)).setText(dateFormat.format(dateSelected.getTime()));
                String textSOS = getResources().getQuantityString(MedicineTypeAux.getTextResourceForTotal(medicineToEdit.getMedicineType().getCode()),medicineToEdit.getTotalSOSDosages(),medicineToEdit.getTotalSOSDosages());
                ((EditText) rootView.findViewById(R.id.medicine_sos_posology)).setText(textSOS);

                for (MedicineSchedule schedule : medicineToEdit.getSchedules()){
                    createNewItemTime(schedule);
                }

                if (medicineToEdit.getMedicineType().getCode().equals(MedicineTypeAux.TYPE1_CODE)){
                    rootView.findViewById(R.id.view_remaining_doses).setVisibility(View.VISIBLE);
                }else{
                    rootView.findViewById(R.id.view_remaining_doses).setVisibility(View.GONE);
                }

            }else{
                if (medicineType.getCode().equals(MedicineTypeAux.TYPE1_CODE)){
                    rootView.findViewById(R.id.view_remaining_doses).setVisibility(View.VISIBLE);
                }else{
                    rootView.findViewById(R.id.view_remaining_doses).setVisibility(View.GONE);
                }
                ArrayList<MedicineTime> times = new ArrayList<MedicineTime>();
                times.add(new MedicineTime(new FaseTime(8,00),1));
                createNewItemTime(new MedicineSchedule(new ScheduleAux(ScheduleAux.ONE_A_DAY,ScheduleAux.getDescCode(ScheduleAux.ONE_A_DAY),0),times,new MedicineDays(MedicineDays.ALL_DAYS_OPTION, new ArrayList<Days>(), 0)));
            }

            rootView.findViewById(R.id.proceed_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    clearAllFocusTextView();

                    ArrayList<MedicineSchedule> listTimeAux = new ArrayList<MedicineSchedule>();

                    SortedSet<Integer> keys = new TreeSet<Integer>(listSchedule.keySet());

                    boolean boolTimes = false;
                    boolean boolDays = false;
                    boolean auxPoso = false;

                    for(Integer key : keys){
                        //Object auxTimes = (listSchedule.get(key).findViewWithTag("item_times_box_"+key)).getTag(R.id.tag_selected_times);

                        //Object auxDays = (listSchedule.get(key).findViewById(R.id.info_days)).getTag(R.id.tag_selected_days);
                        Object auxDays = (listSchedule.get(key).findViewWithTag("item_days_"+key)).getTag(R.id.tag_selected_days);

                        //Object auxSelection = (listSchedule.get(key).findViewById(R.id.medicine_time_box)).getTag(R.id.tag_selected_schedule_code);
                        Object auxSelection = (listSchedule.get(key).findViewWithTag("item_select_box_"+key)).getTag(R.id.tag_selected_schedule_code);

                        if(auxDays!=null) {
                            ScheduleAux medSelection = (ScheduleAux) auxSelection;

                            ArrayList<MedicineTime> medTimes = new ArrayList<MedicineTime>();

                            //Object auxListTimes = (listSchedule.get(key).findViewById(R.id.times_list)).getTag(R.id.tag_selected_times);
                            Object auxListTimes = (listSchedule.get(key).findViewWithTag("item_times_box_"+key)).getTag(R.id.tag_selected_times);

                            HashMap<Integer, LinearLayout> hashTimes = (HashMap<Integer, LinearLayout>) auxListTimes;

                            SortedSet<Integer> keysTimes = new TreeSet<Integer>(hashTimes.keySet());

                            for (Integer keyTime : keysTimes){

                                //Object auxTimes = (hashTimes.get(keyTime).findViewById(R.id.time_dosage_textView)).getTag(R.id.tag_selected_fase_time);
                                Object auxTimes = (hashTimes.get(keyTime).findViewWithTag("item_time_box_"+keyTime)).getTag(R.id.tag_selected_fase_time);

                                if(auxTimes!=null) {
                                    FaseTime faseTime = (FaseTime) auxTimes;

                                    (hashTimes.get(keyTime).findViewById(R.id.medicine_posology_editText)).clearFocus();

                                    int total = (int) (hashTimes.get(keyTime).findViewWithTag("item_podology_"+keyTime)).getTag(R.id.tag_posology_total);

                                    Log.d("ITEM", faseTime.getHourInitDesc() + "-" + total);

                                    if(total>0) {
                                        medTimes.add(new MedicineTime(faseTime, total));
                                    }else{
                                        auxPoso = true;
                                    }
                                }else{
                                    boolTimes = true;
                                }

                            }

                            MedicineDays medDays = (MedicineDays) auxDays;

                            if(medTimes.size()>0) {
                                listTimeAux.add(new MedicineSchedule(medSelection, medTimes, medDays));
                            }else{
                                boolTimes = true;
                            }
                        }else{
                            boolDays = true;
                        }
                    }

                    if(!listTimeAux.isEmpty()){
                        mListener.newStep2End(listTimeAux, totalDosages, dateSelected, nowInsertDate, totalDays, getNoteText(), totalSOSDosages);
                    }else{
                        if(boolDays){
                           Toast.makeText(getActivity(), R.string.one_posology_is_needed,Toast.LENGTH_SHORT).show();
                        }else if(boolTimes){
                            //Toast.makeText(getActivity(), R.string.one_time_is_needed, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(), R.string.one_time_posology_are_nedded, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getActivity(), R.string.one_time_posology_are_nedded, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        return rootView;
    }



    private void setNumberDayMedicine(int duration){
        if (duration != 0){
            ((ImageView) rootView.findViewById(R.id.img_select_continuous)).setImageResource(R.drawable.asset30);
            ((ImageView) rootView.findViewById(R.id.img_select_number_days)).setImageResource(R.drawable.asset31);
            ((EditText) rootView.findViewById(R.id.text_number_days)).setEnabled(true);
        }else{
            ((ImageView) rootView.findViewById(R.id.img_select_continuous)).setImageResource(R.drawable.asset31);
            ((ImageView) rootView.findViewById(R.id.img_select_number_days)).setImageResource(R.drawable.asset30);
            ((EditText) rootView.findViewById(R.id.text_number_days)).setEnabled(false);
            totalDays = 0;
        }
    }

    public void updateMedicine(String medicineName, MedicineType medicineType) {
        this.medicineName = medicineName;
        this.medicineType = medicineType;

        if (medicineType.getCode().equals(MedicineTypeAux.TYPE1_CODE)){
            rootView.findViewById(R.id.view_remaining_doses).setVisibility(View.VISIBLE);
        }else{
            rootView.findViewById(R.id.view_remaining_doses).setVisibility(View.GONE);
        }

        loadMedicineInfo();
    }

    private void loadMedicineInfo() {
        ((ImageView) rootView.findViewById(R.id.medicine_type_icon_imageView)).setImageResource(MedicineTypeAux.getMedicineTypeIcon(medicineType.getCode()));
        ((TextView) rootView.findViewById(R.id.medicine_name_textView)).setText(medicineName);

    }

    private void createNewItemTime(MedicineSchedule schedule){
        HashMap<Integer, LinearLayout> listTimesDos = new HashMap<>();

        ScheduleAux selection = null;
        ArrayList<MedicineTime> auxTimes = null;
        MedicineDays auxDays = null;

        if(schedule!=null){
            selection = schedule.getSelection();
            auxTimes = schedule.getTimes();
            auxDays = schedule.getDays();
        }

        final LinearLayout viewAux = (LinearLayout) View.inflate(getActivity(),R.layout.item_scheduling, null);

        codeTagItems++;
        String tag = "item_"+codeTagItems;
        viewAux.setTag(tag);

        String tagSelectBox = "item_select_box_"+codeTagItems;
        String tagTextBox = "item_select_text_"+codeTagItems;
        viewAux.findViewById(R.id.medicine_time_box).setTag(tagSelectBox);
        viewAux.findViewById(R.id.medicine_time_box).setTag(R.id.tag_selected_schedule_code,selection);
        viewAux.findViewById(R.id.medicine_time_box).setTag(R.id.tag_textView,viewAux.findViewById(R.id.medicine_time_textView));

        String tagTimesBox = "item_times_box_"+codeTagItems;
        viewAux.findViewById(R.id.times_list).setTag(tagTimesBox);
        viewAux.findViewById(R.id.times_list).setTag(R.id.tag_selected_times,auxTimes);

        // SETUP EDIT MEDICINE SELECTION

        if (selection != null){
            viewItemsTiems = viewAux.findViewById(R.id.times_list);

            ((TextView) viewAux.findViewById(R.id.medicine_time_box).getTag(R.id.tag_textView)).setText(selection.getDesc());
            for(int i = 0; i < ScheduleAux.creationNumberForSelection(selection); i++) {
                if (auxTimes.isEmpty()){
                    createNewItemTimeDos(null, selection, i == 0, codeTagItems, listTimesDos);
                }else{
                    createNewItemTimeDos(auxTimes.get(i), selection, i == 0, codeTagItems, listTimesDos);
                }
            }
            viewAux.findViewById(R.id.times_list).setTag(R.id.tag_selected_times,listScheTimesDos.get(codeTagItems));
        }

        String tagDaysBox = "item_days_"+codeTagItems;
        viewAux.findViewById(R.id.info_days).setTag(tagDaysBox);
        viewAux.findViewById(R.id.info_days).setTag(R.id.tag_selected_days,auxDays);

        String tagEveryDay = "item_every_day_"+codeTagItems;
        String tagImgEveryDay = "item_img_every_day_"+codeTagItems;
        viewAux.findViewById(R.id.select_every_day).setTag(tagEveryDay);
        viewAux.findViewById(R.id.img_every_day).setTag(tagImgEveryDay);
        viewAux.findViewById(R.id.select_every_day).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intDays = 2;
                MedicineDays auxDays = new MedicineDays(MedicineDays.ALL_DAYS_OPTION, new ArrayList<Days>(), 0);
                setMedicineDaysInterface(viewAux, auxDays);
                viewAux.findViewById(R.id.info_days).setTag(R.id.tag_selected_days, auxDays);
            }
        });

        String tagWeekDay = "item_week_day_"+codeTagItems;
        String tagImgaWeekDay = "item_img_week_day_"+codeTagItems;
        viewAux.findViewById(R.id.select_week_day).setTag(tagWeekDay);
        viewAux.findViewById(R.id.select_week_day).setTag(R.id.tag_textView,viewAux.findViewById(R.id.week_days_selected));
        viewAux.findViewById(R.id.img_week_day).setTag(tagImgaWeekDay);
        final String auxTagWeekDay = tagWeekDay;
        viewAux.findViewById(R.id.select_week_day).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImageView) viewAux.findViewById(R.id.img_every_day)).setImageResource(R.drawable.asset30);
                ((ImageView) viewAux.findViewById(R.id.img_week_day)).setImageResource(R.drawable.asset31);
                ((ImageView) viewAux.findViewById(R.id.img_range_day)).setImageResource(R.drawable.asset30);
                ((TextView) viewAux.findViewById(R.id.text_days_range)).setVisibility(View.GONE);
                intDays = 2;
                MedicineDays days = (MedicineDays) viewAux.findViewById(R.id.info_days).getTag(R.id.tag_selected_days);
                showDialogWeekDay(viewAux, days);

            }
        });

        String tagRangeDay = "item_range_day_"+codeTagItems;
        String tagImgaRangeDay = "item_img_range_day_"+codeTagItems;
        viewAux.findViewById(R.id.select_range_day).setTag(tagRangeDay);
        viewAux.findViewById(R.id.img_range_day).setTag(tagImgaRangeDay);
        viewAux.findViewById(R.id.select_range_day).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MedicineDays auxDays = new MedicineDays(MedicineDays.INT_DAYS_OPTION, new ArrayList<Days>(), intDays);
                viewAux.findViewById(R.id.info_days).setTag(R.id.tag_selected_days, auxDays);
                setMedicineDaysInterface(viewAux, auxDays);
                showDialogIntDays(viewAux, auxDays);
            }
        });

        // SETUP EDIT MEDICINE DAYS

        if (auxDays == null){
            MedicineDays defaultDays = new MedicineDays(MedicineDays.ALL_DAYS_OPTION, new ArrayList<Days>(), 0);
            viewAux.findViewById(R.id.info_days).setTag(R.id.tag_selected_days, defaultDays);
        }else{
            setMedicineDaysInterface(viewAux, auxDays);
        }

        final ScheduleAux finalSelection = selection;
        viewAux.findViewById(R.id.medicine_time_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showDialogFreqInt(viewAux, finalSelection, codeTagItems);
            }
        });

        viewAux.findViewById(codeTagItems);
        viewAux.findViewById(R.id.medicine_time_textView).setTag(tagTextBox);
        //viewAux.findViewById(R.id.medicine_time_textView).setId(codeTagItems);

        viewSchedule.addView(viewAux);

        listSchedule.put(codeTagItems,viewAux);

    }

    private void setMedicineDaysInterface(final LinearLayout viewAux, MedicineDays days){
        if (days.getSelectedOption() == MedicineDays.ALL_DAYS_OPTION){
            ((ImageView) viewAux.findViewById(R.id.img_every_day)).setImageResource(R.drawable.asset31);
            ((ImageView) viewAux.findViewById(R.id.img_week_day)).setImageResource(R.drawable.asset30);
            ((ImageView) viewAux.findViewById(R.id.img_range_day)).setImageResource(R.drawable.asset30);
            ((TextView) viewAux.findViewById(R.id.select_week_day).getTag(R.id.tag_textView)).setVisibility(View.GONE);
            ((TextView) viewAux.findViewById(R.id.text_days_range)).setVisibility(View.GONE);
        }else if (days.getSelectedOption() == MedicineDays.SPEC_DAYS_OPTION){
            ((ImageView) viewAux.findViewById(R.id.img_every_day)).setImageResource(R.drawable.asset30);
            ((ImageView) viewAux.findViewById(R.id.img_week_day)).setImageResource(R.drawable.asset31);
            ((ImageView) viewAux.findViewById(R.id.img_range_day)).setImageResource(R.drawable.asset30);
            ((TextView) viewAux.findViewById(R.id.text_days_range)).setVisibility(View.GONE);
            ((TextView) viewAux.findViewById(R.id.select_week_day).getTag(R.id.tag_textView)).setVisibility(View.VISIBLE);
            ArrayList<Days> temp_arraylist = new ArrayList<Days>();
            for (Days auxDay : days.getSelectedDays()){
                if (auxDay.isSelected()){
                    temp_arraylist.add(auxDay);
                }
            }
            ((TextView) viewAux.findViewById(R.id.select_week_day).getTag(R.id.tag_textView)).setText(days.getSelectedDaysText(temp_arraylist));
        } else {
            ((ImageView) viewAux.findViewById(R.id.img_every_day)).setImageResource(R.drawable.asset30);
            ((ImageView) viewAux.findViewById(R.id.img_week_day)).setImageResource(R.drawable.asset30);
            ((ImageView) viewAux.findViewById(R.id.img_range_day)).setImageResource(R.drawable.asset31);
            ((TextView) viewAux.findViewById(R.id.select_week_day).getTag(R.id.tag_textView)).setVisibility(View.GONE);
            ((TextView) viewAux.findViewById(R.id.text_days_range)).setVisibility(View.VISIBLE);
            String text = String.format(getResources().getString(R.string.text_int_days), days.getIntervalDays());
            ((TextView) viewAux.findViewById(R.id.text_days_range)).setText(text);
        }
    }

    private void createNewItemTimeDos(MedicineTime time, final ScheduleAux selection, boolean isFirst, int key, final HashMap<Integer, LinearLayout> listTimesDos){
        FaseTime auxSelectedFaseTime = null;
        int auxDosage = 0;

        if(time!=null){
            auxDosage = time.getDosage();
            auxSelectedFaseTime =time.getFaseTime();
        }

        LinearLayout viewAux = (LinearLayout) View.inflate(getActivity(),R.layout.item_new_posology, null);

        codeTagItemsTimes++;
        String tag = "item_"+codeTagItemsTimes;
        viewAux.setTag(tag);

        String tagTimeBox = "item_time_box_"+codeTagItemsTimes;
        String tagPodology = "item_podology_"+codeTagItemsTimes;
        viewAux.findViewById(R.id.time_dosage_textView).setTag(tagTimeBox);
        viewAux.findViewById(R.id.time_dosage_textView).setTag(R.id.tag_selected_fase_time,auxSelectedFaseTime);

        if(auxSelectedFaseTime!=null) {
            ((TextView) viewAux.findViewById(R.id.time_dosage_textView)).setText(auxSelectedFaseTime.getInitTime());
        }else{
            if (isFirst){
                ((TextView) viewAux.findViewById(R.id.time_dosage_textView)).setEnabled(true);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                lastInsertDate = new GregorianCalendar();
                lastInsertDate.set(Calendar.HOUR_OF_DAY, hour);
                lastInsertDate.set(Calendar.MINUTE, minute);

                FaseTime auxFaseTime = new FaseTime(hour,minute);
                ((TextView) viewAux.findViewById(R.id.time_dosage_textView)).setTag(R.id.tag_selected_fase_time,auxFaseTime);
                ((TextView) viewAux.findViewById(R.id.time_dosage_textView)).setText(String.format("%02d:%02d", hour, minute));

            }else{
                int hour = lastInsertDate.get(Calendar.HOUR_OF_DAY);
                int minute = lastInsertDate.get(Calendar.MINUTE);
                int nextHour;

                if (selection.getIntervalHours() == 0){
                    ((TextView) viewAux.findViewById(R.id.time_dosage_textView)).setEnabled(true);
                    nextHour = hour + ScheduleAux.getIntervalForTime(selection);
                }else{
                    ((TextView) viewAux.findViewById(R.id.time_dosage_textView)).setEnabled(false);
                    nextHour = hour + selection.getIntervalHours();
                }

                if (nextHour > 24){
                    nextHour = nextHour - 24;
                }

                lastInsertDate.set(Calendar.HOUR_OF_DAY, nextHour);

                FaseTime auxFaseTime = new FaseTime(nextHour,minute);
                ((TextView) viewAux.findViewById(R.id.time_dosage_textView)).setTag(R.id.tag_selected_fase_time,auxFaseTime);
                ((TextView) viewAux.findViewById(R.id.time_dosage_textView)).setText(String.format("%02d:%02d", nextHour, minute));
            }
        }

        ((TextView) viewAux.findViewById(R.id.time_dosage_textView)).setOnClickListener(new View.OnClickListener() {
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

                        ((TextView) view).setText(auxFaseTime.getInitTime());

                        if (selection.getIntervalHours() != 0){
                            lastInsertDate.set(Calendar.HOUR_OF_DAY, selectedHour);
                            lastInsertDate.set(Calendar.MINUTE, selectedMinute);

                            for(int i = 2; i <= listTimesDos.size(); i++) {
                                LinearLayout auxItem = listTimesDos.get(i);
                                int nextHour = lastInsertDate.get(Calendar.HOUR_OF_DAY) + selection.getIntervalHours();
                                lastInsertDate.set(Calendar.HOUR_OF_DAY, nextHour);
                                FaseTime newAuxTime = new FaseTime(lastInsertDate.get(Calendar.HOUR_OF_DAY), lastInsertDate.get(Calendar.MINUTE));

                                ((TextView) auxItem.findViewById(R.id.time_dosage_textView)).setTag(R.id.tag_selected_fase_time,newAuxTime);
                                ((TextView) auxItem.findViewById(R.id.time_dosage_textView)).setText(newAuxTime.getInitTime());
                            }
                        }
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

        viewAux.findViewById(codeTagItemsTimes);
        //viewAux.findViewById(R.id.time_dosage_textView).setId(codeObjectsTimes++);
        //viewAux.findViewById(R.id.medicine_posology_editText).setId(codeObjectsTimes++);

        viewItemsTiems.addView(viewAux);

        listTimesDos.put(codeTagItemsTimes,viewAux);

        listScheTimesDos.put(key, listTimesDos);

    }

    private void showDialogIntDays(final LinearLayout viewAux, MedicineDays auxDays) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.overlay_int_days, null);//criei esta view porque nao estava a conseguir ir buscar as refs com o dialog.findbyID tava a dar null
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyleNoWindow);
        builder.setView(view);

        final TextView textViewInt = view.findViewById(R.id.text_int_days);
        String text = String.format(getResources().getString(R.string.text_int_days), intDays);
        textViewInt.setText(text);

        view.findViewById(R.id.btn_days_less).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intDays > 2){
                    intDays = intDays - 1;
                    String text = String.format(getResources().getString(R.string.text_int_days), intDays);
                    textViewInt.setText(text);
                    ((TextView) viewAux.findViewById(R.id.text_days_range)).setText(text);
                    MedicineDays auxDays = new MedicineDays(MedicineDays.INT_DAYS_OPTION, new ArrayList<Days>(), intDays);
                    viewAux.findViewById(R.id.info_days).setTag(R.id.tag_selected_days, auxDays);
                }
            }
        });

        view.findViewById(R.id.btn_days_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intDays = intDays + 1;
                String text = String.format(getResources().getString(R.string.text_int_days), intDays);
                textViewInt.setText(text);
                ((TextView) viewAux.findViewById(R.id.text_days_range)).setText(text);
                MedicineDays auxDays = new MedicineDays(MedicineDays.INT_DAYS_OPTION, new ArrayList<Days>(), intDays);
                viewAux.findViewById(R.id.info_days).setTag(R.id.tag_selected_days, auxDays);
            }
        });

        dialog = builder.create();
        dialog.show();

    }

    private void showDialogFreqInt(final LinearLayout viewAux, final ScheduleAux selection, final int key) {
        final HashMap<Integer, LinearLayout> listTimesDos = new HashMap<>();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.overlay_freq_int_med, null);//criei esta view porque nao estava a conseguir ir buscar as refs com o dialog.findbyID tava a dar null
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyleNoWindow);
        builder.setView(view);

        viewItemsTiems = viewAux.findViewById(R.id.times_list);

        RecyclerView recyclerView;
        LinearLayoutManager linearLayoutManager;
        linearLayoutManager = new LinearLayoutManager(getActivity());

        freqAdapter = new FreqIntAdapter(getActivity(), ScheduleAux.getFrequencyList(), new FreqIntAdapter.FreqIntAdapterListener() {
            @Override
            public void onItemClick(ScheduleAux item) {
                viewAux.findViewById(R.id.medicine_time_box).setTag(R.id.tag_selected_schedule_code,item);
                ((TextView) viewAux.findViewById(R.id.medicine_time_box).getTag(R.id.tag_textView)).setText(item.getDesc());
                listScheTimesDos.remove(key);
                //listTimesDos.clear();
                viewItemsTiems.removeAllViews();
                codeTagItemsTimes = 0;
                //codeObjectsTimes = 0;
                for(int i = 0; i < ScheduleAux.creationNumberForSelection(item); i++) {
                    createNewItemTimeDos(null, item, i == 0, key, listTimesDos);
                }
                viewAux.findViewById(R.id.times_list).setTag(R.id.tag_selected_times,listScheTimesDos.get(key));
                dialog.dismiss();
            }
        });

        recyclerView = view.findViewById(R.id.freqList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(freqAdapter);

        RecyclerView recyclerViewInt;
        LinearLayoutManager linearLayoutManager2;
        linearLayoutManager2 = new LinearLayoutManager(getActivity());

        intAdapter = new FreqIntAdapter(getActivity(), ScheduleAux.getIntervalList(), new FreqIntAdapter.FreqIntAdapterListener() {
            @Override
            public void onItemClick(ScheduleAux item) {
                viewAux.findViewById(R.id.medicine_time_box).setTag(R.id.tag_selected_schedule_code,item);
                ((TextView) viewAux.findViewById(R.id.medicine_time_box).getTag(R.id.tag_textView)).setText(item.getDesc());
                listScheTimesDos.remove(key);
                //listTimesDos.clear();
                viewItemsTiems.removeAllViews();
                codeTagItemsTimes = 0;
                //codeObjectsTimes = 0;
                for(int i = 0; i < ScheduleAux.creationNumberForSelection(item); i++) {
                    createNewItemTimeDos(null, item, i == 0, key, listTimesDos);
                }

                viewAux.findViewById(R.id.times_list).setTag(R.id.tag_selected_times,listScheTimesDos.get(key));
                dialog.dismiss();
            }
        });

        recyclerViewInt = view.findViewById(R.id.intervalList);
        recyclerViewInt.setLayoutManager(linearLayoutManager2);
        recyclerViewInt.setAdapter(intAdapter);

        dialog = builder.create();
        dialog.show();

    }

    private void showDialogWeekDay(final LinearLayout v, MedicineDays auxDays) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.overlay_select_days, null);//criei esta view porque nao estava a conseguir ir buscar as refs com o dialog.findbyID tava a dar null
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyleNoWindow);
        builder.setView(view);

        if (auxDays == null || auxDays.getSelectedDays() == null || auxDays.getSelectedDays().isEmpty()){
            ArrayList<Days> days = new ArrayList<Days>();
            days.add(new Days(Days.SUNDAY_OPTION, false, getResources().getString(R.string.day_sunday)));
            days.add(new Days(Days.MONDAY_OPTION, false, getResources().getString(R.string.day_monday)));
            days.add(new Days(Days.TUESDAY_OPTION, false, getResources().getString(R.string.day_tuesday)));
            days.add(new Days(Days.WEDNESDAY_OPTION, false, getResources().getString(R.string.day_wednesday)));
            days.add(new Days(Days.THURSDAY_OPTION, false, getResources().getString(R.string.day_thursday)));
            days.add(new Days(Days.FRIDAY_OPTION, false, getResources().getString(R.string.day_friday)));
            days.add(new Days(Days.SATURDAY_OPTION, false, getResources().getString(R.string.day_saturday)));

            auxDays = new MedicineDays(MedicineDays.SPEC_DAYS_OPTION, days, 0);
        }

        RecyclerView recyclerView;
        LinearLayoutManager linearLayoutManager;

        final MedicineDays finalAuxDays = auxDays;
        daysAdapter = new DaysAdapter(getActivity(), auxDays.getSelectedDays(), new DaysAdapter.DaysAdapterListener() {
            @Override
            public void onDaysClick(Days day) {
                day.setSelected(!day.isSelected());
                daysAdapter.notifyDataSetChanged();
                v.findViewById(R.id.info_days).setTag(R.id.tag_selected_days, finalAuxDays);
                ((TextView) v.findViewById(R.id.select_week_day).getTag(R.id.tag_textView)).setVisibility(View.VISIBLE);
                ArrayList<Days> temp_arraylist = new ArrayList<Days>();
                for (Days auxDay : finalAuxDays.getSelectedDays()){
                    if (auxDay.isSelected()){
                        temp_arraylist.add(auxDay);
                    }
                }
                ((TextView) v.findViewById(R.id.select_week_day).getTag(R.id.tag_textView)).setText(finalAuxDays.getSelectedDaysText(temp_arraylist));
            }
        });

        linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView = view.findViewById(R.id.daysList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(daysAdapter);

        dialog = builder.create();
        dialog.show();

    }

    public String getNoteText() {
        return ((EditText)rootView.findViewById(R.id.textview_notes)).getText().toString();
    }

    private void clearAllFocusTextView(){
        for(Integer key : listScheTimesDos.keySet()){
            for(Integer key2 : listScheTimesDos.get(key).keySet()){
                (listScheTimesDos.get(key).get(key2).findViewWithTag("item_podology_" + key2)).clearFocus();
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

    public interface OnFragmentInteractionListener {
        void newStep2End(ArrayList<MedicineSchedule> listSchedule, int totalDosages, GregorianCalendar dateSelected, GregorianCalendar insertTimeNow, int totalDays, String comment, int totalSOSDosages);
        void onGoBack();
    }

}
