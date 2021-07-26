package com.bloomidea.inspirers.model;

import java.io.Serializable;

/**
 * Created by michellobato on 22/03/17.
 */

public class MedicineInhaler implements Serializable{
    private Long id;
    private boolean active;
    private String barcode;
    private int dosage;

    public MedicineInhaler(Long id, boolean active, String barcode, int dosage) {
        this.id = id;
        this.active = active;
        this.barcode = barcode;
        this.dosage = dosage;
    }

    public MedicineInhaler(boolean active, String barcode, int dosage) {
        this.active = active;
        this.barcode = barcode;
        this.dosage = dosage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getDosage() {
        return dosage;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }
}
