package org.medida.inhalerdetection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static org.medida.inhalerdetection.ExampleMainActivity.INHALER_DETECTION_REQUEST;
import static org.medida.inhalerdetection.InhalerDetectionActivity.EXTRA_IMAGE_NAME;
import static org.medida.inhalerdetection.InhalerDetectionActivity.EXTRA_RESULT_DOSAGE_COUNT_INT;

public class PostDetectionActivity extends Activity {

    int dosage_count;
    private AsyncTask mMyTask;

    private static final String    TAG = "OCVSample::Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detection);

        Button retryButton = findViewById(R.id.retryButton);
        Intent receivedIntent = getIntent();
        dosage_count = receivedIntent.getIntExtra(EXTRA_RESULT_DOSAGE_COUNT_INT,0);
        String imageName = receivedIntent.getStringExtra(EXTRA_IMAGE_NAME);
        ImageView image = (ImageView) findViewById(R.id.imageResult);

        int imageResource = getResources().getIdentifier("imagem_teste", "drawable", getPackageName());

        Bitmap bmp = OpenBitmap("croppedFrame.jpg");
        /*Bundle extras = receivedIntent.getExtras();
        byte[] byteArray = extras.getByteArray("croppedimage");

        Bitmap bmp = null;

        if(byteArray!=null)
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
*/

        if(bmp!=null){
            image.setImageBitmap(bmp);
            //image.setImageResource(imageResource);
            runTextRecognition(bmp);
        }
        else{
            image.setImageResource(imageResource);
        }


        //final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/frames/"+receivedIntent.getStringExtra(EXTRA_IMAGE_NAME));
        final File file = new File(getExternalFilesDir(null).getAbsolutePath()+"/frames/"+receivedIntent.getStringExtra(EXTRA_IMAGE_NAME));



        if(imageName != null && !imageName.isEmpty()){

            ImageView imageResult = findViewById(R.id.imageResult);
            //Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            Matrix matrix = new Matrix();

            //matrix.postRotate(90);
            //Bitmap rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

            int rotationDegree =90;

            //imageResult.setImageBitmap(imageResource);
            //imageResult.setImageResource(imageResource);

            /*Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.imagem_teste);
            InputImage image = InputImage.fromBitmap(bmp, rotationDegree);
            */

            imageResult.setVisibility(View.VISIBLE);

        }


        TextView resultText = findViewById(R.id.resultTextView);
        TextView resultSymbol = findViewById(R.id.resultSymbol);
        boolean success = receivedIntent.getBooleanExtra(InhalerDetectionActivity.EXTRA_RESULT_SUCCESS, false);

        //String aaa = result.getResult().getText();

        //resultText.setText(aaa);
        
        if(success){
            //resultText.setText(getString(R.string.success_message));
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
        //dosesEditText.setText(""+dosage_count);
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
                /*Intent receivedIntent = getIntent();
                Intent newIntent = new Intent(getBaseContext(), PreDetectionActivity.class);
                newIntent.putExtras(receivedIntent);
                startActivityForResult(newIntent,100);*/
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

    public Bitmap OpenBitmap(String name){
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            File file = new File(this.getApplicationContext().getFilesDir() +"/frames"+ "/" + name);
            if(file.exists()) {
                try {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(this.getApplicationContext().getFilesDir() +"/frames"+ "/" + name), null, options);
                    //bitmap = BitmapFactory.decodeFile(this.getApplicationContext().getFilesDir() +"frames"+ "/" + name, options);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Custom method to convert string to url
    protected URL stringToURL(String urlString){
        try{
            URL url = new URL(urlString);
            return url;
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }

    private void runTextRecognition(Bitmap bmp) {
        int rotationDegree =0;
        //Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.imagem_teste);
        InputImage image = InputImage.fromBitmap(bmp, rotationDegree);

        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        TextView resultText = findViewById(R.id.dosesEditText);
        resultText.setText("");

        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                // Task completed successfully
                                // ...
                                for (Text.TextBlock block : visionText.getTextBlocks()) {
                                    //Rect boundingBox = block.getBoundingBox();
                                    //Point[] cornerPoints = block.getCornerPoints();
                                    String text = block.getText();

                                    for (Text.Line line: block.getLines()) {
                                        // ...
                                        for (Text.Element element: line.getElements()) {
                                            TextView resultText = findViewById(R.id.dosesEditText);
                                            resultText.append("|"+element.getText());
                                            Log.d(TAG, "InhalerDetection - " + ">"+element.getText()+"<");

                                        }
                                    }
                                }
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




    }



    private class DownloadTask extends AsyncTask<URL,Void,Bitmap> {

        ImageView aaa;

        public DownloadTask(ImageView imageResult) {
            aaa=imageResult;
        }

        // Before the tasks execution
        protected void onPreExecute(){
            // Display the progress dialog on async task start
            //mProgressDialog.show();
        }

        // Do the task in background/non UI thread
        protected Bitmap doInBackground(URL...urls){
            URL url = urls[0];
            HttpURLConnection connection = null;

            try{
                // Initialize a new http url connection
                connection = (HttpURLConnection) url.openConnection();

                // Connect the http url connection
                connection.connect();

                // Get the input stream from http url connection
                InputStream inputStream = connection.getInputStream();

                /*
                    BufferedInputStream
                        A BufferedInputStream adds functionality to another input stream-namely,
                        the ability to buffer the input and to support the mark and reset methods.
                */
                /*
                    BufferedInputStream(InputStream in)
                        Creates a BufferedInputStream and saves its argument,
                        the input stream in, for later use.
                */
                // Initialize a new BufferedInputStream from InputStream
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                /*
                    decodeStream
                        Bitmap decodeStream (InputStream is)
                            Decode an input stream into a bitmap. If the input stream is null, or
                            cannot be used to decode a bitmap, the function returns null. The stream's
                            position will be where ever it was after the encoded data was read.

                        Parameters
                            is InputStream : The input stream that holds the raw data
                                              to be decoded into a bitmap.
                        Returns
                            Bitmap : The decoded bitmap, or null if the image data could not be decoded.
                */
                // Convert BufferedInputStream to Bitmap object
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                // Return the downloaded bitmap
                return bmp;

            }catch(IOException e){
                e.printStackTrace();
            }finally{
                // Disconnect the http url connection
                connection.disconnect();
            }
            return null;
        }

        // When all async task done
        protected void onPostExecute(Bitmap result){
            // Hide the progress dialog
            //mProgressDialog.dismiss();
            aaa.setImageBitmap(result);

            if(result!=null){


                // Display the downloaded image into ImageView
                //mImageView.setImageBitmap(result);

                // Save bitmap to internal storage
                //Uri imageInternalUri = saveImageToInternalStorage(result);
                // Set the ImageView image from internal storage
                //mImageViewInternal.setImageURI(imageInternalUri);
            }else {
                // Notify user that an error occurred while downloading image
                //Snackbar.make(mCLayout,"Error",Snackbar.LENGTH_LONG).show();
            }
        }



    }
}
