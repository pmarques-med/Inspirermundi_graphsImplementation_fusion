package com.bloomidea.inspirers.model;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * Created by michellobato on 22/03/17.
 */

public class MedicineSchedule implements Serializable{
    private Long id;
    private ScheduleAux selection;
    private ArrayList<MedicineTime> times;
    private MedicineDays days;

    public MedicineSchedule(Long id, ScheduleAux selection, ArrayList<MedicineTime> times, MedicineDays days) {
        this.id = id;
        this.selection = selection;
        this.times = times;
        this.days = days;
    }

    public MedicineSchedule(ScheduleAux selection, ArrayList<MedicineTime> times, MedicineDays days) {
        this.selection = selection;
        this.times = times;
        this.days = days;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScheduleAux getSelection() {
        return selection;
    }

    public void setSelection(ScheduleAux selectedCode) {
        this.selection = selectedCode;
    }

    public ArrayList<MedicineTime> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<MedicineTime> times) {
        this.times = times;
    }

    public MedicineDays getDays() {
        return days;
    }

    public void setDays(MedicineDays days) {
        this.days = days;
    }

}
