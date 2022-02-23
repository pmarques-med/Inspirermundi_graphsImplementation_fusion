package org.medida.inhalerdetection;

import static org.medida.inhalerdetection.PostDetectionActivity.mostCommon;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.*;


import com.google.mlkit.vision.common.InputImage;
//import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

public class ExampleMainActivity extends Activity {

    static final int INHALER_DETECTION_REQUEST = 10;

    //SOFIA:
    private List<String> mLines;
    private Integer n = 0;
    private Bitmap mSelectedImage;
    ArrayList<Integer> listFive = new ArrayList<Integer>();
    private int mostCommonDetected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int duration = 15;
        final int detectionTarget = 1;
        final int patientId = 123;

        final Button flutiformButton = (Button) findViewById(R.id.flutiformButton);
        flutiformButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //*********************************************************************************
                //call activity
                //Intent intent = InhalerDetectionActivity.PrepareIntentInput(getBaseContext(), patientId, InhalerDetectionActivity.InhalerType.Flutiform, detectionTarget ,duration);
                //startActivityForResult(intent, INHALER_DETECTION_REQUEST);
                //*********************************************************************************

                //SOFIA:
                Context context = getApplicationContext();
                String imgURL = "https://users.med.up.pt/~pmarques/validaimagens/" + mLines.get(n);
                mSelectedImage = getImageFromRepository(imgURL);
                System.out.println("url" + imgURL);
                if (mSelectedImage != null) {

                    TextRecognitionActivity TRA = new TextRecognitionActivity();
                    TRA.recognizeText(mSelectedImage, flutiformButton);

                    //Sistema de Votação:
                    if(listFive.size() < 5 && listFive.size() != 0) {
                        mostCommonDetected = mostCommon(listFive);
                        //System.out.println(mostCommon(listFive));
                    }else if(listFive.size() == 5){
                        mostCommonDetected = mostCommon(listFive);
                        //System.out.println(mostCommon(listFive));
                        listFive.clear();
                    }

                } else {
                    System.out.println("URL errado :" + imgURL);
                }

