package org.medida.inhalerdetection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.medida.inhalerdetection.ExampleMainActivity.INHALER_DETECTION_REQUEST;
import static org.medida.inhalerdetection.InhalerDetectionActivity.EXTRA_IMAGE_NAME;
import static org.medida.inhalerdetection.InhalerDetectionActivity.EXTRA_RESULT_DOSAGE_COUNT_INT;

public class PostDetectionActivity extends Activity {

    int dosage_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detection);

        Button retryButton = findViewById(R.id.retryButton);
        Intent receivedIntent = getIntent();
        dosage_count = receivedIntent.getIntExtra(EXTRA_RESULT_DOSAGE_COUNT_INT,0);
        String imageName = receivedIntent.getStringExtra(EXTRA_IMAGE_NAME);
        //final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/frames/"+receivedIntent.getStringExtra(EXTRA_IMAGE_NAME));
        final File file = new File(getExternalFilesDir(null).getAbsolutePath()+"/frames/"+receivedIntent.getStringExtra(EXTRA_IMAGE_NAME));



        if(imageName != null && !imageName.isEmpty()){
            ImageView imageResult = findViewById(R.id.imageResult);
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            Matrix matrix = new Matrix();
            //matrix.postRotate(90);
            //Bitmap rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            //imageResult.setImageBitmap(rotated);

            //PedroimageResult.setVisibility(View.VISIBLE);

            int rotationDegree =90;

            try {
                BufferedInputStream bufferedInputStream;
                InputStream is = (InputStream) new URL("https://users.med.up.pt/~pmarques/validaimagens/1576084148017_spiromax_detected_@1_@3.0@_@12.0@_1576084152747_40_user1.png").getContent();
                bufferedInputStream = new BufferedInputStream(is);

                bmp = BitmapFactory.decodeStream(bufferedInputStream);
                imageResult.setImageBitmap(bmp);
                InputImage image = InputImage.fromBitmap(bmp, rotationDegree);

                TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

                Task<Text> result =
                        recognizer.process(image)
                                .addOnSuccessListener(new OnSuccessListener<Text>() {
                                    @Override
                                    public void onSuccess(Text visionText) {
                                        // Task completed successfully
                                        // ...
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                            }
                                        });

            } catch (IOException e) {
                e.printStackTrace();
            }


        }


        TextView resultText = findViewById(R.id.resultTextView);
        TextView resultSymbol = findViewById(R.id.resultSymbol);
        boolean success = receivedIntent.getBooleanExtra(InhalerDetectionActivity.EXTRA_RESULT_SUCCESS, false);
        if(success){
            resultText.setText(getString(R.string.success_message));
            resultSymbol.setText("✔");
            resultSymbol.setTextColor(Color.parseColor("#00FF00"));
            TextView instructionTextView = findViewById(R.id.instructionTextView);
            instructionTextView.setVisibility(View.VISIBLE);
            instructionTextView.setText(R.string.confirm_doses);
            findViewById(R.id.dosesLayout).setVisibility(View.VISIBLE);
        }
        else{
         //   resultSymbol.setText("❌");
            resultSymbol.setText("!"); //Rute
            // resultSymbol.setTextColor(Color.parseColor("#FF0000"));
            resultSymbol.setTextColor(Color.parseColor("#0000FF")); // new color Rute
            resultText.setText(getString(R.string.fail_message));
            // HACK FOR TESTS
            TextView instructionTextView = findViewById(R.id.instructionTextView);
            instructionTextView.setVisibility(View.VISIBLE);
            instructionTextView.setText(R.string.confirm_doses);
            retryButton.setVisibility(View.VISIBLE);
            findViewById(R.id.dosesLayout).setVisibility(View.VISIBLE);
        }



        View root = resultText.getRootView();
        root.setBackgroundColor(getResources().getColor(android.R.color.white));

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/morebi_rounded_regular.ttf");
        resultText.setTypeface(font);

        final EditText dosesEditText = findViewById(R.id.dosesEditText);
        dosesEditText.setText(""+dosage_count);
        dosesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0){
                    int new_dosage_count = Integer.parseInt(s.toString());
                    if(new_dosage_count < 0) new_dosage_count = 0;
                    dosage_count = new_dosage_count;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button plusButton = findViewById(R.id.dosesPlusButton);
        plusButton.setTypeface(font);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dosage_count ++;
                dosesEditText.setText(""+dosage_count);
            }
        });

        Button minusButton = findViewById(R.id.dosesMinusButton);
        minusButton.setTypeface(font);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dosage_count > 0) {
                    dosage_count--;
                    dosesEditText.setText("" + dosage_count);
                }
            }
        });

        Button okButton = findViewById(R.id.continueButton);
        okButton.setText(R.string.ok_text);
        okButton.setTypeface(font);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                file.renameTo(new File(file.getAbsolutePath()+"_d"+dosage_count));
                Intent receivedIntent = getIntent();
                Intent newIntent = new Intent(getBaseContext(), InhalerDetectionActivity.class);
                newIntent.putExtras(receivedIntent);
                setResult(Activity.RESULT_OK, newIntent);
                finish();
            }
        });
        retryButton.setText(R.string.retry_text);
        retryButton.setTypeface(font);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent receivedIntent = getIntent();
                Intent newIntent = new Intent(getBaseContext(), PreDetectionActivity.class);
                newIntent.putExtras(receivedIntent);
                startActivityForResult(newIntent,100);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 100)
        {
            setResult(resultCode, data);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
    }


}
