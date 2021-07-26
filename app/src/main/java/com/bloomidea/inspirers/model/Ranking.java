package com.bloomidea.inspirers.model;

/**
 * Created by michellobato on 04/04/17.
 */

public class Ranking {
    private int total;
    private double latitude;
    private double longitude;

    public Ranking(int total, double latitude, double longitude) {
        this.total = total;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getTotal() {
        return total;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
