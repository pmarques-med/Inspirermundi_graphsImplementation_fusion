package com.bloomidea.inspirers.model;

/**
 * Created by michellobato on 17/03/17.
 */

public class Country {
    private String nid;
    private String name;
    private String flag;
    private String latitude;
    private String longitude;
    private String iso;

    public Country(String nid, String name, String flag, String latitude, String longitude, String iso) {
        this.nid = nid;
        this.name = name;
        this.flag = flag;
        this.latitude = latitude;
        this.longitude = longitude;
        this.iso = iso;
    }

    public String getNid() {
        return nid;
    }

    public String getName() {
        return name;
    }

    public String getFlag() {
        return flag;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getIso() {
        return iso;
    }
}
