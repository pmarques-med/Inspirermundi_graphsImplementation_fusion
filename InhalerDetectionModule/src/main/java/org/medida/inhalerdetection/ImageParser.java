package org.medida.inhalerdetection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Alberga uma Frame adquirida e processa-a
 */

public class ImageParser extends AsyncTask<Mat, Integer, Boolean> {


    private boolean finished = false;
    private boolean result = false;

    private static String foldername = "/frames";

    private static final String    TAG = "OCVSample::Activity";
    private static int frameCounterJava = 0;
    private int frameID = -9999;

    //SOFIA:
    private ImageView mImageView;
    // Max width (portrait mode)
    private Integer mImageMaxWidth;
    // Max height (portrait mode)
    private Integer mImageMaxHeight;

    public final long timestamp = System.currentTimeMillis();   //timestamp for this image

    public Mat originalImage;
    public Mat croppedImage;

    public String extratedText = "";

    public boolean didInhalerDetection = false;

    static {
        if(!OpenCVLoader.initDebug()){
            throw new RuntimeException("the image parser async task doesnt have opencv loader initialized.. something's messed up badly");
        } else {
            //System.loadLibrary("native-lib");

            System.loadLibrary("jni_wrapper");
            Log.d(TAG, "Native Lib loaded!");

        }
    }

    public ImageParser(int frameID) {
        this.frameID=frameID;
    }


    @Override
    protected Boolean doInBackground(Mat... image) {
        Boolean output = false;
        try {

            //Mat clone = image[0].clone();
            originalImage = image[0].clone();
            Mat clone = image[0];   //testing cloning on main thread to ensure clean image
            //output = InhalerDetection(clone.getNativeObjAddr());

            //Área Fixa
            Rect roi = new Rect(0, 0, 200, 200);

            //SOFIA:

            //Rect roi = null;
/*
            if ("diskusButton".equals(inhalertype)) {
                roi = new Rect(170, 240, 140, 100);
            }else if("elliptaButton".equals(inhalertype)){
                roi = new Rect(160, 370, 110, 80);
            }else if("flutiformButton".equals(inhalertype)){
                roi = new Rect(120, 200, 140, 130);
            }else if("novolizerButton".equals(inhalertype)){
                roi = new Rect(120, 100, 140, 80);
            }else if("spiromaxButton".equals(inhalertype)){
                roi = new Rect(140, 280, 140, 80);
            }else if("turbohalerButton".equals(inhalertype)){
                roi = new Rect(180, 220, 140, 80);
            }
*/

            croppedImage = new Mat(clone, roi);

            croppedImage = image[0].clone();


            /**
             * Calling the frame parse - Pre-processing of Frame for Template Detection ?
             */
            ParseFrame(clone.getNativeObjAddr());


            if(frameCounterJava > 0)
            {
                String debug = InhalerDetectionStr();
                result = debug.charAt(0) == '1' ? true: false;
                Log.d(TAG, "InhalerDetection - ** Frame ID " + frameID + " ** Data from detection: " + debug);

                frameCounterJava = 0;
                didInhalerDetection =  true;

                //if(result)      //DEBUG, JUST FOR TEXAS
                //SaveImage(clone);

            }

            frameCounterJava++;

            //result = false;
            //----------

            finished = true;
            clone.release();

            // ATIVAR O ML KIT SE o INALDADOR FOR DETECTADO COM SUCESSO
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

/*
    @Override
    protected Boolean doInBackground(Mat... mats) {
        return null;
    }*/

    @Override
    protected void onPostExecute(Boolean result)
    {
        Log.d(TAG, "Inhaler detected: " + result);
    }

    public static void SaveImage(Mat image, String name, File path )
    {
        String aa = path.getAbsolutePath() + foldername;

        int a = mkFolder(path.getAbsolutePath()+foldername);

        if (a <= 0) {
            Log.d(TAG, "Problem creating folder! Image will not be saved");
            return;
        }

        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        String filename = name; //+ ".png";

        Bitmap bmp = null;
        try {
            ResizeImage(image.getNativeObjAddr());
            //resize(bmp); //Non-static method 'resize(android.graphics.Bitmap)' cannot be referenced from a static context
            bmp = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(image, bmp);
        } catch (CvException e) {
            Log.d(TAG, e.getMessage());
        }

        //image.release();

        FileOutputStream out = null;


        try {
            File dest = new File(path.getAbsolutePath() + foldername, filename);


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


    /*** NATIVE METHODS ***/
    /**
     *
     * @param imageAddr
     */
    public native void ParseFrame(long imageAddr);

    /**
     * Template Detection
     * @return
     */
    public native String InhalerDetectionStr();
    // public native boolean InhalerDetection(long imageAddr);
    public static native void ResizeImage(long imageAddr);


    /*
    //SOFIA
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }*/


    // SOFIA: Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.

    private Integer getImageMaxWidth() {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = mImageView.getWidth();
        }

        return mImageMaxWidth;
    }

    // SOFIA: Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.

    private Integer getImageMaxHeight() {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight =
                    mImageView.getHeight();
        }

        return mImageMaxHeight;
    }

    // SOFIA: Gets the targeted width / height.

    private Pair<Integer, Integer> getTargetedWidthHeight() {
        int targetWidth;
        int targetHeight;
        int maxWidthForPortraitMode = getImageMaxWidth();
        int maxHeightForPortraitMode = getImageMaxHeight();
        targetWidth = maxWidthForPortraitMode;
        targetHeight = maxHeightForPortraitMode;
        return new Pair<>(targetWidth, targetHeight);
    }



    //SOFIA
    public Bitmap resize(Bitmap mImage){
        // Get the dimensions of the View
        Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

        int targetWidth = targetedSize.first;
        int maxHeight = targetedSize.second;

        // Determine how much to scale down the image
        float scaleFactor =
                Math.max(
                        (float) mImage.getWidth() / (float) targetWidth,
                        (float) mImage.getHeight() / (float) maxHeight);

        Bitmap resizedBitmap =
                Bitmap.createScaledBitmap(
                        mImage,
                        (int) (mImage.getWidth() / scaleFactor),
                        (int) (mImage.getHeight() / scaleFactor),
                        true);

        //mImageView.setImageBitmap(resizedBitmap);
        return resizedBitmap;
    }

    public Bitmap getCroppedImage() {

        Bitmap bmp = null;

        try {

            bmp = Bitmap.createBitmap(croppedImage.cols(), croppedImage.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(croppedImage, bmp);
        }
        catch (CvException e){Log.d("Exception ",e.getMessage());}

        return bmp;
    }

}
