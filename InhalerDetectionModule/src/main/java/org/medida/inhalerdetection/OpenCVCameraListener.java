package org.medida.inhalerdetection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class OpenCVCameraListener implements JavaCameraView.CvCameraViewListener2 {

    private final static String TAG = "OpenCVCameraListener";

    private Activity activity;

    private File path;

    private int frameCounter;

    private InhalerDetectionActivity.InhalerType inhalerType;

    private LinkedBlockingQueue<ImageParser> imageParserQueue = new LinkedBlockingQueue<>();

    private int finished = 0;

    private Mat mRgba;
    private Mat mIntermediateMat;
    private Mat mGray;

    private Mat FailImage50 = null;    //failed detection image at 50% duration
    private Mat FailImage75 = null;    //failed detection image at 75% duration
    private Mat SuccessImage = null;   //first successful detection image

    //

    private static final int POST_DETECTION_CODE = 100;
    private Mat template1;
    private Mat template2;
    private Mat template3;

    private int attemptDuration = 45;  //maximum duration of inhaler detection attempts in seconds

    private long lastFrameTime = 0;
    private long firstFrameTime = 0;

    private long successTimeStamp = 0;

    private boolean successResult;
    private int successCounter = 0;
    private int successCounterTarget = 5;
    private boolean allowParsing = true;


    private String templateString;

    //

    OpenCVCameraListener(Activity activity, InhalerDetectionActivity.InhalerType inhalerType, File path) {
        this.activity = activity;
        this.inhalerType = inhalerType;
        this.path=path;
        frameCounter= 0;
    }

    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
            System.loadLibrary("jni_wrapper");
        }
    }

    private void InitialSetup() {
        Log.d(TAG,"Initializing OpenCV Camera Setup");
        long addr = mRgba.getNativeObjAddr();
        try {
            templateString = "";
            switch(inhalerType)
            {
                case Flutiform:
                    template1 = Utils.loadResource(activity, R.drawable.templateplusbdedges2cropelipsebb11, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(activity, R.drawable.templateplusbdedges2cropnomouthbb, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(activity, R.drawable.templateplusbdedges2crop, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    templateString = "flutiform";
                    break;

                case Diskus:
                    template1 = Utils.loadResource(activity, R.drawable.diskussdetail, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(activity, R.drawable.diskuss, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(activity, R.drawable.diskuss, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    templateString = "diskus";
                    break;

                case Ellipta:
                    template1 = Utils.loadResource(activity, R.drawable.ellipta9_canny_cleanedrotcrop_dialbb13, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(activity, R.drawable.ellipta9_canny_cleanedrotcropcoarse, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(activity, R.drawable.ellipta9_canny_cleanedrotcrop, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    templateString = "ellipta";
                    break;
                case Novoziler:
                    template1 = Utils.loadResource(activity, R.drawable.novolizer_small_custom, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(activity, R.drawable.novolizer_big_custom, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(activity, R.drawable.novolizer_big_custom, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    templateString = "novolizer";
                    break;

                case Spiromax:
                    template1 = Utils.loadResource(activity, R.drawable.spiromaxsmallcustom, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(activity, R.drawable.spiromaxbigcustom2, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(activity, R.drawable.spiromaxbigcustom2, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    templateString = "spiromax";
                    break;

                case Turbohaler:
                    /*template1 = Utils.loadResource(activity, R.drawable.turbohalertemplate1_canny_cropbb, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(activity, R.drawable.turbohalertemplate_canny_crop2, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(activity, R.drawable.turbohalertemplate_canny_crop2, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    */

                    template1 = Utils.loadResource(activity, R.drawable.turbohaler_edge_detail, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(activity, R.drawable.turbohaler_edge, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(activity, R.drawable.turbohaler_edge, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);


                    templateString = "turbohaler";
                    break;

                case Easyhaler:
                    template1 = Utils.loadResource(activity, R.drawable.easyhaler_small, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(activity, R.drawable.easyhaler_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(activity, R.drawable.easyhaler_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    templateString = "easyhaler";
                    break;

                case KHaller:
                    template1 = Utils.loadResource(activity, R.drawable.khaler_small, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(activity, R.drawable.khaler_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(activity, R.drawable.khaler_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    templateString = "khaler";
                    break;

                case MDS3M:
                    template1 = Utils.loadResource(activity, R.drawable.mdi3m_small, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(activity, R.drawable.mdi3m_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(activity, R.drawable.mdi3m_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    templateString = "mdi";
                    break;

                case NextHaler:
                    template1 = Utils.loadResource(activity, R.drawable.nexthaler_small, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(activity, R.drawable.nexthaler_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(activity, R.drawable.nexthaler_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    templateString = "nexthaler";
                    break;

                case Seretide:
                    template1 = Utils.loadResource(activity, R.drawable.seretide_small, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(activity, R.drawable.seretide_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(activity, R.drawable.seretide_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    templateString = "seretide";
                    break;

                case Twisthaler:
                    template1 = Utils.loadResource(activity, R.drawable.twisthaler_small, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template2 = Utils.loadResource(activity, R.drawable.twisthaler_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    template3 = Utils.loadResource(activity, R.drawable.twisthaler_big, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
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

        firstFrameTime = lastFrameTime = SystemClock.elapsedRealtime();
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

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        if (frameCounter == 0) InitialSetup();
        ParseFrame(frameCounter);
        ParseThreadQueue();
        UpdateOverlay(mRgba.getNativeObjAddr(), successResult,successResult);
        if(finished == 1){
            DetectionActivity activity = (DetectionActivity) this.activity;
            String imageName = activity.getActivityTimeStamp()+ "_" + templateString  + "_success_" + successTimeStamp + "_" + attemptDuration + "_user" + activity.getPatientId();
            ImageParser.SaveImage(SuccessImage, imageName,this.path);
            SuccessImage.release();
            activity.AdvanceToPostDetectionActivity(imageName);
            finished++;
        }
        frameCounter++;
        return mRgba;
    }

    private void TryKeepImages(ImageParser parser, boolean successResult) {
        Mat parsedImage = parser.originalImage;
        if(successResult && SuccessImage == null)
        {
            SuccessImage = parsedImage.clone();
            successTimeStamp = parser.timestamp;
        }
    }

    //returns true if the thread attempted inhaler detection
    private boolean ParseFrame(int frameID) {
        if(!allowParsing) return false;
        boolean wasPhotoTaken = false;
        long currentFrameTime = SystemClock.elapsedRealtime();
        long lastDelta = currentFrameTime - lastFrameTime;
        long totalDelta = currentFrameTime - firstFrameTime;
        if (ShouldParseFrame(lastDelta))
        {
            ImageParser imageParser = new ImageParser(frameID);
            imageParserQueue.add(imageParser);
            imageParser.execute(mRgba.clone());
            Log.d(TAG, "Thread started");
            lastFrameTime = currentFrameTime;
            wasPhotoTaken = true;
        }
        return wasPhotoTaken;
    }

    private boolean ShouldParseFrame(long lastDelta) {
        return imageParserQueue.size() == 0 && inhalerType != InhalerDetectionActivity.InhalerType.Unknown;
    }

    private boolean ParseThreadQueue() {
        boolean didInhalerDetection = false;
        ImageParser parser = imageParserQueue.peek();
        if (parser != null && parser.HasFinished()) {

            if(parser.didInhalerDetection)
            {
                Log.i(TAG,"DETECTEI UMA FRAME");
                didInhalerDetection = true;
                successResult = parser.IsSuccessful();
                successCounter += successResult ? 1 : 0;
                if (successResult) {
                    finished = 1;
                }
                TryKeepImages(parser, successResult);
            }
            else parser.originalImage.release();
            imageParserQueue.remove();
        }
        return didInhalerDetection;
    }



    public native void ParseFrame(long imageAddr);
    public native void PrepareTemplate(long imageAddr, String templateType, long template1, long template2, long template3);
    public native void UpdateOverlay(long imageAddr, boolean photoTaken, boolean successfulDetection);
}
