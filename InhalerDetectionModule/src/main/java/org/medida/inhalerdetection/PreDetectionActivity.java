package org.medida.inhalerdetection;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;

import org.opencv.android.JavaCameraView;

import java.io.InputStream;

import static org.medida.inhalerdetection.InhalerDetectionActivity.EXTRA_IMAGE_NAME;
import static org.medida.inhalerdetection.InhalerDetectionActivity.EXTRA_INPUT_INHALER_TYPE_ENUM;
import static org.medida.inhalerdetection.InhalerDetectionActivity.EXTRA_INPUT_PATIENT_ID;
import static org.medida.inhalerdetection.InhalerDetectionActivity.EXTRA_INTERNAL_FINAL_MESSAGE;
import static org.medida.inhalerdetection.InhalerDetectionActivity.EXTRA_RESULT_DETECTION_COUNT_INT;
import static org.medida.inhalerdetection.InhalerDetectionActivity.EXTRA_RESULT_DOSAGE_COUNT_INT;
import static org.medida.inhalerdetection.InhalerDetectionActivity.EXTRA_RESULT_SUCCESS;
import static org.medida.inhalerdetection.InhalerDetectionActivity.InhalerType;

public class PreDetectionActivity extends Activity implements DetectionActivity {

    final int DETECTION_CODE = 100;

    private static final String TAG = "OCVSample::Activity";

    private JavaCameraView mOpenCvCameraView;
    private final long activityTimeStamp = System.currentTimeMillis();

    private static final int POST_DETECTION_CODE = 100;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 300;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 400;
    private static final int MY_PERMISSIONS_REQUEST_BOTH = 500;

    private boolean isCameraPermitted = true;
    private boolean isStoragePermitted = true;

    private int switchPeriod = 1500; //in milisseconds

    private boolean finished = false;

    private AlertDialog dialog;

    InhalerType inhalerType;
    Long patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inhalerType = (InhalerType) getIntent().getSerializableExtra(EXTRA_INPUT_INHALER_TYPE_ENUM);
        patientId = getIntent().getLongExtra(EXTRA_INPUT_PATIENT_ID, 0);

        setContentView(R.layout.activity_pre_detection);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/morebi_rounded_regular.ttf");
        Button startButton = findViewById(R.id.startButton);
        startButton.setText(R.string.start_button_text);
        startButton.setTypeface(font);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(!CheckPermissions())
                    RequestPermissions();
                else
                    AdvanceToInhalerDetection();

            }
        });

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setText(R.string.cancel_button_text);
        cancelButton.setTypeface(font);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                finish();
            }
        });

        TextView text = findViewById(R.id.instructionTextView);
        text.setText(getString(R.string.detect_remanining_doses));
        text.setTypeface(font);

        // MLKIT

        /*
        int rotationDegree = 0;
        Bitmap bitmap = null;
        try {
            // Download Image from URL
            InputStream input = new java.net.URL("https://users.med.up.pt/~pmarques/validaimagens/1576084148017_spiromax_detected_@1_@3.0@_@12.0@_1576084152747_40_user1.png").openStream();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input);
            InputImage image = InputImage.fromBitmap(bitmap, rotationDegree);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            Task<Text> result =
                    recognizer.process(image)
                            .addOnSuccessListener(new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(Text visionText) {
                                    // Task completed successfully
                                    // ..
                                    System.out.println(visionText.getText());
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...
                                            System.out.println("No text detected");
                                        }
                                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


*/

        // MLKIT


        InitView();
    }

    private void AdvanceToInhalerDetection()
    {
        Intent receivedIntent = getIntent();
        Intent newIntent = new Intent(getBaseContext(), InhalerDetectionActivity.class);
        Bundle oldBundle = receivedIntent.getExtras();
        newIntent.putExtras(oldBundle);
        startActivityForResult(newIntent, DETECTION_CODE);
    }

    @Override
    public long getPatientId() {
        return patientId;
    }

    @Override
    public long getActivityTimeStamp() {
        return activityTimeStamp;
    }

    public void AdvanceToPostDetectionActivity(String imageName){
        Log.d(TAG, "Advancing to PostDetectionActivity");
        Intent intent = new Intent(this, PostDetectionActivity.class);
        intent.putExtra(EXTRA_RESULT_DETECTION_COUNT_INT, 1);
        intent.putExtra(EXTRA_RESULT_DOSAGE_COUNT_INT, 0);
        intent.putExtra(EXTRA_RESULT_SUCCESS, true);
        int msgId = R.string.success_message;
        intent.putExtra(EXTRA_INTERNAL_FINAL_MESSAGE, msgId);
        intent.putExtra(EXTRA_IMAGE_NAME, imageName);
        startActivityForResult(intent,POST_DETECTION_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == POST_DETECTION_CODE)
        {
            setResult(resultCode, data);
            finish();
        }
    }

    private void InitView()
    {
        mOpenCvCameraView = findViewById(R.id.openCVvCamera);
        mOpenCvCameraView.setCvCameraViewListener(new OpenCVCameraListener(this, inhalerType));
        mOpenCvCameraView.enableView();
    }

    private void CreatePermissionsRequestWarning()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(PreDetectionActivity.this);
                String message = getString(R.string.permission_request_text);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, getString(R.string.permission_request_title));
                        RequestPermissions();
                    }
                });
                builder.setMessage(message);
                builder.setTitle("Pedido de permissões");
                dialog = builder.create();
                dialog.show();
            }
        });

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


                    AlertDialog.Builder builder = new AlertDialog.Builder(PreDetectionActivity.this);

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
}
