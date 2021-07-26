package com.bloomidea.inspirers;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.events.MedicineCreatedEvent;
import com.bloomidea.inspirers.listener.OkErrorListener;
import com.bloomidea.inspirers.model.Badge;
import com.bloomidea.inspirers.model.MedicineDays;
import com.bloomidea.inspirers.model.MedicineInhaler;
import com.bloomidea.inspirers.model.MedicineSchedule;
import com.bloomidea.inspirers.model.MedicineTime;
import com.bloomidea.inspirers.model.MedicineType;
import com.bloomidea.inspirers.model.UserBadge;
import com.bloomidea.inspirers.model.UserMedicine;
import com.bloomidea.inspirers.model.UserNormalMedicine;
import com.bloomidea.inspirers.utils.BadgesAux;
import com.bloomidea.inspirers.utils.MedicineTypeAux;
import com.bloomidea.inspirers.utils.ProgressBarAnimation;
import com.bloomidea.inspirers.utils.Utils;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class AddMedicineActivity extends MyActiveActivity implements AddMedicineStep1.OnFragmentInteractionListener, AddMedicineStep2.OnFragmentInteractionListener, AddMedicineNewStep2.OnFragmentInteractionListener, AddMedicineStep3.OnFragmentInteractionListener, AddMedicineStep4.OnFragmentInteractionListener, AddMedicineStep5.OnFragmentInteractionListener {
    public static String EXTRA_MEDICINE_EDIT = "EXTRA_MEDICINE_EDIT";

    private AlertDialog dialog;

    private TextView setp1Ind;
    private TextView setp2Ind;
    private TextView setp3Ind;
    private TextView setp4Ind;
    private TextView setp5Ind;

    private ProgressBar progressBar;
    private TextView step_message_textView;
    private View add_new_btn;


    private Fragment step1;
    private Fragment step2;
    private Fragment newStep2;
    private Fragment step3;
    private Fragment step4;
    private Fragment step5;

    //Medicine Info - STEP1;
    private MedicineType medicineType;
    private String medicineName;
    private int medicineDosage;
    private String medicineBarCode;

    //Medicine Info - NEWSTEP2;
    private int totalDosages;
    private GregorianCalendar medicineStartDate;
    private int medicineTotalDays;
    private ArrayList<MedicineSchedule> medicineListSchedule;
    private GregorianCalendar insertTimeNowDate;
    private String medicineNote;
    private int totalSOSDosages;

    //Medicine Info - STEP2;
    private boolean shareDoctor;

    private int currentStep = 1;
    private int tutorialStep = 0;

    //Medicine Info - STEP3;
    //private GregorianCalendar medicineStartDate;
    //private int medicineTotalDays;

    //Medicine Info - STEP4;
    //private ArrayList<MedicineTime> medicineListTimes;
    //private GregorianCalendar insertTimeNowDate;

    //Medicine Info - STEP5;
    //private String medicineNote;


    private UserNormalMedicine medicineToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        medicineToEdit = (UserNormalMedicine) getIntent().getSerializableExtra(EXTRA_MEDICINE_EDIT);

        progressBar = (ProgressBar) findViewById(R.id.level_progress_bar);

        step_message_textView = (TextView) findViewById(R.id.step_message_textView);

        add_new_btn = findViewById(R.id.add_new_btn);

        setp1Ind = (TextView) findViewById(R.id.include).findViewById(R.id.textviewPoints);
        setp2Ind = (TextView) findViewById(R.id.include2).findViewById(R.id.textviewPoints);
        setp3Ind = (TextView) findViewById(R.id.include3).findViewById(R.id.textviewPoints);
        setp4Ind = (TextView) findViewById(R.id.include4).findViewById(R.id.textviewPoints);
        setp5Ind = (TextView) findViewById(R.id.include5).findViewById(R.id.textviewPoints);


        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.help_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOverlayTutorial();
            }
        });

        findViewById(R.id.help_btn_imageView).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.title_textView)).setText(R.string.add_medicine_title);

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int c = getFragmentManager().getBackStackEntryCount();

                stepTopConfig(c);
            }
        });

        goToStep1(false);
    }

    private void showOverlayTutorial() {
        findViewById(R.id.overlay_tutorial).setVisibility(View.VISIBLE);

        findViewById(R.id.tutorial_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((currentStep == 1 && tutorialStep >= 0 && tutorialStep < 2) || (currentStep == 2 && tutorialStep >= 2 && tutorialStep < 5)){
                    tutorialStep = tutorialStep + 1;
                    changeTutorial(tutorialStep);
                }else if ((currentStep == 1 && tutorialStep == 2) || (currentStep == 2 && tutorialStep == 5)){
                    findViewById(R.id.overlay_tutorial).setVisibility(View.GONE);
                }
            }
        });

        findViewById(R.id.tutorial_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((currentStep == 1 && tutorialStep > 0 && tutorialStep <= 2) || (currentStep == 2 && tutorialStep > 3 && tutorialStep <= 5)) {
                    tutorialStep = tutorialStep - 1;
                    changeTutorial(tutorialStep);
                }
            }
        });
    }

    private void changeTutorial(int step){
        if (tutorialStep == 0){
            ((TextView) findViewById(R.id.tutorial_text)).setText(R.string.tutorial_text_one);
            ((ImageView) findViewById(R.id.tutorial_image)).setImageResource(R.drawable.asset46);
        }else if (tutorialStep == 1){
            ((TextView) findViewById(R.id.tutorial_text)).setText(R.string.tutorial_text_two);
            ((ImageView) findViewById(R.id.tutorial_image)).setImageResource(R.drawable.asset47);
        }else if (tutorialStep == 2){
            ((TextView) findViewById(R.id.tutorial_text)).setText(R.string.tutorial_text_three);
            ((ImageView) findViewById(R.id.tutorial_image)).setImageResource(R.drawable.asset51);
        }else if (tutorialStep == 3){
            ((TextView) findViewById(R.id.tutorial_text)).setText(R.string.tutorial_text_four);
            ((ImageView) findViewById(R.id.tutorial_image)).setImageResource(R.drawable.asset49);
        }else if (tutorialStep == 4){
            ((TextView) findViewById(R.id.tutorial_text)).setText(R.string.tutorial_text_five);
            ((ImageView) findViewById(R.id.tutorial_image)).setImageResource(R.drawable.asset50);
        }else if (tutorialStep == 5){
            ((TextView) findViewById(R.id.tutorial_text)).setText(R.string.tutorial_text_six);
            ((ImageView) findViewById(R.id.tutorial_image)).setImageResource(R.drawable.asset48);
        }
    }

    private void clearBackStack() {
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            //FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            //manager.popBackStackImmediate(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        ((FrameLayout) findViewById(R.id.add_medicine_container)).removeAllViews();
    }

    private void goToStep1(boolean clearAll){
        if(clearAll){
            clearBackStack();

            step1 = null;
            step2 = null;
            step3 = null;
            step4 = null;
            step5 = null;

            medicineType = null;
            medicineName = null;
            medicineDosage = -1;
            medicineBarCode = null;
            shareDoctor = true;

            medicineStartDate = null;
            medicineTotalDays = -1;
            medicineListSchedule = null;
            //medicineListTimes = null;
            insertTimeNowDate = null;
            medicineNote = null;
        }

        boolean first =false;
        if (step1 == null) {
            step1 = AddMedicineStep1.newInstance(false, medicineToEdit);
            first = true;
        }

        currentStep = 1;
        tutorialStep = 0;
        ((TextView) findViewById(R.id.tutorial_text)).setText(R.string.tutorial_text_one);
        ((ImageView) findViewById(R.id.tutorial_image)).setImageResource(R.drawable.asset46);

        flipCard(step1,first);
    }

    private void goToNewStep2() {

        if (newStep2 == null) {
            newStep2 = AddMedicineNewStep2.newInstance(medicineName,medicineType,false,medicineToEdit);
        }else{
            ((AddMedicineNewStep2) newStep2).updateMedicine(medicineName,medicineType);
        }

        currentStep = 2;
        tutorialStep = 3;
        ((TextView) findViewById(R.id.tutorial_text)).setText(R.string.tutorial_text_four);
        ((ImageView) findViewById(R.id.tutorial_image)).setImageResource(R.drawable.asset49);

        flipCard(newStep2,false);
    }

    private void goToStep2() {

        if (step2 == null) {
            step2 = AddMedicineStep2.newInstance(medicineName,medicineType,false,medicineToEdit);
        }else{
            ((AddMedicineStep2) step2).updateMedicine(medicineName,medicineType);
        }

        flipCard(step2,false);
    }

    private void stepTopConfig(int stepPos){
        //TextView aux = null;

        Log.d("STEP_POS",""+stepPos);
        ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, progressBar.getProgress(), 19+(20*stepPos));
        anim.setDuration(100);

        progressBar.startAnimation(anim);

        if(stepPos == 0){
            //SETP 1;
            ((TextView) findViewById(R.id.title_textView)).setText(R.string.add_medicine_title);
            findViewById(R.id.medicine_imageView).setVisibility(View.GONE);

            step_message_textView.setText(Html.fromHtml(getString(R.string.add_medicine_step1)));

            setp1Ind.setBackgroundResource(R.drawable.point_not_win_back);
            setp2Ind.setBackgroundResource(R.drawable.point_not_win_back);
            setp3Ind.setBackgroundResource(R.drawable.point_not_win_back);
            setp4Ind.setBackgroundResource(R.drawable.point_not_win_back);
            setp5Ind.setBackgroundResource(R.drawable.point_not_win_back);

            add_new_btn.setVisibility(View.GONE);
            add_new_btn.setOnClickListener(null);
        }else if (stepPos == 1){
            //SETP 2;

            ((TextView) findViewById(R.id.title_textView)).setText(R.string.add_medicine_title);
            findViewById(R.id.medicine_imageView).setVisibility(View.GONE);


            step_message_textView.setText(Html.fromHtml(getString(R.string.add_medicine_step2)));

            setp1Ind.setBackgroundResource(R.drawable.point_win_back);
            setp2Ind.setBackgroundResource(R.drawable.point_not_win_back);
            setp3Ind.setBackgroundResource(R.drawable.point_not_win_back);
            setp4Ind.setBackgroundResource(R.drawable.point_not_win_back);
            setp5Ind.setBackgroundResource(R.drawable.point_not_win_back);

            add_new_btn.setVisibility(View.GONE);
            add_new_btn.setOnClickListener(null);
        }else if (stepPos == 2){
            //SETP 3;
            ((TextView) findViewById(R.id.title_textView)).setText(medicineName);
            findViewById(R.id.medicine_imageView).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.medicine_imageView)).setImageResource(MedicineTypeAux.getMedicineTypeIcon(medicineType.getCode()));

            step_message_textView.setText(Html.fromHtml(getString(R.string.add_medicine_step3)));

            setp1Ind.setBackgroundResource(R.drawable.point_win_back);
            setp2Ind.setBackgroundResource(R.drawable.point_win_back);
            setp3Ind.setBackgroundResource(R.drawable.point_not_win_back);
            setp4Ind.setBackgroundResource(R.drawable.point_not_win_back);
            setp5Ind.setBackgroundResource(R.drawable.point_not_win_back);

            add_new_btn.setVisibility(View.GONE);
            add_new_btn.setOnClickListener(null);
        }else if (stepPos == 3){
            //SETP 4;
            step_message_textView.setText(Html.fromHtml(getString(R.string.add_medicine_step4)));

            setp1Ind.setBackgroundResource(R.drawable.point_win_back);
            setp2Ind.setBackgroundResource(R.drawable.point_win_back);
            setp3Ind.setBackgroundResource(R.drawable.point_win_back);
            setp4Ind.setBackgroundResource(R.drawable.point_not_win_back);
            setp5Ind.setBackgroundResource(R.drawable.point_not_win_back);

            add_new_btn.setVisibility(View.GONE);
            add_new_btn.setOnClickListener(null);

        }else if (stepPos == 4){
            //SETP 5;
            step_message_textView.setText(Html.fromHtml(getString(R.string.add_medicine_step5)));

            setp1Ind.setBackgroundResource(R.drawable.point_win_back);
            setp2Ind.setBackgroundResource(R.drawable.point_win_back);
            setp3Ind.setBackgroundResource(R.drawable.point_win_back);
            setp4Ind.setBackgroundResource(R.drawable.point_win_back);
            setp5Ind.setBackgroundResource(R.drawable.point_not_win_back);

            if(medicineToEdit==null) {
                add_new_btn.setVisibility(View.VISIBLE);
                add_new_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createMedicine(((AddMedicineStep5) step5).getNoteText(), true, true);
                    }
                });
            }
        }

