package com.bloomidea.inspirers.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by michellobato on 23/03/17.
 */

public class UserSOSMedicine extends UserMedicine implements Serializable{
    private int severity;
    private String trigger;
    private HealthServices healthService;
    private int sosDosage;

    //private String latitude;
    //private String longitude;

    public UserSOSMedicine(Long id, MedicineType medicineType, String medicineName, boolean shareWithDoctor, GregorianCalendar startDate, String note, ArrayList<MedicineInhaler> inhalers, int totalSOSDosage, int severity, String trigger, HealthServices healthService, int sosDosage, String nid) {
        super(id, medicineType, medicineName, shareWithDoctor, startDate, note, inhalers, nid, totalSOSDosage);
        this.severity = severity;
        this.trigger = trigger;
        this.healthService = healthService;
        this.sosDosage = sosDosage;
        //this.latitude = latitude;
        //this.longitude = longitude;
    }

    public UserSOSMedicine(MedicineType medicineType, String medicineName, boolean shareWithDoctor, GregorianCalendar startDate, String note, ArrayList<MedicineInhaler> inhalers, int totalSOSDosage, int severity, String trigger, HealthServices healthService, int sosDosage, String nid) {
        super(medicineType, medicineName, shareWithDoctor, startDate, note, inhalers,nid, totalSOSDosage);
        this.severity = severity;
        this.trigger = trigger;
        this.healthService = healthService;
        this.sosDosage = sosDosage;
        //this.latitude = latitude;
        //this.longitude = longitude;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public HealthServices getHealthService() {
        return healthService;
    }

    public void setHealthService(HealthServices healthService) {
        this.healthService = healthService;
    }

    public int getSosDosage() {
        return sosDosage;
    }

    public void setSosDosage(int sosDosage) {
        this.sosDosage = sosDosage;
    }
}
