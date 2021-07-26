package org.medida.inhalerdetection;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
/*import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;*/
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import static org.medida.inhalerdetection.InhalerDetectionActivity.EXTRA_INPUT_INHALER_TYPE_ENUM;
import static org.medida.inhalerdetection.R.drawable.default_instructions;
//import static org.medida.inhalerdetection.R.drawable.ellipta_instructions;
//import static org.medida.inhalerdetection.R.drawable.k_haler_instructions;
//import static org.medida.inhalerdetection.R.drawable.diskus_instructions;
//import static org.medida.inhalerdetection.R.drawable.spiromax_instructions;
//import static org.medida.inhalerdetection.R.drawable.mdi3m_instructions;
//import static org.medida.inhalerdetection.R.drawable.twisthaler_instructions;
//import static org.medida.inhalerdetection.R.drawable.turbohaler_instructions;
//import static org.medida.inhalerdetection.R.drawable.flutiform_instructions;
//import static org.medida.inhalerdetection.R.drawable.easyhaler_instructions;
//import static org.medida.inhalerdetection.R.drawable.nexthaler_instructions;
//import static org.medida.inhalerdetection.R.drawable.novolizer_instructions;
//import static org.medida.inhalerdetection.R.drawable.seretaide_instructions;



public class PreInstructionDetectionActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 300;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 400;
    private static final int MY_PERMISSIONS_REQUEST_BOTH = 500;
    private static final int POST_DETECTION_CODE = 100;

    private static final String TAG = "PreInstructionDetection";

    private boolean isCameraPermitted = true;
    private boolean isStoragePermitted = true;

    private AlertDialog dialog;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_instruction_detection);
        prefs = getSharedPreferences(InhalerDetectionActivity.INHALER_DETECTION_PREFERENCES, MODE_PRIVATE);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/morebi_rounded_regular.ttf");
        Button button = findViewById(R.id.continueButton);
        button.setText(R.string.continue_button_text);
        button.setTypeface(font);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(!CheckPermissions())
                    RequestPermissions();
                else
                    AdvanceToInhalerDetection();
            }
        });
        Intent intent = getIntent();
        InhalerDetectionActivity.InhalerType inhalerType = (InhalerDetectionActivity.InhalerType) intent.getSerializableExtra(EXTRA_INPUT_INHALER_TYPE_ENUM);
        TextView text = findViewById(R.id.instructionTextView);
        text.setText(getString(R.string.pre_rec_instructions));
        CheckBox checkBox = findViewById(R.id.instructionCheckBox);
        checkBox.setText(getString(R.string.pre_rec_checkbox_instructions));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean("doNotShowInstructions",b).apply();
            }
        });
        text.setTypeface(font);
        checkBox.setTypeface(font);
        int tutorialImg;
        switch(inhalerType)
        {
            case Unknown:
                tutorialImg = default_instructions;
                break;
            case KHaller:
                //tutorialImg = k_haler_instructions;
                tutorialImg = default_instructions;
                break;
            case Diskus:
               // tutorialImg = diskus_instructions;
                tutorialImg = default_instructions;
                break;
            case Ellipta:
               // tutorialImg = ellipta_instructions;
                tutorialImg = default_instructions;
                break;
            case Spiromax:
                tutorialImg = default_instructions;
                //tutorialImg = spiromax_instructions;
                break;
            case MDS3M:
                tutorialImg = default_instructions;
                //tutorialImg = mdi3m_instructions;
                break;
            case Twisthaler:
                //tutorialImg = twisthaler_instructions;
                tutorialImg = default_instructions;
                break;
            case Turbohaler:
                //tutorialImg = turbohaler_instructions;
                tutorialImg = default_instructions;
                break;
            case Flutiform:
                //tutorialImg = flutiform_instructions;
                tutorialImg = default_instructions;
                break;
            case Easyhaler:
                //tutorialImg = easyhaler_instructions;
                tutorialImg = default_instructions;
                break;
            case NextHaler:
                //tutorialImg = nexthaler_instructions;
                tutorialImg = default_instructions;
                break;
            case Novoziler:
                //tutorialImg = novolizer_instructions;
                tutorialImg = default_instructions;
                break;
            case Seretide:
                //tutorialImg = seretaide_instructions;
                tutorialImg = default_instructions;
                break;
            default:
                tutorialImg = default_instructions;
                break;
        }


        ImageView tutorialImageView = findViewById(R.id.inhalerTutorialImageView);
        Glide.with(this).load(tutorialImg).into(tutorialImageView);
        ImageView pet = findViewById(R.id.characterView);
        pet.bringToFront();
    }

    private void AdvanceToInhalerDetection()
    {
        Intent receivedIntent = getIntent();
        Intent newIntent = new Intent(getBaseContext(), PreDetectionActivity.class);
        Bundle oldBundle = receivedIntent.getExtras();
        newIntent.putExtras(oldBundle);
        startActivityForResult(newIntent,POST_DETECTION_CODE);
    }

    private boolean CheckPermissions() {
        if(android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1)
            return true;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            isCameraPermitted = false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            isStoragePermitted = false;
        }
        return isCameraPermitted && isStoragePermitted;
    }

    private void RequestPermissions()
    {
        String[] permissions = null;
        int requestCode = 0;
        if(isCameraPermitted && !isStoragePermitted)
        {
            permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestCode = MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
        }
        else if(!isCameraPermitted && isStoragePermitted)
        {
            permissions = new String[]{Manifest.permission.CAMERA};
            requestCode = MY_PERMISSIONS_REQUEST_CAMERA;
        }
        else
        {
            permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            requestCode = MY_PERMISSIONS_REQUEST_BOTH;
        }

        ActivityCompat.requestPermissions(this,
                permissions,
                requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isCameraPermitted = true;
            }
        }
        else if(requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isStoragePermitted = true;
            }
        }
        else if(requestCode == MY_PERMISSIONS_REQUEST_BOTH) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                isStoragePermitted = true;
                isCameraPermitted = true;
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if(isCameraPermitted && isStoragePermitted)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AdvanceToInhalerDetection();
                }
            });

        else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AlertDialog.Builder builder = new AlertDialog.Builder(PreInstructionDetectionActivity.this);

                    String message = getString(R.string.permission_error_text);

                    builder.setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.d(TAG, "User has acknowledged he hasn't given all required permissions");
                            RequestPermissions();
                        }
                    });

                    builder.setNegativeButton(getString(R.string.back_button_text), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            setResult(Activity.RESULT_CANCELED, intent);
                            finish();
                        }
                    });

                    builder.setMessage(message);
                    builder.setTitle(getString(R.string.permission_error_title));

                    dialog = builder.create();
                    dialog.show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == POST_DETECTION_CODE)
        {
            setResult(resultCode, data);
            finish();
        }
    }

}
