package com.bloomidea.inspirers.utils;

import android.content.Context;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.model.Inhaler;
import com.bloomidea.inspirers.model.MedicineType;

import org.medida.inhalerdetection.InhalerDetectionActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale; // language

/**
 * Created by michellobato on 22/03/17. Changes by RuteAlmeida on 28/10/2019
 */

public class MedicineTypeAux {
    private static ArrayList<String> medicinesNeedInlNames;

    private static HashMap<String,InhalerDetectionActivity.InhalerType> listMedicineNameRecognitionCodeToTake;
    private static HashMap<String,String> listMedicineNamePillsToTake;
    private static HashMap<String,String> listMedicineNameOthersToTake;

    //public static final String TYPE1_CODE = "INAL";
    //private static final String TYPE2_CODE = "PILS";
    //private static final String TYPE3_CODE = "INAL_NAR";

    public static final String TYPE1_CODE = "med_type_inhalation";
    public static final String TYPE2_CODE = "med_type_pill";
    public static final String TYPE3_CODE = "med_type_nostil";

    private static ArrayList<MedicineType> listMediceTypes;

    public static HashMap<String,InhalerDetectionActivity.InhalerType> getListMedicineNameRecognitionCodeToTake() {
        if (listMedicineNameRecognitionCodeToTake == null) {
            listMedicineNameRecognitionCodeToTake = new HashMap<>();
            if (Locale.getDefault().getLanguage().contentEquals("pt")) { //Rute & JValente 28.10.2019
                listMedicineNameRecognitionCodeToTake.put("Brisomax Diskus 100/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Brisomax Diskus 250/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Brisomax Diskus 500/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Brisovent Diskus 100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Brisovent Diskus 250", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Brisovent Diskus 500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Dilamax Diskus 50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Flixotaide Diskus 100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Flixotaide Diskus 250", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Flixotaide Diskus 500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Maizar Diskus 100/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Maizar Diskus 250/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Maizar Diskus 500/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Seretaide Diskus 100/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Seretaide Diskus 250/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Seretaide Diskus 500/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Serevent Diskus 50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Veraspir Diskus 100/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Veraspir Diskus 250/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Veraspir Diskus 500/50", InhalerDetectionActivity.InhalerType.Diskus);
            } else if (Locale.getDefault().getLanguage().contentEquals("es")) { //Rute & JValente 28.10.2019
                listMedicineNameRecognitionCodeToTake.put("Anasma Accuhaler 50/100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Anasma Accuhaler 50/250", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Anasma Accuhaler 50/500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("BeglÃ¡n Accuhaler 50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Betamican Accuhaler 50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Flixotide Accuhaler 100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Flixotide Accuhaler 500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Flusonal Accuhaler 100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Flusonal Accuhaler 500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Inalacor Accuhaler 100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Inalacor Accuhaler 500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("InaladÃºo Accuhaler 50/100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("InaladÃºo Accuhaler 50/250", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("InaladÃºo Accuhaler 50/500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Inaspir Accuhaler 50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("PlusVent Accuhaler 50/100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("PlusVent Accuhaler 50/250", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("PlusVent Accuhaler 50/500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Seretaide Accuhaler 50/100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Seretaide Accuhaler 50/250", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Seretaide Accuhaler 50/500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Serevent Accuhaler 50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Trialona Accuhaler 100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Trialona Accuhaler 500", InhalerDetectionActivity.InhalerType.Diskus);
            } else {
                listMedicineNameRecognitionCodeToTake.put("Anasma Accuhaler 50/100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Anasma Accuhaler 50/250", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Anasma Accuhaler 50/500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("BeglÃ¡n Accuhaler 50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Betamican Accuhaler 50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Brisomax Diskus 100/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Brisomax Diskus 250/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Brisomax Diskus 500/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Brisovent Diskus 10", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Brisovent Diskus 250", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Brisovent Diskus 500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Dilamax Diskus 50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Flixotaide Diskus 100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Flixotaide Diskus 25", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Flixotaide Diskus 50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Flixotide Accuhaler 100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Flixotide Accuhaler 500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Flusonal Accuhaler 100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Flusonal Accuhaler 500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Inalacor Accuhaler 100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Inalacor Accuhaler 500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("InaladÃºo Accuhaler 50/100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("InaladÃºo Accuhaler 50/250", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("InaladÃºo Accuhaler 50/500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Inaspir Accuhaler 50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Maizar Diskus 100/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Maizar Diskus 250/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Maizar Diskus 500/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("PlusVent Accuhaler 50/100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("PlusVent Accuhaler 50/250", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("PlusVent Accuhaler 50/500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Seretaide Accuhaler 50/100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Seretaide Accuhaler 50/250", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Seretaide Accuhaler 50/500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Seretaide Diskus 100/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Seretaide Diskus 250/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Seretaide Diskus 500/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Serevent Diskus 50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Serevent Accuhaler 50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Trialona Accuhaler 100", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Trialona Accuhaler 500", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Veraspir Diskus 100/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Veraspir Diskus 250/50", InhalerDetectionActivity.InhalerType.Diskus);
                listMedicineNameRecognitionCodeToTake.put("Veraspir Diskus 500/50", InhalerDetectionActivity.InhalerType.Diskus);
            }
            if (Locale.getDefault().getLanguage().contentEquals("pt")) { //Rute
                listMedicineNameRecognitionCodeToTake.put("Bufomix Easyhaler 160/4.5", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Bufomix Easyhaler 320/9", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Bufomix Easyhaler 80/4.5", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Gibiter Easyhaler 160/4.5", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Gibiter Easyhaler 320/9", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Gibiter Easyhaler 80/4.5", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Salflumix Easyhaler 250/50", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Salflumix Easyhaler 500/50", InhalerDetectionActivity.InhalerType.Easyhaler);
            } else if (Locale.getDefault().getLanguage().contentEquals("es")) { //Rute
                listMedicineNameRecognitionCodeToTake.put("Bufomix Easyhaler 160/4.5", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Bufomix Easyhaler 320/9", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Budesonida Easyhaler 100", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Budesonida Easyhaler 200", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Budesonida Easyhaler 400", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Flusamix Easyhaler 50/500", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Gibiter Easyhaler 160/4.5", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Gibiter Easyhaler 320/9", InhalerDetectionActivity.InhalerType.Easyhaler);
            } else {
                listMedicineNameRecognitionCodeToTake.put("Budesonida Easyhaler 100", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Budesonida Easyhaler 200", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Budesonida Easyhaler 400", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Bufomix Easyhaler 160/4.5", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Bufomix Easyhaler 320/9", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Bufomix Easyhaler 80/4.5", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Flusamix Easyhaler 50/500", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Gibiter Easyhaler 160/4.5", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Gibiter Easyhaler 320/9", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Gibiter Easyhaler 80/4.5", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Salflumix Easyhaler 250/50", InhalerDetectionActivity.InhalerType.Easyhaler);
                listMedicineNameRecognitionCodeToTake.put("Salflumix Easyhaler 500/50", InhalerDetectionActivity.InhalerType.Easyhaler);
            }
            if (Locale.getDefault().getLanguage().contentEquals("pt")) { //Rute
                listMedicineNameRecognitionCodeToTake.put("Anoro Ellipta 55/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Incruse Ellipta 55", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Elebrato Ellipta 92/55/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Laventair Ellipta 55/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Relvar Ellipta 184/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Relvar Ellipta 92/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Revinty Ellipta 184/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Revinty Ellipta 92/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Trelegy Ellipta 92/55/22", InhalerDetectionActivity.InhalerType.Ellipta);
            } else if (Locale.getDefault().getLanguage().contentEquals("es")) { //Rute
                listMedicineNameRecognitionCodeToTake.put("Anoro Ellipta 55/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Incruse Ellipta 55", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Laventair Ellipta 55/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Relvar Ellipta 184/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Relvar Ellipta 92/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Revinty Ellipta 184/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Revinty Ellipta 92/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Rolufta Ellipta 55", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Trelegy Ellipta 92/22", InhalerDetectionActivity.InhalerType.Ellipta);
            } else {
                listMedicineNameRecognitionCodeToTake.put("Anoro Ellipta 55/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Elebrato Ellipta 92/55/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Incruse Ellipta 55", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Laventair Ellipta 55/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Relvar Ellipta 184/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Relvar Ellipta 92/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Revinty Ellipta 184/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Revinty Ellipta 92/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Rolufta Ellipta 55", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Trelegy Ellipta 92/22", InhalerDetectionActivity.InhalerType.Ellipta);
                listMedicineNameRecognitionCodeToTake.put("Trelegy Ellipta 92/55/22", InhalerDetectionActivity.InhalerType.Ellipta);
            }
            listMedicineNameRecognitionCodeToTake.put("Flutiform 125/5", InhalerDetectionActivity.InhalerType.Flutiform);
            listMedicineNameRecognitionCodeToTake.put("Flutiform 250/10", InhalerDetectionActivity.InhalerType.Flutiform);
            listMedicineNameRecognitionCodeToTake.put("Flutiform 50/5", InhalerDetectionActivity.InhalerType.Flutiform);
            listMedicineNameRecognitionCodeToTake.put("Flutiform K-haler 125/5", InhalerDetectionActivity.InhalerType.KHaller);
            listMedicineNameRecognitionCodeToTake.put("Flutiform K-haler 50/5", InhalerDetectionActivity.InhalerType.KHaller);
            if (Locale.getDefault().getLanguage().contentEquals("pt")) { //Rute
                listMedicineNameRecognitionCodeToTake.put("Serkep 125/25", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("Serkep 250/25", InhalerDetectionActivity.InhalerType.MDS3M);
            } else if (Locale.getDefault().getLanguage().contentEquals("es")) { //Rute
                listMedicineNameRecognitionCodeToTake.put("Anasma 25/50", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("Anasma 25/125", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("Anasma 25/250", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("InaladÃºo 25/50", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("InaladÃºo 25/125", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("InaladÃºo 25/250", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("PlusVent 25/50", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("PlusVent 25/125", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("PlusVent 25/250", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("Seretide 25/50", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("Seretide 25/125", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("Seretide 25/250", InhalerDetectionActivity.InhalerType.MDS3M);
            } else {
                listMedicineNameRecognitionCodeToTake.put("Anasma 25/50", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("Anasma 25/125", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("Anasma 25/250", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("InaladÃºo 25/50", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("InaladÃºo 25/125", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("InaladÃºo 25/250", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("PlusVent 25/50", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("PlusVent 25/125", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("PlusVent 25/250", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("Seretide 25/50", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("Seretide 25/125", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("Seretide 25/250", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("Serkep 125/25", InhalerDetectionActivity.InhalerType.MDS3M);
                listMedicineNameRecognitionCodeToTake.put("Serkep 250/25", InhalerDetectionActivity.InhalerType.MDS3M);
            }
            if (Locale.getDefault().getLanguage().contentEquals("pt")) { //Rute
                listMedicineNameRecognitionCodeToTake.put("Symbicort 160/4.5", InhalerDetectionActivity.InhalerType.Symbicort);
            } else if (Locale.getDefault().getLanguage().contentEquals("es")) { //Rute
                listMedicineNameRecognitionCodeToTake.put("Rilast 160/4.5", InhalerDetectionActivity.InhalerType.Symbicort);
                listMedicineNameRecognitionCodeToTake.put("Symbicort 160/4.5", InhalerDetectionActivity.InhalerType.Symbicort);
            } else {
                listMedicineNameRecognitionCodeToTake.put("Rilast 160/4.5", InhalerDetectionActivity.InhalerType.Symbicort);
                listMedicineNameRecognitionCodeToTake.put("Symbicort 160/4.5", InhalerDetectionActivity.InhalerType.Symbicort);
            }
            if (Locale.getDefault().getLanguage().contentEquals("pt")) { //Rute
                listMedicineNameRecognitionCodeToTake.put("Bretaris Genuair 322", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Brimica Genuair 340/12", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Budesonida Novolizer 200", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Budesonida Novolizer 400", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Duaklir Genuair 340/12", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Eklira Genuair 322", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Formoterol Novolizer 12", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Salbutamol Novolizer 100", InhalerDetectionActivity.InhalerType.Novoziler);
            } else if (Locale.getDefault().getLanguage().contentEquals("es")) { //Rute
                listMedicineNameRecognitionCodeToTake.put("Bretaris Genuair 322", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Brimica Genuair 340/12", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Duaklir Genuair 340/12", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Eklira Genuair 322", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Formatris Novolizer 12", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Novopulm Novolizer 200", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Novopulm Novolizer 400", InhalerDetectionActivity.InhalerType.Novoziler);
            } else {
                listMedicineNameRecognitionCodeToTake.put("Bretaris Genuair 322", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Brimica Genuair 340/12", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Budesonida Novolizer 200", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Budesonida Novolizer 400", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Duaklir Genuair 340/12", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Eklira Genuair 322", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Formatris Novolizer 12", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Formoterol Novolizer 12", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Novopulm Novolizer 200", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Novopulm Novolizer 400", InhalerDetectionActivity.InhalerType.Novoziler);
                listMedicineNameRecognitionCodeToTake.put("Salbutamol Novolizer 100", InhalerDetectionActivity.InhalerType.Novoziler);
            }
            if (Locale.getDefault().getLanguage().contentEquals("pt")) { //Rute
                listMedicineNameRecognitionCodeToTake.put("Seretaide Inalador 125/25", InhalerDetectionActivity.InhalerType.Seretide);
                listMedicineNameRecognitionCodeToTake.put("Seretaide Inalador 250/25", InhalerDetectionActivity.InhalerType.Seretide);
                listMedicineNameRecognitionCodeToTake.put("Seretaide Inalador 50/25", InhalerDetectionActivity.InhalerType.Seretide);
            } else if (Locale.getDefault().getLanguage().contentEquals("es")) { //Rute
                listMedicineNameRecognitionCodeToTake.put("Seretide 125/25", InhalerDetectionActivity.InhalerType.Seretide);
                listMedicineNameRecognitionCodeToTake.put("Seretide 250/25", InhalerDetectionActivity.InhalerType.Seretide);
                listMedicineNameRecognitionCodeToTake.put("Seretide 50/25", InhalerDetectionActivity.InhalerType.Seretide);
            } else {
                listMedicineNameRecognitionCodeToTake.put("Seretaide Inalador 125/25", InhalerDetectionActivity.InhalerType.Seretide);
                listMedicineNameRecognitionCodeToTake.put("Seretaide Inalador 250/25", InhalerDetectionActivity.InhalerType.Seretide);
                listMedicineNameRecognitionCodeToTake.put("Seretaide Inalador 50/25", InhalerDetectionActivity.InhalerType.Seretide);
                listMedicineNameRecognitionCodeToTake.put("Seretide 125/25", InhalerDetectionActivity.InhalerType.Seretide);
                listMedicineNameRecognitionCodeToTake.put("Seretide 250/25", InhalerDetectionActivity.InhalerType.Seretide);
                listMedicineNameRecognitionCodeToTake.put("Seretide 50/25", InhalerDetectionActivity.InhalerType.Seretide);
            }
            listMedicineNameRecognitionCodeToTake.put("BiResp Spiromax 160/4.5", InhalerDetectionActivity.InhalerType.Spiromax);
            listMedicineNameRecognitionCodeToTake.put("BiResp Spiromax 320/9", InhalerDetectionActivity.InhalerType.Spiromax);
            listMedicineNameRecognitionCodeToTake.put("DuoResp Spiromax 160/4.5", InhalerDetectionActivity.InhalerType.Spiromax);
            listMedicineNameRecognitionCodeToTake.put("DuoResp Spiromax 320/9", InhalerDetectionActivity.InhalerType.Spiromax);
            if (Locale.getDefault().getLanguage().contentEquals("pt")) { //Rute
                listMedicineNameRecognitionCodeToTake.put("Assieme Turbohaler 160/4.5", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Assieme Turbohaler 80/4.5", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Assieme Turbohaler 320/9", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Bricanyl Turbohaler 500", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Oxis Turbohaler 9", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Pulmicort Turbohaler 200", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Pulmicort Turbohaler 400", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Symbicort Turbohaler 80/4.5", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Symbicort Turbohaler 160/4.5", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Symbicort Turbohaler 320/9", InhalerDetectionActivity.InhalerType.Turbohaler);
            } else if (Locale.getDefault().getLanguage().contentEquals("es")) { //Rute
                listMedicineNameRecognitionCodeToTake.put("Oxis Turbuhaler 4.5", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Oxis Turbuhaler 9", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Pulmicort Turbuhaler 100", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Pulmicort Turbuhaler 200", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Pulmicort Turbuhaler 400", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Rilast Turbuhaler 80/4.5", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Rilast Turbuhaler 160/4.5", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Rilast Forte Turbuhaler 320/9", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Symbicort Turbohaler 80/4.5", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Symbicort Turbohaler 160/4.5", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Symbicort Forte Turbohaler 320/9", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Terbasmin Turbuhaler 500", InhalerDetectionActivity.InhalerType.Turbohaler);
            } else {
                listMedicineNameRecognitionCodeToTake.put("Assieme Turbohaler 160/4.5", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Assieme Turbohaler 80/4.5", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Assieme Turbohaler 320/9", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Bricanyl Turbohaler 500", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Oxis Turbohaler 4.5", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Oxis Turbohaler 9", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Pulmicort Turbohaler 100", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Pulmicort Turbohaler 200", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Pulmicort Turbohaler 400", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Rilast Turbuhaler 80/4.5", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Rilast Turbuhaler 160/4.5", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Rilast Forte Turbuhaler 320/9", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Symbicort Turbohaler 80/4.5", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Symbicort Turbohaler 160/4.5", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Symbicort Turbohaler 320/9", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Symbicort Forte Turbohaler 320/9", InhalerDetectionActivity.InhalerType.Turbohaler);
                listMedicineNameRecognitionCodeToTake.put("Terbasmin Turbuhaler 500", InhalerDetectionActivity.InhalerType.Turbohaler);
            }
            listMedicineNameRecognitionCodeToTake.put("Asmanex Twisthaler 200", InhalerDetectionActivity.InhalerType.Twisthaler);
            listMedicineNameRecognitionCodeToTake.put("Asmanex Twisthaler 400", InhalerDetectionActivity.InhalerType.Twisthaler);
            listMedicineNameRecognitionCodeToTake.put("Foster Nexthaler 100/6", InhalerDetectionActivity.InhalerType.NextHaler);
            listMedicineNameRecognitionCodeToTake.put("Foster Nexthaler 200/6", InhalerDetectionActivity.InhalerType.NextHaler);
            listMedicineNameRecognitionCodeToTake.put("Formodual Nexthaler 100/6", InhalerDetectionActivity.InhalerType.NextHaler);
            listMedicineNameRecognitionCodeToTake.put("FormodualNexthaler 200/6", InhalerDetectionActivity.InhalerType.NextHaler);
        }
            return listMedicineNameRecognitionCodeToTake;
    }

    private static HashMap<String,String> getListMedicineNamePillsToTake(){
        if(listMedicineNamePillsToTake == null) {
            listMedicineNamePillsToTake = new HashMap<>();

            listMedicineNamePillsToTake.put("Actifed 60 mg + 2.5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Aerinaze 2.5 mg + 120 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Aerius 2.5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Aerius 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Atarax 25 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Azomyr 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Bilaxten 20 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Bilaxten 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Cetirizina Alter Genéricos 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Cetirizina Aurobindo 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Cetirizina Aurovitas 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Cetirizina Baldacci 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Cetirizina Bluepharma 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Cetirizina Ciclum 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Cetirizina Farmoz 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Cetirizina Generis 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Cetirizina Germed 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Cetirizina GP 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Cetirizina Jaba 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Cetirizina Mylan 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Cetirizina ratiopharm 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Cetirizina Sandoz 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Cetirizina Zentiva 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Cetix 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Claridon 5 mg + 120 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Claridon QD 10 mg + 240 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Claritine 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Dazenar 30 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Acizan 30 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Acizan 6 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Almus 30 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Almus 6 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Alter 30 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Alter 6 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Ciclum 30 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Ciclum 6 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Cinfa 30 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Cinfa 6 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Farmoz 30 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Farmoz 6 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Generis 30 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Generis 6 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte GP 30 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte GP 6 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Jaba 30 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Jaba 6 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Ratiopharm 30 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Ratiopharm 6 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Tolife 30 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Deflazacorte Tolife 6 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Actavis 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Alter 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Aristo 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Aurovitas 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Basi 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Bluepharma 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Ciclum 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Cinfa 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Farmoz 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Generis 2.5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Generis 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Krka 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Labesfal 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Labesfal OD 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Mepha 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Mylan 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Pharmakern 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Ratiopharm 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Sandoz 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina Teva 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Desloratadina toLife 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Dinaxil 60 mg + 2.5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Ebastina Alter 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Ebastina Cinfa 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Ebastina Cinfa 20 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Ebastina Generis 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Ebastina Generis 20 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Ebastina Mylan 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Ebastina Mylan 20 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Ebastina Qualitec 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Fexofenadina Mylan 120 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Filotempo 225 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Heperpoll Maçã 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Hidroxizina Farmoz 25 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Hidroxizina Generis 25 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Hidroxizina Pentafarma 25 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Histacet 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Histexil 25 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Kestine 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Kestine 20 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Lepicortinolo 20 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Lepicortinolo 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Lergonix 20 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Lergonix 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Levocetirizina Aurovitas 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Levocetirizina Bluefish 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Levocetirizina Ciclum 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Levocetirizina Fair-Med 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Levocetirizina Generis 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Levocetirizina Krka 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Levocetirizina Labesfal 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Levocetirizina ratiopharm 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Levocetirizina Sandoz 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Levocetirizina Tolife 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Levocetirizina Wynn 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Levocetirizina Zentiva 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Loratadina 1Apharma 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Loratadina Aurovitas 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Loratadina Basi 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Loratadina Generis 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Loratadina Germed 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Loratadina Labesfal 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Loratadina Mylan 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Loratadina Ratiopharm 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Lukair 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Medrol 16 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Medrol 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Alter 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Ascafi 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Aurobindo 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Aurobindo 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Aurobindo 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Aurovitas 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Aurovitas 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Aurovitas 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Azevedos 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Azevedos 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Azevedos 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Baldacci 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Bluepharma 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Bluepharma 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Bluepharma 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Chesmon 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Ciclum 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Ciclum 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Ciclum 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Cinfa 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Cinfa 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Cinfa 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Farmoz 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Generis 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Generis 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Generis 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Krka 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Krka 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Krka 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Labesfal 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Labesfal 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Labesfal 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Mylan 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Mylan 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Mylan 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Pharmakern 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Pharmakern 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Pharmakern 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Ratiopharm 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Ratiopharm 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Ratiopharm 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Sandoz 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Sandoz 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Sandoz 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Tecnigen 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Tecnilor 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Tetrafarma 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Teva 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Teva 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Teva 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Tolife 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Tolife 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Montelucaste Tolife 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Nargoran 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Nazonite 120 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Neufil 500 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Onsudil 0.05 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Primalan 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Rinialer 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Rinocalm 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Rosilan 30 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Rosilan 6 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Rovinex 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Rupatadina Bluefish 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Rupatadina Farmoz 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Rupatadina Generis 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Rupatadina Mylan 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Rupatadina Ratiopharm 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Singulair 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Singulair 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Singulair 10 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Tavégyl 1 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Telfast 120 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Telfast 180 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Unicontin 400 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Ventilan 4 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Viternum 6 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Xyzal 5 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Zaditen 1 mg",MedicineTypeAux.TYPE2_CODE);
            listMedicineNamePillsToTake.put("Zyrtec 10 mg",MedicineTypeAux.TYPE2_CODE);

            if (Locale.getDefault().getLanguage().contentEquals("pt")) { //Rute & JValente 28.10.2019
                listMedicineNamePillsToTake.put("Actifed 60 mg + 2.5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Aerinaze 2.5 mg + 120 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Aerius 2.5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Aerius 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Atarax 25 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Azomyr 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Bilaxten 20 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Bilaxten 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Alter GenÃ©ricos 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Aurobindo 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Aurovitas 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Baldacci 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Bluepharma 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Ciclum 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Farmoz 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Generis 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Germed 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina GP 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Jaba 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Mylan 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina ratiopharm 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Sandoz 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Zentiva 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetix 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Claridon 5 mg + 120 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Claridon QD 10 mg + 240 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Claritine 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Dazenar 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Acizan 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Acizan 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Almus 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Almus 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Alter 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Alter 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Ciclum 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Ciclum 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Cinfa 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Cinfa 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Farmoz 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Farmoz 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Generis 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Generis 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte GP 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte GP 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Jaba 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Jaba 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Ratiopharm 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Ratiopharm 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Tolife 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Tolife 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Actavis 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Alter 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Aristo 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Aurovitas 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Basi 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Bluepharma 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Ciclum 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Cinfa 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Farmoz 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Generis 2.5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Generis 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Krka 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Labesfal 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Labesfal OD 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Mepha 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Mylan 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Pharmakern 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Ratiopharm 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Sandoz 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Teva 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina toLife 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Dinaxil 60 mg + 2.5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Alter 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Cinfa 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Cinfa 20 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Generis 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Generis 20 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Mylan 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Mylan 20 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Qualitec 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Fexofenadina Mylan 120 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Filotempo 225 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Heperpoll MaÃ§Ã£ 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Hidroxizina Farmoz 25 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Hidroxizina Generis 25 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Hidroxizina Pentafarma 25 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Histacet 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Histexil 25 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Kestine 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Kestine 20 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Lepicortinolo 20 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Lepicortinolo 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Lergonix 20 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Lergonix 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Aurovitas 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Bluefish 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Ciclum 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Fair-Med 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Generis 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Krka 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Labesfal 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina ratiopharm 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Sandoz 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Tolife 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Wynn 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Zentiva 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina 1Apharma 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Aurovitas 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Basi 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Generis 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Germed 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Labesfal 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Mylan 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Ratiopharm 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Lukair 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Medrol 16 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Medrol 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Alter 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Ascafi 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Aurobindo 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Aurobindo 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Aurobindo 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Aurovitas 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Aurovitas 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Aurovitas 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Azevedos 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Azevedos 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Azevedos 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Baldacci 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Bluepharma 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Bluepharma 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Bluepharma 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Chesmon 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Ciclum 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Ciclum 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Ciclum 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Cinfa 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Cinfa 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Cinfa 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Farmoz 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Generis 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Generis 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Generis 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Krka 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Krka 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Krka 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Labesfal 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Labesfal 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Labesfal 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Mylan 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Mylan 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Mylan 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Pharmakern 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Pharmakern 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Pharmakern 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Ratiopharm 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Ratiopharm 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Ratiopharm 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Sandoz 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Sandoz 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Sandoz 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Tecnigen 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Tecnilor 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Tetrafarma 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Teva 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Teva 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Teva 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Tolife 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Tolife 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Tolife 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Nargoran 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Nazonite 120 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Neufil 500 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Onsudil 0.05 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Primalan 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rinialer 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rinocalm 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rosilan 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rosilan 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rovinex 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Bluefish 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Farmoz 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Generis 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Mylan 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Ratiopharm 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Singulair 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Singulair 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Singulair 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("TavÃ©gyl 1 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Telfast 120 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Telfast 180 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Unicontin 400 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ventilan 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Viternum 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Xyzal 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Zaditen 1 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Zyrtec 10 mg",MedicineTypeAux.TYPE2_CODE);
            }
            else if(Locale.getDefault().getLanguage().contentEquals("es")) { //Rute
                listMedicineNamePillsToTake.put("Aerius 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Aerius 5 mg BUCODISPERSABLES",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Alastina 20 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Alercina 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Alergoliber 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Alerlisin 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Aralevo 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Atarax 25mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Bactil 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Bactil Forte 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Bidiclin 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Bilaxten 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Bilaxten 20 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Almus 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Alter 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Apotex 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Aristo 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Cinfa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Kern Pharma 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Mylan 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Normon 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Pensa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Ratiopharm 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Sandoz 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Tarbis 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Tevagen 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Civeran 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Clarityne",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Clarityne 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Clipper 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cortiment 9mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Dacortin 2,5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Dacortin 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Dacortin 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Dasselta 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Alter 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Alter 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Cinfa 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Cinfa 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Efarmes 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Efarmes 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Kern Pharma 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Kern Pharma 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Normon 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Normon 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Pensa 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Pensa 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Ranbaxy 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Ranbaxy 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Sandoz 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Sandoz 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Stada 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Stada 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Tarbis 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Veris 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Veris 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Vir 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Vir 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Actavis 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Almus 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Alter  5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Alter 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Aristo 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Aurovitas 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Cinfamed 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Cipla 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Combix 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Flas Combix 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Kern Pharma 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina KRKA 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Mylan 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Normon 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Qualigen 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Sandoz 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Stada  5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Tarbis 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Tecnigen 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina TEVA 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina VIR 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Viso Farmaceutica 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Dezacor 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Dezacor 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastel 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastel Flas 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastel forte 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastel Forte Flas 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Alprofarma 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Alter 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Alter 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Apotex 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Apotex 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Aristo 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Aristo 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Cinfa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Cinfa 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Combix 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Combix 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Flas Cinfa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Flas Cinfa 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Flas Stadagen 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Forte Flas Stadagen 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Kern Pharma 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Kern Pharma 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Mylan 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Mylan 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Normon 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Normon 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Qualigen 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Qualigen 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Qualitec 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Qualitec 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Quasset 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Quasset 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Ratiopharm 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Ratiopharm 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Sandoz 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Sandoz 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Stada 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Stada 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Tarbis 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Tarbis 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Tecnigen 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Teva 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Teva 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Tevagen 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Tevagen 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Vir 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Vir 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Fexofenadina Cipla 120mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Fexofenadina Cipla 180mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Fexofenadina Sanofi 180mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Hidroxizina Bluefish 25mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Hidroxizina Qualigen 25mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("IBIS 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("IBIS 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Apotex 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Aurovitas Spain 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Bluefish 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Cipla 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina KRKA 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Mylan 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Normon 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Pensa 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Ratiopharm 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Sandoz 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Stada 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Tarbis 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Teva 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Almus 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Aristo 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Cinfa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Combix 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Edigen 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Falmalider 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Kern Pharma 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Mylan 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Normon 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Qualigen 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Ranbaxy",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Ratiopharm 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Sandoz 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Stada 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Tarbis",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Teva 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Vir 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Mircol 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Monkasta 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Monkasta 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Monkasta 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Accord  5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Accord 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Almus 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Alter 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Alter 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Alter 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Apotex AG 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Asthmapharma 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Asthmapharma 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Asthmapharma 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Aurovitas  5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Aurovitas 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Aurovitas 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Aurovitas Spain  5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Aurovitas Spain 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Aurovitas Spain 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Cinfa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Cinfa 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Cinfa 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Combix 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Kern Pharma 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Kern Pharma 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Kern Pharma 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Mabo 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Mabo 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Mabo 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Mylan 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Mylan 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Mylan 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Normon 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Normon 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Normon 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Pensa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Pensa 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Pensa 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Qualigen  4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Qualigen 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Qualigen 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Ranbaxy 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Ratiopharm 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Ratiopharm 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Ratiopharm 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Sandoz 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Sandoz 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Sandoz 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Stada 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Stada 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Stada 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Tarbis 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Tarbis 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Tarbis 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Tecnigen 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Teva 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Teva 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Teva 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast UR 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast UR 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast UR 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast VIR 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast VIR 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast VIR 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Muntel 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Narine Repetabs",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Pluralais 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Pluralais 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Pluralais 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Alonga 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Alonga 50mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Alonga 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Cinfa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Cinfa 2,5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Cinfa 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Cinfa 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Kern Pharma 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Kern Pharma 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Kern Pharma 5mg ",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Pensa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Pensa 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Pensa 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Tarbis 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Tarbis 2,5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Tarbis 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Tarbis 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rinaler 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupafin 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Aurovitas 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Bluefish 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Cinfa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Kern Pharma 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Normon 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Ratiopharm 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Stada 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Teva 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Singulaire 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Singulaire 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Singulaire 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Telfast 120mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Urbason 16mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Urbason 40mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Urbason 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ventolin",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Xazal 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Zamene 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Zamene 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Zyrtec 10mg",MedicineTypeAux.TYPE2_CODE);
            }else {
                listMedicineNamePillsToTake.put("Actifed 60 mg + 2.5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Aerinaze 2.5 mg + 120 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Aerius 2.5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Aerius 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Alastina 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Alercina 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Alergoliber 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Alerlisin 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Aralevo 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Atarax 25 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Azomyr 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Bactil 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Bactil Forte 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Bidiclin 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Bilaxten 20 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Bilaxten 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Almus 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Alter GenÃ©ricos 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Apotex 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Aristo 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Aurobindo 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Aurovitas 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Baldacci 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Bluepharma 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Ciclum 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Cinfa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Farmoz 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Generis 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Germed 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina GP 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Jaba 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Kern Pharma 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Mylan 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Normon 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Pensa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Ratiopharm 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Sandoz 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Zentiva 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Tarbis 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetirizina Tevagen 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cetix 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Civeran 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Claridon 5 mg + 120 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Claridon QD 10 mg + 240 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Claritine 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Dazenar 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Clarityne",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Clarityne 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Clipper 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Cortiment 9mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Dacortin 2,5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Dacortin 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Dacortin 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Dasselta 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Acizan 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Acizan 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Almus 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Almus 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Alter 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Alter 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Ciclum 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Ciclum 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Cinfa 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Cinfa 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Efarmes 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Efarmes 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Farmoz 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Farmoz 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Generis 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Generis 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte GP 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte GP 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Jaba 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Jaba 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Kern Pharma 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Kern Pharma 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Normon 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Normon 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Pensa 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Pensa 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Ranbaxy 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Ranbaxy 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Ratiopharm 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Ratiopharm 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Sandoz 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Sandoz 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Stada 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Stada 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Tarbis 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Tolife 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacorte Tolife 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Veris 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Veris 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Vir 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Deflazacort Vir 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Actavis 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Almus 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Actavis 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Alter 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Aristo 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Aurovitas 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Basi 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Bluepharma 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Ciclum 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Cinfa 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Cinfamed 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Cipla 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Combix 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Flas Combix 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Farmoz 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Generis 2.5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Generis 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Kern Pharma 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Krka 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Labesfal 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Labesfal OD 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Mepha 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Mylan 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Normon 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Pharmakern 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Qualigen 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Ratiopharm 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Sandoz 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Stada  5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Tarbis 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Tecnigen 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Teva 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina toLife 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina VIR 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Desloratadina Viso Farmaceutica 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Dezacor 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Dezacor 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Dinaxil 60 mg + 2.5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastel 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastel Flas 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastel forte 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastel Forte Flas 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Alter 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Cinfa 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Cinfa 20 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Generis 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Generis 20 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Mylan 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Mylan 20 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Qualitec 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Alprofarma 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Alter 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Alter 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Apotex 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Apotex 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Aristo 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Aristo 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Cinfa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Cinfa 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Combix 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Combix 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Flas Cinfa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Flas Cinfa 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Flas Stadagen 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Forte Flas Stadagen 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Kern Pharma 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Kern Pharma 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Mylan 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Mylan 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Normon 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Normon 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Qualigen 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Qualigen 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Qualitec 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Qualitec 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Quasset 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Quasset 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Ratiopharm 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Ratiopharm 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Sandoz 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Sandoz 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Stada 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Stada 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Tarbis 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Tarbis 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Tecnigen 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Teva 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Teva 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Tevagen 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Tevagen 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Vir 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ebastina Vir 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Fexofenadina Cipla 120mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Fexofenadina Cipla 180mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Fexofenadina Sanofi 180mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Hidroxizina Bluefish 25mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Hidroxizina Qualigen 25mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("IBIS 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("IBIS 20mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Apotex 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Aurovitas Spain 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Bluefish 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Cipla 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina KRKA 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Mylan 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Normon 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Pensa 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Ratiopharm 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Sandoz 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Stada 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Tarbis 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Teva 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Almus 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Aristo 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Cinfa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Combix 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Edigen 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Falmalider 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Kern Pharma 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Mylan 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Normon 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Qualigen 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Ranbaxy",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Ratiopharm 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Sandoz 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Stada 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Tarbis",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Teva 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Vir 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Mircol 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Monkasta 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Monkasta 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Monkasta 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Accord  5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Accord 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Almus 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Alter 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Alter 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Alter 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Apotex AG 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Asthmapharma 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Asthmapharma 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Asthmapharma 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Aurovitas  5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Aurovitas 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Aurovitas 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Aurovitas Spain  5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Aurovitas Spain 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Aurovitas Spain 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Cinfa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Cinfa 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Cinfa 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Combix 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Kern Pharma 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Kern Pharma 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Kern Pharma 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Mabo 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Mabo 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Mabo 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Mylan 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Mylan 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Mylan 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Normon 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Normon 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Normon 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Pensa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Pensa 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Pensa 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Qualigen  4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Qualigen 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Qualigen 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Ranbaxy 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Ratiopharm 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Ratiopharm 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Ratiopharm 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Sandoz 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Sandoz 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Sandoz 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Stada 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Stada 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Stada 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Tarbis 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Tarbis 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Tarbis 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Tecnigen 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Teva 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Teva 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast Teva 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast UR 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast UR 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast UR 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast VIR 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast VIR 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelukast VIR 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Muntel 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Narine Repetabs",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Pluralais 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Pluralais 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Pluralais 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Alonga 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Alonga 50mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Alonga 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Cinfa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Cinfa 2,5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Cinfa 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Cinfa 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Kern Pharma 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Kern Pharma 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Kern Pharma 5mg ",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Pensa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Pensa 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Pensa 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Tarbis 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Tarbis 2,5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Tarbis 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Prednisona Tarbis 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rinaler 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupafin 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Aurovitas 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Bluefish 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Cinfa 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Kern Pharma 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Normon 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Ratiopharm 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Stada 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Teva 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Singulaire 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Singulaire 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Singulaire 5mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Telfast 120mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Urbason 16mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Urbason 40mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Urbason 4mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ventolin",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Xazal 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Zamene 30mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Zamene 6mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Zyrtec 10mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Fexofenadina Mylan 120 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Filotempo 225 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Heperpoll MaÃ§Ã£ 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Hidroxizina Farmoz 25 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Hidroxizina Generis 25 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Hidroxizina Pentafarma 25 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Histacet 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Histexil 25 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Kestine 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Kestine 20 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Lepicortinolo 20 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Lepicortinolo 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Lergonix 20 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Lergonix 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Aurovitas 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Bluefish 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Ciclum 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Fair-Med 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Generis 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Krka 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Labesfal 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina ratiopharm 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Sandoz 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Tolife 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Wynn 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Levocetirizina Zentiva 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina 1Apharma 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Aurovitas 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Basi 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Generis 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Germed 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Labesfal 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Mylan 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Loratadina Ratiopharm 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Lukair 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Medrol 16 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Medrol 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Alter 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Ascafi 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Aurobindo 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Aurobindo 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Aurobindo 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Aurovitas 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Aurovitas 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Aurovitas 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Azevedos 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Azevedos 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Azevedos 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Baldacci 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Bluepharma 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Bluepharma 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Bluepharma 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Chesmon 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Ciclum 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Ciclum 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Ciclum 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Cinfa 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Cinfa 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Cinfa 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Farmoz 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Generis 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Generis 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Generis 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Krka 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Krka 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Krka 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Labesfal 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Labesfal 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Labesfal 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Mylan 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Mylan 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Mylan 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Pharmakern 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Pharmakern 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Pharmakern 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Ratiopharm 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Ratiopharm 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Ratiopharm 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Sandoz 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Sandoz 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Sandoz 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Tecnigen 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Tecnilor 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Tetrafarma 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Teva 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Teva 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Teva 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Tolife 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Tolife 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Montelucaste Tolife 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Nargoran 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Nazonite 120 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Neufil 500 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Onsudil 0.05 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Primalan 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rinialer 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rinocalm 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rosilan 30 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rosilan 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rovinex 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Bluefish 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Farmoz 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Generis 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Mylan 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Rupatadina Ratiopharm 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Singulair 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Singulair 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Singulair 10 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("TavÃ©gyl 1 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Telfast 120 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Telfast 180 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Unicontin 400 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Ventilan 4 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Viternum 6 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Xyzal 5 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Zaditen 1 mg",MedicineTypeAux.TYPE2_CODE);
                listMedicineNamePillsToTake.put("Zyrtec 10 mg",MedicineTypeAux.TYPE2_CODE);
            }
        }

        return listMedicineNamePillsToTake;
    }

    private static HashMap<String,String> getListMedicineNameOthersToTake(){
        if(listMedicineNameOthersToTake == null) {
            listMedicineNameOthersToTake = new HashMap<>();

            listMedicineNameOthersToTake.put("Aeromax Nasal",MedicineTypeAux.TYPE3_CODE);
            listMedicineNameOthersToTake.put("Allergodil",MedicineTypeAux.TYPE3_CODE);
            listMedicineNameOthersToTake.put("Avamys",MedicineTypeAux.TYPE3_CODE);
            listMedicineNameOthersToTake.put("Flonaze",MedicineTypeAux.TYPE3_CODE);
            listMedicineNameOthersToTake.put("Flutaide",MedicineTypeAux.TYPE3_CODE);
            listMedicineNameOthersToTake.put("Fluticasona Nasofan",MedicineTypeAux.TYPE3_CODE);
            listMedicineNameOthersToTake.put("Inspirom",MedicineTypeAux.TYPE3_CODE);
            listMedicineNameOthersToTake.put("Mometasona Alter",MedicineTypeAux.TYPE3_CODE);
            listMedicineNameOthersToTake.put("Mometasona Generis",MedicineTypeAux.TYPE3_CODE);
            listMedicineNameOthersToTake.put("Mometasona Ratiopharm",MedicineTypeAux.TYPE3_CODE);
            listMedicineNameOthersToTake.put("Mometasona Sandoz",MedicineTypeAux.TYPE3_CODE);
            listMedicineNameOthersToTake.put("Mometasona Teva",MedicineTypeAux.TYPE3_CODE);
            listMedicineNameOthersToTake.put("Nasomet",MedicineTypeAux.TYPE3_CODE);
            listMedicineNameOthersToTake.put("Neo-Sinefrina Alergo",MedicineTypeAux.TYPE3_CODE);
            listMedicineNameOthersToTake.put("Rhinizill",MedicineTypeAux.TYPE3_CODE);
            listMedicineNameOthersToTake.put("Pulmicort Nasal Aqua 32",MedicineTypeAux.TYPE3_CODE);
            listMedicineNameOthersToTake.put("Pulmicort Nasal Aqua 64",MedicineTypeAux.TYPE3_CODE);

        }

        return listMedicineNameOthersToTake;
    }

    public static ArrayList<String> getMedicinesNeedInlNames(Inhaler inhalerAux){

        HashMap<String,InhalerDetectionActivity.InhalerType> medicinesNeedInlNamesAux = getListMedicineNameRecognitionCodeToTake();

        if (inhalerAux != null) {
            HashMap<String, InhalerDetectionActivity.InhalerType> tempaux = new HashMap<>();

            for (HashMap.Entry<String, InhalerDetectionActivity.InhalerType> aux : medicinesNeedInlNamesAux.entrySet()) {
                if (aux.getValue().equals(inhalerAux.getType())) {
                    tempaux.put(aux.getKey(), inhalerAux.getType());
                }
            }
            medicinesNeedInlNamesAux = tempaux;
        }

        medicinesNeedInlNames = new ArrayList<>();

        medicinesNeedInlNames.addAll(medicinesNeedInlNamesAux.keySet());

        Collections.sort(medicinesNeedInlNames, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });

        return medicinesNeedInlNames;
    }

    public static ArrayList<String> getMedicinesNeedInlNamesPills(){

        HashMap<String,String> medicinesNeedInlNamesAux = getListMedicineNamePillsToTake();

        medicinesNeedInlNames = new ArrayList<>();

        medicinesNeedInlNames.addAll(medicinesNeedInlNamesAux.keySet());

        Collections.sort(medicinesNeedInlNames, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });

        return medicinesNeedInlNames;
    }

    public static ArrayList<String> getMedicinesNeedInlNamesOthers(){

        HashMap<String,String> medicinesNeedInlNamesAux = getListMedicineNameOthersToTake();

        medicinesNeedInlNames = new ArrayList<>();

        medicinesNeedInlNames.addAll(medicinesNeedInlNamesAux.keySet());

        Collections.sort(medicinesNeedInlNames, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });

        return medicinesNeedInlNames;
    }

    public static ArrayList<MedicineType> getMedicineTypes(Context context){
        if(listMediceTypes==null){
            listMediceTypes = new ArrayList<>();

            listMediceTypes.add(new MedicineType(MedicineTypeAux.TYPE1_CODE, context.getString(R.string.medicine_type_1)));
            listMediceTypes.add(new MedicineType(MedicineTypeAux.TYPE2_CODE, context.getString(R.string.medicine_type_2)));
            listMediceTypes.add(new MedicineType(MedicineTypeAux.TYPE3_CODE, context.getString(R.string.medicine_type_4)));
        }

        return listMediceTypes;
    }

    public static CharSequence[] getMedicineTypesNames(Context context){
        ArrayList<MedicineType> aux = getMedicineTypes(context);

        CharSequence[] result = new CharSequence[aux.size()];

        int pos = 0;

        for(MedicineType medicineType : aux){
            result[pos] = medicineType.getName();
            pos++;
        }

        return result;
    }

    public static MedicineType getMedicineType(String code,Context context){
        MedicineType aux = null;

        for(MedicineType m : getMedicineTypes(context)){
            if(m.getCode().equalsIgnoreCase(code)){
                aux = m;
                break;
            }
        }

        return aux;
    }

    public static int getMedicineTypeIcon(String code){
        return code.equals(TYPE1_CODE)? R.drawable.timeline_logo_inl:(code.equals(TYPE2_CODE)?R.drawable.timeline_logo_pils:R.drawable.timeline_logo_inl_nar);
    }

    public static int getTextResourceForTotal(String code) {
        return code.equals(TYPE1_CODE)? R.plurals.medicine_type_inal:(code.equals(TYPE2_CODE)?R.plurals.medicine_type_pil:R.plurals.medicine_type_inal_nar);
    }

    public static int getTextResource(String code) {
        return code.equals(TYPE1_CODE)? R.string.medicine_type_1:(code.equals(TYPE2_CODE)?R.string.medicine_type_2:R.string.medicine_type_4);
    }


    public static boolean needsDosesAndBarcode(String medicineName, MedicineType auxMedicineType) {
        return !medicineName.isEmpty() && checkMedicineName(medicineName) && auxMedicineType!=null && auxMedicineType.getCode().equals(TYPE1_CODE);
    }

    public static boolean needsShowName(MedicineType auxMedicineType) {
        return !auxMedicineType.getCode().equals(TYPE1_CODE);
    }

    public static boolean needsOpenRecognitionToTake(String medicineName, MedicineType auxMedicineType) {
        return !medicineName.isEmpty() && checkMedicineName(medicineName) && auxMedicineType!=null && (auxMedicineType.getCode().equals(TYPE1_CODE) || auxMedicineType.getCode().equals(TYPE2_CODE) || auxMedicineType.getCode().equals(TYPE3_CODE));
    }

    public static InhalerDetectionActivity.InhalerType getRecognitionCodeToTake(String medicineName) {
        InhalerDetectionActivity.InhalerType aux = getListMedicineNameRecognitionCodeToTake().get(medicineName);
        return aux!=null?aux:InhalerDetectionActivity.InhalerType.Unknown;//InhalerDetectionActivity.InhalerType.Flutiform;//!medicineName.isEmpty() && checkMedicineName(medicineName) && auxMedicineType!=null && auxMedicineType.getCode().equals(TYPE1_CODE);
    }

    private static boolean checkMedicineName(String medicineName) {
        boolean contains = false;

        ArrayList<String> medicineNames = getMedicinesNeedInlNames(null);

        for(String s : medicineNames){
            contains = s.equalsIgnoreCase(medicineName);

            if(contains){
                break;
            }
        }
        return contains;
    }
}
