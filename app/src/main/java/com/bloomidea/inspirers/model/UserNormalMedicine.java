package com.bloomidea.inspirers.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by michellobato on 23/03/17.
 */

public class UserNormalMedicine extends UserMedicine {
    private int duration;
    private ArrayList<MedicineSchedule> schedules;

    public UserNormalMedicine(Long id, MedicineType medicineType, String medicineName, boolean shareWithDoctor, GregorianCalendar startDate, String note, ArrayList<MedicineInhaler> inhalers, int totalSOSDosage, int duration, ArrayList<MedicineSchedule> schedules, String nid) {
        super(id, medicineType, medicineName, shareWithDoctor, startDate, note, inhalers,nid, totalSOSDosage);
        this.duration = duration;
        this.schedules = schedules;
    }

    public UserNormalMedicine(MedicineType medicineType, String medicineName, boolean shareWithDoctor, GregorianCalendar startDate, String note, ArrayList<MedicineInhaler> inhalers, int totalSOSDosage, int duration, ArrayList<MedicineSchedule> schedules, String nid) {
        super(medicineType, medicineName, shareWithDoctor, startDate, note, inhalers,nid, totalSOSDosage);
        this.duration = duration;
        this.schedules = schedules;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ArrayList<MedicineSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(ArrayList<MedicineSchedule> schedules) {
        this.schedules = schedules;
    }
}
