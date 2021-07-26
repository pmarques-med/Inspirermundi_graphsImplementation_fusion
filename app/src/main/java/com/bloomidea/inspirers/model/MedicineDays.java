package com.bloomidea.inspirers.model;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.application.AppController;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by michellobato on 21/03/17.
 */

public class MedicineDays implements Serializable{
    public static final int ALL_DAYS_OPTION = 1;
    public static final int SPEC_DAYS_OPTION = 2;
    public static final int INT_DAYS_OPTION = 3;

    private int selectedOption;
    private ArrayList<Days> selectedDays;
    private Integer intervalDays;

    public MedicineDays(int selectedOption, ArrayList<Days> selectedDays, Integer intervalDays) {

        this.selectedOption = selectedOption;
        this.selectedDays = selectedDays;
        this.intervalDays = intervalDays;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }

    public ArrayList<Days> getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(ArrayList<Days> selectedDays) {
        this.selectedDays = selectedDays;
    }

    public String getSelectedDaysText(ArrayList<Days> selected) {
        String auxStringDays = "";
        int count = 0;
        for (Days auxDay : selected){
            auxStringDays = auxStringDays + auxDay.getName() + (count == (selected.size() - 1) ? "" : ", ");
            count++;
        }
        return auxStringDays;
    }

    public Integer getIntervalDays() {
        return intervalDays;
    }

    public void setIntervalDays(Integer intervalDays) {
        this.intervalDays = intervalDays;
    }

    public String getDayDescByCode(int code){
        if (code == ALL_DAYS_OPTION){
            return AppController.getmInstance().getString(R.string.new_step_all_days);
        }else if (code == SPEC_DAYS_OPTION){
            return AppController.getmInstance().getString(R.string.new_step_scpec_days);
        }else{
            return AppController.getmInstance().getString(R.string.new_step_int_days);
        }
    }
}
