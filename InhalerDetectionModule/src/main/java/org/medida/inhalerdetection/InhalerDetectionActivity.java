package org.medida.inhalerdetection;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/*
The activity returns RESULT_OK in the following scenarios:
- User was able to successfully perform the required inhaler detections
- User was not able to successfully perform the required inhaler detections, but stayed until the end of the timer
- User did n
 */

public class InhalerDetectionActivity extends Activity implements CvCameraViewListener2 {

    private static final boolean IS_PORTRAIT = false;

    private static final String TAG = "OCVSample::Activity";
    private static final int POST_DETECTION_CODE = 100;
    public static final String EXTRA_INTERNAL_FINAL_MESSAGE = "message";
    private AlertDialog dialog;

    private Mat FailImage50 = null;    //failed detection image at 50% duration
    private Mat FailImage75 = null;    //failed detection image at 75% duration
    private Mat SuccessImage = null;   //first successful detection image


    private Mat mRgba;
    private Mat mIntermediateMat;
    private Mat mGray;

    private Mat template1;
    private Mat template2;
    private Mat template3;

    private CameraBridgeViewBase mOpenCvCameraView;

    private ProgressBar progressBar;

    private int attemptDuration = 45;  //maximum duration of inhaler detection attempts in seconds

    private int frameCounter = 0;
    private long lastFrameTime = 0;
    private long firstFrameTime = 0;

    private final long activityTimeStamp = System.currentTimeMillis();
    private long successTimeStamp = 0;

    private boolean successResult;
    private int successCounter = 0;
    private int successCounterTarget = 3;
    private TextView successCounterView;
    private float progressFloat = 0;
    private LinkedBlockingQueue<ImageParser> imageParserQueue = new LinkedBlockingQueue<>();
    private boolean allowParsing = true;
    private boolean finished = false;
    private InhalerType inhalerType;

    public long patientId;

    private final int resultMarkerHisteresis = 5;
    private int resultMarkerCount = 0;
    private String lastImagePath = "";


    public static final String EXTRA_RESULT_SUCCESS = "result";
    public static final String EXTRA_RESULT_DETECTION_COUNT_INT = "totalRec";
    public static final String EXTRA_RESULT_DOSAGE_COUNT_INT = "dosageCount";
    public static final String EXTRA_INPUT_INHALER_TYPE_ENUM = "inhalerType";
    public static final String EXTRA_INPUT_DETECTION_COUNT_INT = "detectionCount";
    public static final String EXTRA_INPUT_DETECTION_MAX_TIME_INT = "detectionTime";
    public static final String EXTRA_INPUT_PATIENT_ID = "patientId";
    public static final String EXTRA_IMAGE_NAME = "imagePath";

    private String templateString;

    public static final String INHALER_DETECTION_PREFERENCES = "inhaler_detection_preferences";

    public enum InhalerType {
        Flutiform,
        Spiromax,
        Turbohaler,
        Diskus,
        Novoziler,
        Ellipta,

        NextHaler,
        KHaller,
        Easyhaler,
        Twisthaler,
        Seretide,
        Symbicort,
        MDS3M,

        Unknown
    }

