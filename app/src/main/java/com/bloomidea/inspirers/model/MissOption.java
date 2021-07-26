package com.bloomidea.inspirers.model;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.application.AppController;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by michellobato on 17/03/17.
 */

public class MissOption implements Serializable {

    private int code;
    private boolean selected;
    private String name;

    public MissOption(int code, boolean selected, String name) {
        this.code = code;
        this.selected = selected;
        this.name = name;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ArrayList<MissOption> getAllMissOptions(){
        ArrayList<MissOption> aux = new ArrayList<MissOption>();

        aux.add(new MissOption(1,false, AppController.getmInstance().getString(R.string.miss_option_one)));
        aux.add(new MissOption(2,false, AppController.getmInstance().getString(R.string.miss_option_two)));
        aux.add(new MissOption(3,false, AppController.getmInstance().getString(R.string.miss_option_three)));
        aux.add(new MissOption(4,false, AppController.getmInstance().getString(R.string.miss_option_four)));
        aux.add(new MissOption(5,false, AppController.getmInstance().getString(R.string.miss_option_five)));
        aux.add(new MissOption(6,false, AppController.getmInstance().getString(R.string.miss_option_six)));
        aux.add(new MissOption(7,false, AppController.getmInstance().getString(R.string.miss_option_seven)));
        aux.add(new MissOption(8,false, AppController.getmInstance().getString(R.string.miss_option_eight)));

        return aux;
    }
}
