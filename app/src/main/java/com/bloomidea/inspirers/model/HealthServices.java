package com.bloomidea.inspirers.model;

import java.io.Serializable;

/**
 * Created by michellobato on 23/03/17.
 */

public class HealthServices implements Serializable {
    private String code;
    private String name;

    public HealthServices(String code, String name) {
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
