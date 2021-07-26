package com.bloomidea.inspirers.utils;

import android.content.Context;
import android.graphics.Color;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.model.Inhaler;
import com.bloomidea.inspirers.model.MedicineType;

import org.medida.inhalerdetection.InhalerDetectionActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.bloomidea.inspirers.R.string.med_kHaller;

/**
 * Created by michellobato on 22/03/17. Changes by RuteAlmeida on 28/10/2019
 */


public class InhalerTypeAux {

    private static ArrayList<Inhaler> listMainInhaler;
    private static ArrayList<Inhaler> listMoreInhaler;

    public static ArrayList<Inhaler> getMainInhalerType(){

        listMainInhaler = new ArrayList<Inhaler>();

        listMainInhaler.add(new Inhaler(AppController.getmInstance().getString(med_kHaller), "flutiformkhaller", getRandomHexString(6), InhalerDetectionActivity.InhalerType.KHaller,false));
        listMainInhaler.add(new Inhaler(AppController.getmInstance().getString(R.string.med_turbohaler), "th", getRandomHexString(6), InhalerDetectionActivity.InhalerType.Turbohaler,false));
        listMainInhaler.add(new Inhaler(AppController.getmInstance().getString(R.string.med_ellipta), "ellipta", getRandomHexString(6), InhalerDetectionActivity.InhalerType.Ellipta,false));
        listMainInhaler.add(new Inhaler(AppController.getmInstance().getString(R.string.med_diskus), "diskus", getRandomHexString(6), InhalerDetectionActivity.InhalerType.Diskus,false));



        return listMainInhaler;
    }
// Rute: change in inhalers order (both lists)
    public static ArrayList<Inhaler> getMoreInhalerType(){

        listMoreInhaler = new ArrayList<Inhaler>();

        listMoreInhaler.add(new Inhaler(AppController.getmInstance().getString(R.string.med_spiromax), "spiromax", getRandomHexString(6), InhalerDetectionActivity.InhalerType.Spiromax, false));
        listMoreInhaler.add(new Inhaler(AppController.getmInstance().getString(R.string.med_flutiform), "flutiform", getRandomHexString(6), InhalerDetectionActivity.InhalerType.Flutiform , false));
        listMoreInhaler.add(new Inhaler(AppController.getmInstance().getString(R.string.med_novolizer), "novoliz", getRandomHexString(6), InhalerDetectionActivity.InhalerType.Novoziler,false));
        listMoreInhaler.add(new Inhaler(AppController.getmInstance().getString(R.string.med_easyhaler), "easyhaler", getRandomHexString(6), InhalerDetectionActivity.InhalerType.Easyhaler,false));
        listMoreInhaler.add(new Inhaler(AppController.getmInstance().getString(R.string.med_symbicort), "symbicort", getRandomHexString(6), InhalerDetectionActivity.InhalerType.Symbicort,false));
        listMoreInhaler.add(new Inhaler(AppController.getmInstance().getString(R.string.med_seretide), "seretaidemdi", getRandomHexString(6), InhalerDetectionActivity.InhalerType.Seretide,false));
        listMoreInhaler.add(new Inhaler(AppController.getmInstance().getString(R.string.med_MDS3M), "mdi3m", getRandomHexString(6), InhalerDetectionActivity.InhalerType.MDS3M,false));
        listMoreInhaler.add(new Inhaler(AppController.getmInstance().getString(R.string.med_twisthaler), "twisthaler", getRandomHexString(6), InhalerDetectionActivity.InhalerType.Twisthaler,false));
        listMoreInhaler.add(new Inhaler(AppController.getmInstance().getString(R.string.med_nextHaler), "nexthaler", getRandomHexString(6), InhalerDetectionActivity.InhalerType.NextHaler, false));
        listMoreInhaler.add(new Inhaler(AppController.getmInstance().getString(R.string.med_other), "asset23", "#ffffff", InhalerDetectionActivity.InhalerType.Unknown, false));


        return listMoreInhaler;
    }
    private static String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return "#" + sb.toString().substring(0, numchars);
    }
}
