package com.bloomidea.inspirers;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.events.MedicineCreatedEvent;
import com.bloomidea.inspirers.model.HealthServices;
import com.bloomidea.inspirers.model.MedicineInhaler;
import com.bloomidea.inspirers.model.MedicineType;
import com.bloomidea.inspirers.model.TimelineItem;
import com.bloomidea.inspirers.model.UserMedicine;
import com.bloomidea.inspirers.model.UserSOSMedicine;
import com.bloomidea.inspirers.utils.HealthServicesAux;
import com.bloomidea.inspirers.utils.MedicineTypeAux;
import com.bloomidea.inspirers.utils.ProgressBarAnimation;
import com.bloomidea.inspirers.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class AddSOSMedicineActivity extends MyActiveActivity implements AddSOSMedicineNewStep1.OnFragmentInteractionListener, AddSOSMedicineNewStep2.OnFragmentInteractionListener, AddMedicineStep1.OnFragmentInteractionListener, AddMedicineStep2.OnFragmentInteractionListener, AddSOSMedicineStep3.OnFragmentInteractionListener, AddSOSMedicineStep4.OnFragmentInteractionListener{
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 666;
    private static final int LOCATION_SETTINGS_REQUEST = 777;

    public static final String EXTRA_MY_MEDS_EXTRA = "EXTRA_MY_MEDS_EXTRA";
    public static final String EXTRA_MY_MEDS_EXTRA_BOOL = "EXTRA_MY_MEDS_EXTRA_BOOL";

    private TextView setp1Ind;
    private TextView setp2Ind;
    private TextView setp3Ind;
    private TextView setp4Ind;

    private ProgressBar progressBar;
    private TextView step_message_textView;


    private Fragment step1;
    private Fragment step2;
    private Fragment step3;
    private Fragment step4;

    //Medicine Info - STEP1;
    private MedicineType medicineType;
    private String medicineName;
    private int medicineInhalerDosage;
    private String medicineBarCode;

    //Medicine Info - STEP2;
    private UserMedicine selectedMedicine;

    //Medicine Info - STEP3;
    private GregorianCalendar sosWhen;
    private int sosDosage;

    //Medicine Info - STEP4;
    private int severity;
    private String trigger;
    private String obs;
    private boolean shareWithDoctor;
    private HealthServices healthServices;

    private boolean openFromMed;

    private UserSOSMedicine lastSOSMedicine;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sosmedicine);

        selectedMedicine = (UserMedicine) getIntent().getSerializableExtra(EXTRA_MY_MEDS_EXTRA);
        openFromMed = getIntent().getBooleanExtra(EXTRA_MY_MEDS_EXTRA_BOOL, false);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        lastSOSMedicine = AppController.getmInstance().getMedicineDataSource().getLastInsertedSOSUserMedicine(AppController.getmInstance().getActiveUser().getId());

        progressBar = (ProgressBar) findViewById(R.id.level_progress_bar);

        step_message_textView = (TextView) findViewById(R.id.step_message_textView);

        setp1Ind = (TextView) findViewById(R.id.include).findViewById(R.id.textviewPoints);
        setp2Ind = (TextView) findViewById(R.id.include2).findViewById(R.id.textviewPoints);
        setp3Ind = (TextView) findViewById(R.id.include3).findViewById(R.id.textviewPoints);
        setp4Ind = (TextView) findViewById(R.id.include4).findViewById(R.id.textviewPoints);


        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ((TextView) findViewById(R.id.title_textView)).setText(R.string.add_sos);

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int c = getFragmentManager().getBackStackEntryCount();

                //stepTopConfig(c);
            }
        });

        if (openFromMed){
            if (step1 == null) {
                step1 = AddSOSMedicineNewStep2.newInstance(selectedMedicine);
            }else{
                ((AddSOSMedicineNewStep2) step1).updateMedicine(selectedMedicine);
            }
            flipCard(step1,true);
        }else{
            goToStep1();
        }

    }

    private void goToStep1(){
        boolean first =false;
        if (step1 == null) {
            step1 = AddSOSMedicineNewStep1.newInstance();
            first = true;
        }

        flipCard(step1,first);
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
            fragmentTransaction.add(R.id.add_medicine_container, fragment)
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

    private void stepTopConfig(int stepPos){
        //TextView aux = null;

        Log.d("STEP_POS",""+stepPos);
        ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, progressBar.getProgress(), 24+(25*stepPos));
        anim.setDuration(100);

        progressBar.startAnimation(anim);

        if(stepPos == 0){
            //SETP 1;
            ((TextView) findViewById(R.id.title_textView)).setText(R.string.add_sos);
            findViewById(R.id.medicine_imageView).setVisibility(View.GONE);

            step_message_textView.setText(Html.fromHtml(getString(R.string.add_medicine_sos_step1)));

            setp1Ind.setBackgroundResource(R.drawable.point_not_win_back);
            setp2Ind.setBackgroundResource(R.drawable.point_not_win_back);
            setp3Ind.setBackgroundResource(R.drawable.point_not_win_back);
            setp4Ind.setBackgroundResource(R.drawable.point_not_win_back);
        }else if (stepPos == 1){
            //SETP 2;

            ((TextView) findViewById(R.id.title_textView)).setText(R.string.add_sos);
            findViewById(R.id.medicine_imageView).setVisibility(View.GONE);


            step_message_textView.setText(Html.fromHtml(getString(R.string.add_medicine_sos_step2)));

            setp1Ind.setBackgroundResource(R.drawable.point_win_back);
            setp2Ind.setBackgroundResource(R.drawable.point_not_win_back);
            setp3Ind.setBackgroundResource(R.drawable.point_not_win_back);
            setp4Ind.setBackgroundResource(R.drawable.point_not_win_back);

        }else if (stepPos == 2){
            //SETP 3;
            ((TextView) findViewById(R.id.title_textView)).setText(medicineName);
            findViewById(R.id.medicine_imageView).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.medicine_imageView)).setImageResource(MedicineTypeAux.getMedicineTypeIcon(medicineType.getCode()));

            step_message_textView.setText(Html.fromHtml(getString(R.string.add_medicine_sos_step3)));

            setp1Ind.setBackgroundResource(R.drawable.point_win_back);
            setp2Ind.setBackgroundResource(R.drawable.point_win_back);
            setp3Ind.setBackgroundResource(R.drawable.point_not_win_back);
            setp4Ind.setBackgroundResource(R.drawable.point_not_win_back);
        }else if (stepPos == 3){
            //SETP 4;
            step_message_textView.setText(Html.fromHtml(getString(R.string.add_medicine_sos_step4)));

            setp1Ind.setBackgroundResource(R.drawable.point_win_back);
            setp2Ind.setBackgroundResource(R.drawable.point_win_back);
            setp3Ind.setBackgroundResource(R.drawable.point_win_back);
            setp4Ind.setBackgroundResource(R.drawable.point_not_win_back);

        }