//        progressBar.setProgress();


    }

    private void goToStep3() {

        if (step3 == null) {
            step3 = AddMedicineStep3.newInstance(medicineToEdit);
        }

        flipCard(step3,false);
    }

    private void goToStep4() {

        if (step4 == null) {
            step4 = AddMedicineStep4.newInstance(medicineType, medicineToEdit,medicineStartDate,medicineTotalDays);
        }else{
            if(step4 instanceof AddMedicineStep4) {
                ((AddMedicineStep4) step4).setNewValues(medicineStartDate,medicineTotalDays);
            }
        }

        flipCard(step4,false);
    }

    private void goToStep5() {

        if (step5 == null) {
            step5 = AddMedicineStep5.newInstance(medicineToEdit);
        }

        flipCard(step5,false);
    }

    @Override
    public void onGoBack() {
        goBack();
    }

    private void goBack() {
        if (getFragmentManager().getBackStackEntryCount()!=0) {
            getFragmentManager().popBackStack();
        }
    }

    public void flipCard(Fragment fragment, boolean first) {
        closeKeyboard();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if(!first){
            fragmentTransaction.setCustomAnimations(R.animator.card_flip_right_in, R.animator.card_flip_right_out, R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                    .replace(R.id.add_medicine_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }else{
            fragmentTransaction.replace(R.id.add_medicine_container, fragment)
                    .commit();
        }
    }

    private void closeKeyboard(){
        View viewFocused = this.getCurrentFocus();
        if (viewFocused != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewFocused.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void step1End(MedicineType medicineType, String medicineName, int dosage, String barCode) {
        this.medicineType = medicineType;
        this.medicineName = medicineName;
        this.medicineDosage = dosage;
        this.medicineBarCode = barCode;

        //goToStep2();
        goToNewStep2();
    }

    public void editMedicineStep1(UserNormalMedicine medicine){
        medicineToEdit = medicine;
    }


    @Override
    public void newStep2End(ArrayList<MedicineSchedule> listSchedule, int totalDosages, GregorianCalendar dateSelected, GregorianCalendar insertTimeNow, int totalDays, String comment, int totalSOSDosages) {

        this.medicineListSchedule = listSchedule;
        this.medicineStartDate = dateSelected;
        this.medicineTotalDays = totalDays;
        this.insertTimeNowDate = insertTimeNow;
        this.totalDosages = totalDosages;
        this.totalSOSDosages = totalSOSDosages;

        if (medicineToEdit!=null){
            medicineToEdit.setMedicineType(this.medicineType);
            medicineToEdit.setMedicineName(this.medicineName);

            editMedicine(comment);
        }else{
            createMedicine(comment, false, true);
        }
    }

    @Override
    public void step2End(boolean shareDoctor) {
        this.shareDoctor = shareDoctor;
        goToStep3();
    }

    @Override
    public void step3End(GregorianCalendar startDate, int totalDays) {
        this.medicineStartDate = startDate;
        this.medicineTotalDays = totalDays;

        goToStep4();
    }

    @Override
    public void step4End(ArrayList<MedicineTime> listTimes, GregorianCalendar insertTimeNowDate) {
        //this.medicineListTimes = listTimes;
        this.insertTimeNowDate = insertTimeNowDate;

        goToStep5();
    }

    @Override
    public void step5End(String comment) {
        if(medicineToEdit!=null){
            editMedicine(comment);
        }else {
            createMedicine(comment, false, true);
        }
    }

    private void editMedicine(String commnet){
        findViewById(R.id.creating_linearlayout).setVisibility(View.VISIBLE);

        medicineNote = commnet;

        new Thread(new Runnable() {
            public void run() {
                ArrayList<MedicineInhaler> inhalers = new ArrayList<>();

                if (MedicineTypeAux.needsDosesAndBarcode(medicineName, medicineType)) {
                    inhalers.add(new MedicineInhaler(true, medicineBarCode, totalDosages));
                }

                UserNormalMedicine auxUserMedicine = new UserNormalMedicine(medicineToEdit.getId(),
                        medicineType,
                        medicineName,
                        shareDoctor,
                        medicineStartDate,
                        medicineNote,
                        inhalers,
                        totalSOSDosages,
                        medicineTotalDays,
                        medicineListSchedule,
                        medicineToEdit.getNid());

                if (AppController.getmInstance().getMedicineDataSource().updateUserMedicine(AppController.getmInstance().getActiveUser().getId(), auxUserMedicine, insertTimeNowDate)) {
                    Utils.deleteAlarmForUserNormalMedicine(medicineToEdit, AddMedicineActivity.this);

                    medicineToEdit.setMedicineType(medicineType);
                    medicineToEdit.setMedicineName(medicineName);
                    medicineToEdit.setShareWithDoctor(shareDoctor);
                    medicineToEdit.setStartDate(medicineStartDate);
                    medicineToEdit.setNote(medicineNote);
                    medicineToEdit.setInhalers(inhalers);
                    medicineToEdit.setTotalSOSDosages(totalSOSDosages);
                    medicineToEdit.setDuration(medicineTotalDays);
                    medicineToEdit.setSchedules(medicineListSchedule);

                    if (AppController.getmInstance().getActiveUser().isPushOn()) {
                        Utils.scheduleAlarmForUserNormalMedicine(medicineToEdit, AddMedicineActivity.this);
                    }

                    AppController.getmInstance().forceSyncManual();

                    myFinish(false);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.creating_linearlayout).setVisibility(View.GONE);
                            Toast.makeText(AddMedicineActivity.this, R.string.error_update_medicine, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void createMedicine(String commnet, final boolean newMed, final boolean winPoints) {
        this.medicineNote = commnet;
        findViewById(R.id.creating_linearlayout).setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            public void run() {
                // a potentially  time consuming task
                ArrayList<MedicineInhaler> inhalers = new ArrayList<>();

                if(MedicineTypeAux.needsDosesAndBarcode(medicineName,medicineType)){
                    inhalers.add(new MedicineInhaler(true, medicineBarCode, totalDosages));
                }

                UserNormalMedicine auxUserMedicine = new UserNormalMedicine(medicineType,
                        medicineName,
                        shareDoctor,
                        medicineStartDate,
                        medicineNote,
                        inhalers,
                        totalSOSDosages,
                        medicineTotalDays,
                        medicineListSchedule,
                        "");

                final int medsBeforeInsert = AppController.getmInstance().getMedicineDataSource().getTotalMedsWithDeleted(AppController.getmInstance().getActiveUser().getId());

                boolean created = AppController.getmInstance().getMedicineDataSource().createUserMedicine(
                        AppController.getmInstance().getActiveUser().getId(),
                        auxUserMedicine, insertTimeNowDate, true, false, true);

                if(created) {
                    if(medicineToEdit==null){
                        Utils.createNavigationAction(getString(R.string.create_med_action));
                    }

                    if(AppController.getmInstance().getActiveUser().isPushOn()) {
                        Utils.scheduleAlarmForUserNormalMedicine(auxUserMedicine, AddMedicineActivity.this);
                    }

                    final Badge win = BadgesAux.verifyWinBagdeOne(medsBeforeInsert,AppController.getmInstance().getActiveUser().getUserBadges());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(win!=null){
                                UserBadge newUserBadge = AppController.getmInstance().createNewUserBadge(win);

                                if(newUserBadge!=null){
                                    Utils.showWinBadge(getSupportFragmentManager(), newUserBadge.getBadge(), new WinBadgeDialog.WinBadgeDialogListener() {
                                        @Override
                                        public void onDismiss() {
                                            updateUserPointsAndEnd(medsBeforeInsert,newMed, winPoints);
                                        }
                                    });
                                }
                            }else{
                                updateUserPointsAndEnd(medsBeforeInsert,newMed, winPoints);
                            }
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.creating_linearlayout).setVisibility(View.GONE);
                            Toast.makeText(AddMedicineActivity.this,R.string.error_create_medicine,Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }

    private void updateUserPointsAndEnd(int medsBeforeInsert,final boolean isNewMed, boolean winPoints){
        if(winPoints) {
            int pointwin = 1;

            if (medsBeforeInsert < 3) {
                pointwin = 30;
            }

            pointwin = pointwin * AppController.getmInstance().getExtraMultiplier();

            AppController.getmInstance().userWinPoints(pointwin, null, getSupportFragmentManager(), new OkErrorListener() {
                @Override
                public void ok() {
                    myFinish(isNewMed);
                }

                @Override
                public void error() {
                    myFinish(isNewMed);
                }
            });
        }else{
            myFinish(isNewMed);
        }

    }

    private void myFinish(boolean newMed) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                findViewById(R.id.creating_linearlayout).setVisibility(View.GONE);
                AppController.getmInstance().getMyBus().send(new MedicineCreatedEvent());
            }
        });


        if (newMed) {
            goToStep1(true);
        } else {
            finish();
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    Log.d("focus", "touchevent");
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
