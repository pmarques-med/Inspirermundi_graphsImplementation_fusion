package org.medida.inhalerdetection;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ExampleMainActivity extends Activity {

    static final int INHALER_DETECTION_REQUEST = 10;

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
                Intent intent = InhalerDetectionActivity.PrepareIntentInput(getBaseContext(), patientId, InhalerDetectionActivity.InhalerType.Flutiform, detectionTarget ,duration);
                startActivityForResult(intent, INHALER_DETECTION_REQUEST);
                //*********************************************************************************
            }

        });

        final Button diskusButton = (Button) findViewById(R.id.diskusButton);
        diskusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //*********************************************************************************
                //call activity
                Intent intent = InhalerDetectionActivity.PrepareIntentInput(getBaseContext(), patientId, InhalerDetectionActivity.InhalerType.Diskus, detectionTarget ,duration);
                startActivityForResult(intent, INHALER_DETECTION_REQUEST);
                //*********************************************************************************
            }

        });

        final Button elliptaButton = (Button) findViewById(R.id.elliptaButton);
        elliptaButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //*********************************************************************************
                //call activity
                Intent intent = InhalerDetectionActivity.PrepareIntentInput(getBaseContext(), patientId, InhalerDetectionActivity.InhalerType.Ellipta, detectionTarget ,duration);
                startActivityForResult(intent, INHALER_DETECTION_REQUEST);
                //*********************************************************************************
            }

        });


        final Button novolizerButton = (Button) findViewById(R.id.novolizerButton);
        novolizerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //*********************************************************************************
                //call activity
                Intent intent = InhalerDetectionActivity.PrepareIntentInput(getBaseContext(), patientId, InhalerDetectionActivity.InhalerType.Novoziler, detectionTarget ,duration);
                startActivityForResult(intent, INHALER_DETECTION_REQUEST);
                //*********************************************************************************
            }

        });


        final Button turbohalerButton = (Button) findViewById(R.id.turbohalerButton);
        turbohalerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //*********************************************************************************
                //call activity
                Intent intent = InhalerDetectionActivity.PrepareIntentInput(getBaseContext(), patientId, InhalerDetectionActivity.InhalerType.Turbohaler, detectionTarget ,duration);
                startActivityForResult(intent, INHALER_DETECTION_REQUEST);
                //*********************************************************************************
            }

        });

        final Button spiromaxButton = (Button) findViewById(R.id.spiromaxButton);
        spiromaxButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //*********************************************************************************
                //call activity
                Intent intent = InhalerDetectionActivity.PrepareIntentInput(getBaseContext(), patientId, InhalerDetectionActivity.InhalerType.Spiromax, detectionTarget ,duration);
                startActivityForResult(intent, INHALER_DETECTION_REQUEST);
                //*********************************************************************************
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
