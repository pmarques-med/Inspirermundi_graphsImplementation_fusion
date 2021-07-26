package com.bloomidea.inspirers.model;

import java.io.Serializable;

/**
 * Created by michellobato on 21/03/17.
 */

public class MedicineType implements Serializable{
    private String code;
    private String name;

    public MedicineType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
