package org.medida.inhalerdetection;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by backup on 22/08/2017.
 */

public class ImageParser extends AsyncTask<Mat, Integer, Boolean> {


    private boolean finished = false;
    private boolean result = false;

    private static String foldername = "/frames";

    private static final String    TAG = "OCVSample::Activity";
    private static int frameCounterJava = 0;

    public final long timestamp = System.currentTimeMillis();   //timestamp for this image

    public Mat originalImage;

    public boolean DidInhalerDetection = false;

    static {
        if(!OpenCVLoader.initDebug()){
            throw new RuntimeException("the image parser async task doesnt have opencv loader initialized.. something's messed up badly");
        } else {
            //System.loadLibrary("native-lib");
            //Log.d(TAG, "Native Lib loaded!");

            System.loadLibrary("jni_wrapper");

        }
    }


    @Override
    protected Boolean doInBackground(Mat... image) {
        Boolean output = false;
        try {

            //Mat clone = image[0].clone();
            originalImage = image[0].clone();
            Mat clone = image[0];   //testing cloning on main thread to ensure clean image
            //output = InhalerDetection(clone.getNativeObjAddr());

            ParseFrame(clone.getNativeObjAddr());

            if(frameCounterJava == 3)
            {
                String debug = InhalerDetectionStr();
                result = debug.charAt(0) == '1' ? true: false;
                frameCounterJava = 0;
                DidInhalerDetection =  true;

                Log.d(TAG, "************ DEBUG: " + debug);

                //if(result)      //DEBUG, JUST FOR TEXAS
                //SaveImage(clone);
            }

            frameCounterJava++;

            //result = false;
            //----------

            finished = true;
            clone.release();

            return output;
        }
        catch(Exception e)
        {
            Log.d(TAG, e.getMessage());
        }
        finally
        {
            return output;
        }

    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        Log.d(TAG, "Inhaler detected: " + result);
    }

    public static void SaveImage(Mat image, String name)
    {
        if (mkFolder(foldername) <= 0) {
            Log.d(TAG, "Problem creating folder! Image will not be saved");
            return;
        }



        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        String filename = name; //+ ".png";

        Bitmap bmp = null;
        try {
            ResizeImage(image.getNativeObjAddr());

            bmp = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(image, bmp);
        } catch (CvException e) {
            Log.d(TAG, e.getMessage());
        }

        //image.release();

        FileOutputStream out = null;

        File dest = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + foldername, filename);


        try {
            dest.createNewFile();
            out = new FileOutputStream(dest);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                    Log.d(TAG, "Saved image with filename: " + filename);
                }
            } catch (IOException e) {
                Log.d(TAG, e.getMessage() + "Error");
                e.printStackTrace();
            }

        }
    }


    private static int mkFolder(String folderName){
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)){
            Log.d(TAG, "Error: external storage is unavailable");
            return 0;
        }
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.d(TAG, "Error: external storage is read only.");
            return 0;
        }
        //Log.d(TAG, "External storage is not read only or unavailable");

        File folder = new File(Environment.getExternalStorageDirectory(), folderName);

        int result = 0;
        if (folder.exists()) {
            //Log.d(TAG,"folder exist:"+folder.toString());
            result = 2; // folder exist
        }else{
            try {
                if (folder.mkdir()) {
                    Log.d(TAG, "folder created:" + folder.toString());
                    result = 1; // folder created
                } else {
                    Log.d(TAG, "creat folder fails:" + folder.toString());
                    result = 0; // creat folder fails
                }
            }catch (Exception ecp){
                ecp.printStackTrace();
            }
        }
        return result;
    }

    public boolean HasFinished()
    {
        return finished;
    }

    public boolean IsSuccessful()
    {
        return result;
    }

    public native void ParseFrame(long imageAddr);
    public native String InhalerDetectionStr();
    public native boolean InhalerDetection(long imageAddr);
    public static native void ResizeImage(long imageAddr);


}