    /**
     * Configures and returns an intent to call the inahaler detection activity with all the necessary parameters
     * @param context The context of the calling activity
     * @param inhalerType The enum of the inhaler type that needs to be detected
     * @param detectionCountTarget The amount of times there needs to be a successful detection to conclude the activity as a success
     * @param detectionMaxTime The maximum amount of time in seconds that the user will have to achieve the specified detection count
     * @return A fully configured intent that calls the inhaler detection activity with all the required parameters
     */
    public static Intent PrepareIntentInput(Context context, long patientId,  InhalerType inhalerType, int detectionCountTarget, int detectionMaxTime)
    {
        SharedPreferences prefs = context.getSharedPreferences(INHALER_DETECTION_PREFERENCES, MODE_PRIVATE);
        if(!prefs.getBoolean("doNotShowInstructions", false)){
            Intent intent = new Intent(context, PreInstructionDetectionActivity.class);
            intent.putExtra(EXTRA_INPUT_INHALER_TYPE_ENUM, inhalerType);
            intent.putExtra(EXTRA_INPUT_DETECTION_COUNT_INT, detectionCountTarget);
            intent.putExtra(EXTRA_INPUT_DETECTION_MAX_TIME_INT, detectionMaxTime);
            intent.putExtra(EXTRA_INPUT_PATIENT_ID, patientId);
            return intent;
        } else {
            Intent intent = new Intent(context, PreDetectionActivity.class);
            intent.putExtra(EXTRA_INPUT_INHALER_TYPE_ENUM, inhalerType);
            intent.putExtra(EXTRA_INPUT_DETECTION_COUNT_INT, detectionCountTarget);
            intent.putExtra(EXTRA_INPUT_DETECTION_MAX_TIME_INT, detectionMaxTime);
            intent.putExtra(EXTRA_INPUT_PATIENT_ID, patientId);
            return intent;
        }
    }

    private Intent IntentPrepareIntentOutput(int successCount, int dosageCount, boolean success)
    {
        Intent intent = new Intent(getBaseContext(), PostDetectionActivity.class);
        intent.putExtra(EXTRA_RESULT_DETECTION_COUNT_INT, successCount);
        intent.putExtra(EXTRA_RESULT_DOSAGE_COUNT_INT, dosageCount);
        intent.putExtra(EXTRA_RESULT_SUCCESS, success);
        intent.putExtra(EXTRA_IMAGE_NAME, lastImagePath);
        int msgId = 0;
        if(success)
            msgId = R.string.success_message;
        else
            msgId = R.string.fail_message;
        intent.putExtra(EXTRA_INTERNAL_FINAL_MESSAGE, msgId);
        return intent;
    }


    private void InitializeVariables() {
        frameCounter = successCounter = 0;
        lastFrameTime = firstFrameTime = 0;
        progressFloat = 0f;
        progressBar.setProgress(0);
        allowParsing = true;
        finished = false;

        Intent intent = getIntent();
        successCounterTarget = intent.getIntExtra(EXTRA_INPUT_DETECTION_COUNT_INT, 3);
        attemptDuration = intent.getIntExtra(EXTRA_INPUT_DETECTION_MAX_TIME_INT, 15);
        inhalerType = (InhalerType) intent.getSerializableExtra(EXTRA_INPUT_INHALER_TYPE_ENUM);
        patientId = intent.getLongExtra(EXTRA_INPUT_PATIENT_ID, 0);

        ImageView view = findViewById(R.id.crosshairView);
        if(inhalerType == InhalerType.Unknown)
        {
            view.setVisibility(View.VISIBLE);
            attemptDuration = 5;
        }
        else
            view.setVisibility(View.INVISIBLE);
    }

    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");