                n++;

            }

        });

        final Button diskusButton = (Button) findViewById(R.id.diskusButton);
        diskusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //*********************************************************************************
                //call activity
                //Intent intent = InhalerDetectionActivity.PrepareIntentInput(getBaseContext(), patientId, InhalerDetectionActivity.InhalerType.Diskus, detectionTarget ,duration);
                //startActivityForResult(intent, INHALER_DETECTION_REQUEST);
                //*********************************************************************************
                String imgURL = "https://users.med.up.pt/~pmarques/validaimagens/" + mLines.get(n);
                mSelectedImage = getImageFromRepository(imgURL);
                System.out.println("url" + imgURL);
                if (mSelectedImage != null) {

                    TextRecognitionActivity TRA = new TextRecognitionActivity();
                    TRA.recognizeText(mSelectedImage, diskusButton);

                    //Sistema de Votação:
                    if(listFive.size() < 5 && listFive.size() != 0) {
                        mostCommonDetected = mostCommon(listFive);
                        //System.out.println(mostCommon(listFive));
                    }else if(listFive.size() == 5){
                        mostCommonDetected = mostCommon(listFive);
                        //System.out.println(mostCommon(listFive));
                        listFive.clear();
                    }

                } else {
                    System.out.println("URL errado :" + imgURL);
                }

                n++;



            }

        });

        final Button elliptaButton = (Button) findViewById(R.id.elliptaButton);
        elliptaButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //*********************************************************************************
                //call activity
                //Intent intent = InhalerDetectionActivity.PrepareIntentInput(getBaseContext(), patientId, InhalerDetectionActivity.InhalerType.Ellipta, detectionTarget ,duration);
                //startActivityForResult(intent, INHALER_DETECTION_REQUEST);
                //*********************************************************************************

                String imgURL = "https://users.med.up.pt/~pmarques/validaimagens/" + mLines.get(n);
                mSelectedImage = getImageFromRepository(imgURL);
                System.out.println("url" + imgURL);
                if (mSelectedImage != null) {

                    TextRecognitionActivity TRA = new TextRecognitionActivity();
                    TRA.recognizeText(mSelectedImage, elliptaButton);

                    //Sistema de Votação:
                    if(listFive.size() < 5 && listFive.size() != 0) {
                        mostCommonDetected = mostCommon(listFive);
                        //System.out.println(mostCommon(listFive));
                    }else if(listFive.size() == 5){
                        mostCommonDetected = mostCommon(listFive);
                        //System.out.println(mostCommon(listFive));
                        listFive.clear();
                    }

                } else {
                    System.out.println("URL errado :" + imgURL);
                }

                n++;

            }

        });

        final Button novolizerButton = (Button) findViewById(R.id.novolizerButton);
        novolizerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //*********************************************************************************
                //call activity
                //Intent intent = InhalerDetectionActivity.PrepareIntentInput(getBaseContext(), patientId, InhalerDetectionActivity.InhalerType.Novoziler, detectionTarget ,duration);
                //startActivityForResult(intent, INHALER_DETECTION_REQUEST);
                //*********************************************************************************
                Context context = getApplicationContext();
                InputStream is = null;
                try {
                    is = context.getAssets().open("novolizer" + "/" + "lista_" + "novolizer" + ".txt");
                    mLines = readLine(is, context);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                String imgURL = "https://users.med.up.pt/~pmarques/validaimagens/" + mLines.get(n);
                mSelectedImage = getImageFromRepository(imgURL);
                System.out.println("url" + imgURL);
                if (mSelectedImage != null) {

                    TextRecognitionActivity TRA = new TextRecognitionActivity();
                    TRA.recognizeText(mSelectedImage, novolizerButton);

                    //Sistema de Votação:
                    if(listFive.size() < 5 && listFive.size() != 0) {
                        mostCommonDetected = mostCommon(listFive);
                        //System.out.println(mostCommon(listFive));
                    }else if(listFive.size() == 5){
                        mostCommonDetected = mostCommon(listFive);
                        //System.out.println(mostCommon(listFive));
                        listFive.clear();
                    }

                } else {
                    System.out.println("URL errado :" + imgURL);
                }

                n++;

            }

        });



        final Button turbohalerButton = (Button) findViewById(R.id.turbohalerButton);
        turbohalerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //*********************************************************************************
                //call activity
                //Intent intent = InhalerDetectionActivity.PrepareIntentInput(getBaseContext(), patientId, InhalerDetectionActivity.InhalerType.Turbohaler, detectionTarget ,duration);
                //startActivityForResult(intent, INHALER_DETECTION_REQUEST);
                //*********************************************************************************
                String imgURL = "https://users.med.up.pt/~pmarques/validaimagens/" + mLines.get(n);
                mSelectedImage = getImageFromRepository(imgURL);
                System.out.println("url" + imgURL);
                if (mSelectedImage != null) {

                    TextRecognitionActivity TRA = new TextRecognitionActivity();
                    TRA.recognizeText(mSelectedImage, turbohalerButton);

                    //Sistema de Votação:
                    if(listFive.size() < 5 && listFive.size() != 0) {
                        mostCommonDetected = mostCommon(listFive);
                        //System.out.println(mostCommon(listFive));
                    }else if(listFive.size() == 5){
                        mostCommonDetected = mostCommon(listFive);
                        //System.out.println(mostCommon(listFive));
                        listFive.clear();
                    }

                } else {
                    System.out.println("URL errado :" + imgURL);
                }

                n++;

            }

        });

        final Button spiromaxButton = (Button) findViewById(R.id.spiromaxButton);
        spiromaxButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //*********************************************************************************
                //call activity
                //Intent intent = InhalerDetectionActivity.PrepareIntentInput(getBaseContext(), patientId, InhalerDetectionActivity.InhalerType.Spiromax, detectionTarget ,duration);
                //startActivityForResult(intent, INHALER_DETECTION_REQUEST);
                //*********************************************************************************
                String imgURL = "https://users.med.up.pt/~pmarques/validaimagens/" + mLines.get(n);
                mSelectedImage = getImageFromRepository(imgURL);
                System.out.println("url" + imgURL);
                if (mSelectedImage != null) {

                    TextRecognitionActivity TRA = new TextRecognitionActivity();
                    TRA.recognizeText(mSelectedImage, spiromaxButton);

                    //Sistema de Votação:
                    if(listFive.size() < 5 && listFive.size() != 0) {
                        mostCommonDetected = mostCommon(listFive);
                        //System.out.println(mostCommon(listFive));
                    }else if(listFive.size() == 5){
                        mostCommonDetected = mostCommon(listFive);
                        //System.out.println(mostCommon(listFive));
                        listFive.clear();
                    }

                } else {
                    System.out.println("URL errado :" + imgURL);
                }

                n++;


            }

        });


        final Button medicationButton = (Button) findViewById(R.id.medicationButton);
        medicationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //*********************************************************************************
                //call activity
                Intent intent = InhalerDetectionActivity.PrepareIntentInput(getBaseContext(), patientId, InhalerDetectionActivity.InhalerType.Unknown, detectionTarget ,duration);
                startActivityForResult(intent, INHALER_DETECTION_REQUEST);
                //*********************************************************************************
            }

        });
    }

    protected Bitmap getImageFromRepository(String... URL) {
        String imageURL = URL[0];
        Bitmap bitmap = null;
        try {
            // Download Image from URL
            InputStream input = new java.net.URL(imageURL).openStream();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return bitmap;
    }

    public List<String> readLine(InputStream is, Context context){
        List<String> mLines = new ArrayList<>();
        AssetManager am = context.getAssets();

        try {
            //InputStream is = am.open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = reader.readLine()) != null) {
                mLines.add(line);
                //System.out.println("nova linha do ficheiro" + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mLines;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == INHALER_DETECTION_REQUEST)
        {
            switch(resultCode)
            {
                case RESULT_OK:
                    int detectionCount = data.getIntExtra(InhalerDetectionActivity.EXTRA_RESULT_DETECTION_COUNT_INT, 0);
                    int dosageCount = data.getIntExtra(InhalerDetectionActivity.EXTRA_RESULT_DOSAGE_COUNT_INT, 0);
                    //Toast.makeText(this, "Result is " + result, Toast.LENGTH_LONG);
                    Log.d("MAIN CLASS", "Inhaler detected " + detectionCount + " times, with dosage count " + dosageCount);
                    break;

                case RESULT_CANCELED:
                    //if this extra is set, that means the user tried to detect the inhaler for the maximum time allowed, but failed to
                    //get enough positive detections
                    if(data == null)
                    {
                        //either the user (via back button) or an error force terminated the activity
                    }
                    else if(data.hasExtra(InhalerDetectionActivity.EXTRA_RESULT_DETECTION_COUNT_INT))
                    {
                        //or the user gracefully exited the activity after failure to detect the inhaler
                    }
                    else
                    {
                        //dont know how any other result could happen
                    }

                default:
                    break;
            }
        }
    }

}
