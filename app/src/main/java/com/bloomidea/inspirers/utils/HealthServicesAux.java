package com.bloomidea.inspirers.utils;

import android.content.Context;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.model.HealthServices;

import java.util.ArrayList;

/**
 * Created by michellobato on 22/03/17.
 */

public class HealthServicesAux {
    public static final String HEALTH_CODE1 = "second_opt_no";
    public static final String HEALTH_CODE2 = "second_opt_doctor";
    public static final String HEALTH_CODE3 = "second_opt_urgency";
    public static final String HEALTH_CODE4 = "second_opt_internment";

    private static ArrayList<HealthServices> listHealthServices;

    public static ArrayList<HealthServices> getHealthServices(Context context){
        if(listHealthServices==null){
            listHealthServices = new ArrayList<>();

            listHealthServices.add(new HealthServices(HealthServicesAux.HEALTH_CODE1, context.getString(R.string.health_code_1)));
            listHealthServices.add(new HealthServices(HealthServicesAux.HEALTH_CODE2, context.getString(R.string.health_code_2)));
            listHealthServices.add(new HealthServices(HealthServicesAux.HEALTH_CODE3, context.getString(R.string.health_code_3)));
            listHealthServices.add(new HealthServices(HealthServicesAux.HEALTH_CODE4, context.getString(R.string.health_code_4)));
        }

        return listHealthServices;
    }

    public static CharSequence[] getHealthServicesNames(Context context){
        ArrayList<HealthServices> aux = getHealthServices(context);

        CharSequence[] result = new CharSequence[aux.size()];

        int pos = 0;

        for(HealthServices healthService : aux){
            result[pos] = healthService.getName();
            pos++;
        }

        return result;
    }

    public static HealthServices getHealthService(String code,Context context){
        HealthServices aux = null;

        for(HealthServices h : getHealthServices(context)){
            if(h.getCode().equalsIgnoreCase(code)){
                aux = h;
                break;
            }
        }

        return aux;
    }
}