            //System.loadLibrary("native-lib");
            //Log.d(TAG, "Native Lib loaded!");
            //System.loadLibrary("frame_analyser");
            System.loadLibrary("jni_wrapper");
        }
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    //mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    public InhalerDetectionActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        setContentView(R.layout.inhaler_detection_layout);

        //CheckPermissions();
        if(!CheckPermissions())
        {
            Log.d(TAG, "This shouldn't be possible, entered inhaler detection activity without proper camera/disk permissions");
            ExitActivity();
        }

        else
            InitView();
    }

    private boolean CheckPermissions() {
        if(android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1)
            return true;

        boolean hasPermissions = true;

        if (getApplicationContext().checkSelfPermission( Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            hasPermissions = false;
        }

        if (getApplicationContext().checkSelfPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            hasPermissions = false;
        }

        return hasPermissions;
    }

    private void InitView()
    {
        mOpenCvCameraView = findViewById(R.id.tutorial1_activity_java_surface_view);
        //mOpenCvCameraView.setMaxFrameSize(1280, 720);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);


        progressBar = findViewById(R.id.progressBar);
        successCounterView = findViewById(R.id.textView);

        InitializeVariables();

        mOpenCvCameraView.enableView();

        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        Log.i(TAG, "String from jni:" + stringFromJNI());
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        if(dialog != null)
            dialog.dismiss();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found on resuming");
            //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package on resuming. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
    }

    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        try {
            mRgba = inputFrame.rgba();
            
            if(finished)
                return mRgba;

            long addr = mRgba.getNativeObjAddr();

            if (frameCounter == 0) //initial setup
                InitialSetup();

            else   //mRgba will be changed here, through pointers
                TryParseFrame();

            //returns true if it performed inhaler detection and not just regular frame parse
            if(ParseThreadQueue())
                resultMarkerCount = resultMarkerHisteresis;

            boolean photoTaken = --resultMarkerCount >= 0;

            //UpdateOverlay(addr, photoTaken, successResult); //update overlay ignores successResult unless photoTrue is true
                                                            //yellow on default, green when photo taken + success, red when photo taken + !success

            if(inhalerType == InhalerType.Unknown)
                TryKeepImages(mRgba);
            else
                UpdateOverlay(addr, successResult, successResult);

            CheckFinishActivity();

            frameCounter++;

            return mRgba;
        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
        }
        finally {
            return mRgba;
        }
    }

    private void InitialSetup() {
        long addr = mRgba.getNativeObjAddr();

        //tem de corresponder a estes strings
        //const char* args[] = {"flutiform", "turbohaler", "diskus", "novolizer", "spiromax", "ellipta"};
        try {
            //template1 = Utils.loadResource(this, R.drawable.templateplusbdedges2cropelipsebb, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
            templateString = "";
            switch(inhalerType)
            {
                case Flutiform:
                    template1 = Utils.loadResource(this, R.drawable.templateplusbdedges2cropelipsebb11, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(this, R.drawable.templateplusbdedges2cropnomouthbb, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(this, R.drawable.templateplusbdedges2crop, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

                    templateString = "flutiform";
                    break;

                case Diskus:
                    /*
                    template1 = Utils.loadResource(this, R.drawable.diskus3_canny_cleanedcropcoarseb2, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(this, R.drawable.diskus3_canny_cleanedcropcoarse, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(this, R.drawable.diskus3_canny_cleanedcropcoarse, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    */
                    template1 = Utils.loadResource(this, R.drawable.diskussdetail, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(this, R.drawable.diskuss, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(this, R.drawable.diskuss, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

                    templateString = "diskus";
                    break;

                case Ellipta:
                    template1 = Utils.loadResource(this, R.drawable.ellipta9_canny_cleanedrotcrop_dialbb13, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(this, R.drawable.ellipta9_canny_cleanedrotcropcoarse, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(this, R.drawable.ellipta9_canny_cleanedrotcrop, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

                    templateString = "ellipta";
                    break;
                case Novoziler:
                    template1 = Utils.loadResource(this, R.drawable.novolizer_small_custom, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(this, R.drawable.novolizer_big_custom, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(this, R.drawable.novolizer_big_custom, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

                    templateString = "novolizer";
                    break;

                case Spiromax:
                    template1 = Utils.loadResource(this, R.drawable.spiromaxsmallcustom, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(this, R.drawable.spiromaxbigcustom2, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(this, R.drawable.spiromaxbigcustom2, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

                    templateString = "spiromax";    //TESTADO
                    break;

                case Turbohaler:
                    template1 = Utils.loadResource(this, R.drawable.turbohalertemplate1_canny_cropbb, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(this, R.drawable.turbohalertemplate_canny_crop2, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(this, R.drawable.turbohalertemplate_canny_crop2, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

                    templateString = "turbohaler";
                    break;

                case Easyhaler:
                    template1 = Utils.loadResource(this, R.drawable.easyhaler_small, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(this, R.drawable.easyhaler_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(this, R.drawable.easyhaler_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

                    templateString = "easyhaler";
                    break;

                case KHaller:
                    template1 = Utils.loadResource(this, R.drawable.khaler_small, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(this, R.drawable.khaler_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(this, R.drawable.khaler_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

                    templateString = "khaler";
                    break;

                case MDS3M:
                    template1 = Utils.loadResource(this, R.drawable.mdi3m_small, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(this, R.drawable.mdi3m_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(this, R.drawable.mdi3m_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

                    templateString = "mdi";
                    break;

                case NextHaler:
                    template1 = Utils.loadResource(this, R.drawable.nexthaler_small, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(this, R.drawable.nexthaler_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(this, R.drawable.nexthaler_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

                    templateString = "nexthaler";
                    break;

                case Seretide:
                    template1 = Utils.loadResource(this, R.drawable.seretide_small, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(this, R.drawable.seretide_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(this, R.drawable.seretide_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

                    templateString = "seretide";
                    break;

                case Twisthaler:
                    template1 = Utils.loadResource(this, R.drawable.twisthaler_small, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(this, R.drawable.twisthaler_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(this, R.drawable.twisthaler_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

                    templateString = "twisthaler";
                    break;

                case Unknown:
                    template1 = null;

                    templateString = "unknown";
                    break;

                default:
                    throw new Exception("Inhaler type not implemented");
            }

            if(template1 != null)
                PrepareTemplate(addr, templateString, template1.getNativeObjAddr(), template2.getNativeObjAddr(), template3.getNativeObjAddr());

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        progressFloat = 0;

        firstFrameTime = lastFrameTime = SystemClock.elapsedRealtime();
    }

    private boolean TryParseFrame() {
        if(allowParsing == false)
            return false;

        boolean wasPhotoTaken = false;

        long currentFrameTime = SystemClock.elapsedRealtime();
        long lastDelta = currentFrameTime - lastFrameTime;
        long totalDelta = currentFrameTime - firstFrameTime;

        if(attemptDuration == 0)
            progressFloat = 0;
        else
            progressFloat = totalDelta / (attemptDuration * 1000f) * progressBar.getMax();


        if (ShouldParseFrame(lastDelta))
        {
            String a="";
            ImageParser imageParser = new ImageParser();
            imageParserQueue.add(imageParser);
            imageParser.execute(mRgba.clone());

            Log.d(TAG, "Thread started");
            lastFrameTime = currentFrameTime;

            wasPhotoTaken = true;
        }

        if (progressFloat > progressBar.getMax())
            progressFloat = progressBar.getMax();

        progressBar.setProgress((int) Math.floor(progressFloat));

        return wasPhotoTaken;
    }

    private void CheckFinishActivity()
    {
        if (allowParsing &&
            (progressBar.getProgress() == progressBar.getMax() || (successCounterTarget == 0 && successCounter > 0) || successCounter >= successCounterTarget))
        {
            allowParsing = false;
        }

        //this is to address the edge case of time running out with a successful detection result coming in after the timeout (due to result lag of threads)
        if(finished == false && allowParsing == false && imageParserQueue.size() == 0)
        {
            finished = true;

            SaveImagesToDisk();

            int dosageCounter = 0;  //go get this from cpp later on
            Intent intent = IntentPrepareIntentOutput(successCounter, dosageCounter, successCounter >= successCounterTarget);
            startActivityForResult(intent, POST_DETECTION_CODE);

            //CreateEndDialog();
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

    //returns true if the thread attempted inhaler detection
    private boolean ParseThreadQueue() {
        boolean didInhalerDetection = false;

        ImageParser parser = imageParserQueue.peek();
        if (parser != null && parser.HasFinished()) {

            if(parser.DidInhalerDetection)
            {
                didInhalerDetection = true;
                successResult = parser.IsSuccessful();
                successCounter += successResult ? 1 : 0;

                if (successResult) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            successCounterView.setText(String.valueOf(successCounter));
                        }
                    });
                }

                TryKeepImages(parser, successResult);
            }
            else
                parser.originalImage.release();

            imageParserQueue.remove();
        }
        return didInhalerDetection;
    }

    //for Unknown inhaler/medication
    private void TryKeepImages(Mat image)
    {
        float progress = (float)progressBar.getProgress() / progressBar.getMax();
        if(FailImage50 == null && progress >= 0.5)
            FailImage50 = image.clone();
        else
        if(FailImage75 == null && progress >= 0.75)
            FailImage75 = image.clone();
    }

    private void TryKeepImages(ImageParser parser, boolean successResult) {
        //saving this as a future simple multi threading approach draft
        /*
        final Mat image = originalImage;

        new Thread(new Runnable() {
            @Override
            public void run() {
                SuccessImage = image.clone();
            }
        });
        */

        Mat parsedImage = parser.originalImage;

        if(successResult && SuccessImage == null)
        {
            SuccessImage = parsedImage.clone();
            successTimeStamp = parser.timestamp;
        }


        else if(!successResult)
        {
            float progress = (float)progressBar.getProgress() / progressBar.getMax();
            if(FailImage50 == null && progress >= 0.5)
                FailImage50 = parsedImage.clone();
            else
            if(FailImage75 == null && progress >= 0.75)
                FailImage75 = parsedImage.clone();
        }
    }

    private boolean ShouldParseFrame(long lastDelta) {
        return imageParserQueue.size() == 0 && inhalerType != InhalerType.Unknown;
    }


    private void ExitActivity()
    {
        SaveImagesToDisk();
        finish();
    }

    private void SaveImagesToDisk() {
        String path = "";
        //<detection start timestamp>_<inhaler type>_<success/quart/half>_<picture timestamp>_<maximum detection duration>_user<patientId>
        if(FailImage50 != null)
        {
            path = activityTimeStamp + "_" + templateString  + "_half_" + activityTimeStamp + (attemptDuration * 750) + "_" + attemptDuration + "_user" + patientId;
            ImageParser.SaveImage(FailImage50 , path);
            FailImage50.release();
        }
        if(FailImage75 != null)
        {
            path = activityTimeStamp + "_" + templateString  + "_quart_" + activityTimeStamp + (attemptDuration * 500) + "_" + attemptDuration + "_user" + patientId;
            ImageParser.SaveImage(FailImage75 , path);
            FailImage75.release();
        }

        if(SuccessImage != null)
        {
            path = activityTimeStamp + "_" + templateString  + "_success_" + successTimeStamp + "_" + attemptDuration + "_user" + patientId;
            ImageParser.SaveImage(SuccessImage, path);
            SuccessImage.release();
        }
        lastImagePath = path;
    }

    /*
    private void CreateEndDialog() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(InhalerDetectionActivity.this);

                String message;
                if(successCounter >= successCounterTarget)
                {
                    message = "Deteção com sucesso!";

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Log.d(TAG, "User had success and clicked ok!");

                            int dosageCounter = 0;  //go get this from cpp later on
                            Intent intent = IntentPrepareIntentOutput(successCounter, dosageCounter);
                            startActivityForResult(intent, POST_DETECTION_CODE);

                            //setResult(Activity.RESULT_OK, returnIntent);
                            //ExitActivity();
                        }
                    });
                }

                else
                {
                    message = "Deteção terminada";

                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Log.d(TAG, "User failed and left the activity correctly!");

                            int dosageCounter = 0;  //go get this from cpp later on
                            Intent intent = IntentPrepareIntentOutput(successCounter, dosageCounter);
                            startActivityForResult(intent, POST_DETECTION_CODE);

                            //setResult(Activity.RESULT_OK, intent);
                            //ExitActivity();

                        }
                    });
                }

                builder.setMessage(message);
                builder.setTitle("Fim de aquisição");

                dialog = builder.create();
                dialog.show();
            }
        });
    }*/

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native void ParseFrame(long imageAddr);
    public native void PrepareTemplate(long imageAddr, String templateType, long template1, long template2, long template3);
    public native void UpdateOverlay(long imageAddr, boolean photoTaken, boolean successfulDetection);
}