//        progressBar.setProgress();


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
        this.medicineInhalerDosage = dosage;
        this.medicineBarCode = barCode;

    }

    private void goToNewStep2() {
        if (step2 == null) {
            step2 = AddSOSMedicineNewStep2.newInstance(selectedMedicine);
        }else{
            ((AddSOSMedicineNewStep2) step2).updateMedicine(selectedMedicine);
        }
        flipCard(step2,false);
    }

    private void goToStep3() {
        if (step3 == null) {
            step3 = AddSOSMedicineStep3.newInstance(medicineType);
        }

        flipCard(step3,false);
    }

    private void goToStep4() {
        if (step4 == null) {
            step4 = AddSOSMedicineStep4.newInstance();
        }

        flipCard(step4,false);
    }

    @Override
    public void stepSOS1End(UserMedicine medicineSelected) {
        this.selectedMedicine = medicineSelected;

        goToNewStep2();
    }

    @Override
    public void stepSOS2End(UserMedicine selectedMed, GregorianCalendar dateTimeSelected, int totalDoses, String notes) {
        this.sosWhen = dateTimeSelected;
        this.sosDosage = totalDoses;
        createsSOSMedicine(0,null,notes,true, new HealthServices(HealthServicesAux.HEALTH_CODE1, getResources().getString(R.string.health_code_1)));
    }

    @Override
    public void step2End(boolean shareDoctor) {
        goToStep3();
    }

    private void goBack() {
        if (getFragmentManager().getBackStackEntryCount()!=0) {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onGoBack() {
        goBack();
    }

    @Override
    public void step3SOSEnd(GregorianCalendar when, int dosage) {
        this.sosWhen = when;
        this.sosDosage = dosage;

        goToStep4();
    }

    @Override
    public void stepSOS4End(int severity, String trigger, String obs, boolean shareWithDoctor, HealthServices healthServices) {
        createsSOSMedicine(severity,trigger,obs,shareWithDoctor,healthServices);
    }

    private void createsSOSMedicine(int severity, String trigger, String obs, boolean shareWithDoctor, HealthServices healthServices){
        this.severity = severity;
        this.trigger = trigger;
        this.obs = obs;
        this.shareWithDoctor = shareWithDoctor;
        this.healthServices = healthServices;

        findViewById(R.id.creating_linearlayout).setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            public void run() {
                ArrayList<MedicineInhaler> inhalers = new ArrayList<>();

                //if(MedicineTypeAux.needsDosesAndBarcode(medicineName,medicineType)){
                //    inhalers.add(new MedicineInhaler(true, medicineBarCode, medicineInhalerDosage));
                //}

                UserSOSMedicine auxUserMedicine= new  UserSOSMedicine(selectedMedicine.getMedicineType(),
                        selectedMedicine.getMedicineName(),
                        AddSOSMedicineActivity.this.shareWithDoctor,
                        sosWhen,
                        AddSOSMedicineActivity.this.obs,
                        inhalers,
                        selectedMedicine.getTotalSOSDosages(),
                        AddSOSMedicineActivity.this.severity,
                        AddSOSMedicineActivity.this.trigger,
                        AddSOSMedicineActivity.this.healthServices,
                        sosDosage,
                        "");

                TimelineItem createdItem = AppController.getmInstance().getMedicineDataSource().createSOSUserMedicine(
                        AppController.getmInstance().getActiveUser().getId(),
                        auxUserMedicine,true,false,true);

                if(createdItem!=null) {
                    Utils.createNavigationAction(getString(R.string.create_sos_action));
                    AppController.getmInstance().addTimelineItemNeedLocation(createdItem);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getLastLocation();

                            AppController.getmInstance().getMyBus().send(new MedicineCreatedEvent());

                            Toast.makeText(AddSOSMedicineActivity.this,R.string.ok_create_medicine,Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.creating_linearlayout).setVisibility(View.GONE);
                            Toast.makeText(AddSOSMedicineActivity.this,R.string.error_create_medicine,Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }).start();
    }

    private void getLastLocation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }else{
            if(checkLocationEnable()) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    AppController.getmInstance().updateAllTimelineItemLocation(location);
                                }
                            }
                        });


                finish();
            }
        }
    }

    private boolean checkLocationEnable() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(myIntent,LOCATION_SETTINGS_REQUEST);
                }
            });
            dialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    finish();
                }
            });
            dialog.show();
        }

        return gps_enabled || network_enabled;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }else{
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    getLastLocation();
                    /*Utils.showMessageAlertDialog(this, getString(R.string.location_access_title), getString(R.string.location_access_explanation), new AlertDialogListener() {
                        @Override
                        public void okClick() {
                            getLastLocation();
                        }
                    });*/
                }else{
                    AppController.getmInstance().forceSyncManual();
                    finish();
                }
            }
        }else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOCATION_SETTINGS_REQUEST){
            getLastLocation();
        }else {
            super.onActivityResult(requestCode, resultCode, data);
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
