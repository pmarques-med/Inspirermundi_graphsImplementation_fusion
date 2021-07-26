package com.bloomidea.inspirers.model;

import java.io.Serializable;

/**
 * Created by michellobato on 21/03/17.
 */

public class MedicineTime implements Serializable{
    private Long id;
    private FaseTime faseTime;
    private int dosage;

    public MedicineTime(Long id, FaseTime faseTime, int dosage) {
        this.id = id;
        this.faseTime = faseTime;
        this.dosage = dosage;
    }

    public MedicineTime(FaseTime faseTime, int dosage) {
        this.faseTime = faseTime;
        this.dosage = dosage;
    }

    public FaseTime getFaseTime() {
        return faseTime;
    }

    public int getDosage() {
        return dosage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
