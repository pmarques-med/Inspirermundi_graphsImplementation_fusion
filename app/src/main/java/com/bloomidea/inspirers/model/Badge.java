package com.bloomidea.inspirers.model;

import java.io.Serializable;

/**
 * Created by michellobato on 17/03/17.
 */

public class Badge implements Serializable{
    private String name;
    private String desc;
    private int imgResourceId;
    private String code;

    public Badge(String name, String desc, int imgResourceId, String code) {
        this.name = name;
        this.desc = desc;
        this.imgResourceId = imgResourceId;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getImgResourceId() {
        return imgResourceId;
    }

    public String getCode() {
        return code;
    }
}
