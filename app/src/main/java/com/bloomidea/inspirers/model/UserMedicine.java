package com.bloomidea.inspirers.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by michellobato on 22/03/17.
 */

public class UserMedicine implements Serializable{
    private Long id;
    private MedicineType medicineType;
    private String medicineName;
    private String nid;
    private boolean shareWithDoctor;
    private GregorianCalendar startDate;
    private String note;
    private int totalSOSDosages;
    private ArrayList<MedicineInhaler> inhalers;

    public UserMedicine(Long id, MedicineType medicineType, String medicineName, boolean shareWithDoctor, GregorianCalendar startDate, String note, ArrayList<MedicineInhaler> inhalers, String nid, int totalSOSDosages) {
        this.id = id;
        this.medicineType = medicineType;
        this.medicineName = medicineName;
        this.shareWithDoctor = shareWithDoctor;
        this.startDate = startDate;
        this.note = note;
        this.inhalers = inhalers;
        this.nid = nid;
        this.totalSOSDosages = totalSOSDosages;
    }

    public UserMedicine(MedicineType medicineType, String medicineName, boolean shareWithDoctor, GregorianCalendar startDate, String note, ArrayList<MedicineInhaler> inhalers, String nid, int totalSOSDosages) {
        this.medicineType = medicineType;
        this.medicineName = medicineName;
        this.shareWithDoctor = shareWithDoctor;
        this.startDate = startDate;
        this.note = note;
        this.inhalers = inhalers;
        this.nid = nid;
        this.totalSOSDosages = totalSOSDosages;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MedicineType getMedicineType() {
        return medicineType;
    }

    public void setMedicineType(MedicineType medicineType) {
        this.medicineType = medicineType;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public boolean isShareWithDoctor() {
        return shareWithDoctor;
    }

    public void setShareWithDoctor(boolean shareWithDoctor) {
        this.shareWithDoctor = shareWithDoctor;
    }

    public GregorianCalendar getStartDate() {
        return startDate;
    }

    public void setStartDate(GregorianCalendar startDate) {
        this.startDate = startDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getTotalSOSDosages() {
        return totalSOSDosages;
    }

    public void setTotalSOSDosages(int totalSOSDosages) {
        this.totalSOSDosages = totalSOSDosages;
    }

    public ArrayList<MedicineInhaler> getInhalers() {
        return inhalers;
    }

    public void setInhalers(ArrayList<MedicineInhaler> inhalers) {
        this.inhalers = inhalers;
    }
}
